package org.basis.enhance.event.event;

/**
 * 默认事件
 *
 * @author Mr_wenpan@163.com 2022/04/01 18:06
 */
public class DefaultMessageEvent extends MessageEvent<String> implements Comparable<String> {

    @Override
    public int compareTo(String o) {
        return 0;
    }
}
