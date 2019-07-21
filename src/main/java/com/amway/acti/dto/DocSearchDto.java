package com.amway.acti.dto;

import com.amway.acti.base.validation.constraints.ShortRange;
import com.amway.acti.base.validation.constraints.StringRange;
import lombok.Data;


@Data
public class DocSearchDto {

    private String name;

    @StringRange(range = {"desc","asc"},message = "排序方式取值错误")
    private String orderType;

    @ShortRange(range = {1,2,3},message = "资料类型取值错误")
    private Short type;

    @ShortRange(range = {1,2},message = "添加方式取值错误")
    private Short addType;

    private Integer pageNo;

    private Integer pageSize;

}
