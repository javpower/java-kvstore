package com.github.javpower.kvstore.command.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.github.javpower.kvstore.command.RedisCommand;
import com.github.javpower.kvstore.engine.StorageEngine;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.List;

@Slf4j
public class RPushCommand implements RedisCommand<LinkedHashMap> {
    @Override
    public LinkedHashMap<String,String> execute(StorageEngine storage, List<String> args) {
        // 检查至少有一个键和一个值
        LinkedHashMap<String,String> list=new LinkedHashMap<>();
        if (args.size() < 2) {
            log.info("RPUSH requires at least two arguments: key and value(s).");
            return list;
        }
        String key = args.get(0);
        List<String> values = args.subList(1, args.size());
        for (String value : values) {
            list.put(value, value); // LinkedHashMap会保留插入顺序
        }
        String v = storage.get(key);
        if (StrUtil.isNotEmpty(v)) {
            list.putAll(JSONUtil.toBean(v, LinkedHashMap.class));
        }
        storage.set(key, JSONUtil.toJsonStr(list), -1); // 序列化列表
        return list;
    }
}