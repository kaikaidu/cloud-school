/**
 * Created by Will Zhang on 2018/2/24.
 */

package com.amway.acti.service.impl;

import com.amway.acti.dao.CourseApprovalMapper;
import com.amway.acti.service.CourseApprovalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CourseApprovalServiceImpl implements CourseApprovalService
{
  @Autowired
  private CourseApprovalMapper courseApprovalMapper;

  /**
   * 需要审批课程获取报名数，只计算审批通过人数
   * @param courseId
   * @return
   */
  @Override
  public int getApplyNum(Integer courseId)
  {
    log.info("courseId:{}",courseId);
    int approvedNum = courseApprovalMapper.countApprovedNum(courseId);
    log.info("approvedNum:{}",approvedNum);
    return approvedNum;
  }

  /**
   * 获取学员的审批状态
   * @param userId
   * @param courseId
   * @return
   */
  @Override
  public Integer getCourseApplyStatus(Integer userId, Integer courseId)
  {
    log.info("userId:{},courseId:{}",userId,courseId);
    long bt = System.currentTimeMillis();
    Integer apprResult = courseApprovalMapper.selectApprResultByUserIdAndCourseId(courseId,userId);
    long et2 = System.currentTimeMillis();
    log.info("courseList3:"+(et2 - bt)+ "ms");
    log.info("apprResult:{}",apprResult);
    return apprResult;
  }
}
