package org.basis.enhance.event.listener;

import org.basis.enhance.event.event.DefaultMessageEvent;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * 默认消息监听器实现
 *
 * @author Mr_wenpan@163.com 2022/04/01 18:06
 */
public class DefaultApplicationListener implements ApplicationListener<DefaultMessageEvent> {

    @Override
    public void onApplicationEvent(DefaultMessageEvent event) {
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
        System.out.println(Thread.currentThread().getName() + "---->" + getClass().getName() + " 执行消息啦，event = " + event);
    }

    /**
     * 支持的事件类型
     *
     * @return java.lang.Class<DefaultMessageEvent> 事件类型
     * @author Mr_wenpan@163.com 2022/4/1 7:17 下午
     */
    @Override
    public Class<DefaultMessageEvent> supportEventType() {
        return DefaultMessageEvent.class;
    }
}
