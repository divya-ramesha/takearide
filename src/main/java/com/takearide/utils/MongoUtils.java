package com.takearide.utils;

import com.google.common.collect.Lists;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import java.net.UnknownHostException;
import java.util.List;

/**
 * Created by divya-r on 17/12/16.
 */
public class MongoUtils {
    public static MongoDbFactory createDatasource(String url, String db) throws UnknownHostException {
        return new SimpleMongoDbFactory(mongoClient(url), db);
    }

    public static MongoClient mongoClient(String url) throws UnknownHostException {
        List<ServerAddress> sa = Lists.newArrayList();
        String[] mongoUrls = url.split(",");
        for (String mongoUrl : mongoUrls) {
            String[] mongoHostAndPorts = mongoUrl.split(":");
            sa.add(new ServerAddress(mongoHostAndPorts[0], Integer.parseInt(mongoHostAndPorts[1])));
        }
        return new MongoClient(sa);
    }

}
