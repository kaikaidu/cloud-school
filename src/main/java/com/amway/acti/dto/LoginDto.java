/**
 * Created by dk on 2018/3/12.
 */

package com.amway.acti.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class LoginDto {

    @NotBlank(message = "code不能为空")
    private String code;

    @NotBlank(message = "iv不能为空")
    private String iv;

    @NotBlank(message = "encryptedData不能为空")
    private String encryptedData;

}
