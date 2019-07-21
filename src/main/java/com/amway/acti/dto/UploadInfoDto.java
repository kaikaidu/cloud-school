package com.amway.acti.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Date:2018/6/7 20:05
 * @Author:wsc
 */
@Data
public class UploadInfoDto {
    private Integer succeNum = 0;   //成功数量
    private Integer failNum = 0;    //失败数量
    private List<UploadFailUserDto> uploadFailUserDtoList = new ArrayList <>();
}
