package org.basis.enhance.mongo.multisource.runner;

import com.mongodb.MongoClientSettings;
import org.apache.commons.collections4.MapUtils;
import org.basis.enhance.mongo.config.properties.EnhanceMongoProperties;
import org.basis.enhance.mongo.config.properties.MongoDataSourceProperties;
import org.basis.enhance.mongo.infra.constant.EnhanceMongoConstant;
import org.basis.enhance.mongo.multisource.context.MongoDataSourceContext;
import org.basis.enhance.mongo.multisource.creator.MongoClientCreator;
import org.basis.enhance.mongo.multisource.factory.DynamicMongoTemplateFactory;
import org.basis.enhance.mongo.multisource.register.MongoDataSourceRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

/**
 * Mongo多数据源注册runner
 * 一个数据源对应一个MongoTemplate，该runner的作用是，当容器启动完毕后，将容器中所有的多数据源的MongoTemplate缓存起来
 *
 * @author Mr_wenpan@163.com 2021/09/06 10:33
 */
@Component
public class MongoMultiSourceRegisterRunner implements CommandLineRunner, EnvironmentAware {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Environment environment;

    private MongoDataSourceContext mongoDataSourceContext;

    private ApplicationContext applicationContext;

    final String mongoTemplate = "mongoTemplate";

    public MongoMultiSourceRegisterRunner(ApplicationContext applicationContext,
                                          MongoDataSourceContext mongoDataSourceContext) {
        this.applicationContext = applicationContext;
        this.mongoDataSourceContext = mongoDataSourceContext;
    }

    @Override
    public void run(String... args) throws Exception {
        // 获取所有数据源名称（注意：只包含spring.data.mongodb.datasource下的数据源）
        Map<String, EnhanceMongoProperties> datasource = applicationContext.getBean(MongoDataSourceProperties.class).getDatasource();
        if (datasource.size() < 1) {
            logger.error("No mongodb multi datasource config, register multi datasource failed. please check config.");
            return;
        }
        // 注册多数据源的mongoTemplate
        registerMultiSourceFromContainer(datasource);
    }

    /**
     * 从容器中获取多个MongoDB数据源，并且注入到MongoDataSourceRegister统一管理
     */
    private void registerMultiSourceFromContainer(Map<String, EnhanceMongoProperties> dataSources) {
        // 注册默认数据源对应的MongoTemplate
        MongoTemplate defaultMongoTemplate = applicationContext.getBean(mongoTemplate, MongoTemplate.class);
        MongoDataSourceRegister.registerMongoTemplate(EnhanceMongoConstant.MultiSource.DEFAULT_SOURCE_TEMPLATE, defaultMongoTemplate);

        if (MapUtils.isEmpty(dataSources)) {
            logger.warn("No available mongo multi-data source was found.");
            return;
        }

        // 注册其余数据源对应的MongoTemplate
        for (String dataSourceName : dataSources.keySet()) {
            String name = dataSourceName + EnhanceMongoConstant.MultiSource.MONGO_TEMPLATE;
            MongoTemplate mongoTemplate = applicationContext.getBean(name, MongoTemplate.class);
            MongoDataSourceRegister.registerMongoTemplate(name, mongoTemplate);
            logger.info("Data source registered successfully, the datasource name is {}.", name);
        }
    }

    /**
     * 创建多MongoTemplate实例，并注册到MongoDataSourceRegister中（但不注册到容器）
     */
    @Deprecated
    private void createMultiMongoTemplate(Map<String, MongoProperties> dataSources) {
        if (MapUtils.isEmpty(dataSources)) {
            logger.warn("No available mongo multi-data source was found.");
            return;
        }
        // 注册多数据源的mongoTemplate
        dataSources.forEach((k, v) -> {
            MongoClientSettingsBuilderCustomizer builderCustomizers = MongoClientCreator.createMongoPropertiesCustomizer(v, environment);
            MongoClientSettings mongoClientSettings = MongoClientCreator.createMongoClientSettings();
            DynamicMongoTemplateFactory dynamicMongoTemplateFactory = mongoDataSourceContext
                    .getDynamicMongoTemplateFactory(k, Collections.singletonList(builderCustomizers), mongoClientSettings);
            try {
                MongoTemplate mongoTemplate = dynamicMongoTemplateFactory.createMongoTemplate();
                // 由于这里没有注入spring容器，需要手动设置上applicationContext
                mongoTemplate.setApplicationContext(applicationContext);
                MongoDataSourceRegister.registerMongoTemplate(k, mongoTemplate);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Create dynamic mongoTemplate failed, the monogo source name is " + k);
            }
        });
    }

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }
}
