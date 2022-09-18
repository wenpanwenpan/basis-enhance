package org.basis.enhance.groovy.annotation.condition;

import org.basis.enhance.groovy.annotation.ConditionalOnExistingProperty;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Map;

/**
 * 属性值必须和指定注解里的值相同
 *
 * @author wenpan 2022/9/18 5:55 下午
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 50)
public class OnExistingPropertyCondition extends SpringBootCondition {

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes(ConditionalOnExistingProperty.class.getName());
        if (annotationAttributes == null) {
            return ConditionOutcome.match("no @ConditionalOnExistingProperty, return match.");
        }
        // 获取注解属性值
        String property = (String) annotationAttributes.get("property");
        String value = (String) annotationAttributes.get("value");
        String configValue = context.getEnvironment().getProperty(property);
        // 注解里的value和配置文件里的value相同，则认为匹配
        if (value.equals(configValue)) {
            return ConditionOutcome.match(
                    ConditionMessage.forCondition(ConditionalOnExistingProperty.class)
                            .because("property [" + property + "] value " + configValue + "matched."));
        }
        return ConditionOutcome.noMatch(
                ConditionMessage.forCondition(ConditionalOnExistingProperty.class)
                        .because("value and configValue is not equals, not matched.")
        );
    }
}