package org.enhance.core.demo.api.controller;

import org.enhance.core.demo.api.dto.ProductQueryDTO;
import org.enhance.core.demo.infra.validgroup.AValidGroup;
import org.enhance.core.web.controller.BaseController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 测试baseController功能
 *
 * @author Mr_wenpan@163.com 2022/04/15 22:30
 */
@RestController
@RequestMapping("/test-base-controller")
public class TestBaseController extends BaseController {

    @PostMapping("/valid-obj")
    public String test(@RequestBody ProductQueryDTO queryDTO) {
        System.out.println("请求进来了，queryDTO = " + queryDTO);
        super.validObject(queryDTO, AValidGroup.class);
        return "hello " + queryDTO;
    }

    /**
     * 可以看到对于List类型的入参，jsr303并不会把它当成一个bean，所以校验失效
     */
    @PostMapping("/valid-list")
    public String testList(@RequestBody @Valid List<ProductQueryDTO> list) {
        System.out.println("请求进来了，list = " + list);
        super.validList(list);
        return "hello testList";
    }
}
