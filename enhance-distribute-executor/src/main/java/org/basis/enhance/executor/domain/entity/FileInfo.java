package org.basis.enhance.executor.domain.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 文件信息
 *
 * @author Mr_wenpan@163.com 2021/06/20 11:47
 */
@Data
public class FileInfo {

    /**
     * 任务
     */
    private Task task;

    /**
     * 文件数据
     */
    private Serializable data;

    /**
     * 文件大小
     */
    private Long size;

    /**
     * 组织ID
     */
    private Long organizationId;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件url
     */
    private String url;

    public static Builder builder() {
        return new Builder();
    }

    /**
     * 建造者
     *
     * @author Mr_wenpan@163.com 2021/6/27 1:53 下午
     */
    public static class Builder {

        private Task task;

        private Serializable data;

        private Long size;

        private Long organizationId;

        private String fileName;

        private String url;

        private Builder() {

        }

        public Builder task(Task task) {
            this.task = task;
            return this;
        }

        public Builder data(Serializable data) {
            this.data = data;
            return this;
        }

        public Builder size(Long size) {
            this.size = size;
            return this;
        }

        public Builder organizationId(Long organizationId) {
            this.organizationId = organizationId;
            return this;
        }

        public Builder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public FileInfo build() {
            FileInfo fileInfo = new FileInfo();
            fileInfo.setTask(task);
            fileInfo.setFileName(fileName);
            fileInfo.setOrganizationId(organizationId);
            fileInfo.setSize(size);
            fileInfo.setData(data);
            fileInfo.setUrl(url);
            return fileInfo;
        }

    }
}
