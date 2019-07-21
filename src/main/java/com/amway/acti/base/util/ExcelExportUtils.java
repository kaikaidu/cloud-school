package com.amway.acti.base.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description:Excel导出
 * @Date:2018/3/14 16:41
 * @Author:wsc
 */
@Slf4j
public class ExcelExportUtils {
    private ExcelExportUtils(){}
    private static int sheelIndex = 0;

    public static void setSheelIndex(int index){
        sheelIndex = index;
    }
    /**
     * @Description:
     * @Date: 2018/4/18 15:36
     * @param: headers 表格属性列名数组
     * @param: t 需要显示的数据集合,集合中一定要放置符合javabean风格的类的对象。此方法支持的
     *                javabean属性的数据类型有基本数据类型及String,Date,byte[](图片数据)
     * @param: out 与输出设备关联的流对象，可以将EXCEL文档导出到本地文件或者网络中
     * @Author: wsc
     */
    public static <T extends Collection> void exportExcel(String[] headers,
                            T t,
                            OutputStream out) throws ReflectiveOperationException, IOException {
        exportExcel("导出EXCEL文档", headers, t, out, "yyyy-MM-dd");
    }


    /**
     * 这是一个通用的方法，利用了JAVA的反射机制，可以将放置在JAVA集合中并且符号一定条件的数据以EXCEL 的形式输出到指定IO设备上
     *
     * @param title   表格标题名
     * @param headers 表格属性列名数组
     * @param t 需要显示的数据集合,集合中一定要放置符合javabean风格的类的对象。此方法支持的
     *                javabean属性的数据类型有基本数据类型及String,Date,byte[](图片数据)
     * @param out     与输出设备关联的流对象，可以将EXCEL文档导出到本地文件或者网络中
     * @param pattern 如果有时间数据，设定输出格式。默认为"yyy-MM-dd"
     */
    public static <T extends Collection> void exportExcel(String title,
                            String[] headers,
                            T t,
                            OutputStream out,
                            String pattern) throws ReflectiveOperationException,IOException {
        log.error("title:{},headers:{},t:{},out:{},pattern:{}", title, headers, t, out, pattern);
        // 声明一个工作薄
        HSSFWorkbook workbook = new HSSFWorkbook();
        //一个表格最大只能导出65535行，超出行数就必须另外生成一个表格
        int ii = t.size() / 65535 + 1;
        Iterator <T> it = t.iterator();
        int index;
        for (int i = 0; i < ii; i++) {
            index = 0;
            // 生成一个表格
            HSSFSheet sheet = workbook.createSheet(title + (i + 1));
            //设置execl字体 样式，利用java反射机制获取it中值
            eeflexforExcel(workbook, sheet, headers, index, it);
        }
        workbook.write(out);
        out.close();
    }


    /**
     * @Description:多个sheel页
     * @Date: 2018/3/28 17:07
     * @param: workbook 工作薄
     * @param: sheetTitle sheel标题
     * @param: title 表格标题名
     * @param: headers 列数组
     * @param: t 数据集合
     * @param: out 流
     * @param: pattern 如果有时间数据，设定输出格式。默认为"yyy-MM-dd"
     * @Author: wsc
     */
    public static <T extends Collection> void exportExcel(HSSFWorkbook workbook,
                            String sheetTitle,
                            String title,
                            String[] headers,
                            T t) throws ReflectiveOperationException {
        log.error("title:{},headers:{},sheelIndex:{}", title, headers,sheelIndex);
        //一个表格最大只能导出65535行，超出行数就必须另外生成一个表格
        int ii = t.size() / 65535 + 1;
        Iterator <T> it = t.iterator();
        int index;
        for (int i = 0; i < ii; i++) {
            index = 0;
            // 生成一个表格
            HSSFSheet sheet = workbook.createSheet(title);
            //根据索引生成sheel
            workbook.setSheetName(sheelIndex, sheetTitle);
            //设置execl字体 样式，利用java反射机制获取it中值
            eeflexforExcel(workbook, sheet, headers, index, it);
            sheelIndex++;
        }
    }

    //设置execl字体 样式，利用java反射机制获取it中值
    private static <T extends Object> void eeflexforExcel(HSSFWorkbook workbook,
                                HSSFSheet sheet,
                                String[] headers,
                                int index,
                                Iterator <T> it) throws ReflectiveOperationException {
        sheet.setDefaultColumnWidth((short) 15);
        //设置execl字体
        HSSFFont font3 = setFont(workbook);
        // 声明一个画图的顶级管理器
        HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
        // 定义注释的大小和位置,详见文档
        HSSFComment comment = patriarch.createComment(new HSSFClientAnchor(0,
                0, 0, 0, (short) 4, 2, (short) 6, 5));
        // 设置注释内容
        comment.setString(new HSSFRichTextString("可以在POI中添加注释！"));
        // 设置注释作者，当鼠标移动到单元格上是可以在状态栏中看到该内容.
        comment.setAuthor("wsc");
        // 产生表格标题行
        HSSFRow row = sheet.createRow(0);
        for (short j = 0; j < headers.length; j++) {
            HSSFCell cell = row.createCell(j);
            HSSFRichTextString text = new HSSFRichTextString(headers[j]);
            cell.setCellValue(text);
        }
        //循环集合，利用反射
        eeflexforIterator(it, index, sheet, font3);

    }

    //循环集合，利用反射
    private static <T extends Object> void eeflexforIterator(Iterator<T> it,
                                   int index,
                                   HSSFSheet sheet,
                                   HSSFFont font3) throws ReflectiveOperationException {
        while (it.hasNext()) {
            index++;
            HSSFRow row = sheet.createRow(index);
            T t = it.next();
            Field[] fields = t.getClass().getDeclaredFields();
            for (short q = 0; q < fields.length; q++) {
                reflex(row,fields,t,font3,q);
            }
            if (index == 65535) {
                break;
            }
        }
    }
    private static <T extends Object> void reflex(HSSFRow row,
                     Field[] fields,
                     T t,
                     HSSFFont font3,
                     short q) throws ReflectiveOperationException {
        Object[] objects = new Object[]{};
        Class[] classes = new Class[]{};
        HSSFCell cell = row.createCell(q);
        Field field = fields[q];
        String fieldName = field.getName();
        String getMethodName = "get"
                + fieldName.substring(0, 1).toUpperCase()
                + fieldName.substring(1);
        Class tCls = t.getClass();
        Method getMethod = tCls.getMethod(getMethodName,
                classes);
        Object value = getMethod.invoke(t, objects);
        // 判断值的类型后进行强制类型转换
        String textValue = null;
        if (value != null) {
            // 其它数据类型都当作字符串简单处理
            textValue = value.toString();
        }
        // 如果不是图片数据，就利用正则表达式判断textValue是否全部由数字组成
        if (textValue != null) {
            Pattern p = Pattern.compile("^//d+(//.//d+)?$");
            Matcher matcher = p.matcher(textValue);
            if (matcher.matches()) {
                // 是数字当作double处理
                cell.setCellValue(Double.parseDouble(textValue));
            } else {
                HSSFRichTextString richString = new HSSFRichTextString(
                        textValue);
                richString.applyFont(font3);
                cell.setCellValue(richString);
            }
        }
    }

    //设置execl字体
    private static HSSFFont setFont(HSSFWorkbook workbook) {
        HSSFCellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setBorderTop(CellStyle.BORDER_THIN);
        style.setAlignment(CellStyle.ALIGN_CENTER);
        HSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setBoldweight(org.apache.poi.ss.usermodel.Font.BOLDWEIGHT_BOLD);
        HSSFFont font3 = workbook.createFont();
        font3.setColor(HSSFColor.BLUE.index);
        return font3;
    }
}
