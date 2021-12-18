package org.basis.enhance.mongo.multisource.context;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import org.apache.commons.lang3.StringUtils;
import org.basis.enhance.mongo.config.properties.MongoDataSourceProperties;
import org.basis.enhance.mongo.multisource.creator.MongoClientCreator;
import org.basis.enhance.mongo.multisource.factory.DynamicMongoTemplateFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * mongo多数据源上下文
 *
 * @author Mr_wenpan@163.com 2021/12/17 10:03
 */
@Component
public class MongoDataSourceContext implements ApplicationContextAware {

    public static final String FIELD_DATASOURCE_NAME = "dataSourceName";

    protected ApplicationContext applicationContext;

    protected String dataSourceName;

    /**
     * 创建动态mongoTemplate工厂
     */
    public DynamicMongoTemplateFactory getDynamicMongoTemplateFactory(String dataSourceName,
                                                                      List<MongoClientSettingsBuilderCustomizer> builderCustomizers,
                                                                      MongoClientSettings settings) {
        // 创建mongo客户端
        MongoClient mongoClient = MongoClientCreator.createMongoClient(builderCustomizers, settings);
        MongoProperties mongoProperties = getMongoProperties(dataSourceName);
        return new DynamicMongoTemplateFactory(mongoClient, mongoProperties, applicationContext);
    }

    /**
     * 通过数据源名称获取该数据源对应的Mongo配置
     */
    public MongoProperties getMongoProperties(String dataSourceName) {
        if (StringUtils.isBlank(dataSourceName)) {
            return null;
        }
        return applicationContext.getBean(MongoDataSourceProperties.class).getDatasource().get(dataSourceName);
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
