package com.zh.service;

import com.zh.DAO.empDAO;
import com.zh.Entity.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by lqp on 2019/7/22
 */
@Service

public class Emp_service {
    @Autowired
    private empDAO empdao;

    public Employee findAllEmp(){
        return empdao.findAllEmp().get(0);
    }

}
