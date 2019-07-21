package com.amway.acti.service.impl;

import com.amway.acti.base.exception.AmwayLogicException;
import com.amway.acti.base.exception.AmwaySystemException;
import com.amway.acti.base.util.Constants;
import com.amway.acti.base.util.ScoreUtil;
import com.amway.acti.dao.*;
import com.amway.acti.model.*;
import com.amway.acti.service.BackendTestBankService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author Wei.Li1
 * @create 2018-03-13 16:19
 **/
@Service
public class BackendTestBankServiceImpl implements BackendTestBankService {

    private static final String SEQUENCE = "sequence";

    private static final String OPTIONS = "options";

    private static final String IS_TRUE = "isTrue";

    @Autowired
    private TestTempMapper testTempMapper;

    @Autowired
    private TestQuestMapper testQuestMapper;

    @Autowired
    private TestPaperMapper testPaperMapper;

    @Autowired
    private TestOptionMapper testOptionMapper;

    @Autowired
    private UserAnswerMapper userAnswerMapper;

    @Autowired
    private UserMarkMapper userMarkMapper;

    @Override
    public PageInfo<TestTemp> findByUser(User user, String search, int pageNo, int pageSize, boolean isDesc) {
        search = StringUtils.isBlank( search ) ? null : "%"+search+"%";
        pageNo = pageNo == 0 ? Constants.TestBank.PAGE_NO_DEFAULT : pageNo;
        pageSize = pageSize == 0 ? Constants.TestBank.PAGE_SIZE_DEFAULT : pageSize;
        PageHelper.startPage(pageNo, pageSize);
        List<TestTemp> list = null;
        int ident = user.getIdent().intValue();
        switch (ident){
            case Constants.USER_IDENT_ADMIN:
                list = testTempMapper.selectAll( search, isDesc );
                break;
            case Constants.USER_IDENT_ASSISTANT:
                list = testTempMapper.selectByUserId( user.getId(), search, isDesc );
                break;
            default:
                break;
        }
        return new PageInfo <>( list );
    }

    @Override
    public PageInfo <TestTemp> findAll(String search, int pageNo, int pageSize, boolean isDesc) {
        search = StringUtils.isBlank( search ) ? null : "%"+search+"%";
        pageNo = pageNo == 0 ? Constants.TestBank.PAGE_NO_DEFAULT : pageNo;
        pageSize = pageSize == 0 ? Constants.TestBank.PAGE_SIZE_DEFAULT : pageSize;
        PageHelper.startPage(pageNo, pageSize);
        return new PageInfo <>( testTempMapper.selectAll( search, isDesc ) );
    }

    @Override
    public boolean hasTestTemp(User user) {
        int count = count(user, null );
        return count != 0;
    }

    @Override
    public int count(User user, String search) {
        search = StringUtils.isBlank( search ) ? null : "%"+search+"%";
        int ident = user.getIdent().intValue();
        List<TestTemp> list = null;
        switch (ident){
            case Constants.USER_IDENT_ADMIN:
                list = testTempMapper.selectAll( search, true );
                break;
            case Constants.USER_IDENT_ASSISTANT:
                list = testTempMapper.selectByUserId( user.getId(), search, true );
                break;
            default:
                break;
        }
        return list== null ? 0 : list.size();
    }

    @Override
    public int countAll(String search) {
        search = StringUtils.isBlank( search ) ? null : "%"+search+"%";
        List<TestTemp> list = testTempMapper.selectAll( search, true );
        return list== null ? 0 : list.size();
    }

    @Transactional
    @Override
    public List<Map<String, Object>> removeTemps(String ids) {
        List<Map<String, Object>> list = new ArrayList <>();
        String[] idArray = ids.split( "," );
        for(String idStr : idArray){
            int id = Integer.parseInt( idStr );
            Map<String, Object> map = new HashMap <>();
            map.put( "id", id );
            TestTemp testTemp = testTempMapper.selectById( id );
            map.put( "name", testTemp == null ? null : testTemp.getName() );
            List<TestPaper> testPapers = testPaperMapper.selectByTempId( id );
            if(testPapers == null || testPapers.isEmpty()){
                map.put( "isUsed", false );
                testTempMapper.updateStateById( id );
            }else{
                map.put( "isUsed", true );
            }
            list.add( map );
        }
        return list;
    }

    @Transactional
    @Override
    public void addTemp(User user, String data) {
        JSONObject tempJson = JSONObject.fromObject( data );
        TestTemp testTemp = new TestTemp();
        // 校验名称是否重复
        String name = tempJson.getString( "name" );
        checkName(name);
        testTemp.setName( name );
        testTemp.setTotalScore( BigDecimal.valueOf( tempJson.getDouble( "totalScore" ) ) );
        testTemp.setCreateTime( new Date( System.currentTimeMillis() ) );
        testTemp.setState( Short.valueOf( "1" ) );
        testTemp.setUserId( user.getId() );
        testTempMapper.insert( testTemp );

        int tempId = testTemp.getId();
        JSONArray questsJson = tempJson.getJSONArray( "questions" );
        addQuests( questsJson, tempId );
    }

    private void addQuests(JSONArray questsJson, int tempId){
        for(int i = 0; i < questsJson.size(); i++){
            addQuest( questsJson.getJSONObject( i ), tempId );
        }
    }

    private void addQuest(JSONObject questJson, int tempId){
        TestQuest testQuest = new TestQuest();
        testQuest.setTempId( tempId );
        testQuest.setSequence( questJson.getInt( SEQUENCE ) );
        testQuest.setScore( BigDecimal.valueOf( questJson.getDouble( "score" ) ) );
        testQuest.setQuestion( questJson.getString( "question" ) );
        testQuest.setType( questJson.getInt( "type" ) );
        testQuestMapper.insert( testQuest );

        int id = testQuest.getId();
        JSONArray optionsJson = questJson.getJSONArray( OPTIONS );
        addOptions( optionsJson, id );
    }

    private void addOptions(JSONArray optionsJson, int questId){
        List<TestOption> list = new ArrayList <>();
        for(int i = 0; i< optionsJson.size(); i++){
            TestOption testOption = new TestOption();
            testOption.setQuestId( questId );
            testOption.setContent( optionsJson.getJSONObject( i ).getString( "content" ) );
            testOption.setIsTrue( optionsJson.getJSONObject( i ).getInt( IS_TRUE ) );
            testOption.setSequence( optionsJson.getJSONObject( i ).getString( SEQUENCE ) );
            list.add( testOption );
        }
        testOptionMapper.batchInsert( list );
    }

    private void checkName(String name){
        int checkCount = testTempMapper.countByName( name );
        if(checkCount != 0){
            throw new AmwayLogicException( "same name" );
        }
    }

    @Transactional
    @Override
    public void editTemp(User user, String data) {
        JSONObject tempJson = JSONObject.fromObject( data );
        TestTemp testTemp = new TestTemp();
        // 查出原来的数据
        int id = tempJson.getInt( "id" );
        testTemp.setId( id );
        // 校验是否被引用
        List<TestPaper> paperList = testPaperMapper.selectByTempId( id );
        if(paperList != null && !paperList.isEmpty()){
            throw new AmwaySystemException( "该模板已被引用，不能被编辑！" );
        }

        TestTemp tt = testTempMapper.selectById( id );
        String name = tempJson.getString( "name" );
        if(!StringUtils.equals( tt.getName(), name )){
            checkName(name);
        }
        testTemp.setName( name );
        testTemp.setTotalScore( BigDecimal.valueOf( tempJson.getDouble( "totalScore" ) ) );
        testTemp.setCreateTime( new Date( System.currentTimeMillis() ) );
        testTemp.setState( new Short( "1" ) );
        testTemp.setUserId( user.getId() );
        testTempMapper.update( testTemp );

        int tempId = testTemp.getId();
        JSONArray questsJson = tempJson.getJSONArray( "questions" );
        editQuests( questsJson, tempId );
    }

    private void editQuests(JSONArray questsJson, int tempId){
        List<Integer> ids = testQuestMapper.selectIdsByTempId( tempId );
        for(int i = 0; i < questsJson.size(); i++){
            String idStr = questsJson.getJSONObject( i ).getString( "id" );
            if(ids != null && StringUtils.isNotBlank( idStr ) && ids.contains( Integer.valueOf( idStr ) )){
                ids.remove( Integer.valueOf( idStr ) );
            }
            editQuest( questsJson.getJSONObject( i ), tempId );
        }
        if(ids != null && !ids.isEmpty()){
            for(Integer id : ids){
                testQuestMapper.deleteById(id);
            }
        }
    }

    private void editQuest(JSONObject questJson, int tempId){
        TestQuest testQuest = new TestQuest();
        testQuest.setTempId( tempId );
        testQuest.setSequence( questJson.getInt( SEQUENCE ) );
        testQuest.setScore( BigDecimal.valueOf( questJson.getDouble( "score" ) ) );
        testQuest.setQuestion( questJson.getString( "question" ) );
        testQuest.setType( questJson.getInt( "type" ) );
        String idStr = questJson.getString( "id" );
        if(StringUtils.isNotBlank( idStr ) && !StringUtils.equals( idStr, "0" )){
            testQuest.setId( Integer.valueOf( idStr ) );
            testQuestMapper.update( testQuest );
        }else{
            testQuestMapper.insert( testQuest );
        }
        int id = testQuest.getId();
        JSONArray optionsJson = questJson.getJSONArray( OPTIONS );
        editOptions( optionsJson, id );
    }

    private void editOptions(JSONArray optionsJson, int questId){
        List<Integer> ids = testOptionMapper.selectIdsByQuestId( questId );
        for(int i = 0; i< optionsJson.size(); i++){
            TestOption testOption = new TestOption();
            testOption.setQuestId( questId );
            testOption.setContent( optionsJson.getJSONObject( i ).getString( "content" ) );
            testOption.setIsTrue( optionsJson.getJSONObject( i ).getInt( IS_TRUE ) );
            testOption.setSequence( optionsJson.getJSONObject( i ).getString( SEQUENCE ) );
            String idStr = optionsJson.getJSONObject( i ).getString( "id" );
            if(StringUtils.isNotBlank( idStr ) && !StringUtils.equals( idStr, "0" )){
                if(ids != null && ids.contains( Integer.valueOf( idStr ))){
                    ids.remove( Integer.valueOf( idStr ) );
                }
                testOption.setId( Integer.valueOf( idStr ) );
                testOptionMapper.update( testOption );
            }else{
                testOptionMapper.insert( testOption );
            }
        }
        if(ids != null && !ids.isEmpty()){
            for(Integer id : ids){
                testOptionMapper.deleteById(id);
            }
        }
    }

    @Override
    public TestTemp findById(int id) {
        return testTempMapper.selectById( id );
    }

    @Override
    public JSONObject findTempInfoById(int id){
        TestTemp testTemp = findById( id );
        JSONObject jsonObject = JSONObject.fromObject( testTemp );
        List<TestQuest> testQuests = findByTempId( id );
        JSONArray questsJson = new JSONArray();
        for(TestQuest testQuest : testQuests){
            JSONObject jj = JSONObject.fromObject( testQuest );
            jj.put( "scoreShow", ScoreUtil.format( String.valueOf( testQuest.getScore() ) ) );
            List<TestOption> testOptions = testOptionMapper.selectByQuestId( testQuest.getId() );
            JSONArray ja = JSONArray.fromObject(testOptions);
            // 计算正确答案
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i<ja.size(); i++){
                JSONObject jo = ja.getJSONObject(i);
                jo.put( "type", testQuest.getType() );
                if(jo.getInt( IS_TRUE ) == 1){
                    sb.append( jo.getString( SEQUENCE ) ).append( "," );
                }
            }
            jj.put( "trueAnswer", sb.substring( 0, sb.length() - 1 ) );
            jj.put( OPTIONS, ja );
            questsJson.add( jj );
        }
        jsonObject.put( "quests", questsJson );
        return jsonObject;
    }

    @Override
    public List <TestQuest> findByTempId(int tempId) {
        return testQuestMapper.selectByTempId( tempId );
    }

    @Override
    public List <Map <String, Object>> findTestDeatil(Integer id) {
        return testPaperMapper.selectDetailByTempId( id );
    }

}
