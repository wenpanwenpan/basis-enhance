package org.basis.enhance.executor.domain.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.basis.enhance.executor.infra.constants.StoneExecutorConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Date;

/**
 * mongo任务数据
 *
 * @author Mr_wenpan@163.com 2021/8/13 11:25 上午
 */
@Document(collection = "stone_executor_tasks")
@CompoundIndexes({
        @CompoundIndex(name = "un_status_time_idx", def = "{'status':1, 'modifyDate':-1}"),
        @CompoundIndex(name = "un_handler_time_idx", def = "{'taskHandlerBeanId':1, 'modifyDate':-1}"),
        @CompoundIndex(name = "un_status_handler_t_idx", def = "{'status':1, 'taskHandlerBeanId':1, 'modifyDate':-1}")
})
@Data
@ApiModel("任务数据")
public class Task implements Serializable {
    private static final long serialVersionUID = -6729139833652772588L;

    public static final String FIELD_TITLE = "title";
    public static final String FIELD_STATUS = "status";
    public static final String FIELD_GROUP = "group";
    public static final String FIELD_TASK_HANDLER_BEAN_ID = "taskHandlerBeanId";
    public static final String FIELD_CREATION_DATE = "creationDate";
    public static final String FIELD_MODIFY_DATE = "modifyDate";

    /**
     * 任务名称
     */
    @Indexed
    @ApiModelProperty("任务名称")
    private String title;

    /**
     * 任务组
     */
    @ApiModelProperty("任务组")
    private String group;

    /**
     * 数据存储
     */
    @ApiModelProperty("数据存储")
    private String dataStore;

    /**
     * 负责处理数据的beanId
     */
    @ApiModelProperty("负责处理数据的beanId")
    private String taskHandlerBeanId;

    /**
     * 任务id uuid
     */
    @Id
    private String id;

    /**
     * 主任务id
     */
    @ApiModelProperty("主任务id")
    private String mainTaskId;

    /**
     * 任务状态
     */
    @ApiModelProperty("任务状态 LOV编码: O2MD.TASK_STATUS")
    private String status;

    /**
     * 错误消息
     */
    @ApiModelProperty("错误消息")
    private String errorMsg;

    /**
     * 任务最终运行的节点记录
     */
    @ApiModelProperty("任务最终运行的节点记录")
    private String lastRunNode;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Date creationDate;

    /**
     * 修改时间
     */
    @ApiModelProperty("修改时间")
    private Date modifyDate;

    /**
     * 任务数据 T必须实现序列化接口
     */
//    @Transient
    private Serializable data;

    @ApiModelProperty("是否是幂等操作")
    private Boolean isIdempotent;

    /**
     * 尝试运行次数
     */
    @ApiModelProperty("尝试运行次数")
    private Integer attemptCount;

    @ApiModelProperty("组织id")
    private Long organizationId;

    private Task() {
        creationDate = new Date();
        attemptCount = 0;
        isIdempotent = false;
        status = StoneExecutorConstants.ProcessStatus.PENDING;
    }

    /**
     * 建造者
     */
    public static class Builder implements Serializable {
        private static final long serialVersionUID = 1213635386257641542L;

        private final Task task;

        public Builder() {
            task = new Task();
        }

        public Builder group(String group) {
            task.setGroup(group);
            return this;
        }

        public Builder data(Serializable data) {
            task.setData(data);
            return this;
        }

        public Builder taskHandlerBeanId(String taskHandlerBeanId) {
            task.setTaskHandlerBeanId(taskHandlerBeanId);
            return this;
        }

        public Builder isIdempotent(boolean isIdempotent) {
            task.setIsIdempotent(isIdempotent);
            return this;
        }

        public Builder title(String title) {
            task.setTitle(title);
            return this;
        }

        public Task build() {
            Assert.notNull(task.group, "property [group] must not be null");
            Assert.notNull(task.data, "property [data] must not be null");
            Assert.notNull(task.taskHandlerBeanId, "property [taskHandlerBeanId] must not be null");
            return task;
        }
    }
}