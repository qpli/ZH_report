package com.zh.service;

import com.zh.DAO.FillInfoDAO;
import com.zh.Entity.FillInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by lqp on 2019/7/25
 */
@Service
public class FillInfoService {
    @Autowired
    FillInfoDAO fillInfoDAO;

    /**
     * 将报表列数据写入数据库中
     * @param fillInfoList
     * @return
     */
    public int addFileInfo(List<FillInfo> fillInfoList){
        for(int i=0;i<fillInfoList.size();i++){
            fillInfoDAO.addFileInfo(fillInfoList.get(i));
        }
        return 1;
    }

    /**
     * 根据col_id和emp_id查是不是该列已存在
     * @param col_id
     * @param emp_id
     * @return  存在则返回true,不存在则返回true
     */
    public boolean existColIdAndEmpId(Integer col_id,String emp_id){
        List<FillInfo> list = fillInfoDAO.existColIdAndEmpId(col_id,emp_id);
        if(list==null)
            return false;
        else
            return true;
    }

    /**
     * 查询填了该报表的所有记录
     * @return
     */
    public List<FillInfo> fillReportAll(Integer reportId){
        return fillInfoDAO.selectByReportId(reportId);
    }

    /**
     * 更新填表状态
     * @param fillId
     * @param status
     */
    public void update(Integer fillId,Integer status){
        fillInfoDAO.updateStatus(fillId,status);
    }

}
