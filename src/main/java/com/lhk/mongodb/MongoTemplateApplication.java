package com.lhk.mongodb;

import column.clf.ColumnClfApi;
import com.lhk.util.HTMLFormatUtils;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCursor;
import market.text.clf.MarketTextClfApi;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import preprocess.clf.column.ColumnPreprocess;
import preprocess.clf.market.MarketPreprocess;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.lhk.common.CommonFunctions.*;

/**
 * @author LHK
 */
public class MongoTemplateApplication {

    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 2, 0,
            TimeUnit.MINUTES, new LinkedBlockingQueue<>());

    public static void main(String[] args) {
        parseMarketTypeAndColumn();
    }

    private static void parseMarketTypeAndColumn() {
        for (int i = 0; i < 20; i++) {
            basicOperation(i, 1000);
        }
        executor.shutdown();
    }

    private static void basicOperation(int page, int size) {
        executor.execute(() -> {
            try {
                MarketTextClfApi marketTextClfApi = marketTextClfApiThreadLocal.get();
                if (marketTextClfApi == null) {
                    marketTextClfApi = new MarketTextClfApi("/root/evaluation/models/market/");
//                    marketTextClfApi = new MarketTextClfApi("c:/tmp/market/");
                    marketTextClfApiThreadLocal.set(marketTextClfApi);
                }
                ColumnClfApi columnClfApi = columnClfApiThreadLocal.get();
                if (columnClfApi == null) {
                    columnClfApi = new ColumnClfApi("/root/evaluation/models/column/");
//                    columnClfApi = new ColumnClfApi("c:/tmp/column/");
                    columnClfApiThreadLocal.set(columnClfApi);
                }

                MongoTemplate mongoTemplate = mongoTemplateThreadLocal.get();
                if (mongoTemplate == null) {
                    mongoTemplate = getMongoTemplate();
                    mongoTemplateThreadLocal.set(mongoTemplate);
                }

                MongoCursor<Document> articleIterator = mongoTemplate.getCollection("all_eastmoney_tags_v4").find()
                        .skip(page * size).limit(size).iterator();
                List<String> marketTextList = new ArrayList<>();
                List<String> columnTextList = new ArrayList<>();
                List<Document> resultMapList = new ArrayList<>();
                while (articleIterator.hasNext()) {
                    Document tempMap = new Document();
                    Document document = articleIterator.next();
                    String title = document.getString("title");
                    String content = HTMLFormatUtils.filterHtml(document.getString("content"));
//                    String originCategory = document.getString("category");
//                    String date = document.getString("date");
//                    String time = document.getString("time");
//                    Boolean isDup = document.getBoolean("isDup");
//                    String createTs = document.getString("createTs");

                    tempMap.put("title", title);
                    tempMap.put("content", content);
//                    tempMap.put("originCategory", originCategory);
//                    tempMap.put("originSubCategory", originSubCategory);
//                    tempMap.put("isDup", isDup);
                    tempMap.put("publishTs", document.getString("postDate"));
                    tempMap.put("createTs", document.getString("createDate"));
                    String segText = segWords(title + " #SQCONTENT# " + content, true);
//                    String segText = title + " #SQCONTENT# " + content;
                    if (segText.endsWith("SQCONTENT")) {
                        segText += "#";
                    }

                    String marketText = MarketPreprocess.preprocessText(segText);
                    String columnText = ColumnPreprocess.preprocessText(segText);

//                    marketTextList.add(segWords(marketText, true));
//                    columnTextList.add(segWords(columnText, true));
                    marketTextList.add(marketText);
                    columnTextList.add(columnText);
                    resultMapList.add(tempMap);
                }
                List<List<Map.Entry<String, Double>>> marketEntryList = formatPredictList(marketTextClfApi.predict(marketTextList));
                List<List<Map.Entry<String, Double>>> columnEntryList = formatPredictList(columnClfApi.predict(columnTextList));

                mergeList(resultMapList, marketTextList, marketEntryList, columnTextList, columnEntryList);

                mongoTemplate.getCollection("ArticleTestOtherResultV2").insertMany(resultMapList);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    public static MongoTemplate getMongoTemplate() {
        return new MongoTemplate(new SimpleMongoDbFactory(new MongoClient("192.168.11.89", 27017),
                "GTJA"));
    }
}
