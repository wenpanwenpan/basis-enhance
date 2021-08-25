package org.basis.enhance.mybatis.helper;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

/**
 * @author Mr_wenpan@163.com 2021/08/14 00:18
 */
public class Test {
    private static Snowflake snowflake = IdUtil.createSnowflake(0, 0);

    public static void main(String[] args) {
        long nextId = Test.snowflake.nextId();
        System.out.println(nextId);
        // 340036669414L是39位，照理说应该移动24位啊？？？
        final Long var1 = 340036669414L << 22;
        final Long var2 = 0L;
        final Long var3 = 0L;
        final Long var4 = 0L;
        System.out.println("===============================");
        System.out.println(var1 | var2 | var3 | var4);
    }
}
