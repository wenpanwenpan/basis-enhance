package org.basis.enhance.mybatis.process;


import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

import java.lang.reflect.Field;

/**
 * 雪花算法自动生成ID处理器
 *
 * @author Mr_wenpan@163.com 2021/8/14 10:24 上午
 */
public class SnowFlakeAutoIdProcess extends BaseAutoIdProcess {

    private static Snowflake snowflake = IdUtil.createSnowflake(0, 0);

    public SnowFlakeAutoIdProcess(Field field) {
        super(field);
    }

    @Override
    void setFieldValue(Object entity) throws Exception {
        // 雪花算法生成全局唯一ID
        long value = SnowFlakeAutoIdProcess.snowflake.nextId();
        field.set(entity, value);
    }
}
