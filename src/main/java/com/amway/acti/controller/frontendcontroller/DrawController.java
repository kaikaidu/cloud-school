/**
 * Created by dk on 2018/3/2.
 */

package com.amway.acti.controller.frontendcontroller;

import com.amway.acti.base.context.MiniProgramRequestContextHolder;
import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.dto.DrawResultDto;
import com.amway.acti.service.DrawService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/frontend")
public class DrawController {

    @Autowired
    private DrawService drawService;

    /**
     * 抽签结果查询
     *
     * @param courseId
     * @return
     */
    @RequestMapping(value = {"/drawResult"}, method = RequestMethod.GET)
    public CommonResponseDto<DrawResultDto> drawResult(@RequestParam int courseId) {
        return CommonResponseDto.ofSuccess(drawService.drawResult(MiniProgramRequestContextHolder.getRequestUser().getUid(), courseId));
    }
}
