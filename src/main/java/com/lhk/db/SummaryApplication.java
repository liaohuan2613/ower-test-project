package com.lhk.db;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class SummaryApplication {

    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();
        JdbcTemplate jdbcTemplate = JdbcTemplateApplication.getJdbcTemplate();
        URI uri = UriComponentsBuilder.fromUriString("http://127.0.0.1:7102/rest/text-analyzer/summarize").build().encode().toUri();
        jdbcTemplate.queryForList("select CONT from poc_error_summary").forEach(contMap -> {
            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("marketCode", "*");
            requestMap.put("text", contMap.get("CONT"));
            requestMap.put("sentenceNumber", 200);
            Map<String, Object> body = restTemplate.postForEntity(uri, requestMap, Map.class).getBody();
            if (body.get("result").toString().length() <= 0) {
                System.out.println(requestMap);
            }
        });
    }
}
