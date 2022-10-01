package org.basis.groovy.mapper;

import org.apache.ibatis.annotations.Param;
import org.basis.groovy.entity.EnhanceGroovyScript;
import org.springframework.lang.NonNull;

import java.util.List;

/**
 * EnhanceGroovyEngine mapper
 *
 * @author wenpan 2022/10/01 16:24
 */
public interface EnhanceGroovyScriptMapper {

    /**
     * 查询全部脚本数据
     *
     * @return java.util.List<org.basis.groovy.entity.EnhanceGroovyScript>
     * @author wenpan 2022/10/1 4:25 下午
     */
    List<EnhanceGroovyScript> selectAll();

    /**
     * 按条件查询 EnhanceGroovyScript
     *
     * @param query 查询条件
     * @return java.util.List<org.basis.groovy.entity.EnhanceGroovyScript>
     * @author wenpan 2022/10/1 4:26 下午
     */
    List<EnhanceGroovyScript> selectByCondition(@NonNull EnhanceGroovyScript query);

    /**
     * 按条件更新 EnhanceGroovyScript
     *
     * @param enhanceGroovyScript 待更新的数据
     * @return java.lang.Integer 更新条数
     * @author wenpan 2022/10/1 4:27 下午
     */
    Integer updateByCondition(@NonNull EnhanceGroovyScript enhanceGroovyScript);

    /**
     * 插入
     *
     * @param enhanceGroovyScript 待插入的数据
     * @return java.lang.Integer 影响行数
     * @author wenpan 2022/10/1 4:27 下午
     */
    Integer insert(@NonNull EnhanceGroovyScript enhanceGroovyScript);

    /**
     * 根据ID删除数据
     *
     * @param id id
     * @return java.lang.Integer
     * @author wenpan 2022/10/1 4:37 下午
     */
    Integer deleteById(@NonNull @Param("id") Long id);
}
