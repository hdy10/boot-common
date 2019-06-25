package com.github.hdy.common.redis;

public class SourceType {
    public static final String preview_key = "hdy_preview_";

    public String name;

    public String name() {
        return this.name;
    }

    public SourceType() {
    }

    public SourceType(String name) {
        this.name = name;
    }

    /**
     * platform storage
     */
    public final static SourceType Redis = new SourceType("redis");
}
