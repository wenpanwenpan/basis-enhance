package org.basis.enhance.mybatis.page;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.pagehelper.PageInfo;
import lombok.Data;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 分页结果
 *
 * @author Mr_wenpan@163.com 2022/4/15 2:39 下午
 */
@Data
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public class Page<E> extends AbstractList<E> {
    /**
     * 总页数
     */
    private int totalPages;
    /**
     * 总条数
     */
    private long totalElements;
    /**
     * 当前页的数据条数
     */
    private int size;
    /**
     * 每页的条数
     */
    private int pageSize;
    /**
     * 当前页
     */
    private int pageNum;
    /**
     * 元素
     */
    private List<E> content;

    public Page() {
        content = new ArrayList<>();
    }

    /**
     * 分页封装对象构造函数
     *
     * @param content       content
     * @param basisPageInfo 分页信息
     */
    public Page(List<E> content, BasisPageInfo basisPageInfo) {
        this.content = content;
        pageNum = basisPageInfo.getPage();
        pageSize = basisPageInfo.getSize();
        size = content.size();
    }

    /**
     * 分页封装对象构造函数
     *
     * @param content       content
     * @param basisPageInfo 分页信息
     * @param total         总共数据条数
     */
    public Page(List<E> content, BasisPageInfo basisPageInfo, long total) {
        this.content = content;
        pageNum = basisPageInfo.getPage();
        pageSize = basisPageInfo.getSize();
        totalElements = total;
        totalPages = (int) (total - 1) / pageSize + 1;
        size = content.size();
    }

    /**
     * 分页封装对象构造函数
     *
     * @param pageInfo 分页信息
     */
    public Page(PageInfo<E> pageInfo) {
        content = pageInfo.getList();
        pageNum = pageInfo.getPageNum();
        pageSize = pageInfo.getPageSize();
        size = pageInfo.getSize();
        totalPages = pageInfo.getPages();
        totalElements = pageInfo.getTotal();
    }

    @Override
    public E get(int i) {
        return content.get(i);
    }

    @Override
    public int size() {
        return content.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        Page<?> page = (Page<?>) o;

        if (totalPages != page.totalPages) {
            return false;
        }
        if (totalElements != page.totalElements) {
            return false;
        }
        if (size != page.size) {
            return false;
        }
        if (pageSize != page.pageSize) {
            return false;
        }
        if (pageNum != page.pageNum) {
            return false;
        }
        return Objects.equals(content, page.content);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + totalPages;
        result = 31 * result + (int) (totalElements ^ (totalElements >>> 32));
        result = 31 * result + size;
        result = 31 * result + pageSize;
        result = 31 * result + pageNum;
        result = 31 * result + (content != null ? content.hashCode() : 0);
        return result;
    }
}