package com.amway.acti.base.util;

import lombok.Data;

/**
 * @author Wei.Li1
 * @create 2018-03-14 13:27
 **/
@Data
public class SqlUtil {
    private Integer pageSize;
    private Integer pageNo;
    private String orderBy;
}
