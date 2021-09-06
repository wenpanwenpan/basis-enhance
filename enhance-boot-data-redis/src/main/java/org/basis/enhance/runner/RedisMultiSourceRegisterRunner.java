package org.basis.enhance.runner;

import org.basis.enhance.helper.ApplicationContextHelper;
import org.basis.enhance.helper.DynamicRedisHelper;
import org.basis.enhance.infra.EnvironmentUtil;
import org.basis.enhance.infra.constant.EnhanceRedisConstants;
import org.basis.enhance.multisource.register.RedisDataSourceRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Redis多数据源注册runner
 * 一个数据源对应一个RedisHelper，一个redisHelper（动态）中包含着多个RedisTemplate（每个db一个RedisTemplate）
 *
 * @author Mr_wenpan@163.com 2021/09/06 10:33
 */
@Component
public class RedisMultiSourceRegisterRunner implements CommandLineRunner, EnvironmentAware {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Environment environment;

    @Override
    public void run(String... args) throws Exception {
        // 获取所有数据源名称（注意：只包含spring.redis.datasource下的数据源）
        Set<String> dataSourceNames = EnvironmentUtil.loadRedisDataSourceName((AbstractEnvironment) environment);
        if (dataSourceNames.size() < 1) {
            logger.error("no multi datasource config, register multi datasource failed. please check config.");
            return;
        }
        // 注册
        dataSourceNames.forEach(name -> {
            String realTemplateName = name + EnhanceRedisConstants.MultiSource.REDIS_TEMPLATE;
            String realHelperName = name + EnhanceRedisConstants.MultiSource.REDIS_HELPER;
            // 通过数据源名称获取bean
            RedisTemplate<String, String> redisTemplate = ApplicationContextHelper.getContext().getBean(realTemplateName, RedisTemplate.class);
            DynamicRedisHelper redisHelper = ApplicationContextHelper.getContext().getBean(realHelperName, DynamicRedisHelper.class);
            // 注册RedisTemplate
            RedisDataSourceRegister.redisterRedisTemplate(realTemplateName, redisTemplate);
            // 注册RedisHelper
            RedisDataSourceRegister.redisterRedisHelper(realHelperName, redisHelper);
        });
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
