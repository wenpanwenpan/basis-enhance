package org.basis.enhance.executor.infra.util;

import java.util.UUID;

/**
 * UUID工具类
 *
 * @author Mr_wenpan@163.com 2021/8/12 3:35 下午
 */
public class UUIDUtil {

    private UUIDUtil() {
    }

    /**
     * UUID 去掉连接符
     *
     * @param tenantId 租户ID
     * @return 租户ID+UUID
     */
    public static String generateTenantUUID(Long tenantId) {
        return tenantId + generateUUID();
    }

    /**
     * @return 去掉连接符的UUID
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

}