**csdn博客：**[https://blog.csdn.net/Hellowenpan?spm=1000.2115.3001.5343](https://blog.csdn.net/Hellowenpan?spm=1000.2115.3001.5343)

**个人博客**：[http://www.bad-guy.cn](http://www.bad-guy.cn)


### 项目功能说明
该工程（basis-enhance）主要是对一些常用的组件的一个增强，实现可插拔，尽量做到开箱即用！

### 已增强的组件

***

#### 1、spring-boot-starter-data-redis模块增强

- 动态切换redis db
  - 非集群模式下支持Redis动态切换db，使用者按照自己的意愿选择要操作的db
- redis队列消费监控
  - 提供redis的list队列消费监控，当队列中有数据时进行拉取消费
- redis多数据源
  - 支持多个redis数据源切换，使用者可以按自己意愿使用多个redis数据源
- 常用redis使用帮助器
  - 将Redis常用的使用操作进行封装，使得更加易用

**详细使用介绍链接**：[spring-boot-data-redis功能增强插件使用及相关原理](./enhance-boot-data-redis/README.md)



***

#### 2、分布式任务增强

- 支持一个主任务拆分成多个子任务后分布到多个节点进行执行，降低单机压力，提升执行效率（任务抢占式执行）
- 适用场景为主任务无需获取子任务执行的结果数据的情景（后面待优化）
- 使用时需要配合中间件MongoDB和Redis以及文件存储（可选）使用

**详细使用介绍链接**：[分布式任务执行器使用及原理介绍](./enhance-distribute-executor/README.md)



***

#### 3、基于logback日志增强插件

- 基于logback进行打印日志增强，可按照使用者意愿打印不同模式下的日志信息（debug模式、info模式等）
- 客户端一次调用过程中，此次调用的所有日志信息的`traceId`都保持一致（包括feign调用、多线程使用等）便于通过日志排查生产环境问题
- 使用者可按照使用意愿控制是否打印入参、出参，以及控制入参或出参中某些字段不打印

**详细使用介绍链接**：问题待优化



***

#### 4、mybatis增强

- mybatis在insert数据的时候，根据用户动态选择的ID生成算法生成待插入数据的ID，并使用该ID插入到数据库。适用于分表字段生成、基因注入
- 该功能在mybatis-plus已经存在，所以这个功能只适合于项目上为了追求项目稳定运行ORM框架不愿意从mybatis升级为mybatis-plus的系统使用，所以现在看起来意义不是很大了，放弃继续开发了，现有代码可以作为思路参考

**详细使用介绍链接**：[mybatis主键ID使用自定义算法生成增强介绍](./enhance-boot-mybatis/README.md)

***

#### 5、异常增强

主要是利用spring的@ControllerAdvice对常见异常进行捕获，然后结合多语言和 `异常描述 + 参数格式化`以友好的方式返回给前端

**详细使用介绍链接**：[enhance-boot-exception 以友好的方式返回异常信息给前台](./enhance-boot-exception/README.md)

***

#### 6、并发功能增强

待整理

***

#### 7、jdk相关功能封装和增强

动态可调整线程池增强（待实现）

