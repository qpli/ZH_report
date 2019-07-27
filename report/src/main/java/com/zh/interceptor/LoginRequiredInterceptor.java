package com.zh.interceptor;

import com.zh.Entity.HostHolder;
import com.zh.controller.LoginController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by lqp on 2019/7/23
 */
@Component
public class LoginRequiredInterceptor implements HandlerInterceptor{
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

        @Autowired
        private HostHolder hostHolder;

        @Override
        public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
            //这个拦截器可以让没有登陆的用户无法访问某些页面、
            logger.info("进入登陆拦截器，当前用户为：" +hostHolder.getUser());
            if (hostHolder.getUser() == null) {
                httpServletResponse.sendRedirect("http://localhost:8081/");
                return false;
            }
            return true;
        }

        @Override
        public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        }

        @Override
        public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        }
    }


