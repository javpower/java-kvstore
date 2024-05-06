package com.github.javpower.kvstore.command.impl;

import com.github.javpower.kvstore.command.RedisCommand;
import com.github.javpower.kvstore.engine.StorageEngine;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class DelCommand implements RedisCommand<Long> {
    @Override
    public Long execute(StorageEngine storage, List<String> args) {
        if (args.isEmpty()) {
            log.info("DEL requires at least one argument: key.");
            return null;
        }
        long deletedCount = 0;
        for (String key : args) {
            storage.remove(key);
            deletedCount++;
        }
        log.info(String.format("Deleted %d key(s).", deletedCount));
        return deletedCount;
    }
}