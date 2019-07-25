package com.zh.controller;

import com.alibaba.fastjson.JSON;
import com.zh.Entity.Excel.ExcelSet;
import com.zh.Entity.HostHolder;
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
            fillInfoService.addFileInfo(ExcelSetToFillInfo.excelToFillInfo(excelSet));

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

}
