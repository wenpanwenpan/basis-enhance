package org.basis.enhance.groovy.entity;

import com.google.common.base.Preconditions;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * ScriptEntry查询DTO
 *
 * @author wenpan 2022/09/18 12:42
 */
@Data
public class ScriptQuery {

    /**
     * 唯一键
     */
    private String uniqueKey;

    public ScriptQuery(String uniqueKey) {
        Preconditions.checkArgument(StringUtils.isNotBlank(uniqueKey), "uniqueKey can not be null.");
        this.uniqueKey = uniqueKey;
    }

}
