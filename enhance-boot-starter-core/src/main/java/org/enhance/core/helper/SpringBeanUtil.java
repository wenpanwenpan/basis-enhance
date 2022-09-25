package org.enhance.core.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractRefreshableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.lang.NonNull;

/***
 * 操作spring容器工具类
 *
 * @author wenpan 2022/9/24 5:11 下午
 */
public class SpringBeanUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringBeanUtil.class);

    /**
     * <ol>
     *     注册单例bean
     *     <li>此种方式注入的单例bean可以通过{@code ApplicationContext#getBean} 获取到单例bean，
     *     但是不能通过{@code beanDefinitionMap} 来获取bean的定义信息，所以如果直接调用
     *     {@link DefaultListableBeanFactory#removeBeanDefinition(String)} 来
     *     移除容器的单例bean时会抛出异常 {@link org.springframework.beans.factory.NoSuchBeanDefinitionException}</li>
     * </ol>
     *
     * @param ctx             ctx
     * @param beanName        beanName 不可与容器里已存在的bean名称重复，否则抛出异常 {@link IllegalStateException}
     * @param singletonObject 单例bean对象
     * @return java.lang.Object 注册后的bean
     */
    public static Object dynamicRegisterSingletonBean(@NonNull ApplicationContext ctx,
                                                      @NonNull String beanName,
                                                      @NonNull Object singletonObject) {
        // 获取到容器
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) ctx.getAutowireCapableBeanFactory();
        // 直接向容器里注入单例bean，如果容器里已经存在名称为beanName的bean，则不允许重复注入，spring会直接抛出异常 IllegalStateException
        beanFactory.registerSingleton(beanName, singletonObject);
        // 返回注册成功的bean
        return ctx.getBean(beanName);
    }

    /**
     * <ol>
     *     根据beanClass动态添加bean到容器（推荐使用此种方式）
     *     <li>此种方式添加的bean，在容器中既可以获取到BeanDefinition又可以获取到bean对象</li>
     *     <li>如果容器中有重复的bean（beanName相同），则由follow应用的配置决定是否需要
     *     覆盖容器中的bean（spring.main.allow-bean-definition-overriding）</li>
     * </ol>
     *
     * @param ctx             ctx
     * @param beanName        beanName 不可与容器里已存在的bean名称重复，否则返回false ,代表注入失败
     * @param beanClass       beanClass
     * @param constructValues constructValues
     */
    public static void dynamicRegisterSingleBean(@NonNull ApplicationContext ctx,
                                                 @NonNull String beanName,
                                                 @NonNull Class<?> beanClass, Object... constructValues) {
        BeanDefinitionRegistry registry;
        // 获取bean工厂（不同类型的容器获取bean工厂的方式不同）
        if (ctx instanceof AbstractRefreshableApplicationContext) {
            registry = (DefaultListableBeanFactory) ((AbstractRefreshableApplicationContext) ctx).getBeanFactory();
        } else if (ctx instanceof GenericApplicationContext) {
            registry = (DefaultListableBeanFactory) ((GenericApplicationContext) ctx).getBeanFactory();
        } else {
            throw new IllegalArgumentException("nonsupport ApplicationContext.");
        }
        // 生成bean的定义信息的构建器
        BeanDefinitionBuilder beanDefBuilder = BeanDefinitionBuilder.genericBeanDefinition(beanClass);
        // 构造函数赋值
        for (Object constructValue : constructValues) {
            beanDefBuilder.addConstructorArgValue(constructValue);
        }
        // 生成bean的定义信息
        BeanDefinition beanDefinition = beanDefBuilder.getBeanDefinition();
        // 注册bean到容器
        registry.registerBeanDefinition(beanName, beanDefinition);
    }

    /**
     * <p>
     * 按beanName从容器里移除bean
     * 这里需要catch xx异常，因为{@link SpringBeanUtil#dynamicRegisterSingletonBean(ApplicationContext, String, Object)}
     * 注入的单例bean是不会在 {@link DefaultListableBeanFactory#beanDefinitionMap} 中存在的，所以当remove的时候会抛出异常
     * {@link NoSuchBeanDefinitionException}
     * </p>
     *
     * @param ctx      ctx
     * @param beanName beanName
     */
    public static void removeBean(@NonNull ApplicationContext ctx, @NonNull String beanName) {
        DefaultListableBeanFactory springFactory = (DefaultListableBeanFactory) ((GenericApplicationContext) ctx).getBeanFactory();
        try {
            // 销毁BeanDefinition和单例池里的bean
            springFactory.removeBeanDefinition(beanName);
        } catch (NoSuchBeanDefinitionException ex) {
            LOGGER.error("removeBean failed, because there is no NoSuchBeanDefinition, beanName is : [{}]", beanName, ex);
            // 直接销毁单例池里的bean
            springFactory.destroySingleton(beanName);
        }
    }

}