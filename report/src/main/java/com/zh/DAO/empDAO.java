package com.zh.DAO;

import com.zh.Entity.Employee;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by lqp on 2019/7/22
 */
@Mapper
@Component
public interface empDAO {
    final  String TABLE_NAME = "emp_info";
     final String INSET_FIELDS = " name, password, salt, role_id, org_id";
     String SELECT_FIELDS = " id, name, password, salt, role_id, org_id,del_flag";


    @Insert({"insert into ", TABLE_NAME, "(", INSET_FIELDS,
            ") values (#{name},#{password},#{salt},#{headUrl})"})
    int addUser(Employee emp);

    @Select({"select * from ", TABLE_NAME })
    List<Employee> findAllEmp( );
}
