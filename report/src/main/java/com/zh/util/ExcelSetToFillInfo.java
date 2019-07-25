package com.zh.util;

import com.zh.Entity.Excel.ExcelSet;
import com.zh.Entity.Excel.ExcelSheet;
import com.zh.Entity.FillInfo;
import com.zh.Entity.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by lqp on 2019/7/25
 */
public class ExcelSetToFillInfo {

    @Autowired
    HostHolder hostHolder;

    public static List<FillInfo> excelToFillInfo(ExcelSet excelSet,Integer reportID,String EmpID){
        List<FillInfo> fillInfolist = new LinkedList<>();

        List<ExcelSheet> excelSheetList = excelSet.getSheets();
        ExcelSheet excelSheet = excelSheetList.get(0);
        List<List<String>> content = excelSheet.getContent();
        for(int i=0;i<content.get(0).size();i++){
            FillInfo fillInfo = new FillInfo();
            StringBuilder fillInfo_context = new StringBuilder();
            for(int j = 1;j<content.size();j++){
                fillInfo_context.append(content.get(j).get(i));
                fillInfo_context.append(",");
            }
            fillInfo.setContext(fillInfo_context.toString());

            fillInfo.setColId(i);  //如何获取列ID
            fillInfo.setDelFlag(0);
            fillInfo.setEmpID(EmpID);  //如何获取用户名
            fillInfo.setFillDatetime(new Date());
//            fillInfo.setFillId(i+10);
            fillInfo.setStatus(1);
            fillInfolist.add(fillInfo);
        }
        return fillInfolist;
    }
}
