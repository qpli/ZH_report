package com.zh.DAO;

import com.zh.Entity.ColInfo;
import com.zh.Entity.FinalReport;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

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
    int selectColIDByReportIdAndColLoc(Integer reportId,Integer col_loc);

    /**
     * 查询指定报表的列信息
     * @param reportId
     * @return
     */
    @Select({"SELECT col_name\n" +
            "  FROM ",tableName ," where REPORT_ID = #{reportId} order by col_loc"})
    String[] queryExcel(Integer reportId);

}
