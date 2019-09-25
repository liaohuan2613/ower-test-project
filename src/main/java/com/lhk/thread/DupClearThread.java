package com.lhk.thread;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lhk.domain.EventCompanyData;
import com.lhk.util.HTMLFormatUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class DupClearThread implements Runnable {
    private Gson gson = new Gson();
    private static Type eventListType = new TypeToken<List<EventCompanyData>>() {
    }.getType();
    private int skipSize;
    private JdbcTemplate jdbcTemplate;

    public DupClearThread(int skipSize, JdbcTemplate jdbcTemplate) {
        this.skipSize = skipSize;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run() {
        List<Map<String, Object>> maps = jdbcTemplate.queryForList("select groupID, dupID, CONT from dup_news_v4 limit " + skipSize + ", 1000");
        for (Map<String, Object> map : maps) {
            try {
                String groupId = map.get("groupID").toString();
                String dupId = map.get("dupID").toString();
                String content = HTMLFormatUtils.clearOnlyHtml(map.get("CONT").toString());
                if ("".equals(HTMLFormatUtils.clearOnlyHtml(content))) {
                    System.out.println(groupId + ", " + dupId + ", " + content);
                }
                jdbcTemplate.update("update dup_news_v4 set CONT = ? where groupID = ? and dupID = ?", ps -> {
                    ps.setString(1, content);
                    ps.setString(2, groupId);
                    ps.setString(3, dupId);
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
