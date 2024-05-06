package com.github.javpower.kvstore.command.impl;

import com.github.javpower.kvstore.command.RedisCommand;
import com.github.javpower.kvstore.engine.StorageEngine;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.github.javpower.kvstore.command.impl.IncrCommand.cr;

@Slf4j
public class DecrCommand implements RedisCommand<Long> {
    @Override
    public Long execute(StorageEngine storage, List<String> args) {
        if (args.size() != 1) {
            log.info("DECR requires exactly one argument: key.");
            return null;
        }
        String key = args.get(0);
        return Long.parseLong(cr(storage, key, -1));
    }
}