package org.enhance.core.web.page;

import java.io.Serializable;

/**
 * 分页信息
 *
 * @author Mr_wenpan@163.com 2022/4/15 2:39 下午
 */
public class PageInfo implements Serializable {

    private int page;

    private int size;

    private int begin;

    private int end;

    private long total;

    private int pages;

    /**
     * 分页信息类构造函数
     *
     * @param page page
     * @param size size
     */
    public PageInfo(int page, int size) {
        this.page = page;
        this.size = size;
        begin = page * size;
        end = begin + size;
    }

    public int getBegin() {
        return begin;
    }

    public void setBegin(int begin) {
        this.begin = begin;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

}