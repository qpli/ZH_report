package com.zh.util;


import com.alibaba.fastjson.JSON;
import com.zh.Entity.Excel.ExcelSet;
import com.zh.Entity.Excel.ExcelSheet;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
/**
 * Created by lqp on 2019/7/24
 */
public class ExcelUtil {

    private static Logger logger = LoggerFactory.getLogger(ExcelUtil.class);
    /**
     * 解析Excel表格,上传填写后的报表
     *
     * @param path 文件路径
     * @return
     * @throws Exception
     */
    public static ExcelSet resolveExcel(String path) throws Exception {

        ExcelSet excelSet = new ExcelSet();


        //Excel文件
        Workbook workbook = WorkbookFactory.create(new File(path));

        try {

            List<ExcelSheet> sheets = new ArrayList<ExcelSheet>();
            Iterator<Sheet> its = workbook.sheetIterator();
            //处理每个sheet
            while (its.hasNext()) {
                Sheet sheet = its.next();

                ExcelSheet excelSheet = new ExcelSheet();
                excelSheet.setName(sheet.getSheetName());

                List<List<String>> content = new ArrayList<List<String>>();
              //  Iterator<Row> itr = sheet.rowIterator();
                //处理该sheet下每一行
                int numRow = sheet.getLastRowNum();
                Row row0 =  sheet.getRow(0);  //Excel表头
                List<String> contentsOfRow = new ArrayList<String>();
                Iterator<Cell> itc = row0.cellIterator();
                //处理该行每个cell
                while (itc.hasNext()) {
                    Cell cell = itc.next();
//                        添加这一行解决数值类型单元格无法正确读取问题
                    cell.setCellType(CellType.STRING);
                    contentsOfRow.add(cell.toString());
                }
                content.add(contentsOfRow);
                logger.info("第一行单元格数目："+contentsOfRow.size()+"第一行表头："+JSON.toJSONString(contentsOfRow));

                int rowNum = sheet.getLastRowNum();
                logger.info("excel的行数：" +rowNum);
                for(int i=1;i<rowNum+1;i++){
                    XSSFRow row = (XSSFRow) sheet.getRow(i);
                    List<String> list = new LinkedList<>();   //行内容
                    logger.info("第一行列数："+row0.getLastCellNum());
                    for(int j=0;j<row0.getLastCellNum();j++){
                        logger.info("进入列中");
//                        Cell cell = row.getCell(j,Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                        String cell = null;
                            if (row.getCell(j) == null) {
                                // 如果单元格元素为空
                                cell =row.getCell(j)+"";
                                list.add(cell);
                                logger.info("进入catch   cell的内容："+JSON.toJSONString("null"));
                            } else {
                                //如果单元格元素不为空
                                cell = row.getCell(j).toString();
//                        String s= row.getCell(j,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
                                logger.info("进入try   cell的内容："+JSON.toJSONString(cell));
                                list.add(cell);
                            }
                       }
                    logger.info("list的内容："+JSON.toJSONString(list));
                    content.add(list);
                }

                excelSheet.setContent(content);
                sheets.add(excelSheet);
            }
            excelSet.setSheets(sheets);
            excelSet.setExcelFile(new File(path));
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("文件解析错误: " + e.getMessage(), e);
        } finally {
            workbook.close();
        }


        return excelSet;
    }


    /**
     * 解析Excel表格,离线上传模板
     *
     * @param path 文件路径
     * @return
     * @throws Exception
     */
    public static List<String> resolveExcelTemplate(String path) throws Exception {

        ExcelSet excelSet = new ExcelSet();

        List<String> contentsOfRow = new ArrayList<String>();
        //Excel文件
        Workbook workbook = WorkbookFactory.create(new File(path));

        try {
            List<ExcelSheet> sheets = new ArrayList<ExcelSheet>();
            Iterator<Sheet> its = workbook.sheetIterator();
            //处理每个sheet
            while (its.hasNext()) {
                Sheet sheet = its.next();
                ExcelSheet excelSheet = new ExcelSheet();
                excelSheet.setName(sheet.getSheetName());
                //  Iterator<Row> itr = sheet.rowIterator();
                //处理该sheet下每一行
                int numRow = sheet.getLastRowNum();
                Row row0 =  sheet.getRow(0);  //Excel表头

                Iterator<Cell> itc = row0.cellIterator();
                //处理该行每个cell
                while (itc.hasNext()) {
                    Cell cell = itc.next();
//                        添加这一行解决数值类型单元格无法正确读取问题
                    cell.setCellType(CellType.STRING);
                    contentsOfRow.add(cell.toString());
                }
                return contentsOfRow;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("文件解析错误: " + e.getMessage(), e);
        } finally {
            workbook.close();
        }
        return contentsOfRow;
    }


    /**
     * 解析Excel表格
     *
     * @param path 文件路径
     * @return
     * @throws Exception
     */
    public static ExcelSet MyResolveExcel(String path) throws Exception {

        ExcelSet excelSet = new ExcelSet();


        //Excel文件
        Workbook workbook = WorkbookFactory.create(new File(path));

        try {

            List<ExcelSheet> sheets = new ArrayList<ExcelSheet>();
            Iterator<Sheet> its = workbook.sheetIterator();
            //处理每个sheet
            while (its.hasNext()) {
                Sheet sheet = its.next();

                ExcelSheet excelSheet = new ExcelSheet();
                excelSheet.setName(sheet.getSheetName());

                List<List<String>> content = new ArrayList<List<String>>();     //单元格所有内容
                Iterator<Row> itr = sheet.rowIterator();
                //处理该sheet下每一行
                while (itr.hasNext()) {
                    Row row = itr.next();

                    List<String> contentsOfRow = new ArrayList<String>();
                    Iterator<Cell> itc = row.cellIterator();
                    //处理该行每个cell
                    while (itc.hasNext()) {
                        Cell cell = itc.next();
//                        添加这一行解决数值类型单元格无法正确读取问题
                        cell.setCellType(CellType.STRING);
                        contentsOfRow.add(cell.toString());

                    }
                    content.add(contentsOfRow);
                }
                excelSheet.setContent(content);
                sheets.add(excelSheet);
            }
            excelSet.setSheets(sheets);
            excelSet.setExcelFile(new File(path));
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("文件解析错误: " + e.getMessage(), e);
        } finally {
            workbook.close();
        }


        return excelSet;
    }

    /**
     * 获取指定单元格内容
     *
     * @param excelSheet
     * @param row
     * @param col
     * @return
     */
    public static String getExcelCellValue(ExcelSheet excelSheet, int row, int col) {
        return excelSheet.getContent().get(row).get(col).trim();
    }

    /**
     * 获取指定单元格内容
     *
     * @param excelSet
     * @param sheetIndex
     * @param row
     * @param col
     * @return
     */
    public static String getExcelCellValue(ExcelSet excelSet, int sheetIndex, int row, int col) {
        return excelSet.getSheets().get(sheetIndex).getContent().get(row).get(col).trim();
    }


    /**
     * 些内容到指定工作表和单元格
     *
     * @param content
     * @param excelSet
     * @param sheetIndex
     * @param rowIndex
     * @param colIndex
     * @throws Exception
     */
    public static void writeCellToExcelFile(String content, ExcelSet excelSet, int sheetIndex, int rowIndex, int colIndex) throws Exception {

        String filename = excelSet.getExcelFile().getAbsolutePath();

        Workbook workbook = WorkbookFactory.create(new FileInputStream(filename));
        Sheet sheet = workbook.getSheetAt(sheetIndex);
        Row row = sheet.getRow(rowIndex);
        Cell cell = row.getCell(colIndex);
        cell.setCellValue(content);


        FileOutputStream fo = new FileOutputStream(filename);
        try {
            workbook.write(fo);
        } finally {
            fo.flush();
            fo.close();
        }

    }

    /**
     * 将ExcelSet对象存入文件
     *
     * @param excelSet
     * @throws Exception
     */
    public static void saveExcelSetToFile(ExcelSet excelSet) throws Exception {

        String filename = excelSet.getExcelFile().getAbsolutePath();
        Workbook workbook = WorkbookFactory.create(new FileInputStream(filename));

        List<ExcelSheet> sheets = excelSet.getSheets();

        FileOutputStream fo = new FileOutputStream(filename);

        try {
//         每个工作表
            for (int sheetIndex = 0; sheetIndex < sheets.size(); sheetIndex++) {
                Sheet toSheet = workbook.getSheetAt(sheetIndex);
                ExcelSheet sheet = sheets.get(sheetIndex);

                List<List<String>> content = sheet.getContent();
//            每个工作表的每一行
                for (int rowIndex = 0; rowIndex < content.size(); rowIndex++) {
                    Row toRow = toSheet.getRow(rowIndex);
                    List<String> row = content.get(rowIndex);

                    int colIndex = 0;
//                每一行的单元格
                    for (int toColIndex = 0; toColIndex < row.size(); toColIndex++) {
                        Cell toCell = toRow.getCell(toColIndex);
                        String cellValue = row.get(colIndex);


                        if (toCell != null) {
                            if (toCell.getCellTypeEnum().equals(CellType.NUMERIC)) {
                                toCell.setCellValue(Double.parseDouble(cellValue));
                            } else {
                                toCell.setCellValue(cellValue);
                            }

                            colIndex++;
                        }
                    }

                }
            }

            workbook.write(fo);

        } finally {
            fo.flush();
            fo.close();
        }
    }

}
