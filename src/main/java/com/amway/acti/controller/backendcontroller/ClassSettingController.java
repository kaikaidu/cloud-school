package com.amway.acti.controller.backendcontroller;

import com.amway.acti.base.util.Constants;
import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.dto.MenuDto;
import com.amway.acti.model.Course;
import com.amway.acti.model.Teacher;
import com.amway.acti.model.User;
import com.amway.acti.service.BackendUserService;
import com.amway.acti.service.ClassSettingService;
import com.amway.acti.service.CourseService;
import com.amway.acti.service.DrawService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @Description:班级设置
 * @Date:2018/4/26 17:21
 * @Author:wsc
 */
@Controller
@RequestMapping(value = "/backend/course")
public class ClassSettingController {

    @Autowired
    private BackendUserService backendUserService;

    @Autowired
    private ClassSettingService classSettingService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private DrawService drawService;

    /**
     * @Description:跳转分班设置
     * @Date: 2018/4/27 9:49
     * @param: courseId
     * @param: session
     * @param: model
     * @Author: wsc
     */
    @GetMapping("/classSettingChoose")
    public String classSettingChoose(Integer courseId,HttpSession session,Model model){
        if (null == courseId || courseId < 1) {return "redirect:/backend/course/basic";}
        menu(session,model,courseId);
        Course course = classSettingService.getCourse(courseId);
        if (null != course.getClassState()) {
            //获取已报名成功的学员
            getStudentCount(courseId,model);
            if (course.getClassState() == Constants.Number.INT_NUMBER0) {
                return "course/class-automatic";
            } else {
                return "course/class-manual";
            }
        } else {
            return "course/classSetting-choose";
        }
    }


    /**
     * @Description:跳转自动分班 | 手动分班
     * @Date: 2018/4/27 9:47
     * @param: courseId 课程id
     * @param: state 标识 1:自动 2：手动
     * @param: model
     * @param: session
     * @Author: wsc
     */
    @GetMapping("/classSetting")
    public String classSetting(Course course, Model model, HttpSession session){
        if (null == course.getSysId() || course.getSysId() < 1) {return "redirect:/backend/course/basic";}
        menu(session,model,course.getSysId());
        //修改课程分班状态
        courseService.updateCourse(course);
        classSettingService.deleteClassByCourseId(course.getSysId());
        getStudentCount(course.getSysId(),model);
        if (course.getClassState() == Constants.Number.INT_NUMBER0) {
            //跳转自动分班
            return "course/class-automatic";
        } else {
            //跳转手动分班
            return "course/class-manual";
        }
    }

    /**
     * @Description:修改课程
     * @Date: 2018/5/3 16:55
     * @param: courseId
     * @Author: wsc
     */
    @GetMapping("/updateCourse")
    public String updateCourse(Integer courseId,HttpSession session,Model model){
        //修改课程分班状态
        courseService.updateCourseForClassState(courseId);
        menu(session,model,courseId);
        return "course/classSetting-choose";
    }

    //获取已报名成功的人数
    private void getStudentCount(Integer courseId,Model model) {
        //根据课程id查询班级
        List<Teacher> mclassList = drawService.findDrawByCourseId(courseId);
        Integer classCount = null;
        if (null != mclassList && !mclassList.isEmpty()) {
            classCount = mclassList.get(0).getInitNumber();
        }
        //获取报名人数
        int count = drawService.selectSignupCount(courseId);
        model.addAttribute("count",count);
        model.addAttribute(Constants.CLASSCOUNT,classCount);
    }


    //获取菜单
    private void menu(HttpSession session, Model model, Integer courseId){
        User user = (User)session.getAttribute(Constants.BACKEND_USER_SESSSION_KEY);
        MenuDto menuDto = backendUserService.findMenu(user);
        MenuDto menuChildDto = backendUserService.findChildMenuByParentCodeAndUser(user,"01");
        model.addAttribute("menuFirstList", menuDto.getMenuList());
        model.addAttribute("menuChildList", menuChildDto.getMenuList());
        model.addAttribute("courseId",courseId);
        model.addAttribute("curFirstMenu","01");
        model.addAttribute("curChildMenu","0106");
    }

    /**
     * @Description:根据课程id查询评分
     * @Date: 2018/4/27 10:11
     * @param: courseId
     * @Author: wsc
     */
    @PostMapping("/getStudentScore")
    public @ResponseBody CommonResponseDto getStudentScore(Integer courseId){
        return classSettingService.getStudentScore(courseId);
    }

    /**
     * @Description:手动添加班级
     * @Date: 2018/4/27 15:52
     * @param: className 班级名称
     * @Author: wsc
     */
    @PostMapping("/addClassManual")
    @ResponseBody
    public CommonResponseDto addClassManual(String className,Integer courseId){
        return classSettingService.addClassManual(className,courseId);
    }

    /**
     * @Description:根据课程id删除班级
     * @Date: 2018/4/28 10:29
     * @param: courseId 课程id
     * @param: classId 班级id
     * @Author: wsc
     */
    @PostMapping("/deleteClass")
    @ResponseBody
    public CommonResponseDto deleteClass(Integer courseId,Integer classId) {
        return classSettingService.deleteClass(courseId,classId);
    }

    /**
     * @Description:根据课程id查询班级
     * @Date: 2018/4/28 11:01
     * @param: courseId 课程id
     * @Author: wsc
     */
    @PostMapping("/classList")
    @ResponseBody
    public CommonResponseDto classList(Integer courseId){
        return classSettingService.classList(courseId);
    }

    /**
     * @Description:删除课程下所有班级,演讲内容,以及关联的讲师
     * @Date: 2018/4/28 14:07
     * @param: courseId 课程id
     * @Author: wsc
     */
    @GetMapping("/deleteClassByCourseId")
    public String deleteClassByCourseId(Integer courseId,HttpSession session,Model model){
        classSettingService.deleteClassByCourseId(courseId);
        menu(session,model,courseId);
        return "course/classSetting-choose";
    }

    /**
     * @Description:根据课程id查询已报名成功的学员
     * @Date: 2018/4/28 16:01
     * @param: courseId 课程id
     * @param: name 学员名称
     * @Author: wsc
     */
    @PostMapping("/findUser")
    @ResponseBody
    public CommonResponseDto findUser(Integer courseId,String name){
        return classSettingService.findUser(courseId,name);
    }

    /**
     * @Description:班级添加学员
     * @Date: 2018/4/29 17:07
     * @param: userIds 学员id
     * @param: classId 班级id
     * @param: courseId 课程id
     * @Author: wsc
     */
    @PostMapping("/addCourseTeacher")
    @ResponseBody
    public CommonResponseDto addCourseTeacher(Integer[] userIds,Integer classId,Integer courseId,Integer studentLength){
        return classSettingService.addCourseTeacher(userIds,classId,courseId,studentLength);
    }


}
