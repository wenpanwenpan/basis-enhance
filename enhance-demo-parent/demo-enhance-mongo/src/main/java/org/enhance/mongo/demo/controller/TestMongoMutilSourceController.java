package org.enhance.mongo.demo.controller;

import lombok.extern.slf4j.Slf4j;
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

    /**
     * 默认数据源切换db测试
     */
    @GetMapping("/insert")
    public void insertTest() {
        MongoTemplate datasource1 = MongoDataSourceRegister.getMongoTemplate("datasource1");
        MongoTemplate datasource2 = MongoDataSourceRegister.getMongoTemplate("datasource2");
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
        MongoTemplate datasource1 = MongoDataSourceRegister.getMongoTemplate("datasource1");
        MongoTemplate datasource2 = MongoDataSourceRegister.getMongoTemplate("datasource2");
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
}
