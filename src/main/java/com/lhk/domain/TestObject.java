package com.lhk.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class TestObject implements Serializable {
    private Map<String, Object> testMap = new HashMap<>();

    public Map<String, Object> getTestMap() {
        return testMap;
    }

    public TestObject setTestMap(Map<String, Object> testMap) {
        this.testMap = testMap;
        return this;
    }

    @Override
    public String toString() {
        return "TestObject{" +
                "testMap=" + testMap +
                '}';
    }
}
