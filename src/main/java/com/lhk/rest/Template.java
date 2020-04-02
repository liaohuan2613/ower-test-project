package com.lhk.rest;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class Template {
    public static void main(String[] args) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(60 * 1000);
        factory.setReadTimeout(60 * 1000);
        RestTemplate restTemplate = new RestTemplate(factory);
        Map<String, Object> removalMap = new HashMap<>(8);
        removalMap.put("id", "1");
        removalMap.put("title", "测试用语");
        removalMap.put("content", "测试用语");
        removalMap.put("source", "测试");
        removalMap.put("database", "1");
        String url = "http://localhost:5002/api/deduplication";
        URI targetUrl = UriComponentsBuilder.fromUriString(url).build().encode().toUri();
        restTemplate.postForEntity(targetUrl, removalMap, Object.class);
    }
}
