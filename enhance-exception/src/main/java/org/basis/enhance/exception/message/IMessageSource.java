package org.basis.enhance.exception.message;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Optional;

/**
 * 获取描述的默认实现（默认是从本地classpath下读取多语言信息）
 * 如果想要改变消息的获取源头，那么直接继承该接口，覆写这几个default方法即可（比如：从Redis获取消息、从MySQL获取消息等）
 *
 * @author Mr_wenpan@163.com 2021/10/9 11:57 下午
 */
public interface IMessageSource {

    Logger LOGGER = LoggerFactory.getLogger(IMessageSource.class);

    default void setParent(MessageSource messageSource) {
    }

    /**
     * 通过code解析出message
     *
     * @param parentMessageSource parentMessageSource
     * @param code                编码
     * @param args                参数
     * @param locale              语言
     * @return org.basis.enhance.spring.message.Message
     */
    default Message resolveMessage(ReloadableResourceBundleMessageSource parentMessageSource, String code, Object[] args, Locale locale) {
        return resolveMessage(parentMessageSource, code, args, null, locale);
    }

    /**
     * 通过code解析出message
     *
     * @param parentMessageSource parentMessageSource
     * @param code                编码
     * @param args                参数
     * @param defaultMessage      默认消息
     * @param locale              语言
     * @return org.basis.enhance.spring.message.Message
     */
    default Message resolveMessage(ReloadableResourceBundleMessageSource parentMessageSource, String code, Object[] args, String defaultMessage, Locale locale) {
        Message message = new Message();
        String desc = null;
        try {
            // 从classpath下通过code获取desc
            desc = parentMessageSource.getMessage(code, null, locale);
        } catch (NoSuchMessageException e) {
            LOGGER.warn("resolveMessage not found message for code={}", code);
        }
        // 如果classpath下的多语言文件里没有，但指定了默认值，则使用默认值
        if (StringUtils.isBlank(desc) && StringUtils.isNotBlank(defaultMessage)) {
            desc = defaultMessage;
        }
        // 如果有desc并且有args，则使用MessageFormat来格式化输出（即替换 {} 占位符为具体的值）
        if (StringUtils.isNotBlank(desc) && ArrayUtils.isNotEmpty(args)) {
            desc = new MessageFormat(desc, locale).format(args);
        }
        // 如果到这里了desc仍然是空的，那么直接code赋值给desc
        if (StringUtils.isBlank(desc)) {
            desc = code;
            // 处理直接throw异常而code不记录在多语言文件的的情况
            if (ArrayUtils.isNotEmpty(args)) {
                try {
                    desc = new MessageFormat(desc, locale).format(args);
                } catch (Exception ex) {
                    LOGGER.error("format desc error, desc = {}", desc);
                }
            }
        }

        String finalDesc = desc;
        // 构建message对象(这里其实就是给message对象的desc和code属性赋值)
        message = Optional.of(message).map(m -> {
            m.setCode(code);
            return m.setDesc(finalDesc);
        }).orElse(new Message(code, desc));

        LOGGER.debug("resolve message: code={}, message={}, language={}", code, message, locale);

        return message;
    }

}