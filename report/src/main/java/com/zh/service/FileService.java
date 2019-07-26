package com.zh.service;

import com.zh.Entity.file.FileItem;

import java.io.OutputStream;
import java.util.List;

/**
* @Author:         lisq
* @CreateDate:     2019/7/26 20:03
* @Description:    文件持久化，默认为文件系统，可以扩展到fastfds等
*/
public interface FileService {
	/**
	 * 得到一个临时文件操作
	 * @param name
	 * @return
	 */
	public FileItem createFileTemp(String name);
	public FileItem loadFileItemByPath(String path);

}
