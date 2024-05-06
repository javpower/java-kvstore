package com.github.javpower.kvstore.factory;

import com.github.javpower.kvstore.command.RedisCommand;
import com.github.javpower.kvstore.command.impl.*;
import com.github.javpower.kvstore.model.RedisCommandType;

import java.util.HashMap;
import java.util.Map;

public class CommandFactory {
    private static final Map<RedisCommandType, RedisCommand> commands = new HashMap<>();

    static {
        commands.put(RedisCommandType.SET, new SetCommand());
        commands.put(RedisCommandType.GET, new GetCommand());
        commands.put(RedisCommandType.EXISTS, new ExistsCommand());
        commands.put(RedisCommandType.EXPIRE, new ExpireCommand());
        commands.put(RedisCommandType.TTL, new TtlCommand());
        commands.put(RedisCommandType.DEL, new DelCommand());
        commands.put(RedisCommandType.LPUSH, new LPushCommand());
        commands.put(RedisCommandType.RPUSH, new RPushCommand());
        commands.put(RedisCommandType.LPOP, new LPopCommand());
        commands.put(RedisCommandType.RPOP, new RPopCommand());
        commands.put(RedisCommandType.INCR, new IncrCommand());
        commands.put(RedisCommandType.DECR, new DecrCommand());
        // 可以继续添加其他命令
    }

    public static RedisCommand getCommand(RedisCommandType commandType) {
        return commands.get(commandType);
    }
}