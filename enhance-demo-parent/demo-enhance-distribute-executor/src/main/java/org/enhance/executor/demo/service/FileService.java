package org.enhance.executor.demo.service;


import org.basis.enhance.executor.domain.entity.FileInfo;

import java.io.UnsupportedEncodingException;

/**
 * 文件服务
 *
 * @author Mr_wenpan@163.com 2021/08/20 11:41
 */
public interface FileService {

    /**
     * 上传文件
     *
     * @param fileInfo 文件信息
     * @return java.lang.String
     * @author Mr_wenpan@163.com 2021/8/20 11:52 上午
     */
    String uploadFile(FileInfo fileInfo);

    /**
     * 下载文件
     *
     * @param fileInfo 文件信息
     * @return byte[] byte
     * @throws UnsupportedEncodingException ex
     * @author Mr_wenpan@163.com 2021/8/20 13:52 下午
     */
    byte[] downloadFile(FileInfo fileInfo) throws UnsupportedEncodingException;

}
