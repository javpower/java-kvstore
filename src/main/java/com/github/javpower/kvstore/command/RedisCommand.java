package com.github.javpower.kvstore.command;

import com.github.javpower.kvstore.engine.StorageEngine;

import java.util.List;

public interface RedisCommand<T> {
    T execute(StorageEngine storage, List<String> args);
}