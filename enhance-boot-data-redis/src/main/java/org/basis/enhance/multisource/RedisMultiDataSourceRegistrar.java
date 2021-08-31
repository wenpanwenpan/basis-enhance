package org.basis.enhance.multisource;

import org.basis.enhance.config.DynamicRedisTemplateFactory;
import org.basis.enhance.helper.DynamicRedisHelper;
import org.basis.enhance.helper.RedisHelper;
import org.basis.enhance.template.DynamicRedisTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Redis数据源注册，根据 <code>spring.redis.datasource.[name]</code> 配置的数据源名称注册一个 {@link RedisTemplate} 和 {@link RedisHelper}.
 * <p>
 * RedisTemplate 的 bean 名称为 <i>nameRedisTemplate</i>，可以通过 {@link Qualifier} 根据名称注册
 * <p>
 * RedisHelper 的 bean 名称有两个： <i>name</i> 以及 <i>nameRedisHelper</i>，可以通过 {@link RedisDataSource} 注入
 *
 * @author wenpanfeng
 */
public class RedisMultiDataSourceRegistrar implements EnvironmentAware, ImportBeanDefinitionRegistrar {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Environment environment;

    private static final String REDIS_TEMPLATE = "RedisTemplate";

    private static final String REDIS_HELPER = "RedisHelper";

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {

        Set<String> names = loadRedisDataSourceName();

        if (names.size() <= 0) {
            logger.error("no multi datasource config, inject multi datasource failed. please check config.");
            return;
        }

        logger.info("register redis datasource: {}", names);

        for (String name : names) {
            // 注册 RedisTemplate BeanDefinition
            registerRedisTemplateBeanDefinition(name, RedisTemplateFactoryBean.class, registry);

            // 注册 RedisHelper BeanDefinition
            registerRedisHelperBeanDefinition(name, RedisHelperFactoryBean.class, registry);
        }
    }

    /**
     * 注册 RedisTemplate BeanDefinition
     */
    protected final void registerRedisTemplateBeanDefinition(String alias, Class<?> type, BeanDefinitionRegistry registry) {
        // BeanDefinition构建器
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(type);
        // 设置通过名称注入
        builder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_NAME);
        builder.addConstructorArgValue(null);
        // 设置数据源的名称(即设置bean的datasource属性的值)
        builder.addPropertyValue(RedisDataSourceContext.FIELD_DATASOURCE_NAME, alias);

        // 通过构建器获取bean的定义信息
        BeanDefinition beanDefinition = builder.getBeanDefinition();
        // 设置该bean为public
        beanDefinition.setPrimary(false);

        String beanName = alias + REDIS_TEMPLATE;
        // 设置该bean的名称（数据源名称 + RedisTemplate）和别名（数据源名称 + -template）
        BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, beanName, new String[]{alias + "-template"});
        // 注册bean定义信息
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
    }

    /**
     * 注册 RedisHelper BeanDefinition
     */
    protected final void registerRedisHelperBeanDefinition(String alias, Class<?> type, BeanDefinitionRegistry registry) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(type);
        builder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_NAME);
        builder.addConstructorArgValue(null);
        builder.addPropertyValue(RedisDataSourceContext.FIELD_DATASOURCE_NAME, alias);

        BeanDefinition beanDefinition = builder.getBeanDefinition();
        beanDefinition.setPrimary(false);
        beanDefinition.setDependsOn(alias + REDIS_TEMPLATE);

        String beanName = alias + REDIS_HELPER;
        // 设置该bean的名称（数据源名称 + RedisHelper）和别名（数据源名称 + -helper）
        BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, beanName, new String[]{alias, alias + "-helper"});
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
    }

    /**
     * 从环境信息Environment中解析出数据源的名称
     *
     * @return 数据源名称
     */
    protected Set<String> loadRedisDataSourceName() {
        MutablePropertySources propertySources = ((AbstractEnvironment) environment).getPropertySources();
        Set<String> configs = StreamSupport.stream(propertySources.spliterator(), false)
                .filter(ps -> ps instanceof EnumerablePropertySource)
                .map(ps -> ((EnumerablePropertySource) ps).getPropertyNames())
                .flatMap(Arrays::stream)
                .filter(propName -> propName.startsWith("spring.redis.datasource."))
                .collect(Collectors.toSet());

        if (configs.size() > 0) {
            return configs.stream().map(item -> item.split("\\.")[3]).collect(Collectors.toSet());
        }

        return Collections.emptySet();
    }

    /**
     * 创建 RedisHelper 的 FactoryBean
     */
    protected class RedisHelperFactoryBean extends RedisDataSourceContext implements FactoryBean<Object> {
        private final Logger logger = LoggerFactory.getLogger(getClass());

        @Override
        public Object getObject() throws Exception {
            // 为该数据源创建一个Redis连接工厂
            DynamicRedisTemplateFactory<String, String> dynamicRedisTemplateFactory = getDynamicRedisTemplateFactory();

            RedisTemplate<String, String> redisTemplate = applicationContext.getBean(dataSourceName + "RedisTemplate", RedisTemplate.class);

            DynamicRedisTemplate<String, String> dynamicRedisTemplate = new DynamicRedisTemplate<>(dynamicRedisTemplateFactory);
            dynamicRedisTemplate.setDefaultRedisTemplate(redisTemplate);
            Map<Object, RedisTemplate<String, String>> redisTemplateMap = new HashMap<>(8);
            redisTemplateMap.put(getRedisProperties().getDatabase(), redisTemplate);
            dynamicRedisTemplate.setRedisTemplates(redisTemplateMap);

            logger.info("Dynamic create a RedisHelper named {}", getDataSourceName());

            return new DynamicRedisHelper(dynamicRedisTemplate);
        }

        @Override
        public Class<?> getObjectType() {
            return RedisHelper.class;
        }
    }

    /**
     * 创建 RedisTemplate 的 FactoryBean
     * FactoryBean一般用于构建复杂的bean
     */
    protected class RedisTemplateFactoryBean extends RedisDataSourceContext implements FactoryBean<Object> {
        private final Logger logger = LoggerFactory.getLogger(getClass());

        /**
         * 返回要创建的bean对象
         */
        @Override
        public Object getObject() throws Exception {
            // 为该数据源创建一个Redis连接工厂
            DynamicRedisTemplateFactory<String, String> dynamicRedisTemplateFactory = getDynamicRedisTemplateFactory();

            logger.info("Dynamic create a RedisTemplate named {}", getDataSourceName());

            return dynamicRedisTemplateFactory.createRedisTemplate(getRedisProperties().getDatabase());
        }

        /**
         * 返回要创建的bean的类型
         */
        @Override
        public Class<?> getObjectType() {
            return RedisTemplate.class;
        }
    }

}