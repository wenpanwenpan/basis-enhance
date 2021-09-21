package org.enhance.executor.demo.api.controller.v1;

import com.luhuiguo.fastdfs.domain.StorePath;
import com.luhuiguo.fastdfs.service.FastFileStorageClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.enhance.executor.demo.domain.entity.File;
import org.enhance.executor.demo.infra.mapper.FileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

@Slf4j
@RestController("UploadController.v1")
@RequestMapping("/v1/test-file")
public class UploadController {

	@Autowired
	private FastFileStorageClient storageClient;

	@Autowired
	FileMapper fileMapper;

	private static final String DEFAULT = "default";
	private static final String DEFAULT_GOURP = "group1";

	/**
	 * MultipartFile是用来接收上传的文件
	 * myFile的名字必须和上传的表单的名字一样
	 */
	@PostMapping("/upload-file")
	public String upload(MultipartFile myFile) throws IOException {
		// myFile.getOriginalFilename():取到文件的名字
		// FilenameUtils.getExtension(""):取到一个文件的后缀名
		String extension = FilenameUtils.getExtension(myFile.getOriginalFilename());

		// group1:指storage服务器的组名
		// myFile.getInputStream():指这个文件中的输入流
		// myFile.getSize():文件的大小
		// 这一行是通过storageClient将文件传到storage容器
		StorePath uploadFile = storageClient.uploadFile(DEFAULT_GOURP, myFile.getInputStream(), myFile.getSize(), extension);

		// 上传数据库
		File file = new File();
		file.setAttachmentUuid(uploadFile.getPath());
		file.setFileName(myFile.getOriginalFilename());
		file.setBucketName(DEFAULT);
		file.setFileType(DEFAULT);
		// 当组名用
		file.setDirectory(DEFAULT_GOURP);
		file.setFileUrl(uploadFile.getFullPath());
		fileMapper.insert(file);

		// 返回它在storage容器的的路径
		return uploadFile.getFullPath();
	}

	@GetMapping("/fdownload/{id}")
	public void download(@PathVariable String id, HttpServletResponse response) throws IOException {

		File file = fileMapper.queryById(id);
		// 解决中文文件名下载后乱码的问题
		String filename = URLEncoder.encode(file.getFileName(), "utf-8");
		// 告诉浏览器 下载的文件名
		response.setHeader("Content-Disposition", "attachment; filename=" + filename + "");
		// 将文件的内容输出到浏览器 fastdfs
		byte[] downloadFile = storageClient.downloadFile(file.getDirectory(), file.getAttachmentUuid());
		response.getOutputStream().write(downloadFile);
	}

}
