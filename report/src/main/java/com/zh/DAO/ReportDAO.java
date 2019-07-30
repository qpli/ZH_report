package com.zh.DAO;

import com.zh.Entity.ReportInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: lisq
 * @Date: 2019/7/25 14:43
 * @Description:
 */
@Mapper
@Component
public interface ReportDAO {
    final String tableName = "report_info";
    final String field = " emp_id, report_name, creat_time, busskey,is_check";

    /**
     * 向数据库中添加一个新的报表信息
     * @param reportInfo
     * @return
     */
    @Insert({"insert into ", tableName,"(",field ,") values (#{empId},#{reportName},#{creattime},#{bussKey},#{isCheck})"})
    int add(ReportInfo reportInfo);

    /**
     * 根据报表名称查询
     * @param reportName
     * @return
     */
    @Select({"select * from ",tableName, " where report_name = #{reportName}"})
    ReportInfo selectByName(String reportName);

    /**
     * 查询所有报表
     * @return
     */
    @Select({"SELECT rep.REPORT_ID,org.ORG_NAME,rep.REPORT_NAME " +
            "FROM report_info rep,org_info org " +
            "where rep.EMP_ID = org.EMP_ID"})
    List<ReportInfo> getAllReport();

    /**
     * 查询员工所在团队的所有报表
     * @param empId
     * @return
     */
    @Select({"SELECT rep.REPORT_ID,org.ORG_NAME,rep.REPORT_NAME \n" +
            "FROM report_info rep,org_info org ,EMP_INFO emp\n" +
            "where emp.EMP_ID = #{empId} and emp.ORG_ID = org.ORG_ID and rep.EMP_ID = org.EMP_ID"})
    List<ReportInfo> getAllReportInteam(String empId);

    /**
     *
     * @param reportId
     * @return
     */
    @Select({"select * \n" +
            "  FROM ",tableName," where REPORT_ID = #{reportId}"})
    ReportInfo getReportInfo(Integer reportId);


    /**
     * 根据报表ID查创建该报表的人
     * @param reportId
     * @return
     */
    @Select({"select emp_id \n" +
            "  FROM ",tableName," where REPORT_ID = #{reportId}"})
    String getCreateEmp(Integer reportId);


}
