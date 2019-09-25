package com.lhk.thread;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lhk.domain.BlockWeightTag;
import com.lhk.domain.EventCompanyData;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.reflect.Type;
import java.util.*;

public class EventsClearThread implements Runnable {
    private Gson gson = new Gson();
    private static Type eventListType = new TypeToken<List<EventCompanyData>>() {
    }.getType();
    private int skipSize;
    private JdbcTemplate jdbcTemplate;

    public EventsClearThread(int skipSize, JdbcTemplate jdbcTemplate) {
        this.skipSize = skipSize;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run() {
        List<Map<String, Object>> maps = jdbcTemplate.queryForList("select ID, EVENTS from poc_news_new_source_v9 limit " + skipSize + ", 1000");
        for (Map<String, Object> map : maps) {
            try {
                String id = map.get("ID").toString();
                String eventsStr = map.get("EVENTS").toString().replace("\\", "、");
                List<EventCompanyData> events = gson.fromJson(eventsStr, eventListType);
                events.removeIf(event -> event.getOriginalText().contains("证券：") || event.getOriginalText().contains("证券表示") || event.getOriginalText().contains("证券判断"));
                jdbcTemplate.update("update poc_news_new_source_v9 set EVENTS = ? where ID = ?", ps -> {
                    ps.setString(1, gson.toJson(events).replaceAll("'", "\\\\\'"));
                    ps.setString(2, id);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static String eventsStrParse(String originEventsStr) {
        String[] originTextGroup = originEventsStr.split("\"originalText\":\"");
        StringBuilder sb = new StringBuilder(originTextGroup[0]);
        for (int i = 1; i < originTextGroup.length; i++) {
            String[] typeGroup = originTextGroup[i].split("\",\"type\"");
            sb.append("\"originalText\":\"")
                    .append(typeGroup[0].replaceAll("\"", "\\\\\""))
                    .append("\",\"type\"").append(typeGroup[1]);
        }
        return sb.toString();
    }
}
