package org.enhance.groovy.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.basis.enhance.groovy.entity.ScriptQuery;
import org.basis.enhance.groovy.helper.RefreshScriptHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试刷新脚本
 *
 * @author wenpan 2022/10/01 19:34
 */
@Slf4j
@RestController("TestRefreshScriptController.v1")
@RequestMapping("/v1/refresh")
public class TestRefreshScriptController {

    @Autowired
    private RefreshScriptHelper refreshScriptHelper;

    /**
     * 手动刷新指定脚本到内存（以存储到MySQL为例）
     * url: http://localhost:1234/v1/refresh/test01?scriptName=customer-console_console-manager_enhance_addScript_sayHello
     */
    @GetMapping("/test01")
    public String refresh(String scriptName) {
        // 手动刷新脚本到内存
        boolean flag = refreshScriptHelper.refresh(new ScriptQuery(scriptName), true);
        System.out.println("是否刷新成功：" + flag);
        return String.valueOf(flag);
    }

    /**
     * 手动刷新所有脚本到内存
     * url: http://localhost:1234/v1/refresh/test02
     */
    @GetMapping("/test02")
    public String refreshAll() throws Exception {
        // 手动刷新所有脚本到内存
        boolean flag = refreshScriptHelper.refreshAll();
        System.out.println("是否刷新成功：" + flag);
        return String.valueOf(flag);
    }

}
