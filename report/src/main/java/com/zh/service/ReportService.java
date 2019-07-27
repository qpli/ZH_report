package com.zh.service;

import com.zh.DAO.ReportDAO;
import com.zh.Entity.FinalReport;
import com.zh.Entity.ReportInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: lisq
 * @Date: 2019/7/25 14:43
 * @Description:
 */
@Service
public class ReportService {

    @Autowired
    ReportDAO reportDAO;

    /**
     * 根据报表名判断该报表是否存在
     * @param reportName
     * @return
     */
    public boolean isExitByName(String reportName){
        ReportInfo reportInfo = reportDAO.selectByName(reportName);
        if (reportInfo == null) return false;
        else return  true;
    }

    /**
     * 根据报表名查询报表
     * @param reportName
     * @return
     */
    public ReportInfo selectByName(String reportName){
        return reportDAO.selectByName(reportName);
    }

    /**
     * 新添加一个报表
     * @param reportInfo
     * @return
     */
    public int addReport(ReportInfo reportInfo){
        return reportDAO.add(reportInfo);
    }

    /**
     * 获取树状报表
     * @param empId
     * @return
     */
    public Map<String, List<ReportInfo>> getAllReportInTeam(String empId){
        Map<String, List<ReportInfo>> map = new HashMap<String, List<ReportInfo>>();
        if (empId.equals("admin")){//admin用户获取当前所有报表
            List<ReportInfo> list = reportDAO.getAllReport();
            if (list == null) return map;
            for (ReportInfo rep: list) {
                if (map.containsKey(rep.getOrgName())){//如果团队已存在；添加报表
                    List<ReportInfo> temp = map.get(rep.getOrgName());
                    temp.add(rep);
                    map.put(rep.getOrgName(),temp);
                }else {//团队还不存在，新增加一个map元素
                    List<ReportInfo> temp = new ArrayList<>();
                    temp.add(rep);
                    map.put(rep.getOrgName(),temp);
                }
            }
            return map;
        }else {//特定用户获取用户所在团队的所有报表
            List<ReportInfo> rep = reportDAO.getAllReportInteam(empId);
            if (rep != null){
                map.put(rep.get(0).getOrgName(),rep);
            }
            return map;
        }
    }

    /**
     * 根据报表名称获取报表名字
     * @param reportId
     * @return
     */
    public String getReportName(Integer reportId){
        return reportDAO.getReportInfo(reportId).getReportName();
    }

}
