package com.lhk.mongodb;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static com.lhk.common.CommonFunctions.postResult;
import static com.lhk.common.CommonFunctions.postResultList;

public class MongoFindSameApplication {

    private static RestTemplate restTemplate = new RestTemplate();

    public static void main(String[] args) {
        find();
    }

    private static void find() {
        MongoTemplate mongoTemplate = MongoTemplateApplication.getMongoTemplate();
        Map<String, Document> clsMap = new HashMap<>(1000);
        for (Document document : mongoTemplate.getCollection("ArticleGtjaCls").find()) {
            Map<String, Object> requestMap = new HashMap<>(5);
            requestMap.put("id", document.getString("newsId"));
            requestMap.put("title", document.getString("title"));
            requestMap.put("content", document.getString("content"));
            requestMap.put("source", document.getString("source"));
            document.remove("_id");
            clsMap.put(document.getString("newsId"), document);
            requestMap.put("database", 13);
            postResult("http://127.0.0.1:5002/api/deduplication", requestMap, restTemplate);
        }


        List<Document> documentList = new ArrayList<>();

        int countNum = 0;
        for (Document document : mongoTemplate.getCollection("ArticleXgb").find()) {
            Map<String, Object> requestMap = new HashMap<>(5);
            requestMap.put("id", document.getString("newsId"));
            requestMap.put("title", document.getString("title"));
            requestMap.put("content", document.getString("content"));
            requestMap.put("source", document.getString("source"));
            document.remove("_id");
            Set<String> keySet = document.keySet();
            requestMap.put("database", 13);
            Map<String, Object> postMap = postResult("http://127.0.0.1:5002/api/deduplication", requestMap, restTemplate);
            boolean flag = false;
            if ("true".equals(postMap.get("isDup").toString())) {
                List<Map<String, Object>> dupIds = (List<Map<String, Object>>) postMap.get("dupIds");
                for (Map<String, Object> dupId : dupIds) {
                    if (dupId != null) {
                        String id = (String) dupId.get("id");
                        Document clsDoc = clsMap.get(id);
                        Document newClsDoc = new Document();
                        if (clsDoc != null) {
                            flag = true;
                            for (String key : keySet) {
                                newClsDoc.put(key + "Cls", clsDoc.get(key));
                            }
                            newClsDoc.putAll(document);
                            newClsDoc.put("_id", countNum++);
                            documentList.add(newClsDoc);
                        }
                    }
                }
            }
            if (!flag) {
                document.put("_id", countNum++);
                documentList.add(document);
            }
        }
        mongoTemplate.getCollection("ArticleXgbClsResultV3").insertMany(documentList);
    }
}
