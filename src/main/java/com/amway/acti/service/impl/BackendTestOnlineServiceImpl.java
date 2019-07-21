package com.amway.acti.service.impl;

import com.amway.acti.base.redis.RedisComponent;
import com.amway.acti.base.exception.AmwayLogicException;
import com.amway.acti.base.util.Constants;
import com.amway.acti.dao.*;
import com.amway.acti.model.*;
import com.amway.acti.service.BackendTestOnlineService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author Wei.Li1
 * @create 2018-03-16 14:07
 **/
@Slf4j
@Service
public class BackendTestOnlineServiceImpl implements BackendTestOnlineService {

    @Autowired
    private TestPaperMapper testPaperMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserMarkMapper userMarkMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private TestQuestMapper testQuestMapper;

    @Autowired
    private TestOptionMapper testOptionMapper;

    @Autowired
    private UserAnswerMapper userAnswerMapper;

    @Autowired
    private RedisComponent redisComponent;

    private static final String SCORE = "score";

    private static final String ANSWER_TYPE = "answerType";

    private static final String GET_SCORE = "getScore";

    private static final String COURSE_ID = "courseId";


    @Override
    public PageInfo<TestPaper> findByUser(User user, Integer courseId, String search, int state, int pageNo, int pageSize) {
        search = StringUtils.isBlank( search ) ? null : "%"+search+"%";
        pageNo = pageNo == 0 ? Constants.TestOnline.PAGE_NO_DEFAULT : pageNo;
        pageSize = pageSize == 0 ? Constants.TestOnline.PAGE_SIZE_DEFAULT : pageSize;
        PageHelper.startPage(pageNo, pageSize);
        List<TestPaper> list = testPaperMapper.selectAll( courseId, search, state );
        /*int ident = user.getIdent().intValue();

        switch (ident){
            case Constants.USER_IDENT_ADMIN:
                list =
                break;
            case Constants.USER_IDENT_ASSISTANT:
                list = testPaperMapper.selectByUserId( user.getId(), courseId, search, state );
                break;
            default:
                list = null;
                break;
        }*/
        return new PageInfo <>( list );
    }

    @Override
    public boolean hasTestPaper(User user, Integer courseId) {
        int count = count( user, courseId, null, 0 );
        return count != 0;
    }

    @Override
    public int count(User user, Integer courseId, String search, int state) {
        search = StringUtils.isBlank( search ) ? null : "%"+search+"%";
        int  ident = user.getIdent().intValue();
        List<TestPaper> list = testPaperMapper.selectAll( courseId, search, state );
        /*switch (ident){
            case Constants.USER_IDENT_ADMIN:
                list = testPaperMapper.selectAll( courseId, search, state );
                break;
            case Constants.USER_IDENT_ASSISTANT:
                list = testPaperMapper.selectByUserId( user.getId(), courseId, search, state );
                break;
            default:
                list = null;
                break;
        }*/
        return list== null ? 0 : list.size();
    }

    @Transactional
    @Override
    public void removePaper(Integer id, int state, int courseId) {
        testPaperMapper.updateStateById(id, state );
        updateCourse( courseId );
    }

    @Transactional
    @Override
    public void removePapers(List<Integer> ids) {
        if(ids != null && !ids.isEmpty()){
            for(int id : ids){
                testPaperMapper.updateStateById(id, Constants.TestOnline.DELETE );
            }
        }
    }

    @Transactional
    @Override
    public void removePapersByCourseIds(List<Integer> courseIds){
        if(courseIds == null || courseIds.isEmpty()){
            return;
        }
        for(int courseId : courseIds){
            List<TestPaper> testPapers = testPaperMapper.selectAllByCourseId( courseId );
            if(testPapers == null || testPapers.isEmpty()){
                return;
            }
            for(TestPaper testPaper : testPapers){
                testPaperMapper.updateStateById( testPaper.getId(), Constants.TestOnline.DELETE );
            }
        }
    }

    @Transactional
    @Override
    public void addPaper(User user, TestPaper testPaper) {
        testPaper.setUserId( user.getId() );
        testPaper.setState( new Short( String.valueOf( Constants.TestOnline.NOT_ONSALE ) ) );
        testPaper.setCreateTime( new Date( System.currentTimeMillis() ) );
        checkName(testPaper.getCourseId(), testPaper.getName());
        testPaperMapper.insert( testPaper );
    }

    private void checkName(int courseId, String name){
        int count = testPaperMapper.countByCourseIdAndName( courseId, name );
        if(count != 0){
            throw new AmwayLogicException( "same name" );
        }
    }

    private void updateCourse(int courseId){
        List<TestPaper> list = testPaperMapper.selectOnByCourseId( courseId );
        int count = 0;
        if(list != null){
            count = list.size();
        }
        if(count == 0){
            courseMapper.updateCourseActivity("is_test", 0, courseId );
        }else{
            courseMapper.updateCourseActivity("is_test", 1, courseId );
        }
    }


    @Override
    public TestPaper findById(Integer id) {
        return testPaperMapper.selectById( id );
    }

    @Transactional
    @Override
    public void editPaper(User user, TestPaper testPaper) {
        int answerCount = userAnswerMapper.countUserAnswerByPaperId( testPaper.getId() );
        if(answerCount > 0){
            throw new AmwayLogicException( "already have answer" );
        }
        testPaper.setCreateTime( new Date( System.currentTimeMillis() ) );
        TestPaper tp = findById( testPaper.getId() );
        if(!StringUtils.equals( tp.getName(), testPaper.getName() )){
            checkName(testPaper.getCourseId(), testPaper.getName());
        }
        testPaperMapper.update( testPaper );
    }

    @Override
    public void onsale(String ids, Integer state, Integer courseId ) {
        if(ids == null){
            return;
        }
        String[] idArray = ids.split( "," );
        for(String id : idArray){
            testPaperMapper.updateStateById( Integer.valueOf( id ), state );
        }
        updateCourse( courseId );
    }

    @Override
    public List<TestPaper> findOnByCourseId(Integer courseId){
        return testPaperMapper.selectOnByCourseId( courseId );
    }

    @Override
    public List<User> findUsersByCourseId(Integer courseId){
        Course course = courseMapper.selectByPrimaryKey( courseId );
        // label 必修 1、线下 2、直播 3   必修和线下不要审核   直播看isVerify，1：需要 0：不需要e
        int flag = 0;
        int label = course.getLabel();
        int isVerify = course.getIsVerify();
        if(label == 3 && isVerify == 1){
            flag = 1;
        }
        List<User> userList;
        if(flag == 1){
            userList = userMapper.selectUsersWithApproed( courseId );
        }else{
            userList = userMapper.selectUsersWithNotApproed( courseId );
        }
        return userList;
    }

    @Override
    public UserMark findUserMarkByUserIdAndPaperId(Integer userId, Integer paperId){
        return userMarkMapper.selectByUserIdAndPaperId( userId, paperId );
    }

    @Override
    public List<Map<String, Object>> getScoreResultByInfo(Map<String, Object> info, int pageNo, int pageSize){
        boolean withApproed = withApproed( Integer.parseInt( info.get( COURSE_ID ).toString() )  );
        List<Map<String, Object>> resultList;
        pageNo = pageNo == 0 ? Constants.TestOnline.PAGE_NO_DEFAULT : pageNo;
        pageSize = pageSize == 0 ? Constants.TestOnline.PAGE_SIZE_DEFAULT : pageSize;
        PageHelper.startPage(pageNo, pageSize);
        if(withApproed){
            resultList = testPaperMapper.selectScoreResultByInfoWithApproed( info );
        }else{
            resultList = testPaperMapper.selectScoreResultByInfoWithNotApproed( info );
        }
        return resultList;
    }

    @Override
    public int countScoreResultByInfo(Map<String, Object> info){
        boolean withApproed = withApproed( Integer.parseInt( info.get( COURSE_ID ).toString() )  );
        List<Map<String, Object>> resultList;
        if(withApproed){
            resultList = testPaperMapper.selectScoreResultByInfoWithApproed( info );
        }else{
            resultList = testPaperMapper.selectScoreResultByInfoWithNotApproed( info );
        }
        return resultList == null ? 0 : resultList.size();
    }

    private boolean withApproed(int courseId){
        Course course = courseMapper.selectByPrimaryKey( courseId );
        // label 必修 1、线下 2、直播 3   必修和线下不要审核   直播看isVerify，1：需要 0：不需要e
        int flag = 0;
        int label = course.getLabel();
        int isVerify = course.getIsVerify();
        if(label == 3 && isVerify == 1){
            flag = 1;
        }
        return flag == 1;
    }

    @Override
    public List<Map<String, Object>> getScoreResult(int courseId){
        List<Map<String, Object>> result = new ArrayList<>();
        // 根据courseId查询出测试卷列表
        List<TestPaper> papers = testPaperMapper.selectOnByCourseId(courseId);
        if(papers == null || papers.isEmpty()){
            return null;
        }
        // 遍历papers查询各个成绩信息
        for(TestPaper testPaper : papers){
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("paperName", testPaper.getName());
            userInfo.put("scoreInfo", getInfo(courseId, testPaper.getId()));
            List<Map<String, Object>> questInfo = testQuestMapper.selectQuestInfo(testPaper.getTempId());
            userInfo.put("questInfo", questInfo);
            result.add(userInfo);
        }
        return result;
    }

    private List<Map<String, Object>> getInfo(int courseId, int paperId){
        Map<String, Object> map = new HashMap <>();
        map.put( COURSE_ID, courseId );
        map.put( "search", null );
        map.put( "sex", 2 );
        map.put( "paperId", paperId );
        map.put( "order", 1 );
        map.put( "state", 2 );
        boolean withApproed = withApproed( courseId );
        List<Map<String, Object>> resultList;
        if(withApproed){
            resultList = testPaperMapper.selectScoreResultByInfoWithApproed( map );
        }else{
            resultList = testPaperMapper.selectScoreResultByInfoWithNotApproed( map );
        }
        for(Map<String, Object> m : resultList){
            int userId = (int) m.get("user_id");
            List<UserAnswer> userAnswers = userAnswerMapper.selectByUserIdAndPaperId(userId, paperId);
            m.put("questInfo", userAnswers);
        }
        return resultList;
    }

    @Override
    public JSONObject getScoreInfo(int userId, int paperId){
        JSONObject jsonObject = new JSONObject();
        User user = userMapper.selectByPrimaryKey( userId );
        jsonObject.put( "user_ada", user.getAdaNumber() );
        jsonObject.put( "user_name", user.getName() );
        TestPaper testPaper = testPaperMapper.selectById( paperId );
        jsonObject.put( "paper_name", testPaper.getName() );
        UserMark userMark = userMarkMapper.selectByUserIdAndPaperId( userId, paperId );
        if(userMark == null){
            jsonObject.put( SCORE, null );
        }else{
            jsonObject.put( SCORE, userMark.getScore() );
        }
        List<TestQuest> testQuests = testQuestMapper.selectByTempId( testPaper.getTempId() );
        if(testQuests == null || testQuests.isEmpty()){
            jsonObject.put( "quests", null );
        }else{
            JSONArray questsArray = new JSONArray();
            buildQuestsArray( testQuests, questsArray, userId, paperId );
            jsonObject.put( "quests", questsArray );
        }
        return jsonObject;
    }

    private void buildQuestsArray(List<TestQuest> testQuests, JSONArray questsArray, int userId, int paperId){
        for(TestQuest testQuest : testQuests){
            JSONObject questjson = new JSONObject();
            buildQuestJson( testQuest, questjson, userId, paperId );
            questsArray.add( questjson );
        }
    }

    private void buildQuestJson(TestQuest testQuest, JSONObject questjson, int userId, int paperId){
        questjson.put( "sequence", testQuest.getSequence() );
        int type = testQuest.getType();
        String typeShow;
        switch (type){
            case 1:
                typeShow = "单选题";
                break;
            case 2:
                typeShow = "多选题";
                break;
            default:
                typeShow = "未定义";
        }
        questjson.put( "typeShow", typeShow );
        questjson.put( "question", testQuest.getQuestion() );
        questjson.put( SCORE, testQuest.getScore() );
        List<TestOption> testOptions = testOptionMapper.selectByQuestId( testQuest.getId() );
        if(testOptions == null || testOptions.isEmpty()){
            questjson.put( "options", null );
        }else{
            StringBuilder sb = new StringBuilder();
            JSONArray optionsArray = new JSONArray();
            buildOptionsArray(testOptions, optionsArray, sb);
            questjson.put( "options", optionsArray );
            questjson.put( "trueAnswer", sb.length() == 0 ? null : sb.substring( 0, sb.length() - 1 ) );
            UserAnswer userAnswer = userAnswerMapper.selectByUserIdAndPaperIdAndQuestId( userId, paperId, testQuest.getId() );
            if(userAnswer == null){
                questjson.put( ANSWER_TYPE, 0 );
                questjson.put( "answer", null );
                questjson.put( GET_SCORE, 0 );
            }else{
                questjson.put( "answer", userAnswer.getAnswer());
                if(userAnswer.getScore().compareTo( testQuest.getScore() ) == 0){
                    questjson.put( ANSWER_TYPE, 1 );
                    questjson.put( GET_SCORE, userAnswer.getScore() );
                }else{
                    questjson.put( ANSWER_TYPE, 0 );
                    questjson.put( GET_SCORE, 0 );
                }
            }
        }
    }

    private void buildOptionsArray(List<TestOption> testOptions, JSONArray optionsArray, StringBuilder sb){
        for(TestOption testOption : testOptions){
            JSONObject optionjson = new JSONObject();
            optionjson.put( "sequence", testOption.getSequence() );
            optionjson.put( "content", testOption.getContent() );
            if(testOption.getIsTrue() == 1){
                sb.append( testOption.getSequence() ).append( "," );
            }
            optionsArray.add( optionjson );
        }
    }

    @Override
    public boolean hasResult(int paperId){
        return userAnswerMapper.countUserAnswerByPaperId( paperId ) > 0;
    }
}
