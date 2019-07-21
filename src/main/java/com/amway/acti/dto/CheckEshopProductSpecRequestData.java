/**
 * Created by dk on 2018/11/15.
 */

package com.amway.acti.dto;

import lombok.Data;

import java.util.List;

@Data
public class CheckEshopProductSpecRequestData {
    private String timestamp;
    private List<SpecRequestData> specs;

}
