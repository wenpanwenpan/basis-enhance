package org.basis.enhance.mybatis.page;

import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import org.basis.enhance.mybatis.annotation.Select;

import java.util.List;

/**
 * 分页助手
 *
 * @author Mr_wenpan@163.com 2022/05/03 19:49
 * @link <a>https://gitee.com/free/Mybatis_PageHelper</a>
 */
public class PageHelper extends PageMethod {

    /**
     * 分页查询
     *
     * @param page   第几页
     * @param size   每页几条数据
     * @param select select
     * @return org.enhance.core.web.page.Page<E>
     */
    public static <E> Page<E> doPage(int page, int size, Select<E> select) {
        PageMethod.startPage(page, size);
        List<E> list = select.doSelect();
        PageInfo<E> pageInfo = new PageInfo<>(list);
        // 转换为我们自己的Page对象
        return new Page<>(pageInfo);
    }

    /**
     * 分页查询
     *
     * @param page       第几页
     * @param size       每页几条数据
     * @param reasonable 是否合理化查询
     * @param select     select
     * @return org.enhance.core.web.page.Page<E>
     */
    public static <E> Page<E> doPage(int page, int size, boolean countSql, Boolean reasonable, Select<E> select) {

        return doPage(page, size, countSql, reasonable, null, select);
    }

    /**
     * 分页查询
     *
     * @param page         第几页
     * @param size         每页几条数据
     * @param reasonable   是否合理化查询
     * @param pageSizeZero pageSizeZero
     * @param select       select
     * @return org.enhance.core.web.page.Page<E>
     */
    public static <E> Page<E> doPage(int page, int size, boolean countSql, boolean reasonable, Boolean pageSizeZero, Select<E> select) {
        PageMethod.startPage(page, size, countSql, reasonable, pageSizeZero);
        List<E> list = select.doSelect();
        PageInfo<E> pageInfo = new PageInfo<>(list);
        // 转换为我们自己的Page对象
        return new Page<>(pageInfo);
    }

    /**
     * 分页查询
     *
     * @param pageRequest 分页对象
     * @param select      select
     * @return org.enhance.core.web.page.Page<E>
     */
    public static <E> Page<E> doPage(PageRequest pageRequest, Select<E> select) {
        PageMethod.startPage(pageRequest.getPage(), pageRequest.getSize(), pageRequest.isCountSql(), pageRequest.isReasonable(), null);
        List<E> list = select.doSelect();
        PageInfo<E> pageInfo = new PageInfo<>(list);
        // 转换为我们自己的Page对象
        return new Page<>(pageInfo);
    }

    /**
     * 不分页查询
     *
     * @param select select
     * @return org.enhance.core.web.page.Page<E>
     */
    private static <E> Page<E> selectAllAsOnePage(Select<E> select) {
        List<E> list = select.doSelect();
        int total = list.size();
        return new Page<>(list, new BasisPageInfo(0, total == 0 ? 1 : total), total);
    }
}
