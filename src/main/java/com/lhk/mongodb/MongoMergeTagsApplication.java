package com.lhk.mongodb;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.ArrayList;
import java.util.List;

import static com.lhk.common.CommonFunctions.mongoTemplateThreadLocal;

public class MongoMergeTagsApplication {
    public static void main(String[] args) {
        findTags(0, 10);
    }

    private static void findTags(int page, int size) {
        MongoTemplate mongoTemplate = mongoTemplateThreadLocal.get();
        if (mongoTemplate == null) {
            mongoTemplate = MongoTemplateApplication.getMongoTemplate();
            mongoTemplateThreadLocal.set(mongoTemplate);
        }
        MongoCollection<Document> article = mongoTemplate.getCollection("Article_201905");
        Document queryDoc = new Document("stockWeights", new Document("$gt", 80));
        MongoCursor<Document> iterator = article.find(queryDoc).skip(page * size).limit(size).iterator();
        while (iterator.hasNext()) {
            Document document = iterator.next();
            ArrayList<Object> stockCodes = document.get("stockCodes", new ArrayList<>());
            ArrayList<Object> stockNames = document.get("stockNames", new ArrayList<>());
            ArrayList<Object> stockWeights = document.get("stockWeights", new ArrayList<>());
            List<Document> tagList = new ArrayList<>();
            for (int i = 0; i < stockCodes.size(); i++) {
                Document tempDoc = new Document();
                tempDoc.put("tagCode", stockCodes.get(i));
                tempDoc.put("tagName", stockNames.get(i));
                tempDoc.put("weight", stockWeights.get(i));
                tagList.add(tempDoc);
            }
            Document firstDoc = new Document();
            firstDoc.put("title", document.getString("title"));
            firstDoc.put("content", document.getString("content"));
            firstDoc.put("date", document.getString("date"));
            firstDoc.put("time", document.getString("time"));
            firstDoc.put("source", document.getString("source"));
            firstDoc.put("nature", document.getString("nature"));
            firstDoc.put("summary", document.getString("summary"));
            firstDoc.put("autoTags", tagList);
            mongoTemplate.getCollection("Article_201905_result").insertOne(firstDoc);
        }
    }

}
