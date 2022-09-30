package org.enhance.message.spring.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.enhance.message.spring.entity.Car;
import org.enhance.message.spring.entity.MutationType;
import org.enhance.message.spring.entity.Phone;
import org.enhance.message.spring.entity.User;
import org.enhance.message.spring.event.MutationEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * spring事件测试 controller
 *
 * @author wenpanfeng 2022/08/02 17:49
 */
@Slf4j
@RestController("SpringEventTestController.v1")
@RequestMapping("/spring-event")
public class SpringEventTestController {

    @Autowired
    private ApplicationEventPublisher publisher;
    @Autowired
    private ApplicationContext applicationContext;

    @GetMapping("/publish")
    public String publish(@RequestParam("name") String name) {
        User user = new User();
        user.setName(name);
        user.setAddress("chengdi");
        user.setAge(20);
        publisher.publishEvent(new MutationEvent<>(user, new MutationType()));
        log.info("publish 消息成功.");
        return "success";
    }

    @GetMapping("/publish2")
    public String publish2(@RequestParam("name") String name) {
        Car car = new Car();
        car.setName(name);
        car.setBrand("bmw");
        publisher.publishEvent(new MutationEvent<>(car, new MutationType()));
        log.info("publish 消息成功.");
        return "success";
    }

    @GetMapping("/test")
    public String test(@RequestParam("name") String name, HttpServletRequest request) throws IOException {
        String params = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
        log.info("params = {}", params);
        return "success";
    }

    @PostMapping("/test-post")
    public String testPost(@RequestBody User user, HttpServletRequest request) throws IOException {
        String params = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
        log.info("params = {}", params);
        return "success";
    }

    @PostMapping("/test-phone")
    public String testPhone(@RequestBody Phone phone) throws IOException {
        log.info("phone = {}", phone);
        return "success";
    }

    @GetMapping("/test-phone")
    public String testPhoneGet(Phone phone) throws IOException {
        log.info("phone = {}", phone);
        return "success";
    }
}