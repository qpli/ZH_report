package com.zh.DAO;

import com.zh.Entity.FillInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.*;
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

    /**
     * 团员填写列信息写入数据库
     * @param fillInfo
     * @return
     */
    @Insert({"insert into ", TABLE_NAME, "( col_id, emp_id, context, fillDatetime ) values (#{colId},#{empID},#{context},#{fillDatetime})"})
    //@Options(keyProperty="fill_id",keyColumn="fillId",useGeneratedKeys=true)
    int addFileInfo(FillInfo fillInfo);

    /**
     *
     * @param fillInfo
     * @return
     */
    @Update({"update ",TABLE_NAME," set context = #{context} , fillDatetime = #{fillDatetime} where col_id = #{colId} and emp_id = #{empID} "})
    int updataFillInfo(FillInfo fillInfo);
    /**
     * 判断该列是否已经存在
     * @param col_id
     * @param emp_id
     * @return
     */
    @Select({"select col_id, emp_id from "+TABLE_NAME+" where col_id = #{col_id} and emp_id = #{emp_id}"})
    List<FillInfo> existColIdAndEmpId(@Param("col_id") Integer colId,@Param("emp_id") String emp_id);

    /**
     * 团队长审核页面的展示结果
     * @param reportId
     * @return
     */
    @Select({"select fill.fill_id,fill.EMP_ID,emp.NAME,col.col_name,fill.fillDatetime,fill.status,col.col_loc " +
            "from col_info col, fill_info fill,EMP_INFO emp " +
            "where col.REPORT_ID = #{reportId} and col.col_ID = fill.col_ID and emp.EMP_ID = fill.EMP_ID " +
            "group by col.col_name,emp.NAME,fill.EMP_ID,fill.fillDatetime,fill.fill_id,fill.status,col.col_loc"})
    List<FillInfo> selectByReportId(Integer reportId);

    /**
     * 更改团队长审核状态
     * @param fillId
     * @param sta  update ZH_report.dbo.fill_info set status = 1 where fill_id = 2
     */
    @Update({"update ",TABLE_NAME," set status = #{sta} where fill_id = #{fillId}"})
    int updateStatus(@Param("fillId") Integer fillId, @Param("sta") Integer sta);

    /**
     * 查询特定报表中所有审核通过的列填写信息
     * @return
     */
    @Select({"SELECT fill.col_ID,fill.EMP_ID,fill.context,fill.fillDatetime,col.REPORT_ID,col.col_loc\n" +
            "  FROM fill_info fill,col_info col \n" +
            "  where fill.Fill_ID = #{id} and col.col_ID = fill.col_ID"})
    FillInfo getPassFillInfo(Integer id);

    /**
     * 查询指定用户填写业务主键列信息
     * @return
     */
    @Select({"SELECT fill.col_ID,fill.EMP_ID,fill.context,fill.fillDatetime,col.REPORT_ID,col.col_loc\n" +
            "  FROM col_info col,fill_info fill\n" +
            "  where  fill.EMP_ID = #{empId} and col.col_loc = #{key} and col.col_ID = fill.col_ID and col.REPORT_ID = #{reportId}"})
    FillInfo getKeyColFill(String empId,Integer key,Integer reportId);
}
