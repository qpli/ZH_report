package com.zh.controller;

import com.alibaba.fastjson.JSON;
import com.zh.Entity.Excel.ExcelSet;
import com.zh.Entity.Excel.ExcelSheet;
import com.zh.Entity.FillInfo;
import com.zh.Entity.HostHolder;
import com.zh.service.ColInfoService;
import com.zh.service.FillInfoService;
import com.zh.service.ResolveExcelService;
import com.zh.util.ExcelSetToFillInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Created by lqp on 2019/7/24
 */
@Controller
public class ResolveExcelController {

    private static Logger logger = LoggerFactory.getLogger(ResolveExcelController.class);

    @Autowired
    HostHolder hostHolder;

    @Autowired
    private ResolveExcelService resolveExcelService;

    @Autowired
    private Environment env;

    @Autowired
    private FillInfoService fillInfoService;
    @Autowired
    private ColInfoService colInfoService;

//    @RequestMapping(value = "/excel")
//    public ModelAndView hello() {
//        ModelAndView view = new ModelAndView("/login.html");
//
//        return view;
//        return "index";
//    }

    @RequestMapping(value = "/upload")
    public ModelAndView upload() {
        ModelAndView view = new ModelAndView("/upload.html");
        return view;
    }

    @RequestMapping(value = "/uploadExcel",method = {RequestMethod.POST})
    public ModelAndView uploadExcel(HttpServletRequest request,
                                    @RequestParam("file") MultipartFile file,
                                    @RequestParam("reportId") Integer reportId) {

        ModelAndView view = new ModelAndView(new MappingJackson2JsonView());
//        ModelAndView view = new ModelAndView( );

        String filename = file.getOriginalFilename();//获取文件名
        System.out.println("是不是一个excel文件？"+isExcelFileName(filename));
        if (!isExcelFileName(filename)) {

            return view.addObject("upload", "请上传后缀名是xls、xlsx的excel文件");
        }

        try {
            String uploadDir = request.getSession().getServletContext().getRealPath("/") + "upload/";

            System.out.println("进入excel文件上传");
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdir();
            }
            File uploadFile = new File(uploadDir + UUID.randomUUID() + file.getOriginalFilename());
            //先保存到本地
            file.transferTo(uploadFile);
            //解析，返回结果
            ExcelSet excelSet = resolveExcelService.resolveExcel(uploadFile.getAbsolutePath());

            logger.info(JSON.toJSONString(excelSet));

            fillInfoService.addFileInfo(excelToFillInfo(excelSet,reportId));

            return view.addObject("upload", excelSet);
        } catch (Exception e) {
            System.out.println("上传excel出现异常");
            e.printStackTrace();
            return view.addObject("upload", e.getMessage());
        }

    }





    /**
     * 判断Excel文件后缀名是否正确
     */
    private boolean isExcelFileName(String filePath){
        int a = filePath.lastIndexOf(".");
        String newStr = filePath.substring(filePath.lastIndexOf(".")+1, filePath.length());
        System.out.println("传入函数中的文件名："+newStr);

        if (newStr.equals( "xls" )|| newStr.equals("xlsx"))
        {
           return true;
        }
        else
        {
            return false;
        }
    }



    public  List<FillInfo> excelToFillInfo(ExcelSet excelSet, Integer reportID){
        List<FillInfo> fillInfolist = new LinkedList<>();

        List<ExcelSheet> excelSheetList = excelSet.getSheets();
        ExcelSheet excelSheet = excelSheetList.get(0);
        List<List<String>> content = excelSheet.getContent();
        for(int i=0;i<content.get(0).size();i++){
            FillInfo fillInfo = new FillInfo();
            StringBuilder fillInfo_context = new StringBuilder();
            for(int j = 1;j<content.size();j++){
                fillInfo_context.append(content.get(j).get(i));
                fillInfo_context.append(",");
            }
            if(ExcelSetToFillInfo.isAllNull(fillInfo_context)){
                continue;
            }
            else {
                fillInfo.setContext(fillInfo_context.toString());
                fillInfo.setColId(colInfoService.selectColIDByReportIdAndColLoc(reportID,i));  //如何获取列ID
                fillInfo.setDelFlag(0);
                fillInfo.setEmpID("lisi");  //如何获取用户名
                fillInfo.setFillDatetime(new Date());
                fillInfo.setStatus(1);
                fillInfolist.add(fillInfo);
            }
        }
        return fillInfolist;
    }


}
