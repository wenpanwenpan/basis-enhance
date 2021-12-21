**源代码**：[https://gitee.com/mr_wenpan/basis-enhance](https://gitee.com/mr_wenpan/basis-enhance)

**使用参考**：`TestMongoMutilSourceController`

## 一、功能介绍

- 支持一个springboot应用程序中配置多个MongoDB数据源
- 使用注解`@EnableMongoMultiSource`开启MongoDB多数据源功能启用与禁用，实现了动态可插拔功能。
- 提供`MongoMultiSourceClient`来获取并操作某个指定的数据源
- 对应每个MongoDB集合都支持自定义分片算法
- 提供默认的一致性hash算法实现，解决使用一致性hash分片算法时可能存在的多数据源数据倾斜问题
- 提供默认的一致性hash分片算法实现，对于MongoDB集合可以采用一致性hash算法来动态选取存放数据的数据源。
  - 使用时只用在代码里使用`@Autowired`注入`mongoTemplateConsistentHash`即可使用一致性hash算法。



## 二、使用教程

### 1、基础配置

#### ①、项目中引入插件依赖

```xml
<dependency>
    <groupId>org.basis.enhance</groupId>
    <artifactId>enhance-boot-mongo</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

#### ②、application.yml配置

```yml
spring:
  data:
    mongodb:
      # 默认数据源
      uri: ${MONGODB_DEFAULT_URL:mongodb://用户名:密码@ip:端口/库名}
      # 开启多数据源分片
      enable-sharding: true
      # 多数据源配置
      datasource:
        datasource1:
          order: 1
          uri: ${MONGODB_DEFAULT_URL:mongodb://用户名:密码@ip:端口/库名}
        datasource2:
          order: 2
          uri: ${MONGODB_DEFAULT_URL:mongodb://用户名:密码@ip:端口/库名}
```

#### ③、启动类上开启MongoDB多数据源

```java
// 使用@EnableMongoMultiSource注解开启MongoDB多数据源
@EnableMongoMultiSource
@SpringBootApplication
public class EnhanceMongoDemoApplication {

    public static void main(String... args) {
        SpringApplication.run(EnhanceMongoDemoApplication.class, args);
    }
}
```

### 2、使用

```java
// MongoDB多数据源使用示例
public void multiSourceExample() {
    // 通过一致性hash算法查找对应数据源的MongoTemplate
    MongoTemplate templateByHash = mongoMultiSourceClient.getMongoTemplateByHash("wenpan");
    // 获取默认的MongoDB数据源
    MongoTemplate defaultMongoTemplate = mongoMultiSourceClient.getDefaultMongoTemplate();
    // 通过数据源名称获取对应的MongoDB数据源
    MongoTemplate datasource1MongoTemplate = mongoMultiSourceClient.getMongoTemplate("datasource1MongoTemplate");
    // 通过集合 + 分片key 获取对应的MongoDB数据源
    MongoTemplate mongoTemplate = mongoMultiSourceClient.getMongoTemplate("enhance_delivery_confirm", "xxx");

    // 使用对应数据源的MongoTemplate去操作MongoDB
    // 省略......
}
```



## 三、核心实现流程

- 首先基于`spring-boot-starter-data-mongodb`，先不用思考如何实现多数据源功能，配置好常规的springboot 和MongoDB整合，先把项目启动起来
- 项目能正常启动后，主要关注mongoTemplate在springboot中是如何通过自动配置被自动注入的，开始debug源码
- 主要关注`MongoAutoConfiguration`、`MongoDatabaseFactoryDependentConfiguration`、`MongoDatabaseFactoryConfiguration`这几个类，在这几个类中可以看到分别有对mongoClient、mongoTemplateFactory、mongoTemplate的注入
- 关注核心流程，对注入mongoClient、mongoTemplateFactory、mongoTemplate的地方打上断点，单步调试这三个类注入的详细流程以及所需的参数
- debug完上述三个类的注入流程后，我们大概的清楚在springboot整合MongoDB并注入方便使用的MongoTemplate，主要是使用Mongo连接工厂去创建对mongo服务器的连接，而mongo连接工厂的创建又需要用到mongoClient去创建连接。
- 所以综上所述，我们要实现mongo多数据源，只需要参考springboot对于MongoDB的自动配置是如何注入MongoTemplate、MongoClientFactory以及MongoFactory就行。然后按照他的注入模式自己创建这些类的对象。
- 为了方便使用，需要为使用方提供一个便于操作的MongoMultiSourceClient，使用方可以使用这个MongoMultiSourceClient去动态的切换MongoDB数据源、也可以对于MongoDB的某个集合，通过【自定义分片算法 + 集合名称 + 分片key】去动态的选择MongoDB数据源。



## 四、问题难点

1、对于如何获取application.yml配置文件中使用方动态配置的数据源信息（比如使用方可以配置3个数据源，也可以是4个数据源，也可以是五个数据源等等），并为这些数据源创建对应的可用连接，并创建出对应的MongoTemplate然后注入到容器这是一个难题

- 我们可以通过`@ConfigurationProperties注解 + hashMap` 将application.yml配置文件中有关MongoDB多数据源的配置映射到map中，map中一个key-value键值对就表示一个数据源的具体配置信息
- 由于数据源的个数是由使用方动态配置的，所以在程序启动前我们并不知道有多少个数据源，所以无法在配置文件中通过`@Bean`的方式硬编码。所以我们利用`factoryBean + spring的后置处理器`可以实现在程序启动过程中动态的获取application.yml文件中的数据源配置，然后根据数据源个数动态的为每个MongoDB数据源注入对应的mongoTemplate



## 五、核心实现

#### 1、多数据源注入

```java
public final class MonogoMultiDataSourceRegistrar implements EnvironmentAware, ImportBeanDefinitionRegistrar {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Environment environment;

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }

    /**
     * 为每个mongo数据源注入BeanDefinition
     */
    @Override
    public void registerBeanDefinitions(@NonNull AnnotationMetadata importingClassMetadata,
                                        @NonNull BeanDefinitionRegistry registry,
                                        BeanNameGenerator importBeanNameGenerator) {
        Set<String> names = EnvironmentUtil.loadMongoDataSourceName((AbstractEnvironment) environment);
        if (names.size() <= 0) {
            logger.error("no mongo multi datasource config, inject multi datasource failed. please check config.");
            return;
        }

        logger.info("register mongo datasource: {}", names);

        for (String name : names) {
            registerMongoTemplateBeanDefinition(name, MongoTemplateFactoryBean.class, registry);
        }
    }

    /**
     * 注册 MongoTemplate BeanDefinition
     */
    protected final void registerMongoTemplateBeanDefinition(String alias, Class<?> type, BeanDefinitionRegistry registry) {
        // BeanDefinition构建器
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(type);
        // 设置通过名称注入
        builder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_NAME);
        builder.addConstructorArgValue(null);
        builder.addPropertyValue(MongoDataSourceContext.FIELD_DATASOURCE_NAME, alias);

        BeanDefinition beanDefinition = builder.getBeanDefinition();
        beanDefinition.setPrimary(false);

        String beanName = alias + EnhanceMongoConstant.MultiSource.MONGO_TEMPLATE;
        BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, beanName, new String[]{alias + "-template"});
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
    }

    /**
     * 创建 MongoTemplate 的 FactoryBean
     * FactoryBean一般用于构建复杂的bean
     */
    protected final class MongoTemplateFactoryBean extends MongoDataSourceContext implements FactoryBean<Object> {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        /**
         * 返回要创建的bean对象
         */
        @Override
        public Object getObject() throws Exception {
            // 为该数据源创建一个Mongo连接工厂，连向指定的数据源
            DynamicMongoTemplateFactory dynamicMongoTemplateFactory = getDynamicMongoTemplateFactory();

            logger.info("Dynamic create a MongoTemplate named {}", getDataSourceName());

            MongoTemplate mongoTemplate = dynamicMongoTemplateFactory.createMongoTemplate();
            // 由于这里没有注入spring容器，需要手动设置上applicationContext
            mongoTemplate.setApplicationContext(applicationContext);
            return mongoTemplate;
        }

        @Override
        public Class<?> getObjectType() {
            return MongoTemplate.class;
        }
    }

}
```

#### 2、mongo连接工厂创建

```java
public DynamicMongoTemplateFactory getDynamicMongoTemplateFactory() {
    MongoProperties mongoProperties = getMongoProperties(dataSourceName);
    MongoClientSettingsBuilderCustomizer builderCustomizers = MongoClientCreator.createMongoPropertiesCustomizer(mongoProperties, environment);
    MongoClientSettings mongoClientSettings = MongoClientCreator.createMongoClientSettings();
    MongoClient mongoClient = MongoClientCreator.createMongoClient(Collections.singletonList(builderCustomizers), mongoClientSettings);
    return new DynamicMongoTemplateFactory(mongoClient, mongoProperties, applicationContext);
}
```

#### 3、mongo数据源对应的mongoTemplate创建

```java
public MongoTemplate createMongoTemplate() throws ClassNotFoundException {
        // 创建mongo客户端工厂
        SimpleMongoClientDatabaseFactory factory = new SimpleMongoClientDatabaseFactory(mongoClient, properties.getMongoClientDatabase());
        MappingMongoConverter mappingMongoConverter = createMappingMongoConverter(factory);
        // 根据工厂创建template
        return new MongoTemplate(factory, mappingMongoConverter);
}
```

