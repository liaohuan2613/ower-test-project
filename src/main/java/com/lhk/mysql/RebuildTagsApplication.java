package com.lhk.mysql;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import preprocess.clf.column.ColumnPreprocess;
import preprocess.clf.market.MarketPreprocess;

import java.net.URI;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.lhk.common.CommonFunctions.*;
import static com.lhk.mysql.JdbcTemplateApplication.getJdbcTemplate;
import static com.lhk.util.HTMLFormatUtils.filterHtml;

public class RebuildTagsApplication {

    private static Logger logger = LoggerFactory.getLogger(RebuildTagsApplication.class);

    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 4, 0,
            TimeUnit.MINUTES, new LinkedBlockingQueue<>());

    public static void main(String[] args) {
        parseMarketTypeAndColumn();
    }

    private static void parseMarketTypeAndColumn() {
        for (int i = 11; i < 64; i++) {
            basicOperation(i, 100);
        }
        executor.shutdown();
    }

    private static void basicOperation(int page, int size) {
        executor.execute(() -> {
            JdbcTemplate jdbcTemplate = jdbcTemplateThreadLocal.get();
            if (jdbcTemplate == null) {
                jdbcTemplate = getJdbcTemplate();
                jdbcTemplateThreadLocal.set(jdbcTemplate);
            }
            RestTemplate restTemplate = restTemplateThreadLocal.get();
            if (restTemplate == null) {
                restTemplate = new RestTemplate();
                restTemplateThreadLocal.set(restTemplate);
            }
            SqlRowSet rowSet = jdbcTemplate.queryForRowSet("select\n" +
                    " title, content,'快讯' category, NOTICE_TYPE block, NOTICE_DATE postDate\n" +
                    "from\n" +
                    " Eastmoney_notice\n" +
                    "where\n" +
                    " NOTICE_DATE >= '2019-06-03'\n" +
                    " and\n" +
                    " NOTICE_DATE < '2019-06-10'\n" +
                    " limit " + (page * size) + "," + size);

            List<Document> resultMapList = new ArrayList<>();

            while (rowSet.next()) {
                Document tempMap = new Document();
                String title = rowSet.getString("title");
                String content = filterHtml(rowSet.getString("content"));
                String originCategory = rowSet.getString("category");
                String originSubCategory = rowSet.getString("block");
                String createDate = "2019-06-17 00:00:00";
                String postDate = rowSet.getString("postDate") + " 00:00:00";

                tempMap.put("title", title);
                tempMap.put("content", content);
                tempMap.put("mktCd", "*");
                Map<String, Object> responseMap = makeTags("http://127.0.0.1:7003/rest/block/general-analyse-recommend",
                        tempMap, restTemplate);
                List<Map<String, Object>> showTags = (List<Map<String, Object>>) responseMap.get("showTags");
                String firstCategory = "";
                if (showTags.size() > 0) {
                    firstCategory = showTags.get(0).getOrDefault("category", "").toString();
                }
                tempMap.remove("mktCd");
                tempMap.put("firstCategory", firstCategory);
                tempMap.put("autoTags", responseMap.get("autoTags"));
                tempMap.put("showTags", responseMap.get("showTags"));
                tempMap.put("originCategory", originCategory);
                tempMap.put("originSubCategory", originSubCategory);
                tempMap.put("createDate", createDate);
                tempMap.put("postDate", postDate);
                resultMapList.add(tempMap);
            }

            jdbcTemplate.batchUpdate("insert into all_eastmoney_tags_v3(title,content,origCategory,origSubcategory," +
                            "createDate,postDate,firstCategory,autoTags,showTags) values(?,?,?,?,?,?,?,?,?)",
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setObject(1, resultMapList.get(i).get("title"));
                            ps.setObject(2, resultMapList.get(i).get("content"));
                            ps.setObject(3, resultMapList.get(i).get("originCategory"));
                            ps.setObject(4, resultMapList.get(i).get("originSubCategory"));
                            ps.setObject(5, resultMapList.get(i).get("createDate"));
                            ps.setObject(6, resultMapList.get(i).get("postDate"));
                            ps.setObject(7, resultMapList.get(i).get("firstCategory"));
                            ps.setObject(8, resultMapList.get(i).get("autoTags").toString());
                            ps.setObject(9, resultMapList.get(i).get("showTags").toString());
                        }

                        @Override
                        public int getBatchSize() {
                            return resultMapList.size();
                        }
                    });
        });
    }
}
