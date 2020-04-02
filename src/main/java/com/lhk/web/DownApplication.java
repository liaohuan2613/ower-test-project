package com.lhk.web;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;

public class DownApplication {
    public static void main(String[] args) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(60 * 1000);
        factory.setReadTimeout(60 * 1000);
        RestTemplate restTemplate = new RestTemplate(factory);
        int num = 82000;
        // https://cdn3.lajiao-bo.com/20191205/7P77akBY/550kb/hls/ZHY9mGJw2582410.ts
        for (int i = 750; i < 751; i++) {
            int name = num + i;
            restTemplate.execute("https://cdn3.lajiao-bo.com/20191205/7P77akBY/550kb/hls/ZHY9mGJw25" + name + ".ts", HttpMethod.GET, null, clientHttpResponse -> {
                File nameFile = new File("D:\\tmp\\mv\\yao001-" + name + ".ts");
                StreamUtils.copy(clientHttpResponse.getBody(), new FileOutputStream(nameFile));
                return nameFile;
            });
        }
    }
}
