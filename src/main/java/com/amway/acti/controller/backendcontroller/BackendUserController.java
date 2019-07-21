/**
 * Created by dk on 2018/2/23.
 */

package com.amway.acti.controller.backendcontroller;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.amway.acti.model.Menu;
import com.amway.acti.model.User;
import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.dto.TeacherBackendDto;
import com.amway.acti.model.User;
import com.amway.acti.service.BackendUserService;
import com.amway.acti.transform.UserTransform;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Controller
@RequestMapping(value = "/backend/user")
@Api(value = "/backend/user")
public class BackendUserController {

    /*@RequestMapping(value = "/login", method = RequestMethod.GET)
    @ApiOperation(value = "后台登录", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponseDto login(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute("test1","aaabbb");
        CommonResponseDto<TokenDto> dto = new CommonResponseDto<>();
        dto.setMessage("success");
        dto.setCode("00");
        return dto;
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    @ApiOperation(value = "新增讲师信息", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponseDto add(HttpServletRequest request) {
        CommonResponseDto dto = new CommonResponseDto<>();
        dto.setMessage("success");
        dto.setCode("00");
        dto.setResult(request.getSession().getId());
        return dto;
    }*/

}
