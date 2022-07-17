package org.common.algorithm.loadbalance;

import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 最优响应算法测试
 *
 * @author wenpan 2022/07/16 23:13
 */
public class ResponseTimeTest {

    /**
     * 创建一个定长的线程池，用于去执行ping任务
     */
    static ExecutorService pingServerPool = Executors.newFixedThreadPool(Servers.SERVERS.size());

    public static String getServer() throws InterruptedException {
        // 创建一个CompletableFuture用于拼接任务
        CompletableFuture<Object> cfAnyOf;
        // 创建一个接收结果返回的server节点对象
        Server resultServer = new Server();
        // 根据集群节点数量初始化一个异步任务数组
        CompletableFuture[] cfs = new CompletableFuture[Servers.SERVERS.size()];

        // 遍历整个服务器列表，为每个节点创建一个ping任务
        for (Server server : Servers.SERVERS) {
            // 获取当前节点在集群列表中的下标
            int index = Servers.SERVERS.indexOf(server);
            // 为每个节点创建一个ping任务，并交给pingServerPool线程池执行
            CompletableFuture<String> cf =
                    CompletableFuture.supplyAsync(server::ping, pingServerPool);
            // 将创建好的异步任务加入数组中
            cfs[index] = cf;
        }

        // 将创建好的多个Ping任务组合成一个聚合任务并执行
        cfAnyOf = CompletableFuture.anyOf(cfs);

        // 监听执行完成后的回调，谁先执行完成则返回谁
        cfAnyOf.thenAccept(resultIP -> {
            System.out.println("最先响应检测请求的节点为：" + resultIP);
            resultServer.setIp((String) resultIP);
        });
        //  阻塞主线程一段时间，防止CompletableFuture退出
        Thread.sleep(3000);

        // 返回最先响应检测请求（ping）的节点作为本次处理客户端请求的节点
        return resultServer.getIp();
    }

    public static void main(String[] args) throws InterruptedException {
        for (int i = 1; i <= 5; i++) {
            System.out.println("第" + i + "个请求：" + getServer());
        }
    }

    static class Servers {
        // 模拟的集群节点信息，活跃数最开始默认为0
        public static List<Server> SERVERS = Arrays.asList(
                new Server("44.120.110.001:8080"),
                new Server("44.120.110.002:8081"),
                new Server("44.120.110.003:8082")
        );
    }

    @Data
    static class Server {
        private String ip;

        public Server() {
        }

        public Server(String ip) {
            this.ip = ip;
        }

        public String ping() {
            // 生成一个1000~3000之间的随机数
            int random = ThreadLocalRandom.current().nextInt(1000, 2000);
            try {
                // 随机休眠一段时间，模拟不同的响应速度
                Thread.sleep(random);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 最后返回自身的IP
            return ip;
        }
    }
}
