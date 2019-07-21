/**
 * Created by dk on 2018/3/5.
 */

package com.amway.acti.service.impl;

import com.amway.acti.base.redis.RedisComponent;
import com.amway.acti.base.exception.AmwayLogicException;
import com.amway.acti.base.util.Constants;
import com.amway.acti.dao.ClassDrawMapper;
import com.amway.acti.dao.CourseMapper;
import com.amway.acti.dao.CourseTeacherMapper;
import com.amway.acti.dao.DrawResultMapper;
import com.amway.acti.dao.MclassMapper;
import com.amway.acti.dao.ScoreAnswerMapper;
import com.amway.acti.dao.ScoreResultMapper;
import com.amway.acti.dao.SitemMapper;
import com.amway.acti.dao.SitemTempMapper;
import com.amway.acti.dao.SpeeMarkMapper;
import com.amway.acti.dto.ScoreDto;
import com.amway.acti.dto.SitemDto;
import com.amway.acti.dto.SitemUserDto;
import com.amway.acti.model.ClassDraw;
import com.amway.acti.model.Course;
import com.amway.acti.model.CourseTeacher;
import com.amway.acti.model.DrawResult;
import com.amway.acti.model.Mclass;
import com.amway.acti.model.ScoreAnswer;
import com.amway.acti.model.ScoreResult;
import com.amway.acti.model.Sitem;
import com.amway.acti.model.SitemTemp;
import com.amway.acti.model.SpeeMark;
import com.amway.acti.model.User;
import com.amway.acti.service.RedisService;
import com.amway.acti.service.SitemService;
import com.amway.acti.service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SitemServiceImpl implements SitemService {

    @Autowired
    private MclassMapper mclassMapper;

    @Autowired
    private DrawResultMapper drawResultMapper;

    @Autowired
    private SitemMapper sitemMapper;

    @Autowired
    private ScoreAnswerMapper scoreAnswerMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private ScoreResultMapper scoreResultMapper;

    @Autowired
    private SpeeMarkMapper speeMarkMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private SitemTempMapper sitemTempMapper;

    @Autowired
    private CourseTeacherMapper courseTeacherMapper;

    @Autowired
    private ClassDrawMapper classDrawMapper;

    @Autowired
    private RedisComponent redisComponent;

    @Autowired
    private RedisService redisService;

    @Value("${redisSwitch.flag}")
    private boolean flag;

    /***
     * get-评分查询缓存
     * @param uid
     * @param courseId
     * @return
     */
    private List<SitemUserDto> getRedisCache(Integer uid, Integer courseId) {
        List<SitemUserDto> resultList = null;
        if (redisComponent.hasKey(Constants.SITEM_CACHE_KEY + courseId)) {
            ObjectMapper objectMapper = new ObjectMapper();
            Object obj = redisService.getValue(Constants.SITEM_CACHE_KEY + courseId);
            Map<String, String> map = new HashMap<>();
            try {
                map = objectMapper.readValue(obj.toString(), new TypeReference<HashMap<String, String>>() {
                });
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            String kv = map.get(courseId + "#" + uid);
            if (redisComponent.hasKey(Constants.SITEM_CACHE_KEY + kv)) {
                Object obj0 = redisService.getValue(Constants.SITEM_CACHE_KEY + kv);
                try {
                    resultList = objectMapper.readValue(obj0.toString(), new TypeReference<List<SitemUserDto>>() {
                    });
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return resultList;
    }

    /***
     * set-评分查询缓存
     * @param uid
     * @param courseId
     * @return
     */
    private void setRedisCache(Integer uid, Integer courseId, List<SitemUserDto> resultList) {
        Map<String, String> map = null;
        if (!redisComponent.hasKey(Constants.SITEM_CACHE_KEY + courseId)) {
            map = new HashMap<>();
            map.put(courseId + "#" + uid, courseId + "#" + uid);
        } else {
            ObjectMapper objectMapper = new ObjectMapper();
            Object obj = redisService.getValue(Constants.SITEM_CACHE_KEY + courseId);
            try {
                map = objectMapper.readValue(obj.toString(), new TypeReference<HashMap<String, String>>() {
                });
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            map.put(courseId + "#" + uid, courseId + "#" + uid);
        }
        redisService.set(Constants.SITEM_CACHE_KEY + courseId, JSONObject.fromObject(map).toString(), 3600 * 24 * 5);
        redisService.set(Constants.SITEM_CACHE_KEY + courseId + "#" + uid, JSONArray.fromObject(resultList).toString(), 3600 * 24 * 5);
    }

    /**
     * 评分项查询
     *
     * @param uid
     * @param courseId
     * @return
     */
    @Override
    public List<SitemUserDto> sitemSearch(Integer uid, Integer courseId) {
        log.info(">>>Begin sitemSearch");
        log.info("uid:{}", uid);
        log.info("courseId:{}" + courseId);

        List<SitemUserDto> resultList = new ArrayList<>();
        List<DrawResult> drawResultList = null;
        User user = userService.getUser(uid);
        log.info("user:{}", user.toString());
        if (Constants.USER_IDENT_LECTURER == user.getIdent()) {
            CourseTeacher courseTeacher = courseTeacherMapper.selectByUserAndCourse(courseId, uid);
            drawResultList = drawResultMapper.selectByClassId(courseTeacher.getClassId(), uid);
            resultList = getSitems(uid, courseId, drawResultList, resultList);
        } else if (Constants.USER_IDENT_STUDENT == user.getIdent()) {
            if (flag) {
                List<SitemUserDto> resultListCache = this.getRedisCache(uid, courseId);
                if (null != resultListCache && !resultListCache.isEmpty()) {
                    log.info("sitemSearch-getRedisCache-success");
                    return resultListCache;
                }
            }
            log.info("user.ident:{}", user.getIdent());
            List<Mclass> classList = mclassMapper.selectByCourseId(courseId);
            log.info("classList Size:{}", classList.size());
            for (Mclass mclass : classList) {
                DrawResult drawResult = new DrawResult();
                drawResult.setUserId(uid);
                drawResult.setClassId(mclass.getId());
                DrawResult dr = drawResultMapper.selectByClassIdAndUserId(drawResult);
                if (dr != null) {
                    log.info("DrawResult.classId:{}", dr.getClassId());
                    // 当前登录的学员所在班级下所有学员抽签结果信息
                    drawResultList = drawResultMapper.selectByClassId(dr.getClassId(), uid);
                    log.info("drawResultList Size:{}", drawResultList.size());
                    break;
                }
            }
            resultList = getSitems(uid, courseId, drawResultList, resultList);
            if (flag) {
                setRedisCache(uid, courseId, resultList);
            }
        }
        log.info(">>>resultList Size:{}", resultList.size());
        return resultList;
    }

    /**
     * 取評分信息
     *
     * @param uid
     * @param courseId
     * @param drawResultList
     * @param resultList
     * @return
     */
    private List<SitemUserDto> getSitems(Integer uid, Integer courseId, List<DrawResult> drawResultList, List<SitemUserDto> resultList) {
        if (drawResultList != null && !drawResultList.isEmpty()) {
            log.info("drawResultList Size:{}", drawResultList.size());
            for (DrawResult drawResult : drawResultList) {
                List<SitemDto> sitems = new ArrayList<>();
                Integer sitemTempId = courseMapper.selectSitemTempBySysId(courseId);
                List<Sitem> sitemList = sitemMapper.selectBySitemTempId(sitemTempId);
                SitemTemp sitemTemp = sitemTempMapper.selectByPrimaryKey(sitemTempId);
                for (Sitem sitem : sitemList) {
                    ScoreAnswer scoreAnswer0 = new ScoreAnswer();
                    scoreAnswer0.setCourseId(courseId);
                    scoreAnswer0.setSitemId(sitem.getId());
                    scoreAnswer0.setStuId(drawResult.getUserId());
                    scoreAnswer0.setUserId(uid);
                    ScoreAnswer scoreAnswer = scoreAnswerMapper.selectByStuIdAndSitemId(scoreAnswer0);
                    SitemDto sitemDto = new SitemDto();
                    if (scoreAnswer != null) {
                        sitemDto.setScore(scoreAnswer.getScore());
                    } else {
                        sitemDto.setScore(new BigDecimal(-1));
                    }
                    sitemDto.setId(sitem.getId());
                    sitemDto.setQuestion(sitem.getQuestion());
                    sitemDto.setOrder(sitem.getOrder());
                    sitemDto.setRate(sitem.getRate());
                    sitems.add(sitemDto);
                }
                SitemUserDto sitemUserDto = new SitemUserDto();
                sitemUserDto.setStandDesc(sitemTemp.getStand());
                sitemUserDto.setName(userService.getUser(drawResult.getUserId()).getName());
                sitemUserDto.setUserId(drawResult.getUserId());
                sitemUserDto.setSitems(sitems);
                //add wsc
                ClassDraw classDraw = classDrawMapper.selectByPrimaryKey(drawResult.getClassDrawId());
                //演讲序号wsc
                sitemUserDto.setCode(Integer.parseInt(classDraw.getCode()));
                resultList.add(sitemUserDto);
            }
        }
        if (null != resultList && !resultList.isEmpty()) {
            resultList = resultList.stream().sorted(Comparator.comparing(SitemUserDto::getCode)).collect(Collectors.toList());
        }
        return resultList;
    }

    /**
     * 评分提交
     *
     * @param uid
     * @param traineeId
     * @param scores
     */
    @Transactional
    @Override
    public void sitemSubmit(Integer uid, Integer courseId, Integer traineeId, List<ScoreDto> scores) {
        log.info(">>>Begin sitemSubmit");
        log.info("scores Size:{}", scores.size());
        Integer classId = scoreResultMapper.selectClassIdByCourseIdAndUid(courseId, traineeId);
        if (classId == null || classId <= 0) {
            return;
        }
        for (ScoreDto scoreDto : scores) {
            if (null == scoreDto.getScore()) {
                return;
            }
        }

        Course course = courseMapper.selectByPrimaryKey(courseId);
        validateCourse(course);
        log.info("course:{}", course.toString());
        BigDecimal score = new BigDecimal(0);
        for (ScoreDto scoreDto : scores) {
            ScoreAnswer scoreAnswer = new ScoreAnswer();
            scoreAnswer.setCourseId(courseId);
            scoreAnswer.setSitemId(scoreDto.getSitemId());
            scoreAnswer.setStuId(traineeId);
            scoreAnswer.setUserId(uid);
            scoreAnswer.setScore(scoreDto.getScore());
            scoreAnswer.setState((short) 1);
            ScoreAnswer sa = scoreAnswerMapper.selectByStuIdAndSitemId(scoreAnswer);
            // 记录到评分答案表
            if (sa == null) {
                // 小程序传来的是60，而我需要的是0.6，所有需要除以100
                BigDecimal te = scoreDto.getScore().multiply((scoreDto.getRate()).divide(BigDecimal.valueOf(100)));
                score = score.add(te);
                int result = scoreAnswerMapper.insertSelective(scoreAnswer);
                log.info("thread:" + result);
            }
        }
        ScoreResult scoreResult = new ScoreResult();
        scoreResult.setCourseId(courseId);
        scoreResult.setScore(score);
        scoreResult.setStuId(traineeId);
        scoreResult.setUserId(uid);
        ScoreResult sr = scoreResultMapper.selectByCourseIdAndStuIdAndUid(scoreResult);
        // 记录到评分结果表
        if (sr == null) {
            scoreResultMapper.insertSelective(scoreResult);
        }
        // 记录到演讲成绩表
        BigDecimal totalScore = scoreResultMapper.selectTotalScoreByStuId(courseId, traineeId);
        log.info("totalScore{}", totalScore);
        SpeeMark speeMark = new SpeeMark();
        speeMark.setClassId(classId);
        speeMark.setCourseId(courseId);
        speeMark.setScore(totalScore);
        speeMark.setUserId(traineeId);
        speeMark.setState((short) 1);
        SpeeMark speeMark0 = speeMarkMapper.selectByCourseIdAndClassIdAndUid(speeMark);
        if (speeMark0 == null) {
            speeMark.setCreateTime(new Date());
            speeMarkMapper.insertSelective(speeMark);
        } else {
            speeMark0.setScore(totalScore);
            speeMarkMapper.updateByPrimaryKey(speeMark0);
        }
        User user = userService.getUser(uid);
        if (flag && null != user && 1 == user.getIdent()) {
            updateRedisCache(uid, courseId, traineeId, scores);
        }
        log.info(">>>>End sitemSubmit");
    }


    /**
     * 更新缓存
     *
     * @param uid
     * @param courseId
     * @param traineeId
     * @param scores
     */
    private void updateRedisCache(Integer uid, Integer courseId, Integer traineeId, List<ScoreDto> scores) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Object obj0 = redisService.getValue(Constants.SITEM_CACHE_KEY + courseId + "#" + uid);
            List<SitemUserDto> resultList = objectMapper.readValue(obj0.toString(), new TypeReference<List<SitemUserDto>>() {
            });
            if (null != resultList && resultList.size() > 0) {
                for (SitemUserDto sitemUserDto : resultList) {
                    for (ScoreDto scoreDto : scores) {
                        if (sitemUserDto.getUserId().equals(traineeId)) {
                            for (SitemDto sitemDto : sitemUserDto.getSitems()) {
                                if (scoreDto.getSitemId().equals(sitemDto.getId())) {
                                    sitemDto.setScore(scoreDto.getScore());
                                }
                            }
                        }
                    }
                }
                redisService.set(Constants.SITEM_CACHE_KEY + courseId + "#" + uid, JSONArray.fromObject(resultList).toString(), 3600 * 24 * 5);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    /**
     * 验证课程信息
     *
     * @param course
     */
    private void validateCourse(Course course) {
        if (course == null) {
            throw new AmwayLogicException(Constants.ErrorCode.COURSE_NOT_EXIST, "课程不存在");
        }
        if (!(new Date().after(course.getStartTime())
            && new Date().before(course.getEndTime()))) {
            throw new AmwayLogicException(Constants.ErrorCode.ERROR_TIME_RANGE, "无效的课程时间!");
        }
        if (0 == course.getIsShelve()) {
            throw new AmwayLogicException(Constants.ErrorCode.COURSE_NOT_EXIST, "课程已下架!");
        }
    }
}
