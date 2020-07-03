package com.lhk.application;

import com.google.gson.Gson;
import com.lhk.db.JdbcTemplateApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MySQLDataApplication {
    private final static Gson gson = new Gson();
    private final static Logger logger = LoggerFactory.getLogger(MySQLDataApplication.class);

    public static void main(String[] args) {
        JdbcTemplate testJdbcTemplate = JdbcTemplateApplication.getLocalTestJdbcTemplate();
        SqlRowSet sqlRowSet = testJdbcTemplate.queryForRowSet("select count(*) count_num from axzq_checked_dup_news where is_dup = 'true'");
        int len = 0;
        int size = 1000;
        if (sqlRowSet.next()) {
            int countNum = sqlRowSet.getInt("count_num");
            len = countNum / size + 1;
        }

        for (int i = 0; i < len; i++) {
            insertData(testJdbcTemplate, i, size);
        }
    }

    private static void insertData(JdbcTemplate testJdbcTemplate, int page, int size) {
        SqlRowSet sqlRowSet = testJdbcTemplate.queryForRowSet("select id,similar_ids " +
                " from axzq_checked_dup_news where is_dup = 'true' limit " + page * size + ", " + size);
        List<Map<String, String>> dupResultList = new ArrayList<>();
        while (sqlRowSet.next()) {
            String groupId = sqlRowSet.getString("id");
            String similarIds = sqlRowSet.getString("similar_ids");
            List<String> strList = gson.fromJson(similarIds, List.class);
            strList.add(groupId);
            strList.forEach(id -> {
                Map<String, String> tempMap = new HashMap<>();
                tempMap.put("id", id);
                tempMap.put("group_id", groupId);
                dupResultList.add(tempMap);
            });
        }


        System.out.println("dup list size is [" + dupResultList.size() + "]");

        testJdbcTemplate.batchUpdate("insert into axzq_dup_list(group_id, id) values(?, ?)",
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
