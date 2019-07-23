package com.zh.Entity;

/**
 * Created by lqp on 2019/7/22
 */
public class Employee {
    private String emp_id;
    private String name;
    private int org_id;
    private int role_id;
    private String password;
    private String salt;
    private int del_flag;

    public String getEmp_id() {
        return emp_id;
    }

    public void setEmp_id(String emp_id) {
        this.emp_id = emp_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrd_id() {
        return org_id;
    }

    public void setOrd_id(int ord_id) {
        this.org_id = ord_id;
    }

    public int getRole_id() {
        return role_id;
    }

    public void setRole_id(int role_id) {
        this.role_id = role_id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public int getDel_flag() {
        return del_flag;
    }

    public void setDel_flag(int del_flag) {
        this.del_flag = del_flag;
    }
}
