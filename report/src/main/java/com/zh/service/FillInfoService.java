package com.zh.service;

import com.alibaba.fastjson.JSON;
import com.zh.DAO.FillInfoDAO;
import com.zh.DAO.FinalReportDAO;
import com.zh.DAO.ReportDAO;
import com.zh.Entity.FillInfo;
import com.zh.Entity.FinalReport;
import com.zh.Entity.ReportInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.*;

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
    public int addFileInfo(List<FillInfo> fillInfoList, Integer reportId){

        //查询该报表是否要审核
        Integer isCheck = reportDAO.getReportInfo(reportId).getIsCheck();
        if(isCheck == 1){//如果isCheck是1 表示要审核
            for(int i=0;i<fillInfoList.size();i++){
                if(existColIdAndEmpId(fillInfoList.get(i).getColId(),fillInfoList.get(i).getEmpID())){
                    fillInfoDAO.updataFillInfo(fillInfoList.get(i));
                }
                else{
                    fillInfoDAO.addFileInfo(fillInfoList.get(i));
                }
            }
        }else {//否则不审核，直接插入final_report表中
            insertFinalReport(reportId,fillInfoList);
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
     * 将审核通过的该列数据填充到final_report表中
     */
    public void fromFillToFinalReport(Integer id , Integer reportId){
        //查询该报表审核通过的列填写信息
        List<FillInfo> fillInfos = new ArrayList<>();
        FillInfo fillInfo = fillInfoDAO.getPassFillInfo(id);
        fillInfos.add(fillInfo);
        //查询该报表的业务主键列填写信息加入到里面去
        //获取报表业务主键信息
        String[] bussKeys = bussKeys(reportId);
        for (String key: bussKeys) {
            FillInfo fillIn = fillInfoDAO.getKeyColFill(fillInfo.getEmpID(),Integer.valueOf(key),reportId);
            if (fillIn != null){
                fillInfos.add(fillIn);
            }
        }
        if (fillInfos.size() == 1) return;
        insertFinalReport(reportId,fillInfos);
    }

    /**
     * 将FillInfo的lis对象插入到final_report表中
     * @param reportId
     * @param fillInfos
     */
    public void insertFinalReport(Integer reportId,List<FillInfo> fillInfos){
        String[] keys = bussKeys(reportId);
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
     * 查询指定报表的业务主键信息
     * @param reportId
     * @return
     */
    public String[] bussKeys(Integer reportId){
        ReportInfo reportInfo = reportDAO.getReportInfo(reportId);
        String bussKey = reportInfo.getBussKey();
        String[] keys = bussKey.split(",");//用，分割
        return  keys;
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

    /**
     * 在线填写插入到final_report
     * @param finalReport
     */
    public void onlineInsert(FinalReport finalReport,String empId){
        String[] keys = bussKeys(finalReport.getReportId());
        String[] fillInfo = objectToString(finalReport);
        //判断业务主键在数据库中是否存在
        String sqlClause = jointWhereClause(fillInfo,keys);
        FinalReport fReport= finalReportDAO.getInfoByBussKey(sqlClause);
        if (fReport == null){//若不存在，则插入
            finalReport.setCreatTime(new Date());
            finalReport.setCreatUser(empId);
            finalReportDAO.insertRowFinalReport(finalReport);
        }else {//否则更新
            try {
                finalReport = updateBean(fReport,finalReport);
                finalReport.setUpdateUser(empId);
                finalReport.setUpdateTime(new Date());
                finalReportDAO.updateRowFinalReport(finalReport);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 解决更新时，源对象被覆盖,利用反射
     * @param src 数据库查出的源对象数据
     * @param desc 前端传的需要更新的数据
     * @return 更新后的对象
     * @throws Exception 源对象与目标对象类型不一致
     */
    public static FinalReport updateBean(FinalReport src,FinalReport desc) throws Exception {
        Class<?> srcClass = src.getClass();
        Class<?> descClass = desc.getClass();
        if(!descClass.equals(srcClass)){
            throw new Exception("源对象与目标对象类型不一致！");
        }
        Method[] descClassDeclaredMethods = descClass.getDeclaredMethods();
        Method[] srcClassDeclaredMethods = srcClass.getDeclaredMethods();
        for(Method descMethod : descClassDeclaredMethods){
            if (descMethod.getName().startsWith("get")){
                Object invoke = descMethod.invoke(desc);
                if ((invoke != null) && !"".equals(invoke.toString().trim())){
                    String methodSetMethod = "set"+descMethod.getName().substring(3);
                    for (Method srcMethod : srcClassDeclaredMethods){
                        if (srcMethod.getName().equalsIgnoreCase(methodSetMethod)){
                            srcMethod.invoke(src,invoke);
                        }
                    }
                }
            }
        }
        return src;
    }

    /**
     * 将final_report对象的列信息转换为数组
     * @param finReport
     * @return
     */
    private String[] objectToString(FinalReport finReport){
        String[] fillInfo = new String[21];
        fillInfo[1] = finReport.getCol1();
        fillInfo[2] = finReport.getCol2();
        fillInfo[3] = finReport.getCol3();
        fillInfo[4] = finReport.getCol4();
        fillInfo[5] = finReport.getCol5();
        fillInfo[6] = finReport.getCol6();
        fillInfo[7] = finReport.getCol7();
        fillInfo[8] = finReport.getCol8();
        fillInfo[9] = finReport.getCol9();
        fillInfo[10] = finReport.getCol10();
        fillInfo[11] = finReport.getCol11();
        fillInfo[12] = finReport.getCol12();
        fillInfo[13] = finReport.getCol13();
        fillInfo[14] = finReport.getCol14();
        fillInfo[15] = finReport.getCol15();
        fillInfo[16] = finReport.getCol16();
        fillInfo[17] = finReport.getCol17();
        fillInfo[18] = finReport.getCol18();
        fillInfo[19] = finReport.getCol19();
        fillInfo[20] = finReport.getCol20();
        return fillInfo;
    }
}
