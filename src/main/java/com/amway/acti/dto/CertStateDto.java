package com.amway.acti.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

/**
 * @Description:
 * @Date:2018/6/14 13:42
 * @Author:wsc
 */
@Data
public class CertStateDto {
    @NotEmpty(message = "请选择用户")
    private Integer[] userIds;

    @NotNull(message = "课程id不能为空")
    private Integer courseId;

    @NotNull(message = "请选择需要修改的颁发状态")
    private Integer state;
}
