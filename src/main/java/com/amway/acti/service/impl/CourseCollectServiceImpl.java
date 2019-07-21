/**
 * Created by Will Zhang on 2018/2/24.
 */

package com.amway.acti.service.impl;

import com.amway.acti.dao.CourseCollectMapper;
import com.amway.acti.service.CourseCollectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CourseCollectServiceImpl implements CourseCollectService
{
  @Autowired
  private CourseCollectMapper courseCollectMapper;

  /**
   * 获取课程收藏数
   * @return
   */
  @Override
  public int getCollectNum(int courseId)
  {
    log.info("courseId:{}",courseId);
    int collectNum = courseCollectMapper.countByCourseId(courseId);
    log.info("collectNum:{}",collectNum);
    return collectNum;
  }
}
