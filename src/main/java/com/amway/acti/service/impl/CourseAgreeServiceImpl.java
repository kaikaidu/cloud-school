/**
 * Created by Will Zhang on 2018/2/24.
 */

package com.amway.acti.service.impl;

import com.amway.acti.base.redis.RedisComponent;
import com.amway.acti.base.util.Constants;
import com.amway.acti.dao.CourseAgreeMapper;
import com.amway.acti.dto.CourseDetailDto;
import com.amway.acti.model.CourseAgree;
import com.amway.acti.service.CourseAgreeService;
import com.amway.acti.service.RealityServer;
import com.amway.acti.service.RedisService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class CourseAgreeServiceImpl implements CourseAgreeService {

    @Autowired
    private CourseAgreeMapper courseAgreeMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private RedisComponent redisComponent;

    @Autowired
    private RealityServer realityServer;


    @Override
    public int getAgreeNum(int courseId) {
        log.info("courseId:{}", courseId);
        int agreeNum = courseAgreeMapper.countByCourseId(courseId);
        log.info("agreeNum:{}", agreeNum);
        return agreeNum;
    }

    @Override
    public boolean selectIsAgreed(int courseId, int userId) {
        return courseAgreeMapper.selectIsAgreed(courseId, userId);
    }

    @Override
    public int addAgree(Integer userId, Integer courseId) {
        log.info("userId:{}, courseId:{}", userId, courseId);
        int result = -1;
        String key = Constants.SAFETYLOCK_AGREE+ courseId;
        try {
            log.info("AGREE setNx:{}", key);
            while (!redisService.setNx0(key, "" + userId)) {
                log.info("user 尚未获取到锁:{}", userId);
            }
            CourseAgree agree = courseAgreeMapper.selectByCourseAndUser(courseId, userId);
            if (agree == null) {
                log.info("agree action");
                agree = new CourseAgree();
                agree.setCourseId(courseId);
                agree.setUserId(userId);
                new Date(System.currentTimeMillis());
                agree.setAgreeTime(new Date());
                result = courseAgreeMapper.insertSelective(agree);

                //课程列表Redis-根据课程id从缓存中取出数据 修改点赞人数 在存入缓存中 add wsc
                realityServer.updateRedisValue(userId, courseId, Constants.Number.INT_NUMBER0, Constants.Number.INT_NUMBER1, "add", null);

                //课程详情Redis-更新是否点赞
                upadteCourseInfoIsAgreeRedisCache(userId, courseId, true);
                //更新缓存中课程的点赞数
                upadteCourseAgreeNumRedisCache(courseId, true);

            } else {
                log.info("cancel agree");
                result = courseAgreeMapper.deleteByPrimaryKey(agree.getId());

                //根据课程id从缓存中取出数据 修改点赞人数 在存入缓存中 add wsc
                realityServer.updateRedisValue(userId, courseId, Constants.Number.INT_NUMBER0, Constants.Number.INT_NUMBER1, "", null);

                //课程详情Redis-更新是否点赞
                upadteCourseInfoIsAgreeRedisCache(userId, courseId, false);
                //更新缓存中课程的点赞数
                upadteCourseAgreeNumRedisCache(courseId, false);
            }
        } catch (Exception ex) {
            redisService.delete(key);
            throw ex;
        } finally {
            redisService.delete(key);
        }
        log.info("result:{}", result);
        return result;
    }

    /***
     * 更新缓存中课程的点赞数
     * @param courseId
     * @param flag
     */
    private void upadteCourseAgreeNumRedisCache(Integer courseId, Boolean flag){
        if (redisComponent.hasKey(Constants.COURSE_AGREE_NUM + courseId)) {
            Integer agreeNum = Integer.parseInt(redisService.getValue(Constants.COURSE_AGREE_NUM + courseId).toString());
            if (flag) {
                ++agreeNum;
            } else {
                if (agreeNum > 0) {
                    --agreeNum;
                }
            }
            redisComponent.set(Constants.COURSE_AGREE_NUM + courseId, agreeNum);
        }else{
            this.initRedisCacheAgreeNumByCourseId(courseId);
        }
    }

    /***
     * 更新课程详情缓存，是否点赞
     * @param userId
     * @param courseId
     */
    private void upadteCourseInfoIsAgreeRedisCache(Integer userId, Integer courseId, Boolean flag) {
        try {
            if (redisComponent.hasKey(Constants.COURSE_USER_INFO + courseId + ":" + userId)) {
                ObjectMapper objectMapper = new ObjectMapper();
                Object obj0 = redisService.getValue(Constants.COURSE_USER_INFO + courseId + ":" + userId);
                CourseDetailDto courseDetailDto = objectMapper.readValue(obj0.toString(), new TypeReference<CourseDetailDto>() {});
                if (null != courseDetailDto) {
                    if (flag) {
                        courseDetailDto.setAgree(1);
                    } else {
                        courseDetailDto.setAgree(0);
                    }
                    redisComponent.set(Constants.COURSE_USER_INFO + courseId + ":" + userId, courseDetailDto,
                        Constants.REDIS_EXPIRED_TIME, Constants.REDIS_EXPIRED_UNIT);
                }
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    /**
     * 初始化缓存中某课程的点赞数，在小程序端查询课程列表时调用（防止缓存数据丢失）
     * @param courseId
     */
    @Override
    public void initRedisCacheAgreeNumByCourseId(Integer courseId) {
        if (!redisComponent.hasKey(Constants.COURSE_AGREE_NUM + courseId)) {
            redisComponent.set(Constants.COURSE_AGREE_NUM + courseId, this.getAgreeNum(courseId));
        }
    }

    /***
     * 从缓存中取某课程的点赞数
     * @param courseId
     * @return
     */
    @Override
    public Integer getAgreeNumFromRedisCache(Integer courseId) {
        if (redisComponent.hasKey(Constants.COURSE_AGREE_NUM + courseId)) {
            return Integer.parseInt(redisService.getValue(Constants.COURSE_AGREE_NUM + courseId).toString());
        }
        return 0;
    }
}
