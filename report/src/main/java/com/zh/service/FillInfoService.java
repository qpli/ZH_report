package com.zh.service;

import com.alibaba.fastjson.JSON;
import com.zh.DAO.FillInfoDAO;
import com.zh.DAO.FinalReportDAO;
import com.zh.DAO.ReportDAO;
import com.zh.Entity.FillInfo;
import com.zh.Entity.FinalReport;
import com.zh.Entity.ReportInfo;
import com.zh.controller.LoginController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lqp on 2019/7/25
 */
@Service
public class FillInfoService {
    private static final Logger logger = LoggerFactory.getLogger(FillInfoService.class);

    @Autowired
    FillInfoDAO fillInfoDAO;
    @Autowired
    ReportDAO reportDAO;
    @Autowired
    FinalReportDAO finalReportDAO;

    /**
     * 将报表列数据写入数据库中
     * @param fillInfoList
     * @return
     */
    public int addFileInfo(List<FillInfo> fillInfoList){
        System.out.println("进入addfill函数中");
        for(int i=0;i<fillInfoList.size();i++){
            System.out.println("进入addfill函数循环体中");
            if(existColIdAndEmpId(fillInfoList.get(i).getColId(),fillInfoList.get(i).getEmpID())){
                System.out.println("updateFillInfo表");
                fillInfoDAO.updataFillInfo(fillInfoList.get(i));
            }
            else{
                System.out.println("addFillInfo表");
                fillInfoDAO.addFileInfo(fillInfoList.get(i));
            }
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
        if(list==null||list.size()==0)
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

    /**
     * 将审核通过的数据填充到final_report表中
     */
    public void fromFillToFinalReport(Integer reportId){
        //查询该报表的业务主键信息
        ReportInfo reportInfo = reportDAO.getReportInfo(reportId);
        String bussKey = reportInfo.getBussKey();
        String[] keys = bussKey.split(",");//用，分割
        //查询该报表审核通过的列填写信息
        List<FillInfo> fillInfos = fillInfoDAO.getPassFillInfo(reportId);
        //按填写人分组
        Map<String, List<FillInfo>> map = groupByEmpId(fillInfos);
        for (List<FillInfo> list : map.values()) {
            Integer cols = list.get(0).getContext().split(",").length;//用，分割
            String[][] colNums = new String[21][cols];
            for (FillInfo fill: list) {
                String[] con = fill.getContext().split(",");//用，分割
                for (int i = 0;i<cols;i++){
                    colNums[fill.getColLoc()][i] =con[i];
                }
            }
            for(int i = 0;i<colNums[0].length;i++){
                String[] fillInfo = new String[21];//对应final_report中的一行数据，从第1列开始
                for (int j = 1;j<fillInfo.length;j++){
                    fillInfo[j] = colNums[j][i];
                }
                //判断业务主键在数据库中是否存在
                String sqlClause = jointWhereClause(fillInfo,keys);
                FinalReport finalReport= finalReportDAO.getInfoByBussKey(sqlClause);
                logger.info("业务主键是否存在"+JSON.toJSONString(finalReport));
                FinalReport finReport = convertToFinalReport(fillInfo,list.get(0),finalReport);
                if (finalReport == null){//若不存在，则插入
                    finalReportDAO.insertRowFinalReport(finReport);
                }else {//否则更新
                    finalReportDAO.updateRowFinalReport(finReport);
                }
            }

        }
    }

    /**
     * 将查询的结果按照填写人员进行分组
     * @param fillInfos
     * @return
     */
    public Map<String, List<FillInfo>> groupByEmpId(List<FillInfo> fillInfos){
        Map<String,List<FillInfo>> map = new HashMap<>();
        for ( FillInfo fill: fillInfos){
            if (map.containsKey(fill.getEmpID())){
                List<FillInfo> temp = map.get(fill.getEmpID());
                temp.add(fill);
                map.put(fill.getEmpID(),temp);
            }else {
                List<FillInfo> infos = new ArrayList<>();
                infos.add(fill);
                map.put(fill.getEmpID(),infos);
            }
        }
        return map;
    }

    /**
     * 给sql语句拼接where子句
     * @return
     */
    private String jointWhereClause(String[] fillInfo, String[] keys){
        String sqlWhere = "";
        for (int i = 0;i<keys.length;i++){
            int value = Integer.valueOf(keys[i]);
            if (sqlWhere.equals("")){
                sqlWhere = sqlWhere + "COL"+value+" = '"+fillInfo[value]+"'";
            }else {
                sqlWhere = sqlWhere + " and COL"+value+" = '"+fillInfo[value]+"'";
            }
        }
        return sqlWhere;
    }

    /**
     * 转换为final_report对象
     * @param finReport
     * @param fill
     * @param sourceFinal 根据业务主键查询的finalReport对象
     * @return
     */
    public FinalReport convertToFinalReport(String[] finReport,FillInfo fill,FinalReport sourceFinal){
        FinalReport finalReport;
        if (sourceFinal == null){
            finalReport = new FinalReport();
            finalReport.setCreatUser(fill.getEmpID());
            finalReport.setCreatTime(fill.getFillDatetime());
            finalReport.setReportId(fill.getReportId());
        }else {
            finalReport = sourceFinal;
            finalReport.setUpdateTime(fill.getFillDatetime());
            finalReport.setUpdateUser(fill.getEmpID());
        }
        if (finReport[1] !=null) finalReport.setCol1(finReport[1]);
        if (finReport[2] !=null) finalReport.setCol2(finReport[2]);
        if (finReport[3] !=null) finalReport.setCol3(finReport[3]);
        if (finReport[4] !=null) finalReport.setCol4(finReport[4]);
        if (finReport[5] !=null) finalReport.setCol5(finReport[5]);
        if (finReport[6] !=null) finalReport.setCol6(finReport[6]);
        if (finReport[7] !=null) finalReport.setCol7(finReport[7]);
        if (finReport[8] !=null) finalReport.setCol8(finReport[8]);
        if (finReport[9] !=null) finalReport.setCol9(finReport[9]);
        if (finReport[10] !=null) finalReport.setCol10(finReport[10]);
        if (finReport[11] !=null) finalReport.setCol11(finReport[11]);
        if (finReport[12] !=null) finalReport.setCol12(finReport[12]);
        if (finReport[13] !=null) finalReport.setCol13(finReport[13]);
        if (finReport[14] !=null) finalReport.setCol14(finReport[14]);
        if (finReport[15] !=null) finalReport.setCol15(finReport[15]);
        if (finReport[16] !=null) finalReport.setCol16(finReport[16]);
        if (finReport[17] !=null) finalReport.setCol17(finReport[17]);
        if (finReport[18] !=null) finalReport.setCol18(finReport[18]);
        if (finReport[19] !=null) finalReport.setCol19(finReport[19]);
        if (finReport[20] !=null) finalReport.setCol20(finReport[20]);
        return finalReport;
    }


}
