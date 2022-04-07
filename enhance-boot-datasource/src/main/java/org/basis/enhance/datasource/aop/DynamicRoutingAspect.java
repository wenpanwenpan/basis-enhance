package org.basis.enhance.datasource.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.basis.enhance.datasource.annotation.DynamicDataSource;
import org.basis.enhance.datasource.core.DynamicDataSourceContextHolder;

/**
 * 动态路由切面
 *
 * @author Mr_wenpan@163.com 2022/04/05 17:00
 */
@Aspect
public class DynamicRoutingAspect {

    /**
     * 为标注了 RoutingWith 注解的方法创建aop切面增强
     */
    @Around("@annotation(dynamicDataSource)")
    public Object routingWithDataSource(ProceedingJoinPoint joinPoint, DynamicDataSource dynamicDataSource) throws Throwable {
        try {
            DynamicDataSourceContextHolder.setDataSourceLookupKey(dynamicDataSource.value());
            // 执行目标方法
            return joinPoint.proceed();
        } finally {
            DynamicDataSourceContextHolder.clearDataSourceLookupKey();
        }
    }
}