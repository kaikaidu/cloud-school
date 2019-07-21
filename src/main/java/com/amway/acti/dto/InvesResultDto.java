package com.amway.acti.dto;

import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Date:2018/3/8 18:54
 * @Author:wsc
 */
@Data
public class InvesResultDto {
    private int courseId;
    private int invesId;
    private List<InvesAnswerDto> invesResult;
}
