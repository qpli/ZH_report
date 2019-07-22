package com.zh.controller;

import com.zh.service.Emp_service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by lqp on 2019/7/22
 */

@Controller
public class UserController {

    @Autowired
    private Emp_service emp_service;
    @ResponseBody
    @RequestMapping(path = {"/"},method = {RequestMethod.GET})
//    @RequestMapping(path = {"/", "/index"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String getEmpInfo(){
        return emp_service.findAllEmp().getName();
    }

}
