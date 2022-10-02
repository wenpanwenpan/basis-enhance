package org.basis.groovy.entity;

import com.google.common.base.Joiner;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.basis.enhance.groovy.entity.ScriptQuery;
import org.basis.enhance.groovy.helper.ApplicationContextHelper;
import org.basis.groovy.config.properties.GroovyMysqlLoaderProperties;
import org.springframework.lang.NonNull;

import java.util.Date;

/**
 * groovy脚本表（表名：enhance_groovy_script）
 *
 * @author wenpan 2022/10/01 16:15
 */
@Data
public class EnhanceGroovyScript {

    /**
     * key分隔符
     */
    private static String KEY_SEPARATOR = null;

    /**
     * id
     */
    private Long id;
    /**
     * 命名空间，用于区分不同的应用
     */
    private String namespace;
    /**
     * 平台码，用于区分不同的平台
     */
    private String platformCode;
    /**
     * 产品码
     */
    private String productCode;
    /**
     * 渠道编码
     */
    private String channelCode;
    /**
     * 业务编码
     */
    private String businessCode;
    /**
     * 是否启用（1代表是，0代表否）
     */
    private Integer enable;
    /**
     * 脚本内容
     */
    private String scriptContent;
    /**
     * 扩展信息，用于后续扩展
     */
    private String extendInfo;
    /**
     * 租户编码，用于区分不同的租户
     */
    private String talent;
    /**
     * 版本号
     */
    private Integer objectVersionNumber;
    /**
     * 创建日期
     */
    private Date creationDate;
    /**
     * 最近修改日期
     */
    private Date latestModifiedDate;

    /**
     * 构建唯一key
     */
    public String buildOnlyKey() {
        if (StringUtils.isBlank(KEY_SEPARATOR)) {
            KEY_SEPARATOR = ApplicationContextHelper.getContext()
                    .getBean(GroovyMysqlLoaderProperties.class).getKeySeparator();
        }
        // 这5个字段值构成了唯一key，可以唯一确定一个groovy脚本
        return Joiner.on(KEY_SEPARATOR).join(namespace, platformCode, productCode, channelCode, businessCode);
    }

    /**
     * 将scriptQuery转换为EnhanceGroovyScript
     */
    public EnhanceGroovyScript queryConverter(@NonNull ScriptQuery scriptQuery) {

        return queryConverter(scriptQuery.getUniqueKey());
    }

    /**
     * 将字符串转换为查询条件
     */
    public EnhanceGroovyScript queryConverter(@NonNull String queryStr) {
        if (StringUtils.isBlank(KEY_SEPARATOR)) {
            KEY_SEPARATOR = ApplicationContextHelper.getContext()
                    .getBean(GroovyMysqlLoaderProperties.class).getKeySeparator();
        }
        // 按下指定分隔符切割
        String[] split = queryStr.split(KEY_SEPARATOR);
        if (split.length != 5) {
            throw new UnsupportedOperationException("uniqueKey length must be 5.");
        }
        // 【命名空间 + 平台编码 + 产品码 + 渠道码 + 业务code】唯一确定一个脚本项
        EnhanceGroovyScript groovyScript = new EnhanceGroovyScript();
        groovyScript.setNamespace(split[0]);
        groovyScript.setPlatformCode(split[1]);
        groovyScript.setProductCode(split[2]);
        groovyScript.setChannelCode(split[3]);
        groovyScript.setBusinessCode(split[4]);
        return groovyScript;
    }

}
