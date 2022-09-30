package org.basis.enhance.groovy.monitor;

/**
 * 监控
 *
 * @author wenpanfeng 2022/09/30 15:15
 */
public interface Monitor<T> {

    /**
     * 拉取全部数据
     *
     * @return T
     * @author wenpan 2022/9/30 15:17
     */
    T fetchWholeData();
}