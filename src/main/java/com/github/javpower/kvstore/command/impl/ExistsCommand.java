package com.github.javpower.kvstore.command.impl;

import com.github.javpower.kvstore.command.RedisCommand;
import com.github.javpower.kvstore.engine.StorageEngine;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ExistsCommand implements RedisCommand<Boolean> {
    @Override
    public Boolean execute(StorageEngine storage, List<String> args) {
        if (args.size() != 1) {
            log.info("EXISTS requires exactly one argument: key.");
            return null;
        }
        String key = args.get(0);
        boolean exists = storage.get(key) != null;
        return exists;
    }
}