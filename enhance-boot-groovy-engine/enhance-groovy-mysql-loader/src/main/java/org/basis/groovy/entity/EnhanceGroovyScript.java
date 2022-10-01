package org.basis.groovy.entity;

import com.google.common.base.Joiner;
import lombok.Data;
import org.basis.enhance.groovy.entity.ScriptQuery;
import org.springframework.lang.NonNull;

import java.util.Date;

/**
 * groovy脚本表（表名：enhance_groovy_script）
 *
 * @author wenpan 2022/10/01 16:15
 */
@Data
public class EnhanceGroovyScript {

    private static final String LOWER_LINE = "_";

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
        // 这5个字段值构成了唯一key，可以唯一确定一个groovy脚本
        return Joiner.on(LOWER_LINE).join(namespace, platformCode, productCode, channelCode, businessCode);
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
        // 按下划线切割
        String[] split = queryStr.split(LOWER_LINE);
        if (split.length != 5) {
            throw new UnsupportedOperationException("uniqueKey length must be 5.");
        }
        EnhanceGroovyScript groovyScript = new EnhanceGroovyScript();
        groovyScript.setNamespace(split[0]);
        groovyScript.setPlatformCode(split[1]);
        groovyScript.setProductCode(split[2]);
        groovyScript.setChannelCode(split[3]);
        groovyScript.setBusinessCode(split[4]);
        return groovyScript;
    }

}
