package com.lhk.thread;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lhk.domain.EventCompanyData;
import com.lhk.util.HTMLFormatUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class SummaryClearThread2 implements Runnable {
    private Gson gson = new Gson();
    private static Type eventListType = new TypeToken<List<EventCompanyData>>() {
    }.getType();
    private int skipSize;
    private JdbcTemplate jdbcTemplate;

    public SummaryClearThread2(int skipSize, JdbcTemplate jdbcTemplate) {
        this.skipSize = skipSize;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run() {
        List<Map<String, Object>> maps = jdbcTemplate.queryForList("select ID, SUMMARY from poc_news_new_source_v6 limit " + skipSize + ", 1000");
        for (Map<String, Object> map : maps) {
            try {
                String id = map.get("ID").toString();
                String originSummary = HTMLFormatUtils.clearOnlyHtml(map.get("SUMMARY").toString());
                String summary = originSummary.startsWith("\"") ? originSummary.substring(1, originSummary.length() - 1) : originSummary;
                System.out.println("=================" + summary);
                jdbcTemplate.update("update poc_news_new_source_v6 set SUMMARY = '" + summary + "' where ID = '" + id + "'");
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
