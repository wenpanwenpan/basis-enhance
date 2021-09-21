package org.enhance.executor.demo.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;

import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 文件记录实体
 *
 * @author Mr_wenpan@163.com 2021/8/20 10:27 上午
 */
@Data
@Table(name = "hfle_file")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class File {

    public static final String FIELD_FILE_ID = "fileId";
    public static final String FIELD_ATTACHMENT_UUID = "attachmentUuid";
    public static final String FIELD_DIRECTORY = "directory";
    public static final String FIELD_FILE_URL = "fileUrl";
    public static final String FIELD_FILE_TYPE = "fileType";
    public static final String FIELD_FILE_NAME = "fileName";
    public static final String FIELD_FILE_SIZE = "fileSize";
    public static final String FIELD_BUCKET_NAME = "bucketName";
    public static final String FIELD_FILE_KEY = "fileKey";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_MD5 = "md5";
    public static final String FIELD_STORAGE_CODE = "storageCode";
    public static final String FIELD_SERVER_CODE = "serverCode";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @Id
    @GeneratedValue
    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long fileId;
    @Length(max = 50)
    @ApiModelProperty("附件集UUID")
    private String attachmentUuid;
    @Length(max = 60)
    @ApiModelProperty("上传目录")
    private String directory;
    @Length(max = 480)
    @ApiModelProperty("文件地址")
    private String fileUrl;
    @Length(max = 120)
    @ApiModelProperty("文件类型")
    private String fileType;
    @ApiModelProperty("文件名称")
    @Length(max = 240)
    private String fileName;
    @ApiModelProperty("文件大小")
    private Long fileSize;
    @NotBlank(message = "文件Bucket不能为空")
    @Length(max = 60)
    @ApiModelProperty("文件目录")
    private String bucketName;
    @ApiModelProperty("对象KEY")
    @Length(max = 480)
    private String fileKey;
    @NotNull(message = "租户不能为空")
    @ApiModelProperty("租户Id")
    private Long tenantId;
    @Length(max = 60)
    @ApiModelProperty("文件MD5")
    private String md5;
    @ApiModelProperty("存储编码")
    private String storageCode;
    @ApiModelProperty("来源类型")
    private String sourceType;
    @ApiModelProperty("服务器编码，hpfm_server.server_code")
    private String serverCode;
    @ApiModelProperty("版本号")
    private Long objectVersionNumber;
    @ApiModelProperty("创建日期")
    private Date creationDate;
    @ApiModelProperty("创建人")
    private Long createdBy;
    @ApiModelProperty("最后更新人")
    private Long lastUpdatedBy;
    @ApiModelProperty("最后更新日期")
    private Date lastUpdateDate;


    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @Transient
    private String tenantName;
    @Transient
    private String realName;
    @Transient
    private String sourceTypeMeaning;
    @Transient
    private String tableName;

}