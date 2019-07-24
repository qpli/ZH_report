package com.zh.Entity.Excel;
import java.io.File;
import java.util.List;
/**
 * Created by lqp on 2019/7/24
 */
public class ExcelSet {
    /**
     * 工作表列表
     */
    private List<ExcelSheet> sheets;

    /**
     * excel文件信息
     */
    private File excelFile;

    public List<ExcelSheet> getSheets() {
        return sheets;
    }

    public void setSheets(List<ExcelSheet> sheets) {
        this.sheets = sheets;
    }

    public File getExcelFile() {
        return excelFile;
    }

    public void setExcelFile(File excelFile) {
        this.excelFile = excelFile;
    }
}
