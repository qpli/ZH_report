package com.zh.controller;


import com.zh.service.Emp_service;
import com.zh.util.ReportUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @RequestMapping(path = {"/reg/"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String reg(@RequestParam("username") String username,
                      @RequestParam("password") String password,
                      @RequestParam(value="rember", defaultValue = "0") int rememberme,
                      HttpServletResponse response) {
        try {
            Map<String, Object> map = emp_service.register(username, password);

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
}
