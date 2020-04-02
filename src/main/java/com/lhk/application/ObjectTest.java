package com.lhk.application;

import java.util.HashMap;
import java.util.Map;

public class ObjectTest {
    public static void main(String[] args) {
        Map<String, Long> tempMap = new HashMap<>(1024 * 1024 * 100);
        for (int i = 0; i < 1024 * 1024; i++) {
            tempMap.putIfAbsent(i + "", 1024 * 1024L + i);
        }
        Runtime rt = Runtime.getRuntime();
        System.out.println("Total Memory = "
                + rt.totalMemory()
                + " Free Memory = "
                + rt.freeMemory());
//        System.out.println(tempMap);
    }
}
