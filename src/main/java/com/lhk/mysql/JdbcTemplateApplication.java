package com.lhk.mysql;

import column.clf.ColumnClfApi;
import com.lhk.metrics.TimerMetrics;
import com.zaxxer.hikari.HikariDataSource;
import market.text.clf.MarketTextClfApi;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import preprocess.clf.column.ColumnPreprocess;
import preprocess.clf.market.MarketPreprocess;

import java.io.FileNotFoundException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.lhk.common.CommonFunctions.*;

public class JdbcTemplateApplication {

    private static Logger logger = LoggerFactory.getLogger(JdbcTemplateApplication.class);

    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 2, 0,
            TimeUnit.MINUTES, new LinkedBlockingQueue<>());

    public static void main(String[] args) {
        parseMarketTypeAndColumn();
    }

    private static void parseMarketTypeAndColumn() {
        for (int i = 0; i < 1; i++) {
            basicOperation(i, 1000);
        }
        executor.shutdown();
    }

    private static void basicOperation(int page, int size) {
        executor.execute(() -> {
            try {
                TimerMetrics timerMetrics = new TimerMetrics();
                long initStartTime = System.currentTimeMillis();
                MarketTextClfApi marketTextClfApi = marketTextClfApiThreadLocal.get();
                if (marketTextClfApi == null) {
                    marketTextClfApi = new MarketTextClfApi("/root/evaluation/models/market/");
                    marketTextClfApiThreadLocal.set(marketTextClfApi);
                }
                ColumnClfApi columnClfApi = columnClfApiThreadLocal.get();
                if (columnClfApi == null) {
                    columnClfApi = new ColumnClfApi("/root/evaluation/models/market/");
                    columnClfApiThreadLocal.set(columnClfApi);
                }
                long initEndTime = System.currentTimeMillis();
                timerMetrics.putTimer("initModel", initEndTime - initStartTime);

                JdbcTemplate jdbcTemplate = jdbcTemplateThreadLocal.get();
                if (jdbcTemplate == null) {
                    jdbcTemplate = getJdbcTemplate();
                    jdbcTemplateThreadLocal.set(jdbcTemplate);
                }

                long selectStartTime = System.currentTimeMillis();
                SqlRowSet rowSet = jdbcTemplate.queryForRowSet("select title, content from all_eastmoney_tags_v4 limit "
                        + (page * size) + "," + size);
                List<String> marketTextList = new ArrayList<>();
                List<String> columnTextList = new ArrayList<>();
                List<Document> resultMapList = new ArrayList<>();
                long selectEndTime = System.currentTimeMillis();
                timerMetrics.putTimer("rowSetTimer", selectEndTime - selectStartTime);

                long modelStartTime = System.currentTimeMillis();
                while (rowSet.next()) {
                    Document tempMap = new Document();
                    String title = rowSet.getString("title");
                    String content = rowSet.getString("content");
                    String originCategory = rowSet.getString("category");
                    String originSubCategory = rowSet.getString("block");
                    String createDate = rowSet.getString("createDate");
                    String postDate = rowSet.getString("postDate");

                    tempMap.put("title", title);
                    tempMap.put("content", content);
                    tempMap.put("originCategory", originCategory);
                    tempMap.put("originSubCategory", originSubCategory);
                    tempMap.put("createDate", createDate);
                    tempMap.put("postDate", postDate);

                    String segText = segWords(title + " #SQCONTENT# " + content, true);
                    String marketText = MarketPreprocess.preprocessText(segText);
                    String columnText = ColumnPreprocess.preprocessText(segText);
//                    String segText = title + " #SQCONTENT# " + content;
//                    String marketText = MarketPreprocess.preprocessText(segText);
//                    String columnText = ColumnPreprocess.preprocessText(segText);

//                    marketTextList.add(segWords(marketText, true));
//                    columnTextList.add(segWords(columnText, true));
                    marketTextList.add(marketText);
                    columnTextList.add(columnText);
                    resultMapList.add(tempMap);
                }
                long modelEndTime = System.currentTimeMillis();
                timerMetrics.putTimer("modelEndTimer", modelEndTime - modelStartTime);

                logger.info("timerMetrics; {}", timerMetrics);

                List<List<Map.Entry<String, Double>>> marketEntryList = formatPredictList(marketTextClfApi.predict(marketTextList));
                List<List<Map.Entry<String, Double>>> columnEntryList = formatPredictList(columnClfApi.predict(columnTextList));

                mergeList(resultMapList, marketTextList, marketEntryList, columnTextList, columnEntryList);
                jdbcTemplate.batchUpdate("insert into all_eastmoney_other_v4(title,content,origCategory,origSubcategory,createDate,postDate," +
                                " marketWordSegment,columnWordSegment,marketCategories,columnCategories,marketCategory,columnCategory) " +
                                " values(?,?,?,?,?,?,?,?,?,?,?,?)",
                        new BatchPreparedStatementSetter() {
                            @Override
                            public void setValues(PreparedStatement ps, int i) throws SQLException {
                                ps.setObject(1, resultMapList.get(i).get("title"));
                                ps.setObject(2, resultMapList.get(i).get("content"));
                                ps.setObject(3, resultMapList.get(i).get("originCategory"));
                                ps.setObject(4, resultMapList.get(i).get("originSubCategory"));
                                ps.setObject(5, resultMapList.get(i).get("createDate"));
                                ps.setObject(6, resultMapList.get(i).get("postDate"));
                                ps.setObject(7, resultMapList.get(i).get("marketWordSegment"));
                                ps.setObject(8, resultMapList.get(i).get("columnWordSegment"));
                                ps.setObject(9, resultMapList.get(i).get("marketCategories").toString());
                                ps.setObject(10, resultMapList.get(i).get("columnCategories").toString());
                                ps.setObject(11, resultMapList.get(i).get("marketCategory"));
                                ps.setObject(12, resultMapList.get(i).get("columnCategory"));
                            }

                            @Override
                            public int getBatchSize() {
                                return resultMapList.size();
                            }
                        });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    public static JdbcTemplate getJdbcTemplate() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setJdbcUrl("jdbc:mysql://47.96.3.207:3306/NEWS_FEED?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B8");
        dataSource.setUsername("canal");
        dataSource.setPassword("ui#xctk!mzOc$aOC");
        return new JdbcTemplate(dataSource);
    }

    public static JdbcTemplate getSpiderJdbcTemplate() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setJdbcUrl("jdbc:mysql://118.184.215.7:12306/pyspider_resultdb?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B8");
        dataSource.setUsername("pyspider");
        dataSource.setPassword("strzsJQWpiuw9oKB");
        return new JdbcTemplate(dataSource);
    }

    public static JdbcTemplate getLocalJdbcTemplate() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/gorich_test?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B8");
        dataSource.setUsername("root");
        dataSource.setPassword("password1!");
        return new JdbcTemplate(dataSource);
    }
}
