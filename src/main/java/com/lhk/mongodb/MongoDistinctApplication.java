package com.lhk.mongodb;

import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.HashSet;
import java.util.Set;

public class MongoDistinctApplication {
    public static void main(String[] args) {
        distinct();
    }

    private static void distinct() {
        MongoTemplate mongoTemplate = MongoTemplateApplication.getMongoTemplate();
        Set<String> titleSet = new HashSet<>();
        for (Document document : mongoTemplate.getCollection("ArticleClsXgbResultV4").find()) {
            String title = document.getString("title");
            String titleCls = document.getString("titleCls") == null ? "" : document.getString("titleCls");
            if (!titleSet.contains(title + titleCls)) {
                titleSet.add(title + titleCls);
                mongoTemplate.getCollection("ArticleClsXgbDistinctResultV4").insertOne(document);
            }
        }
    }
}
