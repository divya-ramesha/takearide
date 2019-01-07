package com.takearide.configs;

import com.mongodb.MongoClient;
import com.takearide.utils.MongoUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.net.UnknownHostException;

/**
 * Created by divya-r on 17/12/16.
 */
@Configuration
public class MainMongoConfig {

    @Value("${mongo.global.db.mongoUrl}")
    String url;

    @Value("${mongo.global.db.mongoDb}")
    String db;

    @Bean(name = "globalMongoDS")
    public MongoDbFactory mongoGlobalDataSource() throws UnknownHostException {
        return MongoUtils.createDatasource(url, db);
    }

    @Bean(name = "globalMongoClient")
    public MongoClient mongoClient() throws UnknownHostException {
        return MongoUtils.mongoClient(url);
    }

    @Bean(name = "mongoTemplate")
    public MongoTemplate mongoTemplate(@Qualifier("globalMongoDS") MongoDbFactory mongoDBFactory) {
        MongoTemplate mongoTemplate = new MongoTemplate(mongoDBFactory);
        return mongoTemplate;
    }
}