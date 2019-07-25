package com.zh.DAO;

import com.zh.Entity.FillInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

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
}
