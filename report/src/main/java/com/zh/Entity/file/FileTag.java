package com.zh.Entity.file;

/**
 * @Author: lisq
 * @Date: 2019/7/26 15:47
 * @Description:
 */
public class FileTag {
    String name;
    String value;
    Long fileId;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public Long getFileId() {
        return fileId;
    }
    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }
    
}
