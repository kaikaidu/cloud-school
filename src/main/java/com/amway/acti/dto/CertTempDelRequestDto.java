/**
 * Created by dk on 2018/6/19.
 */

package com.amway.acti.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

@Data
public class CertTempDelRequestDto {
    @NotEmpty(message = "证书模板ID为必填项")
    private Integer[] ids;
}
