package org.enhance.groovy.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.basis.enhance.groovy.helper.RegisterScriptHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试手动注册脚本
 *
 * @author wenpan 2022/10/01 19:30
 */
@Slf4j
@RestController("TestRegisterController.v1")
@RequestMapping("/v1/register")
public class TestRegisterController {

    @Autowired
    private RegisterScriptHelper registerScriptHelper;

    /**
     * 测试运行中手动新增脚本
     * url: http://localhost:1234/v1/register/test01?scriptName=customer-console_console-manager_enhance_addScript_sayHello&scriptContent=println "hello world."
     */
    @GetMapping("/test01")
    public String addScript(String scriptName, String scriptContent) throws Exception {
        // 手动新增脚本
        boolean flag = registerScriptHelper.registerScript(scriptName, scriptContent, false);
        System.out.println("是否增加成功：" + flag);
        return String.valueOf(flag);
    }

}
