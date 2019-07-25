package com.zh.DAO;

import com.zh.Entity.FillInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by lqp on 2019/7/24
 */
@Mapper
@Component
public interface FillInfoDAO {


    final  String TABLE_NAME = "fill_info";
    final String INSET_FIELDS = " col_id, emp_id, context, fillDatetime, status";
    String SELECT_FIELDS = " fill_id, col_id, emp_id, context, fillDatetime, status, del_flag";

    @Insert({"insert into ", TABLE_NAME, "(", INSET_FIELDS,
            ") values (#{colId},#{empID},#{context},#{fillDatetime},#{status})"})
    //@Options(keyProperty="fill_id",keyColumn="fillId",useGeneratedKeys=true)
    int addFileInfo(FillInfo fillInfo);

    /**
     * 团队长审核页面的展示结果
     * @param reportId
     * @return
     */
    @Select({"select fill.fill_id,fill.EMP_ID,emp.NAME,col.col_name,fill.fillDatetime,fill.status " +
            "from col_info col, fill_info fill,EMP_INFO emp " +
            "where col.REPORT_ID = #{reportId} and col.col_ID = fill.col_ID and emp.EMP_ID = fill.EMP_ID " +
            "group by col.col_name,emp.NAME,fill.EMP_ID,fill.fillDatetime,fill.fill_id,fill.status"})
    List<FillInfo> selectByReportId(Integer reportId);

    /**
     * 更改团队长审核状态
     * @param id
     * @param status  update ZH_report.dbo.fill_info set status = 1 where fill_id = 2
     */
    @Select({"select status from ",TABLE_NAME," where fill_id = #{fillId}"})
    FillInfo updateStatus(Integer fillId);
}
