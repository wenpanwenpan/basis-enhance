package org.enhance.core.web.page.inf;

import java.util.List;

/**
 * 分页查询函数式接口
 *
 * @author Mr_wenpan@163.com 2022/4/15 2:42 下午
 */
@FunctionalInterface
public interface Select<E> {

    /**
     * 分页查询接口
     *
     * @return java.util.List<E> 分页查询结果
     * @author Mr_wenpan@163.com 2022/4/15 2:52 下午
     */
    List<E> doSelect();

}