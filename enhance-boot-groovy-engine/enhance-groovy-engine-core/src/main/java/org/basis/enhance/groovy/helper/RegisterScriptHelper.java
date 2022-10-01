package org.basis.enhance.groovy.helper;

import org.springframework.lang.NonNull;

import java.util.Map;

/**
 * 手动注册脚本助手
 *
 * @author wenpan 2022/09/30 21:09
 */
public interface RegisterScriptHelper {

    /**
     * <p>
     * 注册groovy脚本
     * </p>
     *
     * @param name       脚本名称
     * @param content    脚本内容
     * @param allowCover 是否允许覆盖
     * @return true / false
     * @throws Exception 异常
     */
    boolean registerScript(@NonNull String name, @NonNull String content, boolean allowCover) throws Exception;

    /**
     * <p>
     * 批量注册groovy脚本，key为脚本名称，value 为脚本内容
     * </p>
     *
     * @param scriptMap  脚本信息map
     * @param allowCover 是否允许覆盖
     * @return true / false
     * @throws Exception 异常
     */
    boolean batchRegisterScript(@NonNull Map<String, String> scriptMap, boolean allowCover) throws Exception;
}
