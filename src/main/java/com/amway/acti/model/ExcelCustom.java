package com.amway.acti.model;

import lombok.Data;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.File;
import java.io.InputStream;

/**
 * @Description:
 * @Date:2018/6/7 19:12
 * @Author:wsc
 */
@Data
public class ExcelCustom {
    private File file;
    private Sheet sheet;
    private int totalCells;
    private int totalRows;
    private InputStream is;
}
