package com.zh.service.Impl;

import com.zh.Entity.file.FileItem;
import com.zh.service.FileService;
import com.zh.util.DateUtil;
import com.zh.util.UUIDUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import com.zh.Entity.file.*;

import java.io.File;


/**
* @Author:         lisq
* @CreateDate:     2019/7/26 20:03
* @Description:    一个本地文件系统，管理临时文件和用户文件
*/
public class LocalFileService implements FileService {
    Log log = LogFactory.getLog(this.getClass());
	String root = null;

	public LocalFileService(ApplicationContext ctx,String root) {
		this.root = root;
		new File(root,"temp").mkdir();
	}

	@Override
	public FileItem createFileTemp(String name) {
		FileItem item = new LocalFileItem(root);
		String fileName = "temp"+File.separator+name + "." + this.suffix();
		item.setPath(fileName);
		item.setName(name);
		item.setTemp(true);
		return item;
	}

	@Override
	public FileItem loadFileItemByPath(String path) {
		LocalFileItem item = new LocalFileItem(root);
		item.setPath(path);
		item.setName(parseTempFileName(path));
		item.setTemp(true);
		return item;
	}

	private String parseTempFileName(String path) {
		File file = new File(path);
		String name =  file.getName();
		//去掉最后的临时标记
		int index = name.lastIndexOf(".");
		return name.substring(0, index);
	}

	private String suffix() {
		// TODO,改成唯一算法
		return DateUtil.now("yyyyMMddhhmm")+ "-" + UUIDUtil.uuid();
	}


}
