package org.common.algorithm.loadbalance;

import org.common.algorithm.loadbalance.common.Servers;

/**
 * 随机算法测试
 *
 * @author wenpan 2022/07/16 22:58
 */
public class RandomTest {
    /**
     * 随机数产生器，用于产生随机因子
     */
    public static java.util.Random random = new java.util.Random();

    public static String getServer() {
        // 从已配置的服务器列表中，随机抽取一个节点处理请求
        return Servers.SERVERS.get(random.nextInt(Servers.SERVERS.size()));
    }
}
