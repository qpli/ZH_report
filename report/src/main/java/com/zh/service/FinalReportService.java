package com.zh.service;

import com.zh.DAO.FinalReportDAO;
import com.zh.Entity.FinalReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by lqp on 2019/7/29
 */
@Service
public class FinalReportService {
    @Autowired
    FinalReportDAO finalReportDAO;

    public List<FinalReport> getInfoByReportId(Integer report_id,Integer page,Integer limit){
        System.out.println("report_id: "+report_id+" page: "+page+" limit: "+limit);
        int start = (page-1)*limit+1;
        int end = page*limit;
        System.out.println("report_id: "+report_id+" start: "+start+" end: "+end);
        return finalReportDAO.getInfoByReportId(report_id,start,end);
    }

    public  int getCount(Integer reportId){
        return finalReportDAO.getCount(reportId);
    }

}
