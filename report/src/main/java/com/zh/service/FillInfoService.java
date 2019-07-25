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

    public int addFileInfo(List<FillInfo> fillInfoList){
        for(int i=0;i<fillInfoList.size();i++){
            fillInfoDAO.addFileInfo(fillInfoList.get(i));
        }
        return 1;
    }

}
