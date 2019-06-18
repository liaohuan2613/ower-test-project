package com.lhk.mongodb;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.lhk.common.CommonFunctions.mongoTemplateThreadLocal;

public class MongoMergeTagsApplication {

    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 4, 0,
            TimeUnit.MINUTES, new LinkedBlockingQueue<>());

    private static String[] tagCategories = new String[]{"stock", "industry", "concept", "news", "region"};

    public static void main(String[] args) {
        for (int i = 0; i < 181; i++) {
            findTags(i, 1000);
        }
    }

    private static void findTags(int page, int size) {
        executor.execute(() -> {
            MongoTemplate mongoTemplate = mongoTemplateThreadLocal.get();
            if (mongoTemplate == null) {
                mongoTemplate = MongoTemplateApplication.getMongoTemplate();
                mongoTemplateThreadLocal.set(mongoTemplate);
            }
            MongoCollection<Document> article = mongoTemplate.getCollection("Article_201905");
            Document queryDoc = new Document();
            List<Document> insertList = new ArrayList<>();
            for (Document document : article.find(queryDoc).skip(page * size).limit(size)) {
                List<Document> tagList = new ArrayList<>();
                for (String tagCategory : tagCategories) {
                    ArrayList<Object> codes = document.get(tagCategory + "Codes", new ArrayList<>());
                    ArrayList<Object> names = document.get(tagCategory + "Names", new ArrayList<>());
                    ArrayList<Object> weights = document.get(tagCategory + "Weights", new ArrayList<>());
                    ArrayList<Object> sources = document.get(tagCategory + "Sources", new ArrayList<>());
                    for (int i = 0; i < codes.size(); i++) {
                        Document tempDoc = new Document();
                        tempDoc.put("tagCode", codes.get(i));
                        if (!names.isEmpty()) {
                            tempDoc.put("tagName", names.get(i));
                        }
                        if (!weights.isEmpty()) {
                            tempDoc.put("weight", weights.get(i));
                        }
                        if (!sources.isEmpty()) {
                            tempDoc.put("source", sources.get(i));
                        }
                        tempDoc.put("category", tagCategory.toUpperCase());
                        tagList.add(tempDoc);
                    }
                }

                Document firstDoc = new Document();
                firstDoc.put("newsId", document.getString("newsId"));
                firstDoc.put("title", document.getString("title"));
                firstDoc.put("content", document.getString("content"));
                firstDoc.put("date", document.getString("date"));
                firstDoc.put("time", document.getString("time"));
                firstDoc.put("isDup", document.getBoolean("isDup"));
                firstDoc.put("supplier", document.getString("supplier"));
                firstDoc.put("source", document.getString("source"));
                firstDoc.put("nature", document.getString("nature"));
                firstDoc.put("summary", document.getString("summary"));
                firstDoc.put("autoTags", tagList);
                firstDoc.put("showTags", document.get("showTags"));
                insertList.add(firstDoc);
            }
            mongoTemplate.getCollection("Article_201905_result").insertMany(insertList);
        });
    }

}
