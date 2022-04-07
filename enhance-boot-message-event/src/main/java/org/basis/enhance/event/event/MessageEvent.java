package org.basis.enhance.event.event;

/**
 * 消息事件
 *
 * @author Mr_wenpan@163.com 2021/6/28 11:31 上午
 */
public class MessageEvent<T> extends BasisEvent {

    private static final long serialVersionUID = 5165097550163084120L;

    /**
     * 业务key
     */
    private String businessCode;

    /**
     * 事件内容
     */
    private T content;

    public MessageEvent() {
    }

    public String getBusinessCode() {
        return businessCode;
    }

    public MessageEvent<T> setBusinessCode(String businessCode) {
        this.businessCode = businessCode;
        return this;
    }

    public T getContent() {
        return content;
    }

    public MessageEvent<T> setContent(T content) {
        this.content = content;
        return this;
    }
}