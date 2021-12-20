package org.basis.enhance.mongo.multisource.register;

import org.apache.commons.lang3.StringUtils;
import org.basis.enhance.mongo.exception.ShardingAlgorithmExistedException;
import org.basis.enhance.mongo.multisource.algorithm.ShardingAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * 分片算法注册器
 *
 * @author Mr_wenpan@163.com 2021/12/19 19:38
 */
public final class ShardingAlgorithmRegister {

    private final static Logger logger = LoggerFactory.getLogger(ShardingAlgorithmRegister.class);

    /**
     * 分片算法容器
     */
    private final static Map<String, ShardingAlgorithm> SHARDING_ALGORITHM_MAP = new ConcurrentHashMap<>();

    private ShardingAlgorithmRegister() {

    }

    /**
     * 注册分片算法，容器启动时算法注入
     */
    public static void register(ShardingAlgorithm algorithm) {
        if (Objects.isNull(algorithm)) {
            logger.warn("Note that the sharding algorithm is empty.");
            return;
        }
        // 禁止重复注册(一个collection注册多个分片算法)
        String collectionName = algorithm.collectionName();
        ShardingAlgorithm shardingAlgorithm = SHARDING_ALGORITHM_MAP.get(collectionName);
        if (Objects.nonNull(shardingAlgorithm)) {
            throw new ShardingAlgorithmExistedException("The collection repeatedly registers " +
                    "the sharding algorithm，collection is : " + collectionName);
        }
        SHARDING_ALGORITHM_MAP.put(collectionName, algorithm);
    }

    /**
     * 注册分片算法，用于使用方自定义分片算法注入
     *
     * @param collectionName 集合名称
     * @param algorithm      分片算法
     * @return 算法是否注册成功
     * @author Mr_wenpan@163.com 2021/12/19 11:19 下午
     */
    public static boolean register(String collectionName, ShardingAlgorithm algorithm) {

        if (StringUtils.isBlank(collectionName) && StringUtils.isBlank(algorithm.collectionName())) {
            logger.warn("Note that the sharding algorithm is empty.");
            return Boolean.FALSE;
        }

        // 优先注册指定的 collectionName
        if (StringUtils.isNotBlank(collectionName)) {
            if (Objects.nonNull(SHARDING_ALGORITHM_MAP.get(collectionName))) {
                logger.warn("The collection repeatedly registers the sharding algorithm，collection is {}", collectionName);
                return Boolean.FALSE;
            }
            SHARDING_ALGORITHM_MAP.put(collectionName, algorithm);
        } else {
            if (Objects.nonNull(SHARDING_ALGORITHM_MAP.get(algorithm.collectionName()))) {
                logger.warn("The collection repeatedly registers the sharding algorithm，collection is {}", collectionName);
                return Boolean.FALSE;
            }
            SHARDING_ALGORITHM_MAP.put(algorithm.collectionName(), algorithm);
        }
        return Boolean.TRUE;
    }

    /**
     * 注册分片算法，用于使用方自定义分片算法注入
     */
    public static void register(String collectionName, Supplier<ShardingAlgorithm> supplier) {
        register(collectionName, supplier.get());
    }

    /**
     * 通过集合名称获取对应的分片算法
     */
    public static ShardingAlgorithm getShardingAlgorithm(String collectionName) {
        return SHARDING_ALGORITHM_MAP.get(collectionName);
    }

    public static Map<String, ShardingAlgorithm> getShardingAlgorithmMap() {
        return SHARDING_ALGORITHM_MAP;
    }

    /**
     * 某个集合是否有分片
     *
     * @param collectionName 集合名称
     * @return java.lang.Boolean
     * @author Mr_wenpan@163.com 2021/12/19 9:58 下午
     */
    public static Boolean hasShardingAlgorithm(String collectionName) {
        return Objects.nonNull(SHARDING_ALGORITHM_MAP.get(collectionName));
    }

}
