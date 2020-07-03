package com.lhk.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.internal.connection.ServerAddressHelper;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import java.util.Collections;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author LHK
 */
public class MongoTemplateApplication {

    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 2, 0,
            TimeUnit.MINUTES, new LinkedBlockingQueue<>());

    public static MongoTemplate getMongoTemplate() {
        MongoCredential mongoCredential = MongoCredential.createCredential("root", "NET", "1$uYtHf7equd1$#J".toCharArray());
        return new MongoTemplate(new SimpleMongoDbFactory(new MongoClient(ServerAddressHelper
                .createServerAddress("47.96.26.118", 27017), Collections.singletonList(mongoCredential)), "NET"));
    }

    public static MongoTemplate getTestMongoTemplate() {
        MongoCredential mongoCredential = MongoCredential.createCredential("root", "TEBON", "#FGJoW^A3u*SSTbP".toCharArray());
        return new MongoTemplate(new SimpleMongoDbFactory(new MongoClient(ServerAddressHelper
                .createServerAddress("203.156.205.101", 10917), Collections.singletonList(mongoCredential)), "TEBON"));
    }
}
