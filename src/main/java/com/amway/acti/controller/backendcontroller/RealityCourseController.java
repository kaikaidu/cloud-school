package com.amway.acti.controller.backendcontroller;

import com.amway.acti.base.util.Constants;
import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.dto.MenuDto;
import com.amway.acti.model.Course;
import com.amway.acti.model.CourseUser;
import com.amway.acti.model.User;
import com.amway.acti.service.BackendUserService;
import com.amway.acti.service.CourseService;
import com.amway.acti.service.RealityServer;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.ParseException;

/**
 * @Description:线下课程
 * @Date:2018/3/9 16:45
 * @Author:wsc
 */
@Controller
@RequestMapping(value = "/backend/course")
@Api(value = "/backend/realityCourse")
@Slf4j
public class RealityCourseController {


    @Autowired
    private RealityServer realityServer;

    @Autowired
    private CourseService courseService;

    @Autowired
    private BackendUserService backendUserService;

    /**
     * @Description:跳转学员状态 判断是线下还是直播
     * @Date: 2018/3/15 10:11
     * @Author: wsc
     */
    @RequestMapping(value = "/getRealityCourse", method = RequestMethod.GET)
    public String realityCourse(HttpSession session, Integer courseId,Model model) {
        if (null == courseId || courseId < 1) {
            return "redirect:/backend/course/basic";
        }
        User user = (User)session.getAttribute(Constants.BACKEND_USER_SESSSION_KEY);
        MenuDto menuDto = backendUserService.findMenu(user);
        MenuDto menuChildDto = backendUserService.findChildMenuByParentCodeAndUser(user,"01");
        model.addAttribute("menuFirstList", menuDto.getMenuList());
        model.addAttribute("menuChildList", menuChildDto.getMenuList());
        model.addAttribute("courseId",courseId);
        model.addAttribute("curFirstMenu","01");
        model.addAttribute("curChildMenu","0102");
        Course course = courseService.selectCourseById(courseId);
        //是否需要审核 0：不需要  1：需要
        if (course.getIsVerify() != Constants.Number.BYTE_NUMBER) {
            return "course/realityCourse";
        } else {
            return "course/liveTelecast";
        }
    }

    /**
     * @Description:校验学员信息 导入学员信息
     * @Date: 2018/3/13 17:44
     * @param: jsonArray 学员json
     * @param: courseId 课程id
     * @Author: wsc
     */
    @RequestMapping(value = "/checkInsertUser", method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto checkInsertUser(Integer courseId,
                                             String state,
                                             Integer dataLength,
                                             HttpServletRequest request,MultipartFile file)  throws ParseException, IOException{
        return realityServer.checkInsertUser(courseId,state,dataLength,(User)request.getSession().getAttribute(Constants.BACKEND_USER_SESSSION_KEY),file);
    }

    /**
     * @Description:条件分页查询学员
     * @Date: 2018/3/13 18:19
     * @param: courseId 课程id
     * @param: name 学员名称
     * @param: pageNo 当前页
     * @param: sortSex 性别排序
     * @param: sortSignupState 报名排序
     * @param: sortViaState 通过状态排序
     * @Author: wsc
     */
    @RequestMapping(value = "/selectUserAndSignupByCourseId",method = RequestMethod.GET)
    @ResponseBody
    public CommonResponseDto<PageInfo<User>> selectUserAndSignupByCourseId(Integer pageNo, Integer pageSize, CourseUser courseUser) {
        PageInfo<User> pageInfo = realityServer.selectUserAndSignupByCourseId(pageNo,pageSize,courseUser);
        return CommonResponseDto.ofSuccess(pageInfo);
    }

    /**
     * @Description:获取学员总记录数
     * @Date: 2018/3/14 13:59
     * @param: courseId 课程id
     * @Author: wsc
     */
    @RequestMapping(value = "/selectUserSignCount" ,method = RequestMethod.GET)
    @ResponseBody
    public CommonResponseDto selectUserSignCount(CourseUser courseUser){
        int count =  realityServer.selectUserSignCount(courseUser);
        return CommonResponseDto.ofSuccess(count);
    }

    /**
     * @Description:修改报名通过状态
     * @Date: 2018/3/14 15:29
     * @param: ids 学员id
     * @param: state 通过状态
     * @Author: wsc
     */
    @RequestMapping(value = "/updateSignState",method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto updateSignState(String ids, Integer state){
        return realityServer.updateSignState(ids,state);
    }

    /**
     * @Description:导出学员
     * @Date: 2018/4/2 9:57
     * @param: response
     * @param: courseUser 参数对象
     * @Author: wsc
     */
    @RequestMapping(value = "/exportUser",method = RequestMethod.GET)
    public void exportUser(HttpServletResponse response, HttpServletRequest request, CourseUser courseUser) throws Exception{
        realityServer.exportUser(response,request,courseUser);
    }

    /**
     * @Description:根据课程id查询课程关联的标签
     * @Date: 2018/4/11 11:23
     * @param: courseId
     * @Author: wsc
     */
    @PostMapping("/getLabelByCourseId")
    @ResponseBody
    public CommonResponseDto getLabelByCourseId(Integer courseId){
        return realityServer.getLabelByCourseId(courseId);
    }

    /**
     * @Description:获取excel导入完成进度
     * @Date: 2018/4/16 14:39
     * @Author: wsc
     */
    @PostMapping("/getCompletedProgress")
    @ResponseBody
    public CommonResponseDto getCompletedProgress(Integer courseId,HttpServletRequest request){
        User user = (User) request.getSession().getAttribute(Constants.BACKEND_USER_SESSSION_KEY);
        return realityServer.getCompletedProgress(courseId,user.getId());
    }

    /**
     * @Description: 获取excel导入完成数量
     * @Date: 2018/8/28 10:49
     * @param: courseId
     * @param: request
     * @Author: wsc
     */
    @PostMapping("/getSuccessCount")
    @ResponseBody
    public CommonResponseDto getSuccessCount(Integer courseId,HttpServletRequest request){
        User user = (User) request.getSession().getAttribute(Constants.BACKEND_USER_SESSSION_KEY);
        return realityServer.getSuccessCount(courseId,user.getId());
    }

    /***
     * 批量删除
     * @param ids
     * @return
     */
    @RequestMapping(value = "/state/batchDel",method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto batchDel(String ids, Integer courseId){
        return realityServer.batchDel(ids, courseId);
    }

    /**
     * @Description:删除班级表和演讲表
     * @Date: 2018/8/31 10:44
     * @param: ids 用户id
     * @param: courseId 课程id
     * @param: type 0：删除全部 1：剔除删除的用户
     * @Author: wsc
     */
    @PostMapping("/state/deleteClassAndDraw")
    @ResponseBody
    public CommonResponseDto deleteClassAndDraw(String ids, Integer courseId,Integer type){
        return realityServer.deleteClassAndDraw(ids, courseId,type);
    }

    /**
     * @Description:根据课程id查询是否分班
     * @Date: 2018/8/31 11:05
     * @param: courseId
     * @Author: wsc
     */
    @PostMapping("/state/checkClass")
    @ResponseBody
    public CommonResponseDto checkClass(Integer courseId){
        return realityServer.checkClass(courseId);
    }
}
