package com.lhk.mysql;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class ProperNounApplication {
    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();
        JdbcTemplate jdbcTemplate = JdbcTemplateApplication.getJdbcTemplate();
        URI uri = UriComponentsBuilder.fromUriString("http://192.168.11.57:10003/").build().encode().toUri();
        List<Map<String, Object>> mapList = jdbcTemplate.queryForList("select ID, CONT from POC_NEWS_v8 where nerProperNounSet = '[]'  limit 3000");
        for (int i = 0; i < mapList.size(); i++) {
            Map<String, Object> map = mapList.get(i);
            String content = map.get("CONT").toString();
            Map<String, Object> requestMap = new HashMap<>(1);
            requestMap.put("text", content);
            Map<String, Object> body = new HashMap<>();
            try {
                body = restTemplate.postForEntity(uri, requestMap, Map.class).getBody();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Set<String> properNounSet = new HashSet<>();
            if (body.get("result") instanceof List) {
                List<Map<String, Object>> resultList = (List<Map<String, Object>>) body.get("result");
                for (Map<String, Object> res : resultList) {
                    properNounSet.add(res.get("name").toString());
                }
            }
            map.put("properNounSet", properNounSet.toString());
        }
        updateNews(jdbcTemplate, mapList);
    }

    private static void updateNews(JdbcTemplate jdbcTemplate, List<Map<String, Object>> mapList) {
        jdbcTemplate.batchUpdate("update POC_NEWS_v8 set nerProperNounSet = ? where ID = ?", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, mapList.get(i).get("properNounSet").toString());
                ps.setString(2, mapList.get(i).get("ID").toString());
            }

            @Override
            public int getBatchSize() {
                return mapList.size();
            }
        });
    }
}
