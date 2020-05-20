package com.workoss.boot.util.event;

public abstract class Event<T> {
    private final long timestamp = System.currentTimeMillis();

    public long getTimestamp() {
        return timestamp;
    }
}