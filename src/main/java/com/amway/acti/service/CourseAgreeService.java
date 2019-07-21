/**
 * Created by Will Zhang on 2018/2/24.
 */
package com.amway.acti.service;

import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Validated
public interface CourseAgreeService
{

  /**
   * 获取课程点赞数
   * @return
   */
  int getAgreeNum(int courseId);

  int addAgree(@NotNull(message = "用户ID不能为空") Integer userId, @NotNull(message = "课程ID不能为空") Integer courseId);

  /**
   * 判断用户是否点赞
   * @param courseId
   * @param userId
   * @return
   */
  boolean selectIsAgreed(int courseId,int userId);

  Integer getAgreeNumFromRedisCache(Integer courseId);

  void initRedisCacheAgreeNumByCourseId(Integer courseId);
}
