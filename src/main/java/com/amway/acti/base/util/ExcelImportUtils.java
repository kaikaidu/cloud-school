package com.amway.acti.base.util;

import com.amway.acti.model.ExcelCustom;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.DecimalFormat;
import java.util.Date;

/**
 * @Description:导入工具类
 * @Date:2018/3/12 9:39
 * @Author:wsc
 */
public class ExcelImportUtils {
    private ExcelImportUtils(){}

    /**
     * @Description:是否是2003的excel，返回true是2003
     * @Date: 2018/3/12 9:40
     * @param: filePath
     * @Author: wsc
     */
    public static boolean isExcel2003(String filePath)  {
        return filePath.matches("^.+\\.(?i)(xls)$");
    }

    /**
     * @Description:是否是2007的excel，返回true是2007
     * @Date: 2018/3/12 9:40
     * @param: filePath
     * @Author: wsc
     */
    public static boolean isExcel2007(String filePath)  {
        return filePath.matches("^.+\\.(?i)(xlsx)$");
    }

    /**
     * @Description:验证EXCEL文件是否合格
     * @Date: 2018/3/12 9:40
     * @param: filePath
     * @Author: wsc
     */
    public static boolean validateExcel(String filePath){
        boolean flag = true;
        if (filePath == null || !(isExcel2003(filePath) || isExcel2007(filePath))){
            flag = false;
        }
        return flag;
    }

    /**
     * 校验表头是否正确
     * @param row
     * @param header
     * @return
     */
    public static boolean checkExcelTitle(Row row,String[] header){
        boolean flag = true;
        for (int i = 0; i < header.length ; i ++) {
            Cell cell = row.getCell(i);
            if (null != cell) {
                if (!cell.getStringCellValue().equals(header[i])) {
                    flag = false;
                    break;
                }
            } else {
                flag = false;
            }
        }
        return flag;
    }

    /**
     * 校验整行是否为空
     * @param totalCells
     * @param row
     * @return
     */
    public static boolean checkCellIsNull(int totalCells,Row row) {
        boolean flag = true;
        int num = 0;
        int cellNum = 0;
        String value;
        for (int c = 0; c < totalCells; c++) {
            cellNum ++;
            Cell cell = row.getCell(c);
            value = getValueByType(cell);
            if ("".equals(value.trim()) || null == value) {
                num ++;
            }
        }
        if (cellNum == num) {
            flag = false;
        }
        return flag;
    }

    /**
     * 根据cell类型获取值
     * @param cell
     * @return
     */
    public static String getValueByType(Cell cell){
        String value = "";
        DecimalFormat decimalFormat = new DecimalFormat("###################.###########");
        if (null != cell) {
            switch (cell.getCellType()) {
                case HSSFCell.CELL_TYPE_NUMERIC :
                    value = decimalFormat.format(cell.getNumericCellValue());
                    break;
                case HSSFCell.CELL_TYPE_STRING :
                    value = cell.getStringCellValue();
                    break;
                default:
                    break;
            }
        }
        return  value;
    }

    //获取导入Excel需要的参数
    public static ExcelCustom getSheet(String localFolder, MultipartFile file) throws IOException {
        //不存在文件夹时创建文件夹
        File fileDir = new File(localFolder);
        if(!fileDir.exists()){
            fileDir.mkdirs();
        }
        ExcelCustom excelCustom = new ExcelCustom();
        File uploadDir = new File(Constants.UPLOAD_URL);
        //获取文件名
        String fileName = file.getOriginalFilename();
        //创建一个目录 （它的路径名由当前 File 对象指定，包括任一必须的父路径。）
        if (!uploadDir.exists()) uploadDir.mkdirs();
        //新建一个文件
        File tempFile = new File(localFolder + new Date().getTime() + ".xlsx");
        excelCustom.setFile(tempFile);
        //初始化输入流
        InputStream is;
        //将上传的文件写入新建的文件中
        file.transferTo(tempFile);
        //根据新建的文件实例化输入流
        is = new FileInputStream(tempFile);
        excelCustom.setIs(is);
        //根据版本选择创建Workbook的方式
        Workbook wb;
        //根据文件名判断文件是2003版本还是2007版本
        if (ExcelImportUtils.isExcel2007(fileName)) {
            wb = new XSSFWorkbook(is);
        } else {
            wb = new HSSFWorkbook(is);
        }
        //得到第一个shell
        Sheet sheet = wb.getSheetAt(0);
        excelCustom.setSheet(sheet);
        //得到Excel的行数
        int totalRows = sheet.getPhysicalNumberOfRows();
        excelCustom.setTotalRows(totalRows);
        //总列数
        int totalCells = 0;
        //得到Excel的列数(前提是有行数)，从第二行算起
        if (totalRows >= 2 && sheet.getRow(1) != null) {
            totalCells = sheet.getRow(1).getLastCellNum ();
        }
        excelCustom.setTotalCells(totalCells);
        return excelCustom;
    }
}
