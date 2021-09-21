package org.enhance.executor.demo.domain.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.enhance.executor.demo.infra.constant.StoneExecutorTestConstants;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * mongo实体
 *
 * @author Mr_wenpan@163.com 2021/8/19 4:42 下午
 */
@ApiModel("前台职级履历更新EC层")
@Data
@Document(collection = StoneExecutorTestConstants.Mongo.RANK_CHANGE_DETAIL_BUFFER)
public class RankChangeDetailBuffer {

    public static final String FIELD_ID = "_id";
    public static final String FIELD_PROCESS_STATUS = "processStatusCode";
    public static final String FIELD_PROCESS_MESSAGE = "processMessage";

    @ApiModelProperty("表ID,主键")
    private String _id;
    @ApiModelProperty("客户编码")
    private String userID;
    @ApiModelProperty("当前职级")
    private String rankNo;
    @ApiModelProperty("当前职级日期")
    private String rankDate;

    @ApiModelProperty("当前职级")
    private String paidRank;
    @ApiModelProperty("当前职级日期")
    private String runDate;

    @ApiModelProperty("最高职级编号")
    private String maxRankNo;
    @ApiModelProperty("最高职级日期")
    private String maxRankDate;

    @ApiModelProperty("最高职级编号")
    private String endRank;
    @ApiModelProperty("最高职级1里程碑日期")
    private String rnkDt1;
    @ApiModelProperty("最高职级2里程碑日期")
    private String rnkDt2;
    @ApiModelProperty("最高职级3里程碑日期")
    private String rnkDt3;
    @ApiModelProperty("最高职级4里程碑日期")
    private String rnkDt4;
    @ApiModelProperty("最高职级5里程碑日期")
    private String rnkDt5;
    @ApiModelProperty("最高职级6里程碑日期")
    private String rnkDt6;
    @ApiModelProperty("最高职级7里程碑日期")
    private String rnkDt7;
    @ApiModelProperty("最高职级8里程碑日期")
    private String rnkDt8;
    @ApiModelProperty("最高职级9里程碑日期")
    private String rnkDt9;
    @ApiModelProperty("最高职级10里程碑日期")
    private String rnkDt10;
    @ApiModelProperty("最高职级11里程碑日期")
    private String rnkDt11;
    @ApiModelProperty("最高职级12里程碑日期")
    private String rnkDt12;

    @ApiModelProperty("职级编号")
    private String officialPaidRank;
    @ApiModelProperty("晋升日期")
    private String pvDate;

    @ApiModelProperty("转换状态")
    private String processStatusCode;
    @ApiModelProperty("转换错误消息")
    private String processErrorMsg;

}