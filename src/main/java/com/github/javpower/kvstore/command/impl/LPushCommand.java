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
public class LPushCommand implements RedisCommand<List<String>> {
    @Override
    public List<String> execute(StorageEngine storage, List<String> args) {
        // 检查至少有一个键和一个值
        if (args.size() < 2) {
            log.info("LPUSH requires at least two arguments: key and value(s).");
            return null;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        String key = args.get(0);
        List<String> values = args.subList(1, args.size());
        LinkedHashMap<String, String> list = new LinkedHashMap<>();
        String v = storage.get(key);
        if (StrUtil.isNotEmpty(v)) {
            try {
                // 反序列化 JSON String 为 LinkedHashMap
                list = objectMapper.readValue(v, LinkedHashMap.class);
            } catch (JsonProcessingException e) {
                log.error("解析 JSON 时出错，字符串: {}", v, e);
                // 处理异常，例如返回错误信息或者默认值
                return null; // 或者设置一个默认的空列表
            }
        }
        for (String value : values) {
            list.putIfAbsent(value, value); // LinkedHashMap会保留插入顺序
        }
        // 序列化 LinkedHashMap 为 JSON String
        String jsonString = null;
        try {
            jsonString = objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        storage.set(key, jsonString, -1); // 序列化列表
        return Arrays.asList(list.values().toArray(new String[]{}));
    }
}