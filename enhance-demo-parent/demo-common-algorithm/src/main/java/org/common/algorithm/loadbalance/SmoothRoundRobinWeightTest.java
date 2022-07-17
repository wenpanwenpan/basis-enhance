package org.common.algorithm.loadbalance;

import lombok.Data;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 平滑加权轮询测试
 *
 * @author wenpan 2022/07/16 23:04
 */
public class SmoothRoundRobinWeightTest {

    /**
     * 初始化存储每个节点的权重容器
     */
    private static Map<String, Weight> weightMap = new HashMap<>();

    /**
     * 计算总权重值，只需要计算一次，因此放在静态代码块中执行
     */
    private static int weightTotal = 0;

    static {
        sumWeightTotal();
    }

    /**
     * 求和总权重值，后续动态伸缩节点时，再次调用该方法即可。
     */
    public static void sumWeightTotal() {
        for (Integer weight : Servers.WEIGHT_SERVERS.values()) {
            weightTotal += weight;
        }
    }

    /**
     * 获取处理本次请求的具体服务器IP
     */
    public static String getServer() {
        // 判断权重容器中是否有节点信息
        if (weightMap.isEmpty()) {
            // 如果没有则将配置的权重服务器列表挨个载入容器
            Servers.WEIGHT_SERVERS.forEach((servers, weight) -> {
                // 初始化时，每个节点的动态权重值都为0
                weightMap.put(servers, new Weight(servers, weight, 0));
            });
        }

        // 每次请求时，更改动态权重值*
        for (Weight weight : weightMap.values()) {
            weight.setCurrentWeight(weight.getCurrentWeight()
                    + weight.getWeight());
        }

        // 判断权重容器中最大的动态权重值
        Weight maxCurrentWeight = null;
        for (Weight weight : weightMap.values()) {
            if (maxCurrentWeight == null || weight.getCurrentWeight()
                    > maxCurrentWeight.getCurrentWeight()) {
                maxCurrentWeight = weight;
            }
        }

        // 最后用最大的动态权重值减去所有节点的总权重值
        assert maxCurrentWeight != null;
        maxCurrentWeight.setCurrentWeight(maxCurrentWeight.getCurrentWeight() - weightTotal);

        // 返回最大的动态权重值对应的节点IP
        return maxCurrentWeight.getServer();
    }

    public static void main(String[] args) {
        // 使用for循环模拟6次请求
        for (int i = 1; i <= 6; i++) {
            System.out.println("第" + i + "个请求：" + getServer());
        }
    }

    // ================================================================================

    /**
     * 权重服务器的配置类
     */
    static class Servers {
        public static Map<String, Integer> WEIGHT_SERVERS = new LinkedHashMap<>();

        static {
            // 权重值设置的略微小一点，方便后续理解算法
            WEIGHT_SERVERS.put("44.120.110.001:8080", 3);
            WEIGHT_SERVERS.put("44.120.110.002:8081", 2);
            WEIGHT_SERVERS.put("44.120.110.003:8082", 1);
        }
    }

    /**
     * 权重类
     */
    @Data
    static class Weight {
        // 节点信息
        private String server;
        // 节点权重值
        private Integer weight;
        // 动态权重值
        private Integer currentWeight;

        // 构造方法
        public Weight() {
        }

        public Weight(String server, Integer weight, Integer currentWeight) {
            this.server = server;
            this.weight = weight;
            this.currentWeight = currentWeight;
        }
    }
}
