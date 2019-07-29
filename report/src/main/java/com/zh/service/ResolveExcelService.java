package com.zh.service;

import com.zh.Entity.Excel.ExcelSet;

import java.util.List;

/**
 * Created by lqp on 2019/7/24
 */
public interface ResolveExcelService {
    /**
     * @param path excel文件路径
     * @return 解析结果
     */
    ExcelSet resolveExcel(String path);

    List<String> resolveExcelTemplate(String path);
}
