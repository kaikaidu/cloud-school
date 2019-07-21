/**
 * Created by Will Zhang on 2018/3/8.
 */

package com.amway.acti.base.exception;

public class AmwaySystemException extends RuntimeException
{
  private static final String SYSTEM_CODE = "55";

  public String getErrorCode(){
    return SYSTEM_CODE;
  }

  public AmwaySystemException(String message) {
    super(message);
  }

  public AmwaySystemException(String message, Throwable cause) {
    super(message, cause);
  }

}
