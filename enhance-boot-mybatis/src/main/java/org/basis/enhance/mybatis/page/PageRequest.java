package org.basis.enhance.mybatis.page;

import lombok.Data;

/**
 * source : {@link org.springframework.data.domain# PageRequest}
 * 分页请求封装对象
 *
 * @author wenpan
 **/
@Data
public class PageRequest {
    /**
     * 页数
     */
    private int page;
    /**
     * 每页大小
     */
    private int size;
    /**
     * 是否要执行count sql (也就是只进行分页，但是不统计数据总条数)，默认为true
     */
    private boolean countSql;
    /**
     * 是否合理化查询（页数小于0的默认查第一页，页数大于最大页数，则默认查最后一页）, 默认true
     */
    private boolean reasonable = true;

    public PageRequest() {
        countSql = true;
    }

    public PageRequest(int page, int size) {
        this.page = page;
        this.size = size;
        countSql = true;
    }

    public PageRequest(int page, int size, boolean countSql) {
        this.page = page;
        this.size = size;
        this.countSql = countSql;
    }

}