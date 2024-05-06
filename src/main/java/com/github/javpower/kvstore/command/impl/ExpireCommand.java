package com.github.javpower.kvstore.command.impl;

import com.github.javpower.kvstore.command.RedisCommand;
import com.github.javpower.kvstore.engine.StorageEngine;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ExpireCommand implements RedisCommand<Boolean> {
    @Override
    public Boolean execute(StorageEngine storage, List<String> args) {
        if (args.size() != 2) {
            log.info("EXPIRE requires exactly two arguments: key and TTL (time to live).");
            return null;
        }
        String key = args.get(0);
        String ttlString = args.get(1);
        long ttl;
        try {
            ttl = Long.parseLong(ttlString);
            if (ttl < 0) {
                log.info("TTL must be a non-negative integer.");
                return false;
            }
        } catch (NumberFormatException e) {
            log.info("TTL must be an integer.");
            return false;
        }
        storage.set(key, storage.get(key), ttl);
        return true;
    }
}