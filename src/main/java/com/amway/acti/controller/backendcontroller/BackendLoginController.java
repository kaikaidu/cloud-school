package com.amway.acti.controller.backendcontroller;

import com.amway.acti.base.util.Constants;
import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.dto.TeacherLoginDto;
import com.amway.acti.model.User;
import com.amway.acti.service.BackendUserService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * @Author Jie.Qiu
 */
@Slf4j
@Api
@Controller
@RequestMapping("/backend")
public class BackendLoginController extends BaseController{

    @Autowired
    private BackendUserService backendUserService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("/login")
    public String index() {
        return "login";
    }

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto backendLogin(TeacherLoginDto dto, HttpServletRequest request, HttpSession session) throws GeneralSecurityException, IOException {
        try {
            backendUserService.checkByEmailAndPwd(dto, request);
            User user = backendUserService.findByEmail(dto.getEmail());
            session.setAttribute(Constants.BACKEND_USER_SESSSION_KEY, user);
            // 将当前用户的sessionId放到redis中
            stringRedisTemplate.opsForValue().set(Constants.ADMIN_CACHE_NAME + user.getId(), session.getId());
            log.info("ip=" + this.getClientIp(request) + " email=" + dto.getEmail() + " categoryOutcome=Success");
        } catch (Exception ex) {
            log.info("ip=" + this.getClientIp(request) + " email=" + dto.getEmail() + " categoryOutcome=Failure errorMessage=" + ex.getMessage());
            throw ex;
        }
        return CommonResponseDto.ofSuccess();
    }

    @RequestMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().invalidate();//清除当前用户相关的session对象
        return "redirect:/backend/login";
    }

    @GetMapping("/isLogin")
    @ResponseBody
    public CommonResponseDto<Boolean> isLogin(HttpSession session){
        return CommonResponseDto.ofSuccess();
    }
}
