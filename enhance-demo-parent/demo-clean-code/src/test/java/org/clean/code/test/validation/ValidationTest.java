package org.clean.code.test.validation;

import org.clean.code.validation.entity.Grade;
import org.clean.code.validation.entity.UserInfo;
import org.clean.code.validation.util.ValidationUtil;
import org.junit.Test;

import javax.validation.groups.Default;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 校验测试
 *
 * @author Mr_wenpan@163.com 2022/05/03 14:51
 */
public class ValidationTest {

    @Test
    public void test01() {
        UserInfo userInfo = new UserInfo();

        // 测试基础注解校验
        userInfo.setName("wenpan");
        userInfo.setAge(20);
        userInfo.setEmail("987868534@qq.com");
        userInfo.setPhone("13548765723");
        userInfo.setBirthDay(LocalDateTime.now().minusHours(1));
        userInfo.setPersonalPage("https://www.bad-guy.cn");

        // 测试级联属性校验
        Grade grade = new Grade();
        grade.setClassNo("一班");
        userInfo.setGrade(grade);

        // 测试自定义注解
        userInfo.setStatus(1001);

        List<String> result = ValidationUtil.validFailFast(userInfo, UserInfo.AddGroup.class, Default.class);
        System.out.println(result);
    }
}
