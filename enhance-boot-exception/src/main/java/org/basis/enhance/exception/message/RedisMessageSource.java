package org.basis.enhance.exception.message;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Objects;

/**
 * 基于Redis的消息源
 *
 * @author Mr_wenpan@163.com 2021/10/10 9:57 下午
 */
public class RedisMessageSource extends AbstractMessageSource implements IMessageSource {

    /**
     * 覆写设置父消息源方法（父消息源可用于获取本地多语言文件消息）
     */
    @Override
    public void setParent(MessageSource messageSource) {
        setParentMessageSource(messageSource);
        setAlwaysUseMessageFormat(true);
    }

    @Override
    public Message resolveMessage(ReloadableResourceBundleMessageSource parentMessageSource, String code, Object[] args, String defaultMessage, Locale locale) {
        // 先通过code从Redis数据源获取该code对应的desc，并封装为Message
        Message message = getMessageObject(code, locale);

        String desc = null;
        if (message != null) {
            desc = message.desc();
        } else {
            try {
                // 从父数据源通过code获取desc（一般是从本地多语言文件）
                desc = Objects.requireNonNull(getParentMessageSource()).getMessage(code, null, locale);
            } catch (NoSuchMessageException e) {
                // 当通过code从本地多语言文件中没有获取到desc的时候，就抛出NoSuchMessageException
                LOGGER.warn("resolveMessage not found message for code={}", code);
            }
        }
        // 如果有默认消息则设置默认消息
        if (StringUtils.isBlank(desc) && StringUtils.isNotBlank(defaultMessage)) {
            desc = defaultMessage;
        }
        // 格式化desc(即将参数格式化到desc上)
        if (StringUtils.isNotBlank(desc) && ArrayUtils.isNotEmpty(args)) {
            desc = createMessageFormat(desc, locale).format(args);
        }
        if (StringUtils.isBlank(desc)) {
            desc = code;
        }

        message = new Message(code, desc);
        LOGGER.debug("resolve message: code={}, message={}, language={}", code, message, locale);
        return message;
    }

    /**
     * 覆写spring 的 resolveCode方法
     */
    @Override
    protected MessageFormat resolveCode(String code, Locale locale) {

        // 先通过code从Redis数据源获取该code对应的desc，并封装为Message
        Message message = getMessageObject(code, locale);

        String msg = null;
        // 如果获取到了code的desc，则msg = desc
        if (message != null) {
            msg = message.desc();
        } else {
            // 如果没有获取到code的desc，则desc = 从父消息源去本地多语言文件获取
            try {
                // 使用parent的消息源来获取code对应的desc
                msg = getParentMessageSource().getMessage(code, null, locale);
            } catch (NoSuchMessageException e) {
                LOGGER.warn("resolveCode not found message for code={}", code);
            }
        }
        if (StringUtils.isNotBlank(msg)) {
            // 创建MessageFormat，用于格式化desc
            return createMessageFormat(msg, locale);
        }
        return null;
    }

    /**
     * 获取消息对象（这里可以是通过code从Redis获取数据并封装为Message对象返回，也可以是其他途径获取）
     * todo 待实现
     *
     * @param code   编码
     * @param locale 语言
     * @return org.basis.enhance.exception.message.Message
     */
    private Message getMessageObject(String code, Locale locale) {

        // get desc by code from redis ...

        // 暂时返回为null，表示没有获取到
        return null;
    }

}