package com.zh.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zh.Entity.*;
import com.zh.Entity.file.FileItem;
import com.zh.service.ColInfoService;
import com.zh.service.FileService;
import com.zh.service.FillInfoService;
import com.zh.service.ReportService;
import com.zh.util.JsonResult;
import com.zh.util.PlatformException;
import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author: lisq
 * @Date: 2019/7/25 14:41
 * @Description:
 */
@Controller
@CrossOrigin
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
     * 登陆成功后进入的页面，团队长进入新建报表页面，员工进入在线填写页面
     * @return
     */
    @GetMapping(path = {"/successIndex"})
    @ResponseBody
    public ModelAndView creatIndex(){
        ModelAndView view = null;
        Employee user = hostHolder.getUser();
        if (user.getRoleId() == 1){//团队长
            view = new ModelAndView("/leader/C_createReport.html");
        }else {//普通员工
            view = new ModelAndView("/employee/T_offlineUploadTable_New.html");
        }
        view.addObject("user",user);
        view.addObject("reportLists",allReport());
        return view;
    }

    /**
     * 团队长母版页
     * @param response
     * @return
     */
    @GetMapping(path = {"/top"})
    @ResponseBody
    public ModelAndView top(HttpServletResponse response){
        ModelAndView view = new ModelAndView("/leader/C_Top.html");
        Employee user = hostHolder.getUser();
        view.addObject("user",user);
        view.addObject("reportLists",allReport());
        return view;
    }

    /**
     * 团队长查看报表
     * @param response
     * @param repoorId
     * @return
     */
    @GetMapping(path = {"/previewTable"})
    @ResponseBody
    public ModelAndView preview(HttpServletResponse response,Integer repoorId){
        ModelAndView view = new ModelAndView("/leader/C_previewTable.html");
        ReportInfo reportInfo = reportService.getReportInfo(repoorId);
        view.addObject("reportInfo",reportInfo);
        return view;
    }

    /**
     * 团队长审核页面
     * @param response
     * @param repoorId
     * @return
     */
    @GetMapping(path = {"/checkTable"})
    @ResponseBody
    public ModelAndView checkTable(HttpServletResponse response,Integer repoorId){
        ModelAndView view = new ModelAndView("/leader/C_checkTable.html");
        ReportInfo reportInfo = reportService.getReportInfo(repoorId);
        view.addObject("reportInfo",reportInfo);
        return view;
    }


    /**
     * 创建新报表
     * @param reportName
     * @param colNames
     * @param bussKeys
     * @return
     */
    @PostMapping("/create")
    @ResponseBody
    public JsonResult createReport(String reportName,
                                   @RequestParam(value = "colNames[]") String[] colNames,
                                   @RequestParam(value = "bussKeys[]") boolean[] bussKeys,
                                   String isCheck){
        ReportInfo reportInfo  = new ReportInfo();
        reportInfo.setReportName(reportName);
        //获取当前用户
        String empId = hostHolder.getUser().getEmpId();
        reportInfo.setEmpId(empId);
        reportInfo.setIsCheck(Integer.valueOf(isCheck));
        String bussKey = "";
        for(int i = 1;i<bussKeys.length;i++){
            if(bussKeys[i-1]==true){
                bussKey = bussKey+i+",";
            }
        }
        if (bussKey.equals("")) return JsonResult.failMessage("报表模板没有业务主键");
        reportInfo.setBussKey(bussKey);
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
        System.out.println("ReportId: "+reportId);
        return fillInfoService.fillReportAll(reportId);
    }
    @PostMapping("/auditDisplayTest")
    @ResponseBody
    public  Map<String, Object> getUsersInfo(Integer reportId) {
        List<FillInfo> fillInfos = fillInfoService.fillReportAll(reportId);
        JSONArray data = ToJson(fillInfos);

        if (data != null) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", 0);
            jsonObject.put("msg", "");
            jsonObject.put("count", data.size());
            jsonObject.put("data", data);
            return jsonObject;
        }
        return null;
    }

    /**
     * 将用户信息转化成前台JSON
     *
     * @return String
     */
    public static JSONArray ToJson(List<FillInfo> fillInfos) {
        JSONArray jsonArray = new JSONArray();
        if (fillInfos != null && fillInfos.size() > 0) {
            for (FillInfo fillInfo : fillInfos) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", fillInfo.getFillId());
                jsonObject.put("empId", fillInfo.getEmpID());
                jsonObject.put("name", fillInfo.getName());
                jsonObject.put("colName", fillInfo.getColName());
                jsonObject.put("fillDate",fillInfo.getFillDatetime());
                jsonObject.put("flag",fillInfo.getStatus());
                String status = "";
                if (fillInfo.getStatus() == 0){
                    status =  "未审核";
                }else if(fillInfo.getStatus() == 1){
                    status = "审核通过";
                }else if (fillInfo.getStatus() == 2){
                    status = "审核不通过";
                }
                jsonObject.put("status", status);
                jsonArray.add(jsonObject);
            }
        }
        return jsonArray;
    }

    /**
     * 团队长审核通过与不通过
     * @param ids
     * @param status
     * @return
     */
    @PostMapping("/audit/submit")
    @ResponseBody
    public JsonResult audit(int[] ids,int[] status,Integer reportId){
        for(int i = 0 ; i<ids.length ; i++){
            if (status[i] != 0){
                fillInfoService.update(ids[i],status[i]);
            }
        }
        //审核完成后，需要将fill_info数据添加到final_report中
        fillInfoService.fromFillToFinalReport(reportId);
        return JsonResult.success();
    }

    /**
     * 获取员工所在团队的所有报表
     * @return
     */
    @PostMapping("/allReport")
    @ResponseBody
    public List<ReportInfo> allReport(){
        //获取当前用户
        String empId = hostHolder.getUser().getEmpId();
        if (empId == null) empId = "admin";
//        logger.info("当前用户: "+hostHolder.getUser());
        Map<String, List<ReportInfo>> map = reportService.getAllReportInTeam(empId);
        if (!empId .equals("admin")){
            logger.info("当前用户为admin: "+empId);
            List<ReportInfo> list = new ArrayList<>();
            for (List<ReportInfo> temp:map.values()) {
               list =temp ;
            }
            return  list;
        }else {
            logger.info("当前用户: "+empId);
            return reportService.mapToList(map);
        }
    }

    @PostMapping("/onlineFill")
    @ResponseBody
    public JsonResult onLineFill(FinalReport finalReport){
        String empId = hostHolder.getUser().getEmpId();
        fillInfoService.onlineInsert(finalReport,empId);
        return  JsonResult.success();
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
        System.out.println(reportId);
        System.out.println("reportName:"+reportService.getReportInfo(reportId));
        String reportName = reportService.getReportInfo(reportId).getReportName();

        try(InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(excelTemplate)) {
            if(is==null) {
                throw new PlatformException("模板资源不存在："+excelTemplate);
            }
            FileItem item = fileService.createFileTemp(reportName+"模板.xlsx");
            OutputStream os = item.openOutpuStream();
            Context context = new Context();
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

//    @RequestMapping(path = {"/test"})
//    @ResponseBody
//    public ModelAndView loginIndex(){
//        ModelAndView view = new ModelAndView("/test.html");
//        view.addObject("test","名字");
//        return view;
//    }


//    @GetMapping(path = {"/previewTable"})
//    @ResponseBody
//    public ModelAndView test(Integer repoorId){
//        System.out.println("测试 :  "+repoorId);
//        ModelAndView view = new ModelAndView("/test.html");
//        return view;
//    }
}
