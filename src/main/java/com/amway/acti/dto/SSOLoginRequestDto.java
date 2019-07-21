/**
 * Created by Will Zhang on 2018/2/22.
 */

package com.amway.acti.dto;

import lombok.Data;

@Data
public class SSOLoginRequestDto
{
  private String appid;

  private Long state;

  private String redirect_uri;

  private String cancel_uri;

  private String timestamp;

  private String parameter;

  private String display;

  private String signature;
}
