package org.basis.enhance.mongo.multisource.register;

import org.basis.enhance.mongo.infra.constant.EnhanceMongoConstant;
import org.basis.enhance.mongo.multisource.algorithm.ShardingAlgorithm;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * mongo多数据源注册
 *
 * @author Mr_wenpan@163.com 2021/12/16 22:12
 */
public final class MongoDataSourceRegister {

    /**
     * 多数据源MongoTemplate注册（不包括默认数据源）
     */
    private final static Map<String, MongoTemplate> MONGO_TEMPLATE_REGISTER = new ConcurrentHashMap<>();

    private MongoDataSourceRegister() {

    }

    /**
     * 注册MongoTemplate
     */
    public static void registerMongoTemplate(String name, MongoTemplate mongoTemplate) {
        if (mongoTemplate == null || name == null) {
            return;
        }
        MONGO_TEMPLATE_REGISTER.put(name, mongoTemplate);
    }

    /**
     * 获取指定数据源的MongoTemplate
     */
    public static MongoTemplate getMongoTemplate(String dataSourceName) {
        return MONGO_TEMPLATE_REGISTER.get(dataSourceName);
    }

    /**
     * 获取指定数据源的MongoTemplate
     */
    public static MongoTemplate getDefaultMongoTemplate() {
        return MONGO_TEMPLATE_REGISTER.get(EnhanceMongoConstant.MultiSource.DEFAULT_SOURCE_TEMPLATE);
    }

    /**
     * 根据写入值 + 指定的算法 获取指定数据源的MongoTemplate
     */
    public static MongoTemplate getMongoTemplate(String value, ShardingAlgorithm algorithm) {
        String dataSourceName = algorithm.getTargetInstanceByValue(value);
        return MONGO_TEMPLATE_REGISTER.get(dataSourceName);
    }

    /**
     * 根据集合名称 + 分片key获取指定数据源的MongoTemplate
     *
     * @param collectionName 集合名称
     * @param value          分片键
     * @return org.springframework.data.mongodb.core.MongoTemplate
     * @author Mr_wenpan@163.com 2021/12/19 9:45 下午
     */
    public static MongoTemplate getMongoTemplate(String collectionName, Object value) {
        ShardingAlgorithm shardingAlgorithm = ShardingAlgorithmRegister.getShardingAlgorithm(collectionName);
        String dataSourceName = shardingAlgorithm.getTargetInstanceByValue(value);
        return MONGO_TEMPLATE_REGISTER.get(dataSourceName);
    }

    public static Map<String, MongoTemplate> getMongoTemplateRegister() {
        return MONGO_TEMPLATE_REGISTER;
    }

}
