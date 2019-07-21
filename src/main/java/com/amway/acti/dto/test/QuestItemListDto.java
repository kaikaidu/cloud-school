package com.amway.acti.dto.test;

import lombok.Data;

/**
 * @author Wei.Li1
 * @create 2018-03-05 14:25
 **/
@Data
public class QuestItemListDto {

    /**
     * 选项 A、B、C
     */
    private String sequence;

    /**
     * 选项内容
     */
    private String item;
}
