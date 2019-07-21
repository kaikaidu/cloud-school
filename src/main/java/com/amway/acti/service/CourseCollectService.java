/**
 * Created by Will Zhang on 2018/2/24.
 */

package com.amway.acti.service;

import org.springframework.validation.annotation.Validated;

@Validated
public interface CourseCollectService
{
  /**
   * 获取课程收藏数
   * @return
   */
  int getCollectNum( int courseId);
}
