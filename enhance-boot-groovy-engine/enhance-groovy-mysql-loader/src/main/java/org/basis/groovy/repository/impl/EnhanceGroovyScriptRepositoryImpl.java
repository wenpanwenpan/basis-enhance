package org.basis.groovy.repository.impl;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.basis.groovy.entity.EnhanceGroovyScript;
import org.basis.groovy.mapper.EnhanceGroovyScriptMapper;
import org.basis.groovy.repository.EnhanceGroovyScriptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * EnhanceGroovyEngineRepository实现类
 *
 * @author wenpan 2022/10/01 16:29
 */
@Repository
public class EnhanceGroovyScriptRepositoryImpl implements EnhanceGroovyScriptRepository {

    @Autowired
    private EnhanceGroovyScriptMapper enhanceGroovyScriptMapper;

    @Override
    public List<EnhanceGroovyScript> selectAll() {

        return enhanceGroovyScriptMapper.selectAll();
    }

    @Override
    public List<EnhanceGroovyScript> selectByCondition(@NonNull EnhanceGroovyScript query) {

        return enhanceGroovyScriptMapper.selectByCondition(query);
    }

    @Override
    public Integer updateByCondition(@NonNull EnhanceGroovyScript enhanceGroovyScript) {
        // 更新操作是一个非常危险的操作，这里做条件限制强校验
        Preconditions.checkArgument(StringUtils.isNotBlank(enhanceGroovyScript.getNamespace()), "namespace can not be null.");
        Preconditions.checkArgument(StringUtils.isNotBlank(enhanceGroovyScript.getScriptContent()), "scriptContent can not be null.");
        Preconditions.checkArgument(StringUtils.isNotBlank(enhanceGroovyScript.getChannelCode()), "channelCode can not be null.");
        Preconditions.checkArgument(StringUtils.isNotBlank(enhanceGroovyScript.getPlatformCode()), "platformCode can not be null.");
        Preconditions.checkArgument(StringUtils.isNotBlank(enhanceGroovyScript.getProductCode()), "roductCode can not be null.");

        return enhanceGroovyScriptMapper.updateByCondition(enhanceGroovyScript);
    }

    @Override
    public Integer insert(@NonNull EnhanceGroovyScript enhanceGroovyScript) {

        return enhanceGroovyScriptMapper.insert(enhanceGroovyScript);
    }

    @Override
    public Integer deleteById(@NonNull Long id) {

        return enhanceGroovyScriptMapper.deleteById(id);
    }
}
