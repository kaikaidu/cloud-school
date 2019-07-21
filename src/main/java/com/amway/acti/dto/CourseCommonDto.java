/**
 * Created by Will Zhang on 2018/2/24.
 */

package com.amway.acti.dto;

import lombok.Data;

@Data
public abstract class CourseCommonDto
{
  /**
   * 标题
   */
  private String title;

  /**
   * 图片
   */
  private String picture;

  /**
   * 描述
   */
  private String description;

  /**
   * 上课时间
   */
  private String startTime;

  /**
   * 标签
   */
  private String label;

  /**
   * 报名数
   */
  private Integer applyNum;

  /**
   * 点赞数
   */
  private Integer agreeNum;

  /**
   * 收藏数
   */
  private Integer collectNum;
}
