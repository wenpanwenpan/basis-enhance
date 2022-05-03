package org.enhance.core.test.mockito.quickstart;

import org.enhance.core.test.mockito.common.Account;
import org.enhance.core.test.mockito.common.AccountDAO;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.mockito.Mockito.mock;

/**
 * 测试通过rule的方式进行mock（这种方式不常用，常用的是通过runWith和注解的方式）
 *
 * @author Mr_wenpan@163.com 2022/04/17 19:06
 */
public class MockByRuleTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test
    public void testMock() {
        AccountDAO accountDAO = mock(AccountDAO.class);
        Account account = accountDAO.findAccount("xx", "xx");
        System.out.println(account);
    }

}
