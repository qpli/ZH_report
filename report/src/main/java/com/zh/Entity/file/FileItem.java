package com.zh.Entity.file;

import java.io.OutputStream;

/**
 * @Author: lisq
 * @Date: 2019/7/26 12:08
 * @Description:
 */
public abstract class FileItem {
    protected Long id;
	protected String name;
	protected String path;
	boolean isTemp = false;
	
	public abstract OutputStream openOutpuStream();
	
	public abstract void copy(OutputStream os);
	
	
	public abstract boolean delete();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}

    public boolean isTemp() {
        return isTemp;
    }

    public void setTemp(boolean isTemp) {
        this.isTemp = isTemp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
}
