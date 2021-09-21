package org.enhance.executor.demo.infra.mapper;

import org.apache.ibatis.annotations.Param;
import org.enhance.executor.demo.domain.entity.File;

/**
 * 文件操作mapper
 *
 * @author Mr_wenpan@163.com 2021/08/20 10:10
 */
public interface FileMapper {

    /**
     * 插入文件记录
     *
     * @param file 文件记录
     * @author Mr_wenpan@163.com 2021/8/20 10:21 上午
     */
    void insert(File file);

    /**
     * 根据文件ID查询文件
     *
     * @param fileId 文件ID
     * @return com.stone.executor.test.domain.entity.File
     * @author Mr_wenpan@163.com 2021/8/20 10:51 上午
     */
    File queryById(@Param("fileId") String fileId);
}
