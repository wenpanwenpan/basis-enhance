package org.basis.enhance.groovy.entity;

import com.google.common.base.Preconditions;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * 脚本项，一条记录对应着一个脚本
 *
 * @author wenpan 2022/09/18 11:26
 */
@Setter
@Getter
@EqualsAndHashCode
@ToString
public class ScriptEntry {

    /**
     * 脚本名称（需要保证唯一）
     */
    private String name;

    /**
     * 脚本内容
     */
    private final String scriptContext;

    /**
     * 脚本指纹
     */
    private final String fingerprint;

    /**
     * 最近修改时间
     */
    private Long lastModifiedTime;

    /**
     * 脚本code对应的Class
     */
    private Class<?> clazz;

    public ScriptEntry(String scriptContext, String fingerprint, Long lastModifiedTime) {
        Preconditions.checkArgument(StringUtils.isNotBlank(scriptContext), "scriptContext can not be null.");
        Preconditions.checkArgument(StringUtils.isNotBlank(fingerprint), "fingerprint can not be null.");
        Preconditions.checkArgument(Objects.nonNull(lastModifiedTime), "lastModifiedTime can not be null.");
        this.scriptContext = scriptContext;
        this.fingerprint = fingerprint;
        this.lastModifiedTime = lastModifiedTime;
    }

    /**
     * 指纹是否相同
     */
    public boolean fingerprintIsEquals(String otherFingerprint) {
        return fingerprint.equals(otherFingerprint);
    }

}
