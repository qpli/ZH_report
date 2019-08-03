package com.zh.service;

import com.zh.DAO.ColInfoDAO;
import com.zh.Entity.ColInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: lisq
 * @Date: 2019/7/25 15:02
 * @Description:
 */
@Service
public class ColInfoService {

    @Autowired
    ColInfoDAO colInfoDAO;

    /**
     * 添加对应报表的列信息
     * @param reportId
     * @param colNames
     */
    public void addColInfo(Integer reportId,String[] colNames){
        for (int i = 0;i<colNames.length;i++){
            ColInfo colInfo = new ColInfo();
            colInfo.setReportId(reportId);
            colInfo.setColName(colNames[i]);
            colInfo.setColLoc(i+1);
            colInfo.setStatus(0);
            colInfoDAO.add(colInfo);
        }
    }

    public Integer selectColIDByReportIdAndColLoc(Integer reportId,Integer col_loc){
        return colInfoDAO.selectColIDByReportIdAndColLoc(reportId,col_loc+1);
    }

    /**
     * 获取指定报表模板
     * @param reportId
     * @return
     */
    public List<ColInfo> queryColInfo(Integer reportId){
        return colInfoDAO.queryExcel(reportId);
    }
    public String[] queryExcel(Integer reportId){
        List<ColInfo> colInfos =  colInfoDAO.queryExcel(reportId);
        String[] colNames = new String[colInfos.size()];
        for (int i = 0;i<colInfos.size();i++) {
            ColInfo colInfo = colInfos.get(i);
            colNames[i] = colInfo.getColName();
        }
        return colNames;
    }

    /**
     * 根据报表ID获取列名
     * @param reportId
     * @return
     */
    public List<String> selectColNameByReportId( Integer reportId){
        return colInfoDAO.selectColNameByReportId(reportId);
    }

}
