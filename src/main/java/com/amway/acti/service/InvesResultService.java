package com.amway.acti.service;

import com.amway.acti.dto.InvesResultDto;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
/**
 * @Description:
 * @Date:2018/3/8 18:27
 * @Author:wsc
 */
@Validated
public interface InvesResultService {
    void insertInvesResult(InvesResultDto invesResultDto,@NotNull(message = "用户ID不能为空") int userId);
}
