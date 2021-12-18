package org.basis.enhance.mongo.multisource.runner;

import com.mongodb.MongoClientSettings;
import org.basis.enhance.mongo.config.properties.MongoDataSourceProperties;
import org.basis.enhance.mongo.multisource.context.MongoDataSourceContext;
import org.basis.enhance.mongo.multisource.creator.MongoClientCreator;
import org.basis.enhance.mongo.multisource.factory.DynamicMongoTemplateFactory;
import org.basis.enhance.mongo.multisource.register.MongoDataSourceRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private MongoDataSourceContext mongoDataSourceContext;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void run(String... args) throws Exception {
        // 获取所有数据源名称（注意：只包含spring.data.mongodb.datasource下的数据源）
        MongoDataSourceProperties sourceProperties = applicationContext.getBean(MongoDataSourceProperties.class);
        Map<String, MongoProperties> datasource = sourceProperties.getDatasource();

        if (datasource.size() < 1) {
            logger.error("no mongodb multi datasource config, register multi datasource failed. please check config.");
            return;
        }

        // 注册多数据源的mongoTemplate
        datasource.forEach((k, v) -> {
            MongoClientSettingsBuilderCustomizer builderCustomizers = MongoClientCreator.createMongoPropertiesCustomizer(v, environment);
            MongoClientSettings mongoClientSettings = MongoClientCreator.createMongoClientSettings();
            DynamicMongoTemplateFactory dynamicMongoTemplateFactory = mongoDataSourceContext
                    .getDynamicMongoTemplateFactory(k, Collections.singletonList(builderCustomizers), mongoClientSettings);
            try {
                MongoTemplate mongoTemplate = dynamicMongoTemplateFactory.createMongoTemplate();
                // 由于这里没有注入spring容器，需要手动设置上applicationContext
                mongoTemplate.setApplicationContext(applicationContext);
                MongoDataSourceRegister.redisterMongoTemplate(k, mongoTemplate);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("create dynamic mongoTemplate failed, the monogo source name is " + k);
            }
        });

        System.out.println("mongo多数据源注册完毕");

    }

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }
}
