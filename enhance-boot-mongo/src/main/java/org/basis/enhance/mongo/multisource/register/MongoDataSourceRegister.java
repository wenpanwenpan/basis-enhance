package org.basis.enhance.mongo.multisource.register;

import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * mongo多数据源注册
 *
 * @author Mr_wenpan@163.com 2021/12/16 22:12
 */
public class MongoDataSourceRegister {

    /**
     * 多数据源MongoTemplate注册（不包括默认数据源）
     */
    private final static Map<String, MongoTemplate> MONGO_TEMPLATE_REGISTER = new ConcurrentHashMap<>();

    public MongoDataSourceRegister() {

    }

    /**
     * 注册MongoTemplate
     */
    public static void redisterMongoTemplate(String name, MongoTemplate mongoTemplate) {
        if (mongoTemplate == null || name == null) {
            return;
        }
        MONGO_TEMPLATE_REGISTER.put(name, mongoTemplate);
    }

    /**
     * 获取指定数据源的MongoTemplate
     */
    public static MongoTemplate getMongoTemplate(String name) {
        return MONGO_TEMPLATE_REGISTER.get(name);
    }

    public static Map<String, MongoTemplate> getMongoTemplateRegister() {
        return MONGO_TEMPLATE_REGISTER;
    }
}
