package com.zh.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class testController {

    @RequestMapping(path = {"/","/index"})
    @ResponseBody
    public ModelAndView loginIndex(){
        ModelAndView view = new ModelAndView("/index.html");
        return view;
    }
}
