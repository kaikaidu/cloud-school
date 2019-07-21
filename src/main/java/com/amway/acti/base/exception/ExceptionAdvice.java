/**
 * Created by Will Zhang on 2018/2/23.
 */

package com.amway.acti.base.exception;

import com.amway.acti.base.util.Constants;
import com.amway.acti.base.util.Constants.ErrorCode;
import com.amway.acti.dto.CommonResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice("com.amway.acti.controller")
@ResponseBody
public class ExceptionAdvice
{

  /**
   * 处理参数异常
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public CommonResponseDto handleParamException(ConstraintViolationException ex){
    String message = ex.getConstraintViolations().stream().map(ee -> ee.getMessage()).collect(
        Collectors.joining(","));
    return CommonResponseDto.ofFailure(Constants.ErrorCode.PARAM_EXCEPTION,message);
  }

  @ExceptionHandler(AmwayParamException.class)
  public CommonResponseDto handleAmwayParamException(AmwayParamException ex){
    log.error(ex.getMessage(),ex);
    return CommonResponseDto.ofFailure(ex.getErrorCode(),ex.getMessage());
  }


  /**
   * 逻辑异常处理
   */
  @ExceptionHandler(AmwayLogicException.class)
  public CommonResponseDto handleLogicException(AmwayLogicException ex){
    log.error(ex.getMessage(),ex);
    return CommonResponseDto.ofFailure(ex.getErrorCode(),ex.getMessage());
  }

  /**
   * 系统异常处理
   */
  @ExceptionHandler(AmwaySystemException.class)
  public CommonResponseDto handleAmwaySystemException(AmwaySystemException ex){
    log.error(ex.getMessage(),ex);
    return CommonResponseDto.ofFailure(ex.getErrorCode(),ex.getMessage());
  }

  /**
   * 处理参数转换异常
   * @param ex
   * @return
   */
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public CommonResponseDto handleAmwaySystemException(MethodArgumentTypeMismatchException ex){
    log.error(ex.getMessage(),ex);
    return CommonResponseDto.ofFailure(ErrorCode.PARAM_EXCEPTION,ex.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public CommonResponseDto handleException(Exception ex){
    log.error(ex.getMessage(),ex);
    return CommonResponseDto.ofFailure(ErrorCode.SYSTEM_EXCEPTION,"系统内部错误");
  }

}
