package org.enhance.executor.demo.infra.constant;

/**
 * 分布式任务执行器测试常量
 *
 * @author Mr_wenpan@163.com 2021/08/19 16:13
 */
public interface StoneExecutorTestConstants {

    /**
     * MongoDB 数据
     */
    interface Mongo {
        /**
         * 最新一条loyalty数据集合名
         */
        String LATEST_LOYALTY_COLLECTION = "o2vip_latest_customer_loyalty";
        /**
         * loyalty百分比计算 job分割数据id范围
         */
        String LOYALTY_DATA_LIMIT_COLLECTION = "o2vip_loyalty_data_limit_collection";
        /**
         * 前台职级履历更新EC层
         */
        String RANK_CHANGE_DETAIL_BUFFER = "o2vip_rank_change_detail_buffer";
    }
}
