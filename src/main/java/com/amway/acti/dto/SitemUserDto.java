/**
 * Created by dk on 2018/3/5.
 */

package com.amway.acti.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class SitemUserDto implements Serializable {
    private Integer userId;
    private String name;
    private String standDesc;
    private Integer code;//演讲序号wsc
    private List<SitemDto> sitems = new ArrayList<>();
}
