package org.basis.enhance.datasource.provider;

import lombok.extern.slf4j.Slf4j;
import org.basis.enhance.datasource.config.properties.DataSourceProperty;
import org.basis.enhance.datasource.config.properties.DynamicDataSourceProperties;
import org.basis.enhance.datasource.creator.DynamicDataSourceCreator;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * YML数据源提供者
 *
 * @author Mr_wenpan@163.com 2022/4/7 4:19 下午
 */
@Slf4j
public class YmlDynamicDataSourceProvider implements DynamicDataSourceProvider {

    /**
     * 多数据源参数
     */
    private DynamicDataSourceProperties properties;
    /**
     * 多数据源创建器
     */
    private DynamicDataSourceCreator dynamicDataSourceCreator;

    public YmlDynamicDataSourceProvider(DynamicDataSourceProperties properties, DynamicDataSourceCreator dynamicDataSourceCreator) {
        this.properties = properties;
        this.dynamicDataSourceCreator = dynamicDataSourceCreator;
    }

    @Override
    public Map<String, DataSource> loadDataSources() {
        Map<String, DataSourceProperty> dataSourcePropertiesMap = properties.getDatasource();
        if (dataSourcePropertiesMap.size() < 1) {
            throw new RuntimeException("error please check datasource config, or cancel @EnableDynamicDataSource.");
        }
        Map<String, DataSource> dataSourceMap = new HashMap<>(dataSourcePropertiesMap.size());
        for (Map.Entry<String, DataSourceProperty> item : dataSourcePropertiesMap.entrySet()) {
            String pollName = item.getKey();
            DataSourceProperty dataSourceProperty = item.getValue();
            dataSourceProperty.setPollName(pollName);
            dataSourceMap.put(pollName, dynamicDataSourceCreator.createDataSource(dataSourceProperty));
        }
        return dataSourceMap;
    }
}