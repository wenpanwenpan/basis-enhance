package org.basis.enhance.mongo.multisource.algorithm.impl;

import org.basis.enhance.mongo.infra.function.Hash32;
import org.basis.enhance.mongo.multisource.algorithm.ConsistentHash;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Collection;

/**
 * mongo多数据源一致性hash实现
 *
 * @author Mr_wenpan@163.com 2021/12/20 11:09
 */
public class MongoDataSourceConsistentHashImpl extends ConsistentHash<MongoTemplate> {

    public MongoDataSourceConsistentHashImpl(int numberOfReplicas, Collection<MongoTemplate> nodes) {
        super(numberOfReplicas, nodes);
    }

    public MongoDataSourceConsistentHashImpl(Hash32<Object> hashFunc, int numberOfReplicas, Collection<MongoTemplate> nodes) {
        super(hashFunc, numberOfReplicas, nodes);
    }
}
