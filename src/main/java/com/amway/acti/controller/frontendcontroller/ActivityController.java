/**
 * Created by Will Zhang on 2018/3/5.
 */

package com.amway.acti.controller.frontendcontroller;

import com.amway.acti.base.context.MiniProgramRequestContextHolder;
import com.amway.acti.base.util.Constants;
import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.model.User;
import com.amway.acti.service.CourseRegisterService;
import com.amway.acti.service.CourseSignUpService;
import com.amway.acti.service.RedisService;
import com.amway.acti.service.UserService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api
@RestController
@RequestMapping("/frontend")
public class ActivityController {
    @Autowired
    private CourseRegisterService courseRegisterService;

    @Autowired
    private CourseSignUpService courseSignUpService;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisService redisService;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public CommonResponseDto register(Integer courseId) {
        log.info("courseId:{}", courseId);
        //调用userService,根据openid查询用户信息
        User user = userService.getUser(MiniProgramRequestContextHolder.getRequestUser().getUid());
        log.info("user:{}", user.toString());
        new Thread(() -> {
            courseRegisterService.register(user, courseId);
        }, "thread-name" + System.currentTimeMillis()).start();

        return CommonResponseDto.ofSuccess();
    }

    @RequestMapping(value = "/signUp", method = RequestMethod.POST)
    public CommonResponseDto signUp(Integer courseId, String formId) {
        int userId = MiniProgramRequestContextHolder.getRequestUser().getUid();
        //调用userService,根据openid查询用户信息
        User user = userService.getUser(userId);
        int signUpResult = 1;
        log.info("user:{}", user.toString());
        String key = Constants.SAFETYLOCK_SIGN + courseId;
        log.info("signUp setNx:{}", key);
        try {
            while (!redisService.setNx0(key, "" + userId)) {
                log.info("user 尚未获取到锁:{}", userId);
            }
            signUpResult = courseSignUpService.signUp(user, courseId, formId);
        } catch (Exception ex) {
            redisService.delete(key);
            throw ex;
        } finally {
            redisService.delete(key);
        }
        return CommonResponseDto.ofSuccess(signUpResult);
    }
}
