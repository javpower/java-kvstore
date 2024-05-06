package com.github.javpower.kvstore.command.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.github.javpower.kvstore.command.RedisCommand;
import com.github.javpower.kvstore.engine.StorageEngine;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class LPopCommand implements RedisCommand<String> {
    @Override
    public String execute(StorageEngine storage, List<String> args) {
        if (args.size() != 1) {
            log.info("LPOP requires exactly one argument: key.");
            return null;
        }
        String key = args.get(0);
        String v = storage.get(key);
        if (StrUtil.isNotEmpty(v)) {
            LinkedHashMap<String, String> list = JSONUtil.toBean(v, LinkedHashMap.class);
            if (list == null || list.isEmpty()) {
                return null;
            }
            // 由于LinkedHashMap保留了插入顺序，entrySet().iterator().next()将返回第一个键值对
            Map.Entry<String, String> firstEntry = list.entrySet().iterator().next();
            String value = firstEntry.getKey();
            list.remove(value);
            storage.set(key, JSONUtil.toJsonStr(list), -1);
            return value;
        }
        return null;
    }
}