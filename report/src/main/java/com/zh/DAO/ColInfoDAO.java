package com.zh.DAO;

import com.zh.Entity.ColInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: lisq
 * @Date: 2019/7/25 14:52
 * @Description:
 */
@Mapper
@Component
public interface ColInfoDAO {
    final String tableName = "col_info";
    final String field = " report_id, col_name, col_loc, status";

    /**
     * 向col_info数据库表中添加报表的列信息
     * @param colInfo
     * @return
     */
    @Insert({"insert into ", tableName,"(",field ,") values (#{reportId},#{colName},#{colLoc},#{status})"})
    int add(ColInfo colInfo);

    @Select({"select col_id from "+tableName+" where REPORT_ID = #{reportId} and col_loc = #{col_loc}" })
    int selectColIDByReportIdAndColLoc(@Param("reportId") Integer reportId,@Param("col_loc") Integer col_loc);

    /**
     * 查询指定报表的列信息
     * @param reportId
     * @return
     */
    @Select({"SELECT *\n" +
            "  FROM ",tableName ," where REPORT_ID = #{reportId} order by col_loc"})
    List<ColInfo> queryExcel(@Param("reportId") Integer reportId);

    /**
     *根据报表名查列名
     * @param reportId
     * @param
     * @return
     */
    @Select({"select col_name from "+tableName+" where REPORT_ID = #{reportId} order by col_loc" })
    List<String> selectColNameByReportId(@Param("reportId") Integer reportId);



}
