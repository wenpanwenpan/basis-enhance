//package org.basis.enhance.mongo.multisource;
//
//import org.basis.enhance.mongo.infra.constant.EnhanceMongoConstant;
//import org.basis.enhance.mongo.infra.util.EnvironmentUtil;
//import org.basis.enhance.mongo.multisource.context.MongoDataSourceContext;
//import org.basis.enhance.mongo.multisource.creator.MongoClientCreator;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.FactoryBean;
//import org.springframework.beans.factory.config.BeanDefinition;
//import org.springframework.beans.factory.config.BeanDefinitionHolder;
//import org.springframework.beans.factory.support.*;
//import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
//import org.springframework.context.EnvironmentAware;
//import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
//import org.springframework.core.env.AbstractEnvironment;
//import org.springframework.core.env.Environment;
//import org.springframework.core.type.AnnotationMetadata;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.lang.NonNull;
//
//import java.util.Set;
//
///**
// * Redis数据源注册，根据 <code>spring.data.mongodb.datasource.[name]</code>
// * 配置的数据源名称注册一个 {@link org.springframework.data.mongodb.core.MongoTemplate}.
// * <p>
// * MongoTemplate 的 bean 名称为 <i>nameMongoTemplate</i>，
// * 可以通过 {@link org.springframework.beans.factory.annotation.Qualifier} 根据名称注册
// * <p>
// *
// * @author Mr_wenpan@163.com 2021/12/18 15:00
// */
//public class MonogoMultiDataSourceRegistrar implements EnvironmentAware, ImportBeanDefinitionRegistrar {
//
//    private final Logger logger = LoggerFactory.getLogger(getClass());
//
//    private Environment environment;
//
//    @Override
//    public void setEnvironment(@NonNull Environment environment) {
//        this.environment = environment;
//    }
//
//    /**
//     * 为每个mongo数据源注入BeanDefinition
//     */
//    @Override
//    public void registerBeanDefinitions(@NonNull AnnotationMetadata importingClassMetadata,
//                                        @NonNull BeanDefinitionRegistry registry,
//                                        BeanNameGenerator importBeanNameGenerator) {
//        Set<String> names = EnvironmentUtil.loadMongoDataSourceName((AbstractEnvironment) environment);
//        if (names.size() <= 0) {
//            logger.error("no mongo multi datasource config, inject multi datasource failed. please check config.");
//            return;
//        }
//
//        logger.info("register mongo datasource: {}", names);
//
//        for (String name : names) {
//
//        }
//    }
//
//    /**
//     * 注册 MongoTemplate BeanDefinition
//     */
//    protected final void registerMongoTemplateBeanDefinition(String alias, Class<?> type, BeanDefinitionRegistry registry) {
//        // BeanDefinition构建器
//        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(type);
//        // 设置通过名称注入
//        builder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_NAME);
//        builder.addConstructorArgValue(null);
//        // 设置数据源的名称(即设置bean的datasource属性的值)
//        builder.addPropertyValue(MongoDataSourceContext.FIELD_DATASOURCE_NAME, alias);
//
//        // 通过构建器获取bean的定义信息
//        BeanDefinition beanDefinition = builder.getBeanDefinition();
//        // 设置该bean为public
//        beanDefinition.setPrimary(false);
//
//        String beanName = alias + EnhanceMongoConstant.MultiSource.MONGO_TEMPLATE;
//        // 设置该bean的名称（数据源名称 + RedisTemplate）和别名（数据源名称 + -template）
//        BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, beanName, new String[]{alias + "-template"});
//        // 注册bean定义信息
//        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
//    }
//
//    /**
//     * 创建 RedisTemplate 的 FactoryBean
//     * FactoryBean一般用于构建复杂的bean
//     */
//    protected class RedisTemplateFactoryBean extends MongoDataSourceContext implements FactoryBean<Object> {
//
//        private final Logger logger = LoggerFactory.getLogger(getClass());
//
//        /**
//         * 返回要创建的bean对象
//         */
//        @Override
//        public Object getObject() throws Exception {
//            // 为该数据源创建一个Redis连接工厂，连向指定的数据源
//            MongoClientSettingsBuilderCustomizer builderCustomizers = MongoClientCreator.createMongoPropertiesCustomizer(v, environment);
//            DynamicRedisTemplateFactory<String, String> dynamicRedisTemplateFactory = getDynamicMongoTemplateFactory();
//
//            logger.info("Dynamic create a RedisTemplate named {}", getDataSourceName());
//
//            // 使用工厂类去创建RedisTemplate（该工厂为我们自定义的创建RedisTemplate的工厂类，使用自定义redis相关配置以及db）
//            // getRedisProperties()表示获取到该数据源对应的Redis配置
//            return dynamicRedisTemplateFactory.createRedisTemplate(getRedisProperties().getDatabase());
//        }
//
//        /**
//         * 返回要创建的bean的类型
//         */
//        @Override
//        public Class<?> getObjectType() {
//            return MongoTemplate.class;
//        }
//    }
//
//}
