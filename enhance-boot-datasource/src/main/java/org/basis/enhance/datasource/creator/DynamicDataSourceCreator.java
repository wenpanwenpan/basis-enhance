package org.basis.enhance.datasource.creator;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.basis.enhance.datasource.config.properties.DataSourceProperty;
import org.basis.enhance.datasource.config.properties.HikariCpConfig;

import javax.sql.DataSource;

/**
 * 数据源创建器
 *
 * @author TaoYu
 * @since 2.3.0
 */
@Slf4j
public class DynamicDataSourceCreator {

    /**
     * DRUID数据源类
     */
    private static final String DRUID_DATASOURCE = "com.alibaba.druid.pool.DruidDataSource";
    /**
     * HikariCp数据源
     */
    private static final String HIKARI_DATASOURCE = "com.zaxxer.hikari.HikariDataSource";
    /**
     * 是否存在druid
     */
    private Boolean druidExists = false;
    /**
     * 是否存在hikari
     */
    private Boolean hikariExists = false;

    @Setter
    private HikariCpConfig hikariGlobalConfig;

    public DynamicDataSourceCreator() {
        try {
            Class.forName(DRUID_DATASOURCE);
            druidExists = true;
        } catch (ClassNotFoundException e) {
            // do nothing
        }
        try {
            Class.forName(HIKARI_DATASOURCE);
            hikariExists = true;
        } catch (ClassNotFoundException e) {
            // do nothing
        }
    }

    /**
     * 创建数据源
     *
     * @param dataSourceProperty 数据源信息
     * @return 数据源
     */
    public DataSource createDataSource(DataSourceProperty dataSourceProperty) {
        Class<? extends DataSource> type = dataSourceProperty.getType();
        // 未指定数据源类型
        if (type == null) {
            // 如果没有指定数据源类型，那么优先使用hikari
            if (hikariExists) {
                return createHikariDataSource(dataSourceProperty);
            } else if (druidExists) {
                return createDruidDataSource(dataSourceProperty);
            } else {
                throw new RuntimeException("create datasource occur error.");
            }
        } else if (DRUID_DATASOURCE.equals(type.getName())) {
            return createDruidDataSource(dataSourceProperty);
        } else if (HIKARI_DATASOURCE.equals(type.getName())) {
            return createHikariDataSource(dataSourceProperty);
        } else {
            throw new RuntimeException("create datasource occur error.");
        }
    }

    /**
     * 创建Druid数据源
     *
     * @param dataSourceProperty 数据源属性Property
     * @return javax.sql.DataSource druid数据源
     * @author Mr_wenpan@163.com 2022/4/7 5:10 下午
     */
    public DataSource createDruidDataSource(DataSourceProperty dataSourceProperty) {

        // todo 待实现
        return null;
    }

    /**
     * 创建Hikari数据源
     *
     * @param dataSourceProperty 数据源参数
     * @return 数据源
     * @author wenpan
     */
    public DataSource createHikariDataSource(DataSourceProperty dataSourceProperty) {
        HikariCpConfig hikariCpConfig = dataSourceProperty.getHikari();
        HikariConfig config = hikariCpConfig.toHikariConfig(hikariGlobalConfig);
        config.setUsername(dataSourceProperty.getUsername());
        config.setPassword(dataSourceProperty.getPassword());
        config.setJdbcUrl(dataSourceProperty.getUrl());
        config.setDriverClassName(dataSourceProperty.getDriverClassName());
        config.setPoolName(dataSourceProperty.getPollName());
        return new HikariDataSource(config);
    }
}
