package org.enhance.executor.demo.service;


import org.enhance.executor.demo.domain.entity.RankChangeDetailBuffer;

/**
 * Mongo操作service
 *
 * @author Mr_wenpan@163.com 2021/08/19 16:08
 */
public interface MongoOperationService {

    /**
     * 按ID查找
     */
    RankChangeDetailBuffer findById(String id);
}
