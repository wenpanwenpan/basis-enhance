package org.common.algorithm.loadbalance.common;

import java.util.Arrays;
import java.util.List;

/**
 * 模拟多个服务器节点
 */
public class Servers {

    /**
     * 模拟配置的集群节点
     */
    public static List<String> SERVERS = Arrays.asList(
            "44.120.110.001:8080",
            "44.120.110.002:8081",
            "44.120.110.003:8082",
            "44.120.110.004:8083",
            "44.120.110.005:8084"
    );
}