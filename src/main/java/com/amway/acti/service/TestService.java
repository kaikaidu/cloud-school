package com.amway.acti.service;

import com.amway.acti.base.exception.AmwayLogicException;
import com.amway.acti.dto.test.TestResultDto;
import com.amway.acti.model.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Wei.Li1
 * @create 2018-03-05 11:27
 **/
@Validated
public interface TestService {

    ///////////////前端小程序接口////////////////

    /**
     * 校验用户是否存在并有效
     * @param user
     * @throws AmwayLogicException
     */
    void checkUser(@NotNull(message = "用户不能为空") User user);

    /**
     * 校验课程是否存在并有效
     * @param course
     * @throws AmwayLogicException
     */
    void checkCourse(@NotNull(message = "课程不能为空") Course course);

    /**
     * 校验用户和课程之间是不是正确关系
     * @param userId
     * @param courseId
     * @throws AmwayLogicException
     */
    void checkUserAndCourse(@NotNull(message = "用户ID不能为空") Integer userId, @NotNull(message = "课程ID不能为空") Integer courseId);

    /**
     * 校验课程与测试卷之间的关系
     * @param courseId
     * @param paperId
     */
    void checkCourseAndPaper(@NotNull(message = "课程ID不能为空") Integer courseId, @NotNull(message = "测试卷ID不能为空") Integer paperId);

    /**
     * 根据课程ID获取试卷分页列表
     * @param courseId
     * @return
     */
    List<TestPaper> getPaperList(@NotNull(message = "课程ID不能为空") Integer courseId);

    /**
     * 获取学员成绩
     * @param userId
     * @param paperId
     * @return
     */
    UserMark getUserMark(@NotNull(message = "用户ID不能为空") Integer userId, @NotNull(message = "试卷ID不能为空") Integer paperId);

    /**
     * 设置学员测试答案
     * @param userAnswers
     * @return
     */
    void setUserAnswer(@NotNull(message = "学员测试答案对象不能为空") List<UserAnswer> userAnswers);

    /**
     * 录入学员成绩
     * @param userId
     * @param paperId
     * @param timeLength
     * @param userAnswers
     * @return
     */
    void setUserMark(@NotNull(message = "用户ID不能为空") Integer userId,
                     @NotNull(message = "试卷ID不能为空") Integer paperId,
                     @NotNull(message = "答卷时间不能为空") String timeLength,
                     @NotNull(message = "答案不能是空") List<UserAnswer> userAnswers);

    /**
     * 根据试卷ID获取试卷信息
     * @param id
     * @return
     */
    TestPaper getTestPaperById(@NotNull(message = "试卷ID不能为空") Integer id);

    /**
     * 根据测试模板ID获取测试模板详情
     * @param id
     * @return
     */
    TestTemp getTestTempById(@NotNull(message = "测试模板ID不能为空") Integer id);


    /**
     * 根据试题ID获取试题详情
     * @param id
     * @return
     */
    TestQuest getTestQuestById(@NotNull(message = "试题ID不能为空") Integer id);

    /**
     * 根据试卷ID获取试题列表
     * @param paperId
     * @return
     */
    List<TestQuest> getQuestList(@NotNull(message = "试卷ID不能为空") Integer paperId);

    /**
     * 根据试题ID查询所有的选项
     * @param questId
     * @return
     */
    List<TestOption> getOptionList(@NotNull(message = "试题ID不能为空") Integer questId);

    /**
     * 根据tempId获取试卷列表
     * @param tempId
     * @return
     */
    List<TestPaper> getPaperListByTempId(@NotNull(message = "模板ID不能为空") Integer tempId);























    /**
     * 根据用户ID和试卷ID获取所有答案
     *
     * @param userId
     * @param paperId
     * @return
     */
    List<UserAnswer> getUserAnswerByUserIdAndPaperId(@NotNull(message = "用户ID不能为空") Integer userId, @NotNull(message = "试卷ID不能为空") Integer paperId);

    /**
     * 用户提交测试卷，并得出最终成绩
     * @param userId
     * @param testResultDto
     */
    void testSubmit(@NotNull(message = "用户ID不能为空") Integer userId, @Valid TestResultDto testResultDto);

    /**
     * 校验试题与模板之间的关系
     * @param tempId
     * @param questId
     */
    void checkTempAndQuest(@NotNull(message = "模板ID不能为空") Integer tempId, @NotNull(message = "试题ID不能为空") Integer questId);

    /**
     * 校验试题与试卷之间的关系
     * @param paperId
     * @param questId
     */
    void checkPaperAndQuest(@NotNull(message = "试卷ID不能为空") Integer paperId, @NotNull(message = "试题ID不能为空") Integer questId);
}
