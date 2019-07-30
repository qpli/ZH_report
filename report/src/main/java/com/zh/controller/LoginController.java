package com.zh.controller;


import com.zh.Entity.Employee;
import com.zh.Entity.HostHolder;
import com.zh.service.Emp_service;
import com.zh.util.ReportUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
/**
 * Created by lqp on 2019/7/23
 */
@Controller
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    Emp_service emp_service;

    @Autowired
    ReportController reportController;

    @Autowired
    HostHolder hostHolder;

    /**
     * 登陆页面路由
     * @return
     */
    @RequestMapping(path = {"/","/index"})
    @ResponseBody
    public ModelAndView loginIndex(){
        ModelAndView view = new ModelAndView("/login.html");
        return view;
    }

    /**
     * Top组件路由
     * @return
     */
    @RequestMapping(path = {"/Ttop"})
    @ResponseBody
    public ModelAndView Top(){
        logger.info("进入TOP路由");
        ModelAndView view = new ModelAndView("/T_top.html");
        Employee user = hostHolder.getUser();
        view.addObject("user",user);
        view.addObject("reportLists",reportController.allReport());
        return view;
    }

    /**
     * Top组件路由
     * @return
     */
    @RequestMapping(path = {"/testOfflineUploadTable"})
    @ResponseBody
    public ModelAndView T_offlineUploadTable_New(){
        ModelAndView view = new ModelAndView("/T_offlineUploadTable_New.html");
        Employee user = hostHolder.getUser();
        view.addObject("user",user);
        view.addObject("reportLists",reportController.allReport());
        return view;
    }


    /**
     * 离线上传页面路由
     * @return
     */
    @GetMapping(path = {"/loginSuccess"})
    @ResponseBody
    public ModelAndView loginSuccessIndex(){
        ModelAndView view = new ModelAndView("/C_offlineCreateTable.html");
        return view;
    }

    /**
     * 注册页面路由
     * @return
     */
    @GetMapping(path = {"/regIndex"})
    @ResponseBody
    public ModelAndView regIndex(){
        ModelAndView view = new ModelAndView("/Register.html");
        return view;
    }


    /**
     * 审核页面路由
     * @return
     */
    @GetMapping(path = {"/checkTable1"})
    @ResponseBody
    public ModelAndView checkTable1(){
        ModelAndView view = new ModelAndView("/C_checkTable1.html");
        Employee user = hostHolder.getUser();
        view.addObject("user",user);
        view.addObject("reportLists",reportController.allReport());
        return view;
    }

    /**
     * 测试注册
     * @param username
     * @param password
     * @param empId
     * @param rememberme
     * @return
     */
    @RequestMapping(path = {"/reg1"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String reg(@RequestParam("username") String username,
                      @RequestParam("password") String password,
                      @RequestParam("empId") String empId,
                      @RequestParam(value="rember", defaultValue = "0") int rememberme){
        Employee emp = new Employee();
        emp.setName(username);
        emp.setRoleId(1);
        emp.setOrgId(1);
        emp.setEmpId(empId);
        emp.setSalt("sddfds");
        emp.setPassword(password);
        int res =  emp_service.addEmp(emp);

        return " "+res;
    }


    /**
     * 用户注册
     * @param username
     * @param password
     * @param empId
     * @param rememberme
     * @param response
     * @return
     */
    @RequestMapping(path = {"/reg"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String reg(@RequestParam("empName") String username,
                      @RequestParam("password") String password,
                      @RequestParam("empId") String empId,
                      @RequestParam("orgName") String orgName,
                      @RequestParam("roleId") Integer orgId,
                      @RequestParam(value = "updateOrgEmpFlag",defaultValue = "0") Integer flag,
                      @RequestParam(value="rember", defaultValue = "0") int rememberme,
                      HttpServletResponse response) {
        try {
            Map<String, Object> map = emp_service.register(empId, password,username,orgName,orgId,flag);
            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                cookie.setPath("/");
                if (rememberme > 0) {
                    cookie.setMaxAge(3600*24*5);
                }
                response.addCookie(cookie);
                return ReportUtil.getJSONString(0, "注册成功");
            } else {
                return ReportUtil.getJSONString(1, map);
            }

        } catch (Exception e) {
            logger.error("注册异常" + e.getMessage());
            return ReportUtil.getJSONString(1, "注册异常");
        }
    }


    /**
     *  登陆功能
     * @param emp_id
     * @param password
     * @param rememberme
     * @param response
     * @return
     */
    @RequestMapping(path = {"/login"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String login(@RequestParam("emp_id") String emp_id,
                        @RequestParam("password") String password,
                        @RequestParam(value="rember", defaultValue = "0") int rememberme,
                        HttpServletResponse response) {
        try {
            Map<String, Object> map = emp_service.login(emp_id, password);
            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                cookie.setPath("/");
                if (rememberme > 0) {
                    cookie.setMaxAge(3600*24*5);
                }
                response.addCookie(cookie);

                return ReportUtil.getJSONString(0, "登录成功");
            } else {
                return ReportUtil.getJSONString(1, map);
            }

        } catch (Exception e) {
            logger.error("登录异常功" + e.getMessage());
            return ReportUtil.getJSONString(1, "登录异常");
        }
    }

    @RequestMapping(path = {"/logout"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String logout(@CookieValue("ticket") String ticket) {
        emp_service.logout(ticket);
        return "redirect:/";
    }
}
