package com.zh.service;

import com.zh.DAO.LoginTicketDao;
import com.zh.DAO.empDAO;
import com.zh.Entity.Employee;
import com.zh.Entity.LoginTicket;
import com.zh.Entity.Organization;
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
    private OrgService orgService;
    @Autowired
    private LoginTicketDao loginTicketDao;

    public Employee findAllEmp(){
        return empdao.findAllEmp().get(0);
    }

    public int addEmp(Employee emp){
        return empdao.addUser(emp);
    }


    public Map<String, Object> register(String empId, String password,String empName,String orgName,Integer role_Id,Integer flag) {
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isBlank(empId)) {
            map.put("msgId", "用户ID不能为空");
            return map;
        }
        if (StringUtils.isBlank(empName)) {
            map.put("msgusename", "用户名不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("msgpwd", "密码不能为空");
            return map;
        }
        if (StringUtils.isBlank(orgName)) {
            map.put("magorg", "所属团队不能为空");
            return map;
        }
        if (role_Id==null) {
            map.put("magrole", "角色名不能为空");
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

        emp.setRoleId(role_Id);
        emp.setSalt(UUID.randomUUID().toString().substring(0, 5));
        emp.setPassword(ReportUtil.MD5(password + emp.getSalt()));

        if(emp.getRoleId()==0){
            System.out.println("注册普通员工成功11111");

            if(orgService.selectByOrgName(orgName)==null||orgService.selectByOrgName(orgName).equals(null)){
                map.put("msgorg", "普通用户注册，所属团队不存在");
                return map;
            }
            else {
                emp.setOrgId(orgService.selectByOrgName(orgName).getOrgId());
                empdao.addUser(emp);
            }
        }
        //如果角色为团队长
        if(emp.getRoleId()==1){
            if(orgService.selectByOrgName(orgName)==null||orgService.selectByOrgName(orgName).equals(null)){
                //团队长填的团队名不存在，则需要插入该团队至团队表
                Organization organization = new Organization();
                organization.setEmpId(empId);
                organization.setOrgName(orgName);
                orgService.addOrg(organization);

                emp.setOrgId(orgService.selectByOrgName(orgName).getOrgId());
                empdao.addUser(emp);
            }

            else {
                if(flag==0){
                    map.put("msgorg", "该团队已经有团队长，不能重复覆盖");
                    return map;
                }
                else {
                    orgService.updateOrg(empId,orgName);
                    emp.setOrgId(orgService.selectByOrgName(orgName).getOrgId());
                    empdao.addUser(emp);
                }
            }
        }

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
