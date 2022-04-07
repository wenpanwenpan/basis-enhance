package org.basis.enhance.datasource.config;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;

/**
 * 动态数据源导入selector
 *
 * @author Mr_wenpan@163.com 2022/04/07 11:28
 */
public class DynamicDataSourceImportSelector implements ImportSelector {

    @NonNull
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {

        return new String[]{EnhanceDatasourceAutoConfiguration.class.getName()};
    }
}
