package org.enhance.executor.demo.service.impl;

import com.luhuiguo.fastdfs.domain.StorePath;
import com.luhuiguo.fastdfs.service.FastFileStorageClient;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.basis.enhance.executor.domain.entity.FileInfo;
import org.enhance.executor.demo.domain.entity.File;
import org.enhance.executor.demo.infra.mapper.FileMapper;
import org.enhance.executor.demo.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 文件服务实现
 *
 * @author Mr_wenpan@163.com 2021/08/20 11:42
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FileServiceImpl implements FileService {

    private final FastFileStorageClient storageClient;

    private final FileMapper fileMapper;

    private static final String DEFAULT = "default";
    private static final String DEFAULT_GOURP = "group1";

    @Override
    public String uploadFile(FileInfo fileInfo) {
        // FilenameUtils.getExtension(""):取到一个文件的后缀名
        String extension = FilenameUtils.getExtension(fileInfo.getFileName());

        // group1:指storage服务器的组名
        StorePath uploadFile = storageClient.uploadFile(DEFAULT_GOURP, (InputStream) fileInfo.getData(), fileInfo.getSize(), extension);

        // saveFileInfo2Db(uploadFile, fileInfo);

        return uploadFile.getFullPath();

    }

    @Override
    public byte[] downloadFile(FileInfo fileInfo) throws UnsupportedEncodingException {

        // 解决中文文件名下载后乱码的问题
        String filename = URLEncoder.encode(fileInfo.getFileName(), "utf-8");
        // 将文件的内容输出到浏览器 fastdfs
        return storageClient.downloadFile("file.getDirectory()", fileInfo.getUrl());
    }

    private void saveFileInfo2Db(StorePath uploadFile, FileInfo fileInfo) {
        // 上传数据库
        File file = new File();
        file.setAttachmentUuid(uploadFile.getPath());
        file.setFileName(fileInfo.getFileName());
        file.setBucketName(DEFAULT);
        file.setFileType(DEFAULT);
        // 当组名用
        file.setDirectory(DEFAULT_GOURP);
        file.setFileUrl(uploadFile.getFullPath());
        fileMapper.insert(file);
    }
}
