package org.basis.enhance.limit.selector;

import org.basis.enhance.limit.config.RedisLimitingAutoConfiguration;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;

/**
 * redis限流组件导入selector
 *
 * @author wenpanfeng 2021/07/05 15:45
 */
public class RedisLimitingImportSelector implements ImportSelector {

    @NonNull
    @Override
    public String[] selectImports(@NonNull AnnotationMetadata annotationMetadata) {

        return new String[]{RedisLimitingAutoConfiguration.class.getName()};
    }
}