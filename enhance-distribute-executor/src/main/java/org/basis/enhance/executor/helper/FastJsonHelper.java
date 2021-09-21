package org.basis.enhance.executor.helper;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * fastjson帮助器
 *
 * @author Mr_wenpan@163.com 2021/8/4 5:43 下午
 */
public class FastJsonHelper {

    public static <T> byte[] objectToByte(T data) {
        return JSONObject.toJSONString(data).getBytes();
    }

    public static <T> String objectToString(T data) {
        return JSONObject.toJSONString(data);
    }

    public static <T> T byteToObject(byte[] data, Class<T> clz) {
        return JSONObject.parseObject(data, clz);
    }

    public static <T> T stringToObject(String data, Class<T> clz) {
        return JSONObject.parseObject(data, clz);
    }

    public static <K, V> byte[] mapToByte(Map<K, V> data) {
        return JSONObject.toJSONString(data).getBytes();
    }

    public static <K, V> String mapToString(Map<K, V> data) {
        return JSONObject.toJSONString(data);
    }

    public static <K, V> Map<K, V> byteToMap(byte[] data) {
        return JSONObject.parseObject(data, Map.class);
    }

    public static <K, V> Map<K, V> stringToMap(String data) {
        return JSONObject.parseObject(data.getBytes(), Map.class);
    }

    public static <T extends Collection> byte[] collectionToByte(T data) {
        return JSONArray.toJSONString(data).getBytes();
    }

    public static <T extends Collection> String collectionToString(T data) {
        return JSONArray.toJSONString(data);
    }

    public static <T> List<T> byteToArray(byte[] data, Class<T> clz) {
        return JSONArray.parseObject(data, clz);
    }

    public static <T> List<T> stringToArray(String data, Class<T> clz) {
        return JSONArray.parseArray(data, clz);
    }

    public static JSONObject stringToJsonObject(String data) {
        return JSONObject.parseObject(data);
    }
}