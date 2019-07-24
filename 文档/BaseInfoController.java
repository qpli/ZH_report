package com.corp.dslx.web;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.corp.dslx.dao.BaseInfoDao;
import com.corp.dslx.dao.ExtramuralMentorDao;
import com.corp.dslx.dao.MentorExperienceDao;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.beetl.sql.core.engine.PageQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import com.ibeetl.admin.core.annotation.Function;
import com.ibeetl.admin.core.file.FileService;
import com.ibeetl.admin.core.web.JsonResult;
import com.ibeetl.admin.core.util.*;
import com.corp.dslx.entity.*;
import com.corp.dslx.service.*;
import com.corp.dslx.web.query.*;

/**
 * BaseInfo 接口
 */
@Controller
public class BaseInfoController{

    private final Log log = LogFactory.getLog(this.getClass());
    private static final String MODEL = "/dslx/baseInfo";


    @Autowired private BaseInfoService baseInfoService;
    //    @Autowired private BaseInfoDao baseInfoDao;
    @Autowired private ExtramuralMentorService extramuralMentorService;
    //    @Autowired private ExtramuralMentorDao extramuralMentorDao;
    @Autowired private MentorExperienceService mentorExperienceService;
//    @Autowired private MentorExperienceDao mentorExperienceDao;

    @Autowired
    FileService fileService;
    /* 页面 */

    @GetMapping(MODEL + "/extraIndex.do")
    @Function("ExtraInfo")
    @ResponseBody
    public ModelAndView extraIndex() {
        ModelAndView view = new ModelAndView("/dslx/baseInfo/extraIndex.html") ;
        ExtramuralMentor extramuralMentor = extramuralMentorService .queryByGh("2018003");
        if(extramuralMentor != null){
            view.addObject("extramuralMentor", extramuralMentor);
        }else{
            extramuralMentor = new ExtramuralMentor();
            extramuralMentor.setGh("2018006");
            view.addObject("extramuralMentor", extramuralMentor);
        }
        return view;
    }


    /**
     * 校外老师上传图片操作
     * @ Author: lisiqi[lisiqi0911@stumail.nwu.edu.cn]
     * @ Date: 2018/9/6 18:52
     * @ Param:
     * @ return:
     */
    @PostMapping(MODEL + "/photo")
    @Function("ExtraInfo")
    @ResponseBody
    public JsonResult updatePhoto(String gh, @RequestParam("file") MultipartFile file){
        Map<String, Object> json = new HashMap<String, Object>();
        try {
            //图片存储绝对路径
            String url = "C:/xampp/htdocs/photos";
            //原始文件名称
            String oldName = file.getOriginalFilename();
            //获取后缀名
            String newName = oldName.substring(oldName.indexOf('.'));
            //图片存储名称  工号
            newName = "/"+gh+".jpg";
            //相对路径
            File file1 = new File(url);
            if(!file1.exists()){
                System.out.println(file1);
                file1.mkdirs();
            }
            file.transferTo(new File(url+newName));
            //存入数据库
            ExtramuralMentor extramuralMentor = new ExtramuralMentor();
            extramuralMentor.setPicurl("http://localhost/photos"+newName);
            //判断该工号的老师是否在数据库中已存在
            boolean exit = extramuralMentorService.isExistByGh(gh);
            if (!exit){//若不存在，则保存一条新纪录
                extramuralMentor.setGh(gh);
                extramuralMentorService.save(extramuralMentor);
                return new JsonResult().success();
            }else{//若存在，则更新
                ExtramuralMentor extramuralMentor1 = extramuralMentorService.queryByGh(gh);
                extramuralMentor1.setPicurl(extramuralMentor.getPicurl());
                boolean success = extramuralMentorService.update(extramuralMentor1);
                if(success){
                    return JsonResult.successMessage("上传成功");
                }else {
                    return JsonResult.failMessage("上传失败");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.failMessage("上传失败");
        }
    }

    /**
     * 校外老师上传文件
     * @ Author: lisiqi[lisiqi0911@stumail.nwu.edu.cn]
     * @ Date: 2018/9/6 18:53
     * @ Param:
     * @ return:
     */
    @PostMapping(MODEL + "/uploadFile")
    @Function("ExtraInfo")
    @ResponseBody
    public JsonResult uploadFile(String gh,String fileType, @RequestParam("file") MultipartFile file){
        Map<String, Object> json = new HashMap<String, Object>();
        try {
            //图片存储绝对路径
            String url = "C:/xampp/htdocs/extraFiles/"+gh;
            //原始文件名称
            String oldName = file.getOriginalFilename();
            //获取后缀名
            String newName = oldName.substring(oldName.indexOf('.'));
            //判断上传的文件类型为主要科研情况还是科研获奖情况
            if(fileType.equals("kyzm")){//上传的文件为主要科研情况
                //图片存储名称  工号+主要科研证明材料
                newName = "/"+gh+"主要科研证明材料"+newName;
            }else{//上传文件是科研获奖情况
                newName = "/"+gh+"科研获奖证明材料"+newName;
            }
            //相对路径
            File file1 = new File(url);
            if(!file1.exists()){
                System.out.println(file1);
                file1.mkdirs();
            }
            file.transferTo(new File(url+newName));
            //存入数据库
            ExtramuralMentor extramuralMentor = new ExtramuralMentor();
            if(fileType.equals("kyzm")){//上传的文件为主要科研情况
                extramuralMentor.setKyzmurl("http://localhost/extraFiles/"+gh+newName);
            }else {//上传文件是科研获奖情况
                extramuralMentor.setHjzmurl("http://localhost/extraFiles/"+gh+newName);
            }
            //判断该工号的老师是否在数据库中已存在
            boolean exit = extramuralMentorService.isExistByGh(gh);
            if (!exit){//若不存在，则保存一条新纪录
                extramuralMentor.setGh(gh);
                extramuralMentorService.save(extramuralMentor);
                return new JsonResult().success();
            }else{//若存在，则更新
                ExtramuralMentor extramuralMentor1 = extramuralMentorService.queryByGh(gh);
                if(fileType.equals("kyzm")){//上传的文件为主要科研情况
                    extramuralMentor1.setKyzmurl(extramuralMentor.getKyzmurl());
                }else {//上传文件是科研获奖情况
                    extramuralMentor1.setHjzmurl(extramuralMentor.getHjzmurl());
                }
                boolean success = extramuralMentorService.update(extramuralMentor1);
                if(success){
                    return JsonResult.successMessage("上传成功");
                }else {
                    return JsonResult.failMessage("上传失败");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.failMessage("上传失败");
        }
    }

    /**
     * 通过教师工号查询返回教师经历对象
     * @ Author: lisiqi[lisiqi0911@stumail.nwu.edu.cn]
     * @ Date: 2018/8/10 14:58
     * @ Param:
     * @ return:
     */
    @PostMapping(MODEL + "/ghList.json")
    @Function("ExtraInfo")
    @ResponseBody
    public Map<String,Object> list(String gh){
        List<MentorExperience> list = mentorExperienceService.queryByGh(gh);
        JSONArray jsonArray = new JSONArray();
        if(list!=null){
            for(MentorExperience men:list){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id",men.getId());
                jsonObject.put("jsxm",men.getJsxm());
                jsonObject.put("jsgh",men.getJsgh());
                jsonObject.put("qzny",men.getQzny());
                jsonObject.put("gzdwynr",men.getGzdwynr());
                jsonObject.put("zwzc",men.getZwzc());
                jsonObject.put("delFlag",men.getDelFlag());
                jsonArray.add(jsonObject);
            }
        }
        if (jsonArray != null) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", 0);
            jsonObject.put("msg", "");
            jsonObject.put("count", jsonArray.size());
            jsonObject.put("data", jsonArray);
            return jsonObject;
        }

        return null;
    }

    /**
     * 添加校外导师经历
     * @ Author: lisiqi[lisiqi0911@stumail.nwu.edu.cn]
     * @ Date: 2018/8/7 22:18
     * @ Param:
     * @ return:
     */
    @PostMapping(MODEL + "/addExperi.json")
    @Function("ExtraInfo")
    @ResponseBody
    public JsonResult add(@Validated(ValidateConfig.ADD.class)MentorExperience mentorExperience)
    {
        System.out.println(mentorExperience);
        mentorExperienceService.save(mentorExperience);
        return new JsonResult().success();
    }

    /**
     * 校外申请人基本信息保存
     * @ Author: lisiqi[lisiqi0911@stumail.nwu.edu.cn]
     * @ Date: 2018/8/5 15:41
     */
    @PostMapping(MODEL+"/saveBase.json")
    @Function("ExtraInfo")
    @ResponseBody
    public JsonResult saveBase(@Validated(ValidateConfig.ADD.class) ExtramuralMentor extramuralMentor){
        //判断该老师在数据库中是否存在
        boolean exist = extramuralMentorService.isExistByGh(extramuralMentor.getGh());
        if(!exist){ //若不存在，则新建保存
            extramuralMentorService.save(extramuralMentor);
            return new JsonResult().success();
        }
        else{//若存在，则更新数据
            //在前台不需要填写url和id，以及审核状态，所以需把数据库中之前存的重新set，否则更新过后回为null
            Integer id =  extramuralMentorService.queryByGh(extramuralMentor.getGh()).getId();
            extramuralMentor.setPicurl(extramuralMentorService.queryByGh(extramuralMentor.getGh()).getPicurl());
            extramuralMentor.setKyzmurl(extramuralMentorService.queryByGh(extramuralMentor.getGh()).getKyzmurl());
            extramuralMentor.setHjzmurl(extramuralMentorService.queryByGh(extramuralMentor.getGh()).getHjzmurl());
            extramuralMentor.setId(id);
            extramuralMentor.setStatus("00");
            boolean success = extramuralMentorService.update(extramuralMentor);
            if(success){
                return new JsonResult().success();
            }else {
                return JsonResult.failMessage("保存失败");
            }
        }
    }

    /**
     * 更新校外导师主要经历
     * @ Author: lisiqi[lisiqi0911@stumail.nwu.edu.cn]
     * @ Date: 2018/8/8 10:26
     * @ Param:
     * @ return:
     */
    @PostMapping(MODEL + "/updateExperi.json")
    @Function("ExtraInfo")
    @ResponseBody
    public JsonResult<String> update(@Validated(ValidateConfig.UPDATE.class)  MentorExperience mentorExperience) {
        boolean success = mentorExperienceService.update(mentorExperience);
        if (success) {
            return new JsonResult().success();
        } else {
            return JsonResult.failMessage("保存失败");
        }
    }

    /**
     * 批量删除校外导师主要经历
     * @ Author: lisiqi[lisiqi0911@stumail.nwu.edu.cn]
     * @ Date: 2018/8/8 11:00
     * @ Param:
     * @ return:
     */
    @PostMapping(MODEL + "/deleteExperi.json")
    @Function("ExtraInfo")
    @ResponseBody
    public JsonResult delete(String ids) {
        if (ids.endsWith(",")) {
            ids = StringUtils.substringBeforeLast(ids, ",");
        }
        List<Long> idList = ConvertUtil.str2longs(ids);
        mentorExperienceService.batchDelMentorExperience(idList);
        return new JsonResult().success();
    }


//-----------------------------------------------------------------------------


    /**
     * 读取指定校内教师基本信息并显示
     * @ Author: LiuYixue [lyx876@outlook.com]
     * @ Date:2018/8/10
     * @ Param:
     * @ return:
     */
    @GetMapping(MODEL + "/index.do")
    @Function("baseInfo.query")
    @ResponseBody
    public ModelAndView index(){
        ModelAndView view = new ModelAndView("/dslx/baseInfo/index.html");
        //需要从当前用户获取工号
        String gh = null;
        if(gh == null){
            gh = "20170002";
        }
        BaseInfo baseInfo = baseInfoService.queryByGh(gh);
        view.addObject("baseInfo",baseInfo);
        return view;
    }


//    @GetMapping(MODEL + "/edit.do")
//    @Function("baseInfo.edit")
//    @ResponseBody
//    public ModelAndView edit(Integer id) {
//        ModelAndView view = new ModelAndView("/dslx/baseInfo/edit.html");
//        BaseInfo baseInfo = baseInfoService.queryById(id);
//        view.addObject("baseInfo", baseInfo);
//        return view;
//    }

    /**
     * 更新照片
     * @ Author: LiuYixue [lyx876@outlook.com]
     * @ Date:2018/8/14
     * @ param gh
     * @ param file
     * @ return
     */
    @PostMapping(MODEL + "/photos")
    @Function("BaseiInfo")
    @ResponseBody
    public JsonResult updatePhotos(String gh,@RequestParam("file") MultipartFile file){
        Map<String, Object> json = new HashMap<String, Object>();
        try {
            //图片存储绝对路径
            String url = "C:/xampp/htdocs/photos";
            BaseInfo baseInfo = baseInfoService.queryByGh(gh);
            System.out.println("xm:"+baseInfo.getXm());
            //原始文件名称
            String oldName = file.getOriginalFilename();
            //获取后缀名
            String newName = oldName.substring(oldName.indexOf('.'));
            //图片存储名称  工号
            newName = "/"+gh+".jpg";
            //相对路径

            File file1 = new File(url);
            if(!file1.exists()){
                System.out.println(file1);
                file1.mkdirs();
            }
            file.transferTo(new File(url+newName));
            //存入数据库
            baseInfo.setPicurl("http://localhost/photos"+newName);
            boolean success = baseInfoService.update(baseInfo);
            if(success){
                return JsonResult.successMessage("上传成功");
            }else {
                return JsonResult.failMessage("上传失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.failMessage("上传失败");
        }
    }






    /* ajax json */

    /**
     * 保存校内教师基本信息
     * @ Author LiuYixue [lyx876@outlook.com]
     * @ Date:2018/8/12
     * @ param baseInfo
     * @ return
     */
    @PostMapping(MODEL+"/saveBaseInfo.json")
    @Function("BaseInfo")
    @ResponseBody
    public  JsonResult saveBaseInfo(@Validated(ValidateConfig.ADD.class)BaseInfo baseInfo){


        if (baseInfo.getSfsjz()==null||!baseInfo.getSfsjz().equals("是")){
            baseInfo.setSfsjz("否");
        }
        if (baseInfo.getSfzwjzbd()==null||!baseInfo.getSfzwjzbd().equals("是")){
            baseInfo.setSfzwjzbd("否");
        }
        if (baseInfo.getSfzwjzsd()==null||!baseInfo.getSfzwjzsd().equals("是")){
            baseInfo.setSfzwjzsd("否");
        }

        baseInfo.setPicurl(baseInfoService.queryByGh(baseInfo.getGh()).getPicurl());
        boolean exist = baseInfoService.isExistByGh(baseInfo.getGh());
        if (!exist){
            baseInfoService.save(baseInfo);
            return new JsonResult().success();
        }
        else {
            Integer id = baseInfoService.queryByGh(baseInfo.getGh()).getId();
            baseInfo.setId(id);
            baseInfo.setStatus("00");
            boolean success = baseInfoService.update(baseInfo);
            if (success){
                return new JsonResult().success();
            }else {
                return JsonResult.failMessage("保存失败");
            }
        }
    }


//    @PostMapping(MODEL + "/list.json")
//    @Function("baseInfo.query")
//    @ResponseBody
//    public JsonResult<PageQuery> list(BaseInfoQuery condtion)
//    {
//        PageQuery page = condtion.getPageQuery();
//        baseInfoService.queryByCondition(page);
//        return JsonResult.success(page);
//    }
//
//    @PostMapping(MODEL + "/add.json")
//    @Function("baseInfo.add")
//    @ResponseBody
//    public JsonResult add(@Validated(ValidateConfig.ADD.class)BaseInfo baseInfo)
//    {
//        baseInfoService.save(baseInfo);
//        return new JsonResult().success();
//    }
//
//    @GetMapping(MODEL + "/view.json")
//    @Function("baseInfo.query")
//    @ResponseBody
//    public JsonResult<BaseInfo>queryInfo(Integer id) {
//        BaseInfo baseInfo = baseInfoService.queryById( id);
//        ModelAndView modelAndView = new ModelAndView();
//        return  JsonResult.success(baseInfo);
//    }

//    @PostMapping(MODEL + "/delete.json")
//    @Function("baseInfo.delete")
//    @ResponseBody
//    public JsonResult delete(String ids) {
//        if (ids.endsWith(",")) {
//            ids = StringUtils.substringBeforeLast(ids, ",");
//        }
//        List<Long> idList = ConvertUtil.str2longs(ids);
//        baseInfoService.batchDelBaseInfo(idList);
//        return new JsonResult().success();
//    }


//
//    @PostMapping(MODEL + "/excel/export.json")
//    @Function("baseInfo.export")
//    @ResponseBody
//    public JsonResult<String> export(HttpServletResponse response,BaseInfoQuery condtion) {
//        /**
//         * 1)需要用你自己编写一个的excel模板
//         * 2)通常excel导出需要关联更多数据，因此baseInfoService.queryByCondition方法经常不符合需求，需要重写一个为模板导出的查询
//         * 3)参考ConsoleDictController来实现模板导入导出
//         */
//        String excelTemplate ="excelTemplates/dslx/baseInfo/你的excel模板文件名字.xls";
//        PageQuery<BaseInfo> page = condtion.getPageQuery();
//        //取出全部符合条件的
//        page.setPageSize(Integer.MAX_VALUE);
//        page.setPageNumber(1);
//        page.setTotalRow(Integer.MAX_VALUE);
//        //本次导出需要的数据
//        List<BaseInfo> list =baseInfoService.queryByCondition(page).getList();
//        try(InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(excelTemplate)) {
//            if(is==null) {
//                throw new PlatformException("模板资源不存在："+excelTemplate);
//            }
//            FileItem item = fileService.createFileTemp("BaseInfo_"+DateUtil.now("yyyyMMddHHmmss")+".xls");
//            OutputStream os = item.openOutpuStream();
//            Context context = new Context();
//            context.putVar("list", list);
//            JxlsHelper.getInstance().processTemplate(is, os, context);
//            os.close();
//            //下载参考FileSystemContorller
//            return  JsonResult.success(item.getPath());
//        } catch (IOException e) {
//            throw new PlatformException(e.getMessage());
//        }
//
//    }
//
//    @PostMapping(MODEL + "/excel/import.do")
//    @Function("baseInfo.import")
//    @ResponseBody
//    public JsonResult importExcel(@RequestParam("file") MultipartFile file) throws Exception {
//        if (file.isEmpty()) {
//           return JsonResult.fail();
//        }
//        InputStream ins = file.getInputStream();
//        /*解析模板并导入到数据库里,参考DictConsoleContorller，使用jxls reader读取excel数据*/
//        ins.close();
//        return JsonResult.success();
//    }


}
