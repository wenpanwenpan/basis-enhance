package org.basis.enhance.mongo.multisource.client;

import org.basis.enhance.mongo.config.properties.MongoDataSourceProperties;
import org.basis.enhance.mongo.multisource.algorithm.ConsistentHash;
import org.basis.enhance.mongo.multisource.algorithm.ShardingAlgorithm;
import org.basis.enhance.mongo.multisource.register.MongoDataSourceRegister;
import org.basis.enhance.mongo.multisource.register.ShardingAlgorithmRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

/**
 * mongo多数据源客户端
 *
 * @author Mr_wenpan@163.com 2021/12/18 21:32
 */
@Component
public class MongoMultiSourceClient {

    private MongoDataSourceProperties mongoDataSourceProperties;

    private ConsistentHash<MongoTemplate> consistentHash;

    public MongoMultiSourceClient(MongoDataSourceProperties mongoDataSourceProperties,
                                  @Autowired(required = false) @Qualifier("mongoTemplateConsistentHash") ConsistentHash<MongoTemplate> consistentHash) {
        this.mongoDataSourceProperties = mongoDataSourceProperties;
        this.consistentHash = consistentHash;
    }

    /**
     * 将value通过一致性hash算法计算获取对应的MongoTemplate
     *
     * @param value value
     * @return org.springframework.data.mongodb.core.MongoTemplate
     * @author Mr_wenpan@163.com 2021/12/20 11:29 上午
     */
    public MongoTemplate getMongoTemplateByHash(Object value) {
        // 没有开启分片则返回默认数据源
        if (!mongoDataSourceProperties.getEnableSharding()) {
            return getDefaultMongoTemplate();
        }
        return consistentHash.get(value);
    }

    /**
     * 根据集合名称获取相应的分片算法，然后使用分片算法 + value 来获取分片实例
     *
     * @param collectionName mongo集合名称
     * @param value          分片键
     * @return org.springframework.data.mongodb.core.MongoTemplate
     */
    public MongoTemplate getMongoTemplate(String collectionName, Object value) {
        // 没有开启分片则返回默认数据源
        if (!mongoDataSourceProperties.getEnableSharding() || !isSharding(collectionName)) {
            return getDefaultMongoTemplate();
        }
        ShardingAlgorithm algorithm = ShardingAlgorithmRegister.getShardingAlgorithm(collectionName);
        String dataSourceName = algorithm.getTargetInstanceByValue(value);
        return MongoDataSourceRegister.getMongoTemplate(dataSourceName);
    }

    /**
     * 根据数据源名称获取对应的MongoDB数据源MongoTemplate
     *
     * @param dataSourceName 数据源名称
     * @return org.springframework.data.mongodb.core.MongoTemplate
     */
    public MongoTemplate getMongoTemplate(String dataSourceName) {

        return MongoDataSourceRegister.getMongoTemplate(dataSourceName);
    }

    /**
     * 获取默认MongoTemplate
     *
     * @return org.springframework.data.mongodb.core.MongoTemplate
     */
    public MongoTemplate getDefaultMongoTemplate() {

        return MongoDataSourceRegister.getDefaultMongoTemplate();
    }

    /**
     * 自定义算法条件获取指定数据源
     *
     * @param supplier 供应型接口
     * @return org.springframework.data.mongodb.core.MongoTemplate
     */
    public MongoTemplate getMongoTemplate(Supplier<String> supplier) {

        return MongoDataSourceRegister.getMongoTemplate(supplier.get());
    }

    /**
     * 集合是否分片
     *
     * @param collectionName 集合名称
     * @return true 表示分片 false不分片
     */
    private boolean isSharding(String collectionName) {

        return ShardingAlgorithmRegister.hasShardingAlgorithm(collectionName);
    }
}
