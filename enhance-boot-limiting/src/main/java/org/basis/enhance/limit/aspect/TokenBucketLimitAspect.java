package org.basis.enhance.limit.aspect;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.basis.enhance.limit.annotation.TokenBucketLimit;
import org.basis.enhance.limit.exception.EnhanceRedisLimitException;
import org.basis.enhance.limit.helper.RedisLimitHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 令牌桶限流切面
 *
 * @author wenpanfeng 2021/7/4 21:46
 */
@Order(0)
@Aspect
@Component
public class TokenBucketLimitAspect {

    private static final String DOT = ".";

    @Autowired
    private RedisLimitHelper redisLimitHelper;

    /**
     * 切点
     */
    @Pointcut("@annotation(org.basis.enhance.limit.annotation.TokenBucketLimit)")
    public void aspect() {
    }

    @Around("aspect() && @annotation(limit)")
    public Object interceptor(ProceedingJoinPoint proceedingJoinPoint, TokenBucketLimit limit) throws Throwable {
        Object result;
        String limitKey;
        // 未配置限流key的情况下，以 [类名 + 方法名] 作为限流key
        if (StringUtils.isBlank(limit.limitKey())) {
            MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
            limitKey = signature.getDeclaringTypeName() + DOT + signature.getMethod().getName();
        } else {
            limitKey = limit.limitKey();
        }
        // 使用滑动时间窗口限流法
        Boolean pass = redisLimitHelper.tokenLimit(limitKey, limit.capacity(), limit.permits(), limit.rate());
        // 通过则放行目标方法
        if (pass) {
            result = proceedingJoinPoint.proceed();
        } else {
            // 这里避免并发情况下频繁打印异常堆栈消耗性能，这里抛出自定义异常！
            throw new EnhanceRedisLimitException("Congestion requested, please try again later.");
        }

        return result;
    }

}
