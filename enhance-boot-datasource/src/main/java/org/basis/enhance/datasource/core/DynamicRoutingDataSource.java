package org.basis.enhance.datasource.core;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.basis.enhance.datasource.exception.DynamicDataSourceException;
import org.basis.enhance.datasource.provider.DynamicDataSourceProvider;

import javax.sql.DataSource;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 核心动态数据源组件
 *
 * @author Mr_wenpan@163.com 2022/4/7 4:41 下午
 */
@Slf4j
public class DynamicRoutingDataSource extends AbstractRoutingDataSource {

    @Setter
    protected DynamicDataSourceProvider provider;
    @Setter
    protected String primary;
    /**
     * 所有数据库
     */
    private Map<String, DataSource> dataSourceMap = new LinkedHashMap<>();

    @Override
    public DataSource determineDataSource() {
        DataSource dataSource = dataSourceMap.get(DynamicDataSourceContextHolder.getDataSourceLookupKey());
        return dataSource == null ? determinePrimaryDataSource() : dataSource;
    }

    /**
     * 获取默认数据源
     *
     * @return javax.sql.DataSource 默认数据源
     * @author Mr_wenpan@163.com 2022/4/7 6:02 下午
     */
    public DataSource determinePrimaryDataSource() {

        return dataSourceMap.get(primary);
    }

    /**
     * 添加数据源
     *
     * @param ds         数据源名称
     * @param dataSource 数据源
     */
    public synchronized void addDataSource(String ds, DataSource dataSource) {
        dataSourceMap.put(ds, dataSource);
        log.info("动态数据源-加载 {} 成功", ds);
    }

    /**
     * 删除数据源
     *
     * @param ds 数据源名称
     */
    public synchronized void removeDataSource(String ds) {
        dataSourceMap.remove(ds);
        log.info("动态数据源 {} 删除成功.", ds);
    }

    public void init() {
        // 通过数据源提供器根据配置信息获取所有数据源
        Map<String, DataSource> dataSources = provider.loadDataSources();
        log.info("初始共加载 {} 个数据源", dataSources.size());
        for (Map.Entry<String, DataSource> dsItem : dataSources.entrySet()) {
            addDataSource(dsItem.getKey(), dsItem.getValue());
        }
        //检测默认数据源设置
        if (dataSourceMap.containsKey(primary)) {
            log.info("当前的默认数据源是单数据源，数据源名为 {}", primary);
        } else {
            throw new DynamicDataSourceException("请检查primary默认数据库设置");
        }
    }

}