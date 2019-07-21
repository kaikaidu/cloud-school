package com.amway.acti.service.impl;

import com.amway.acti.base.redis.RedisComponent;
import com.amway.acti.base.exception.AmwayLogicException;
import com.amway.acti.base.util.Constants;
import com.amway.acti.dao.*;
import com.amway.acti.model.*;
import com.amway.acti.service.BackendInvesOnlineService;
import com.amway.acti.service.RedisService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

/**
 * @author Wei.Li1
 * @create 2018-03-16 14:07
 **/
@Service
@Slf4j
public class BackendInvesOnlineServiceImpl implements BackendInvesOnlineService {

    private static final String SEQUENCE = "sequence";

    private static final String OPTIONS = "options";

    @Autowired
    private InvesPaperMapper invesPaperMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private InvesQuestMapper invesQuestMapper;

    @Autowired
    private InvesOptionMapper invesOptionMapper;

    @Autowired
    private InvesResultMapper invesResultMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Value("${redisSwitch.flag}")
    private boolean flag;

    @Autowired
    private RedisComponent redisComponent;

    @Autowired
    private RedisService redisService;

    private static final String COURSE_ID = "courseId";

    @Override
    public PageInfo<InvesPaper> findByUser(User user, Integer courseId, String search, int state, int pageNo, int pageSize) {
        search = StringUtils.isBlank( search ) ? null : "%"+search+"%";
        pageNo = pageNo == 0 ? Constants.InvesOnline.PAGE_NO_DEFAULT : pageNo;
        pageSize = pageSize == 0 ? Constants.InvesOnline.PAGE_SIZE_DEFAULT : pageSize;
        PageHelper.startPage(pageNo, pageSize);
        List<InvesPaper> list = invesPaperMapper.selectAll( courseId, search, state );
        /*int ident = user.getIdent().intValue();
        switch (ident){
            case Constants.USER_IDENT_ADMIN:
                list = invesPaperMapper.selectAll( courseId, search, state );
                break;
            case Constants.USER_IDENT_ASSISTANT:
                list = invesPaperMapper.selectByUserId( user.getId(), courseId, search, state );
                break;
            default:
                break;
        }*/
        return new PageInfo <>( list );
    }

    @Override
    public boolean hasInvesPaper(User user, Integer courseId) {
        int count = count( user, courseId, null, 0 );
        return count != 0;
    }

    @Override
    public int count(User user, Integer courseId, String search, int state) {
        search = StringUtils.isBlank( search ) ? null : "%"+search+"%";
        int ident = user.getIdent().intValue();
        List<InvesPaper> list = invesPaperMapper.selectAll( courseId, search, state );
        /*switch (ident){
            case Constants.USER_IDENT_ADMIN:
                list = invesPaperMapper.selectAll( courseId, search, state );
                break;
            case Constants.USER_IDENT_ASSISTANT:
                list = invesPaperMapper.selectByUserId( user.getId(), courseId, search, state );
                break;
            default:
                break;
        }*/
        return list== null ? 0 : list.size();
    }

    @Transactional
    @Override
    public void removePaper(Integer id, int state, int courseId) {
        invesPaperMapper.updateStateById(id, state );
        updateCourse( courseId );
    }

    @Transactional
    @Override
    public void removePapers(List<Integer> ids) {
        if(ids != null && !ids.isEmpty()){
            for(int id : ids){
                invesPaperMapper.updateStateById(id, Constants.InvesOnline.DELETE );
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
            List<InvesPaper> invesPapers = invesPaperMapper.selectAllByCourseId( courseId );
            if(invesPapers == null || invesPapers.isEmpty()){
                return;
            }
            for(InvesPaper invesPaper : invesPapers){
                invesPaperMapper.updateStateById( invesPaper.getId(), Constants.InvesOnline.DELETE );
            }
        }
    }

    @Override
    public void addPaper(User user, InvesPaper invesPaper) {
        invesPaper.setUserId( user.getId() );
        invesPaper.setState( new Short( String.valueOf( Constants.InvesOnline.NOT_ONSALE ) ) );
        invesPaper.setCreateTime( new Date( System.currentTimeMillis() ) );
        checkName(invesPaper.getCourseId(), invesPaper.getName());
        invesPaperMapper.insert( invesPaper );
    }
    private void checkName(int courseId, String name){
        int count = invesPaperMapper.countByCourseIdAndName( courseId, name );
        if(count != 0){
            throw new AmwayLogicException( "same name" );
        }
    }


    @Override
    public InvesPaper findById(Integer id) {
        return invesPaperMapper.selectById( id );
    }

    @Override
    public void editPaper(User user, InvesPaper invesPaper) {
        int answerCount = invesResultMapper.countUserAnswerByPaperId( invesPaper.getId() );
        if(answerCount > 0){
            throw new AmwayLogicException( "already have answer" );
        }
        invesPaper.setCreateTime( new Date( System.currentTimeMillis() ) );
        InvesPaper tp = findById( invesPaper.getId() );
        if(!StringUtils.equals( tp.getName(), invesPaper.getName() )){
            checkName(invesPaper.getCourseId(), invesPaper.getName());
        }
        invesPaperMapper.update( invesPaper );

        //根据课程id删除缓存中的问卷
        deleteRedisInves(invesPaper.getCourseId());
        //根据课程id删除缓存中的问卷详情
        deleteRedisInvesInfo(invesPaper.getCourseId());
    }

    @Override
    public void onsale(String ids, Integer state, Integer courseId) {
        if(ids == null){
            return;
        }
        String[] idArray = ids.split( "," );
        for(String id : idArray){
            invesPaperMapper.updateStateById( Integer.valueOf( id ), state );
        }
        updateCourse(courseId);
    }

    private void updateCourse(int courseId){
        List<InvesPaper> list = invesPaperMapper.selectOnByCourseId( courseId );
        int count = 0;
        if(list != null){
            count = list.size();
        }
        if(count == 0){
            courseMapper.updateCourseActivity("is_questionnaire", 0, courseId );
        }else{
            courseMapper.updateCourseActivity("is_questionnaire", 1, courseId );
        }
        //根据课程id删除缓存中的问卷
        deleteRedisInves(courseId);
        //根据课程id删除缓存中的问卷详情
        deleteRedisInvesInfo(courseId);
        //删除课程详情缓存
        String key = Constants.COURSE_TEST_PAPER_USER_LIST + courseId;
        String[] keys = redisComponent.keys( key );
        redisComponent.del( keys );
    }

    //根据课程id删除缓存中的问卷
    private void deleteRedisInves(int courseId){
        if (flag) {
            if (redisComponent.hasKey(Constants.INVES_CACHE_KEY+"map:"+courseId)) {
                ObjectMapper objectMapper = new ObjectMapper();
                Object obj = redisService.getValue(Constants.INVES_CACHE_KEY+"map:"+courseId);
                try {
                    Map<String,String> map = objectMapper.readValue(obj.toString(),new TypeReference<HashMap<String, String>>() {});
                    for(String s:map.keySet()){
                        redisComponent.del(Constants.INVES_CACHE_KEY+map.get(s));
                    }
                    redisComponent.del(Constants.INVES_CACHE_KEY+"map:"+courseId);
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }

    //根据课程id删除缓存中的问卷详情
    private void deleteRedisInvesInfo(int courseId){
        if (flag) {
            if (redisComponent.hasKey(Constants.INVESINFO_CACHE_KEY+"map:"+courseId)) {
                ObjectMapper objectMapper = new ObjectMapper();
                Object obj = redisService.getValue(Constants.INVESINFO_CACHE_KEY+"map:"+courseId);
                try {
                    Map<String,String> map = objectMapper.readValue(obj.toString(),new TypeReference<HashMap<String, String>>() {});
                    for(String s:map.keySet()){
                        redisComponent.del(Constants.INVESINFO_CACHE_KEY+map.get(s));
                    }
                    redisComponent.del(Constants.INVESINFO_CACHE_KEY+"map:"+courseId);
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }

    @Override
    public List<InvesPaper> findOnByCourseId(Integer courseId){
        return invesPaperMapper.selectOnByCourseId( courseId );
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
    public List<Map<String, Object>> getResultByInfo(Map<String, Object> info, int pageNo, int pageSize){
        boolean withApproed = withApproed( Integer.parseInt( info.get( COURSE_ID ).toString() )  );
        List<Map<String, Object>> resultList;
        pageNo = pageNo == 0 ? Constants.TestOnline.PAGE_NO_DEFAULT : pageNo;
        pageSize = pageSize == 0 ? Constants.TestOnline.PAGE_SIZE_DEFAULT : pageSize;
        PageHelper.startPage(pageNo, pageSize);
        if(withApproed){
            resultList = invesPaperMapper.selectResultByInfoWithApproed( info );
        }else{
            resultList = invesPaperMapper.selectResultByInfoWithNotApproed( info );
        }
        return resultList;
    }

    @Override
    public int countResultByInfo(Map<String, Object> info){
        boolean withApproed = withApproed( Integer.parseInt( info.get( COURSE_ID ).toString() )  );
        List<Map<String, Object>> resultList;
        if(withApproed){
            resultList = invesPaperMapper.selectResultByInfoWithApproed( info );
        }else{
            resultList = invesPaperMapper.selectResultByInfoWithNotApproed( info );
        }
        return resultList == null ? 0 : resultList.size();
    }

    @Override
    public List<Map<String, Object>> getResult(int courseId){
        List<Map<String, Object>> result = new ArrayList<>();
        // 根据courseId查询出问卷列表
        List<InvesPaper> papers = invesPaperMapper.selectOnByCourseId(courseId);
        if(papers == null || papers.isEmpty()){
            return null;
        }
        // 遍历papers查询各个答题情况
        for(InvesPaper invesPaper : papers){
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("paperName", invesPaper.getName());
            userInfo.put("resultInfo", getInfo(courseId, invesPaper.getId(), invesPaper.getTempId()));
            List<Map<String, Object>> questInfo = invesQuestMapper.selectInfoByTempId(invesPaper.getTempId());
            userInfo.put("questInfo", questInfo);
            result.add(userInfo);
        }
        return result;
    }

    private List<Map<String, Object>> getInfo(int courseId, int paperId, int tempId){
        Map<String, Object> map = new HashMap <>();
        map.put( COURSE_ID, courseId );
        map.put( "search", null );
        map.put( "sex", 2 );
        map.put( "paperId", paperId );
        map.put( "state", 2 );
        boolean withApproed = withApproed( courseId );
        List<Map<String, Object>> resultList;
        if(withApproed){
            resultList = invesPaperMapper.selectResultByInfoWithApproed( map );
        }else{
            resultList = invesPaperMapper.selectResultByInfoWithNotApproed( map );
        }
        for(Map<String, Object> m : resultList){
            int userId = (int) m.get("user_id");
            List<Map<String, String>> allResults = invesResultMapper.selectAllAnswerByQuest(paperId, userId, tempId);
            m.put("questInfo", allResults);
        }
        return resultList;
    }

    @Override
    public JSONObject buildViewData(int userId, int paperId){
        JSONObject jsonObject = new JSONObject();
        InvesPaper invesPaper = invesPaperMapper.selectById( paperId );
        if(invesPaper == null){
            return jsonObject;
        }
        jsonObject.put( "name", invesPaper.getName() );
        // 根据tempId获取题目集合
        List<InvesQuest> invesQuests = invesQuestMapper.selectByTempId( invesPaper.getTempId() );
        if(invesQuests == null){
            return jsonObject;
        }
        JSONArray questsArray = new JSONArray();
        buildQuestsArray( invesQuests, questsArray, userId, paperId );
        jsonObject.put( "quests", questsArray );
        return jsonObject;
    }

    private void buildQuestsArray(List<InvesQuest> invesQuests, JSONArray questsArray, int userId, int paperId){
        for(InvesQuest invesQuest : invesQuests){
            JSONObject questJson = new JSONObject();
            int questId= invesQuest.getId();
            int type = invesQuest.getType();
            int isRequried = invesQuest.getIsRequired();
            String question = invesQuest.getQuestion();
            int sequence = invesQuest.getSequence();
            questJson.put( "type", type );
            questJson.put("isRequired", isRequried);
            questJson.put( "question", question );
            questJson.put( SEQUENCE, sequence );
            String typeShow;
            switch (type){
                case 1:
                    typeShow = "单选题";
                    break;
                case 2:
                    typeShow = "多选题";
                    break;
                case 3:
                    typeShow = "问答题";
                    break;
                default:
                    typeShow = "未定义";
            }
            questJson.put( "typeShow", typeShow );

            InvesResult invesResult = invesResultMapper.selectByUserIdAndPaperIdAndQuestId( userId, paperId, questId );
            if(invesResult == null){
                questJson.put( "done", 0 );
                invesResult = new InvesResult();
            }else{
                questJson.put( "done", 1 );
            }
            String userAnswer = invesResult.getAnswer();
            if(type == 3) {
                questJson.put( "answer", userAnswer );
            }else{
                List<InvesOption> invesOptions = invesOptionMapper.selectByQuestId( invesQuest.getId() );
                if(invesOptions == null){
                    questJson.put( OPTIONS, null );
                }else{
                    JSONArray optionsArray = new JSONArray();
                    buildOptionsArray( invesOptions, optionsArray, type, userAnswer );
                    questJson.put( OPTIONS, optionsArray );
                }
            }
            questsArray.add( questJson );
        }
    }

    private void buildOptionsArray(List<InvesOption> invesOptions, JSONArray optionsArray, int type, String userAnswer){
        List<String> ans = new ArrayList <>();
        if(userAnswer != null){
            if(type == 1){
                ans.add( userAnswer.replaceAll( ",", "" ) );
            }else{
                ans = Arrays.asList( userAnswer.split( "," ) );
            }
        }
        for(InvesOption invesOption : invesOptions){
            JSONObject optionjson = new JSONObject();
            optionjson.put( SEQUENCE, invesOption.getSequence() );
            optionjson.put( "content", invesOption.getContent() );
            if(ans.contains( invesOption.getSequence() )){
                optionjson.put( "isTrue", 1 );
            }
            optionsArray.add( optionjson );
        }
    }

    @Override
    public boolean hasResult(int paperId){
        return invesResultMapper.countUserAnswerByPaperId( paperId ) > 0;
    }

}
