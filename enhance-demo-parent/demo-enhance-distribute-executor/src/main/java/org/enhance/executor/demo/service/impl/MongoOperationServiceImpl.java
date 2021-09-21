package org.enhance.executor.demo.service.impl;

import org.enhance.executor.demo.domain.entity.RankChangeDetailBuffer;
import org.enhance.executor.demo.service.MongoOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

/**
 * mongo操作service实现
 *
 * @author Mr_wenpan@163.com 2021/08/19 16:09
 */
@Service
public class MongoOperationServiceImpl implements MongoOperationService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public RankChangeDetailBuffer findById(String id) {
        return mongoTemplate.findById(id, RankChangeDetailBuffer.class);
    }
}
