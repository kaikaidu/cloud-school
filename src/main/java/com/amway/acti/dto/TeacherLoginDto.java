/**
 * Created by Will Zhang on 2018/2/26.
 */

package com.amway.acti.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;

@Data
public class TeacherLoginDto implements Serializable
{
  @NotBlank(message = "邮箱为必填项")
  @Email(regexp = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$",message = "请输入正确的邮箱")
  private String email;

  @NotBlank(message = "密码为必填项")
  private String password;

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
