package org.common.algorithm.loadbalance;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 随机权重算法测试
 *
 * @author wenpan 2022/07/16 23:00
 */
public class RandomWeightTest {

    public static void main(String[] args) {
        // 利用for循环模拟10个客户端请求测试
        for (int i = 1; i <= 10; i++) {
            System.out.println("第" + i + "个请求：" + getServer());
        }
    }

    /**
     * 初始化随机数生产器
     */
    static java.util.Random random = new java.util.Random();

    public static String getServer() {
        // 计算总权重值
        int weightTotal = 0;
        for (Integer weight : Servers.WEIGHT_SERVERS.values()) {
            weightTotal += weight;
        }

        // 从总权重的范围内随机生成一个索引
        int index = random.nextInt(weightTotal);
        System.out.println(index);

        // 遍历整个权重集群的节点列表，选择节点处理请求
        String targetServer = "";
        for (String server : Servers.WEIGHT_SERVERS.keySet()) {
            // 获取每个节点的权重值
            Integer weight = Servers.WEIGHT_SERVERS.get(server);
            // 如果权重值大于产生的随机数，则代表此次随机分配应该落入该节点
            if (weight > index) {
                // 直接返回对应的节点去处理本次请求并终止循环
                targetServer = server;
                break;
            }
            // 如果当前节点的权重值小于随机索引，则用随机索引减去当前节点的权重值，
            // 继续循环权重列表，与其他的权重值进行对比，
            // 最终该请求总会落入到某个IP的权重值范围内
            index = index - weight;
        }
        // 返回选中的目标节点
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
