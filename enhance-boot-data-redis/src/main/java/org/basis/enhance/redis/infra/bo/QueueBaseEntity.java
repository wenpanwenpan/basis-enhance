package org.basis.enhance.redis.infra.bo;

import lombok.Data;

import java.util.Date;

/**
 * 队列服务基础类型
 *
 * @author Mr_wenpan@163.com 2021/9/7 9:14 下午
 */
@Data
public class QueueBaseEntity {

    /**
     * 业务code
     */
    public String code;
    /**
     * 修改时间
     */
    public Date modifiedDate;
    /**
     * 操作类型
     */
    public String operationTypeCode;
    /**
     * 业务数据json
     */
    public String dataJson;

}