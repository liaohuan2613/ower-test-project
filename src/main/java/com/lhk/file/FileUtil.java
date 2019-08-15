package com.lhk.file;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map;
import java.util.UUID;

public class FileUtil {
    public static void main(String[] args) throws Exception {
        Gson gson = new Gson();
        BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\Administrator\\Desktop\\港股.txt"));
        String line = null;
        while ((line = br.readLine()) != null) {
            gson.fromJson(line, Map.class).forEach((k, v) -> {
                if (v instanceof Map) {
                    String code = "HK_" + ((Map) v).get("f12");
                    String name = ((Map) v).get("f14").toString();
                    System.out.println(UUID.randomUUID().toString() + "\t*\t" + code + "\t" + name + "\t" + "HK");
                }
            });
        }
    }
}
