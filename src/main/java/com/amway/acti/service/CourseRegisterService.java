/**
 * Created by Will Zhang on 2018/3/5.
 */

package com.amway.acti.service;

import com.amway.acti.model.User;
import javax.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public interface CourseRegisterService
{

  /**
   * 判断是否已经签到
   * @return
   */
  Boolean checkIsRegister(@NotNull(message = "用户id不能为空") Integer userId,@NotNull(message = "课程id不能为空") Integer courseId);

  /**
   *签到
   */
  void register(User user,@NotNull(message = "课程id不能为空") Integer courseId);
}
