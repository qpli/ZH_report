package com.zh.service;

import com.zh.DAO.LoginTicketDao;
import com.zh.DAO.empDAO;
import com.zh.Entity.Employee;
import com.zh.Entity.LoginTicket;
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

    public int addEmp(Employee emp){
        return empdao.addUser(emp);
    }


    public Map<String, Object> register(String empId, String password,String empName) {
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
//        if( ReportUtil.checkPassword(password)=="弱")
//        {
//            map.put("msgP", "密码强度太弱");
//            return map;
//        }
        System.out.println("开始添加用户");
        //用户名检验，敏感词，特殊符号等
        //密码强度
        emp = new Employee();
        emp.setName(empName);
        emp.setEmpId(empId);
//        emp.setDel_flag(0);
        emp.setOrgId(1);
        emp.setRoleId(1);
        emp.setSalt(UUID.randomUUID().toString().substring(0, 5));
        emp.setPassword(ReportUtil.MD5(password + emp.getSalt()));
        System.out.println("添加用户成功11111");
        empdao.addUser(emp);

        System.out.println("添加用户成功");
        //传入ticket，也就是登录成功
        String ticket = addLoginTicket(emp.getEmpId());
        map.put("ticket", ticket);
        return map;
    }

    private String addLoginTicket(String emp_id) {
        LoginTicket ticket = new LoginTicket();
        ticket.setEmpId(emp_id);
        Date date = new Date();
        date.setTime(date.getTime() + 1000*3600*24);
        ticket.setExpired(date);
        ticket.setStatus(0);
        ticket.setTicket(UUID.randomUUID().toString().replaceAll("-", ""));
        System.out.println("ticket已创建");

        loginTicketDao.addTicket(ticket);
        System.out.println("ticket已添加");
        return ticket.getTicket();
    }



    public Map<String, Object> login(String emp_id, String password) {
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isBlank(emp_id)) {
            map.put("msg", "用户名不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("msg", "密码不能为空");
            return map;
        }
        Employee emp = empdao.selectByEmpId(emp_id);
        if (emp == null) {
            map.put("msg", "用户名不存在");
            return map;
        }
        if (!emp.getPassword().equals(ReportUtil.MD5(password + emp.getSalt()))) {
            map.put("msg", "密码错误");
            return map;
        }


        //传入ticket，也就是登录成功
        System.out.println("emp.getEmp_id():  "+emp.getEmpId()+"name: "+emp.getName());
        String ticket = addLoginTicket(emp_id);

        System.out.println("登陆时已添加ticket");
        map.put("ticket", ticket);
        return map;
    }

    public void logout(String ticket) {
        loginTicketDao.updateStatus(ticket, 1);
    }


}
