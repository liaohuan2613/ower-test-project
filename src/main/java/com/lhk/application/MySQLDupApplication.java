package com.lhk.application;

import com.google.gson.Gson;
import com.lhk.db.JdbcTemplateApplication;
import com.lhk.response.ArticleDupResponse;
import com.lhk.response.ArticleDupResult;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MySQLDupApplication {
    private static RestTemplate restTemplate = new RestTemplate();
    private static String dupUrl = "http://localhost:5002/api/deduplication";

    public static void main(String[] args) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(60000);
        factory.setReadTimeout(60000);
        restTemplate.setRequestFactory(factory);
        Gson gson = new Gson();

        JdbcTemplate testJdbcTemplate = JdbcTemplateApplication.getLocalTestJdbcTemplate();
        SqlRowSet sqlRowSet = testJdbcTemplate.queryForRowSet("select distinct * from axzq_dup_list");
        List<Map<String, String>> dupResultList = new ArrayList<>();
        while (sqlRowSet.next()) {
            Map<String, Object> requestMap = new HashMap<>();
            String id = sqlRowSet.getString("id");
            if (!StringUtils.isEmpty(sqlRowSet.getString("title"))) {
                requestMap.put("id", id);
                requestMap.put("title", sqlRowSet.getString("title"));
                requestMap.put("content", sqlRowSet.getString("content"));
                requestMap.put("source", "安信");
                requestMap.put("database", 10);
                URI targetUrl = UriComponentsBuilder.fromUriString(dupUrl).build().encode().toUri();
                String body = restTemplate.postForEntity(targetUrl, requestMap, String.class).getBody();
                ArticleDupResponse response = gson.fromJson(body, ArticleDupResponse.class);
                if (response.getResult().isIsDup() != null && response.getResult().isIsDup()) {
                    response.getResult().getDupIds().forEach(dupId -> {
                        Map<String, String> dup = new HashMap<>();
                        dup.put("group_id", id);
                        dup.put("id", dupId.getId());
                        dupResultList.add(dup);
                    });
                }
            }

            testJdbcTemplate.batchUpdate("insert into axzq_dup_list2(group_id, id) values(?, ?)",
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setString(1, dupResultList.get(i).get("group_id"));
                            ps.setString(2, dupResultList.get(i).get("id"));
                        }

                        @Override
                        public int getBatchSize() {
                            return dupResultList.size();
                        }
                    });
        }
    }
}
