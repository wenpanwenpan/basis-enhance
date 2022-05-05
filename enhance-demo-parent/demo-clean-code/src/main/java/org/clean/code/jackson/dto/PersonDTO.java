package org.clean.code.jackson.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * person
 * 使用JsonInclude.Include.NON_NULL指定在序列化的时候非空字段不序列化
 *
 * @author Mr_wenpan@163.com 2022/05/04 16:20
 */
@EqualsAndHashCode(callSuper = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PersonDTO extends BaseDTO<String> {
    /**
     * id
     */
    private Long id;
    /**
     * name
     */
    private String name;
    /**
     * 密码，使用@JsonIgnore指定序列化和反序列化的时候该字段不参与
     */
    @JsonIgnore
    private String pwd;
    /**
     * 余额
     */
    private BigDecimal account;
    /**
     * 住址，通过@JsonProperty 来指定属性名和序列化后的json中字段的对应关系
     */
    @JsonProperty(value = "address")
    private String addr;
    /**
     * 身份证号
     */
    private String number;
    /**
     * 住宅
     */
    private HouseDTO hourse;
    /**
     * 爱好
     */
    private List<String> hobby;
    /**
     * 车辆
     */
    private List<CarDTO> carVOList;
    /**
     * 生日 使用JsonFormat来指定日期的格式化形式
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date birthday;
    /**
     * dateTime  使用@JsonFormat来指定LocalDateTime日期的格式化形式
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime dateTime;

}
