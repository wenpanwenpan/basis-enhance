package org.basis.enhance.mongo.config;

import org.basis.enhance.mongo.config.properties.MongoDataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * mongo增强自动配置
 *
 * @author Mr_wenpan@163.com 2021/12/16 21:18
 */
@Configuration
@ComponentScan(basePackages = {"org.basis.enhance.mongo.multisource"})
@EnableConfigurationProperties({MongoDataSourceProperties.class})
public class EnhanceMongoAutoconfiguration {


//    private final MongoProperties properties;
//
//    public EnhanceMongoAutoconfiguration(MongoProperties properties) {
//        this.properties = properties;
//    }
//
//    /**
//     * 注入MongoDB工厂
//     */
//    @Bean
//    MongoDatabaseFactorySupport<?> mongoDatabaseFactory(MongoClient mongoClient, MongoProperties properties) {
//        return new SimpleMongoClientDatabaseFactory(mongoClient, properties.getMongoClientDatabase());
//    }
//
//    @Bean
//    @ConditionalOnMissingBean(MongoOperations.class)
//    MongoTemplate mongoTemplate(MongoDatabaseFactory factory, MongoConverter converter) {
//        return new MongoTemplate(factory, converter);
//    }
//
//    @Bean
//    @ConditionalOnMissingBean(MongoConverter.class)
//    MappingMongoConverter mappingMongoConverter(MongoDatabaseFactory factory, MongoMappingContext context,
//                                                MongoCustomConversions conversions) {
//        DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
//        MappingMongoConverter mappingConverter = new MappingMongoConverter(dbRefResolver, context);
//        mappingConverter.setCustomConversions(conversions);
//        return mappingConverter;
//    }
//
//    @Bean
//    @ConditionalOnMissingBean(GridFsOperations.class)
//    GridFsTemplate gridFsTemplate(MongoDatabaseFactory factory, MongoTemplate mongoTemplate) {
//        return new GridFsTemplate(new GridFsMongoDatabaseFactory(factory, properties),
//                mongoTemplate.getConverter(), properties.getGridfs().getBucket());
//    }
//
//    /**
//     * {@link MongoDatabaseFactory} decorator to respect {@link Gridfs#getDatabase()} if
//     * set.
//     */
//    static class GridFsMongoDatabaseFactory implements MongoDatabaseFactory {
//
//        private final MongoDatabaseFactory mongoDatabaseFactory;
//
//        private final MongoProperties properties;
//
//        GridFsMongoDatabaseFactory(MongoDatabaseFactory mongoDatabaseFactory, MongoProperties properties) {
//            Assert.notNull(mongoDatabaseFactory, "MongoDatabaseFactory must not be null");
//            Assert.notNull(properties, "Properties must not be null");
//            this.mongoDatabaseFactory = mongoDatabaseFactory;
//            this.properties = properties;
//        }
//
//        @Override
//        public MongoDatabase getMongoDatabase() throws DataAccessException {
//            String gridFsDatabase = properties.getGridfs().getDatabase();
//            if (StringUtils.hasText(gridFsDatabase)) {
//                return mongoDatabaseFactory.getMongoDatabase(gridFsDatabase);
//            }
//            return mongoDatabaseFactory.getMongoDatabase();
//        }
//
//        @Override
//        public MongoDatabase getMongoDatabase(String dbName) throws DataAccessException {
//            return mongoDatabaseFactory.getMongoDatabase(dbName);
//        }
//
//        @Override
//        public PersistenceExceptionTranslator getExceptionTranslator() {
//            return mongoDatabaseFactory.getExceptionTranslator();
//        }
//
//        @Override
//        public ClientSession getSession(ClientSessionOptions options) {
//            return mongoDatabaseFactory.getSession(options);
//        }
//
//        @Override
//        public MongoDatabaseFactory withSession(ClientSession session) {
//            return mongoDatabaseFactory.withSession(session);
//        }
//
//    }

}
