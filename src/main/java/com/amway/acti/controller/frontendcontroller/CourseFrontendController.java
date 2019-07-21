/**
 * Created by Will Zhang on 2018/2/23.
 */

package com.amway.acti.controller.frontendcontroller;

import com.amway.acti.base.context.MiniProgramRequestContextHolder;
import com.amway.acti.base.util.Constants;
import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.dto.CourseDetailDto;
import com.amway.acti.dto.CourseDto;
import com.amway.acti.dto.PageDto;
import com.amway.acti.model.Course;
import com.amway.acti.model.User;
import com.amway.acti.service.CourseService;
import com.amway.acti.service.UserService;
import com.amway.acti.transform.CourseTransform;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/frontend/course")
public class CourseFrontendController {
    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseTransform courseTransform;

    @Autowired
    private UserService userService;

    @Value("${redisSwitch.flag}")
    private boolean redisSwitch;

    /**
     * 课程列表查询
     *
     * @param pageNo
     * @param pageSize
     * @param type
     * @return
     */
    @RequestMapping(value = "/courseList", method = RequestMethod.GET)
    public CommonResponseDto<PageDto<CourseDto>> courseList(@RequestParam(value = "pageNo") Integer pageNo, @RequestParam("pageSize") Integer pageSize,
                                                            @RequestParam(value = "type", required = false) String type) {
        log.info("pageNo:{},pageSize:{},type:{}", pageNo, pageSize, type);
       // int userId = RequestHeaderContext.getInstance().getUid();
        //调用userService,根据openid查询用户信息
        User user = userService.getUser(MiniProgramRequestContextHolder.getRequestUser().getUid());

        log.info("user:{}", user.toString());

        PageInfo<Course> pageInfo;
        PageDto<CourseDto> pageDto = null;
        int ident = user.getIdent();
        //学员分类查询课程
        if (Constants.USER_IDENT_STUDENT == ident) {
            pageInfo = courseService.selectByTypeAndUser(user.getId(), user.getAdainfoMd5(), pageNo, pageSize, type);
            pageDto = courseTransform.transformPageInfoToDto(pageInfo, type, user.getId());
        } else if (Constants.USER_IDENT_LECTURER == ident) {
            //讲师查询相关课程
            pageInfo = courseService.selectByTeacher(user.getId(), pageNo, pageSize);
            pageDto = courseTransform.transformPageInfoToDto(pageInfo, null, user.getId());
        }
        return CommonResponseDto.ofSuccess(pageDto);
    }

    /**
     * 课程详情查询接口
     */
    @RequestMapping(value = "/courseInfo", method = RequestMethod.GET)
    public CommonResponseDto<CourseDetailDto> courseInfo(Integer courseId) {
        return CommonResponseDto.ofSuccess(courseTransform.transformModelToDto(courseId,
            MiniProgramRequestContextHolder.getRequestUser().getUid()));
    }
}
