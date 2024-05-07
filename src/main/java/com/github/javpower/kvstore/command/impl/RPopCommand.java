package com.github.javpower.kvstore.command.impl;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javpower.kvstore.command.RedisCommand;
import com.github.javpower.kvstore.engine.StorageEngine;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.List;

@Slf4j
public class RPopCommand implements RedisCommand<String> {
    @Override
    public String execute(StorageEngine storage, List<String> args) {
        if (args.size() != 1) {
            log.info("RPOP requires exactly one argument: key.");
            return null;
        }
        String key = args.get(0);
        String v = storage.get(key);
        ObjectMapper objectMapper = new ObjectMapper();
        if (StrUtil.isNotEmpty(v)) {
            LinkedHashMap<String, String> list = null;
            try {
                list = objectMapper.readValue(v, LinkedHashMap.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            if (list != null && !list.isEmpty()) {
                // 获取最后一个键
                String lastKey = list.keySet().stream()
                        .reduce((first, second) -> second)
                        .get();
                // 获取最后一个值并从映射中移除这个键值对
                String value = list.remove(lastKey);
                // 更新序列化后的列表回 StorageEngine
                try {
                    String  jsonString = objectMapper.writeValueAsString(list);
                    storage.set(key, jsonString, -1);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                // 返回被移除的值
                return value;
            } else {
                // 如果列表为空，返回null
                return null;
            }
        }
        return null;
    }
}