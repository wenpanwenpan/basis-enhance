package org.basis.groovy.helper;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.basis.enhance.groovy.entity.ScriptEntry;
import org.basis.enhance.groovy.entity.ScriptQuery;
import org.basis.enhance.groovy.helper.RegisterScriptHelper;
import org.basis.enhance.groovy.loader.ScriptLoader;
import org.basis.enhance.groovy.registry.ScriptRegistry;
import org.basis.groovy.config.properties.GroovyRedisLoaderProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.NonNull;

import java.util.Map;

/**
 * 手动注册脚本助手
 *
 * @author wenpan 2022/09/30 21:09
 */
@Slf4j
public class ManualRegisterScriptHelper implements RegisterScriptHelper {

    private ScriptRegistry scriptRegistry;

    private ScriptLoader redisScriptLoader;

    private RedisTemplate<String, String> redisTemplate;

    private GroovyRedisLoaderProperties groovyRedisLoaderProperties;

    public ManualRegisterScriptHelper(ScriptRegistry scriptRegistry,
                                      ScriptLoader redisScriptLoader,
                                      RedisTemplate<String, String> redisTemplate,
                                      GroovyRedisLoaderProperties groovyRedisLoaderProperties) {
        this.scriptRegistry = scriptRegistry;
        this.redisScriptLoader = redisScriptLoader;
        this.redisTemplate = redisTemplate;
        this.groovyRedisLoaderProperties = groovyRedisLoaderProperties;
    }

    /**
     * 手动注册groovy脚本
     */
    @Override
    public boolean registerScript(@NonNull String name, @NonNull String content) throws Exception {
        log.warn("start manual register script, name is : [{}], script content is : {}", name, content);
        if (StringUtils.isBlank(name) || StringUtils.isBlank(content)) {
            throw new IllegalArgumentException("name and content can not be null.");
        }
        // 脚本放入Redis缓存
        redisTemplate.opsForHash().put(groovyRedisLoaderProperties.getGroup(), name, content);
        log.warn("[{}] script store to redis successfully.", name);
        // 从Redis加载
        ScriptEntry scriptEntry = redisScriptLoader.load(new ScriptQuery(name));
        // 注册到脚本注册中心
        scriptRegistry.register(scriptEntry);
        log.warn("[{}] script register to registry successfully.", name);
        return true;
    }

    /**
     * <p>
     * 批量手动注册groovy脚本，key为脚本名称，value 为脚本内容
     * </p>
     */
    @Override
    public boolean batchRegisterScript(@NonNull Map<String, String> scriptMap) throws Exception {
        log.warn("batch register script start.");
        scriptMap.forEach((name, content) -> {
            try {
                registerScript(name, content);
            } catch (Exception e) {
                throw new RuntimeException("注册脚本失败，请重试", e);
            }
        });
        log.warn("batch register script success.");
        return true;
    }
}
