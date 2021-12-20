package org.basis.enhance.mongo.infra.constant;

/**
 * 增强mongo基础常量
 *
 * @author Mr_wenpan@163.com 2021/12/16 21:22
 */
public interface EnhanceMongoConstant {

    /**
     * 多数据源相关常量
     */
    interface MultiSource {

        String MONGO_TEMPLATE = "MongoTemplate";

        String DEFAULT_SOURCE = "defaultSource";

        String DEFAULT_SOURCE_TEMPLATE = "defaultSourceMongoTemplate";
    }

}
