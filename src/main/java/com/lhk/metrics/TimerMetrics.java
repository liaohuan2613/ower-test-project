package com.lhk.metrics;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TimerMetrics {
    private Map<String, Long> concurrentMap = new ConcurrentHashMap<>();

    public long getTimer(String key) {
        return concurrentMap.get(key);
    }

    public void putTimer(String key, long value) {
        concurrentMap.put(key, value);
    }

    @Override
    public String toString() {
        return "TimerMetrics{" +
                "concurrentMap=" + concurrentMap +
                '}';
    }
}
