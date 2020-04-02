package com.lhk.web;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class DownApplication3 {
    private static AtomicInteger counter = new AtomicInteger(0);
    public static void main(String[] args) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 10, 0, TimeUnit.MINUTES,
                new LinkedBlockingQueue<>());
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(60 * 1000);
        factory.setReadTimeout(60 * 1000);
        RestTemplate restTemplate = new RestTemplate(factory);
        String url = "https://cdn3.lajiao-bo.com/20191205/7P77akBY/550kb/hls/ZHY9mGJw2582750.ts";
        String folder = "D:\\tmp\\mv\\" + System.currentTimeMillis();
        File file = new File(folder);
        if (file.mkdir()) {
            String endUrl = url.substring(url.lastIndexOf("/"), url.lastIndexOf("."));

            StringBuilder topSb = new StringBuilder();
            StringBuilder numSb = new StringBuilder();

            boolean flag = false;
            for (int i = endUrl.length() -1; i >= 0; i--) {
                if (endUrl.charAt(i) >= 'A' && endUrl.charAt(i) <= 'z' || flag) {
                    topSb.insert(0, endUrl.charAt(i));
                    flag = true;
                } else {
                    numSb.insert(0, endUrl.charAt(i));
                }
            }
            String preUrl = url.substring(0, url.lastIndexOf("/")) + topSb.toString();
            int endNum = Integer.parseInt(numSb.toString()) + 1;
            int startNum = endNum / 1000 * 1000;
            int len = endNum - startNum;
            int missionLen = len / 10;
            int lastMissionStartNum = startNum + missionLen * 10;
            for (int i = 0; i < 10; i++) {
                int name = i;
                executor.execute(() -> {
                    for (int j = 0; j < missionLen; j++) {
                        int tempName = startNum + name * missionLen + j;
                        restTemplate.execute(preUrl + tempName + ".ts", HttpMethod.GET, null, clientHttpResponse -> {
                            File nameFile = new File(folder + "\\yao-" + name + ".ts");
                            StreamUtils.copy(clientHttpResponse.getBody(), new FileOutputStream(nameFile, true));
                            return nameFile;
                        });
                    }
//                    int countNum = counter.addAndGet(1);
//                    if (countNum == 10 &&  executor.getActiveCount() == 0) {
//                        executor.shutdown();
//                    }
                });
            }

            for (int i = lastMissionStartNum; i < endNum; i++) {
                int finalIndex = i;
                restTemplate.execute(preUrl + i + ".ts", HttpMethod.GET, null, clientHttpResponse -> {
                    File nameFile = new File(folder + "\\yao-" + finalIndex + ".ts");
                    StreamUtils.copy(clientHttpResponse.getBody(), new FileOutputStream(nameFile));
                    return nameFile;
                });
            }
        }
    }
}
