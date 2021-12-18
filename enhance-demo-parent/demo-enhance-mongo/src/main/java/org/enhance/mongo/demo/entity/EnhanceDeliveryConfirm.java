package org.enhance.mongo.demo.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * 增强mongo多数据源测试实体
 *
 * @author Mr_wenpan@163.com 2021/12/17 3:43 下午
 */
@Data
@Document(collection = "enhance_delivery_confirm")
public class EnhanceDeliveryConfirm {

    public static final String FIELD_COLLECTION = "o2ot_delivery_confirm";
    public static final String FIELD_ID = "_id";
    public static final String FIELD_SC_CODE = "scCode";
    public static final String FIELD_CARRIER_NAME = "carrierName";
    public static final String FIELD_LOGISTICS_NUMBER = "logisticsNumber";
    public static final String FIELD_DELIVERY_DATE = "deliveryDate";
    public static final String FIELD_LINE_NO = "lineNo";
    public static final String FIELD_SKU_CODE = "skuCode";
    public static final String FIELD_DELIVERY_QUANTITY = "deliveryQuantity";
    public static final String FIELD_PROCESS_STATUS = "processStatus";
    public static final String FIELD_PROCESS_MESSAGE = "processMessage";

    @Id
    private String _id;
    private String scCode;
    private String carrierName;
    private String logisticsNumber;
    private Date deliveryDate;
    private String processStatus;
    private String processMessage;
    private Date createdDate;
}