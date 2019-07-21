/**
 * Created by Will Zhang on 2018/2/22.
 */

package com.amway.acti.dto;

import lombok.Data;

@Data
public class SSOLoginResponseDto
{
  private String appid_from;

  private String parameter;

  private String timestamp;

  private String code;

  private String signature;
}
