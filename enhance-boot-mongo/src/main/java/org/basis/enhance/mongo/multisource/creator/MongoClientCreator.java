package org.basis.enhance.mongo.multisource.creator;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.apache.commons.collections4.CollectionUtils;
import org.bson.UuidRepresentation;
import org.springframework.boot.autoconfigure.mongo.MongoClientFactory;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.autoconfigure.mongo.MongoPropertiesClientSettingsBuilderCustomizer;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.SpringDataMongoDB;

import java.util.List;

/**
 * mongoClient创建器
 *
 * @author Mr_wenpan@163.com 2021/12/17 09:54
 */
public class MongoClientCreator {

    /**
     * 创建MongoClient，该MongoClient默认连接到指定的localhost的MongoDB
     */
    public static MongoClient createMongoClient() {
        MongoClientSettings mongoClientSettings = createMongoClientSettings();
        return MongoClients.create(mongoClientSettings, SpringDataMongoDB.driverInformation());
    }

    /**
     * 创建MongoClient，该MongoClient可以连接到指定的远程MongoDB
     *
     * @param builderCustomizers 用户自定义配置
     * @param settings           mongo客户端配置文件
     * @return com.mongodb.client.MongoClient
     * @author Mr_wenpan@163.com 2021/12/17 12:59 下午
     */
    public static MongoClient createMongoClient(List<MongoClientSettingsBuilderCustomizer> builderCustomizers, MongoClientSettings settings) {

        // 通MongoClientFactory去创建MongoClient，在工厂内部会优先使用builderCustomizers的配置
        return new MongoClientFactory(customizerOrderedStream(builderCustomizers)).createMongoClient(settings);
    }

    /**
     * 创建MongoClient，该MongoClient默认连接到指定的localhost的MongoDB
     */
    public static MongoClient createMongoClient(MongoClientSettings settings) {
        return MongoClients.create(settings, SpringDataMongoDB.driverInformation());
    }

    /**
     * 创建自定义MongoDB客户端配置文件
     *
     * @param properties  mongodb客户端配置文件
     * @param environment 环境对象
     * @return org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer
     * @author Mr_wenpan@163.com 2021/12/17 1:00 下午
     */
    public static MongoClientSettingsBuilderCustomizer createMongoPropertiesCustomizer(MongoProperties properties, Environment environment) {
        return new MongoPropertiesClientSettingsBuilderCustomizer(properties, environment);
    }

    /**
     * 创建mongo客户端配置文件
     */
    public static MongoClientSettings createMongoClientSettings() {
        MongoClientSettings.Builder builder = MongoClientSettings.builder();
        builder.uuidRepresentation(UuidRepresentation.JAVA_LEGACY);
        // 空方法，预留钩子
        configureClientSettings(builder);
        return builder.build();
    }

    /**
     * Configure {@link MongoClientSettings} via its {@link MongoClientSettings.Builder} API.
     *
     * @param builder never {@literal null}.
     * @since 3.0
     */
    protected static void configureClientSettings(MongoClientSettings.Builder builder) {
        // customization hook
    }

    /**
     * 自定义orderedStream实现
     */
    private static List<MongoClientSettingsBuilderCustomizer> customizerOrderedStream(List<MongoClientSettingsBuilderCustomizer> builderCustomizers) {

        if (CollectionUtils.isEmpty(builderCustomizers)) {
            return builderCustomizers;
        }
        // todo 自定义排序

        return builderCustomizers;
    }
}
