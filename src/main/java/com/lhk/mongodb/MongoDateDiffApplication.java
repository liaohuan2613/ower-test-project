package com.lhk.mongodb;

import com.google.gson.Gson;
import com.lhk.util.DateUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

public class MongoDateDiffApplication {
    public static void main(String[] args) {
        MongoTemplate mongoTemplate = MongoTemplateApplication.getMongoTemplate();
        BasicDBObject dateDbObject = new BasicDBObject("$gte", "2019-07-16");
        dateDbObject.append("$lt", "2019-08-15");
        BasicDBObject filterDbObject = new BasicDBObject("createTs", dateDbObject);
        BasicDBObject projectDbObject = new BasicDBObject("supplier", 1);
        projectDbObject.append("createTs", 1);
        projectDbObject.append("date", 1);
        projectDbObject.append("time", 1);
        MongoCursor<Document> mongoCursor = mongoTemplate.getCollection("Article").find(filterDbObject).projection(projectDbObject).iterator();
        Map<String, Map<String, Long>> keyMapMap = new HashMap<>();
        while (mongoCursor.hasNext()) {
            Document doc = mongoCursor.next();
            LocalDateTime publishTime;
            LocalDateTime enterTime;
            String supplier;
            try {
                supplier = doc.getString("supplier");

                Map<String, Long> effectiveInformation = keyMapMap.computeIfAbsent("effectiveInformation", k -> new HashMap<>(1));
                long defaultCountNum = effectiveInformation.getOrDefault(supplier, 0L);
                effectiveInformation.put(supplier, defaultCountNum + 1);

                publishTime = DateUtils.asLocalDateTime(DateUtils.parse(doc.getString("date") + " " + doc.getString("time")));
                enterTime = DateUtils.asLocalDateTime(DateUtils.parse(doc.getString("createTs")));
                long delaySecond = enterTime.toEpochSecond(ZoneOffset.UTC) - publishTime.toEpochSecond(ZoneOffset.UTC);
                Map<String, Long> delayInformation = keyMapMap.computeIfAbsent("delayInformation", k -> new HashMap<>(1));
                defaultCountNum = delayInformation.getOrDefault(supplier, 0L);
                delayInformation.put(supplier, defaultCountNum + delaySecond);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        System.out.println(new Gson().toJson(keyMapMap));
    }
}
