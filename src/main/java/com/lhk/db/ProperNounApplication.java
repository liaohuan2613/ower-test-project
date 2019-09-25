package com.lhk.db;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Type;
import java.net.URI;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class ProperNounApplication {
    private static Gson gson = new Gson();
    private static Type strSetType = new TypeToken<Set<String>>() {
    }.getType();

    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();
        JdbcTemplate jdbcTemplate = JdbcTemplateApplication.getPocJdbcTemplate();
        URI uri = UriComponentsBuilder.fromUriString("http://192.168.11.57:10003/").build().encode().toUri();
        updateNewsProperSet(restTemplate, jdbcTemplate, uri, "POC_NEWS_v1");
        updateNewsProperSet(restTemplate, jdbcTemplate, uri, "POC_NEWS_v3");
    }

    private static void updateNewsProperSet(RestTemplate restTemplate, JdbcTemplate jdbcTemplate, URI uri, String tableName) {
        List<Map<String, Object>> mapList = jdbcTemplate.queryForList("select ID, CONT, properNounSet from " + tableName);
        for (int i = 0; i < mapList.size(); i++) {
            Map<String, Object> map = mapList.get(i);
            String content = map.get("CONT").toString();
            Set<String> mixProperNounSet = gson.fromJson(map.getOrDefault("properNounSet", "[]").toString(), strSetType);
            Map<String, Object> requestMap = new HashMap<>(1);
            requestMap.put("text", content);
            Map<String, Object> body = new HashMap<>();
            try {
                body = restTemplate.postForEntity(uri, requestMap, Map.class).getBody();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Set<String> nerProperNounSet = new HashSet<>();
            if (body.get("result") instanceof List) {
                List<Map<String, Object>> resultList = (List<Map<String, Object>>) body.get("result");
                for (Map<String, Object> res : resultList) {
                    nerProperNounSet.add(res.get("name").toString());
                }
            }
            map.put("nerProperNounSet", gson.toJson(nerProperNounSet));
            mixProperNounSet.addAll(nerProperNounSet);
            map.put("mixProperNounSet", gson.toJson(mixProperNounSet));
        }
        updateNews(jdbcTemplate, mapList, tableName);
    }

    private static void updateNews(JdbcTemplate jdbcTemplate, List<Map<String, Object>> mapList, String tableName) {
        jdbcTemplate.batchUpdate("update " + tableName + " set nerProperNounSet = ?, mixProperNounSet = ? where ID = ?", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, mapList.get(i).get("nerProperNounSet").toString());
                ps.setString(2, mapList.get(i).get("mixProperNounSet").toString());
                ps.setString(3, mapList.get(i).get("ID").toString());
            }

            @Override
            public int getBatchSize() {
                return mapList.size();
            }
        });
    }
}
