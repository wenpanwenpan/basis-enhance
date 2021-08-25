package org.basis.enhance.mybatis.process;

import org.springframework.util.ObjectUtils;

import java.lang.reflect.Field;

/**
 * 基础自动id处理器
 *
 * @author Mr_wenpan@163.com 2021/8/14 10:15 上午
 */
public abstract class BaseAutoIdProcess {

    /**
     * 待设置值的属性
     */
    protected Field field;
    /**
     * 主键名
     */
    protected String primaryKey;

    public BaseAutoIdProcess(Field field) {
        this.field = field;
    }

    /**
     * 设置entity中的属性值
     *
     * @param entity 实体
     * @throws Exception 异常
     */
    abstract void setFieldValue(Object entity) throws Exception;

    /**
     * 判断id字段是否已经有值，如果不存在，则进行设值
     *
     * @param entity 实体
     */
    private boolean isNotExistFieldValue(Object entity) throws Exception {
        field.setAccessible(true);
        Object value = field.get(entity);
        return ObjectUtils.isEmpty(value);
    }

    /**
     * 处理ID值
     *
     * @param entity 实体
     */
    public void process(Object entity) throws Exception {
        // 如果该实体中不存在指定的ID字段的值，则设置值
        if (isNotExistFieldValue(entity)) {
            setFieldValue(entity);
        }
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }
}
