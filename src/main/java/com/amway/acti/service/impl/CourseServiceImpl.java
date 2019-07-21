/**
 * Created by Will Zhang on 2018/2/23.
 */

package com.amway.acti.service.impl;

import com.amway.acti.base.redis.CourseRedisComponent;
import com.amway.acti.base.redis.RedisComponent;
import com.amway.acti.base.exception.AmwayLogicException;
import com.amway.acti.base.util.Constants;
import com.amway.acti.base.util.Constants.CourseSearchType;
import com.amway.acti.base.util.Constants.ErrorCode;
import com.amway.acti.base.util.DateUtil;
import com.amway.acti.dao.*;
import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.dto.CourseListSearchDto;
import com.amway.acti.model.*;
import com.amway.acti.service.BackendInvesOnlineService;
import com.amway.acti.service.BackendTestOnlineService;
import com.amway.acti.service.CourseService;
import com.amway.acti.service.RedisService;
import com.amway.acti.transform.CourseTeacherTransform;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CourseServiceImpl implements CourseService
{

  @Autowired
  private CourseMapper courseMapper;

  @Autowired
  private CourseTeacherTransform courseTeacherTransform;

  @Autowired
  private CourseTeacherMapper courseTeacherMapper;

  @Autowired
  private BackendTestOnlineService backendTestOnlineService;

  @Autowired
  private BackendInvesOnlineService backendInvesOnlineService;

  @Autowired
  private RedisService redisService;

  @Autowired
  private CourseRedisComponent courseRedisComponent;

  @Autowired
  private RedisComponent redisComponent;

  @Autowired
  private CourseAssistMapper courseAssistMapper;

  @Autowired
  private CourseApprovalMapper courseApprovalMapper;

  @Autowired
  private CourseSignUpMapper courseSignUpMapper;

  @Autowired
  private UserMapper userMapper;

  @Override
  public PageInfo<Course> selectByTypeAndUser(Integer userId,String adaInfoMd5, Integer pageNo, Integer pageSize, String type)
  {
    log.info("adaInfoMd5:{},pageNo:{},pageSize:{},type:{}",adaInfoMd5,pageNo,pageSize,type);
    PageHelper.startPage(pageNo,pageSize);
    Date localDate = new Date();
    List<Course> courses;
    long bt1 = System.currentTimeMillis();
    //可参加
    if(CourseSearchType.PARTICIPATING.equals(type)){
      courses = courseMapper.selectPartCourse(adaInfoMd5,localDate);
    }
    //已报名
    else if(CourseSearchType.REGISTERED.equals(type)) {
      courses = courseMapper.selectRegistedCourse(adaInfoMd5,localDate);
    }
    //已完成
    else {
      courses = courseMapper.selectFinishedCourse(adaInfoMd5,localDate);
    }
    long et2 = System.currentTimeMillis();
    log.info(Thread.currentThread().getName()+"_kaikai_courseList_Test0:"+(et2 - bt1)+ "ms");
    log.info("courses:{}",courses);
    return new PageInfo<>(courses);
  }

  @Override
  public PageInfo<Course> selectByTeacher(int userId,Integer pageNo,Integer pageSize)
  {
    PageHelper.startPage(pageNo,pageSize);
    List<Course> courses = courseMapper.selectByTeacher(userId);
    return new PageInfo<>(courses);
  }

  /**
   * 课程详情查询
   * @param courseId
   * @return
   */
  @Override
  public Course selectDetailCourse(Integer courseId,Integer userId,Short ident)
  {
    Course course = courseMapper.selectByPrimaryKey(courseId);
    if(course == null || course.getState() == 0){
      throw new AmwayLogicException(ErrorCode.COURSE_NOT_EXIST,"课程不存在");
    }
    //助教
    if(ident!=null && Constants.USER_IDENT_ASSISTANT == ident && userId != course.getSysId()){
      throw new AmwayLogicException(ErrorCode.COURSE_NOT_OPERATE,"课程不能操作");
    }
    return course;
  }

  /**
   * 课程详情查询
   * @param courseId
   * @return
   */
  @Override
  public Course getCourseById(Integer courseId) {
    return courseMapper.selectByPrimaryKey(courseId);
  }

  /**
   * @Description:校验当前课程是否正在导入报名学员
   * @Date: 2018/8/28 17:51
   * @param: dto
   * @param: session
   * @Author: wsc
   */
  @Override
  public CommonResponseDto checkSignImport(Integer courseId, Integer userId) {
    if (!redisComponent.hasKey(Constants.STUDENT_COUNT + courseId + "#" + userId)) {
      return CommonResponseDto.ofSuccess();
    } else {
      return CommonResponseDto.ofFailure(ErrorCode.TEACHAR_NULL,"当前课程正在导入报名学员，请在导入完成后修改审批状态!");
    }
  }

  /**
   * @Description:查询是否有报名学员
   * @Date: 2018/8/30 15:46
   * @param: dto
   * @Author: wsc
   */
  @Override
  public CommonResponseDto checkSign(Integer courseId) {
    List<CourseSignUp> courseSignUps = courseSignUpMapper.selectSignByCourseId(courseId);
    if (null != courseSignUps && !courseSignUps.isEmpty()) {
      return CommonResponseDto.ofSuccess();
    }
    return CommonResponseDto.ofFailure(ErrorCode.TEACHAR_NULL,"");
  }


  /**
   * @Description:新增或修改课程基本信息
   * @Date: 2018/5/21 18:52
   * @param: course
   * @param: teachers
   * @param: courseId
   */
  @Override
  @Transactional
  public Integer insertOrUpdateCourse(Course course, String[] teachers, String[] assists, Integer courseId) {
    if(courseId == null){
      courseMapper.insertSelective(course);
      addAgreeNumAndApplyNumtoRedis(course.getSysId());
    }else {
      course.setSysId(courseId);
      courseMapper.updateBasicInfo(course);
      //修改缓存中的基本信息
      courseRedisComponent.delCourseRedisByCourseId(courseId);
      //add wsc start
      //根据课程id查询审核表
      List<CourseApproval> courseApprovals = courseApprovalMapper.selectApprovalByCourseId(courseId);
      //判断审核状态  是否需要审核 1：需要 0：不需要
      if (course.getIsVerify() == Constants.Number.BYTE_NUMBER) {
        //不需要审核改为需要审核，需要在审核表中加入已报名的相关人员，切全部审核通过
        insertCourseApprotal(courseApprovals,courseId);
      } else {
        //需要审核改为不需要审核，需要把审核表中相关课程的清空,审核中的学员，自动变成报名成功。
        updateSignUp(courseApprovals,courseId);
      }
      //add wsc end
    }
    courseId = course.getSysId();
    log.info("courseId:{}",courseId);
    List<CourseTeacher> courseTeachers = courseTeacherTransform.transformStringToModel(teachers,courseId);
    Integer deleteResult = courseTeacherMapper.deleteByCourse(courseId);
    log.info("deleteByCourse result:{}",deleteResult);
    if (null != courseTeachers && !courseTeachers.isEmpty()) {
      Integer result = courseTeacherMapper.insertList(courseTeachers);
      log.info("insertList result:{},listSize:{}",result,courseTeachers.size());
    }

    List<CourseAssist> courseAssists = courseTeacherTransform.transformStringToModelByAssists(assists,courseId);
    courseAssistMapper.deleteByCourse(courseId);
    if (null != courseAssists && !courseAssists.isEmpty()) {
      courseAssistMapper.insertList(courseAssists);
    }
    return course.getSysId();
  }

  //新增审核表
  private void insertCourseApprotal(List<CourseApproval> courseApprovals,Integer courseId){
    //审核表中为null，说明是从不需要审核改为需要审核，需在审核表中新增报名学员
    if (null == courseApprovals || courseApprovals.isEmpty()) {
      //根据课程id查询查询报名学员表
      courseApprovals = courseSignUpMapper.selectSignUpByCourseId(courseId);
      //报名学员不为null，说明该课程有报名的学员
      if (null != courseApprovals && !courseApprovals.isEmpty()) {
        int pointsDataLimit = 300;//限制条数
        int size = courseApprovals.size();
        //数量大于300 分批处理 sqlserver 执行sql时有参数个数限制，参数不能超过2100个
        if (size > pointsDataLimit) {
          int partNum = (int)Math.ceil((double)size/(double)pointsDataLimit);//分批数
          log.info("courseApprovals.size:{},partNum:{}",size,partNum);
          for (int i = 0 ; i < partNum ; i ++) {
            if (i+1 == partNum) {
              pointsDataLimit = courseApprovals.size();
            }
            List<CourseApproval> listPage = courseApprovals.subList(0, pointsDataLimit);
            courseApprovalMapper.insertByList(listPage);
            //剔除
            courseApprovals.subList(0,pointsDataLimit).clear();
          }
        } else {
          courseApprovalMapper.insertByList(courseApprovals);
        }
      }
    }
  }

  //删除报名表
  private void updateSignUp(List<CourseApproval> courseApprovals,Integer courseId){
    //courseApprovals 不为null，说明是从需要审核改为不需要审核
    if (null != courseApprovals && !courseApprovals.isEmpty()) {
      //查询出审核失败的学员
      List<User> userList = userMapper.selectUserByCourseIdAndApprResult(courseId);
      //删除审核表中数据，根据课程id
      courseApprovalMapper.deleteApprotalByCourseId(courseId);
      if (null != userList && !userList.isEmpty()) {
        int pointsDataLimit = 2000;//限制条数
        int size = userList.size();
        //数量大于2000 分批处理 sqlserver 执行sql时有参数个数限制，参数不能超过2100个
        if (size > pointsDataLimit) {
          int partNum = (int)Math.ceil((double)size/(double)pointsDataLimit);//分批数
          log.info("userList.size:{},partNum:{}",size,partNum);
          for (int i = 0 ; i < partNum ; i ++) {
            if (i+1 == partNum) {
              pointsDataLimit = userList.size();
            }
            List<User> listPage = userList.subList(0, pointsDataLimit);
            //删除审核失败的报名学员
            courseSignUpMapper.deleteByAdainfoMd5List(listPage);
            //剔除
            userList.subList(0,pointsDataLimit).clear();
          }
        } else {
          //删除审核失败的报名学员
          courseSignUpMapper.deleteByAdainfoMd5List(userList);
        }
      }
    }
  }

  /***
   * 创建课程时，添加该课程的点赞数与报名数到缓存，供课程列表与课程详情使用
   * @param courseId
   */
  private void addAgreeNumAndApplyNumtoRedis(Integer courseId) {
    try {
      redisComponent.set(Constants.COURSE_AGREE_NUM + courseId, 0,
          Constants.REDIS_EXPIRED_TIME, Constants.REDIS_EXPIRED_UNIT);
      redisComponent.set(Constants.COURSE_APPLY_NUM + courseId, 0,
          Constants.REDIS_EXPIRED_TIME, Constants.REDIS_EXPIRED_UNIT);
    } catch (Exception ex) {
      log.error(ex.getMessage(), ex);
    }
  }

  @Override
  public Boolean checkUserAndCourse(Integer userId, Integer courseId) {
    return Boolean.TRUE;
  }

  /**
   * 判断用户是否有权限操作课程
   * @param courseIds
   * @param userId
   * @param ident
   * @return
   */
  @Override
  public Boolean checkUserAndCourses(List<Integer> courseIds,Integer userId,Short ident){
    //学员，讲师，返回false
    if(Constants.USER_IDENT_STUDENT == ident || Constants.USER_IDENT_LECTURER == ident){
      return false;
    }
    //管理员，返回true
    if(Constants.USER_IDENT_ADMIN == ident){
      return true;
    }
    //助教
    Integer count = courseMapper.countUserWithinCourses(userId,courseIds);
    return count == courseIds.size();
  }

  @Override
  public PageInfo<Course> selectCourseList(User user, CourseListSearchDto dto) {
    log.info("CourseListSearchDto:{}",dto.toString());
    PageHelper.startPage(dto.getPageNum(),dto.getPageSize());
    Map<String,Object> param = handleParam(user,dto);
    List<Course> courses = courseMapper.selectCourseList(param);
    return new PageInfo<>(courses);
  }

  @Override
  public Long countCourse(User user, CourseListSearchDto dto) {
    Map<String,Object> param = handleParam(user,dto);
    return courseMapper.countCourse(param);
  }

  public Map<String,Object> handleParam(User user, CourseListSearchDto dto){
    Map<String,Object> param = new HashMap<>();
    param.put("title",dto.getTitle());
    param.put("createTime",dto.getCreateTime());
    param.put("startTime",dto.getStartTime());
    param.put("endTime",dto.getEndTime());
    param.put("label",dto.getLabel());
    param.put("shelve",dto.getShelve());
    param.put("ident",user.getIdent());
    param.put("userId",user.getId());
    param.put("order",dto.getCreateTimeOrder());
    return param;
  }

  @Override
  public void shelveCourse(User user, List<Integer> courseIds, Boolean shelve) {
    Boolean checkResult = checkUserAndCourses(courseIds,user.getId(),user.getIdent());
    log.info("check user within courses, result:{}",checkResult);
    if(!checkResult){
      throw new AmwayLogicException(ErrorCode.COURSE_NOT_OPERATE,"课程不能操作");
    }
    Integer result = courseMapper.updateShelveByIds(shelve,courseIds);
    log.info("updateResult:{},courseIds.size:{}",result,courseIds.size());
  }

  @Override
  public void deleteCourse(User user, List<Integer> courseIds) {
    Boolean checkResult = checkUserAndCourses(courseIds,user.getId(),user.getIdent());
    log.info("check user within courses, result:{}",checkResult);
    if(!checkResult){
      throw new AmwayLogicException(ErrorCode.COURSE_NOT_OPERATE,"课程不能操作");
    }
    Integer result = courseMapper.deleteByIds(courseIds);
    log.info("deleteResult:{},courseIds.size:{}",result,courseIds.size());
    //删除相关的问卷，测试卷
    backendTestOnlineService.removePapersByCourseIds(courseIds);
    backendInvesOnlineService.removePapersByCourseIds(courseIds);

    //清除缓存中关联的课程 add wsc
    for (Integer i: courseIds) {
      redisService.delete(Constants.COURSE_CACHE_KEY+i);
    }
  }

  /**
   * @Description:根据课程id查询课程详情
   * @Date: 2018/3/19 14:02
   * @param: courseId 课程id
   * @Author: wsc
   */
  @Override
  public Course selectCourseById(Integer courseId) {
    return courseMapper.selectByPrimaryKey(courseId);
  }

  @Override
  public Integer countByTitle(String title,Integer courseId) {
    return courseMapper.countByTitle(title,courseId);
  }

  @Override
  public Boolean isCourseStart(Integer courseId) {
    Course course = courseMapper.selectByPrimaryKey( courseId );
    if(course == null || course.getState() == 0){
      return false;
    }else if(course.getState() == 1){
      Date now = new Date( System.currentTimeMillis() );
      return DateUtil.isEffectiveDate( now, course.getStartTime(), course.getEndTime() );
    }
    return false;
  }

  /**
   * @Description:修改课程
   * @Date: 2018/5/3 16:55
   * @param: courseId
   * @Author: wsc
   */
  @Override
  public void updateCourseForClassState(Integer courseId) {
    courseMapper.updateCourseForClassState(courseId);
  }

  /**
   * @Description:修改课程
   * @Date: 2018/4/27 17:31
   * @param: courseId
   * @param: state
   * @Author: wsc
   */
  @Override
  public void updateCourse(Course course) {
      courseMapper.updateByPrimaryKeySelective(course);
  }
}
