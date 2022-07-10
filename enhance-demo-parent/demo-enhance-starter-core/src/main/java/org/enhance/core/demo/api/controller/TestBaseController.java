package org.enhance.core.demo.api.controller;

import org.enhance.core.demo.api.dto.ProductQueryDTO;
import org.enhance.core.demo.infra.validgroup.AValidGroup;
import org.enhance.core.web.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Enumeration;
import java.util.List;

/**
 * 测试baseController功能
 * 要使得jsr303校验生效，必须在接口上配合@Valid注解一起使用，否则不生效
 * 比较推荐手动调用validObject的方法来进行校验，直接使用springmvc提供的校验不会抛出异常（控制台只会打印校验不通过的字段），无法捕获异常
 *
 * @author Mr_wenpan@163.com 2022/04/15 22:30
 */
@RestController
@RequestMapping("/test-base-controller")
public class TestBaseController extends BaseController {

    @Autowired
    private HttpServletRequest httpServletRequest;

    /**
     * 测试普通对象校验
     */
    @PostMapping("/valid-base-obj")
    public String testBaseObj(@RequestBody ProductQueryDTO queryDTO) {
        System.out.println("请求进来了，queryDTO = " + queryDTO);
        super.validObject(queryDTO);
        return "valid-base-obj " + queryDTO;
    }

    /**
     * 测试jsr303分组校验
     */
    @PostMapping("/valid-group")
    public String testGroupValid(@RequestBody ProductQueryDTO queryDTO) {
        super.validObject(queryDTO, AValidGroup.class);
        return "valid-group " + queryDTO;
    }

    /**
     * 测试jsr303 对list入参的校验：可以看到对于List类型的入参，jsr303并不会把它当成一个bean，所以校验失效
     */
    @PostMapping("/valid-list")
    public String testList(@RequestBody @Valid List<ProductQueryDTO> list) {
        System.out.println("请求进来了，list = " + list);
        super.validList(list);
        return "hello testList";
    }

    @GetMapping("/test")
    public String test() {
        String requestURI = httpServletRequest.getRequestURI();
        Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
        System.out.println(requestURI);
        while (headerNames.hasMoreElements()) {
            System.out.println(headerNames.nextElement());
        }
        System.out.println("=======================================================");


        return "success";
    }
}
