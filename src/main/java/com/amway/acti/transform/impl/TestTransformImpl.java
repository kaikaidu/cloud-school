package com.amway.acti.transform.impl;

import com.amway.acti.base.util.Constants;
import com.amway.acti.base.util.DateUtil;
import com.amway.acti.base.util.ScoreUtil;
import com.amway.acti.dto.PageDto;
import com.amway.acti.dto.test.*;
import com.amway.acti.model.*;
import com.amway.acti.service.TestService;
import com.amway.acti.transform.TestTransform;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Wei.Li1
 * @create 2018-03-05 14:07
 **/
@Slf4j
@Component
public class TestTransformImpl implements TestTransform {

    @Autowired
    private TestService testService;

    @Override
    public TestPaperDto transformToTestPaperDto(TestPaper testPaper, Integer userId){
        TestPaperDto testPaperDto = new TestPaperDto();
        testPaperDto.setId( testPaper.getId() );
        testPaperDto.setName( testPaper.getName() );
        // 设置是否完成
        UserMark userMark = testService.getUserMark( userId, testPaper.getId() );
        testPaperDto.setIsSubmit( userMark == null ? 0 : 1 );
        return testPaperDto;
    }

    @Override
    public List <TestPaperDto> transformToTestPaperDtos(List <TestPaper> list, Integer userId) {
        List <TestPaperDto> testPaperDtos = new ArrayList <>();
        for (TestPaper testPaper : list) {
            TestPaperDto testPaperDto = transformToTestPaperDto(testPaper, userId);
            testPaperDtos.add( testPaperDto );
        }
        return testPaperDtos;
    }

    @Override
    public StuMarkDto transformUserMarkToStuMarkDto(UserMark userMark) {
        StuMarkDto stuMarkDto = new StuMarkDto();
        stuMarkDto.setScore( userMark.getScore().doubleValue() );
        Integer paperId = userMark.getPaperId();
        TestPaper testPaper = testService.getTestPaperById( paperId );
        if (testPaper == null) {
            return null;
        }
        TestTemp testTemp = testService.getTestTempById( testPaper.getTempId() );
        stuMarkDto.setTotalScore( testTemp.getTotalScore().doubleValue() );
        stuMarkDto.setTimeLength( userMark.getTimeLength() );
        return stuMarkDto;
    }

    @Override
    public PaperResultDto transformToPaperResultDto(Integer userId, Integer paperId, List <TestQuest> pageInfo) {
        PaperResultDto paperResultDto = new PaperResultDto();
        UserMark userMark = testService.getUserMark( userId, paperId );
        paperResultDto.setPaperInfo( transformToPaperInfoDto( paperId, pageInfo ) );
        if (userMark == null) {
            paperResultDto.setTo( Constants.PaperResultStatus.TODO );
        } else {
            paperResultDto.setTo( Constants.PaperResultStatus.DONE );
            paperResultDto.setStuMark( transformUserMarkToStuMarkDto( userMark ) );
        }
        return paperResultDto;
    }

    @Override
    public PaperInfoDto transformToPaperInfoDto(Integer paperId, List <TestQuest> pageInfo) {
        TestPaper testPaper = testService.getTestPaperById( paperId );
        PaperInfoDto paperInfoDto = new PaperInfoDto();
        paperInfoDto.setName( testPaper.getName() );
        paperInfoDto.setDesc( testPaper.getDescribe() );
        paperInfoDto.setQuestList( transformPageInfoToQuestListDto( pageInfo ) );
        return paperInfoDto;
    }

    @Override
    public List <QuestListDto> transformPageInfoToQuestListDto(List <TestQuest> pageInfo) {
        List <QuestListDto> testPaperDtos = new ArrayList <>();
        for (TestQuest testQuest : pageInfo) {
            testPaperDtos.add( transformToQuestListDto( testQuest ) );
        }
        return testPaperDtos;
    }

    @Override
    public QuestListDto transformToQuestListDto(TestQuest testQuest) {
        QuestListDto questListDto = new QuestListDto();
        questListDto.setId( testQuest.getId() );
        questListDto.setContent( testQuest.getQuestion() );
        questListDto.setOrder( testQuest.getSequence() );
        questListDto.setType( testQuest.getType() );
        // 将选项转为list
        List<TestOption> testOptions = testService.getOptionList( testQuest.getId() );
        questListDto.setQuestItem( transformToQuestItemListDtos( testOptions ) );
        return questListDto;
    }

    @Override
    public QuestItemListDto transformToQuestItemListDto(TestOption testOption) {
        QuestItemListDto questItemListDto = new QuestItemListDto();
        questItemListDto.setItem( testOption.getContent() );
        questItemListDto.setSequence( testOption.getSequence() );
        return questItemListDto;
    }

    @Override
    public List <QuestItemListDto> transformToQuestItemListDtos(List <TestOption> testOptions) {
        List <QuestItemListDto> list = new ArrayList <>();
        for (TestOption item : testOptions) {
            list.add( transformToQuestItemListDto( item ) );
        }
        return list;
    }

    @Override
    public UserAnswer transformToUserAnswer(AnswerResultDto answerResultDto, Integer userId, Integer paperId) {
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setUserId( userId );
        userAnswer.setPaperId( paperId );
        userAnswer.setAnswer( answerResultDto.getAnswer() );
        int questId = answerResultDto.getQuestId();
        userAnswer.setQuestId( questId );
        testService.checkPaperAndQuest(paperId, questId);
        TestQuest testQuest = testService.getTestQuestById( answerResultDto.getQuestId() );
        List<TestOption> testOptions = testService.getOptionList( testQuest.getId() );
        List<String> chars = new ArrayList <>();
        for(TestOption testOption : testOptions){
            if(testOption.getIsTrue() == 1){
                chars.add( testOption.getSequence() );
            }
        }
        chars.sort( (String s1, String s2) -> s1.compareTo(s2) );

        StringBuilder sb = new StringBuilder();
        for(String ch : chars){
            sb.append( ch ).append( "," );
        }
        String answer = userAnswer.getAnswer();
        if(!answer.endsWith( "," )){
            answer += ",";
        }
        if(StringUtils.equals( answer, sb.toString() )){
            userAnswer.setScore( testQuest.getScore() );
        }else{
            userAnswer.setScore( BigDecimal.valueOf( 0 ) );
        }
        userAnswer.setCreateTime(new Date());//add wsc
        return userAnswer;
    }

    @Override
    public List <UserAnswer> transformToUserAnswers(List <AnswerResultDto> answerResultDtos, Integer userId, Integer paperId) {
        List<UserAnswer> list = new ArrayList <>();
        for(AnswerResultDto answerResultDto : answerResultDtos){
            list.add( transformToUserAnswer( answerResultDto, userId, paperId ) );
        }
        return list;
    }

    @Override
    public PageDto <TestTempDto> transformInfoToTestTempDto(PageInfo <TestTemp> pageInfo) {
        PageDto <TestTempDto> pageDto = new PageDto <>();
        pageDto.setTotalPages( pageInfo.getPages() );
        pageDto.setCurrentPage( pageInfo.getPageNum() );

        List <TestTempDto> testTempDtos = new ArrayList <>();
        for (TestTemp testTemp : pageInfo.getList()) {
            TestTempDto testTempDto = new TestTempDto();
            testTempDto.setId( testTemp.getId() );
            testTempDto.setName( testTemp.getName() );
            testTempDto.setCreateTime( DateUtil.format( testTemp.getCreateTime(), DateUtil.YYYY_MM_DD ) );
            List<TestPaper> paperList = testService.getPaperListByTempId( testTemp.getId() );
            if(paperList == null || paperList.isEmpty()){
                testTempDto.setIsUsed( 0 );
            }else{
                testTempDto.setIsUsed( 1 );
            }
            testTempDtos.add( testTempDto );
        }
        pageDto.setDataList( testTempDtos );
        return pageDto;
    }

    @Override
    public JSONObject transformToViewJson(TestTemp testTemp, List<TestQuest> testQuests) {
        JSONObject jobj = new JSONObject();
        if(testTemp == null){
            return jobj;
        }
        jobj.put( "id", testTemp.getId() );
        jobj.put( "name", testTemp.getName() );
        jobj.put( "totalScore", testTemp.getTotalScore() );
        JSONArray jarr = new JSONArray();
        if(testQuests == null){
            return jobj;
        }
        for(TestQuest testQuest : testQuests){
            JSONObject jj = new JSONObject();
            jj.put( "type", testQuest.getType() );
            jj.put( "question", testQuest.getQuestion() );
            // 解析选项
            List<TestOption> testOptions = testService.getOptionList( testQuest.getId() );
            JSONArray ja = JSONArray.fromObject( testOptions );
            // 计算正确答案
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i<ja.size(); i++){
                JSONObject jo = ja.getJSONObject(i);
                jo.put( "type", testQuest.getType() );
                if(jo.getInt( "isTrue" ) == 1){
                    sb.append( jo.getString( "sequence" ) ).append( "," );
                }
            }
            jj.put( "trueAnswer", sb.length() == 0 ? null : sb.substring( 0, sb.length() - 1 ) );
            jj.put( "options", ja );
            String score = testQuest.getScore().toString();
            jj.put( "score", ScoreUtil.format( score ) );
            jj.put( "sequence", testQuest.getSequence() );
            jarr.add( jj );
        }
        jobj.put( "quests", jarr );
        return jobj;
    }

    @Override
    public JSONObject transformToPaperViewJson(TestPaper testPaper, TestTemp testTemp, List <TestQuest> testQuests) {
        JSONObject jobj = new JSONObject();
        if(testPaper == null){
            return jobj;
        }
        jobj.put( "name", testPaper.getName() );
        JSONObject tempJson = transformToViewJson( testTemp, testQuests );
        jobj.put( "temp", tempJson );
        return jobj;
    }

}
