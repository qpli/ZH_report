package com.zh.controller;

import com.github.pagehelper.PageHelper;
import com.zh.Entity.FillInfo;
import com.zh.service.FillInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.List;

/**
 * Created by lqp on 2019/7/27
 */
@Controller
public class TestPageHelperController {

    @Autowired
    FillInfoService fillInfoService;

    @GetMapping("/testPage")
    public ModelAndView testPageController(  @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                                           @RequestParam(value = "pageSize",defaultValue = "5") Integer pageSize){
        ModelAndView view = new ModelAndView(new MappingJackson2JsonView());
        PageHelper.startPage(pageNum, pageSize);
        List<FillInfo> list =  fillInfoService.fillReportAll(4);
        return view.addObject("upload", list);
    }

}
