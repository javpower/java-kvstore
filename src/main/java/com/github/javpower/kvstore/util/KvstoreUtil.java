package com.github.javpower.kvstore.util;

import com.github.javpower.kvstore.model.RedisCommandType;
import com.github.javpower.kvstore.service.RedisServer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class KvstoreUtil {
    private final RedisServer redisServer;

    public KvstoreUtil(RedisServer redisServer) {
        this.redisServer = redisServer;
    }

    // DECR命令
    public Long decr(String key) {
        return executeCommand(RedisCommandType.DECR, key);
    }

    // DEL命令
    public Long del(String... keys) {
        List<String> args = new ArrayList<>();
        args.add(RedisCommandType.DEL.name());
        args.addAll(Arrays.asList(keys));
        return executeCommand(args);
    }

    // EXISTS命令
    public Boolean exists(String key) {
        return executeCommand(RedisCommandType.EXISTS, key);
    }

    // EXPIRE命令
    public Boolean expire(String key, Long ttl) {
        List<String> args = new ArrayList<>();
        args.add(RedisCommandType.EXPIRE.name());
        args.add(key);
        args.add(ttl.toString());
        return executeCommand(args);
    }

    // GET命令
    public String get(String key) {
        return executeCommand(RedisCommandType.GET, key);
    }

    // INCR命令
    public Long incr(String key) {
        return executeCommand(RedisCommandType.INCR, key);
    }

    // LPOP命令
    public String lpop(String key) {
        return executeCommand(RedisCommandType.LPOP, key);
    }

    // LPUSH命令
    public List<String> lpush(String key, String... values) {
        List<String> args = new ArrayList<>();
        args.add(RedisCommandType.LPUSH.name());
        args.add(key);
        args.addAll(Arrays.asList(values));
        return executeCommand(args);
    }

    // RPOP命令
    public String rpop(String key) {
        return executeCommand(RedisCommandType.RPOP, key);
    }

    // RPUSH命令
    public  List<String> rpush(String key, String... values) {
        List<String> args = new ArrayList<>();
        args.add(RedisCommandType.RPUSH.name());
        args.add(key);
        args.addAll(Arrays.asList(values));
        return executeCommand(args);
    }

    // SET命令
    public String set(String key, String value, Long ttl) {
        List<String> args = new ArrayList<>();
        args.add(RedisCommandType.SET.name());
        args.add(key);
        args.add(value);
        if (ttl != null) {
            args.add(ttl.toString());
        }
        return executeCommand(args);
    }

    // TTL命令
    public Long ttl(String key) {
        // 执行原始的TTL命令，获取过期时间的时间戳（假设是毫秒）
        Long expireTimestamp = executeCommand(RedisCommandType.TTL, key);
        // 检查返回的时间戳是否为null或者是否为-1（表示键不存在或者没有设置过期时间）
        if(expireTimestamp == null){
            return null;
        }
        if (expireTimestamp == -1) {
            return -1L; // 或者返回-1
        }
        // 获取当前时间的时间戳（毫秒）
        Long currentTimestamp = System.currentTimeMillis();
        // 计算剩余时间（毫秒）
        Long remainingTimeMillis = expireTimestamp - currentTimestamp;
        // 检查是否已经过期
        if (remainingTimeMillis <= 0) {
            return 0L; // 键已过期或即将过期
        }
        // 将剩余时间转换为秒
        return remainingTimeMillis / 1000;
    }


    // 通用执行命令的方法
    private <T> T executeCommand(RedisCommandType type, String... args) {
        List<String> command = new ArrayList<>();
        command.add(type.name());
        command.addAll(Arrays.asList(args));
        String commandLine = String.join("<--!-->", command);
        return (T) redisServer.processCommand(commandLine);
    }

    private <T> T executeCommand(List<String> command) {
        String commandLine = String.join("<--!-->", command);
        return (T) redisServer.processCommand(commandLine);
    }
}