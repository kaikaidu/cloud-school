/**
 * Created by Will Zhang on 2018/2/24.
 */

package com.amway.acti.service.impl;

import com.amway.acti.base.redis.RedisComponent;
import com.amway.acti.base.exception.AmwayLogicException;
import com.amway.acti.base.util.Constants;
import com.amway.acti.base.util.Constants.ApprResult;
import com.amway.acti.base.util.Constants.ErrorCode;
import com.amway.acti.dao.CourseApprovalMapper;
import com.amway.acti.dao.CourseMapper;
import com.amway.acti.dao.CourseSignUpMapper;
import com.amway.acti.dao.UserCertMapper;
import com.amway.acti.dto.CourseDetailDto;
import com.amway.acti.model.Course;
import com.amway.acti.model.CourseApproval;
import com.amway.acti.model.CourseSignUp;
import com.amway.acti.model.User;
import com.amway.acti.model.UserCert;
import com.amway.acti.service.CourseSignUpService;
import com.amway.acti.service.RealityServer;
import com.amway.acti.service.RedisService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class CourseSignUpServiceImpl implements CourseSignUpService {
    @Autowired
    private CourseSignUpMapper courseSignUpMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private CourseApprovalMapper courseApprovalMapper;

    @Autowired
    private RealityServer realityServer;

    @Autowired
    private RedisService redisService;

    @Autowired
    private RedisComponent redisComponent;
    @Autowired
    private UserCertMapper userCertMapper;

    /**
     * 不需要审批的课程查询报名人数
     *
     * @param courseId
     * @return
     */
    @Override
    public Integer getSignSuccessNum(Integer courseId, Byte verity) {
        log.info("courseId:{}", courseId);
        boolean isVerity = verity != null && 1 == verity;
        log.info("isVerity:{}", isVerity);
        int signUpNum;
        if (isVerity) {
            signUpNum = courseApprovalMapper.countApprovedNum(courseId);
        } else { //不需要审批的课程，查询报名表
            signUpNum = courseSignUpMapper.countByCourseId(courseId);
        }
        log.info("signUpNum:{}", signUpNum);
        return signUpNum;
    }

    @Override
    public Boolean checkIsSignUp(Integer courseId, String adaInfoMd5) {
        log.info("courseId:{},adaInfoMd5:{}", courseId, adaInfoMd5);
        CourseSignUp courseSignUp = courseSignUpMapper.selectByUserAndCourse(courseId, adaInfoMd5);
        if (ObjectUtils.isEmpty(courseSignUp)) {
            log.info("result:{}", false);
            return false;
        }
        log.info("result:{}", true);
        return true;
    }

    @Override
    @Transactional
    public int signUp(User user, Integer courseId, String formId) {
        int signUpResult = 1;
        log.info("user:{},courseId:{}", user.toString(), courseId);
        Course course = courseMapper.selectByPrimaryKey(courseId);
        if (1 == course.getIsVerify()) {
            signUpResult = 2;
        }
        checkSignUp(user, course);
        CourseSignUp courseSignUp1 = new CourseSignUp();
        courseSignUp1.setCourseId(courseId);
        courseSignUp1.setAdainfoMd5(user.getAdainfoMd5());
        courseSignUp1.setSingupTime(new Date());
        courseSignUp1.setFormid(formId);
        log.info("CourseSignUp:{}", courseSignUp1.toString());
        courseSignUpMapper.insertSelective(courseSignUp1);
        //报名时添加颁发证书记录
        if (0 == userCertMapper.selectCountByCourseIdAndUserId(courseId, user.getId())) {
            UserCert userCert = new UserCert();
            userCert.setIsAward(0);
            userCert.setCourseId(courseId);
            userCert.setUserId(user.getId());
            userCert.setState(1);
            userCert.setCreateTime(new Date());
            userCertMapper.insertSelective(userCert);
        }
        if (1 == course.getIsVerify()) {
            CourseApproval courseApproval = new CourseApproval();
            courseApproval.setUserId(user.getId());
            courseApproval.setCourseId(courseId);
            courseApproval.setApprResult(ApprResult.PENDING);
            log.info("CourseApproval:{}", courseApproval.toString());
            courseApprovalMapper.insertSelective(courseApproval);
            //signUpResult = 2;
            //根据课程id从缓存中取出数据 修改报名人数 在存入缓存中 add wsc
            realityServer.updateRedisValue(user.getId(), courseId, Constants.Number.INT_NUMBER1, Constants.Number.INT_NUMBER0, "", Constants.CourseApplyStatus.PENDING);
            upadteCourseApplyNumRedisCache(courseId, true);
        } else {
            //根据课程id从缓存中取出数据 修改报名人数 在存入缓存中 add wsc
            realityServer.updateRedisValue(user.getId(), courseId, Constants.Number.INT_NUMBER1, Constants.Number.INT_NUMBER0, "", Constants.CourseApplyStatus.APPROVED);
            //更新课程的报名人数
            upadteCourseApplyNumRedisCache(courseId, true);
        }

        // 更新课程详情是否已报名（按钮不显示）
        upadteCourseInfoIsSignUpRedisCache(user.getId(), courseId);
        return signUpResult;
    }

    public void checkSignUp(User user, Course course) {
        if (course == null || 0 == course.getState()) {
            throw new AmwayLogicException(ErrorCode.COURSE_NOT_EXIST, "课程不存在");
        }
        if (0 == course.getIsShelve()) {
            throw new AmwayLogicException(ErrorCode.COURSE_NOT_EXIST, "课程已下架");
        }
        if (Constants.Number.INT_NUMBER3 != course.getLabel()) {
            throw new AmwayLogicException(ErrorCode.NOT_SUPPORT_ACTIVITY, "不支持报名");
        }
        if (System.currentTimeMillis() < course.getSignUpBeginTime().getTime() || System.currentTimeMillis() > course.getSignUpEndTime().getTime()) {
            throw new AmwayLogicException(ErrorCode.ERROR_TIME_RANGE, "不在报名时间内");
        }
        List<CourseSignUp> courseSignUp = courseSignUpMapper.selectByUserAndCourseList(course.getSysId(), user.getAdainfoMd5());
        if (courseSignUp != null && courseSignUp.size() > 0) {
            throw new AmwayLogicException(ErrorCode.USER_SIGNED, "已经报名过");
        }
        //获取报名成功人数
        int applyNum = getSignSuccessNum(course.getSysId(), course.getIsVerify());
        if (applyNum >= course.getMaxApplyNum()) {
            throw new AmwayLogicException(ErrorCode.FULL_APPLY_NUM, "报名人数已满");
        }
    }

    @Override
    public Integer getSignUpNum(Integer courseId) {
        return courseSignUpMapper.countByCourseId(courseId);
    }

    /***
     * 更新缓存中课程的报名数
     * @param courseId
     */
    @Override
    public void upadteCourseApplyNumRedisCache(Integer courseId,Boolean flag) {
        if (redisComponent.hasKey(Constants.COURSE_APPLY_NUM + courseId)) {
            Integer applyNum = Integer.parseInt(redisService.
                getValue(Constants.COURSE_APPLY_NUM + courseId).toString());
            if (flag) {
                redisComponent.set(Constants.COURSE_APPLY_NUM + courseId, ++applyNum);
            } else {
                if (applyNum > 0) {
                    redisComponent.set(Constants.COURSE_APPLY_NUM + courseId, --applyNum);
                }
            }
        } else {
            this.initRedisCacheApplyNumByCourseId(courseId);
        }
    }

    /**
     * 初始化缓存中某课程的报名数，在小程序端查询课程列表时调用（防止缓存数据丢失）
     * @param courseId
     */
    @Override
    public void initRedisCacheApplyNumByCourseId(Integer courseId) {
        if (!redisComponent.hasKey(Constants.COURSE_APPLY_NUM + courseId)) {
            Course course = courseMapper.selectByPrimaryKey(courseId);
            redisComponent.set(Constants.COURSE_APPLY_NUM + courseId,
                this.getSignSuccessNum(courseId, course.getIsVerify()));
        }
    }

    /***
     * 从缓存中取某课程的报名数
     * @param courseId
     * @return
     */
    @Override
    public Integer getApplyNumFromRedisCache(Integer courseId) {
        if (redisComponent.hasKey(Constants.COURSE_APPLY_NUM + courseId)) {
            return Integer.parseInt(redisService.getValue(Constants.COURSE_APPLY_NUM + courseId).toString());
        }
        return 0;
    }

    /***
     * 更新课程详情缓存，是否已报名
     * @param userId
     * @param courseId
     */
    private void upadteCourseInfoIsSignUpRedisCache(Integer userId, Integer courseId) {
        try {
            if (redisComponent.hasKey(Constants.COURSE_USER_INFO + courseId + ":" + userId)) {
                ObjectMapper objectMapper = new ObjectMapper();
                Object obj0 = redisService.getValue(Constants.COURSE_USER_INFO + courseId + ":" + userId);
                CourseDetailDto courseDetailDto = objectMapper.readValue(obj0.toString(), new TypeReference<CourseDetailDto>() {
                });
                if (null != courseDetailDto) {
                    courseDetailDto.setSignUp(0);
                    redisComponent.set(Constants.COURSE_USER_INFO + courseId + ":" + userId, courseDetailDto,
                        Constants.REDIS_EXPIRED_TIME, Constants.REDIS_EXPIRED_UNIT);
                }
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }
}
