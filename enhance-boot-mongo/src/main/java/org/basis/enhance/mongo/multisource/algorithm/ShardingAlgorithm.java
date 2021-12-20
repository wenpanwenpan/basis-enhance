package org.basis.enhance.mongo.multisource.algorithm;

/**
 * 分片算法
 *
 * @author Mr_wenpan@163.com 2021/12/19 10:56 上午
 */
public interface ShardingAlgorithm {

    /**
     * 根据value获取目标实例
     *
     * @param value 分片数据
     * @return 实例名称
     */
    String getTargetInstanceByValue(Object value);

    /**
     * 通过集合名称获取对应的MongoDB数据源名称
     *
     * @param collectionName 集合名称
     * @return 数据源名称
     * @author Mr_wenpan@163.com 2021/12/19 10:05 下午
     */
    String getTargetInstanceByCollection(String collectionName);

    /**
     * 返回分片的集合名称
     *
     * @return MongoDB集合名称
     */
    String collectionName();
}
