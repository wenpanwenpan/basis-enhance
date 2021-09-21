package org.enhance.executor.demo.api.controller.v1;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.enhance.executor.demo.domain.entity.RankChangeDetailBuffer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * mongo联通测试
 *
 * @author Mr_wenpan@163.com 2021/08/19 16:37
 */
@Slf4j
@RestController("StoneMongoTestController.v1")
@RequestMapping("/v1/test-mongo")
public class StoneMongoTestController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping
    @ApiOperation(value = "mongo get测试")
    public RankChangeDetailBuffer get() {
        log.info("收到mongo test请求。。。");
        RankChangeDetailBuffer buffer = mongoTemplate.findById(1, RankChangeDetailBuffer.class);
        log.info("buffer = {}", buffer);
        return buffer;
    }
}
