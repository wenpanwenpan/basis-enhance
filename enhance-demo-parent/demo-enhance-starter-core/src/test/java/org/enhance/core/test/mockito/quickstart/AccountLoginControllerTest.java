package org.enhance.core.test.mockito.quickstart;

import org.enhance.core.test.mockito.common.Account;
import org.enhance.core.test.mockito.common.AccountDAO;
import org.enhance.core.test.mockito.common.AccountLoginController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * AccountLoginController测试（mockito快速入门）
 *
 * @author Mr_wenpan@163.com 2022/04/17 18:38
 */
@RunWith(MockitoJUnitRunner.class)
public class AccountLoginControllerTest {

    private AccountDAO accountDAO;

    private HttpServletRequest httpServletRequest;

    private AccountLoginController accountLoginController;

    @Before
    public void init() {
        // mock出具体的实例
        accountDAO = mock(AccountDAO.class);
        httpServletRequest = mock(HttpServletRequest.class);
        accountLoginController = new AccountLoginController(accountDAO);
    }

    @Test
    public void testLoginSuccess() {
        // 从mock出来的 httpServletRequest 中模拟获取username和password
        when(httpServletRequest.getParameter("username")).thenReturn("wenpan");
        when(httpServletRequest.getParameter("password")).thenReturn("wenpan");
        // 这里模拟查询账号信息成功
        when(accountDAO.findAccount(anyString(), anyString())).thenReturn(new Account());

        // 具体执行login方法
        String result = accountLoginController.login(httpServletRequest);
        // 对返回结果进行断言
        assertThat(result, equalTo("index"));
    }


    @Test
    public void testLoginFailed() {
        // 从mock出来的 httpServletRequest 中模拟获取username和password
        when(httpServletRequest.getParameter("username")).thenReturn("wenpan");
        when(httpServletRequest.getParameter("password")).thenReturn("wenpan");
        // 这里模拟执行失败，也就是没有找到账号信息
        when(accountDAO.findAccount(anyString(), anyString())).thenReturn(null);

        // 具体执行login方法
        String result = accountLoginController.login(httpServletRequest);
        // 对返回结果进行断言
        assertThat(result, equalTo("/login"));
    }


    /**
     * 模拟dao方法抛出异常
     */
    @Test
    public void testLoginException() {
        // 从mock出来的 httpServletRequest 中模拟获取username和password
        when(httpServletRequest.getParameter("username")).thenReturn("wenpan");
        when(httpServletRequest.getParameter("password")).thenReturn("wenpan");
        // 这里模拟查询数据库出现异常（也就是db出现问题了）
        when(accountDAO.findAccount(anyString(), anyString())).thenThrow(UnsupportedOperationException.class);

        // 具体执行login方法
        String result = accountLoginController.login(httpServletRequest);
        // 对返回结果进行断言
        assertThat(result, equalTo("/505"));
    }
}
