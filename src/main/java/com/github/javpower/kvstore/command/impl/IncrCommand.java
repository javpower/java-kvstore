package com.github.javpower.kvstore.command.impl;

import com.github.javpower.kvstore.command.RedisCommand;
import com.github.javpower.kvstore.engine.StorageEngine;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class IncrCommand implements RedisCommand<Long> {
    @Override
    public Long execute(StorageEngine storage, List<String> args) {
        if (args.size() != 1) {
            log.info("INCR requires exactly one argument: key.");
            return null;
        }
        String key = args.get(0);
        return Long.parseLong(cr(storage, key, 1));
    }

    public static String cr(StorageEngine storage, String key, long diff) {
        // 使用AtomicReference来持有当前的值和更新后的值
        AtomicReference<String> ref = new AtomicReference<>(storage.get(key));
        String newValue;
        do {
            // 解析当前的字符串值
            long currentValue = Long.parseLong(ref.get());
            // 计算新的值
            long incrementedValue = currentValue + diff;
            // 将新值转换为字符串
            newValue = Long.toString(incrementedValue);
            // 尝试用新值更新AtomicReference
        } while (!ref.compareAndSet(ref.get(), newValue));

        // 更新存储引擎，写入新值
        storage.set(key, newValue,-1);
        return newValue;
    }
}