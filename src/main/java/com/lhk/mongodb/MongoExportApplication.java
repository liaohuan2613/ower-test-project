package com.lhk.mongodb;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Arrays;
import java.util.List;

public class MongoExportApplication {
    public static void main(String[] args) {
        int count = 0;
        MongoTemplate mongoTemplate = MongoTemplateApplication.getMongoTemplate();
        BasicDBObject basicDBObject = new BasicDBObject("stockNames", new BasicDBObject("$in", Arrays.asList("贵州茅台酒股份有限公司",
                "新城控股集团股份有限公司", "视觉(中国)文化发展股份有限公司", "暴风集团股份有限公司",
                "辅仁药业集团有限公司", "方大特钢科技股份有限公司"))).append("date", "2019-01-01");

        System.out.println(count);
    }
}
