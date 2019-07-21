package com.amway.acti.controller.backendcontroller;

import com.amway.acti.base.util.Constants;
import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.dto.MenuDto;
import com.amway.acti.dto.MenuRolesDto;
import com.amway.acti.model.User;
import com.amway.acti.service.BackendUserService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Api
@RestController
@RequestMapping("/backend/menu")
public class MenuController {

    @Autowired
    private BackendUserService backendUserService;

    /**
     * 跳转到基本信息页面
     * @return
     */
    @RequestMapping("/courseInfo")
    public ModelAndView forwardCourseInfo(HttpSession session, @RequestParam(required = false) Integer courseId) {
        ModelAndView mv = new ModelAndView();
        User user = (User) session.getAttribute(Constants.BACKEND_USER_SESSSION_KEY);
        mv = findMenuAll(user,mv,courseId);
        String viewName = "/course/courseInfo";
        mv.setViewName(viewName);
        return mv;
    }


    /**
     * 查询菜单的公共方法
     * @param user
     * @param mv
     * @return
     */
    public ModelAndView findMenuAll(User user,ModelAndView mv,Integer courseId){
        MenuDto menuDto = backendUserService.findMenu(user);
        MenuDto menuChildDto = backendUserService.findChildMenuByParentCodeAndUser(user,"01");
        if(courseId != null){
            mv.addObject("courseId",courseId);
        }
        mv.addObject("menuFirstList", menuDto.getMenuList());
        mv.addObject("menuChildList", menuChildDto.getMenuList());
        return mv;
    }

    @RequestMapping(value = "/getMenus",method = RequestMethod.GET)
    public CommonResponseDto<MenuRolesDto> getMenus(HttpServletResponse response, HttpSession session, @RequestParam String code) throws IOException {
        //调用userService,根据openid查询用户信息
        User user = (User) session.getAttribute(Constants.BACKEND_USER_SESSSION_KEY);
        MenuDto menuDto = backendUserService.findMenu(user);
        MenuDto menuChildDto = backendUserService.findChildMenuByParentCodeAndUser(user, code);
        MenuRolesDto menuRolesDto = new MenuRolesDto();
        menuRolesDto.setMenuFirstList(menuDto.getMenuList());
        menuRolesDto.setMenuChildList(menuChildDto.getMenuList());
        menuRolesDto.setUser(user);
        return CommonResponseDto.ofSuccess(menuRolesDto);
    }
}
