package com.zh.Entity;

import java.util.Date;

public class FillInfo {
    Integer fillId;
    Integer colId;
    String empID;
    String context;
    Date fillDatetime;
    Integer status;
    Integer delFlag;

    public Integer getFillId() {
        return fillId;
    }

    public void setFillId(Integer fillId) {
        this.fillId = fillId;
    }

    public Integer getColId() {
        return colId;
    }

    public void setColId(Integer colId) {
        this.colId = colId;
    }

    public String getEmpID() {
        return empID;
    }

    public void setEmpID(String empID) {
        this.empID = empID;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public Date getFillDatetime() {
        return fillDatetime;
    }

    public void setFillDatetime(Date fillDatetime) {
        this.fillDatetime = fillDatetime;
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
