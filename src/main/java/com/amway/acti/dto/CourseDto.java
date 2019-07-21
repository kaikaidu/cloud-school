/**
 * Created by Will Zhang on 2018/2/23.
 */

package com.amway.acti.dto;

import lombok.Data;

/**
 * 课程列表Dto
 */
@Data
public class CourseDto extends CourseCommonDto
{

  /**
   * 审核状态
   *  approved:可参加
   *  pending:审核中
   *  failed:审核失败
   */
  private String applyStatus;

  /**
   * 是否点赞：0-未点赞，1-已点赞
   */
  private int isAgree;

  private Integer courseId;
}
