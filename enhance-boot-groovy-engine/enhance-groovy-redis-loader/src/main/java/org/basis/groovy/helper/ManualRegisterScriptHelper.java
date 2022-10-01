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
import java.util.Objects;

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

    @Override
    public boolean registerScript(@NonNull String name, @NonNull String content, boolean allowCover) throws Exception {
        log.warn("start manual register script, name is : [{}], script content is : {}", name, content);
        if (StringUtils.isBlank(name) || StringUtils.isBlank(content)) {
            throw new IllegalArgumentException("name and content can not be null.");
        }
        // 查找脚本是否存在
        Object oldScript = redisTemplate.opsForHash().get(groovyRedisLoaderProperties.getNamespace(), name);
        // 如果脚本不存在或允许覆盖，则写入数据源然后注册到registry
        if (Objects.isNull(oldScript) || allowCover) {
            // 脚本放入Redis缓存
            redisTemplate.opsForHash().put(groovyRedisLoaderProperties.getNamespace(), name, content);
            log.warn("[{}] script store to redis successfully.", name);
            // 从Redis加载
            ScriptEntry scriptEntry = redisScriptLoader.load(new ScriptQuery(name));
            // 注册到脚本注册中心
            scriptRegistry.register(scriptEntry);
            log.warn("[{}] script register to registry successfully.", name);
        } else {
            throw new UnsupportedOperationException(
                    String.format("can not register script, because [%s] is already exists in datasource.", name));
        }

        return true;
    }

    @Override
    public boolean batchRegisterScript(@NonNull Map<String, String> scriptMap, boolean allowCover) throws Exception {
        log.warn("batch register script start.");
        scriptMap.forEach((name, content) -> {
            try {
                registerScript(name, content, allowCover);
            } catch (Exception e) {
                throw new RuntimeException("register failed，please retry.", e);
            }
        });
        log.warn("batch register script success.");
        return true;
    }
}
