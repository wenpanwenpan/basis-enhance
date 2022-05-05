package org.basis.enhance.event.test;

import org.basis.enhance.event.event.DefaultMessageEvent;
import org.basis.enhance.event.event.MessageEvent;
import org.springframework.core.GenericTypeResolver;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 泛型测试
 *
 * @author Mr_wenpan@163.com 2022/05/04 15:39
 */
public class GenericTest {

    /**
     * 测试泛型
     */
    public void test(DefaultMessageEvent event) {
        Class<? extends DefaultMessageEvent> eventClass = event.getClass();
        // 获取到该类所实现接口的复合类型
        Type[] genericInterfaces = eventClass.getGenericInterfaces();
        // 获取到父类的复合类型
        Type genericSuperclass = eventClass.getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType) {
            // 获取内层类型
            Type[] rawType = ((ParameterizedType) genericSuperclass).getActualTypeArguments();
            if (rawType.length == 1 && rawType[0] == String.class) {
                System.out.println("=========>>>>>rawType = " + rawType[0]);
            }
        }
        for (Type genericInterface : genericInterfaces) {
            // 复合类型
            if (genericInterface instanceof ParameterizedType) {
                ParameterizedType parameterizedType = ((ParameterizedType) genericInterface);
                // 获取到外层类型
                Type rawType = parameterizedType.getRawType();
                System.out.println("rawType = " + rawType);
                // 获取内层类型
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                System.out.println("parameterizedType = " + parameterizedType);
                Type xxx = ((ParameterizedType) genericInterface).getRawType();
                System.out.println("xxx === " + xxx);
                if (actualTypeArguments.length == 1 && actualTypeArguments[0] == String.class) {
                    System.out.println("=========>>>>>actualTypeArguments = " + Arrays.toString(actualTypeArguments));
                }
            }
        }
    }


    /**
     * 使用spring提供的泛型工具类
     */
    public static void test02() {
        // 获取DefaultMessageEvent类所继承的父类MessageEvent，并且指定的泛型类型
        Class<?> typeArgument = GenericTypeResolver.resolveTypeArgument(DefaultMessageEvent.class, MessageEvent.class);
        System.out.println(typeArgument);
        HashMap<String, String> hashMap = new HashMap<>();
        Class<?>[] classes = GenericTypeResolver.resolveTypeArguments(HashMap.class, Map.class);
        System.out.println(Arrays.toString(classes));
    }

    public static void main(String[] args) {
        test02();
    }
}
