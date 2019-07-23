package com.zh.Entity;

import org.springframework.stereotype.Component;

/**
 * Created by lqp on 2019/7/23
 */
@Component
public class HostHolder {
    //使用threadlocal，保证多线程中的数据独占。
    private static ThreadLocal<Employee> users = new ThreadLocal<Employee>();

    public Employee getUser() {
        return users.get();
    }

    public void setUsers(Employee user) {
        users.set(user);
    }

    public void clear() {
        users.remove();
    }
}
