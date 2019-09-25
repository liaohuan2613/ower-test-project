package com.lhk.thread;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lhk.domain.BlockWeightTag;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HotCountThread implements Runnable {
    private Gson gson = new Gson();
    private static Type tagListType = new TypeToken<List<BlockWeightTag>>() {
    }.getType();
    private String dateFormat;
    private JdbcTemplate jdbcTemplate;

    public HotCountThread(String dateFormat, JdbcTemplate jdbcTemplate) {
        this.dateFormat = dateFormat;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run() {
        List<Map<String, Object>> maps = jdbcTemplate.queryForList("select * from POC_NEWS_NEW_SOURCE_v7 " +
                " where PUB_DT like '" + dateFormat + "%'");
        Map<String, String> idNameMap = new HashMap<>();
        Map<String, Integer> codeCountMap = new HashMap<>();
        for (Map<String, Object> map : maps) {
            String id = map.get("ID").toString();
            String maxName = "";
            double maxWeight = -1;
            List<BlockWeightTag> tagList = new ArrayList<>();
            tagList.addAll(gson.fromJson(map.get("CONCEPT_TAGS").toString(), tagListType));
            tagList.addAll(gson.fromJson(map.get("STOCK_TAGS").toString(), tagListType));
            for (BlockWeightTag tag : tagList) {
                String name = tag.getTagName();
                double weight = tag.getWeight();
                if (maxWeight < weight) {
                    maxName = name;
                    maxWeight = weight;
                }
                if (weight > 60) {
                    codeCountMap.put(name, codeCountMap.getOrDefault(name, 0) + 1);
                }
            }
            idNameMap.put(id, maxName);
        }

        int allMaxHot = -1;
        for (int hot : codeCountMap.values()) {
            if (allMaxHot < hot) {
                allMaxHot = hot;
            }
        }
        for (Map.Entry<String, String> entry : idNameMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            double floor = Math.floor(codeCountMap.getOrDefault(value, 0) * 1.0 / allMaxHot * 10) <= 0.1 ?
                    1.0 : Math.floor(codeCountMap.getOrDefault(value, 0) * 1.0 / allMaxHot * 10);
            jdbcTemplate.update("update POC_NEWS_NEW_SOURCE_v7 set HOT_NAME = ?, HOT_VALUE = ? where ID = ?", ps -> {
                ps.setString(1, "".equals(value) ? "其他" : value);
                ps.setDouble(2, floor);
                ps.setString(3, key);
                System.out.println(key + "<==================>" + value + "<===========================>" + floor);
            });
        }
    }
}
