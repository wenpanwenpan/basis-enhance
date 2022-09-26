package org.enhance.core.annotation.condition;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.enhance.core.annotation.ConditionalOnProperties;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * {@link ConditionalOnProperties} 注解condition
 * </p>
 *
 * @author wenpan 2022/09/08 19:10
 */
@Slf4j
public class ConditionalOnPropertiesCondition extends SpringBootCondition {

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        // 获取注解属性
        Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes(ConditionalOnProperties.class.getName());
        // 这里不会为空，加判空只是为了过代码扫描
        if (MapUtils.isEmpty(annotationAttributes)) {
            return ConditionOutcome.match();
        }
        String prefix = (String) annotationAttributes.get("prefix");
        String[] properties = (String[]) annotationAttributes.get("properties");
        String[] values = (String[]) annotationAttributes.get("values");
        Boolean anyMatch = (Boolean) annotationAttributes.get("anyMatch");
        Boolean matchIfMissing = (Boolean) annotationAttributes.get("matchIfMissing");

        // 配置正确性检查
        if (Objects.isNull(prefix)
                || ArrayUtils.isEmpty(properties)
                || ArrayUtils.isEmpty(values)
                || properties.length != values.length) {
            log.error("properties.length not equal values.length, properties.length is {}, values.length is {}," +
                    " properties is {}, values is {}", properties.length, values.length, properties, values);
            throw new IllegalArgumentException("ConditionalOnPropertyEnhance params is not correct.");
        }

        int matchNumber = 0;
        for (int i = 0; i < properties.length; i++) {
            String key = prefix + (prefix.endsWith(".") ? "" : ".") + properties[i];
            String propertyValue = context.getEnvironment().getProperty(key);
            if (Objects.nonNull(propertyValue) && propertyValue.equals(values[i])) {
                matchNumber++;
                // 任意匹配一个则满足条件，则直接返回
                if (anyMatch) {
                    return ConditionOutcome.match();
                }
            }
        }

        // 没有任何匹配
        if (matchNumber == 0) {
            // 如果没有任何属性匹配，并且配置了如果没有任何匹配时当作true
            return matchIfMissing ? ConditionOutcome.match("@ConditionalOnProperties condition matched.")
                    : ConditionOutcome.noMatch("@ConditionalOnProperties not matches.");
        }

        // 要求全部匹配并且确实是所有属性都匹配，则返回匹配，反之则返回不匹配
        return matchNumber == values.length ? ConditionOutcome.match("@ConditionalOnProperties condition matched.")
                : ConditionOutcome.noMatch("@ConditionalOnProperties not matches");
    }

}