package org.enhance.core.test.mockito.quickstart;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.when;

/**
 * deep mock测试
 * stubbing : 可以理解为录制
 * 具体执行测试方法：可以理解为播放
 *
 * @author Mr_wenpan@163.com 2022/04/17 19:13
 */
public class DeepMockTest {

    /**
     * answer = Answers.RETURNS_DEEP_STUBS是指需要深度mock lesson03Service
     */
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Lesson03Service lesson03Service;

    @Mock
    Lesson03 lesson03;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * 深度mock测试，就是说mock了一个对象，该对象的方法里如果有调用其他对象的方法，那也一起做了
     */
    @Test
    public void testDeepMock() {
        // 普通mock需要一步步的定义stubbing
        when(lesson03Service.get()).thenReturn(lesson03);
        Lesson03 lesson = lesson03Service.get();
        lesson.foo();

        // 深度mock一步到位（这里的 lesson03Service.get() 的返回值不是我们自己想要的返回值，而是mockito为我们mock出来的结果，所以使用时需要自己权衡）
//        lesson03Service.get().foo();
    }
}
