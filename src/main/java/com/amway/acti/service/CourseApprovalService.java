/**
 * Created by Will Zhang on 2018/2/24.
 */

package com.amway.acti.service;

import javax.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public interface CourseApprovalService
{

  /**
   * 需要审批课程获取报名数
   * @param courseId
   * @return
   */
  int getApplyNum(@NotNull(message = "课程id不能为空") Integer courseId);

  /**
   * 获取学员的审批状态
   * @param userId
   * @param courseId
   * @return
   */
  Integer getCourseApplyStatus(@NotNull(message = "userId不能为空") Integer userId,@NotNull(message = "课程id不能为空") Integer courseId);
}
