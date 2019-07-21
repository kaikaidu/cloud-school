/**
 * Created by Will Zhang on 2018/2/26.
 */

package com.amway.acti.base.exception;

public class AmwayLogicException extends RuntimeException
{
  private static final String BASE_CODE = "440";
  private String selfExceptionCode;

  public String getSelfExceptionCode() {
    return this.selfExceptionCode;
  }

  public String getErrorCode(){
    return BASE_CODE+this.selfExceptionCode;
  }

  public void setSelfExceptionCode(String selfExceptionCode) {
    this.selfExceptionCode = selfExceptionCode;
  }

  public AmwayLogicException(String message) {
    super(message);
  }

  public AmwayLogicException(String selfExceptionCode, String message) {
    super(message);
    this.selfExceptionCode = selfExceptionCode;
  }

  public AmwayLogicException(String selfExceptionCode, String message, Throwable cause) {
    super(message, cause);
    this.selfExceptionCode = selfExceptionCode;
  }
}
