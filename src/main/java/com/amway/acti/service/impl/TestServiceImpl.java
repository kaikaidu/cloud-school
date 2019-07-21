package com.amway.acti.service.impl;

import com.amway.acti.base.exception.AmwayLogicException;
import com.amway.acti.base.exception.AmwaySystemException;
import com.amway.acti.dao.*;
import com.amway.acti.dto.test.AnswerResultDto;
import com.amway.acti.dto.test.TestResultDto;
import com.amway.acti.model.*;
import com.amway.acti.service.CourseService;
import com.amway.acti.service.TestService;
import com.amway.acti.service.UserService;
import com.amway.acti.transform.TestTransform;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author Wei.Li1
 * @create 2018-03-05 11:30
 **/
@Slf4j
@Service
public class TestServiceImpl implements TestService{

    @Autowired
    private TestPaperMapper testPaperMapper;

    @Autowired
    private TestQuestMapper testQuestMapper;

    @Autowired
    private TestOptionMapper testOptionMapper;

    @Autowired
    private UserAnswerMapper userAnswerMapper;

    @Autowired
    private UserMarkMapper userMarkMapper;

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    @Autowired
    private TestTempMapper testTempMapper;

    @Autowired
    private TestTransform testTransform;

    @Override
    public void checkUser(User user){
        if(user == null || user.getState() == 0){
            throw new AmwayLogicException( "0001", "用户不存在" );
        }
    }

    @Override
    public void checkCourse(Course course){
        if(course == null || course.getState() == 0){
            throw new AmwayLogicException( "0002", "课程不存在" );
        }
    }

    @Override
    public void checkUserAndCourse(Integer userId, Integer courseId){
        User user = userService.getUser( userId );
        checkUser( user );
        Course course = courseService.selectCourseById( courseId );
        checkCourse( course );
        // label 必修 1、线下 2、直播 3   必修和线下不要审核   直播看isVerify，1：需要 0：不需要e
        int flag = 0;
        int label = course.getLabel();
        int isVerify = course.getIsVerify();
        if(label == 3 && isVerify == 1){
            flag = 1;
        }
        User userTemp;
        if(flag == 1){
            userTemp = userService.findWithApproed( userId, courseId );
        }else{
            userTemp = userService.findWithNotApproed( userId, courseId );
        }
        if(userTemp == null){
            throw new AmwayLogicException( "用户和课程的关系不匹配" );
        }
    }

    @Override
    public void checkCourseAndPaper(Integer courseId, Integer paperId) {
        TestPaper testPaper = testPaperMapper.selectById( paperId );
        if(testPaper == null || testPaper.getCourseId().intValue() != courseId.intValue()){
            throw new AmwaySystemException( "课程和试卷的关系不匹配" );
        }
    }

    @Override
    public TestPaper getTestPaperById(Integer id) {
        return testPaperMapper.selectById(id);
    }

    @Override
    public TestTemp getTestTempById(Integer id) {
        return testTempMapper.selectById( id );
    }

    @Override
    public List<TestPaper> getPaperList(Integer courseId) {
        log.info("courseId:{}", courseId);
        return testPaperMapper.selectOnByCourseId(courseId);
    }

    @Override
    public UserMark getUserMark(Integer userId, Integer paperId) {
        log.info("userId:{}, paperId:{}", userId, paperId);
        return userMarkMapper.selectByUserIdAndPaperId(userId, paperId);
    }

    @Override
    public void setUserAnswer(List<UserAnswer> userAnswers) {
        for(UserAnswer userAnswer : userAnswers) {
            UserAnswer ua;
            try {
                ua = userAnswerMapper.selectByUserIdAndPaperIdAndQuestId( userAnswer.getUserId(), userAnswer.getPaperId(), userAnswer.getQuestId() );
            } catch (MyBatisSystemException e) {
                log.error( e.getMessage() );
                throw new AmwaySystemException( "用户答案重复" );
            }
            if (ua != null) {
                throw new AmwaySystemException( "用户已提交过答案  UserAnswer == " + ua );
            }
            checkPaperAndQuest(userAnswer.getPaperId(), userAnswer.getQuestId());
        }
        userAnswerMapper.batchInsert(userAnswers);
    }

    @Override
    public void setUserMark(Integer userId, Integer paperId, String timeLength, List<UserAnswer> userAnswers) {
        UserMark userMark = new UserMark();
        userMark.setPaperId( paperId );
        userMark.setTimeLength( timeLength );
        userMark.setUserId( userId );
        userMark.setState( new Short( "1" ) );

        BigDecimal total = new BigDecimal( 0 );
        for(UserAnswer userAnswer : userAnswers){
            total = total.add( userAnswer.getScore() );
        }
        userMark.setScore( total );
        UserMark um;
        try {
            um = userMarkMapper.selectByUserIdAndPaperId( userMark.getUserId(), userMark.getPaperId() );
        } catch (MyBatisSystemException e){
            log.error( e.getMessage() );
            throw new AmwaySystemException( "该用户已经提交过问卷" );
        }
        if(um != null){
            throw new AmwaySystemException( "该用户已经提交过问卷" );
        }
        userMark.setCreateTime(new Date());//add wsc
        userMarkMapper.insert(userMark);
    }

    @Override
    public TestQuest getTestQuestById(Integer id) {
        log.info("id:{}", id);
        return testQuestMapper.selectById(id);
    }

    @Override
    public List<TestQuest> getQuestList(Integer paperId) {
        log.info("paperId:{}", paperId);
        // 根据试卷ID求出模板ID
        TestPaper testPaper = testPaperMapper.selectById( paperId );
        if(testPaper == null){
            throw new AmwaySystemException( "不存在这个试卷" );
        }
        return testQuestMapper.selectByTempId( testPaper.getTempId() );
    }

    @Override
    public List<TestOption> getOptionList(Integer questId){
        return testOptionMapper.selectByQuestId( questId );
    }

    @Override
    public List<TestPaper> getPaperListByTempId(Integer tempId){
        return testPaperMapper.selectByTempId( tempId );
    }

















    @Override
    public List <UserAnswer> getUserAnswerByUserIdAndPaperId(Integer userId, Integer paperId) {
        return userAnswerMapper.selectByUserIdAndPaperId( userId, paperId );
    }

    @Transactional
    @Override
    public void testSubmit(Integer userId, TestResultDto testResultDto) {
        int paperId = testResultDto.getPaperId();
        String timeLength = testResultDto.getTimeLength();
        List<AnswerResultDto> answerResultDtos = testResultDto.getAnswerResult();
        List<UserAnswer> userAnswers = testTransform.transformToUserAnswers( answerResultDtos, userId, paperId );
        setUserAnswer( userAnswers );
        // 获取最终得分
        setUserMark( userId, paperId, timeLength, userAnswers );
    }

    @Override
    public void checkTempAndQuest(Integer tempId, Integer questId) {
        TestQuest testQuest;
        try {
            testQuest = testQuestMapper.selectById( questId );
        } catch (MyBatisSystemException e){
            log.error( e.getMessage() );
            throw new AmwaySystemException( "查询试题失败" );
        }
        if(testQuest == null){
            throw new AmwaySystemException( "试题与模板不匹配" );
        }
    }

    @Override
    public void checkPaperAndQuest(Integer paperId, Integer questId) {
        TestPaper testPaper = null;
        int tempId = 0;
        try {
            testPaper = testPaperMapper.selectById( paperId );
            tempId = testPaper.getTempId();
        } catch (MyBatisSystemException e){
            log.error( e.getMessage() );
            throw new AmwaySystemException( "查询试卷失败" );
        }
        checkTempAndQuest(tempId, questId);
    }




}
