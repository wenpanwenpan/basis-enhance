package org.basis.enhance.mongo.multisource.factory;

import com.mongodb.client.MongoClient;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.domain.EntityScanner;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mapping.model.FieldNamingStrategy;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import java.util.Collections;

/**
 * 动态mongoTemplate工厂类
 *
 * @author Mr_wenpan@163.com 2021/12/16 22:30
 */
public class DynamicMongoTemplateFactory {

    /**
     * mongo客户端
     */
    private MongoClient mongoClient;

    /**
     * mongo配置文件
     */
    private MongoProperties properties;

    private ApplicationContext applicationContext;

    public DynamicMongoTemplateFactory(MongoClient mongoClient,
                                       MongoProperties properties,
                                       ApplicationContext applicationContext) {
        this.mongoClient = mongoClient;
        this.properties = properties;
        this.applicationContext = applicationContext;
    }

    /**
     * 创建MongoTemplate(每个template使用默认ClientDatabase)
     */
    public MongoTemplate createMongoTemplate() throws ClassNotFoundException {
        // 创建mongo客户端工厂
        SimpleMongoClientDatabaseFactory factory = new SimpleMongoClientDatabaseFactory(mongoClient, properties.getMongoClientDatabase());
        MappingMongoConverter mappingMongoConverter = createMappingMongoConverter(factory);
        // 根据工厂创建template
        return new MongoTemplate(factory, mappingMongoConverter);
    }

    /**
     * 创建MappingMongoConverter
     */
    public MappingMongoConverter createMappingMongoConverter(MongoDatabaseFactory factory) throws ClassNotFoundException {
        MongoCustomConversions mongoCustomConversions = createMongoCustomConversions();
        MongoMappingContext mappingContext = createMongoMappingContext(applicationContext, properties, mongoCustomConversions);
        return createMappingMongoConverter(factory, mappingContext, mongoCustomConversions);
    }

    /**
     * 创建MongoMappingContext
     */
    public MongoMappingContext createMongoMappingContext(ApplicationContext applicationContext,
                                                         MongoProperties properties,
                                                         MongoCustomConversions conversions) throws ClassNotFoundException {
        PropertyMapper mapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
        MongoMappingContext context = new MongoMappingContext();
        mapper.from(properties.isAutoIndexCreation()).to(context::setAutoIndexCreation);
        context.setInitialEntitySet(new EntityScanner(applicationContext).scan(Document.class));
        Class<?> strategyClass = properties.getFieldNamingStrategy();
        if (strategyClass != null) {
            context.setFieldNamingStrategy((FieldNamingStrategy) BeanUtils.instantiateClass(strategyClass));
        }
        context.setSimpleTypeHolder(conversions.getSimpleTypeHolder());
        // 手动设置applicationContext
        context.setApplicationContext(applicationContext);
        return context;
    }

    /**
     * 创建MappingMongoConverter
     */
    public MappingMongoConverter createMappingMongoConverter(MongoDatabaseFactory factory,
                                                             MongoMappingContext context,
                                                             MongoCustomConversions conversions) {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
        MappingMongoConverter mappingConverter = new MappingMongoConverter(dbRefResolver, context);
        mappingConverter.setCustomConversions(conversions);
        return mappingConverter;
    }

    /**
     * 创建MongoCustomConversions，springboot标准流程也是直接new 一个空list然后注入MongoCustomConversions
     */
    public MongoCustomConversions createMongoCustomConversions() {
        return new MongoCustomConversions(Collections.emptyList());
    }

}
