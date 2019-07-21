package com.amway.acti.controller.backendcontroller;

import com.amway.acti.base.redis.RedisComponent;
import com.amway.acti.base.util.Constants;
import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.dto.CourseBackendDto;
import com.amway.acti.dto.CourseBasicDto;
import com.amway.acti.dto.CourseListBackendDto;
import com.amway.acti.dto.CourseListSearchDto;
import com.amway.acti.dto.MenuDto;
import com.amway.acti.dto.PageDto;
import com.amway.acti.dto.TeacherBackendDto;
import com.amway.acti.model.Course;
import com.amway.acti.model.Label;
import com.amway.acti.model.User;
import com.amway.acti.service.BackendUserService;
import com.amway.acti.service.CourseService;
import com.amway.acti.service.LabelService;
import com.amway.acti.transform.CourseTransform;
import com.amway.acti.transform.UserTransform;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("/backend/course")
public class BackendCourseController {

    @Autowired
    private UserTransform userTransform;

    @Autowired
    private LabelService labelService;

    @Autowired
    private CourseTransform courseTransform;

    @Autowired
    private CourseService courseService;

    @Autowired
    private BackendUserService backendUserService;

    @Autowired
    private RedisComponent redisComponent;

   @RequestMapping(value = "/basic",method = RequestMethod.GET)
    public String basic(Model model,@RequestParam(required = false) Integer courseId,HttpSession session){
        User user = (User)session.getAttribute(Constants.BACKEND_USER_SESSSION_KEY);
        MenuDto menuDto = backendUserService.findMenu(user);
        MenuDto menuChildDto = backendUserService.findChildMenuByParentCodeAndUser(user,"01");
        model.addAttribute("menuFirstList", menuDto.getMenuList());
        model.addAttribute("menuChildList", menuChildDto.getMenuList());
        model.addAttribute("curFirstMenu","01");
        model.addAttribute("curChildMenu","0101");

        List<User> teachers = backendUserService.getTeachers();
        List<User> assists = backendUserService.getAssists();
        List<TeacherBackendDto> dtos = userTransform.modelsToTeacherDtos(teachers);
        List<Label> labels = labelService.selectByType(Constants.LabelType.COURSE);
        model.addAttribute("teachers",dtos);
        model.addAttribute("labels",labels);
        model.addAttribute("assists",assists);
        if(courseId != null){
            model.addAttribute("courseId",courseId);
        }
        return "course/courseInfo";
    }

    @RequestMapping(value = "/courseInfo",method = RequestMethod.GET)
    @ResponseBody
    public CommonResponseDto<CourseBackendDto> courseInfo(HttpSession session,Integer courseId){
        log.info("courseId:{}",courseId);
        //调用userService,根据openid查询用户信息
        User user = (User)session.getAttribute(Constants.BACKEND_USER_SESSSION_KEY);
        log.info("courseInfo >>> user:{}",user.toString());
        Course course = courseService.selectDetailCourse(courseId,null,null);
        CourseBackendDto dto = courseTransform.transformModelToDto(course,false);
        return CommonResponseDto.ofSuccess(dto);
    }

    @RequestMapping(value = "/preview",method = RequestMethod.GET)
    @ResponseBody
    public CommonResponseDto<CourseBackendDto> coursePreview(HttpSession session,Integer courseId){
        log.info("courseId:{}",courseId);
        //调用userService,根据openid查询用户信息
        User user = (User)session.getAttribute(Constants.BACKEND_USER_SESSSION_KEY);
        log.info("coursePreview >>> user:{}",user.toString());
        Course course = courseService.selectDetailCourse(courseId,null,null);
        CourseBackendDto dto = courseTransform.transformModelToDto(course,true);
        return CommonResponseDto.ofSuccess(dto);
    }

    @RequestMapping(value = "/saveBasicInfo",method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto saveBasicInfo(@RequestBody CourseBasicDto dto, HttpSession session){
        User user = (User)session.getAttribute(Constants.BACKEND_USER_SESSSION_KEY);
        log.info("saveBasicInfo >>> user:{}",user.toString());
        Course course = courseTransform.transformDtoToModel(dto,user.getId());
        if(course == null){
            return CommonResponseDto.ofFailure( "55", "课程不存在" );
        }
        Integer courseId = courseService.insertOrUpdateCourse(course,dto.getTeachers(),dto.getAssists() ,dto.getCourseId());
        //delRedis(courseId);
        return CommonResponseDto.ofSuccess(courseId);
    }

    private void delRedis(int courseId){
        String key = Constants.COURSE_USER_INFO + courseId;
        String[] keys = redisComponent.keys( key );
        redisComponent.del( keys );
    }

    /**
     * 课程列表页
     * @return
     */
    @RequestMapping(value = "/courseManage",method = RequestMethod.GET)
    public String getCourseList(Model model, HttpSession session){
        User user = (User) session.getAttribute(Constants.BACKEND_USER_SESSSION_KEY);
        MenuDto menuDto = backendUserService.findMenu(user);
        List<Label> labels = labelService.selectByType(Constants.LabelType.COURSE);
        model.addAttribute("menuFirstList",menuDto.getMenuList());
        model.addAttribute("curFirstMenu","01");
        model.addAttribute("labels",labels);
        return "course/courseManage";
    }

    /**
     * 查询课程列表
     * @param session
     * @return
     */
    @RequestMapping(value = "/courseList",method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto getCourseList(HttpSession session, @RequestBody CourseListSearchDto dto){
        User user = (User)session.getAttribute(Constants.BACKEND_USER_SESSSION_KEY);
        PageInfo<Course> pageInfo = courseService.selectCourseList(user,dto);
        PageDto<CourseListBackendDto> dtoPageDto = courseTransform.transformPageInfoToDto(pageInfo);
        return CommonResponseDto.ofSuccess(dtoPageDto);
    }

    /**
     * 查询课程总记录数
     * @param session
     * @return
     */
    @RequestMapping(value = "/courseCount",method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto getCourseCount(HttpSession session, @RequestBody CourseListSearchDto dto){
        User user = (User)session.getAttribute(Constants.BACKEND_USER_SESSSION_KEY);
        Long totalCount = courseService.countCourse(user,dto);
        log.info("totalCount:{}",totalCount);
        return CommonResponseDto.ofSuccess(totalCount);
    }

    /**
     * 上架
     * @param session
     * @param courseIds
     * @return
     */
    @RequestMapping(value = "/shelve",method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto shelveCourse(HttpSession session,@RequestBody List<Integer> courseIds){
        User user = (User)session.getAttribute(Constants.BACKEND_USER_SESSSION_KEY);
        courseService.shelveCourse(user,courseIds,true);
        return CommonResponseDto.ofSuccess();
    }

    /**
     * 下架
     * @param session
     * @param courseIds
     * @return
     */
    @RequestMapping(value = "/offShelve",method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto offShelveCourse(HttpSession session,@RequestBody List<Integer> courseIds){
        User user = (User)session.getAttribute(Constants.BACKEND_USER_SESSSION_KEY);
        courseService.shelveCourse(user,courseIds,false);
        new Thread( new Runnable() {
            @Override
            public void run() {
                for(int courseId : courseIds){
                    delRedis(courseId);
                }
            }
        } ).start();
        return CommonResponseDto.ofSuccess();
    }

    /**
     * 删除
     */
    @RequestMapping(value = "/delete",method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto deleteCourses(HttpSession session,@RequestBody List<Integer> courseIds){
        User user = (User)session.getAttribute(Constants.BACKEND_USER_SESSSION_KEY);
        courseService.deleteCourse(user,courseIds);
        new Thread( new Runnable() {
            @Override
            public void run() {
                for(int courseId : courseIds){
                    delRedis(courseId);
                }
            }
        } ).start();
        return CommonResponseDto.ofSuccess();
    }

    /**
     * @Description:校验当前课程是否正在导入报名学员
     * @Date: 2018/8/28 17:51
     * @param: dto
     * @param: session
     * @Author: wsc
     */
    @PostMapping("/checkSignImport")
    @ResponseBody
    public CommonResponseDto checkSignImport(@RequestBody CourseBasicDto dto, HttpSession session){
        User user = (User)session.getAttribute(Constants.BACKEND_USER_SESSSION_KEY);
        return courseService.checkSignImport(dto.getCourseId(),user.getId());
    }

    /**
     * @Description:查询是否有报名学员
     * @Date: 2018/8/30 15:46
     * @param: dto
     * @Author: wsc
     */
    @PostMapping("/checkSign")
    @ResponseBody
    public CommonResponseDto checkSign(@RequestBody CourseBasicDto dto){
        return courseService.checkSign(dto.getCourseId());
    }
}
