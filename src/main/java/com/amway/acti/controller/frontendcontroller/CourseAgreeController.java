package com.amway.acti.controller.frontendcontroller;

import com.amway.acti.base.context.MiniProgramRequestContextHolder;
import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.service.CourseAgreeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Wei.Li1
 * @create 2018-03-02 16:20
 **/
@Slf4j
@RestController
@RequestMapping("frontend")
public class CourseAgreeController {

    @Autowired
    private CourseAgreeService courseAgreeService;

    @RequestMapping(value = "/agree", method = RequestMethod.POST)
    public CommonResponseDto agree(@RequestParam("courseId") Integer courseId) {
        log.info("courseId:{}", courseId);
        courseAgreeService.addAgree(MiniProgramRequestContextHolder.getRequestUser().getUid(), courseId);
        return CommonResponseDto.ofSuccess();
    }
}
