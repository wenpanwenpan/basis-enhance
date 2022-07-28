package org.enhance.mongo.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.basis.enhance.mongo.multisource.algorithm.ConsistentHash;
import org.basis.enhance.mongo.multisource.client.MongoMultiSourceClient;
import org.basis.enhance.mongo.multisource.register.MongoDataSourceRegister;
import org.enhance.mongo.demo.entity.EnhanceDeliveryConfirm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * 测试MongoDB多数据源
 *
 * @author Mr_wenpan@163.com 2021/12/17 15:31
 */

@Slf4j
@RestController("TestMongoMutilSourceController.v1")
@RequestMapping("/v1/test-mongo")
public class TestMongoMutilSourceController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Qualifier("datasource1MongoTemplate")
    @Autowired(required = false)
    private MongoTemplate datasource1MongoTemplate;

    @Qualifier("datasource2MongoTemplate")
    @Autowired(required = false)
    private MongoTemplate datasource2MongoTemplate;

    @Autowired(required = false)
    private MongoMultiSourceClient mongoMultiSourceClient;

    @Autowired(required = false)
    ConsistentHash<MongoTemplate> mongoTemplateConsistentHash;

    /**
     * 默认数据源切换db测试
     */
    @GetMapping("/insert")
    public void insertTest() {
        MongoTemplate datasource1 = MongoDataSourceRegister.getMongoTemplate("datasource1MongoTemplate");
        MongoTemplate datasource2 = MongoDataSourceRegister.getMongoTemplate("datasource2MongoTemplate");
        EnhanceDeliveryConfirm enhanceDeliveryConfirm = new EnhanceDeliveryConfirm();
        enhanceDeliveryConfirm.setCarrierName("wenpan");
        enhanceDeliveryConfirm.setCreatedDate(new Date());
        enhanceDeliveryConfirm.setLogisticsNumber("123456");
        enhanceDeliveryConfirm.setProcessStatus("success");
        enhanceDeliveryConfirm.setScCode("datasource1");

        EnhanceDeliveryConfirm enhanceDeliveryConfirm2 = new EnhanceDeliveryConfirm();
        enhanceDeliveryConfirm2.setCarrierName("wenpanfeng");
        enhanceDeliveryConfirm2.setCreatedDate(new Date());
        enhanceDeliveryConfirm2.setLogisticsNumber("456789");
        enhanceDeliveryConfirm2.setProcessStatus("failed");
        enhanceDeliveryConfirm2.setScCode("datasource2");

        EnhanceDeliveryConfirm enhanceDeliveryConfirm3 = new EnhanceDeliveryConfirm();
        enhanceDeliveryConfirm3.setCarrierName("wenpanfeng-xxx");
        enhanceDeliveryConfirm3.setCreatedDate(new Date());
        enhanceDeliveryConfirm3.setLogisticsNumber("112233");
        enhanceDeliveryConfirm3.setProcessStatus("success");
        enhanceDeliveryConfirm3.setScCode("mongoTemplate");

        datasource1.insert(enhanceDeliveryConfirm);
        datasource2.insert(enhanceDeliveryConfirm2);
        mongoTemplate.insert(enhanceDeliveryConfirm3);


//        datasource1.save(enhanceDeliveryConfirm);
//        datasource1.save(enhanceDeliveryConfirm2);
    }

    @GetMapping("/query")
    public void query() {
        MongoTemplate datasource1 = MongoDataSourceRegister.getMongoTemplate("datasource1MongoTemplate");
        MongoTemplate datasource2 = MongoDataSourceRegister.getMongoTemplate("datasource1MongoTemplate");
        Query query = new Query();
        query.addCriteria(Criteria.where(EnhanceDeliveryConfirm.FIELD_CARRIER_NAME).is("wenpan"));
        query.with(PageRequest.of(0, 10));
        List<EnhanceDeliveryConfirm> list1 = datasource1.find(query, EnhanceDeliveryConfirm.class);
        List<EnhanceDeliveryConfirm> list2 = datasource2.find(query, EnhanceDeliveryConfirm.class);

        log.info("list1 = {}", list1);
        log.info("list2 = {}", list2);
    }

    @GetMapping("/inject")
    public void testInject() {
        Query query = new Query();
        query.addCriteria(Criteria.where(EnhanceDeliveryConfirm.FIELD_CARRIER_NAME).is("wenpan"));
        List<EnhanceDeliveryConfirm> list1 = datasource1MongoTemplate.find(query, EnhanceDeliveryConfirm.class);
        List<EnhanceDeliveryConfirm> list2 = datasource2MongoTemplate.find(query, EnhanceDeliveryConfirm.class);
        log.info("list1 = {}", list1);
        log.info("list2 = {}", list2);
    }

    @GetMapping("/hash")
    public void testHash() {
        // 通过一致性hash算法查找对应数据源的MongoTemplate
        MongoTemplate templateByHash = mongoMultiSourceClient.getMongoTemplateByHash("wenpan");
        // 获取默认的MongoDB数据源
        MongoTemplate defaultMongoTemplate = mongoMultiSourceClient.getDefaultMongoTemplate();
        // 通过数据源名称获取对应的MongoDB数据源
        MongoTemplate datasource1MongoTemplate = mongoMultiSourceClient.getMongoTemplate("datasource1MongoTemplate");
        // 通过集合 + 分片key 获取对应的MongoDB数据源
        MongoTemplate mongoTemplate = mongoMultiSourceClient.getMongoTemplate("enhance_delivery_confirm", "xxx");

        mongoTemplateConsistentHash.get("sharding_key").insert(new Object());
        // 使用对应数据源的MongoTemplate去操作MongoDB
        // 省略......
//        ShardingAlgorithmRegister.register("enhance_delivery_confirm", () -> {
//            return new ShardingAlgorithm() {
//                @Override
//                public String getTargetInstanceByValue(Object value) {
//
//                    return mongoTemplateConsistentHash.get(value);
//                }
//
//                @Override
//                public String getTargetInstanceByCollection(String collectionName) {
//                    return null;
//                }
//
//                @Override
//                public String collectionName() {
//                    return null;
//                }
//            }
//        });
        log.info("templateByHash = {}", templateByHash);
        log.info("defaultMongoTemplate = {}", defaultMongoTemplate);
        log.info("datasource1MongoTemplate = {}", datasource1MongoTemplate);
        log.info("mongoTemplate = {}", mongoTemplate);
    }
}
