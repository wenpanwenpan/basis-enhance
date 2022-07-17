package org.common.algorithm.loadbalance;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询权重算法测试
 *
 * @author wenpan 2022/07/16 23:01
 */
public class RoundRobinWeightTest {

    private static AtomicInteger requestCount = new AtomicInteger(0);

    public static void main(String[] args) {
        for (int i = 1; i <= 10; i++) {
            System.out.println("第" + i + "个请求：" + getServer());
        }
    }

    /**
     * 通过加权轮询算法从服务器列表中获取一个可用的节点
     */
    public static String getServer() {
        int weightTotal = 0;
        for (Integer weight : Servers.WEIGHT_SERVERS.values()) {
            weightTotal += weight;
        }

        String targetServer = "";
        int index = requestCount.get() % weightTotal;
        requestCount.incrementAndGet();

        for (String server : Servers.WEIGHT_SERVERS.keySet()) {
            Integer weight = Servers.WEIGHT_SERVERS.get(server);
            if (weight > index) {
                targetServer = server;
                break;
            }
            index = index - weight;
        }
        return targetServer;
    }

    static class Servers {
        // 在之前是Servers类中再加入一个权重服务列表
        public static Map<String, Integer> WEIGHT_SERVERS = new LinkedHashMap<>();

        static {
            // 配置集群的所有节点信息及权重值
            WEIGHT_SERVERS.put("44.120.110.001:8080", 17);
            WEIGHT_SERVERS.put("44.120.110.002:8081", 11);
            WEIGHT_SERVERS.put("44.120.110.003:8082", 30);
        }
    }
}
