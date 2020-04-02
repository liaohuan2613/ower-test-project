package com.lhk.mongodb;

import com.mongodb.BasicDBObject;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MongoEventCountApplication {
    public static void main(String[] args) {
        MongoTemplate testMongoTemplate = MongoTemplateApplication.getMongoTemplate();
        Map<String, Integer> countMap = new HashMap<>();
        Map<String, Integer> monthEventCountMap = new HashMap<>();

        LocalDate startTime = LocalDate.parse("2019-01-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        for (int i = 0; i < 365; i++) {
            String monthValue = startTime.getMonthValue() + "";
            List<BasicDBObject> pipeList = new ArrayList<>();
            BasicDBObject pipe = new BasicDBObject("date", startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            pipe.append("events.companyCode", new BasicDBObject("$exists", true));
            pipeList.add(new BasicDBObject("$match", pipe));

            pipeList.add(new BasicDBObject("$project", new BasicDBObject("events", 1)));

            pipeList.add(new BasicDBObject("$unwind", "$events"));

            BasicDBObject eventPipe = new BasicDBObject("name", "$events.name");
            eventPipe.append("type", "$events.type");
            BasicDBObject secPipe = new BasicDBObject("_id", eventPipe);
            secPipe.append("num_tutorial", new BasicDBObject("$sum", 1));
            pipeList.add(new BasicDBObject("$group", secPipe));

            for (Document next : testMongoTemplate.getCollection("Article").aggregate(pipeList)) {
                Document id = next.get("_id", Document.class);
                String nameType = id.getString("type") + "_" + id.getString("name");
                countMap.put(nameType, countMap.getOrDefault(nameType, 0) + next.getInteger("num_tutorial"));
                monthEventCountMap.put(monthValue, monthEventCountMap.getOrDefault(monthValue, 0) + next.getInteger("num_tutorial"));
            }
            startTime = startTime.plusDays(1);
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream("D:\\data-source\\客户数据\\深擎\\event.txt");
            for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
                String key = entry.getKey();
                Integer value = entry.getValue();
                try {
                    fos.write((key + "\t" + value).getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            monthEventCountMap.forEach((key, value) -> System.out.println(key + "_" + value));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
