package org.basis.groovy.loader;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.basis.enhance.groovy.compiler.DynamicCodeCompiler;
import org.basis.enhance.groovy.entity.ScriptEntry;
import org.basis.enhance.groovy.entity.ScriptQuery;
import org.basis.enhance.groovy.loader.ScriptLoader;
import org.basis.groovy.config.properties.GroovyMysqlLoaderProperties;
import org.basis.groovy.entity.EnhanceGroovyScript;
import org.basis.groovy.repository.EnhanceGroovyScriptRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 从MySQL数据库里加载脚本
 *
 * @author wenpan 2022/10/01 16:10
 */
public class MysqlScriptLoader implements ScriptLoader {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DynamicCodeCompiler dynamicCodeCompiler;
    @Autowired
    private GroovyMysqlLoaderProperties groovyMysqlLoaderProperties;
    @Autowired
    private EnhanceGroovyScriptRepository enhanceGroovyScriptRepository;

    @Override
    public ScriptEntry load(@NonNull ScriptQuery query) throws Exception {
        // 按条件查询脚本
        EnhanceGroovyScript groovyScriptQuery = new EnhanceGroovyScript();
        groovyScriptQuery = groovyScriptQuery.queryConverter(query);
        groovyScriptQuery.setEnable(1);
        List<EnhanceGroovyScript> groovyScripts = enhanceGroovyScriptRepository.selectByCondition(groovyScriptQuery);

        if (CollectionUtils.isEmpty(groovyScripts)) {
            logger.warn("can not found groovy script by condition : {}", groovyScriptQuery);
            return null;
        }

        EnhanceGroovyScript groovyScript = groovyScripts.get(0);
        String scriptContent = groovyScript.getScriptContent();
        // 获取脚本指纹
        String fingerprint = DigestUtils.md5DigestAsHex(scriptContent.getBytes());
        // 创建脚本对象
        ScriptEntry scriptEntry = new ScriptEntry(
                groovyScript.buildOnlyKey(), scriptContent, fingerprint, System.currentTimeMillis());
        // 动态加载脚本为Class
        Class<?> aClass = dynamicCodeCompiler.compile(scriptEntry);
        scriptEntry.setClazz(aClass);

        return scriptEntry;
    }

    @Override
    public List<ScriptEntry> load() {

        logger.info("load all groovy script start.");

        List<ScriptEntry> resultList = new ArrayList<>();
        EnhanceGroovyScript query = new EnhanceGroovyScript();
        query.setEnable(1);
        query.setNamespace(groovyMysqlLoaderProperties.getNamespace());
        Preconditions.checkArgument(StringUtils.isNotBlank(query.getNamespace()));
        // 加载该命名空间下所有的脚本
        List<EnhanceGroovyScript> enhanceGroovyScripts = enhanceGroovyScriptRepository.selectByCondition(query);

        // 没有查到脚本，则不处理
        if (CollectionUtils.isEmpty(enhanceGroovyScripts)) {
            logger.warn("can not found EnhanceGroovyScripts by condition : [{}].", query);
            return resultList;
        }

        logger.info("load groovy script count is : [{}]", enhanceGroovyScripts.size());

        for (EnhanceGroovyScript groovyScript : enhanceGroovyScripts) {
            String scriptContent = groovyScript.getScriptContent();
            // 空脚本不处理
            if (StringUtils.isBlank(scriptContent)) {
                logger.error("script content is blank , groovyScript is : {}.", groovyScript);
                continue;
            }
            // 获取脚本指纹
            String fingerprint = DigestUtils.md5DigestAsHex(scriptContent.getBytes());
            // 创建脚本对象
            ScriptEntry scriptEntry = new ScriptEntry(
                    groovyScript.buildOnlyKey(), scriptContent, fingerprint, System.currentTimeMillis());
            resultList.add(scriptEntry);
        }

        logger.info("load all groovy script success.");

        return resultList;
    }
}
