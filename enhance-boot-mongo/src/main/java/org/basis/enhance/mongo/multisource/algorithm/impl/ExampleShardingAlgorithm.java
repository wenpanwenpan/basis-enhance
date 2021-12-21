package org.basis.enhance.mongo.multisource.algorithm.impl;

import org.basis.enhance.mongo.multisource.algorithm.ShardingAlgorithm;
import org.springframework.stereotype.Component;

/**
 * 默认分片算法
 *
 * @author Mr_wenpan@163.com 2021/12/19 19:49
 */
@Component
public class ExampleShardingAlgorithm implements ShardingAlgorithm {

    @Override
    public String getTargetInstanceByValue(Object value) {
        return "datasource1MongoTemplate";
    }

    @Override
    public String getTargetInstanceByCollection(String collectionName) {
        return null;
    }

    @Override
    public String collectionName() {
        return "enhance_delivery_confirm";
    }
}
