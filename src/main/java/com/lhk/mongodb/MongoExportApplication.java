package com.lhk.mongodb;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.opencsv.CSVWriter;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MongoExportApplication {
    public static void main(String[] args) throws IOException {
        int count = 0;
        MongoTemplate mongoTemplate = MongoTemplateApplication.getTestMongoTemplate();
//        BasicDBObject basicDBObject = new BasicDBObject("stockNames", new BasicDBObject("$in", Arrays.asList("贵州茅台酒股份有限公司",
//                "新城控股集团股份有限公司", "视觉(中国)文化发展股份有限公司", "暴风集团股份有限公司",
//                "辅仁药业集团有限公司", "方大特钢科技股份有限公司"))).append("date", "2019-01-01");

        MongoCursor<Document> article = mongoTemplate.getCollection("Article")
                .find(new BasicDBObject("supplier", "财联社").append("date", new BasicDBObject("$lt", "2020-04-01")))
                .projection(new BasicDBObject("createTs", 1)
                        .append("newsId", 1)
                        .append("title", 1)
                        .append("content", 1)
                        .append("autoTags", 1)
                        .append("showTags", 1))
                .sort(new BasicDBObject("date", -1))
                .batchSize(1000)
                .limit(25000).iterator();
        Gson gson = new Gson();
        CSVWriter writer = new CSVWriter(new FileWriter("D:/yourfile_cls.csv"), ',');
        String[] titleEntries = new String[6];
        titleEntries[0] = "newsId";
        titleEntries[1] = "title";
        titleEntries[2] = "content";
        titleEntries[3] = "createTs";
        titleEntries[4] = "autoTags";
        titleEntries[5] = "showTags";
        writer.writeNext(titleEntries);
        while (article.hasNext()) {
            Document next = article.next();
            String[] entries = new String[6];
            entries[0] = next.getString("newsId");
            entries[1] = next.getString("title");
            entries[2] = next.getString("content");
            entries[3] = next.getString("createTs");
            entries[4] = gson.toJson(next.get("autoTags"));
            entries[5] = gson.toJson(next.get("showTags"));
            writer.writeNext(entries);
            System.out.println(count++);
        }
        writer.flush();
    }
}
