package org.enhance.executor.demo.api.controller.v1;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.basis.enhance.executor.domain.repository.TaskRedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * redis联通测试
 *
 * @author Mr_wenpan@163.com 2021/08/19 16:34
 */
@Slf4j
@RestController("StoneRedisTestController.v1")
@RequestMapping("/v1/test-redis")
public class StoneRedisTestController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private TaskRedisRepository taskRedisRepository;

    /**
     * lua脚本测试
     */
    private static final DefaultRedisScript<?> TEST_SCRIPT;

    /**
     * 测试lua脚本
     */
    private static final String TEST_LUA = "script/lua/test.lua";

    static {
        TEST_SCRIPT = new DefaultRedisScript<>();
        TEST_SCRIPT.setScriptSource(new ResourceScriptSource(new ClassPathResource(TEST_LUA)));
    }

    @ApiOperation(value = "redis get测试")
    @GetMapping
    public String redisGetTest() {
        String value = stringRedisTemplate.opsForValue().get("wenpan-key");
        log.info("value={}", value);
        return value;
    }

    @ApiOperation(value = "redis set测试")
    @GetMapping("/{value}")
    public String redisSetTest(@PathVariable("value") String param) {
        stringRedisTemplate.opsForValue().set("wenpan-key", param);
        log.info("param={}", param);
        return param;
    }

    @ApiOperation(value = "redis lua测试")
    @GetMapping("/lua")
    public String redisLuaTest() {
        taskRedisRepository.luaTest();
        return "success";
    }

    @ApiOperation(value = "redis lua1测试")
    @GetMapping("/lua-1")
    public String redisLuaTest2() {
        DefaultRedisScript<?> TEST_SCRIPT = new DefaultRedisScript<>();
        TEST_SCRIPT.setScriptSource(new ResourceScriptSource(new ClassPathResource("script.lua/test.lua")));
        taskOperation(TEST_SCRIPT);
        return "success";
    }

    /**
     * 任务操作
     *
     * @param script 脚本
     * @param params 参数
     * @return 结果
     */
    private <T> T taskOperation(RedisScript<T> script, Object... params) {
        return taskOperation(script, null, params);
    }

    /**
     * 任务操作
     *
     * @param script 脚本
     * @param params 参数
     * @return 结果
     */
    private <T> T taskOperation(RedisScript<T> script, List<String> keys, Object... params) {
        return stringRedisTemplate.execute(script, keys, params);
    }

}
