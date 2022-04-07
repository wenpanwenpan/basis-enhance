package org.basis.enhance.datasource.config;

import org.basis.enhance.datasource.aop.DynamicRoutingAspect;
import org.basis.enhance.datasource.config.properties.DynamicDataSourceProperties;
import org.basis.enhance.datasource.core.DynamicRoutingDataSource;
import org.basis.enhance.datasource.creator.DynamicDataSourceCreator;
import org.basis.enhance.datasource.provider.DynamicDataSourceProvider;
import org.basis.enhance.datasource.provider.YmlDynamicDataSourceProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * 增强数据源自动配置
 *
 * @author Mr_wenpan@163.com 2022/02/20 11:50
 */
@Configuration
@EnableConfigurationProperties(DynamicDataSourceProperties.class)
public class EnhanceDatasourceAutoConfiguration {

    @Bean
    public DynamicRoutingAspect dynamicRoutingAspect() {

        return new DynamicRoutingAspect();
    }

    @Bean
    public DynamicDataSourceCreator dynamicDataSourceCreator(DynamicDataSourceProperties properties) {
        DynamicDataSourceCreator dynamicDataSourceCreator = new DynamicDataSourceCreator();
        dynamicDataSourceCreator.setHikariGlobalConfig(properties.getHikari());
        return dynamicDataSourceCreator;
    }

    @Bean
    @ConditionalOnMissingBean(DynamicDataSourceProvider.class)
    public DynamicDataSourceProvider ymlDynamicDataSourceProvider(DynamicDataSourceProperties properties,
                                                                  DynamicDataSourceCreator dynamicDataSourceCreator) {
        return new YmlDynamicDataSourceProvider(properties, dynamicDataSourceCreator);
    }

    @Bean
    @ConditionalOnMissingBean
    public DataSource dataSource(DynamicDataSourceProvider dynamicDataSourceProvider,
                                 DynamicDataSourceProperties properties) {
        DynamicRoutingDataSource dataSource = new DynamicRoutingDataSource();
        dataSource.setPrimary(properties.getPrimary());
        dataSource.setProvider(dynamicDataSourceProvider);
        dataSource.init();
        return dataSource;
    }
}
