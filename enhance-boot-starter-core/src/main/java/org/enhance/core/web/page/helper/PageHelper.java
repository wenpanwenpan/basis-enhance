package org.enhance.core.web.page.helper;

import org.enhance.core.web.page.Page;
import org.enhance.core.web.page.PageInfo;
import org.enhance.core.web.page.inf.Select;
import org.enhance.core.web.page.inf.SelectCount;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 分页助手
 *
 * @author Mr_wenpan@163.com 2022/04/15 14:41
 */
public class PageHelper {

    /**
     * 分页查询(不带count)
     *
     * @param page   第几页
     * @param size   每页大小
     * @param select 查询函数式接口
     * @return org.enhance.datasource.demo.page.Page<E>
     */
    public static <E> Page<E> doPage(int page, int size, @NonNull Select<E> select) {

        return doPage(new PageInfo(page, size), select);
    }

    /**
     * 分页查询(不带count)
     *
     * @param pageInfo 分页信息
     * @param select   查询函数式接口
     * @return org.enhance.datasource.demo.page.Page<E>
     */
    public static <E> Page<E> doPage(@NonNull PageInfo pageInfo, @NonNull Select<E> select) {

        return new Page<>(executePage(pageInfo, select), pageInfo);
    }

    /**
     * 分页查询(不带count，需要手动传入total)
     * todo 如果这里需要提供 仅通过select就完成即分页又求出count，那么需要通过mybatis拦截器，拦截SQL执行生命周期 select 执行前 改写一个count SQL
     *
     * @param pageInfo 分页信息
     * @param total    总条数（需要使用时外部传入）
     * @param select   查询函数式接口
     * @return org.enhance.datasource.demo.page.Page<E>
     */
    public static <E> Page<E> doPage(@NonNull PageInfo pageInfo, long total, @NonNull Select<E> select) {

        return new Page<>(executePage(pageInfo, select), pageInfo, total);
    }

    /**
     * 分页查询方法（带count）
     *
     * @param pageInfo    分页信息
     * @param selectCount 查询总数 函数式接口
     * @param select      分页查询函数式接口
     * @return org.enhance.datasource.demo.page.Page<E>
     */
    public static <E> Page<E> doPageAndCount(@NonNull PageInfo pageInfo,
                                             @NonNull SelectCount selectCount,
                                             @NonNull Select<E> select) {

        return new Page<>(executePage(pageInfo, select), pageInfo, selectCount.selectCount());
    }

    /**
     * 即分页又排序（需要由重写mybatis拦截器 + 自定义threadlocal实现，等后续有时间再实现）
     *
     * @param <E>      返回数据类型
     * @param pageInfo 分页信息
     * @param select   select
     * @return Page 分页对象
     */
    public static <E> Page<E> doPageAndSort(@NonNull PageInfo pageInfo, @NonNull Select<E> select) {

        throw new RuntimeException("Method not implemented.");
    }

    private static <E> List<E> executePage(PageInfo pageInfo, Select<E> select) {
        // 非法参数
        if (pageInfo.getPage() < 0 || pageInfo.getSize() < 0) {
            return new Page<>();
        }
        List<E> list = select.doSelect();
        if (CollectionUtils.isEmpty(list)) {
            return new Page<>();
        }
        return list;
    }
}
