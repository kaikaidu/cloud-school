/**
 * Created by Will Zhang on 2018/3/8.
 */

package com.amway.acti.base.exception;

public class AmwayParamException extends RuntimeException
{
  private static final String PARAM_CODE = "33";

  public String getErrorCode(){
    return PARAM_CODE;
  }

  public AmwayParamException(String message) {
    super(message);
  }

  public AmwayParamException(String message, Throwable cause) {
    super(message, cause);
  }

}
