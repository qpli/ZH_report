package com.zh.DAO;

import com.zh.Entity.ReportInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

/**
 * @Author: lisq
 * @Date: 2019/7/25 14:43
 * @Description:
 */
@Mapper
@Component
public interface ReportDAO {
    final String tableName = "report_info";
    final String field = " emp_id, report_name, creat_time, busskey";

    /**
     * 向数据库中添加一个新的报表信息
     * @param reportInfo
     * @return
     */
    @Insert({"insert into ", tableName,"(",field ,") values (#{empId},#{reportName},#{creattime},#{bussKey})"})
    int add(ReportInfo reportInfo);

    /**
     * 根据报表名称查询
     * @param reportName
     * @return
     */
    @Select({"select * from ",tableName, " where report_name = #{reportName}"})
    ReportInfo selectByName(String reportName);


}
