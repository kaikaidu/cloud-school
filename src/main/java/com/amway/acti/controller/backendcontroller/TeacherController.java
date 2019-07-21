package com.amway.acti.controller.backendcontroller;

import com.amway.acti.base.util.Constants;
import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.dto.MenuDto;
import com.amway.acti.model.User;
import com.amway.acti.service.BackendUserService;
import com.amway.acti.service.TeacherService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;


/**
 * @Description:讲师
 * @Date:2018/3/23 10:08
 * @Author:wsc
 */
@Controller
@RequestMapping(value = "/backend/user")
@Slf4j
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private BackendUserService backendUserService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * @Description:跳转新增讲师|讲师列表
     * @Date: 2018/3/23 10:18
     * @Author: wsc
     */
    @GetMapping(value = "/getTeacher")
    public String getTeacher(HttpSession session ,Model model,Integer courseId){
        User user = (User)session.getAttribute(Constants.BACKEND_USER_SESSSION_KEY);
        MenuDto menuDto = backendUserService.findMenu(user);
        MenuDto menuChildDto = backendUserService.findChildMenuByParentCodeAndUser(user,"03");
        model.addAttribute("menuFirstList", menuDto.getMenuList());
        model.addAttribute("menuChildList", menuChildDto.getMenuList());
        model.addAttribute("courseId", courseId);
        model.addAttribute("curFirstMenu","03");
        model.addAttribute("curChildMenu","0302");
        List<User> userList = teacherService.findTeacherByIdent(Constants.USER_IDENT_LECTURER);
        if (null != userList && !userList.isEmpty()) {
            return "user/teacher-list";
        } else {
            return "user/teacher-add";
        }
    }

    /**
     * @Description:新增讲师
     * @Date: 2018/3/23 11:22
     * @param: name 姓名
     * @param: email 邮箱
     * @param: password 密码
     * @Author: wsc
     */
    @PostMapping(value = "/addTeacher")
    @ResponseBody
    public CommonResponseDto addTeacher(String name, String email, String password) throws Exception{
        return teacherService.addTeacher(name,email,password);
    }

    /**
     * @Description:获取讲师总数
     * @Date: 2018/3/23 14:00
     * @param: name 讲师名称
     * @Author: wsc
     */
    @GetMapping(value = "/getTeacherCount")
    @ResponseBody
    public CommonResponseDto getTeacherCount(String name){
        return teacherService.getTeacherCount(name);
    }

    /**
     * @Description:根据讲师名称查询讲师列表
     * @Date: 2018/3/23 14:11
     * @param: pageNo 当前页
     * @param: pageSize 每页数量
     * @param: name 讲师名称
     * @Author: wsc
     */
    @PostMapping(value = "/findTeacherByName")
    @ResponseBody
    public CommonResponseDto<PageInfo<User>> findTeacherByName(Integer pageNo, Integer pageSize, String name) throws Exception{
        PageInfo<User> pageInfo = teacherService.findTeacherByName(pageNo,pageSize,name);
        return CommonResponseDto.ofSuccess(pageInfo);
    }

    /**
     * @Description:根据id删除讲师
     * @Date: 2018/3/23 14:49
     * @param: ids 讲师id
     * @Author: wsc
     */
    @PostMapping(value = "/updateTeacherById")
    @ResponseBody
    public CommonResponseDto updateTeacherById(String ids){
        return teacherService.updateTeacherById(ids);
    }

    /**
     * @Description:根据讲师id查询讲师详情
     * @Date: 2018/3/26 10:11
     * @param: id 讲师id
     * @Author: wsc
     */
    @PostMapping(value = "/getTeacherById")
    @ResponseBody
    public CommonResponseDto getTeacherById(Integer id) throws Exception{
        return teacherService.getTeacherById(id);
    }

    /**
     * @Description:修改讲师
     * @Date: 2018/3/26 11:35
     * @param: id 讲师id
     * @param: name 讲师名字
     * @param: email 邮箱
     * @param: pwd 密码
     * @Author: wsc
     */
    @PostMapping(value = "/updateTeacher")
    @ResponseBody
    public CommonResponseDto updateTeacher(Integer id, String name, String email, String pwd) throws Exception{
        teacherService.updateTeacher(id,name,email,pwd);
        return CommonResponseDto.ofSuccess();
    }

    /**
     * @Description:根据邮箱查询讲师
     * @Date: 2018/4/8 10:08
     * @param: email
     * @Author: wsc
     */
    @PostMapping("/getTeacherByEmail")
    @ResponseBody
    public CommonResponseDto getTeacherByEmail(String email){
        return teacherService.getTeacherByEmail(email);
    }

    /**
     * 解锁
     * @param email
     * @return
     */
    @PostMapping("/deblocking")
    @ResponseBody
    public CommonResponseDto deblocking(String email){
        stringRedisTemplate.delete(email);
        return CommonResponseDto.ofSuccess();
    }
}
