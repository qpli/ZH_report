package com.zh.interceptor;

import com.alibaba.fastjson.JSON;
import com.zh.DAO.LoginTicketDao;
import com.zh.DAO.empDAO;
import com.zh.Entity.Employee;
import com.zh.Entity.HostHolder;
import com.zh.Entity.LoginTicket;
import com.zh.controller.LoginController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
@Component
/**
 * Created by lqp on 2019/7/26
 */
public class PassportInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private empDAO userDAO;

    @Autowired
    private LoginTicketDao loginTicketDAO;

    @Autowired
    private HostHolder hostHolder;
/*
    //true继续请求，false拒绝请求
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        //处理用户信息，判断是否有ticket,一个用户一个ticket，但是有时限
        logger.info("进入拦截器");
        String ticket = null;
        if (httpServletRequest.getCookies() != null) {
            for (Cookie cookie : httpServletRequest.getCookies()) {
                if (cookie.getName().equals("ticket")) {
                    ticket = cookie.getValue();
                    break;
                }
            }
            //判断ticket是否过期和无效
            if (ticket != null) {
                Login_ticket loginTicket = loginTicketDAO.selectByTicket(ticket);
                logger.info("进入拦截器，通过EMP获取的用户为："+ JSON.toJSONString(loginTicket));
                if (loginTicket == null || loginTicket.getExpired().before(new Date()) || loginTicket.getStatus() != 0) {
                    return true;
                }
                else {

                    Employee user = userDAO.selectByEmpId(loginTicket.getEmp_id());
                    logger.info("进入拦截器，通过EMP获取的用户为："+user);
                    hostHolder.setUsers(user);
                    logger.info("进入拦截器，通过hostHolder获取的用户为："+hostHolder.getUser());
                    return true;
                    //不能直接放在request里，因为是全局的一个ticket，其他服务想要读取时可能不会用到httprequest请求，
                    // 但是可以注入hostholder来获取用户信息。
                }
            }
        }
        return true;
    }*/

    //true继续请求，false拒绝请求
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        //处理用户信息，判断是否有ticket,一个用户一个ticket，但是有时限
        logger.info("进入拦截器");
        String ticket = null;
        if (httpServletRequest.getCookies() != null) {
            for (Cookie cookie : httpServletRequest.getCookies()) {
                if (cookie.getName().equals("ticket")) {
                    ticket = cookie.getValue();
                    break;
                }
            }
            //判断ticket是否过期和无效
            if (ticket != null) {
                LoginTicket loginTicket = loginTicketDAO.selectByTicket(ticket);
                String emp_id = loginTicketDAO.selectEmpIDByTicket(ticket);

                logger.info("进入拦截器，通过EMP_ID获取的用户为："+ JSON.toJSONString(emp_id));
                logger.info("进入拦截器，通过EMP获取的用户为："+ JSON.toJSONString(loginTicket));
                if (loginTicket == null || loginTicket.getExpired().before(new Date()) || loginTicket.getStatus() != 0) {
                    return true;
                }
                else {

                    Employee user = userDAO.selectByEmpId(emp_id);
                    logger.info("进入拦截器，将当前用户加入hostHolder通过EMP获取的用户为："+JSON.toJSONString(user));
                    hostHolder.setUsers(user);
//                    logger.info("进入拦截器，通过hostHolder获取的用户为："+hostHolder.getUser());
                    return true;
                    //不能直接放在request里，因为是全局的一个ticket，其他服务想要读取时可能不会用到httprequest请求，
                    // 但是可以注入hostholder来获取用户信息。
                }
            }
        }
        return true;
    }


    //渲染之前提供的后处理方法，可以添加模型数据，自动传给前端。
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null && hostHolder.getUser() != null) {
            modelAndView.addObject(hostHolder.getUser());
            hostHolder.clear();
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
