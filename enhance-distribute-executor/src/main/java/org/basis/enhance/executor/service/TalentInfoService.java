package org.basis.enhance.executor.service;

/**
 * 租户信息获取接口
 *
 * @author Mr_wenpan@163.com 2021/08/17 18:24
 */
public interface TalentInfoService {

    /**
     * 获取租户组织id接口，留给使用方实现
     *
     * @return java.lang.Long
     * @author Mr_wenpan@163.com 2021/8/17 6:25 下午
     */
    Long getOrganizationId();

    /**
     * 获取租户名称接口，留给使用方实现
     *
     * @return java.lang.String
     * @author Mr_wenpan@163.com 2021/8/17 6:26 下午
     */
    String getUserName();

}
