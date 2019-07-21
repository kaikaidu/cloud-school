package com.amway.acti.transform;

import com.amway.acti.dto.PageDto;
import com.amway.acti.dto.test.*;
import com.amway.acti.model.*;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import net.sf.json.JSONObject;
import org.aspectj.weaver.ast.Test;

import java.util.List;

/**
 * @author Wei.Li1
 * @create 2018-03-05 14:06
 **/
public interface TestTransform {

    /**
     * 转换得到TestPaperDto
     *
     * @param testPaper
     * @param userId
     * @return
     */
    TestPaperDto transformToTestPaperDto(TestPaper testPaper, Integer userId);

    /**
     * 转换得到List<TestPaperDto>
     *
     * @param list
     * @param userId
     * @return
     */
    List<TestPaperDto> transformToTestPaperDtos(List<TestPaper> list, Integer userId);

    /**
     * 转换得到StuMarkDto
     *
     * @param userMark
     * @return
     */
    StuMarkDto transformUserMarkToStuMarkDto(UserMark userMark);

    /**
     * 转换得到PaperResultDto
     *
     * @param userId
     * @param paperId
     * @param pageInfo
     * @return
     */
    PaperResultDto transformToPaperResultDto(Integer userId, Integer paperId, List<TestQuest> pageInfo);

    /**
     * 转换得到PaperInfoDto
     *
     * @param paperId
     * @param pageInfo
     * @return
     */
    PaperInfoDto transformToPaperInfoDto(Integer paperId, List<TestQuest> pageInfo);

    /**
     * 转换得到PageDto<QuestListDto>
     *
     * @param pageInfo
     * @return
     */
    List<QuestListDto> transformPageInfoToQuestListDto(List<TestQuest> pageInfo);

    /**
     * 转换得到QuestListDto
     *
     * @param testQuest
     * @return
     */
    QuestListDto transformToQuestListDto(TestQuest testQuest);

    /**
     * 转换得到QuestItemListDto
     * @param testOption
     * @return
     */
    QuestItemListDto transformToQuestItemListDto(TestOption testOption);

    /**
     * 转换得到List<QuestItemListDto>
     * @param testOptions
     * @return
     */
    List<QuestItemListDto> transformToQuestItemListDtos(List<TestOption> testOptions);

    /**
     * 转换得到UserAnswer
     *
     * @param answerResultDto
     * @param userId
     * @param paperId
     * @return
     */
    UserAnswer transformToUserAnswer(AnswerResultDto answerResultDto, Integer userId, Integer paperId);

    /**
     * 转换得到List<UserAnswer>
     *
     * @param answerResultDtos
     * @param userId
     * @param paperId
     * @return
     */
    List<UserAnswer> transformToUserAnswers(List<AnswerResultDto> answerResultDtos, Integer userId, Integer paperId);

    ///////////////////李伟后台测试相关////////////////////

    /**
     * 转换得到PageDto<TestTempDto>
     *
     * @param pageInfo
     * @return
     */
    PageDto<TestTempDto> transformInfoToTestTempDto(PageInfo<TestTemp> pageInfo);

    /**
     * 转换得到预览模板对象（包含试题）
     * @param testTemp
     * @param testQuests
     * @return
     */
    JSONObject transformToViewJson(TestTemp testTemp, List<TestQuest> testQuests);

    /**
     * 转换得到预览测试卷对象（包含试题）
     * @param testTemp
     * @param testQuests
     * @return
     */
    JSONObject transformToPaperViewJson(TestPaper testPaper, TestTemp testTemp, List<TestQuest> testQuests);


}
