package org.basis.enhance.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * redis多数据源客户端配置properties
 *
 * @author Mr_wenpan@163.com 2021/09/04 18:38
 */
@Deprecated
@ConfigurationProperties(prefix = "stone.redis.sharding")
public class RedisMultiSourceProperties {

    /**
     * 是否开启多数据源客户端
     */
    private boolean enabled;
    /**
     * 多数据源配置
     */
    private Map<String, RedisConnector> instances;
    /**
     * 最小闲置
     */
    private int poolMinIdle;
    /**
     * 最大闲置
     */
    private int poolMaxIdle;
    /**
     * 最大等待时间
     */
    private long poolMaxWaitMillis;

    public static class RedisConnector {
        /**
         * 连接地址
         */
        private String host;
        /**
         * 连接端口
         */
        private int port;
        /**
         * 节点地址（哨兵或主从）
         */
        private String nodes;
        /**
         * 主节点地址
         */
        private String master;
        /**
         * 密码
         */
        private String password;
        /**
         * 超时时间
         */
        private long timeoutMillis;
        /**
         * 使用的db
         */
        private int dbIndex;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getNodes() {
            return nodes;
        }

        public void setNodes(String nodes) {
            this.nodes = nodes;
        }

        public String getMaster() {
            return master;
        }

        public void setMaster(String master) {
            this.master = master;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public long getTimeoutMillis() {
            return timeoutMillis;
        }

        public void setTimeoutMillis(long timeoutMillis) {
            this.timeoutMillis = timeoutMillis;
        }

        public int getDbIndex() {
            return dbIndex;
        }

        public void setDbIndex(int dbIndex) {
            this.dbIndex = dbIndex;
        }
    }

    public RedisMultiSourceProperties() {
        enabled = false;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Map<String, RedisConnector> getInstances() {
        return instances;
    }

    public void setInstances(Map<String, RedisConnector> instances) {
        this.instances = instances;
    }

    public int getPoolMinIdle() {
        return poolMinIdle;
    }

    public void setPoolMinIdle(int poolMinIdle) {
        this.poolMinIdle = poolMinIdle;
    }

    public int getPoolMaxIdle() {
        return poolMaxIdle;
    }

    public void setPoolMaxIdle(int poolMaxIdle) {
        this.poolMaxIdle = poolMaxIdle;
    }

    public long getPoolMaxWaitMillis() {
        return poolMaxWaitMillis;
    }

    public void setPoolMaxWaitMillis(long poolMaxWaitMillis) {
        this.poolMaxWaitMillis = poolMaxWaitMillis;
    }

}
