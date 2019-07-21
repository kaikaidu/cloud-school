/**
 * Created by Will Zhang on 2018/3/5.
 */

package com.amway.acti.service.impl;

import com.amway.acti.dao.CourseAssistMapper;
import com.amway.acti.dao.CourseTeacherMapper;
import com.amway.acti.model.CourseTeacher;
import com.amway.acti.service.CourseTeacherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Slf4j
@Service
public class CourseTeacherServiceImpl implements CourseTeacherService
{

  @Autowired
  private CourseTeacherMapper courseTeacherMapper;

  @Autowired
  private CourseAssistMapper courseAssistMapper;

  /**
   * 根据课程查询讲师列表
   * @param courseId
   * @return
   */
  @Override
  public List<String> selectTeacherByCourse(Integer courseId)
  {
    log.info("courseId:{}",courseId);
    List<String> teachers = courseTeacherMapper.selectTeacherByCourse(courseId);
    log.info("courseId:{},teachers:{}", courseId, teachers.toString());
    return teachers;
  }

  /**
   * 根据课程查询助教列表
   * @param courseId
   * @return
   */
  @Override
  public List<String> selectAssistByCourse(Integer courseId)
  {
    log.info("courseId:{}",courseId);
    List<String> teachers = courseAssistMapper.selectAssistByCourseId(courseId);
    log.info("courseId:{},teachers:{}", courseId, teachers.toString());
    return teachers;
  }

  /**
   * 根据课程查询讲师列表
   * @param courseId
   * @return
   */
  @Override
  public List<Integer> selectTeacherIdByCourse(Integer courseId)
  {
    log.info("courseId:{}",courseId);
    return courseTeacherMapper.selectTeacherIdsByCourse(courseId);
  }

  /**
   * 根据课程查询讲师列表
   * @param courseId
   * @return
   */
  @Override
  public List<Integer> selectAssistIdByCourse(Integer courseId)
  {
    log.info("courseId:{}",courseId);
    return courseAssistMapper.selectAssistIdByCourse(courseId);
}

  /**
   * 查看讲师是否有评分权限，是否是班级下面的讲师
   * @param courseId
   * @param userId
   * @return
   */
  @Override
  public Boolean checkIsSitem(Integer courseId, Integer userId)
  {
    CourseTeacher courseTeacher = courseTeacherMapper.selectByUserAndCourse(courseId,userId);
    if(ObjectUtils.isEmpty(courseTeacher)){
      log.info("result:{}",false);
      return false;
    }
    log.info("result:{}",true);
    return true;
  }
}
