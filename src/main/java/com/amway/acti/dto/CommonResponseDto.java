package com.amway.acti.dto;

import com.amway.acti.base.util.Constants;
import lombok.Data;


@Data
public class CommonResponseDto<T> {
    private String code;
    private String message;
    private T data;

    public static <T> CommonResponseDto<T> ofSuccess(T result) {
        CommonResponseDto<T> responseDto = new CommonResponseDto<>();
        responseDto.setCode(Constants.ErrorCode.SUCCESS_CODE);
        responseDto.setMessage("success");
        responseDto.setData(result);
        return responseDto;
    }

    public static CommonResponseDto ofSuccess() {
        CommonResponseDto responseDto = new CommonResponseDto();
        responseDto.setCode(Constants.ErrorCode.SUCCESS_CODE);
        responseDto.setMessage("success");
        return responseDto;
    }

    public static CommonResponseDto ofFailure(String errorCode, String message) {
        CommonResponseDto responseDto = new CommonResponseDto();
        responseDto.setCode(errorCode);
        responseDto.setMessage(message);
        return responseDto;
    }

    public static <T> CommonResponseDto<T> ofFailure(String errorCode, String message ,T result) {
        CommonResponseDto responseDto = new CommonResponseDto();
        responseDto.setCode(errorCode);
        responseDto.setMessage(message);
        responseDto.setData(result);
        return responseDto;
    }
}
