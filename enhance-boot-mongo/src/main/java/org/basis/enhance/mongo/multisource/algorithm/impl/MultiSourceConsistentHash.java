package org.basis.enhance.mongo.multisource.algorithm.impl;

import org.basis.enhance.mongo.infra.function.Hash32;
import org.basis.enhance.mongo.multisource.algorithm.ConsistentHash;

import java.util.Collection;

/**
 * 多数据源一致性hash算法
 *
 * @author Mr_wenpan@163.com 2021/12/20 15:45
 */
public class MultiSourceConsistentHash extends ConsistentHash<String> {

    public MultiSourceConsistentHash(int numberOfReplicas, Collection<String> nodes) {
        super(numberOfReplicas, nodes);
    }

    public MultiSourceConsistentHash(Hash32<Object> hashFunc, int numberOfReplicas, Collection<String> nodes) {
        super(hashFunc, numberOfReplicas, nodes);
    }

}
