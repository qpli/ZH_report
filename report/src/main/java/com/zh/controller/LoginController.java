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
        ModelAndView view = new ModelAndView("/employee/T_top.html");
        Employee user = hostHolder.getUser();
        view.addObject("user",user);
        view.addObject("reportLists",reportController.allReport());
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
//                return "redirect:/successIndex";
                return ReportUtil.getJSONString(0, "注册成功");
            } else {
//                return "redirect:/reg";
                return ReportUtil.getJSONString(1, (String) map.get("msg"));
            }

        } catch (Exception e) {
            logger.error("注册异常" + e.getMessage());
//            return "redirect:/reg";
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
