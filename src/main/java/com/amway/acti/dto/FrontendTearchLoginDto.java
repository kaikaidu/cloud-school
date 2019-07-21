/**
 * Created by dk on 2018/3/22.
 */

package com.amway.acti.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class FrontendTearchLoginDto {

    @NotBlank(message = "code不能为空")
    private String code;

    @NotBlank(message = "iv不能为空")
    private String iv;

    @NotBlank(message = "encryptedData不能为空")
    private String encryptedData;

    @NotBlank(message = "邮箱为必填项")
    @Email(regexp = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$", message = "请输入正确的邮箱")
    private String email;

    @NotBlank(message = "密码为必填项")
    private String password;
}
