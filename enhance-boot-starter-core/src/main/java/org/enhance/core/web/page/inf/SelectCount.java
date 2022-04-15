package org.enhance.core.web.page.inf;

/**
 * SQL查询count数量
 *
 * @author Mr_wenpan@163.com 2022/04/15 15:29
 */
@FunctionalInterface
public interface SelectCount {

    /**
     * 分页查询接口
     *
     * @return SQL执行后统计的数量
     */
    Long selectCount();
}