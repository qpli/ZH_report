package com.zh.util;

import com.zh.Entity.HostHolder;
import com.zh.service.ColInfoService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by lqp on 2019/7/25
 */

public class ExcelSetToFillInfo {

    @Autowired
    HostHolder hostHolder;
    @Autowired
    ColInfoService colInfoService;
/*
    public  List<FillInfo> excelToFillInfo(ExcelSet excelSet,Integer reportID){
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
            if(ExcelSetToFillInfo.isAllNull(fillInfo_context)){
                continue;
            }
            else {
                fillInfo.setContext(fillInfo_context.toString());
                fillInfo.setColId(colInfoService.selectColIDByReportIdAndColLoc(reportID,i));  //如何获取列ID
                fillInfo.setDelFlag(0);
                fillInfo.setEmpID("lisi");  //如何获取用户名
                fillInfo.setFillDatetime(new Date());
                fillInfo.setStatus(1);
                fillInfolist.add(fillInfo);
            }
        }
        return fillInfolist;
    }
*/

    public static boolean isAllNull(StringBuilder sb){
        String[] strs = sb.toString().split(",");
        int nullCount = 0;
        for(int i = 0;i<strs.length;i++){
            if(strs[i].equals("null")){
                nullCount++;
            }
        }
        if(nullCount==strs.length)
            return true;
        else
            return false;
    }
}
