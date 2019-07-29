package com.zh.service;

import com.zh.DAO.OrgDAO;
import com.zh.Entity.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by lqp on 2019/7/29
 */
@Service
public class OrgService {

    @Autowired
    OrgDAO orgDAO;

    int addOrg(Organization organization){
        return orgDAO.addOrg(organization);
    };

    Organization selectByOrgName(String  orgname){
        return orgDAO.selectByOrgName(orgname);
    };


    int updateOrg(String empId,String orgName){
        return orgDAO.updateOrg(empId,orgName);
    };

    public Integer selectOrgIdByCreatEmp(String emp_id){
        return orgDAO.selectOrgIdByCreatEmp(emp_id);
    };


}
