package com.github.javpower.kvstore.command.impl;

import com.github.javpower.kvstore.command.RedisCommand;
import com.github.javpower.kvstore.engine.StorageEngine;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class SetCommand implements RedisCommand<String> {
    @Override
    public String execute(StorageEngine storage, List<String> args) {
        if (args.size() < 2) {
            log.info("SET requires at least two arguments: key and value.");
            return null;
        }
        String key = args.get(0);
        String value = args.get(1);
        long ttl = -1; // 默认不过期
        // 检查是否提供了过期时间
        if (args.size() > 2 && args.get(2).matches("^\\d+$")) {
            // 假设过期时间以秒为单位，且为正整数
            ttl = Long.parseLong(args.get(2));
        }
        storage.set(key, value, ttl);
        log.info("SET requires key and value.{} {}",key,value);
        return value;
    }
}