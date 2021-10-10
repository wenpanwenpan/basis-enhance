package org.basis.enhance.exception.message;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息及类型
 *
 * @author Mr_wenpan@163.com 2021/10/9 11:47 下午
 */
public class Message {

    public static final Type DEFAULT_TYPE = Type.WARN;

    private String code;

    private String desc;

    private String type;

    public Message() {
    }

    /**
     * 构建消息，默认消息类型为 警告(WARN) 基本
     */
    public Message(String code, String desc) {
        this.code = code;
        this.desc = desc;
        String[] arr = StringUtils.split(code, ".");
        if (arr != null && arr.length > 2) {
            Type t = Type.match(arr[1]);
            if (t != null) {
                type = t.code;
            }
        }
        if (StringUtils.isBlank(type)) {
            type = Message.DEFAULT_TYPE.code();
        }
    }

    public Message(String code, String desc, Type type) {
        this.code = code;
        this.desc = desc;
        this.type = type.code();
    }

    public String code() {
        return code;
    }

    public Message setCode(String code) {
        this.code = code;
        return this;
    }

    public String desc() {
        return desc;
    }

    public Message setDesc(String desc) {
        this.desc = desc;
        return this;
    }

    public String type() {
        return type;
    }

    public Message setType(String type) {
        this.type = type;
        return this;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public String getType() {
        return type;
    }

    public enum Type {
        /**
         * info级别
         */
        INFO("info"),

        /**
         * warn级别
         */
        WARN("warn"),

        /**
         * error级别
         */
        ERROR("error");

        private String code;

        static final Map<String, Type> MAP = new HashMap<>();

        // 缓存枚举变量
        static {
            for (Type value : Type.values()) {
                MAP.put(value.code, value);
            }
        }

        Type(String code) {
            this.code = code;
        }

        public String code() {
            return code;
        }

        /**
         * 通过code获取枚举变量
         */
        public static Type match(String code) {
            return MAP.get(StringUtils.lowerCase(code));
        }
    }
}
