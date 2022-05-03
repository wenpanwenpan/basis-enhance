package org.clean.code.mapstruct.vo;

import lombok.Builder;
import lombok.Data;

/**
 * PartVO 车部件
 *
 * @author Mr_wenpan@163.com 2022/05/02 15:44
 */
@Data
@Builder
public class PartVO {

    /**
     * 部件ID
     */
    private String partId;
    /**
     * 部件名称
     */
    private String partName;
}
