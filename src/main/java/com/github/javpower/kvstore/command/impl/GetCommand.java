package com.github.javpower.kvstore.command.impl;

import com.github.javpower.kvstore.command.RedisCommand;
import com.github.javpower.kvstore.engine.StorageEngine;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class GetCommand implements RedisCommand<String> {
    @Override
    public String execute(StorageEngine storage, List<String> args) {
        if (args.size() < 1) {
            log.info("GET requires exactly one argument: key.");
            return null;
        }
        String key = args.get(0);
        log.info("execute GET key.{}",key);
        String value = storage.get(key);
        if (value == null) {
            log.info("null");
        } else {
            log.info(value);
        }
        return value;
    }
}