package com.zh.service;

import com.zh.DAO.ReportDAO;
import com.zh.Entity.ReportInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: lisq
 * @Date: 2019/7/25 14:43
 * @Description:
 */
@Service
public class ReportService {

    @Autowired
    ReportDAO reportDAO;

    /**
     * 根据报表名判断该报表是否存在
     * @param reportName
     * @return
     */
    public boolean isExitByName(String reportName){
        ReportInfo reportInfo = reportDAO.selectByName(reportName);
        if (reportInfo == null) return false;
        else return  true;
    }

    /**
     * 根据报表名查询报表
     * @param reportName
     * @return
     */
    public ReportInfo selectByName(String reportName){
        return reportDAO.selectByName(reportName);
    }

    /**
     * 新添加一个报表
     * @param reportInfo
     * @return
     */
    public int addReport(ReportInfo reportInfo){
        return reportDAO.add(reportInfo);
    }

}
