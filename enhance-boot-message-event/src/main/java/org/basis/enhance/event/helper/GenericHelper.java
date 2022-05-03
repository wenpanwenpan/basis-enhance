package org.basis.enhance.event.helper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 泛型帮助器
 *
 * @author Mr_wenpan@163.com 2022/05/02 10:52
 */
public class GenericHelper {

    /**
     * 判断clazz继承的直接父类的的泛型是否是genericClazz
     * 如果要判断所有的父类，需要递归调用clazz.getSuperclass().getGenericSuperclass()，一直找到最顶层父类
     *
     * @param clazz        clazz
     * @param genericClazz 泛型clazz
     * @return boolean
     */
    public static boolean supperClassGeneric(Class<?> clazz, Class<?> genericClazz) {
        // 获取到直接父类的复合类型（由于Java是单继承，所以这里只有一个）
        Type genericSuperclass = clazz.getGenericSuperclass();

        if (genericSuperclass instanceof ParameterizedType) {
            // 获取复合类型的内层类型，这里是一个数组（比如：List<String,Object> 内层类型就有两个，一个是String一个是Object）
            Type[] rawType = ((ParameterizedType) genericSuperclass).getActualTypeArguments();
            for (Type type : rawType) {
                // 找到了匹配的泛型
                if (type == genericClazz) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取到继承的父类的泛型
     *
     * @param clazz clazz
     * @return java.lang.reflect.Type[] 泛型数组
     */
    public static Type[] supperClassGeneric(Class<?> clazz) {
        // 获取到直接父类的复合类型（由于Java是单继承，所以这里只有一个）
        Type genericSuperclass = clazz.getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType) {
            // 获取复合类型的内层类型，这里是一个数组（比如：List<String,Object> 内层类型就有两个，一个是String一个是Object）
            return ((ParameterizedType) genericSuperclass).getActualTypeArguments();
        }
        return null;
    }

    /**
     * 获取某个类的某个直接实现的接口中的所有泛型
     *
     * @param clazz          clazz
     * @param interfaceClazz 目标接口的clazz
     * @return java.lang.reflect.Type[]
     */
    public static Type[] interfaceClassGeneric(Class<?> clazz, Class<?> interfaceClazz) {
        // 获取到该类直接实现的接口的type
        Type[] genericInterfaces = clazz.getGenericInterfaces();
        for (Type genericInterface : genericInterfaces) {
            if (genericInterface instanceof ParameterizedType) {
                ParameterizedType parameterizedType = ((ParameterizedType) genericInterface);
                // 获取到外层类型
                Type rawType = parameterizedType.getRawType();
                // 外层类型和接口类型一致
                if (rawType == interfaceClazz) {
                    // 获取内层类型（获取该接口的所有泛型类型）
                    return parameterizedType.getActualTypeArguments();
                }
            }
        }
        return null;
    }
}
