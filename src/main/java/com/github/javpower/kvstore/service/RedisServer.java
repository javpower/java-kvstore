package com.github.javpower.kvstore.service;

import com.github.javpower.kvstore.command.RedisCommand;
import com.github.javpower.kvstore.engine.StorageEngine;
import com.github.javpower.kvstore.engine.impl.InMemoryStorageEngine;
import com.github.javpower.kvstore.factory.CommandFactory;
import com.github.javpower.kvstore.model.RedisCommandType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.Arrays;
import java.util.List;


@Service
@Slf4j
public class RedisServer implements AutoCloseable{
    private final StorageEngine storage = new InMemoryStorageEngine("kvstore.gz");


    public Object processCommand(String commandLine) {
        String[] tokens = commandLine.split("<--!-->");
        String commandName = tokens[0];
        RedisCommand command = CommandFactory.getCommand(RedisCommandType.valueOf(commandName));
        if (command != null) {
            List<String> args = Arrays.asList(Arrays.copyOfRange(tokens, 1, tokens.length));
            Object execute = command.execute(storage, args);
            return execute;
        } else {
            log.info("Unknown command: " + commandName);
            return null;
        }
    }

    @Override
    @PreDestroy
    public void close() throws Exception {
        log.info("Closing RedisServer, persisting data...");
        // 持久化数据
        storage.persist();
        // 关闭资源
        storage.shutdown();
    }

}