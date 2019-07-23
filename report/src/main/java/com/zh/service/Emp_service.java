package com.zh.service;

import com.zh.DAO.LoginTicketDao;
import com.zh.DAO.empDAO;
import com.zh.Entity.Employee;
import com.zh.Entity.Login_ticket;
import com.zh.util.ReportUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

//import org.apache.tomcat.util.buf.StringUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.rumons.lang.StringUtils;
//import org.springframework.util.StringUtils;

/**
 * Created by lqp on 2019/7/22
 */
@Service

public class Emp_service {
    @Autowired
    private empDAO empdao;

    @Autowired
    private LoginTicketDao loginTicketDao;

    public Employee findAllEmp(){
        return empdao.findAllEmp().get(0);
    }

    public Map<String, Object> register(String empId, String password) {
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isBlank(empId)) {
            map.put("msgusename", "用户名不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("msgpwd", "密码不能为空");
            return map;
        }
        Employee emp = empdao.selectByEmpId(empId);
        if (emp != null) {
            map.put("msgreg", "用户名已被注册");
            return map;
        }
        if( ReportUtil.checkPassword(password)=="弱")
        {
            map.put("msgP", "密码强度太弱");
            return map;
        }

        //用户名检验，敏感词，特殊符号等
        //密码强度
        emp = new Employee();
        emp.setName(empId);
        emp.setSalt(UUID.randomUUID().toString().substring(0, 5));
        emp.setPassword(ReportUtil.MD5(password + emp.getSalt()));
        empdao.addUser(emp);

        //传入ticket，也就是登录成功
        String ticket = addLoginTicket(emp.getEmp_id());
        map.put("ticket", ticket);
        return map;
    }


    private String addLoginTicket(String emp_id) {
        Login_ticket ticket = new Login_ticket();
        ticket.setEmp_id(emp_id);
        Date date = new Date();
        date.setTime(date.getTime() + 1000*3600*24);
        ticket.setExpired(date);
        ticket.setStatus(0);
        ticket.setTicket(UUID.randomUUID().toString().replaceAll("-", ""));
        loginTicketDao.addTicket(ticket);
        return ticket.getTicket();
    }





}
