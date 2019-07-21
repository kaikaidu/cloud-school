package com.amway.acti.service.impl;

import com.amway.acti.base.exception.AmwayLogicException;
import com.amway.acti.base.exception.AmwaySystemException;
import com.amway.acti.base.util.Constants;
import com.amway.acti.dao.InvesOptionMapper;
import com.amway.acti.dao.InvesPaperMapper;
import com.amway.acti.dao.InvesQuestMapper;
import com.amway.acti.dao.InvesTempMapper;
import com.amway.acti.model.*;
import com.amway.acti.service.BackendInvesBankService;
import com.amway.acti.service.RedisService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author Wei.Li1
 * @create 2018-03-13 16:19
 **/
@Service
public class BackendInvesBankServiceImpl implements BackendInvesBankService {

    private static final String SEQUENCE = "sequence";

    private static final String OPTIONS = "options";

    @Autowired
    private InvesTempMapper invesTempMapper;

    @Autowired
    private InvesQuestMapper invesQuestMapper;

    @Autowired
    private InvesPaperMapper invesPaperMapper;
    
    @Autowired
    private InvesOptionMapper invesOptionMapper;

    @Autowired
    private RedisService redisService;

    @Override
    public PageInfo<InvesTemp> findByUser(User user, String search, int pageNo, int pageSize, boolean isDesc) {
        search = StringUtils.isBlank( search ) ? null : "%"+search+"%";
        pageNo = pageNo == 0 ? Constants.InvesBank.PAGE_NO_DEFAULT : pageNo;
        pageSize = pageSize == 0 ? Constants.InvesBank.PAGE_SIZE_DEFAULT : pageSize;
        PageHelper.startPage(pageNo, pageSize);
        List<InvesTemp> list = null;
        int ident = user.getIdent().intValue();
        switch (ident){
            case Constants.USER_IDENT_ADMIN:
                list = invesTempMapper.selectAll( search, isDesc );
                break;
            case Constants.USER_IDENT_ASSISTANT:
                list = invesTempMapper.selectByUserId( user.getId(), search, isDesc );
                break;
            default:
                break;
        }
        return new PageInfo <>( list );
    }

    @Override
    public PageInfo <InvesTemp> findAll(String search, int pageNo, int pageSize, boolean isDesc) {
        search = StringUtils.isBlank( search ) ? null : "%"+search+"%";
        pageNo = pageNo == 0 ? Constants.InvesBank.PAGE_NO_DEFAULT : pageNo;
        pageSize = pageSize == 0 ? Constants.InvesBank.PAGE_SIZE_DEFAULT : pageSize;
        PageHelper.startPage(pageNo, pageSize);
        return new PageInfo <>( invesTempMapper.selectAll( search, isDesc ) );
    }

    @Override
    public boolean hasInvesTemp(User user) {
        int count = count( user, null );
        return count != 0;
    }

    @Override
    public int count(User user, String search) {
        search = StringUtils.isBlank( search ) ? null : "%"+search+"%";
        int ident = user.getIdent().intValue();
        List<InvesTemp> list = null;
        switch (ident){
            case Constants.USER_IDENT_ADMIN:
                list = invesTempMapper.selectAll( search, true );
                break;
            case Constants.USER_IDENT_ASSISTANT:
                list = invesTempMapper.selectByUserId( user.getId(), search, true );
                break;
            default:
                break;
        }
        return list== null ? 0 : list.size();
    }

    @Override
    public int countAll(String search) {
        search = StringUtils.isBlank( search ) ? null : "%"+search+"%";
        List<InvesTemp> list = invesTempMapper.selectAll( search, true );
        return list== null ? 0 : list.size();
    }

    @Transactional
    @Override
    public List<Map<String, Object>> removeTemps(String ids) {
        List<Map<String, Object>> list = new ArrayList <>();
        String[] idArray = ids.split( "," );
        for(String idStr : idArray){
            int id = Integer.parseInt( idStr );
            Map<String, Object> map = new HashMap<>();
            map.put( "id", id );
            InvesTemp invesTemp = invesTempMapper.selectById( id );
            map.put( "name", invesTemp == null ? null : invesTemp.getName() );
            List<InvesPaper> invesPapers = invesPaperMapper.selectByTempId( id );
            if(invesPapers == null || invesPapers.isEmpty()){
                map.put( "isUsed", false );
                invesTempMapper.updateStateById( id );
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
        InvesTemp invesTemp = new InvesTemp();
        String name = tempJson.getString( "name" );
        checkName(name);
        invesTemp.setName( name );
        invesTemp.setCreateTime( new Date( System.currentTimeMillis() ) );
        invesTemp.setState( new Short( "1" ) );
        invesTemp.setUserId( user.getId() );
        invesTempMapper.insert( invesTemp );

        int tempId = invesTemp.getId();
        JSONArray questsJson = tempJson.getJSONArray( "questions" );
        addQuests( questsJson, tempId );
    }

    private void addQuests(JSONArray questsJson, int tempId){
        for(int i = 0; i < questsJson.size(); i++){
            addQuest( questsJson.getJSONObject( i ), tempId );
        }
    }

    private void addQuest(JSONObject questJson, int tempId){
        InvesQuest invesQuest = new InvesQuest();
        invesQuest.setTempId( tempId );
        invesQuest.setSequence( questJson.getInt( SEQUENCE ) );
        invesQuest.setQuestion( questJson.getString( "question" ) );
        invesQuest.setType( questJson.getInt( "type" ) );
        invesQuest.setIsRequired(questJson.getInt("isRequired"));
        invesQuestMapper.insert( invesQuest );

        int id = invesQuest.getId();
        if(invesQuest.getType() != 3){
            JSONArray optionsJson = questJson.getJSONArray( OPTIONS );
            addOptions( optionsJson, id );
        }
    }

    private void addOptions(JSONArray optionsJson, int questId){
        List<InvesOption> list = new ArrayList <>();
        for(int i = 0; i< optionsJson.size(); i++){
            InvesOption invesOption = new InvesOption();
            invesOption.setQuestId( questId );
            invesOption.setContent( optionsJson.getJSONObject( i ).getString( "content" ) );
            invesOption.setSequence( optionsJson.getJSONObject( i ).getString( SEQUENCE ) );
            list.add( invesOption );
        }
        invesOptionMapper.batchInsert( list );
    }

    private void checkName(String name){
        int checkCount = invesTempMapper.countByName( name );
        if(checkCount != 0){
            throw new AmwayLogicException( "same name" );
        }
    }

    @Transactional
    @Override
    public void editTemp(User user, String data) {
        JSONObject tempJson = JSONObject.fromObject( data );
        InvesTemp invesTemp = new InvesTemp();
        // 查出原来的数据
        int id = tempJson.getInt( "id" );
        // 校验是否被引用
        List<InvesPaper> paperList = invesPaperMapper.selectByTempId( id );
        if(paperList != null && !paperList.isEmpty()){
            throw new AmwaySystemException( "该模板已被引用，不能被编辑！" );
        }

        InvesTemp it = invesTempMapper.selectById( id );
        String name = tempJson.getString( "name" );
        if(!StringUtils.equals( it.getName(), name )){
            checkName(name);
        }
        invesTemp.setId( id );
        invesTemp.setName( name );
        invesTemp.setCreateTime( new Date( System.currentTimeMillis() ) );
        invesTemp.setState( Short.valueOf( "1" ) );
        invesTemp.setUserId( user.getId() );
        invesTempMapper.update( invesTemp );

        int tempId = invesTemp.getId();
        JSONArray questsJson = tempJson.getJSONArray( "questions" );
        editQuests( questsJson, tempId );
    }

    private void editQuests(JSONArray questsJson, int tempId){
        List<Integer> ids = invesQuestMapper.selectIdsByTempId( tempId );
        for(int i = 0; i < questsJson.size(); i++){
            String idStr = questsJson.getJSONObject( i ).getString( "id" );
            if(ids != null && StringUtils.isNotBlank( idStr ) && ids.contains( Integer.valueOf( idStr ) )){
                ids.remove( Integer.valueOf( idStr ) );
            }
            editQuest( questsJson.getJSONObject( i ), tempId );
        }
        if(ids != null && !ids.isEmpty()){
            for(Integer id : ids){
                invesQuestMapper.deleteById(id);
            }
        }
    }

    private void editQuest(JSONObject questJson, int tempId){
        InvesQuest invesQuest = new InvesQuest();
        invesQuest.setTempId( tempId );
        invesQuest.setSequence( questJson.getInt( SEQUENCE ) );
        invesQuest.setQuestion( questJson.getString( "question" ) );
        invesQuest.setType( questJson.getInt( "type" ) );
        invesQuest.setIsRequired(questJson.getInt("isRequired"));
        String idStr = questJson.getString( "id" );
        if(StringUtils.isNotBlank( idStr ) && !StringUtils.equals( idStr, "0" )){
            invesQuest.setId( Integer.valueOf( idStr ) );
            invesQuestMapper.update( invesQuest );
        }else{
            invesQuestMapper.insert( invesQuest );
        }
        int id = invesQuest.getId();
        if(invesQuest.getType() != 3){
            JSONArray optionsJson = questJson.getJSONArray( OPTIONS );
            editOptions( optionsJson, id );
        }
    }

    private void editOptions(JSONArray optionsJson, int questId){
        List<Integer> ids = invesOptionMapper.selectIdsByQuestId( questId );
        for(int i = 0; i< optionsJson.size(); i++){
            InvesOption invesOption = new InvesOption();
            invesOption.setQuestId( questId );
            invesOption.setContent( optionsJson.getJSONObject( i ).getString( "content" ) );
            invesOption.setSequence( optionsJson.getJSONObject( i ).getString( SEQUENCE ) );
            String idStr = optionsJson.getJSONObject( i ).getString( "id" );
            if(StringUtils.isNotBlank( idStr ) && !StringUtils.equals( idStr, "0" )){
                if(ids != null && ids.contains( Integer.valueOf( idStr ))){
                    ids.remove( Integer.valueOf( idStr ) );
                }
                invesOption.setId( Integer.valueOf( idStr ) );
                invesOptionMapper.update( invesOption );
            }else{
                invesOptionMapper.insert( invesOption );
            }
        }
        if(ids != null && !ids.isEmpty()){
            for(Integer id : ids){
                invesOptionMapper.deleteById(id);
            }
        }
    }


    @Override
    public InvesTemp findById(int id) {
        return invesTempMapper.selectById( id );
    }

    @Override
    public List <InvesQuest> findByTempId(int tempId) {
        return invesQuestMapper.selectByTempId( tempId );
    }

    @Override
    public List <Map <String, Object>> findInvesDeatil(Integer id) {
        return invesPaperMapper.selectDetailByTempId( id );
    }
    
    @Override
    public JSONObject findTempInfoById(Integer id){
        InvesTemp invesTemp = findById( id );
        JSONObject jsonObject = JSONObject.fromObject( invesTemp );
        List<InvesQuest> invesQuests = findByTempId( id );
        JSONArray questsJson = new JSONArray();
        for(InvesQuest invesQuest : invesQuests){
            JSONObject jj = JSONObject.fromObject( invesQuest );
            List<InvesOption> invesOptions = invesOptionMapper.selectByQuestId( invesQuest.getId() );
            JSONArray ja = JSONArray.fromObject(invesOptions);
            for(int i = 0; i<ja.size(); i++){
                JSONObject jo = ja.getJSONObject(i);
                jo.put( "type", invesQuest.getType() );
            }
            jj.put( OPTIONS, ja );
            questsJson.add( jj );
        }
        jsonObject.put( "quests", questsJson );
        return jsonObject;
    }


}
