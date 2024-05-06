package com.github.javpower.kvstore.model;

public enum RedisCommandType {
    SET,
    GET,
    EXISTS,
    EXPIRE,
    TTL,
    DEL,
    LPUSH,
    RPUSH,
    LPOP,
    RPOP,
    INCR,
    DECR
    // 可以继续添加其他命令
}