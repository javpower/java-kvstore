package com.github.javpower.kvstore.engine;

import java.io.IOException;
import java.util.Map;

public interface StorageEngine {
    void set(String key, String value, long ttl); // 设置键值对和过期时间
    String get(String key); // 获取键对应的值
    Long ttl(String key);
    void remove(String key); // 删除键
    void persist() throws IOException; // 持久化到磁盘
    void load() throws IOException; // 从磁盘加载
    Map<String, String> getAll(); // 获取所有键值对，用于测试
    void shutdown();//关闭定时任务，可以在应用关闭时调用

}