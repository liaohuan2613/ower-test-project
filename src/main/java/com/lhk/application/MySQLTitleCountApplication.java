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
import java.util.stream.Collectors;

public class MySQLTitleCountApplication {
    private final static Gson gson = new Gson();
    private final static Logger logger = LoggerFactory.getLogger(MySQLTitleCountApplication.class);

    public static void main(String[] args) {
        JdbcTemplate testJdbcTemplate = JdbcTemplateApplication.getLocalTestJdbcTemplate();
        List<Map<String, List<String>>> mapList = new ArrayList<>(7);
        for (int i = 2; i < 3; i++) {
            Map<String, List<String>> titleListMap = new HashMap<>();
            SqlRowSet sqlRowSet = testJdbcTemplate.queryForRowSet("select title from axzq_aodu_news where source = '港股公告'");
            while (sqlRowSet.next()) {
                String title = sqlRowSet.getString("title");
                if (title.length() > i) {
                    String subTitle = title.substring(0, i);
                    List<String> titleList = titleListMap.computeIfAbsent(subTitle, key -> new ArrayList<>());
                    titleList.add(title);

                    String subTitle2 = title.substring(title.length() - i);
                    List<String> titleList2 = titleListMap.computeIfAbsent(subTitle2, key -> new ArrayList<>());
                    titleList2.add(title);
                }
            }
            mapList.add(titleListMap.entrySet().stream().filter(entry -> entry.getValue().size() > 1)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        }
        System.out.println(gson.toJson(mapList));
    }
}
