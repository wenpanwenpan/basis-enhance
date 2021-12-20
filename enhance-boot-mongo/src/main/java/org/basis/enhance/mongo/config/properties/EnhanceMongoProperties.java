package org.basis.enhance.mongo.config.properties;

import org.springframework.boot.autoconfigure.mongo.MongoProperties;

/**
 * 增强Mongo properties
 *
 * @author Mr_wenpan@163.com 2021/12/18 22:10
 */
public class EnhanceMongoProperties extends MongoProperties {

    /**
     * 数据源顺序
     */
    private Integer order;

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
}
