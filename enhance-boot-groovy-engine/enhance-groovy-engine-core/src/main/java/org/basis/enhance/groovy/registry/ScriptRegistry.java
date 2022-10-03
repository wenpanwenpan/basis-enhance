package org.basis.enhance.groovy.registry;

import org.basis.enhance.groovy.entity.ScriptEntry;
import org.basis.enhance.groovy.entity.ScriptQuery;

import java.util.List;
import java.util.Map;

/**
 * 引擎注册中心
 *
 * @author wenpan 2022/09/18 11:24
 */
public interface ScriptRegistry {

    /**
     * <p>
     * 注册脚本，如果脚本项存在则强制覆盖
     * </p>
     *
     * @param scriptEntry 脚本项
     * @return java.lang.Boolean 注册失败，则返回false，不抛出异常
     * @author wenpan 2022/9/18 12:22 下午
     */
    Boolean register(ScriptEntry scriptEntry);

    /**
     * <p>
     * 批量注册脚本项，如果脚本项存在则强制覆盖
     * </p>
     *
     * @param scriptEntries 脚本项集合
     * @return java.lang.Boolean 注册失败，则返回false，不抛出异常
     * @author wenpan 2022/10/1 3:08 下午
     */
    Boolean batchRegister(List<ScriptEntry> scriptEntries);

    /**
     * <p>
     * 注册脚本，如果脚本项存在是否覆盖，则取决于 allowToCover
     * </p>
     *
     * @param scriptEntry  脚本项
     * @param allowToCover 当有重复时是否允许覆盖
     * @return java.lang.Boolean 注册失败，则返回false，不抛出异常
     * @author wenpan 2022/9/18 12:22 下午
     */
    Boolean register(ScriptEntry scriptEntry, boolean allowToCover);

    /**
     * <p>
     * 批量注册脚本项，如果脚本项存在是否覆盖，则取决于 allowToCover
     * </p>
     *
     * @param scriptEntries 脚本项集合
     * @param allowToCover  是否允许覆盖
     * @return java.lang.Boolean 注册失败，则返回false，不抛出异常
     * @author wenpan 2022/10/1 3:08 下午
     */
    Boolean batchRegister(List<ScriptEntry> scriptEntries, boolean allowToCover);

    /**
     * <p>
     * 只从缓存中查找脚本，如果缓存中没有，则返回空（不从数据源进行加载）
     * </p>
     *
     * @param scriptQuery 查找条件
     * @return org.basis.enhance.groovy.entity.ScriptEntry
     * @author wenpan 2022/10/1 2:47 下午
     */
    ScriptEntry findOnCache(ScriptQuery scriptQuery);

    /**
     * <p>
     * 按条件查询脚本项，如果缓存中不存在，则从数据源按条件进行加载，加载到后放入缓存并返回
     * </p>
     *
     * @param scriptQuery 查询条件
     * @return org.basis.enhance.groovy.entity.ScriptEntry
     * @throws Exception 运行时异常
     * @author wenpan 2022/9/18 1:23 下午
     */
    ScriptEntry find(ScriptQuery scriptQuery) throws Exception;

    /**
     * <p>
     * 从缓存中获取所有脚本项
     *  <ol>
     *     参数 {@code needLatestData} 表示：是否需要最新数据
     *     <li>如果为true，则会先去数据源拉取一遍数据并写入到缓存，然后返回</li>
     *     <li>如果为false则直接返回当前缓存里的数据</li>
     *  </ol>
     * </p>
     *
     * @param needLatestData 是否需要最新数据
     * @return Map<String, ScriptEntry>
     * @author wenpan 2022/10/1 2:53 下午
     */
    Map<String, ScriptEntry> findAllOnCache(boolean needLatestData);

    /**
     * <p>
     * 清除注册中心缓存
     * </p>
     *
     * @author wenpan 2022/9/18 5:34 下午
     */
    void clear();

    /**
     * <p>
     * 按条件清除
     * </p>
     *
     * @param scriptQuery 清除条件
     * @return java.lang.Boolean
     * @author wenpan 2022/9/18 5:35 下午
     */
    Boolean clear(ScriptQuery scriptQuery);

}
