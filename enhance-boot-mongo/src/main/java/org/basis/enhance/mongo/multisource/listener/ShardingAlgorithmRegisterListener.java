package org.basis.enhance.mongo.multisource.listener;

import org.apache.commons.collections4.MapUtils;
import org.basis.enhance.mongo.multisource.algorithm.ShardingAlgorithm;
import org.basis.enhance.mongo.multisource.register.ShardingAlgorithmRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.lang.NonNull;

import java.util.Map;

/**
 * 分片算法注册监听器
 *
 * @author Mr_wenpan@163.com 2021/12/19 11:28
 */
public class ShardingAlgorithmRegisterListener implements ApplicationListener<ContextRefreshedEvent> {

    private ApplicationContext applicationContext;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public ShardingAlgorithmRegisterListener(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(@NonNull ContextRefreshedEvent event) {
        Map<String, ShardingAlgorithm> beansOfType = applicationContext.getBeansOfType(ShardingAlgorithm.class);
        if (MapUtils.isEmpty(beansOfType)) {
            logger.warn("Note that the sharding algorithm is empty.");
            return;
        }
        // 注册mongo分片算法
        for (ShardingAlgorithm shardingAlgorithm : beansOfType.values()) {
            ShardingAlgorithmRegister.register(shardingAlgorithm);
        }
    }
}
