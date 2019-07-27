package com.zh.DAO;

import com.zh.Entity.FinalReport;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
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
     * @return"update ",TABLE_NAME," set status = #{sta} where fill_id = #{fillId}"
     */
    @Update({"update ", TABLE_NAME," set report_id = #{reportId}, creat_user = #{creatUser}, creat_time = #{creatTime}," +
            "update_user = #{updateUser},update_time = #{updateTime}, col1 = #{col1},col2 = #{col2},col3 = #{col3},col4 = #{col4}," +
            "col5 = #{col5},col6 = #{col6},col7 = #{col7},col8 = #{col8},col9 = #{col9},col10 = #{col10},col11 = #{col11}," +
            "col12 = #{col12},col13 = #{col13},col14 = #{col14},col15 = #{col15},col16 = #{col16},col17 = #{col17},col18 = #{col18}," +
            "col19 = #{col19},col20 = #{col20} where final_id = #{finalId}"})
    public int updateRowFinalReport(FinalReport finalReport);

    /**
     * 通过业务主键获取信息
     * @return
     */
    @Select({" select * from ",TABLE_NAME," where ${jointWhereClause}"})
    FinalReport getInfoByBussKey(String jointWhereClause);

}
