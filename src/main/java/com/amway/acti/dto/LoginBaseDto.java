/**
 * Created by Will Zhang on 2018/2/26.
 */

package com.amway.acti.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public abstract class LoginBaseDto
{
  @NotBlank(message = "openId不能为空")
  private String openId;

  @NotBlank(message = "unionId不能为空")
  private String unionId;
}
