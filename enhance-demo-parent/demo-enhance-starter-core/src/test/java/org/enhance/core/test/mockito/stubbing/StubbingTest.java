package org.enhance.core.test.mockito.stubbing;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * stubbing使用测试
 *
 * @author Mr_wenpan@163.com 2022/04/17 18:16
 */
@RunWith(MockitoJUnitRunner.class)
public class StubbingTest {
    private List<String> list;

    @Before
    public void init() {
        // mock一个list对象
        list = mock(ArrayList.class);
    }

    @Test
    public void howToUserStubbing() {
        when(list.get(0)).thenReturn("first");
        assertThat(list.get(0), equalTo("first"));
        when(list.get(anyInt())).thenThrow(new RuntimeException());

        // 具体执行调用
        try {
            list.get(0);
            fail();
        } catch (Exception ex) {
            // 捉住异常做断言
            assertThat(ex, instanceOf(RuntimeException.class));
        }

    }

    /**
     * 如何测试返回值为空的方法调用
     */
    @Test
    public void howToUserReturnVoidStubbing() {
        // 当执行到list.clear的方法时什么也不做（list.clear方法返回值为空）
        doNothing().when(list).clear();
        list.clear();
        // 校验list.clear方法是否被调用了一次
        verify(list, times(1)).clear();
        // 当执行list.clear方法时抛出异常
        doThrow(RuntimeException.class).when(list).clear();

        // 具体执行
        try {
            list.clear();
            // 执行到这里时控制台显示失败
            fail();
        } catch (Exception ex) {
            // 这是我们预料之内的结果
            assertThat(ex, instanceOf(RuntimeException.class));
        }
    }

    @Test
    public void stubbingDoReturn() {
        when(list.get(0)).thenReturn("first");
        doReturn("second").when(list.get(1));
        // 断言
        assertThat(list.get(0), equalTo("first"));
        assertThat(list.get(1), equalTo("second"));
    }

    /**
     * 迭代的方式stubbing
     */
    @Test
    public void iterateStubbing() {
        // 方式一
        when(list.size()).thenReturn(1, 2, 3, 4);
        // 方式二
//        when(list.size()).thenReturn(1).thenReturn(2).thenReturn(3).thenReturn(4);
        // 第1次调用返回1
        assertThat(list.size(), equalTo(1));
        // 第次调用返回2
        assertThat(list.size(), equalTo(2));
        // 第3次调用返回3
        assertThat(list.size(), equalTo(3));
        // 第4次调用返回4
        assertThat(list.size(), equalTo(4));
        // 第5次调用返回5
        assertThat(list.size(), equalTo(5));
    }

    @Test
    public void stubbingWithAnswer() {
        when(list.get(anyInt())).thenAnswer(invocationOnMock -> {
            // 获取list.get(i)中的i
            Integer argumentAt = invocationOnMock.getArgumentAt(0, Integer.class);
            return String.valueOf(argumentAt * 10);
        });

        // 开始执行
        assertThat(list.get(0), equalTo("0"));
        assertThat(list.get(10), equalTo("100"));
    }

    /**
     * 调用真实方法
     */
    @Test
    public void stubbingWithRealCall() {
        // mock出一个对象
        StubbingService stubbingService = mock(StubbingService.class);
        System.out.println(stubbingService.getClass());
        // 这里并不会调用我们自己定义的方法里的内容，而是调用mock的方法，并不会在这里抛出异常
        stubbingService.getS();
        // 这里也不会返回10，而是返回0
        System.out.println(stubbingService.getI());


        // 调用getI方法时指定一个返回值
        when(stubbingService.getS()).thenReturn("wenpan");
        // 执行并且断言（这里返回的是上面的mock出来的wenpan）
        assertThat(stubbingService.getS(), equalTo("wenpan"));

        // 调用StubbingService类中的真实方法
        when(stubbingService.getI()).thenCallRealMethod();
        // 执行并且断言
        assertThat(stubbingService.getI(), equalTo(10));
    }

    @After
    public void destroy() {

    }

}
