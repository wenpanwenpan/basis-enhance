package org.basis.enhance.executor.config;

import org.basis.enhance.executor.config.property.ExecutorProperties;
import org.basis.enhance.executor.domain.repository.TaskRedisRepository;
import org.basis.enhance.executor.domain.repository.TaskRepository;
import org.basis.enhance.executor.handler.DefaultRetryWarnHandler;
import org.basis.enhance.executor.handler.RetryWarnHandler;
import org.basis.enhance.executor.helper.ExecutorApplicationContextHelper;
import org.basis.enhance.executor.infra.client.TaskClient;
import org.basis.enhance.executor.infra.repository.impl.TaskRedisRepositoryImpl;
import org.basis.enhance.executor.infra.repository.impl.TaskRepositoryImpl;
import org.basis.enhance.executor.infra.server.context.ExecutorContext;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 分布式任务自动配置
 *
 * @author Mr_wenpan@163.com 2021/04/01 15:22
 */
@Configuration
@EnableConfigurationProperties(ExecutorProperties.class)
@ConditionalOnProperty(prefix = "stone.executor", name = {"enable"}, havingValue = "true")
public class DistributeExecutorAutoConfiguration {

    @Bean("executorApplicationHelper")
    public ExecutorApplicationContextHelper applicationContextHelper() {

        return new ExecutorApplicationContextHelper();
    }

    @Bean
    @ConditionalOnMissingBean(RetryWarnHandler.class)
    public RetryWarnHandler defaultRetryWarnHandler(ExecutorProperties executorProperties) {

        return new DefaultRetryWarnHandler(executorProperties);
    }

    @Bean
    public TaskRepository taskRepository(MongoTemplate mongoTemplate) {
        return new TaskRepositoryImpl(mongoTemplate);
    }

    @Bean
    // @ConditionalOnBean(StringRedisTemplate.class)
    public TaskRedisRepository taskRedisRepository(StringRedisTemplate stringRedisTemplate) {

        return new TaskRedisRepositoryImpl(stringRedisTemplate);
    }

    @Bean
    public TaskClient taskClient(TaskRepository taskRepository,
                                 TaskRedisRepository taskRedisRepository,
                                 ExecutorProperties executorProperties) {

        return new TaskClient(executorProperties, taskRepository, taskRedisRepository);
    }

    @Bean
    @ConditionalOnProperty(prefix = "stone.executor", name = {"group"})
    public ExecutorContext executorContext(ExecutorProperties properties,
                                           TaskRepository taskRepository,
                                           ApplicationContext applicationContext,
                                           TaskRedisRepository taskRedisRepository) {

        return new ExecutorContext(properties, taskRepository, applicationContext, taskRedisRepository);
    }

    @Bean
    @ConditionalOnProperty(prefix = "stone.executor", name = {"group"})
    public CommandLineRunner executorServerStart(ExecutorContext executorContext) {

        return args -> executorContext.start();
    }

}
