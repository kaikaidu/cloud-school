package com.amway.acti.service.impl;

import com.amway.acti.base.redis.RedisComponent;
import com.amway.acti.base.exception.AmwayLogicException;
import com.amway.acti.base.util.Constants;
import com.amway.acti.dao.*;
import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.model.*;
import com.amway.acti.service.ClassSettingService;
import com.amway.acti.service.RedisService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.*;

/**
 * @Description:分班设置
 * @Date:2018/4/27 10:12
 * @Author:wsc
 */
@Service
@Slf4j
public class ClassSettingServiceImpl implements ClassSettingService{

    @Autowired
    private ScoreResultMapper scoreResultMapper;

    @Autowired
    private MclassMapper mclassMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private ClassDrawMapper classDrawMapper;

    @Autowired
    private DrawResultMapper drawResultMapper;

    @Autowired
    private CourseTeacherMapper courseTeacherMapper;

    @Autowired
    private UserCustomMapper userCustomMapper;

    @Autowired
    private MclassCustomMapper mclassCustomMapper;

    @Autowired
    private WeightMapper weightMapper;

    @Autowired
    private RedisComponent redisComponent;

    @Autowired
    private RedisService redisService;

    /**
     * @Description:根据课程id查询评分
     * @Date: 2018/4/27 10:16
     * @param: courseId 课程id
     * @Author: wsc
     */
    @Override
    public CommonResponseDto getStudentScore(Integer courseId) {
        log.info("courseId:{}",courseId);
        List<ScoreResult> scoreResults = scoreResultMapper.selectScoreResultByCourseId(courseId);
        if (null != scoreResults && !scoreResults.isEmpty()) {
            throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"该课程已评分，不能修改");
        }
        return CommonResponseDto.ofSuccess();
    }

    /**
     * @Description:手动添加班级
     * @Date: 2018/4/27 15:52
     * @param: className 班级名称
     * @Author: wsc
     */
    @Override
    @Transactional
    public CommonResponseDto addClassManual(String className,Integer courseId) {
        log.info("className:{},courseId:{}",className,courseId);
        Mclass mclass = new Mclass();
        mclass.setName(className);
        mclass.setCourseId(courseId);
        mclass.setState(Constants.States.VALID);
        mclass.setCreateTime(new Date());
        mclassMapper.insertSelective(mclass);
        //删除评分模板
        weightMapper.deleteWeightByCourseId(courseId);
        //删除缓存中的评分
        deleteRedisScoreMap(courseId);
        String key = Constants.COURSE_USER_INFO + courseId;
        String[] keys = redisComponent.keys( key );
        redisComponent.del( keys );
        return CommonResponseDto.ofSuccess(mclass.getId());
    }

    /**
     * @Description:查询当前课程下是否已分班
     * @Date: 2018/4/27 17:54
     * @param: courseId
     * @Author: wsc
     */
    @Override
    public Course getCourse(Integer courseId) {
        return courseMapper.selectByPrimaryKey(courseId);
    }

    /**
     * @Description:根据课程id删除班级
     * @Date: 2018/4/28 10:29
     * @param: courseId 课程id
     * @Author: wsc
     */
    @Override
    @Transactional
    public CommonResponseDto deleteClass(Integer courseId,Integer classId) {
        //根据班级id删除班级
        mclassMapper.deleteClassById(classId);
        //删除跟班级关联的学员
        drawResultMapper.deleteByClassId(classId);
        //删除演讲内容
        classDrawMapper.deleteClassDrawByClassId(classId);
        //删除讲师
        courseTeacherMapper.deleteByClassId(classId);
        //修改课程表的抽签，评分状态，删除权重表
        deleteCourseWeight(courseId);
        //删除缓存中的评分
        deleteRedisScoreMap(courseId);
        String key = Constants.COURSE_USER_INFO + courseId;
        String[] keys = redisComponent.keys( key );
        redisComponent.del( keys );
        //获取该课程下所有的班级
        List<Mclass> mclassList = mclassMapper.selectByCourseId(courseId);
        //存放重置后的班级名称
        List<String> strList = new ArrayList <>();
        //重置班级名称
        boolean flag = reset(mclassList,strList);
        if (flag) {
            for (int i = 0; i < mclassList.size(); i++) {
                mclassList.get(i).setName(strList.get(i));
            }
            //批量修改
            mclassMapper.updateMclassForList(mclassList);
            return CommonResponseDto.ofSuccess();
        } else {
            throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"重置班级名称失败");
        }
    }

    /**
     * @Description:根据课程id查询班级
     * @Date: 2018/4/28 11:01
     * @param: courseId 课程id
     * @Author: wsc
     */
    @Override
    public CommonResponseDto classList(Integer courseId) {
        //查询课程下的班级，有是否关联学员标识
        List<MclassCustom> mclassList = mclassCustomMapper.selectByCourseId(courseId);
        return CommonResponseDto.ofSuccess(mclassList);
    }

    /**
     * @Description:删除课程下所有班级,演讲内容,以及关联的讲师
     * @Date: 2018/4/28 14:07
     * @param: courseId 课程id
     * @Author: wsc
     */
    @Override
    @Transactional
    public CommonResponseDto deleteClassByCourseId(Integer courseId) {
        List<Mclass> mclassList = mclassMapper.selectByCourseId(courseId);
        if (null != mclassList && !mclassList.isEmpty()) {
            List<Integer> classIdList = new ArrayList <>();
            for (Mclass c : mclassList) {
                classIdList.add(c.getId());
            }
            //删除班级
            mclassMapper.deleteClassByCourseId(courseId);
            //删除跟班级关联的学员
            drawResultMapper.deleteByClassIdList(classIdList);
            //删除演讲内容
            classDrawMapper.deleteClassDrawByList(classIdList);
            //删除讲师
            courseTeacherMapper.deleteByCourseId(courseId);
        }
        //修改课程表的抽签，评分状态，删除权重表
        deleteCourseWeight(courseId);
        //删除缓存中的评分
        deleteRedisScoreMap(courseId);
        String key = Constants.COURSE_USER_INFO + courseId;
        String[] keys = redisComponent.keys( key );
        redisComponent.del( keys );
        return CommonResponseDto.ofSuccess();
    }

    /**
     * @Description:根据课程id查询已报名成功的学员
     * @Date: 2018/4/28 16:01
     * @param: courseId 课程id
     * @param: name 学员名称
     * @Author: wsc
     */
    @Override
    public CommonResponseDto findUser(Integer courseId,String name) {
        //判断课程是否需要审核，已成功报名的学员根据是否需要审核来自不同的表
        Course course = courseMapper.selectByPrimaryKey(courseId);
        if (!ObjectUtils.isEmpty(course)) {
            List<UserCustom> userList;
            //审核 1:需要审核 0：不需要
            if (course.getIsVerify() == Constants.Number.BYTE_NUMBER) {
                userList = userCustomMapper.selectApprByCourseId(courseId,name);
            } else {
                userList = userCustomMapper.selectSignupByCourseId(courseId,name);
            }
            return CommonResponseDto.ofSuccess(userList);
        }
        throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"未查询到课程");
    }

    /**
     * @Description:班级添加学员
     * @Date: 2018/4/29 17:07
     * @param: userIds 学员id
     * @param: classId 班级id
     * @param: courseId 课程id
     * @Author: wsc
     */
    @Override
    @Transactional
    public CommonResponseDto addCourseTeacher(Integer[] userIds, Integer classId, Integer courseId,Integer studentLength) {
        List<DrawResult> drawResults = new ArrayList <>();
        DrawResult drawResult;
        //根据班级id删除关联的学员
        drawResultMapper.deleteByClassId(classId);
        for (Integer user : userIds) {
            drawResult = new DrawResult();
            drawResult.setClassId(classId);
            drawResult.setUserId(user);
            drawResult.setState(Constants.Number.SHORT_NUMBER1);
            drawResult.setCreateTime(new Date());
            drawResult.setCourseId(courseId);
            drawResults.add(drawResult);
        }
        drawResultMapper.insertList(drawResults);
        //修改班级表中的人数
        Mclass mclass = new Mclass();
        mclass.setId(classId);
        mclass.setInitNumber(userIds.length);
        mclass.setNumber(userIds.length);
        mclassMapper.updateByPrimaryKeySelective(mclass);
        //判断评分是否已关联模板
        List<Weight> weights = weightMapper.queryWeightListByCourseId(courseId);
        if (null != weights && !weights.isEmpty()) {
            //判断是否更换了学员
            if (userIds.length != studentLength) {
                deleteCourseWeight(courseId);
            }
        }
        //删除缓存中的评分
        deleteRedisScoreMap(courseId);
        String key = Constants.COURSE_USER_INFO + courseId;
        String[] keys = redisComponent.keys( key );
        redisComponent.del( keys );
        return CommonResponseDto.ofSuccess();
    }

    //重置班级名称
    private boolean reset(List<Mclass> mclassList, List<String> strList) {
        int count = mclassList.size();
        double num =  count > 26 ? Math.ceil((double)count / 26) : 1;
        int classNum = 0;
        int b = 0;
        boolean flag = true;
        //获取26英文字母
        String[] classArray = forLetter();
        //超过26个班级后缀+1，num：集合总数/26
        for (int i = 0 ; i < num ; i ++) {
            addStrList(i,b,count,strList,classNum,classArray);
            classNum ++;
        }
        if (mclassList.size() != strList.size()) {
            flag = false;
        }
        return flag;
    }

    //班级名称插入集合
    private void addStrList(int i, int b, int count, List<String> strList, int classNum,String[] classArray) {
        for (int a = 0 ; a <= classArray.length -1; a ++,b++) {
            //得到英文字母
            if (i == 0) {
                if (b < count) {
                    strList.add(classArray[a] + "班");
                } else {
                    break;
                }
            //英文字母后缀+N
            } else {
                if (b < count) {
                    strList.add(classArray[a] + classNum + "班");
                } else {
                    break;
                }
            }
        }
    }

    //就获取26个英文字母
    private String[] forLetter() {
        String[] array = new String[26];
        int b = 0;
        for (int a = 'A'; a <= 'Z'; a++,b++) {
            array[b] = String.valueOf((char)a);
        }
        return array;
    }

    //修改课程表的抽签，评分状态，删除权重表
    private void deleteCourseWeight(Integer courseId){
        //修改课程的抽签状态
        Course course = new Course();
        course.setSysId(courseId);
        course.setIsBallot(Constants.Number.BYTE_NUMBER0);//抽签
        course.setIsScore(Constants.Number.BYTE_NUMBER0);//评分
        courseMapper.updateByPrimaryKeySelective(course);
        //删除权重
        weightMapper.deleteWeightByCourseId(courseId);
    }

    //删除缓存中评分
    private void deleteRedisScoreMap(int courseId) {
        if (redisComponent.hasKey(Constants.SITEM_CACHE_KEY + courseId)) {
            ObjectMapper objectMapper = new ObjectMapper();
            Object obj = redisService.getValue(Constants.SITEM_CACHE_KEY + courseId);
            Map<String, String> map = new HashMap<>();
            try {
                map = objectMapper.readValue(obj.toString(), new TypeReference<HashMap<String, String>>() {});
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            for(String s:map.keySet()){
                redisComponent.del(Constants.SITEM_CACHE_KEY+map.get(s));
            }
            redisComponent.del(Constants.SITEM_CACHE_KEY + courseId);
        }
    }
}
