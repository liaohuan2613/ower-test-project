package com.lhk.map;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class MapTest {
    public static void main(String[] args) {
        Map<String, Object> testMap = new HashMap<>();
        Set<String> activeSet = new HashSet<>();
        Set<String> custidSet = new HashSet<>();
        for (int i = 0; i < 2500000; i++) {
            Random random = new Random();
            String code = random.nextInt() + "";
            if (i % 50 == 0) {
                activeSet.add(code);
            }
            custidSet.add(code);
        }

        long nanoTime = System.nanoTime();
        for (String custid : custidSet) {
            String userCode = custid + "";
            if (StringUtils.isBlank(userCode)) {
                continue;
            }
            if (!testMap.containsKey(userCode) && activeSet.contains(userCode) && !userCode.equals("mockuser")) {
                testMap.put(userCode, "");
            }
        }
        System.out.println(System.nanoTime() - nanoTime);
    }
}
