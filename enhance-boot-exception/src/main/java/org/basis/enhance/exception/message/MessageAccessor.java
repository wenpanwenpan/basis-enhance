package org.basis.enhance.exception.message;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.util.List;
import java.util.Locale;

/**
 * 消息访问
 *
 * @author Mr_wenpan@163.com 2021/10/09 23:56
 */
public class MessageAccessor {

    /**
     * 消息源
     */
    private static final IMessageSource REDIS_MESSAGE_SOURCE;
    /**
     * 父消息源，spring加载多语言资源所需要的消息源类，提供了定时刷新功能，允许在不重启系统的情况下，更新资源的信息
     * 作用就是用于访问多语言文件的内容
     */
    private static final ReloadableResourceBundleMessageSource PARENT_MESSAGE_SOURCE;

    /**
     * 多语言文件
     */
    private static final List<String> basenames = ImmutableList.of(
            "classpath:messages/messages",
            "classpath:messages/messages_core",
            "classpath:messages/messages_redis",
            "classpath:messages/messages_export",
            "classpath:messages/messages_ext"
    );

    // 初始化消息源和父消息源
    static {
        PARENT_MESSAGE_SOURCE = new ReloadableResourceBundleMessageSource();
        PARENT_MESSAGE_SOURCE.setBasenames(getBasenames());
        PARENT_MESSAGE_SOURCE.setDefaultEncoding("UTF-8");

        Class clazz;
        IMessageSource redisMessageSource;
        try {
            // 优先加载Redis消息源，从Redis中取消息
            clazz = Class.forName("org.basis.enhance.exception.message.RedisMessageSource");
            redisMessageSource = (IMessageSource) clazz.newInstance();
        } catch (Exception e) {
            // 如果加载Redis消息源失败了，则加载默认消息源（从本地多语言文件中取消息）
            redisMessageSource = new IMessageSource() {
                @Override
                public void setParent(MessageSource messageSource) {
                }
            };
        }

        // 设置父消息源，为什么要设置？（因为RedisMessageSource继承了spring的AbstractMessageSource）
        redisMessageSource.setParent(PARENT_MESSAGE_SOURCE);
        REDIS_MESSAGE_SOURCE = redisMessageSource;
    }

    public static String[] getBasenames() {
        return ArrayUtils.toStringArray(basenames.toArray());
    }

    /**
     * 添加资源文件位置
     *
     * @param names 如 <code>classpath:messages/messages_core</code>
     */
    public static void addBasenames(String... names) {
        PARENT_MESSAGE_SOURCE.addBasenames(names);
        PARENT_MESSAGE_SOURCE.clearCache();
    }

    /**
     * 覆盖默认资源文件位置
     *
     * @param names 如 <code>classpath:messages/messages_core</code>
     */
    public static void setBasenames(String... names) {
        PARENT_MESSAGE_SOURCE.setBasenames(names);
        PARENT_MESSAGE_SOURCE.clearCache();
    }

    // ===========================先从Redis缓存获取，如果获取不到再从本地消息文件获取多语言消息===========================
    // 这里暂时未实现，因为需要引入Redis模块依赖，其实比较简单。后面实现
    //
    // ===========================先从Redis缓存获取，如果获取不到再从本地消息文件获取多语言消息===========================

    /**
     * 先从缓存获取多语言消息，没有则从本地消息文件获取多语言消息
     */
    public static Message getMessage(String code, Object[] args, String defaultMessage, Locale locale) {

        return REDIS_MESSAGE_SOURCE.resolveMessage(PARENT_MESSAGE_SOURCE, code, args, defaultMessage, locale);
    }

    /**
     * 先从缓存获取多语言消息，没有则从本地消息文件获取多语言消息
     */
    public static Message getMessage(String code, Object[] args, Locale locale) {

        return REDIS_MESSAGE_SOURCE.resolveMessage(PARENT_MESSAGE_SOURCE, code, args, locale);
    }

    /**
     * 先从缓存获取多语言消息，没有则从本地消息文件获取多语言消息
     */
    public static Message getMessage(String code, Object[] args) {

        return REDIS_MESSAGE_SOURCE.resolveMessage(PARENT_MESSAGE_SOURCE, code, args, LanguageHelper.locale());
    }

    /**
     * 先从缓存获取多语言消息，没有则从本地消息文件获取多语言消息
     */
    public static Message getMessage(String code) {

        return REDIS_MESSAGE_SOURCE.resolveMessage(PARENT_MESSAGE_SOURCE, code, null, LanguageHelper.locale());
    }

    // =====================================直接从本地消息文件获取多语言消息=====================================
    // PARENT_MESSAGE_SOURCE.getMessage(code, null, LanguageHelper.locale()) 由spring提供，只需要传入
    // "code + args + 语言" 便可以去本地消息文件中匹配对应的消息
    // =====================================直接从本地消息文件获取多语言消息=====================================

    /**
     * 从本地消息文件获取多语言消息
     */
    public static Message getMessageLocal(String code) {

        return new Message(code, PARENT_MESSAGE_SOURCE.getMessage(code, null, LanguageHelper.locale()));
    }

    /**
     * 从本地消息文件获取多语言消息
     */
    public static Message getMessageLocal(String code, Locale locale) {

        return new Message(code, PARENT_MESSAGE_SOURCE.getMessage(code, null, locale));
    }

    /**
     * 从本地消息文件获取多语言消息
     */
    public static Message getMessageLocal(String code, Object[] args) {

        return new Message(code, PARENT_MESSAGE_SOURCE.getMessage(code, args, LanguageHelper.locale()));
    }

    /**
     * 从本地消息文件获取多语言消息
     */
    public static Message getMessageLocal(String code, Object[] args, Locale locale) {

        return new Message(code, PARENT_MESSAGE_SOURCE.getMessage(code, args, locale));
    }

}
