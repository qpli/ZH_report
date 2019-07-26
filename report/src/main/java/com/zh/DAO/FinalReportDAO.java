package com.zh.DAO;

import com.zh.Entity.FinalReport;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * Created by lqp on 2019/7/26
 */
@Mapper
@Component
public interface FinalReportDAO {

    final  String TABLE_NAME = "final_report";
    final String INSET_FIELDS = " report_id, creat_user, creat_time, update_user, update_time, " +
            "col1, col2, col3, col4, col5, col6, col7, col8, col9, col10, col11, col12, col13, " +
            "col14, col15, col16, col17, col18, col19, col20";
    String SELECT_FIELDS = " final_id, del_flag, report_id, creat_user, creat_time, update_user, update_time, " +
            "col1, col2, col3, col4, col5, col6, col7, col8, col9, col10, col11, col12, col13, " +
            "col14, col15, col16, col17, col18, col19, col20";

    /**
     * 将数据插入FinalReport中
     * @param finalReport
     * @return
     */
    @Insert({"insert into ", TABLE_NAME, "(", INSET_FIELDS,
            ") values (#{reportId},#{creatUser},#{creatTime},#{updateUser},#{updateTime}, " +
                    "#{col1},#{col2},#{col3},#{col4},#{col5},#{col6},#{col7},#{col8},#{col9}," +
                    "#{col10},#{col11},#{col12},#{col13},#{col14},#{col15},#{col16},#{col17},#{col18}," +
                    "#{col19},#{col20})"})
    public int insertRowFinalReport(FinalReport finalReport);


    /**
     * 更新FinalReport中的数据
     * @param finalReport
     * @return
     */
    @Insert({"insert into ", TABLE_NAME, "(", INSET_FIELDS,
            ") values (#{reportId},#{creatUser},#{creatTime},#{updateUser},#{updateTime}, " +
                    "#{col1},#{col2},#{col3},#{col4},#{col5},#{col6},#{col7},#{col8},#{col9}," +
                    "#{col10},#{col11},#{col12},#{col13},#{col14},#{col15},#{col16},#{col17},#{col18}," +
                    "#{col19},#{col20})"})
    public int updateRowFinalReport(FinalReport finalReport);



}
