package org.basis.groovy.helper;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.basis.enhance.groovy.entity.ScriptEntry;
import org.basis.enhance.groovy.entity.ScriptQuery;
import org.basis.enhance.groovy.helper.RegisterScriptHelper;
import org.basis.enhance.groovy.loader.ScriptLoader;
import org.basis.enhance.groovy.registry.ScriptRegistry;
import org.basis.groovy.entity.EnhanceGroovyScript;
import org.basis.groovy.repository.EnhanceGroovyScriptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * 注册脚本到MySQL中helper
 *
 * @author wenpan 2022/10/01 19:13
 */
@Slf4j
public class RegisterScriptToMysqlHelper implements RegisterScriptHelper {

    @Autowired
    private EnhanceGroovyScriptRepository enhanceGroovyScriptRepository;

    @Autowired
    private ScriptLoader scriptLoader;

    @Autowired
    private ScriptRegistry scriptRegistry;

    @Override
    public boolean registerScript(@NonNull String name, @NonNull String content, boolean allowCover) throws Exception {
        log.warn("start manual register script, name is : [{}], script content is : {}", name, content);
        if (StringUtils.isBlank(name) || StringUtils.isBlank(content)) {
            throw new IllegalArgumentException("name and content can not be null.");
        }

        // 从数据库加载脚本
        EnhanceGroovyScript enhanceGroovyScript = new EnhanceGroovyScript();
        EnhanceGroovyScript query = enhanceGroovyScript.queryConverter(name);
        List<EnhanceGroovyScript> groovyScripts = enhanceGroovyScriptRepository.selectByCondition(query);

        // 如果数据库里不存在 或 允许覆盖，则插入
        if (CollectionUtils.isEmpty(groovyScripts) || allowCover) {
            query.setEnable(1);
            query.setObjectVersionNumber(0);
            query.setScriptContent(content);
            log.warn("[{}] script store to db start.", name);
            enhanceGroovyScriptRepository.insert(query);
            log.warn("[{}] script store to db successfully.", name);
            // 从mysql加载
            ScriptEntry scriptEntry = scriptLoader.load(new ScriptQuery(name));
            // 注册到脚本注册中心
            Boolean success = scriptRegistry.register(scriptEntry);
            log.warn("[{}] script register to registry result is : [{}].", name, success);
            return success;
        }
        throw new UnsupportedOperationException(
                String.format("can not register script, because [%s] is already exists in datasource.", name));
    }

    @Override
    public boolean batchRegisterScript(@NonNull Map<String, String> scriptMap, boolean allowCover) {
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
