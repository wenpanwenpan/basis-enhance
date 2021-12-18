package org.basis.enhance.mongo.infra;

import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @Data: 2019/12/18
 * @Des: Mongo 数据源配置
 */
public class MongoDB {
    private MongoTemplate mongoTemplate;
    private GridFsTemplate gridfsTemplate;
    private MongoDbFactory mongodbFactory;
    private TransactionTemplate transactionTemplate;

    public MongoDB(MongoTemplate mongoTemplate,
                   GridFsTemplate gridfsTemplate,
                   MongoDbFactory mongodbFactory,
                   TransactionTemplate transactionTemplate) {
        this.mongoTemplate = mongoTemplate;
        this.gridfsTemplate = gridfsTemplate;
        this.mongodbFactory = mongodbFactory;
        this.transactionTemplate = transactionTemplate;
    }

    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    public GridFsTemplate getGridfsTemplate() {
        return gridfsTemplate;
    }

    public MongoDbFactory getMongodbFactory() {
        return mongodbFactory;
    }

    public TransactionTemplate getTransactionTemplate() {
        return transactionTemplate;
    }
}
