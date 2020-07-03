package com.lhk.db;

import com.lhk.util.DateUtils;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class JdbcTemplateApplication {

    private static Logger logger = LoggerFactory.getLogger(JdbcTemplateApplication.class);

    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 2, 0,
            TimeUnit.MINUTES, new LinkedBlockingQueue<>());

    public static void main(String[] args) {
        JdbcTemplate oracleJdbcTemplate = getOracleJdbcTemplate();
//        int[] update = oracleJdbcTemplate.batchUpdate("update DICT_IGNORE_REGEX set created_by = 'admin' where id = '7b29e9c7-7c57-11e9-aa00-fa163e01e774'");
//        System.out.println(Arrays.toString(update));
        List<Map<String, Object>> mapList = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("regex", ".*(券商|证券行业|证券业|证券市场).*");
        map.put("memo", null);
        map.put("last_modified_by", "admin");
        map.put("created_date", "2019-05-22 00:00:00");
        map.put("last_modified_date", "2019-05-22 00:00:00");
        map.put("id", "7b29e9c7-7c57-11e9-aa00-fa163e01e7741");
        map.put("market_code", "*");
        map.put("category", "MARKET_SECURITY");
        map.put("created_by", "admin");
        mapList.add(map);
        Map<String, Object> map2 = new HashMap<>();
        map2.put("regex", "(?<!\\d)(,|，)");
        map2.put("memo", null);
        map2.put("last_modified_by", "admin");
        map2.put("created_date", "2019-01-23 00:00:00");
        map2.put("last_modified_date", "2019-01-23 00:00:00");
        map2.put("id", "8518b0aa-1ebc-11e9-9ed6-00163e02e09c");
        map2.put("market_code", "*");
        map2.put("category", "EventIgnore");
        map2.put("created_by", "admin");
        mapList.add(map2);

        String sql = "update dict_ignore_regex set regex = ?, last_modified_date = ?, memo = ?, last_modified_by = ?, created_date = ?, market_code = ?, category = ?, created_by = ?  where id = ?";
        List<String> updateList = new ArrayList<>();
        updateList.add("regex");
        updateList.add("last_modified_date");
        updateList.add("memo");
        updateList.add("last_modified_by");
        updateList.add("created_date");
        updateList.add("market_code");
        updateList.add("category");
        updateList.add("created_by");
        updateList.add("id");
        List<String> timestampFiledList = new ArrayList<>();
        timestampFiledList.add("last_modified_date");
        timestampFiledList.add("created_date");
        testBatchUpdate(oracleJdbcTemplate, mapList, sql, updateList, timestampFiledList);
    }

    private static void testBatchUpdate(JdbcTemplate jdbcTemplate, List<Map<String, Object>> mapList, String sql,
                                        List<String> updateList, List<String> timestampFiledList) {
        int[] ints = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                int keyCount = 0;
                for (String key : updateList) {
                    if (timestampFiledList.contains(key) && mapList.get(i).get(key) != null) {
                        preparedStatement.setTimestamp(++keyCount, new Timestamp(DateUtils.parse(mapList.get(i).get(key).toString()).getTime()));
                    } else {
                        preparedStatement.setObject(++keyCount, mapList.get(i).get(key));
                    }
                }
            }

            @Override
            public int getBatchSize() {
                return mapList.size();
            }
        });
        System.out.println(Arrays.toString(ints));

        List<String> cloudPrimaryKeyList = new ArrayList<>();
        cloudPrimaryKeyList.add("id");
        int[] resultInts = new int[mapList.size()];
        int valueCount = 0;
        for (Map<String, Object> map : mapList) {
            int finalValueCount = valueCount;
            jdbcTemplate.query(parseSelectSql("dict_ignore_regex", cloudPrimaryKeyList, cloudPrimaryKeyList),
                    ps -> {
                        int keyCount = 0;
                        for (String cloudPrimaryKey : cloudPrimaryKeyList) {
                            ps.setObject(++keyCount, map.get(cloudPrimaryKey));
                        }
                    },
                    rs -> {
                        resultInts[finalValueCount] = 1;
                    });
            valueCount++;
        }
        System.out.println(Arrays.toString(resultInts));
    }

    private static String parseSelectSql(String tableName, List<String> setFiledList, List<String> valueFiledList) {
        StringBuilder selectSetSql = new StringBuilder();
        selectSetSql.append("select ");
        boolean isFirst = true;
        for (String clientFiled : setFiledList) {
            if (isFirst) {
                selectSetSql.append(clientFiled);
                isFirst = false;
            } else {
                selectSetSql.append(", ").append(clientFiled);
            }
        }
        StringBuilder selectWhereSql = new StringBuilder();
        isFirst = true;
        for (String primaryKey : valueFiledList) {
            if (isFirst) {
                selectWhereSql.append(" where ").append(primaryKey).append(" = ?");
                isFirst = false;
            } else {
                selectWhereSql.append(" and ").append(primaryKey).append(" = ?");
            }
        }
        return selectSetSql + " from " + tableName + " " + selectWhereSql;
    }

    public static JdbcTemplate getJdbcTemplate() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setJdbcUrl("jdbc:mysql://47.96.3.207:3306/NEWS_FEED?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B8");
        dataSource.setUsername("canal");
        dataSource.setPassword("ui#xctk!mzOc$aOC");
        return new JdbcTemplate(dataSource);
    }

    public static JdbcTemplate getPocJdbcTemplate() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/anxin?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B8");
        dataSource.setUsername("root");
        dataSource.setPassword("password1!");
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


    public static JdbcTemplate getLocalTestJdbcTemplate() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setJdbcUrl("jdbc:mysql://47.116.58.10:3306/poc?useUnicode=true&characterEncoding=utf8&useSSL=true&verifyServerCertificate=false&serverTimezone=GMT%2B8");
        dataSource.setUsername("gorich");
        dataSource.setPassword("GPsbcj6tvVjn8aNX");
        return new JdbcTemplate(dataSource);
    }

    public static JdbcTemplate getOracleJdbcTemplate() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
        dataSource.setJdbcUrl("jdbc:oracle:thin:@47.96.26.118:1521/XE");
        dataSource.setUsername("LH");
        dataSource.setPassword("PASSWORD1!");
        return new JdbcTemplate(dataSource);
    }

    public static JdbcTemplate getZGNewsJdbcTemplate() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setJdbcUrl("jdbc:mysql://47.103.102.172:3308/zgnews?useUnicode=true&characterEncoding=utf8&useSSL=false");
        dataSource.setUsername("zgnews");
        dataSource.setPassword("1qaz@WSX#EDC");
        return new JdbcTemplate(dataSource);
    }

}
