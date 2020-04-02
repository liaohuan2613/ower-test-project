package com.lhk.mongodb;

import com.mongodb.BasicDBObject;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

public class MongoTestApplication {
    public static void main(String[] args) {
        int count = 0;
        MongoTemplate testMongoTemplate = MongoTemplateApplication.getMongoTemplate();
        BasicDBObject basicDBObject = new BasicDBObject("stockNames", "晶晨股份");
        for (int j = 1; j < 23; j++) {
            String a = "";
            String b = "";
            if (j + 1 < 10) {
                b = "0" + (j + 1);
            } else {
                b = "" + (j + 1);
            }
            if (j < 10) {
                a = "0" + j;
            } else {
                a = "" + j;
            }
            basicDBObject.append("date", new BasicDBObject("$gte", "2019-08-" + a).append("$lt", "2019-08-" + b));
            basicDBObject.append("isDup", false);
            for (Document document : testMongoTemplate.getCollection("Article").find(basicDBObject).limit(1)) {
                count++;
                Document doc = document;
                System.out.print(doc.getString("title"));
                if (doc.getString("title").contains("A股")) {
                    System.out.println("===========================" + doc.getString("newsId"));
                }
                List stockNames = doc.get("stockNames", List.class);
                List stockWeights = doc.get("stockWeights", List.class);
                for (int i = 0; i < stockNames.size(); i++) {
                    System.out.println("\t" + stockNames.get(i) + "\t" + stockWeights.get(i));
                }

                List industryNames = doc.get("industryNames", List.class);
                List industryWeights = doc.get("industryWeights", List.class);

                for (int i = 0; i < industryNames.size(); i++) {
                    System.out.println("\t" + industryNames.get(i) + "\t" + industryWeights.get(i));
                }
                List newsNames = doc.get("newsNames", List.class);
                List newsWeights = doc.get("newsWeights", List.class);
                for (int i = 0; i < newsNames.size(); i++) {
                    System.out.println("\t" + newsNames.get(i) + "\t" + newsWeights.get(i));
                }

                List conceptNames = doc.get("conceptNames", List.class);
                List conceptWeights = doc.get("conceptWeights", List.class);
                for (int i = 0; i < conceptNames.size(); i++) {
                    System.out.println("\t" + conceptNames.get(i) + "\t" + conceptWeights.get(i));
                }

                List regionNames = doc.get("regionNames", List.class);
                List regionWeights = doc.get("regionWeights", List.class);
                for (int i = 0; i < regionNames.size(); i++) {
                    System.out.println("\t" + regionNames.get(i) + "\t" + regionWeights.get(i));
                }
                System.out.println();
            }
        }
        System.out.println(count);
    }
}
