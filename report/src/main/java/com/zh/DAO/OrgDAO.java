package com.zh.DAO;

import com.zh.Entity.Organization;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

/**
 * Created by lqp on 2019/7/29
 */
@Mapper
@Component
public interface OrgDAO {
    final  String TABLE_NAME = "org_info";
    final String INSET_FIELDS = " org_name, emp_id ";
    String SELECT_FIELDS = " org_id, org_name, parent_id, emp_id, del_flag";

    @Insert({"insert into ", TABLE_NAME, "(", INSET_FIELDS,
            ") values (#{orgName},#{empId})"})
    int addOrg(Organization organization);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where org_name=#{orgname}"})
    Organization selectByOrgName(String orgname);

    @Update({"update ",TABLE_NAME," set emp_id = #{empId} where org_name = #{orgName}"})
    int updateOrg(@Param("empId") String empId, @Param("orgName") String orgName);

    @Select({"select org_id from ", TABLE_NAME, " where emp_id=#{emp_id}"})
    Integer selectOrgIdByCreatEmp(String emp_id);

}
