package com.lhk.db;

import column.clf.ColumnClfApi;
import com.lhk.metrics.TimerMetrics;
import com.lhk.util.DateUtils;
import com.zaxxer.hikari.HikariDataSource;
import market.text.clf.MarketTextClfApi;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import preprocess.clf.column.ColumnPreprocess;
import preprocess.clf.market.MarketPreprocess;

import java.io.FileNotFoundException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static com.lhk.common.CommonFunctions.*;

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
//        checkRegex();
//        parseMarketTypeAndColumn();
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

    private static void checkRegex() {
        String[] strGroup = new String[]{"([(（])(?<!\\d)(1[012]|0?[1-9])[-/.](3[01]|[12]+[0-9]|0?[1-9])(?!\\d)([）)])涨停揭秘：.{1,16}涨停",
                "（(?<!\\d)(1[012]|0?[1-9])[-/.](3[01]|[12]+[0-9]|0?[1-9])(?!\\d)）[\\u4e00-\\u9fa5]{1,16}连续[一二三四五六七八九十0-9]{1,2}[日号]?收于年线之上(，为近[一二两三四五六七八九十零0-9]{1,10}年首次)?",
                "龙虎榜解读（(?<!\\d)(1[012]|0?[1-9])[-/.](3[01]|[12]+[0-9]|0?[1-9])(?!\\d)）:.{1,25}",
                "[\\u4e00-\\u9fa5]{2,15}(参考价|报价)(（)?[0-9]{1,2}月[0-9]{1,2}[日号]?(）)?(（上午）|（下午）)?",
                "(简讯：)?[0-9]{1,2}月[0-9]{1,2}[日号]?[\\u4e00-\\u9fa5]{2,15}报价走势稳定",
                "(简讯：|生意社：|[0-9]{2,4}年)?[0-9]{1,2}月[0-9]{1,2}[日号]?.{2,20}(行情一览|金价|走势行情|价格(行情|稳定)?|报价|行情动态|走势稳定|保持平稳|维持稳定|维持平稳)",
                "华东有色金属城[0-9]{1,2}月[0-9]{1,2}[日号]?[\\u4e00-\\u9fa5]{2,15}报价",
                ".{2,15}(报价|价格)([0-9]{1,2}月[0-9]{1,2}[日号]?|[0-9]{1,5})",
                ".{1,8}：融资净(偿还|买入)[0-9]{1,10}([点.][0-9]{1,10})?[百千万亿]{0,4}(元|人民币)，融资余额[0-9]{1,10}([点.][0-9]{1,10})?[百千万亿]{0,4}(元|人民币)（(?<!\\d)(1[012]|0?[1-9])[-/.](3[01]|[12]+[0-9]|0?[1-9])(?!\\d)）",
                ".{1,8}\\([0-9]{1,10}\\)融资融券信息\\((?<!\\d)(1[012]|0?[1-9])[-/.](3[01]|[12]+[0-9]|0?[1-9])(?!\\d)\\)",
                ".{1,8}：连续[一二三四五六七八九十0-9]{1,2}[日号]?融资净(偿还|买入)累计[0-9]{1,10}([点.][0-9]{1,10})?[百千万亿]{0,4}(元|人民币)（(?<!\\d)(1[012]|0?[1-9])[-/.](3[01]|[12]+[0-9]|0?[1-9])(?!\\d)）",
                ".{1,3}市上市公司公告\\([0-9]{1,2}月[0-9]{1,2}[日号]?\\)",
                "全球主要黄金ETFs[0-9]{1,2}月[0-9]{1,2}[日号]?持金总量与上一交易日增加[0-9]{1,10}([点.][0-9]{1,10})?[百千万亿]{0,4}吨",
                ".{3,8}（[0-9]{1,10}）龙虎榜数据（(?<!\\d)(1[012]|0?[1-9])[-/.](3[01]|[12]+[0-9]|0?[1-9])(?!\\d)）",
                "长江有色金属现货上午快讯[0-9]{1,2}月[0-9]{1,2}[日号]?\\([\\u4e00-\\u9fa5]{1,15}\\)",
                "老黑策略：.{1,15}策略",
                ".{3,8}速览晚间上市公司公告\\((?<!\\d)(1[012]|0?[1-9])[-/.](3[01]|[12]+[0-9]|0?[1-9])(?!\\d)\\)",
                ".{1,3}市今日停复牌一览\\([0-9]{1,2}月[0-9]{1,2}[日号]?\\)",
                "[\\u4e00-\\u9fa5]{1,10}[0-9]{1,2}月[0-9]{1,2}[日号]?([0-9]{1,3}只个股)?发生[0-9]{1,3}笔大宗交易 (共)?成交[0-9]{1,10}([点.][0-9]{1,10})?[百千万亿]{0,4}(元|人民币)",
                ".{1,8}预计前[一二三四五六七八九十0-9]{1,2}季度净利润[0-9]{1,10}([点.][0-9]{1,10})?[百千万亿]{0,4}(元|人民币)至[0-9]{1,10}([点.][0-9]{1,10})?[百千万亿]{0,4}(元|人民币) 同比(变动|增长|下降)[-0-9]{1,4}%至[-0-9]{1,4}%",
                "上海现货-.{1,30}",
                "上海金基准价([早午])盘报[0-9]{1,10}([点.][0-9]{1,10})?[百千万亿]{0,4}(元|人民币)/克",
                "(栓子说汇事：)?远东贵金属(?<!\\d)(19\\d{2}|20\\d{2}|\\d{2})[-/.](1[012]|0?[1-9])[-/.](3[01]|[12]+[0-9]|0?[1-9])(?!\\d)金银原油白盘操作建议",
                "机构账户[0-9]{1,2}月[0-9]{1,2}[日号]?大宗交易净买入.{1,5}[0-9]{1,10}([点.][0-9]{1,10})?[百千万亿]{0,4}(元|人民币)",
                "操盘必读：[0-9]{1,2}月[0-9]{1,2}[日号]?证券市场要闻",
                "广东有色现货报价\\(南储华(南|东|西|北)地区\\)[0-9]{1,2}月[0-9]{1,2}[日号]?[(（][\\u4e00-\\u9fa5]{1,15}[）)]",
                "季峥：今日市场观点 (?<!\\d)(19\\d{2}|20\\d{2}|\\d{2})[-/.](1[012]|0?[1-9])[-/.](3[01]|[12]+[0-9]|0?[1-9])(?!\\d)",
                "国际废旧金属现货行情（[0-9]{1,2}月[0-9]{1,2}[日号]?）(.{1,30})?",
                "国际贵金属行情(?<!\\d)(19\\d{2}|20\\d{2}|\\d{2})[-/.](1[012]|0?[1-9])[-/.](3[01]|[12]+[0-9]|0?[1-9])(?!\\d)",
                "南海有色金属及废金属（[0-9]{1,2}月[0-9]{1,2}[日号]?）现货参考行情",
                ".{3,8}(盘中最高|盘中最低价|收报|报收)[0-9]{1,10}([点.][0-9]{1,10})?[百千万亿]{0,4}(元|人民币).{0,10}创.{1,10}(新低|新高)(，总市值[0-9]{1,10}([点.][0-9]{1,10})?[百千万亿]{0,4}(元|人民币))?",
                "上海.{4,25}([（(])[\\u4e00-\\u9fa5]{1,15}([）)])([0-9]{1,6})?",
                "上海华通铂银交易市场交易行情[0-9]{1,2}月[0-9]{1,2}[日号]?",
                "今日[\\u4e00-\\u9fa5]{1,10}行情价格[0-9]{1,2}月[0-9]{1,2}[日号]?",
                "[0-9]{1,2}月[0-9]{1,2}[日号]?[\\u4e00-\\u9fa5]{1,8}行情一览",
                "[0-9]{1,2}月[0-9]{1,2}[日号]? (NYMEX|COMEX) [0-9]{1,2}月[\\u4e00-\\u9fa5]{1,5}未平仓(合约|约)(减少|增加)[0-9]{1,5}手",
                ".{3,8}：融资净(偿还|买入)[0-9]{1,10}([点.][0-9]{1,10})?[百千万亿]{0,4}(元|人民币)，.{1,4}市排名第[一二三四五六七八九十0-9]{1,3}（(?<!\\d)(1[012]|0?[1-9])[-/.](3[01]|[12]+[0-9]|0?[1-9])(?!\\d)）",
                ".{3,8}股东户数(不变|下降[0-9]{1,10}([点.][0-9]{1,10})?%|(增加|减少)[0-9]{1,8}户)，户均持股[0-9]{1,10}([点.][0-9]{1,10})?[百千万亿]{0,4}(元|人民币)",
                "([0-9]{2,4}年)?[0-9]{1,2}月[0-9]{1,2}[日号]?.{5,27}(精华摘要|统计|信息|最新快递|汇总|消息速递|提示|消息一览)(\\(附名单\\)|（附新闻联播集锦）|\\(附股\\)|\\(A股\\)|\\(B股\\))?",
                "【调研快报】.{4,30}调研",
                "Hussey铜业公司[0-9]{1,2}月[0-9]{1,2}[日号]?铜价报[0-9]{1,10}([点.][0-9]{1,10})?[百千万亿]{0,4}(美元|元|人民币)/磅",
                ".{3,8}拟([0-9]{1,10}([点.][0-9]{1,10})?[百千万亿]{0,4}(美元|元|人民币)回购|回购[0-9]{1,10}([点.][0-9]{1,10})?[百千万亿]{0,4}股)股(权|份)(激励股份)?并注销",
                "下周沪深上市公司重大事项公告最新快递\\((?<!\\d)(1[012]|0?[1-9])[-/.](3[01]|[12]+[0-9]|0?[1-9])(?!\\d)\\)",
                "上海黄金交易所[0-9]{2,4}年[0-9]{1,2}月[0-9]{1,2}[日号]?交易行情",
                ".{3,8}成交[0-9]{1,10}([点.][0-9]{1,10})?[百千万亿]{0,4}(美元|元|人民币)两市居首，一文细看今日A股风云\\((?<!\\d)(1[012]|0?[1-9])[-/.](3[01]|[12]+[0-9]|0?[1-9])(?!\\d)\\)",
                ".{3,8}：[0-9]{1,4}前[一二三四五六七八九十0-9]{1,3}季度.{5,20}",
                "[0-9]{1,4}家公司公告进行股东增减持 .{3,8}[0-9]{1,10}([点.][0-9]{1,10})?[百千万亿]{0,4}股今日解禁",
                "[0-9]{2,4}年[0-9]{1,2}月[0-9]{1,2}[日号]?.{3,8}(集团铝价|分销价持平)",
                "北向资金今日净流入[0-9]{1,10}([点.][0-9]{1,10})?[百千万亿]{0,4}(美元|元|人民币) 大幅净买入[\\u4e00-\\u9fa5]{1,5}[0-9]{1,10}([点.][0-9]{1,10})?[百千万亿]{0,4}(美元|元|人民币)",
                "[0-9]{1,2}月[0-9]{1,2}[日号]?股指期货成交持仓龙虎榜"};
        Pattern[] ps = new Pattern[strGroup.length];
        for (int i = 0; i < strGroup.length; i++) {
            String regex = strGroup[i];
            ps[i] = Pattern.compile(regex);
        }
        JdbcTemplate localTestJdbcTemplate = getLocalTestJdbcTemplate();
        SqlRowSet sqlRowSet = localTestJdbcTemplate.queryForRowSet("select TIT from template_news_v1");
        while (sqlRowSet.next()) {
            String title = sqlRowSet.getString("TIT");
            boolean isFlag = true;
            for (Pattern p : ps) {
                if (p.matcher(title).matches()) {
                    isFlag = false;
                }
            }
            if (isFlag) {
                System.out.println(title);
            }
        }
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
        dataSource.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B8");
        dataSource.setUsername("root");
        dataSource.setPassword("password1!");
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

    public static JdbcTemplate getFhlJdbcTemplate() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setJdbcUrl("jdbc:mysql://203.156.205.101:11706/newsAudit?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B8");
        dataSource.setUsername("root");
        dataSource.setPassword("Mdrz#F(K14(oLcsVd^cH");
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
