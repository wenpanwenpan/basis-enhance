## 一、功能介绍

- 该项目（`enhance-boot-limiting`）主要是基于Redis + lua实现了分布式限流功能
- 项目中提供两种分布式限流算法（一种是滑动时间窗口算法、一种是令牌桶算法）
- 项目中提供了方便使用的注解形式来直接对接口进行限流，详情见`@SlideWindowLimit`和`@TokenBucketLimit`注解，并且提供了`@EnableRedisLimiting`注解来实现动态可插拔功能
- 同时项目中也提供了灵活使用的限流助手`RedisLimitHelper`，可以通过RedisLimitHelper来灵活的实现限流功能



## 二、如何使用

### 1、拉取项目源代码

- 源码地址：[https://gitee.com/mr_wenpan/basis-enhance.git](https://gitee.com/mr_wenpan/basis-enhance.git)
- 由于我是将多个项目通过一个父pom来进行管理，并且jar包没有发布到maven仓库，所以需要自己将`enhance-boot-limiting`模块打包到自己本地maven私服（打包命令：`mvn clean install`）

### 2、打包项目到maven私服

- 命令 mvn clean install

### 3、项目的pom文件中引入`enhance-boot-limiting`依赖

- 注意：由于该组件是基于Redis而开发的分布式限流器，所以需要依赖`spring-boot-starter-data-redis`模块

```xml
<dependencies>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
  </dependency>
  <dependency>
    <groupId>org.basis.enhance</groupId>
    <artifactId>enhance-boot-limiting</artifactId>
    <version>1.0-SNAPSHOT</version>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
  </dependency>
  <dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-pool2</artifactId>
  </dependency>
  <dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
  </dependency>
</dependencies>
```

### 4、application.yml文件中配置

```yaml
server:
  port: 20002
spring:
  application:
    name: enhance-data-redis-demo
  # redis配置
  redis:
    host: ${SPRING_REDIS_HOST:192.168.1.22}
    port: ${SPRING_REDIS_PORT:6379}
    password: ${SPRING_REDIS_PASSWORD:123456}
    database: ${SPRING_REDIS_DATABASE:8}
    client-type: lettuce
    lettuce:
      pool:
        max-active: ${SPRING_REDIS_POOL_MAX_ACTIVE:16}
        max-idle: ${SPRING_REDIS_POOL_MAX_IDLE:16}
        max-wait: ${SPRING_REDIS_POOL_MAX_WAIT:5000}
```

### 5、主启动类上开启限流功能

- 使用`@EnableRedisLimiting`显示开启限流功能

```java
// 开启分布式限流
@EnableRedisLimiting
@SpringBootApplication
@EnableConfigurationProperties
public class DemoEnhanceLimitApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoEnhanceLimitApplication.class, args);
    }

}
```



## 三、使用测试

### 1、使用限流注解

直接在需要限流的接口上使用`@TokenBucketLimit`或`@SlideWindowLimit`便可以进行限流

```java
/**
* 使用注解进行滑动时间窗限流
*/
@SlideWindowLimit
@GetMapping("/test-annotation")
  public String testAnnotation() {
  log.info("请求没有被限流...");
  return "success";
}

/**
 * 使用注解进行令牌桶限流
 */
@TokenBucketLimit
@GetMapping("/test-annotation")
public String testAnnotation() {
  log.info("请求没有被限流...");
  return "success";
}
```



### 2、使用redisLimitHelper进行限流

- 使用redisLimitHelper进行限流更加灵活

```java
 @GetMapping("/test-01")
public String test01(String limitKey,Integer capacity,Integer permits,Double rate) {
  // 循环100次
  for (int i = 0; i < 100; i++) {
    // 桶的容量为10，每秒流入1个令牌，每次获取一个令牌
    Boolean limit = redisLimitHelper.tokenLimit(limitKey, capacity, permits, rate);
    // 是否被限流
    if (limit) {
      log.info("[{}] pass.", i);
    } else {
      log.error("[{}] can not pass.", i);
    }
  }
  return "success";
}
```



### 3、使用示例

- 具体使用详情见：[基于lua脚本的分布式限流使用测试](https://gitee.com/mr_wenpan/basis-enhance/tree/master/enhance-demo-parent/demo-enhance-boot-limiting/src/main/java/org/enhance/limiting/demo/api/v1/controller)



## 四、核心原理

使用方式和原理都很简单，不过多赘述，这里贴出两个核心的lua脚本

### 1、滑动窗口限流lua脚本

```lua
-- key对应着某个接口, value对应着这个接口的上一次请求时间
local unique_identifier = KEYS[1]
-- 上次请求时间key
local timeKey = 'lastTime'
-- 时间窗口内累计请求数量key
local requestKey = 'requestCount'
-- 限流大小,限流最大请求数
local maxRequest = tonumber(ARGV[1])
-- 当前请求时间戳,也就是请求的发起时间（毫秒）
local nowTime = tonumber(ARGV[2])
-- 窗口长度(毫秒)
local windowLength = tonumber(ARGV[3])

-- 限流开始时间
local currentTime = tonumber(redis.call('HGET', unique_identifier, timeKey) or '0')
-- 限流累计请求数
local currentRequest = tonumber(redis.call('HGET', unique_identifier, requestKey) or '0')

-- 当前时间在滑动窗口内
if currentTime + windowLength > nowTime then
    if currentRequest + 1 > maxRequest then
        return 0;
    else
        -- 在时间窗口内且请求数没超，请求数加一
        redis.call('HINCRBY', unique_identifier, requestKey, 1)
        return 1;
    end
else
    -- 超时后重置，开启一个新的时间窗口
    redis.call('HSET', unique_identifier, timeKey, nowTime)
    redis.call('HSET', unique_identifier, requestKey, '0')
    -- 窗口过期时间
    local expireTime = windowLength / 1000;
    redis.call('EXPIRE', unique_identifier, expireTime)
    redis.call('HINCRBY', unique_identifier, requestKey, 1)
    return 1;
end

```

### 2、令牌桶限流lua脚本

```lua
-- 令牌桶
local bucketKey = KEYS[1]
-- 上次请求的时间key
local last_request_time_key = 'lastRequestTime'
-- 令牌桶的容量
local capacity = tonumber(ARGV[1])
-- 请求令牌的数量
local permits = tonumber(ARGV[2])
-- 令牌流入的速率(按毫秒计算)
local rate = tonumber(ARGV[3])
-- 当前时间(毫秒)
local current_time = tonumber(ARGV[4])
-- 唯一标识
local unique_identifier = bucketKey

-- 恶意请求
if permits <= 0 then
    return 1
end

-- 获取当前桶内令牌的数量
local current_limit = tonumber(redis.call('HGET', unique_identifier, bucketKey) or '0')
-- 获取上次请求的时间
local last_mill_request_time = tonumber(redis.call('HGET', unique_identifier, last_request_time_key) or '0')
-- 计算向桶里添加令牌的数量
local add_token_num = 0
if last_mill_request_time == 0 then
   -- 如果是第一次请求，则进行初始化令牌桶，并且更新上次请求时间
   add_token_num = capacity
   redis.call("HSET", unique_identifier, last_request_time_key, current_time)
else
    -- 令牌流入桶内
   add_token_num = math.floor((current_time - last_mill_request_time) * rate)
end

-- 更新令牌的数量
if current_limit + add_token_num > capacity then
    current_limit = capacity
else
   current_limit = current_limit + add_token_num
end
-- 更新桶内令牌的数量
redis.pcall('HSET',unique_identifier, bucketKey, current_limit)

-- 限流判断
if current_limit - permits < 0 then
    -- 达到限流大小
    return 0
else
    -- 没有达到限流大小
   current_limit = current_limit - permits
   redis.pcall('HSET', unique_identifier, bucketKey, current_limit)
   -- 更新上次请求的时间
   redis.call('HSET', unique_identifier, last_request_time_key, current_time)
   return 1
end
```

