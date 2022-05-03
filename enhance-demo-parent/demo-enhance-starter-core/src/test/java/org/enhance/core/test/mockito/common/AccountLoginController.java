package org.enhance.core.test.mockito.common;

import javax.servlet.http.HttpServletRequest;

/**
 * 账户登录controller
 *
 * @author Mr_wenpan@163.com 2022/04/17 18:32
 */
public class AccountLoginController {

    private AccountDAO accountDAO;

    public AccountLoginController(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    public String login(HttpServletRequest request) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        try {
            Account account = accountDAO.findAccount(username, password);
            if (account == null) {
                return "/login";
            } else {
                return "index";
            }
        } catch (Exception ex) {
            // db出现异常的情况
            return "/505";
        }
    }
}
