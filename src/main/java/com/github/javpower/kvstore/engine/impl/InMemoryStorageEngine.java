package com.github.javpower.kvstore.engine.impl;

import com.github.javpower.kvstore.engine.StorageEngine;
import com.github.javpower.kvstore.model.Kv;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.InvalidProtocolBufferException;
import org.slf4j.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static org.slf4j.LoggerFactory.getLogger;

public class InMemoryStorageEngine implements StorageEngine, AutoCloseable {
    // 日志对象
    private static final Logger logger = getLogger(InMemoryStorageEngine.class);
    // 持久化的时间间隔，5分钟
    private static final long PERSIST_INTERVAL_MS = TimeUnit.MINUTES.toMillis(5);
    private static final long EXPIRE_INTERVAL_MS = TimeUnit.SECONDS.toMillis(20);

    // 每次清理过期键的最大数量
    private static final int MAX_KEYS_TO_CLEANUP_PER_RUN = 1000;

    // 存储键值对的 ConcurrentHashMap
    private final Map<String, String> database = new ConcurrentHashMap<>();
    // 存储键的过期时间的 ConcurrentHashMap
    private final Map<String, Long> expires = new ConcurrentHashMap<>();
    // 持久化文件的名称
    private final String filename;
    // 持久化操作的 ScheduledExecutorService
    private final ScheduledExecutorService persistExecutorService = Executors.newSingleThreadScheduledExecutor();
    // 过期键清理操作的 ScheduledExecutorService
    private final ScheduledExecutorService cleanupExecutorService = Executors.newSingleThreadScheduledExecutor();
    // 持久化操作的标志，确保不会并发执行持久化
    private final AtomicBoolean isPersisting = new AtomicBoolean(false);

    // 构造函数
    public InMemoryStorageEngine(String filename) {
        this.filename = filename;
        try {
            load(); // 初始加载持久化的数据
            logger.info("Data loaded from persistence.");
        } catch (IOException e) {
            logger.error("Failed to load data from persistence.", e);
            throw new RuntimeException("Failed to load data.", e);
        }
        schedulePeriodicPersist(); // 调度定期持久化任务
        scheduleExpireCleanup(); // 调度过期键清理任务
    }

    private void schedulePeriodicPersist() {
        persistExecutorService.scheduleAtFixedRate(this::persist, PERSIST_INTERVAL_MS, PERSIST_INTERVAL_MS, TimeUnit.MILLISECONDS);
        logger.info("Scheduled periodic persist every {} ms.", PERSIST_INTERVAL_MS);
    }

    private void scheduleExpireCleanup() {
        cleanupExecutorService.scheduleAtFixedRate(this::cleanupExpiredKeys, EXPIRE_INTERVAL_MS, EXPIRE_INTERVAL_MS, TimeUnit.MILLISECONDS);
        logger.info("Scheduled expire cleanup every {} ms.",EXPIRE_INTERVAL_MS);
    }

    public void persist() {
        if (isPersisting.compareAndSet(false, true)) {
            logger.info("Starting data persist.");
            try {
                Kv.Storage storage = Kv.Storage.newBuilder()
                        .addAllKvPairs(database.entrySet().stream()
                                .map(entry -> Kv.KvPair.newBuilder()
                                        .setKey(entry.getKey())
                                        .setValue(entry.getValue())
                                        .setExpireTimestamp(expires.getOrDefault(entry.getKey(), -1L))
                                        .build())
                                .collect(Collectors.toList()))
                        .build();

                try (FileOutputStream fos = new FileOutputStream(filename);
                     GZIPOutputStream gz = new GZIPOutputStream(fos)) {
                    gz.write(storage.toByteArray());
                }
                logger.info("Data successfully persisted.");
            } catch (IOException e) {
                logger.error("Failed to persist data.", e);
            } finally {
                isPersisting.set(false);
            }
        }
    }

    private void cleanupExpiredKeys() {
        logger.info("Starting cleanup of expired keys.");
        int cleanupCount = 0; // 局部变量，用于跟踪单次清理操作中的清理数量
        Iterator<Map.Entry<String, Long>> it = expires.entrySet().iterator();
        long now = System.currentTimeMillis();
        while (it.hasNext()) {
            if (cleanupCount >= MAX_KEYS_TO_CLEANUP_PER_RUN) {
                logger.info("Reached maximum keys to cleanup per run. Cleanup will continue in the next run.");
                break;
            }
            Map.Entry<String, Long> entry = it.next();
            if (entry.getValue() != -1 && now > entry.getValue()) {
                database.remove(entry.getKey());
                it.remove();
                cleanupCount++; // 清理一个过期键后递增计数器
                logger.debug("Expired key removed: {}", entry.getKey());
            }
        }
    }

    @Override
    public void set(String key, String value, long ttl) {
        database.put(key, value);
        expires.put(key, ttl == -1 ? -1L : System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(ttl));
    }

    @Override
    public String get(String key) {
        Long expireTime = expires.get(key);
        if (expireTime != null && (expireTime == -1 || System.currentTimeMillis() < expireTime)) {
            return database.get(key);
        }
        database.remove(key);
        expires.remove(key);
        return null;
    }
    @Override
    public Long ttl(String key) {
        return  expires.get(key);
    }


    @Override
    public void remove(String key) {
        database.remove(key);
        expires.remove(key);
    }

    @Override
    public void load() throws IOException {
        File file = new File(filename);
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file);
                 GZIPInputStream gz = new GZIPInputStream(fis);
                 ByteArrayOutputStream bas = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = gz.read(buffer)) != -1) {
                    bas.write(buffer, 0, length);
                }
                try {
                    byte[] data = bas.toByteArray();
                    CodedInputStream cis = CodedInputStream.newInstance(new ByteArrayInputStream(data));
                    cis.setSizeLimit(Integer.MAX_VALUE); // 设置合理的大小限制以避免过大的数据
                    Kv.Storage storage;
                    try {
                        storage = Kv.Storage.parseFrom(cis);
                    } catch (InvalidProtocolBufferException e) {
                        logger.error("Failed to parse the Protocol Buffers message from the file.", e);
                        throw new IOException("Corrupted data file.", e);
                    }
                    for (Kv.KvPair kvPair : storage.getKvPairsList()) {
                        database.put(kvPair.getKey(), kvPair.getValue());
                        expires.put(kvPair.getKey(), kvPair.getExpireTimestamp());
                    }
                } finally {
                    // 确保 CodedInputStream 在这里关闭，尽管它不是 try-with-resources 的直接部分,它将由 ByteArrayInputStream 的关闭操作隐式关闭
                    bas.close();
                }
            }
        } else {
            logger.info("No existing persistence file found. Starting with an empty store.");
        }
    }

    @Override
    public Map<String, String> getAll() {
        return new HashMap<>(database);
    }

    @Override
    public void shutdown() {
        persistExecutorService.shutdown();
        cleanupExecutorService.shutdown();
    }

    // 用于关闭资源的 close 方法
    @Override
    public void close() {
        shutdown();
    }
}