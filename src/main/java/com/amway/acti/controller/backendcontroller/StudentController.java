package com.amway.acti.controller.backendcontroller;

import com.amway.acti.base.util.Constants;
import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.dto.MenuDto;
import com.amway.acti.model.User;
import com.amway.acti.model.UserCustom;
import com.amway.acti.service.BackendUserService;
import com.amway.acti.service.StudentService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * @Description:学员管理
 * @Date:2018/3/29 19:31
 * @Author:wsc
 */
@Controller
@Slf4j
@RequestMapping("/backend/user")
public class StudentController extends BaseController{
    @Autowired
    private StudentService studentService;

    @Autowired
    private BackendUserService backendUserService;

    /**
     * @Description:跳转学员管理
     * @Date: 2018/3/29 19:35
     * @param: model
     * @param: courseId
     * @Author: wsc
     */
    @GetMapping("/student")
    public String student(Model model, HttpSession session, Integer courseId){
        User user = (User)session.getAttribute(Constants.BACKEND_USER_SESSSION_KEY);
        log.info("user:{}",user);
        MenuDto menuDto = backendUserService.findMenu(user);
        log.info("menuDto:{}",menuDto);
        MenuDto menuChildDto = backendUserService.findChildMenuByParentCodeAndUser(user,"03");
        log.info("menuChildDto:{}",menuChildDto);
        model.addAttribute("menuFirstList", menuDto.getMenuList());
        model.addAttribute("menuChildList", menuChildDto.getMenuList());
        model.addAttribute("courseId", courseId);
        model.addAttribute("curFirstMenu","03");
        model.addAttribute("curChildMenu","0301");
        return "user/student";
    }

    /**
     * @Description:查询学员
     * @Date: 2018/3/29 19:37
     * @param: pageNo
     * @param: pageSize
     * @param: name
     * @Author: wsc
     */
    @PostMapping("/studentList")
    @ResponseBody
    public CommonResponseDto<PageInfo<UserCustom>> studentList(Integer pageNo, Integer pageSize, String name){
        return studentService.studentServicestudentList(pageNo,pageSize,name);
    }

    /**
     * @Description:获取总数
     * @Date: 2018/3/29 20:03
     * @param: name
     * @Author: wsc
     */
    @GetMapping("/getStudentCount")
    @ResponseBody
    public CommonResponseDto getStudentCount(String name){
        return studentService.getStudentCount(name);
    }

    /**
     * @Description:软删除学员
     * @Date: 2018/3/29 20:57
     * @param: ids
     * @Author: wsc
     */
    @PostMapping("/updateStudentById")
    @ResponseBody
    public CommonResponseDto updateStudentById(String ids) {
        CommonResponseDto dto = null;
        User loginUser = null;
        try {
            dto = studentService.updateStudentById(ids);
            loginUser = this.getSessionAdmin();
            log.info("delete student id:{}", ids);
            log.info("ip=" + this.getClientIp(request) + " email=" + loginUser.getEmail() + " categoryOutcome=Success");
        } catch (Exception ex) {
            log.info("ip=" + this.getClientIp(request) + " email=" + loginUser.getEmail() + " categoryOutcome=Failure errorMessage=" + ex.getMessage());
            throw ex;
        }
        return dto;
    }

    /**
     * @Description:根据id查询学员
     * @Date: 2018/3/29 21:08
     * @param: id
     * @Author: wsc
     */
    @PostMapping("/getStudentById")
    @ResponseBody
    public CommonResponseDto getStudentById(Integer id){
        return studentService.getStudentById(id);
    }

    /**
     * @Description:根据id修改学员
     * @Date: 2018/3/30 9:44
     * @param: id
     * @param: phone
     * @param: address
     * @Author: wsc
     */
    @PostMapping("/updateStudent")
    @ResponseBody
    public CommonResponseDto updateStudent(User user) {
        User loginUser = null;
        try {
            loginUser = this.getSessionAdmin();
            studentService.updateStudent(user);
            log.info("update student id:{}", user.getId());
            log.info("ip=" + this.getClientIp(request) + " email=" + loginUser.getEmail() + " categoryOutcome=Success");
        } catch (Exception ex) {
            log.info("ip=" + this.getClientIp(request) + " email=" + loginUser.getEmail() + " categoryOutcome=Failure errorMessage=" + ex.getMessage());
            throw ex;
        }
        return CommonResponseDto.ofSuccess();
    }

    /**
     * @Description:获取省份
     * @Date: 2018/4/2 15:34
     * @param:
     * @Author: wsc
     */
    @PostMapping("/provinceList")
    @ResponseBody
    public CommonResponseDto provinceList(){
        return studentService.provinceList();
    }

    /**
     * @Description:根据code查询地址
     * @Date: 2018/4/2 15:34
     * @param: code
     * @Author: wsc
     */
    @PostMapping("/getAddr")
    @ResponseBody
    public CommonResponseDto getAddr(String code){
        return studentService.getAddr(code);
    }
}
