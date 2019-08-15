package com.lhk.mysql;

import com.lhk.mongodb.MongoTemplateApplication;
import com.mongodb.BasicDBObject;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CommentApplication {
    public static void main(String[] args) {
        List<String> stockList = Arrays.asList("SH_601899", "SH_600740");
        List<String> conceptList = new ArrayList<>();
        MongoTemplate mongoTemplate = MongoTemplateApplication.getMongoTemplate();
        JdbcTemplate jdbcTemplate = JdbcTemplateApplication.getJdbcTemplate();
        JdbcTemplate spiderJdbcTemplate = JdbcTemplateApplication.getSpiderJdbcTemplate();
        JdbcTemplate localJdbcTemplate = JdbcTemplateApplication.getLocalJdbcTemplate();
        BasicDBObject findBasic = new BasicDBObject();
        List<BasicDBObject> orBasicList = new ArrayList<>();
        orBasicList.add(new BasicDBObject("stockCodes", new BasicDBObject("$in", stockList)));
        orBasicList.add(new BasicDBObject("conceptCodes", new BasicDBObject("$in", conceptList)));
        findBasic.put("$or", orBasicList);
        findBasic.append("$where", "(this.content.length > 150)");
        BasicDBObject sortBasic = new BasicDBObject("date", -1);
        sortBasic.append("time", -1);

        boolean isFirst = true;
        StringBuilder sb = new StringBuilder();
        for (String stockCode : stockList) {
            if (isFirst) {
                isFirst = false;
                sb.append("'").append(stockCode).append("'");
            } else {
                sb.append(",'").append(stockCode).append("'");
            }
        }
        List<Map<String, Object>> mapList = localJdbcTemplate.queryForList("select object_code from block_block_relation " +
                " where type = 'STOCK_CONCEPT_RELATION' and subject_code in (" + sb.toString() + ")");
        mapList.forEach(map -> conceptList.add(map.get("object_code").toString()));

        Map<String, Object> map = jdbcTemplate.queryForMap("SELECT * FROM lian_v1_article WHERE `title` LIKE '%每日收评%' ORDER BY ctime DESC limit 1");
        String title = map.get("title").toString();
        String content = map.get("content").toString();
        String[] contentGroup = content.split("</p>\n");
        StringBuilder resultSb = new StringBuilder();

        resultSb.append(title).append("\n");
        List<String> stockList2 = new ArrayList<>();
        stockList.forEach(stock -> {
            String[] split = stock.split("_");
            stockList2.add(split[0].toLowerCase() + split[1]);
        });
        resultSb.append("自选股今日表现：").append(getCodeResult(stockList2)).append("\n");
        resultSb.append("行情趋势：\n");
        resultSb.append(contentGroup[0].replaceAll("<p>", "")).append("\n板块趋势：\n");
        resultSb.append(contentGroup[2].replaceAll("<p>", "")).append("\n");
        List<Map<String, Object>> topMapList = jdbcTemplate.queryForList("SELECT title FROM lian_v1_article a " +
                " LEFT JOIN lian_depth_column_recommend d ON a.id = d.article_id " +
                " WHERE a.type = 0 AND d.pid = 0 ORDER BY a.ctime DESC LIMIT 3");

        List<Map<String, Object>> normalMapList = new ArrayList<>();
        for (Document document : mongoTemplate.getCollection("Article").find(findBasic).projection(new BasicDBObject("title", 1)).sort(sortBasic).limit(3)) {
            Map<String, Object> normalMap = new HashMap<>(1);
            normalMap.put("title", document.get("title"));
            normalMapList.add(normalMap);
        }

//        List<Map<String, Object>> normalMapList = jdbcTemplate.queryForList("SELECT DISTINCT a1.title title, a1.ctime " +
//                " FROM lian_v1_article a1 LEFT JOIN lian_v1_article_stock as1 ON a1.id = as1.article_id " +
//                " WHERE a1.type = 0 AND as1.stock_code IN ('sh600740') ORDER BY a1.ctime DESC LIMIT 3");

        resultSb.append("宏观要闻：\n");
        topMapList.addAll(normalMapList);
        Set<String> checkedSet = new HashSet<>();
        int count = 0;
        for (Map<String, Object> tempMap : topMapList) {
            String tempTitle = tempMap.get("title").toString();
            if (!checkedSet.contains(tempTitle)) {
                count++;
                checkedSet.add(tempTitle);
                resultSb.append(count).append("、").append(tempTitle).append("\n");
            }
        }

        List<Map<String, Object>> fastMapList = jdbcTemplate.queryForList("SELECT content FROM lian_v1_article " +
                " WHERE type = -1 AND recommend = 1 ORDER BY ctime DESC LIMIT 1");

        // 快讯只用财联社的
//        findBasic.append("$where", "(this.content.length > 50 && this.content.length < 150)");
//        List<Map<String, Object>> shortMapList = new ArrayList<>();
//        for (Document document : mongoTemplate.getCollection("Article").find(findBasic).projection(new BasicDBObject("title", 1)).sort(sortBasic).limit(3)) {
//            Map<String, Object> shortMap = new HashMap<>(1);
//            shortMap.put("content", document.get("title"));
//            shortMapList.add(shortMap);
//        }

        List<Map<String, Object>> shortMapList = jdbcTemplate.queryForList("SELECT DISTINCT a1.content content, a1.ctime " +
                " FROM lian_v1_article a1 LEFT JOIN lian_v1_article_stock as1 ON a1.id = as1.article_id " +
                " WHERE a1.type = -1 AND as1.stock_code IN ('sh600740') ORDER BY a1.ctime DESC LIMIT 3");
        resultSb.append("宏观快讯：\n");
        fastMapList.addAll(shortMapList);
        count = 0;
        for (int i = 0; i < fastMapList.size(); i++) {
            Map<String, Object> tempMap = fastMapList.get(i);
            String tempContent = tempMap.get("content").toString();
            if (!checkedSet.contains(tempContent)) {
                count++;
                checkedSet.add(tempContent);
                resultSb.append(count).append("、").append(tempContent).append("\n");
            }
        }

        List<Map<String, Object>> stockNameList = localJdbcTemplate.queryForList("select name from block where code in (" + sb.toString() + ")");
        isFirst = true;
        StringBuilder stockNameSb = new StringBuilder();
        for (Map<String, Object> stock : stockNameList) {
            if (isFirst) {
                isFirst = false;
                stockNameSb.append("'").append(stock.get("name")).append("'");
            } else {
                stockNameSb.append(",'").append(stock.get("name")).append("'");
            }
        }

        List<Map<String, Object>> ybList = spiderJdbcTemplate.queryForList("select * from Eastmoney_ggyb " +
                " where secuName in (" + stockNameSb.toString() + ") order by createDate desc limit 3");
        if (ybList.size() > 0) {
            resultSb.append("研报方面：\n");
            ybList.forEach(yb -> resultSb.append(yb.get("secuName")).append(" ").append(yb.get("insName")).append("最新研报称：").append(ybList.get(0).get("title")).append("\n"));
//            resultSb.append("原文链接：").append(ybList.get(0).get("url")).append("\n");
        }

        List<Map<String, Object>> queryForList = spiderJdbcTemplate.queryForList("SELECT stockcode stock_code, stockname stock_name, title, type, url FROM AGuGongGao " +
                " WHERE stockcode in ('600740', '601899') ORDER BY postDate DESC limit 3");

        if (queryForList.size() < 3) {
            List<Map<String, Object>> secondaryQueryForList = spiderJdbcTemplate.queryForList("SELECT SECURITY_CODE stock_code, SECURITY_SHORTNAME stock_name, title, " +
                    " NOTICE_TYPE type, URL url FROM Eastmoney_notice WHERE SECURITY_CODE in ('600740', '601899') ORDER BY NOTICE_DATE DESC limit 3");
            queryForList.addAll(secondaryQueryForList);
        }
        if (queryForList.size() > 0) {
            resultSb.append("重大公告事项：\n");
//        spiderJdbcTemplate.queryForMap("")
            queryForList.forEach(query -> {
                String noticeTitle = query.get("title").toString();
//                String noticeStockCode = query.get("stock_code").toString();
//                String noticeStockName = query.get("stock_name").toString();
//                String noticeType = query.get("type").toString();
//                String noticeUrl = query.get("url").toString();
//            resultSb.append(noticeStockName).append("(").append(noticeStockCode).append(")").append(noticeType).append("\n");
                if (noticeTitle.contains("\n")) {
                    resultSb.append(noticeTitle, 0, noticeTitle.indexOf("\n")).append("\n");
                } else {
                    resultSb.append(noticeTitle).append("\n");
                }
            });
//            resultSb.append("原文链接：").append(noticeUrl).append("\n");
        }

        resultSb.append("板块投资机会：\n");
//        List<Map<String, Object>> plateStockList = jdbcTemplate.queryForList("select * from lian_v1_plate_stock where rel_desc != '' " +
//                " and rel_desc is not null order by id desc limit 3");
//        if (plateStockList.size() > 0) {
//            String
//            resultSb.append(timeStart).append(" 预计 ")
//                    .append(timeAxisList.get(0).get("content")).append("，请留意相关投资机会").append("\n");
//        }

        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("select a.stock_code stockCode, b.name plateName, a.rel_desc relDesc from lian_v1_plate_stock a " +
                "left join lian_v1_plate b on a.plate_id = b.id where rel_desc != '' and rel_desc is not null order by a.id desc");
        Set<String> plateNameSet = new HashSet<>();
        while (sqlRowSet.next()) {
            String plateName = sqlRowSet.getString("plateName");
            if (!plateNameSet.contains(plateName)) {
                plateNameSet.add(plateName);
                String oldStockCode = sqlRowSet.getString("stockCode");
                int firstNumberIndex = 0;
                for (int i = 0; i < oldStockCode.length(); i++) {
                    if (Character.isDigit(oldStockCode.charAt(i))) {
                        firstNumberIndex = i;
                        break;
                    }
                }
                String stockCode = oldStockCode.substring(0, firstNumberIndex).toUpperCase() + "_" + oldStockCode.substring(firstNumberIndex);
                Map<String, Object> stockNameMap = localJdbcTemplate.queryForMap("select name from block where code = '" + stockCode + "'");
                String stockName = stockNameMap.get("name").toString();
                String relDesc = sqlRowSet.getString("relDesc");
                relDesc = relDesc.substring(0, !relDesc.contains("。") ? relDesc.length() : relDesc.indexOf("。"));
                relDesc = relDesc.substring(0, !relDesc.contains("；") ? relDesc.length() : relDesc.indexOf("；"));
                resultSb.append(plateName).append(" ").append(stockName).append("(").append(stockCode).append(")").append(" ").append(relDesc).append("\n");
            }
            if (plateNameSet.size() >= 3) {
                break;
            }
        }

        List<Map<String, Object>> timeAxisList = jdbcTemplate.queryForList("select * from lian_time_axis order by time_update desc limit 3");
        if (timeAxisList.size() > 0) {
            resultSb.append("行业大事提示：").append("\n");
            timeAxisList.forEach(timeAxis -> {
                String timeStart = LocalDateTime.ofEpochSecond(Long.valueOf(timeAxis.get("time_start").toString()), 0, ZoneOffset.ofHours(8))
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                resultSb.append(timeStart).append(" ").append(timeAxis.get("content")).append("\n");
            });
            resultSb.append("请留意相关投资机会").append("\n");
        }

        System.out.println(resultSb.toString());

    }

    private static String getCodeResult(List<String> list) {
        StringBuilder listStr = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            String s = list.get(i);
            if (i == 0) {
                listStr = new StringBuilder(s);
            } else {
                listStr.append(",").append(s);
            }
        }
        RestTemplate restTemplate = new RestTemplate();
        URI targetUrl = UriComponentsBuilder.fromUriString("http://hq.sinajs.cn/list=" + listStr).build().encode().toUri();
        String body = restTemplate.getForEntity(targetUrl, String.class).getBody();
        if (body != null) {
            String[] codeGroup = body.split("\n");
            StringBuilder returnStr = new StringBuilder();
            for (int i = 0; i < codeGroup.length; i++) {
                String result = codeGroup[i];
                int index = result.indexOf("=");
                String str = result.substring(index + 1);
                String[] arr = str.split(",");
                double nowInd = 0.0;
                double todayStart = 0.1;
                String stockName = "未知股";
                try {
//                if (source.equals("HK")) {
                    stockName = arr[0].replaceAll("\"", "");
                    nowInd = Float.valueOf(arr[4]);
                    todayStart = Float.valueOf(arr[2]);
//                } else {
//                    nowInd = Float.valueOf(arr[3]);
//                    todayStart = Float.valueOf(arr[1]);
//                }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                double rate = (nowInd - todayStart) / todayStart * 100;
                DecimalFormat df = new DecimalFormat("##0.00");
                String rateStr = df.format(rate);
                if (i == 0) {
                    returnStr = new StringBuilder(stockName + " 涨幅:" + rateStr).append("%");
                } else {
                    returnStr.append(";").append(stockName).append(" 涨幅:").append(rateStr).append("%");
                }
            }
            return returnStr.toString();
        }
        return "";
    }

}