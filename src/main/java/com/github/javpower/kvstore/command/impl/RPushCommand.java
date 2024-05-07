package com.github.javpower.kvstore.command.impl;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javpower.kvstore.command.RedisCommand;
import com.github.javpower.kvstore.engine.StorageEngine;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

@Slf4j
public class RPushCommand implements RedisCommand<List<String>> {
    @Override
    public List<String> execute(StorageEngine storage, List<String> args) {
        // 检查至少有一个键和一个值
        LinkedHashMap<String,String> list=new LinkedHashMap<>();
        if (args.size() < 2) {
            log.info("RPUSH requires at least two arguments: key and value(s).");
            return null;
        }
        String key = args.get(0);
        List<String> values = args.subList(1, args.size());
        for (String value : values) {
            list.putIfAbsent(value, value); // LinkedHashMap会保留插入顺序
        }
        String v = storage.get(key);
        ObjectMapper objectMapper = new ObjectMapper();
        if (StrUtil.isNotEmpty(v)) {
            try {
                list.putAll(objectMapper.readValue(v, LinkedHashMap.class));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        // 序列化 LinkedHashMap 为 JSON String
        String jsonString = null;
        try {
            jsonString = objectMapper.writeValueAsString(list);
            storage.set(key, jsonString, -1); // 序列化列表
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return Arrays.asList(list.values().toArray(new String[]{}));
    }
}