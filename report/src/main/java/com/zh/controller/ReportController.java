package com.zh.controller;

import com.zh.Entity.FillInfo;
import com.zh.Entity.ReportInfo;
import com.zh.service.ColInfoService;
import com.zh.service.FillInfoService;
import com.zh.service.ReportService;
import com.zh.util.JsonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.SimpleFormatter;

/**
 * @Author: lisq
 * @Date: 2019/7/25 14:41
 * @Description:
 */
@Controller
public class ReportController {
    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

    @Autowired
    ReportService reportService;
    @Autowired
    ColInfoService colInfoService;
    @Autowired
    FillInfoService fillInfoService;

    /**
     * 创建新报表
     * @param reportInfo
     * @param colNames
     * @return
     */
    @PostMapping("/create")
    @ResponseBody
    public JsonResult createReport(ReportInfo reportInfo, String[] colNames){

        //获取当前用户
        String empId = "lisi";
        reportInfo.setEmpId(empId);

        //判断数据库中是否存在该报表
        boolean isExit = reportService.isExitByName(reportInfo.getReportName());
        if (!isExit){//若不存在，则添加
            reportInfo.setCreattime(new Date());// new Date()为获取当前系统时间
            reportService.addReport(reportInfo);//添加报表

            //获取报表id添加列信息
            ReportInfo reinfo = reportService.selectByName(reportInfo.getReportName());
            colInfoService.addColInfo(reinfo.getReportId(),colNames);
            return JsonResult.success();
        }else {
            return JsonResult.failMessage("添加失败，该报表名已存在！");
        }
    }

    /**
     * 审核页面的表格展示
     * @param reportId
     * @return
     */
    @PostMapping("/auditDisplay")
    @ResponseBody
    public List<FillInfo> auditDisplay(Integer reportId){
        return fillInfoService.fillReportAll(reportId);
    }

    /**
     * 团队长审核通过与不通过
     * @param ids
     * @param status
     * @return
     */
    @PostMapping("/audit")
    @ResponseBody
    public JsonResult audit(int ids){
//        for(int i = 0 ; i<ids.length ; i++){
//            if (status[i] != 0){
//                System.out.println("id:"+ids[i]+" status:"+status[i]);
//                fillInfoService.update(ids[i],status[i]);
//            }
//        }

        fillInfoService.update(ids);
        return JsonResult.success();
    }


}
