/**
 * Created by Will Zhang on 2018/3/5.
 */

package com.amway.acti.service;

import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;

@Validated
public interface CourseTeacherService
{

  /**
   * 根据课程查询讲师列表
   * @param courseId
   * @return
   */
  List<String> selectTeacherByCourse(@NotNull(message = "课程id不能为空") Integer courseId);

  /**
   * 查看讲师是否有评分权限，是否是班级下面的讲师
   * @param courseId
   * @param userId
   * @return
   */
  Boolean checkIsSitem(Integer courseId,Integer userId);

  List<Integer> selectTeacherIdByCourse(Integer courseId);

  List<Integer> selectAssistIdByCourse(Integer courseId);

  List<String> selectAssistByCourse(Integer courseId);
}
