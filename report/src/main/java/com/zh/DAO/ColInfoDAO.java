package com.zh.DAO;

import com.zh.Entity.ColInfo;
import com.zh.Entity.ReportInfo;
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

}
