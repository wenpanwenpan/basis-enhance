package org.basis.enhance.template;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.query.SortQuery;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.core.script.ScriptExecutor;
import org.springframework.data.redis.core.types.RedisClientInfo;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.io.Closeable;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * RedisTemplate 动态路由
 *
 * @author Mr_wenpan@163.com 2021/8/4 12:20 下午
 */
public abstract class AbstractRoutingRedisTemplate<K, V> extends RedisTemplate<K, V> implements InitializingBean {

    /**
     * 存放对应库的redisTemplate，用于操作对应的db
     */
    private Map<Object, RedisTemplate<K, V>> redisTemplates;

    /**
     * 当不指定库时默认使用的redisTemplate
     */
    private RedisTemplate<K, V> defaultRedisTemplate;

    /**
     * 当类被加载到容器中属性设置完毕后检查redisTemplates和defaultRedisTemplate是否为空
     *
     * @author Mr_wenpan@163.com 2021/8/4 12:27 下午
     */
    @Override
    public void afterPropertiesSet() {
        if (redisTemplates == null) {
            throw new IllegalArgumentException("Property 'redisTemplates' is required");
        }
        if (defaultRedisTemplate == null) {
            throw new IllegalArgumentException("Property 'defaultRedisTemplate' is required");
        }
    }

    /**
     * 获取要操作的RedisTemplate
     */
    protected RedisTemplate<K, V> determineTargetRedisTemplate() {
        // 当前要操作的DB
        Object lookupKey = determineCurrentLookupKey();
        // 如果当前要操作的DB为空则使用默认的RedisTemplate（使用0号库）
        if (lookupKey == null) {
            return defaultRedisTemplate;
        }
        RedisTemplate<K, V> redisTemplate = redisTemplates.get(lookupKey);
        // 如果当前要操作的db还没有维护到redisTemplates中，则创建一个对该库的连接并缓存起来
        if (redisTemplate == null) {
            // 双重检查，这里直接使用synchronized锁，因为创建redisTemplate不会很频繁，一般整个生命周期只有几次，不会有性能问题
            synchronized (DynamicRedisTemplate.class) {
                if (null == redisTemplates.get(lookupKey)) {
                    redisTemplate = createRedisTemplateOnMissing(lookupKey);
                    redisTemplates.put(lookupKey, redisTemplate);
                }
            }
        }
        return redisTemplate;
    }

    /**
     * 获取当前 Redis db
     *
     * @return current redis db
     */
    protected abstract Object determineCurrentLookupKey();

    /**
     * 没有对应 db 的 RedisTemplate 时，则调用此方法创建 RedisTemplate
     *
     * @param lookupKey RedisDB
     * @return RedisTemplate
     */
    public abstract RedisTemplate<K, V> createRedisTemplateOnMissing(Object lookupKey);

    public void setRedisTemplates(Map<Object, RedisTemplate<K, V>> redisTemplates) {
        this.redisTemplates = redisTemplates;
    }

    public void setDefaultRedisTemplate(RedisTemplate<K, V> defaultRedisTemplate) {
        this.defaultRedisTemplate = defaultRedisTemplate;
    }

    public Map<Object, RedisTemplate<K, V>> getRedisTemplates() {
        return redisTemplates;
    }

    public RedisTemplate<K, V> getDefaultRedisTemplate() {
        return defaultRedisTemplate;
    }

    // ====================以下都是继承自父类的方法，RedisTemplate中的方法执行时会调用下面的方法=====================

    @Override
    public <T> T execute(RedisCallback<T> action) {
        return determineTargetRedisTemplate().execute(action);
    }

    @Override
    public <T> T execute(RedisCallback<T> action, boolean exposeConnection) {
        return determineTargetRedisTemplate().execute(action, exposeConnection);
    }

    @Override
    public <T> T execute(RedisCallback<T> action, boolean exposeConnection, boolean pipeline) {
        return determineTargetRedisTemplate().execute(action, exposeConnection, pipeline);
    }

    @Override
    public <T> T execute(SessionCallback<T> session) {
        return determineTargetRedisTemplate().execute(session);
    }

    @Override
    public List<Object> executePipelined(SessionCallback<?> session) {
        return determineTargetRedisTemplate().executePipelined(session);
    }

    @Override
    public List<Object> executePipelined(SessionCallback<?> session, RedisSerializer<?> resultSerializer) {
        return determineTargetRedisTemplate().executePipelined(session, resultSerializer);
    }

    @Override
    public List<Object> executePipelined(RedisCallback<?> action) {
        return determineTargetRedisTemplate().executePipelined(action);
    }

    @Override
    public List<Object> executePipelined(RedisCallback<?> action, RedisSerializer<?> resultSerializer) {
        return determineTargetRedisTemplate().executePipelined(action, resultSerializer);
    }

    @Override
    public <T> T execute(RedisScript<T> script, List<K> keys, Object... args) {
        return determineTargetRedisTemplate().execute(script, keys, args);
    }

    @Override
    public <T> T execute(RedisScript<T> script, RedisSerializer<?> argsSerializer,
                         RedisSerializer<T> resultSerializer, List<K> keys, Object... args) {
        return determineTargetRedisTemplate().execute(script, argsSerializer, resultSerializer, keys, args);
    }

    @Override
    public <T extends Closeable> T executeWithStickyConnection(RedisCallback<T> callback) {
        return determineTargetRedisTemplate().executeWithStickyConnection(callback);
    }

    @Override
    public boolean isExposeConnection() {
        return determineTargetRedisTemplate().isExposeConnection();
    }

    @Override
    public void setExposeConnection(boolean exposeConnection) {
        determineTargetRedisTemplate().setExposeConnection(exposeConnection);
    }

    @Override
    public boolean isEnableDefaultSerializer() {
        return determineTargetRedisTemplate().isEnableDefaultSerializer();
    }

    @Override
    public void setEnableDefaultSerializer(boolean enableDefaultSerializer) {
        determineTargetRedisTemplate().setEnableDefaultSerializer(enableDefaultSerializer);
    }

    @Override
    public RedisSerializer<?> getDefaultSerializer() {
        return determineTargetRedisTemplate().getDefaultSerializer();
    }

    @Override
    public void setDefaultSerializer(RedisSerializer<?> serializer) {
        determineTargetRedisTemplate().setDefaultSerializer(serializer);
    }

    @Override
    public void setKeySerializer(RedisSerializer<?> serializer) {
        determineTargetRedisTemplate().setKeySerializer(serializer);
    }

    @Override
    public RedisSerializer<?> getKeySerializer() {
        return determineTargetRedisTemplate().getKeySerializer();
    }

    @Override
    public void setValueSerializer(RedisSerializer<?> serializer) {
        determineTargetRedisTemplate().setValueSerializer(serializer);
    }

    @Override
    public RedisSerializer<?> getValueSerializer() {
        return determineTargetRedisTemplate().getValueSerializer();
    }

    @Override
    public RedisSerializer<?> getHashKeySerializer() {
        return determineTargetRedisTemplate().getHashKeySerializer();
    }

    @Override
    public void setHashKeySerializer(RedisSerializer<?> hashKeySerializer) {
        determineTargetRedisTemplate().setHashKeySerializer(hashKeySerializer);
    }

    @Override
    public RedisSerializer<?> getHashValueSerializer() {
        return determineTargetRedisTemplate().getHashValueSerializer();
    }

    @Override
    public void setHashValueSerializer(RedisSerializer<?> hashValueSerializer) {
        determineTargetRedisTemplate().setHashValueSerializer(hashValueSerializer);
    }

    @Override
    public RedisSerializer<String> getStringSerializer() {
        return determineTargetRedisTemplate().getStringSerializer();
    }

    @Override
    public void setStringSerializer(RedisSerializer<String> stringSerializer) {
        determineTargetRedisTemplate().setStringSerializer(stringSerializer);
    }

    @Override
    public void setScriptExecutor(ScriptExecutor<K> scriptExecutor) {
        determineTargetRedisTemplate().setScriptExecutor(scriptExecutor);
    }

    @Override
    public List<Object> exec() {
        return determineTargetRedisTemplate().exec();
    }

    @Override
    public List<Object> exec(RedisSerializer<?> valueSerializer) {
        return determineTargetRedisTemplate().exec(valueSerializer);
    }

    @Override
    public Boolean delete(K key) {
        return determineTargetRedisTemplate().delete(key);
    }

    @Override
    public Long delete(Collection<K> keys) {
        return determineTargetRedisTemplate().delete(keys);
    }

    @Override
    public Boolean hasKey(K key) {
        return determineTargetRedisTemplate().hasKey(key);
    }

    @Override
    public Boolean expire(K key, long timeout, TimeUnit unit) {
        return determineTargetRedisTemplate().expire(key, timeout, unit);
    }

    @Override
    public Boolean expireAt(K key, Date date) {
        return determineTargetRedisTemplate().expireAt(key, date);
    }

    @Override
    public void convertAndSend(String channel, Object message) {
        determineTargetRedisTemplate().convertAndSend(channel, message);
    }

    @Override
    public Long getExpire(K key) {
        return determineTargetRedisTemplate().getExpire(key);
    }

    @Override
    public Long getExpire(K key, TimeUnit timeUnit) {
        return determineTargetRedisTemplate().getExpire(key, timeUnit);
    }

    @Override
    public Set<K> keys(K pattern) {
        return determineTargetRedisTemplate().keys(pattern);
    }

    @Override
    public Boolean persist(K key) {
        return determineTargetRedisTemplate().persist(key);
    }

    @Override
    public Boolean move(K key, int dbIndex) {
        return determineTargetRedisTemplate().move(key, dbIndex);
    }

    @Override
    public K randomKey() {
        return determineTargetRedisTemplate().randomKey();
    }

    @Override
    public void rename(K oldKey, K newKey) {
        determineTargetRedisTemplate().rename(oldKey, newKey);
    }

    @Override
    public Boolean renameIfAbsent(K oldKey, K newKey) {
        return determineTargetRedisTemplate().renameIfAbsent(oldKey, newKey);
    }

    @Override
    public DataType type(K key) {
        return determineTargetRedisTemplate().type(key);
    }

    @Override
    public byte[] dump(K key) {
        return determineTargetRedisTemplate().dump(key);
    }

    @Override
    public void restore(K key, byte[] value, long timeToLive, TimeUnit unit) {
        determineTargetRedisTemplate().restore(key, value, timeToLive, unit);
    }

    @Override
    public void multi() {
        determineTargetRedisTemplate().multi();
    }

    @Override
    public void discard() {
        determineTargetRedisTemplate().discard();
    }

    @Override
    public void watch(K key) {
        determineTargetRedisTemplate().watch(key);
    }

    @Override
    public void watch(Collection<K> keys) {
        determineTargetRedisTemplate().watch(keys);
    }

    @Override
    public void unwatch() {
        determineTargetRedisTemplate().unwatch();
    }

    @Override
    public List<V> sort(SortQuery<K> query) {
        return determineTargetRedisTemplate().sort(query);
    }

    @Override
    public <T> List<T> sort(SortQuery<K> query, RedisSerializer<T> resultSerializer) {
        return determineTargetRedisTemplate().sort(query, resultSerializer);
    }

    @Override
    public <T> List<T> sort(SortQuery<K> query, BulkMapper<T, V> bulkMapper) {
        return determineTargetRedisTemplate().sort(query, bulkMapper);
    }

    @Override
    public <T, S> List<T> sort(SortQuery<K> query, BulkMapper<T, S> bulkMapper, RedisSerializer<S> resultSerializer) {
        return determineTargetRedisTemplate().sort(query, bulkMapper, resultSerializer);
    }

    @Override
    public Long sort(SortQuery<K> query, K storeKey) {
        return determineTargetRedisTemplate().sort(query, storeKey);
    }

    @Override
    public BoundValueOperations<K, V> boundValueOps(K key) {
        return determineTargetRedisTemplate().boundValueOps(key);
    }

    @Override
    public ValueOperations<K, V> opsForValue() {
        return determineTargetRedisTemplate().opsForValue();
    }

    @Override
    public ListOperations<K, V> opsForList() {
        return determineTargetRedisTemplate().opsForList();
    }

    @Override
    public BoundListOperations<K, V> boundListOps(K key) {
        return determineTargetRedisTemplate().boundListOps(key);
    }

    @Override
    public BoundSetOperations<K, V> boundSetOps(K key) {
        return determineTargetRedisTemplate().boundSetOps(key);
    }

    @Override
    public SetOperations<K, V> opsForSet() {
        return determineTargetRedisTemplate().opsForSet();
    }

    @Override
    public BoundZSetOperations<K, V> boundZSetOps(K key) {
        return determineTargetRedisTemplate().boundZSetOps(key);
    }

    @Override
    public ZSetOperations<K, V> opsForZSet() {
        return determineTargetRedisTemplate().opsForZSet();
    }

    @Override
    public GeoOperations<K, V> opsForGeo() {
        return determineTargetRedisTemplate().opsForGeo();
    }

    @Override
    public BoundGeoOperations<K, V> boundGeoOps(K key) {
        return determineTargetRedisTemplate().boundGeoOps(key);
    }

    @Override
    public HyperLogLogOperations<K, V> opsForHyperLogLog() {
        return determineTargetRedisTemplate().opsForHyperLogLog();
    }

    @Override
    public <HK, HV> BoundHashOperations<K, HK, HV> boundHashOps(K key) {
        return determineTargetRedisTemplate().boundHashOps(key);
    }

    @Override
    public <HK, HV> HashOperations<K, HK, HV> opsForHash() {
        return determineTargetRedisTemplate().opsForHash();
    }

    @Override
    public ClusterOperations<K, V> opsForCluster() {
        return determineTargetRedisTemplate().opsForCluster();
    }

    @Override
    public void killClient(String host, int port) {
        determineTargetRedisTemplate().killClient(host, port);
    }

    @Override
    public List<RedisClientInfo> getClientList() {
        return determineTargetRedisTemplate().getClientList();
    }

    @Override
    public void slaveOf(String host, int port) {
        determineTargetRedisTemplate().slaveOf(host, port);
    }

    @Override
    public void slaveOfNoOne() {
        determineTargetRedisTemplate().slaveOfNoOne();
    }

    @Override
    public void setEnableTransactionSupport(boolean enableTransactionSupport) {
        determineTargetRedisTemplate().setEnableTransactionSupport(enableTransactionSupport);
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        determineTargetRedisTemplate().setBeanClassLoader(classLoader);
    }

    @Override
    public RedisConnectionFactory getConnectionFactory() {
        return determineTargetRedisTemplate().getConnectionFactory();
    }

    @Override
    public void setConnectionFactory(RedisConnectionFactory connectionFactory) {
        determineTargetRedisTemplate().setConnectionFactory(connectionFactory);
    }

}
