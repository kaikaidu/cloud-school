/**
 * Created by Will Zhang on 2018/3/5.
 */

package com.amway.acti.service.impl;

import com.amway.acti.base.exception.AmwayLogicException;
import com.amway.acti.base.util.Constants.ErrorCode;
import com.amway.acti.dao.CourseMapper;
import com.amway.acti.dao.CourseRegisterMapper;
import com.amway.acti.dao.CourseSignUpMapper;
import com.amway.acti.model.Course;
import com.amway.acti.model.CourseRegister;
import com.amway.acti.model.CourseSignUp;
import com.amway.acti.model.User;
import com.amway.acti.service.CourseRegisterService;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Slf4j
@Service
public class CourseRegisterServiceImpl implements CourseRegisterService
{

  @Autowired
  private CourseRegisterMapper courseRegisterMapper;

  @Autowired
  private CourseSignUpMapper courseSignUpMapper;

  @Autowired
  private CourseMapper courseMapper;

  @Override
  public Boolean checkIsRegister(Integer userId, Integer courseId)
  {
    log.info("userId:{},courseId:{}",userId,courseId);
    CourseRegister courseRegister = courseRegisterMapper.selectByUserAndCourse(userId,courseId);
    if(courseRegister == null){
      log.info("result:{}",false);
      return false;
    }
    log.info("result:{}",true);
    return true;
  }

  @Override
  public void register(User user, Integer courseId)
  {
    log.info("user:{},courseId:{}",user.toString(),courseId);
    Course course = courseMapper.selectByPrimaryKey(courseId);
    if(course == null || 0 == course.getState()){
      throw new AmwayLogicException(ErrorCode.COURSE_NOT_EXIST,"课程不存在");
    }
    if(0 == course.getIsShelve()){
      throw new AmwayLogicException(ErrorCode.COURSE_NOT_EXIST,"课程已下架");
    }
    if(0 == course.getIsSign()){
      throw new AmwayLogicException(ErrorCode.NOT_SUPPORT_ACTIVITY,"课程不需要签到");
    }
    if(System.currentTimeMillis()<course.getStartTime().getTime()||System.currentTimeMillis()>course.getEndTime().getTime()){
      throw new AmwayLogicException(ErrorCode.ERROR_TIME_RANGE,"不在签到时间内");
    }
    CourseSignUp courseSignUp = courseSignUpMapper.selectByUserAndCourse(courseId,user.getAdainfoMd5());
    if(ObjectUtils.isEmpty(courseSignUp)){
      throw new AmwayLogicException(ErrorCode.USER_NOT_SIGN,"没有报名该课程");
    }
    CourseRegister courseRegister = courseRegisterMapper.selectByUserAndCourse(user.getId(),courseId);
    if(courseRegister != null){
      throw new AmwayLogicException(ErrorCode.USER_REGISTERED,"已经签到过");
    }
    CourseRegister courseRegister1 = new CourseRegister();
    courseRegister1.setCourseId(courseId);
    courseRegister1.setUserId(user.getId());
    courseRegister1.setRegisterTime(new Date());
    log.info("CourseRegister:{}",courseRegister1.toString());
    courseRegisterMapper.insertSelective(courseRegister1);
  }
}
