package com.amway.acti.dto.test;

import lombok.Data;

/**
 * @author Wei.Li1
 * @create 2018-03-05 10:29
 **/
@Data
public class TestPaperDto {

    /**
     * 试卷名称
     */
    private String name;
    /**
     * 试卷id
     */
    private Integer id;

    /**
     * 是否已提交 0-未提交 1-已提交
     */
    private Integer isSubmit;
}
