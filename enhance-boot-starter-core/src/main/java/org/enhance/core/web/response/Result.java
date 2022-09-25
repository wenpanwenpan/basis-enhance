package org.enhance.core.web.response;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.enhance.core.constants.BaseConstants;

import java.util.HashMap;

/**
 * <p>
 * 统一响应格式返回工具类，可实现高度扩展化
 * </p>
 *
 * @author wenpan 2022/08/11 10:59
 */
public class Result extends HashMap<String, Object> {

    private static final long serialVersionUID = 1L;

    /**
     * 默认成功描述success
     */
    public static final String SUCCESS = "success";
    /**
     * message属性
     */
    public static final String FILED_MESSAGE = "message";
    /**
     * code属性
     */
    public static final String FILED_CODE = "code";
    /**
     * body属性
     */
    public static final String FILED_BODY = "body";

    public Result() {

    }

    /**
     * 自定义封装数据body
     */
    public Result setBody(Object body) {
        return put(FILED_BODY, body);
    }

    /**
     * 解析数据
     * 1.@ResponseBody返回类型被封装成了Json格式
     * 2.feign接收参数时也会封装成json格式，data对象也被解析成json格式的数据（[集合对象]或{map对象}）
     * 3.将data转成json字符串格式，然后再解析成对象
     */
    public <T> T getBody(TypeReference<T> type) {
        Object body = get(FILED_BODY);
        String jsonString = JSONObject.toJSONStringWithDateFormat(body, BaseConstants.Pattern.DATETIME);
        return JSONObject.parseObject(jsonString, type);
    }

    /**
     * 获取任意key的value，并转换位自定义的类型
     */
    public <T> T get(String key, TypeReference<T> type) {
        Object body = get(key);
        String jsonString = JSONObject.toJSONStringWithDateFormat(body, BaseConstants.Pattern.DATETIME);
        return JSONObject.parseObject(jsonString, type);
    }

    /**
     * 默认请求失败方法
     * <p>
     * 状态码：500
     * 失败message：未知异常，请联系管理员
     * </p>
     */
    public static Result error() {
        return error(500, "未知异常，请联系管理员");
    }

    /**
     * 默认请求失败方法
     * <p>
     * 状态码：500
     * 失败message：自定义
     * </p>
     */
    public static Result error(String message) {
        return error(500, message);
    }

    /**
     * 默认请求失败方法
     * <p>
     * 状态码：自定义
     * 失败message：自定义
     * </p>
     */
    public static Result error(int code, String message) {
        Result result = new Result();
        result.put(FILED_CODE, code);
        result.put(FILED_MESSAGE, message);
        result.put(SUCCESS, false);
        return result;
    }

    /**
     * 默认成功方法
     * <p>
     * 状态码：200
     * message：自定义
     * </p>
     *
     * @param data 业务数据
     * @author wenpanfeng 2022/08/11 10:59
     */
    public static Result ok(Object data) {
        Result result = new Result();
        result.put(FILED_CODE, 200);
        result.put(FILED_MESSAGE, SUCCESS);
        result.put(SUCCESS, true);
        result.put(FILED_BODY, data);
        return result;
    }

    /**
     * 默认成功方法
     * <p>
     * 状态码：200
     * message：success
     * </p>
     */
    public static Result ok() {
        Result result = new Result();
        result.put(FILED_CODE, 200);
        result.put(FILED_MESSAGE, SUCCESS);
        result.put(SUCCESS, true);
        return result;
    }

    /**
     * 自定义往返回结果里添加属性
     */
    @Override
    public Result put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    /**
     * 获取执行状态
     */
    public Integer getCode() {
        return (Integer) get(FILED_CODE);
    }

    /**
     * 是否成功
     */
    public boolean isSuccess() {
        assert getCode() != null;
        return 200 == getCode();
    }

    public String getMessage() {
        return (String) get(FILED_MESSAGE);
    }

}