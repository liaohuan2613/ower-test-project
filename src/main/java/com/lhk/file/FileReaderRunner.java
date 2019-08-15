package com.lhk.file;

import java.io.BufferedReader;
import java.io.FileReader;

public class FileReaderRunner {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\Administrator\\Desktop\\词汇.txt"));
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null) {
            sb.append(",'" + line + "'");
        }
        System.out.println(sb.toString());
    }
}
