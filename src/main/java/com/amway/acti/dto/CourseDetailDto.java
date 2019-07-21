/**
 * Created by Will Zhang on 2018/3/2.
 */

package com.amway.acti.dto;

import lombok.Data;

@Data
public class CourseDetailDto extends CourseCommonDto
{

  /**
   * 讲师
   */
  private String teacher;

  /**
   * 上课地址
   */
  private String address;

  /**
   * 课程编号
   */
  private String code;

  /**
   * 学习群二维码
   */
  private String qrCode;

  /**
   * 课程直播链接
   */
  private String liveUrl;

  /**
   * 签到按钮：0-不显示，1-显示
   */
  private int register = 0;

  /**
   * 调查按钮；0-不显示，1-显示
   */
  private int inves = 0;

  /**
   * 选择测试题按钮：0-不显示，1-显示
   */
  private int test = 0;

  /**
   * 抽签按钮：0-不显示，1-显示
   */
  private int draw = 0;

  /**
   * 评分按钮：0-不显示，1-显示
   */
  private int sitem = 0;

  /**
   * 资料下载按钮：0-不显示，1-显示
   */
  private int doc = 0;

  /**
   * 报名按钮：0-不显示，1-显示
   */
  private int signUp = 0;

  /**
   * 是否点赞：0-未点赞，1-已点赞
   */
  private int agree = 0;

  /**
   * 直播课程开始学习按钮，0-不显示；1-至灰；2-可点击
   */
  private int startLearn = 0;

  /**
   * 是否允许分享，0-不允许；1-允许
   */
  private int isShare;

  /**
   * 证书按钮是否显示 0-不显示，1-显示
   */
  private int cert;

  /**
   * 是否有未读的证书消息：0-没有，1-有
   */
  private int certIsRead;

}
