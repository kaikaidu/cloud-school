/**
 * Created by Will Zhang on 2018/2/24.
 */

package com.amway.acti.service;

import com.amway.acti.model.User;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Validated
public interface CourseSignUpService
{

  /**
   * 获取实际报名成功人数
   * @param courseId
   * @return
   */
  Integer getSignSuccessNum(@NotNull(message = "课程id不能为空")Integer courseId, Byte verity);

  /**
   * 获取报名人数
   * @param courseId
   * @return
   */
  Integer getSignUpNum(@NotNull(message = "课程id不能为空")Integer courseId);

  /**
   *查询是否报名
   */
  Boolean checkIsSignUp(@NotNull(message = "课程id不能为空") Integer courseId,String adaInfoMd5);

  /**
   * 报名
   * @param user
   * @param courseId
   */
  int signUp(User user,@NotNull(message = "课程id不能为空")Integer courseId,@NotBlank(message = "formId不能为空") String formId);

  void initRedisCacheApplyNumByCourseId(Integer courseId);

  Integer getApplyNumFromRedisCache(Integer courseId);

  void upadteCourseApplyNumRedisCache(Integer courseId,Boolean flag);

}
