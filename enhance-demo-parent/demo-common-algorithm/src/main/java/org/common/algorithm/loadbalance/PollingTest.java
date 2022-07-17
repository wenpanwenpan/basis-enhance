package org.common.algorithm.loadbalance;

import org.common.algorithm.loadbalance.common.Servers;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询算法测试
 *
 * @author wenpan 2022/07/16 22:55
 */
public class PollingTest {

    public static void main(String[] args) {
        // 使用for循环简单模拟10个客户端请求
        for (int i = 1; i <= 10; i++) {
            System.out.println("第" + i + "个请求：" + RoundRobin.getServer());
        }
    }

    /**
     * 轮询策略类：实现基本的轮询算法
     */
    static class RoundRobin {
        /**
         * 用于记录当前请求的序列号
         */
        private static AtomicInteger requestIndex = new AtomicInteger(0);

        /**
         * 从集群节点中选取一个节点处理请求
         */
        public static String getServer() {
            // 用请求序列号取余集群节点数量，求得本次处理请求的节点下标
            int index = requestIndex.get() % Servers.SERVERS.size();
            // 从服务器列表中获取具体的节点IP地址信息
            String server = Servers.SERVERS.get(index);
            // 自增一次请求序列号，方便下个请求计算
            requestIndex.incrementAndGet();
            // 返回获取到的服务器IP地址
            return server;
        }
    }
}



