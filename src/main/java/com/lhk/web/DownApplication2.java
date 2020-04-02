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

public class DownApplication2 {
    public static void main(String[] args) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 10, 0, TimeUnit.MINUTES,
                new LinkedBlockingQueue<>());
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(60 * 1000);
        factory.setReadTimeout(60 * 1000);
        RestTemplate restTemplate = new RestTemplate(factory);
        int num = 6646000;
//        for (int i = 0; i < 10; i++) {
//            int name = i;
//            executor.execute(() -> {
//                for (int j = 0; j < 72; j++) {
//                    int tempName = num + name * 72 + j;
//                    restTemplate.execute("https://cdn3.lajiao-bo.com/20190825/gy1S4BAB/650kb/hls/tB0JvBT" + tempName + ".ts", HttpMethod.GET, null, clientHttpResponse -> {
//                        File nameFile = new File("D:\\tmp\\mv\\yao002-" + tempName + ".ts");
//                        StreamUtils.copy(clientHttpResponse.getBody(), new FileOutputStream(nameFile));
//                        return nameFile;
//                    });
//                }
//            });
//
//        }

        for (int i = 6646720; i < 6646729; i++) {
            int finalIndex = i;
            restTemplate.execute("https://cdn3.lajiao-bo.com/20190825/gy1S4BAB/650kb/hls/tB0JvBT" + i + ".ts", HttpMethod.GET, null, clientHttpResponse -> {
                File nameFile = new File("D:\\tmp\\mv\\yao002-" + finalIndex + ".ts");
                StreamUtils.copy(clientHttpResponse.getBody(), new FileOutputStream(nameFile));
                return nameFile;
            });
        }
    }
}
