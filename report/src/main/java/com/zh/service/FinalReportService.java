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

    public List<FinalReport> getInfoByReportId(Integer report_id){
        return finalReportDAO.getInfoByReportId(report_id);
    }

}
