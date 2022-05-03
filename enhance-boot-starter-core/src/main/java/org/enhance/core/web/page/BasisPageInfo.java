package org.enhance.core.web.page;

import lombok.Data;

import java.io.Serializable;

/**
 * 分页信息
 *
 * @author Mr_wenpan@163.com 2022/4/15 2:39 下午
 */
@Data
public class BasisPageInfo implements Serializable {

    /**
     * 第几页
     */
    private int page;
    /**
     * 每页的大小
     */
    private int size;
    /**
     * 起始偏移量
     */
    private int begin;
    /**
     * 结束偏移量
     */
    private int end;

    /**
     * 分页信息类构造函数
     *
     * @param page page
     * @param size size
     */
    public BasisPageInfo(int page, int size) {
        this.page = page;
        this.size = size;
        begin = page * size;
        // 注意：这里的end指的是从begin开始向后取多少条
        end = size;
    }

}