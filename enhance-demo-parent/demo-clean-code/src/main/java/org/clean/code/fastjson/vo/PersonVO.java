package org.clean.code.fastjson.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * person
 *
 * @author Mr_wenpan@163.com 2022/05/04 16:20
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PersonVO extends BaseVO<String> {
    /**
     * id
     */
    private Long id;
    /**
     * name
     */
    private String name;
    /**
     * 密码，使用serialize和deserialize指定序列化和反序列化的时候该字段不参与
     */
    @JSONField(serialize = false, deserialize = false)
    private String pwd;
    /**
     * 余额
     */
    private BigDecimal account;
    /**
     * 住址，通过@JSONField 来指定属性名和序列化后的json中字段的对应关系
     */
    @JSONField(name = "address")
    private String addr;
    /**
     * 身份证号
     */
    private String number;
    /**
     * 住宅
     */
    private HouseVO hourse;
    /**
     * 爱好
     */
    private List<String> hobby;
    /**
     * 车辆
     */
    private List<CarVO> carVOList;
    /**
     * 生日 使用@JSONField来指定日期的格式化形式
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date birthday;
    /**
     * dateTime  使用@JSONField来指定LocalDateTime日期的格式化形式
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateTime;

}
