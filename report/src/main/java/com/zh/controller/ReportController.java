package com.zh.controller;

import com.zh.Entity.*;
import com.zh.Entity.file.FileItem;
import com.zh.service.ColInfoService;
import com.zh.service.FileService;
import com.zh.service.FillInfoService;
import com.zh.service.ReportService;
import com.zh.util.JsonResult;
import com.zh.util.PlatformException;
import org.jxls.util.JxlsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletResponse;
import org.jxls.common.Context;
import org.springframework.web.servlet.ModelAndView;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

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
    @Autowired
    FileService fileService;
    @Autowired
    HostHolder hostHolder;


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
        String empId = hostHolder.getUser().getEmpId();
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
        //审核完成后，需要将fill_info数据添加到final_report中
        fillInfoService.fromFillToFinalReport();
        return JsonResult.success();
    }

    /**
     * 获取员工所在团队的所有报表
     * @return
     */
    @PostMapping("/allReport")
    @ResponseBody
    public Map<String, List<ReportInfo>> allReport(){
        //获取当前用户
        String empId = hostHolder.getUser().getEmpId();
        if (empId == null) empId = "admin";
        return reportService.getAllReportInTeam(empId);
    }

    /**
     * 员工导出报表列信息
     *
     *    1)需要用你自己编写一个的excel模板
     *    2)通常excel导出需要关联更多数据，因此yxcsInfoService.queryByCondition方法经常不符合需求，需要重写一个为模板导出的查询
     *    3)参考ConsoleDictController来实现模板导入导出
     *
     * @param reportId
     * @return
     */
    @PostMapping("/excel/export")
    @ResponseBody
    public JsonResult<String> export(Integer reportId) {
        String excelTemplate ="excelTemplates/报表_template.xlsx";

        //本次导出需要的数据
        String[] list = colInfoService.queryExcel(reportId);

        String reportName = reportService.getReportName(reportId);

        try(InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(excelTemplate)) {
            if(is==null) {
                throw new PlatformException("模板资源不存在："+excelTemplate);
            }
            FileItem item = fileService.createFileTemp(reportName+"模板.xlsx");
            OutputStream os = item.openOutpuStream();
            Context context = new Context();

            System.out.println("reportName:"+reportName);

            context.putVar("reportName",reportName);
            int i = 0;
            //加入真实报表列信息
            for(;i<list.length;i++){
                int value = i+1;
                System.out.println("COL"+value+":  "+list[i]);
                context.putVar("COL"+value,list[i]);
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

    @GetMapping("/get.do")
    @ResponseBody
    public ModelAndView index(HttpServletResponse response,String id) throws IOException {
        System.out.println("test");
        String path = id;
        response.setContentType("text/html; charset = UTF-8");
        FileItem fileItem = fileService.loadFileItemByPath(path);
        response.setHeader("Content-Disposition", "attachment; filename="+java.net.URLEncoder.encode(fileItem.getName(), "UTF-8"));
        fileItem.copy(response.getOutputStream());
        if(fileItem.isTemp()) {
            fileItem.delete();
        }
        return null;
    }

    @RequestMapping(path = {"/test"})
    @ResponseBody
    public ModelAndView loginIndex(){
        ModelAndView view = new ModelAndView("/test.html");
        return view;
    }
}
