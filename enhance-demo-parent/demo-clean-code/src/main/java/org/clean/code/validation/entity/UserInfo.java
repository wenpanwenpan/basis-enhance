package org.clean.code.validation.entity;

import lombok.Data;
import org.clean.code.validation.annotation.UserStatus;
import org.hibernate.validator.constraints.URL;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * 用户信息
 *
 * @author Mr_wenpan@163.com 2022/05/03 14:40
 */
@Data
public class UserInfo {

    /**
     * 用户ID，新增时必须没有ID，修改时必须要有id，这时就需要分组校验了
     * 如果没有指定分组，则默认组是：javax.validation.groups.Default
     */
    @Null(groups = AddGroup.class) //新增时必须为空
    @NotNull(groups = UpdateGroup.class) // 修改时必须要有ID
    private Long id;
    /**
     * 用户姓名，通过message自定义校验消息
     */
    @NotNull
    @NotEmpty
    @NotBlank(message = "姓名不能为空")
    private String name;
    /**
     * 年龄，使用el表达式来自定义提示信息
     */
    @NotNull
    @Min(value = 1, message = "年龄小于{value}岁，禁止进入")
    @Max(800)
//    @Range(min = 1, max = 800)
    private Integer age;
    /**
     * 要先校验非空
     * 邮箱
     */
    @NotBlank
    @Email
    private String email;
    /**
     * 手机号，通过@Pattern自定义校验正则表达式
     */
//    @Pattern(regexp = "")
    private String phone;
    /**
     * 生日
     */
    @NotNull
    @Past
    private LocalDateTime birthDay;
    /**
     * 个人主页url
     */
    @URL
    private String personalPage;

    /**
     * 班级，如果要校验这种引用对象，则需要在该属性上还要叫上@valid注解
     */
    @NotNull
    @Valid
    private Grade grade;

    /**
     * 状态，这里演示自定义校验注解
     */
    @UserStatus
    private Integer status;

    /**
     * 新增组
     */
    public interface AddGroup {

    }

    /**
     * 修改组
     */
    public interface UpdateGroup {

    }
}
