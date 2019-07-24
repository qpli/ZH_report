package com.zh.service.Impl;

import com.zh.Entity.Excel.ExcelSet;
import com.zh.MyException.CommonException;
import com.zh.service.ResolveExcelService;
import com.zh.util.ExcelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by lqp on 2019/7/24
 */
@Service
public class ResolveExcelServiceImpl implements ResolveExcelService {

    private static Logger logger = LoggerFactory.getLogger(ResolveExcelServiceImpl.class);

    @Override
    public ExcelSet resolveExcel(String path) {
        logger.info("开始解析excel文件: {}", path);

        ExcelSet excelSet = new ExcelSet();
        try {
            excelSet = ExcelUtil.resolveExcel(path);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CommonException("文件解析错误: " + e.getMessage(), e);
        }
        logger.info("解析excel文件结束: {}", path);
        return excelSet;
    }
}
