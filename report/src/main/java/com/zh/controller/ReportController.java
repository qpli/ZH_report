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

import java.util.Date;
import java.util.List;
import java.util.Map;

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
//
//    @Autowired
//    FileService fileService;

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
    @PostMapping("/audit/submit")
    @ResponseBody
    public JsonResult audit(int[] ids,int[] status){
        for(int i = 0 ; i<ids.length ; i++){
            if (status[i] != 0){
                fillInfoService.update(ids[i],status[i]);
            }
        }
        return JsonResult.success();
    }

    /**
     * 获取员工所在团队的所有报表
     * @param empId
     * @return
     */
    @PostMapping("/allReport")
    @ResponseBody
    public Map<String, List<ReportInfo>> allReport(String empId){
        //获取当前用户
        if (empId == null) empId = "admin";
        return reportService.getAllReportInTeam(empId);
    }

    /**
     * 员工导出报表列信息
     * @param response
     * @param reportId
     * @return
     */



    /**
     * 1)需要用你自己编写一个的excel模板
     * 2)通常excel导出需要关联更多数据，因此yxcsInfoService.queryByCondition方法经常不符合需求，需要重写一个为模板导出的查询
     * 3)参考ConsoleDictController来实现模板导入导出
     */
    /*
    @PostMapping("/excel/export")
    @ResponseBody
    public JsonResult<String> export(HttpServletResponse response,Integer reportId) {

        String excelTemplate ="excelTemplates/dslx/yjsyInfo/报表_template.xlsx";

        //本次导出需要的数据
        String[] list = colInfoService.queryExcel(reportId);

        String reportName = reportService.getReportName(reportId);

        try(InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(excelTemplate)) {
            if(is==null) {
                throw new PlatformException("模板资源不存在："+excelTemplate);
            }
            FileItem item = fileService.createFileTemp(reportName+"报表模板"+reportName+".xlsx");
            OutputStream os = item.openOutpuStream();
            Context context = new Context();

            context.putVar("reportName",reportName);
            int i = 0;
            //加入真实报表列信息
            for(;i<list.length;i++){
                context.putVar("COL"+i+1,list[i]);
            }
            //多余的报表列信息为空
            for (;i<20;i++){
                context.putVar("COL"+i+1,"");
            }

            JxlsHelper.getInstance().processTemplate(is, os, context);
//            os.close();
            //下载参考FileSystemContorller
            return  JsonResult.success(item.getPath());
        } catch (IOException e) {
            throw new PlatformException(e.getMessage());
        }
    }
    */
}
