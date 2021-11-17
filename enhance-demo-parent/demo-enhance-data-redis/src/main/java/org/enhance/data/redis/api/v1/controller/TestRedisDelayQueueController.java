package org.enhance.data.redis.api.v1.controller;

import com.alibaba.fastjson.JSONObject;
import org.basis.enhance.redis.delayqueue.RedisDelayQueueManager;
import org.enhance.data.redis.domain.entity.Customer;
import org.enhance.data.redis.domain.entity.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

/**
 * 测试基于Redis的延时队列
 *
 * @author Mr_wenpan@163.com 2021/11/16 10:34
 */

@RestController("redisDelayedQueueController.v1")
@RequestMapping("/v1/delayed-queue")
public class TestRedisDelayQueueController {

    @PostMapping("/add-with-time")
    public ResponseEntity<?> addWithTime(@RequestParam("queue") String queue,
                                         @RequestParam("delayed") Integer delayed,
                                         @RequestBody Order data) {
        RedisDelayQueueManager.offer(queue, data, delayed, TimeUnit.SECONDS);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/add-with-time1")
    public ResponseEntity<?> addWithTime1(@RequestParam("queue") String queue,
                                          @RequestParam("delayed") Integer delayed,
                                          @RequestBody Customer data) {
        RedisDelayQueueManager.offer(queue, data, delayed, TimeUnit.SECONDS);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/add-with-time2")
    public ResponseEntity<?> addWithTime2(@RequestParam("queue") String queue,
                                          @RequestParam("delayed") Integer delayed,
                                          @RequestBody Order data) {
        // 按字符串存储到Redis
        RedisDelayQueueManager.offer(queue, JSONObject.toJSONString(data), delayed, TimeUnit.SECONDS);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
