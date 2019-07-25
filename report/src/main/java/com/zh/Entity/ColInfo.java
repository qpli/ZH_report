package com.zh.Entity;

/**
 * @Author: lisq
 * @Date: 2019/7/25 14:41
 * @Description:
 */
public class ColInfo {
    Integer colId;
    Integer reportId;
    String colName;
    Integer colLoc;
    Integer status;
    Integer delFlag;

    public Integer getColId() {
        return colId;
    }

    public void setColId(Integer colId) {
        this.colId = colId;
    }

    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

    public String getColName() {
        return colName;
    }

    public void setColName(String colName) {
        this.colName = colName;
    }

    public Integer getColLoc() {
        return colLoc;
    }

    public void setColLoc(Integer colLoc) {
        this.colLoc = colLoc;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }
}
