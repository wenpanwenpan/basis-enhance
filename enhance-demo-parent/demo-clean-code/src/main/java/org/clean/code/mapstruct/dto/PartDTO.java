package org.clean.code.mapstruct.dto;

import lombok.Builder;
import lombok.Data;

/**
 * partDTO
 *
 * @author Mr_wenpan@163.com 2022/05/02 15:44
 */
@Data
@Builder
public class PartDTO {

    /**
     * 部件ID
     */
    private String partId;
    /**
     * 部件名称
     */
    private String partName;
}
