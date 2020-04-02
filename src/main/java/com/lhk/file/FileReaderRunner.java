package com.lhk.file;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FileReaderRunner {
    public static void main(String[] args) throws Exception {
//        BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\Administrator\\Desktop\\词汇.txt"));
//        String line;
//        StringBuilder sb = new StringBuilder();
//        while ((line = br.readLine()) != null) {
//            sb.append(",'" + line + "'");
//        }
//        System.out.println(sb.toString());
        Map<String, String> testMap = new HashMap<>();
        testMap.put("1", "111");
        testMap.put("2", "222");
        testMap.put("3", "333");
        testMap.put("4", "444");
        Set<String> set = new HashSet<>();
        set.add("1");
        set.add("2");
        set.add("3");
        set.add("4");
        set.forEach(k -> {
            String a = testMap.get(k);
            testMap.remove(k);
            System.out.println(a);
        });
    }
}
