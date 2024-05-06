package com.github.javpower.kvstore.model;

import lombok.Data;

@Data
public class SetRequest {
    private String key;
    private String value;
    private Long ttl; // 可以为 null，表示键不过期
}