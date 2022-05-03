package org.enhance.core.test.mockito.quickstart;

import org.enhance.core.test.mockito.common.Account;
import org.enhance.core.test.mockito.common.AccountDAO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * 注解方式的mock测试
 * 可以看到类上面没有添加@RunWith注解
 *
 * @author Mr_wenpan@163.com 2022/04/17 19:02
 */
public class MockByAnnotationTest {

    /**
     * 使用@Mock注解进行mock对象
     * answer = Answers.RETURNS_SMART_NULLS : 指定mock的返回方式
     */
    @Mock(answer = Answers.RETURNS_SMART_NULLS)
    private AccountDAO accountDAO;

    @Before
    public void init() {
        // 使用注解的方式来进行mock
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testMock() {
        Account account = accountDAO.findAccount("xx", "xx");
        System.out.println(account);
    }
}

