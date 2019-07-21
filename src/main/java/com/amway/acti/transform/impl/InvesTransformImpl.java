package com.amway.acti.transform.impl;

import com.amway.acti.base.redis.RedisComponent;
import com.amway.acti.base.util.Constants;
import com.amway.acti.base.util.DateUtil;
import com.amway.acti.dao.InvesOptionMapper;
import com.amway.acti.dto.*;
import com.amway.acti.dto.inves.InvesTempDto;
import com.amway.acti.model.InvesOption;
import com.amway.acti.model.InvesPaper;
import com.amway.acti.model.InvesQuest;
import com.amway.acti.model.InvesTemp;
import com.amway.acti.service.InvesQuestService;
import com.amway.acti.service.InvesService;
import com.amway.acti.service.RedisService;
import com.amway.acti.transform.InvesTransform;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Description:问卷DTO转换
 * @Date:2018/3/8 10:13
 * @Author:wsc
 */
@Slf4j
@Service
public class InvesTransformImpl implements InvesTransform{

    @Autowired
    private InvesQuestService invesQuestService;

    @Autowired
    private InvesService invesService;

    @Autowired
    private InvesOptionMapper invesOptionMapper;

    @Value("${redisSwitch.flag}")
    private boolean flag;

    @Autowired
    private RedisService redisService;

    @Autowired
    private RedisComponent redisComponent;

    /**
     * @Description:封装问卷DTO
     * @Date:2018/3/8 10:27
     * @param invesPaperList
     * @Author:wsc
     */
    @Override
    public List<InvesDto> invesformPageDto(List<InvesPaper> invesPaperList) {
        if(CollectionUtils.isEmpty(invesPaperList)){
            return Collections.emptyList();
        }
        List<InvesDto> invesDtos = new ArrayList<>();
        for (InvesPaper inves:invesPaperList) {
            InvesDto dto = transformInvesToInvesDtos(inves);
            invesDtos.add(dto);
        }
        return invesDtos;
    }

    /**
     * @Description:封装问卷题目
     * @Date:2018/3/8 17:03
     * @param invesPaper 问卷对象
     * @Author:wsc
     */
    @Override
    public InvesInfoDto invesfromInvesInfoDto(InvesPaper invesPaper) {
        InvesInfoDto invesInfoDto = new InvesInfoDto();
        if (null != invesPaper) {
            invesInfoDto.setName(invesPaper.getName());
            invesInfoDto.setDesc(invesPaper.getDescribe());
            if (flag) {
                if (redisComponent.hasKey(Constants.INVESINFO_CACHE_KEY+invesPaper.getCourseId()+"#"+invesPaper.getId())) {
                    invesInfoDto = redisComponent.get(Constants.INVESINFO_CACHE_KEY+invesPaper.getCourseId()+"#"+invesPaper.getId(),InvesInfoDto.class);

                    updateRedisInvesInfo(invesPaper);
                    return invesInfoDto;
                }
            }
            updateRedisInvesInfo(invesPaper);
            List<InvesQuest> invesQuestList = invesQuestService.selectQuestByInvesId(invesPaper.getTempId());
            if (!CollectionUtils.isEmpty(invesQuestList)) {
                List<QuestListDto> questListDtoList = new ArrayList<>();
                //封装数据
                setInvesList(invesQuestList,questListDtoList,invesInfoDto);
            }
            if (flag) {
                redisComponent.set(Constants.INVESINFO_CACHE_KEY+invesPaper.getCourseId()+"#"+invesPaper.getId(),invesInfoDto,5,TimeUnit.DAYS);
            }
        }
        return invesInfoDto;
    }

    private void updateRedisInvesInfo(InvesPaper invesPaper){
        Map<String,String> map = new HashMap <>();
        if (redisComponent.hasKey(Constants.INVESINFO_CACHE_KEY+"map:"+invesPaper.getCourseId())) {
            ObjectMapper objectMapper = new ObjectMapper();
            Object obj = redisService.getValue(Constants.INVESINFO_CACHE_KEY+"map:"+invesPaper.getCourseId());
            try {
                map = objectMapper.readValue(obj.toString(),new TypeReference<HashMap<String, String>>() {});
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }

        map.put(invesPaper.getCourseId()+"#"+invesPaper.getId(),invesPaper.getCourseId()+"#"+invesPaper.getId());
        redisService.set(Constants.INVESINFO_CACHE_KEY+"map:"+invesPaper.getCourseId(),JSONObject.fromObject(map).toString(),3600*24*5);
    }



    //封装数据
    private void setInvesList(List<InvesQuest> invesQuestList,List<QuestListDto> questListDtoList,InvesInfoDto invesInfoDto){
        for (InvesQuest quest : invesQuestList) {
            QuestListDto questListDto = new QuestListDto();
            questListDto.setId(quest.getId());
            questListDto.setContent(quest.getQuestion());
            questListDto.setType(quest.getType());
            questListDto.setOrder(quest.getSequence());
            // 添加required
            questListDto.setRequired(quest.getIsRequired());
            //根据题目id查询选项
            List<InvesOption> invesOptions = invesOptionMapper.selectByQuestId(quest.getId());
            List<QuestItemListDto> questItemListDtoList = new ArrayList <>();
            for (InvesOption o : invesOptions) {
                QuestItemListDto questItemListDto = new QuestItemListDto();
                questItemListDto.setSequence(o.getSequence());
                questItemListDto.setItem(o.getContent());
                questItemListDtoList.add(questItemListDto);
                questListDto.setQuestItem(questItemListDtoList);
            }
            questListDtoList.add(questListDto);
            invesInfoDto.setQuestList(questListDtoList);
        }
    }

    /**
     * @Description:问卷对象转换DTO
     * @Date:2018/3/8 10:26
     * @param :invesPaper 问卷对象
     * @Author:wsc
     */
    private InvesDto transformInvesToInvesDtos(InvesPaper invesPaper){
        InvesDto invesDto = new InvesDto();
        invesDto.setId(invesPaper.getId());
        invesDto.setTempId(invesPaper.getTempId());
        invesDto.setName(invesPaper.getName());
        if (StringUtils.isEmpty(invesPaper.getAnswer())) {
            invesDto.setIsSubmit(Constants.States.NO_AVAI);
        } else {
            invesDto.setIsSubmit(Constants.States.VALID);
        }
        return invesDto;
    }

    ///////////////////李伟新增后台相关/////////////////////
    @Override
    public PageDto <InvesTempDto> transformInfoToInvesTempDto(PageInfo <InvesTemp> pageInfo) {
        PageDto <InvesTempDto> pageDto = new PageDto <>();
        pageDto.setTotalPages( pageInfo.getPages() );
        pageDto.setCurrentPage( pageInfo.getPageNum() );

        List <InvesTempDto> invesTempDtos = new ArrayList <>();
        for (InvesTemp invesTemp : pageInfo.getList()) {
            InvesTempDto invesTempDto = new InvesTempDto();
            invesTempDto.setId( invesTemp.getId() );
            invesTempDto.setName( invesTemp.getName() );
            invesTempDto.setCreateTime( DateUtil.format( invesTemp.getCreateTime(), DateUtil.YYYY_MM_DD ) );
            List<InvesPaper> paperList = invesService.getPapersListByTempId( invesTemp.getId() );
            if(paperList == null || paperList.isEmpty()){
                invesTempDto.setIsUsed( 0 );
            }else {
                invesTempDto.setIsUsed( 1 );
            }
            invesTempDtos.add( invesTempDto );
        }
        pageDto.setDataList( invesTempDtos );
        return pageDto;
    }

    @Override
    public JSONObject transformToJson(InvesTemp invesTemp, List <InvesQuest> invesQuests) {
        JSONObject jobj = new JSONObject();
        if(invesTemp == null){
            return jobj;
        }
        jobj.put( "id", invesTemp.getId() );
        jobj.put( "name", invesTemp.getName() );
        JSONArray jarr = new JSONArray();
        if(invesQuests == null){
            return jobj;
        }
        for(InvesQuest invesQuest : invesQuests){
            JSONObject jj = new JSONObject();
            jj.put( "type", invesQuest.getType() );
            jj.put( "question", invesQuest.getQuestion() );
            jj.put( "options", /*invesQuest.getOptionsShow()*/ null );
            jj.put( "sequence", invesQuest.getSequence() );
            jarr.add( jj );
        }
        jobj.put( "questions", jarr );
        return jobj;
    }

    @Override
    public JSONObject transformToViewJson(InvesTemp invesTemp, List <InvesQuest> invesQuests) {
        JSONObject jobj = new JSONObject();
        if(invesTemp == null){
            return jobj;
        }
        jobj.put( "id", invesTemp.getId() );
        jobj.put( "name", invesTemp.getName() );
        JSONArray jarr = new JSONArray();
        if(invesQuests == null){
            return jobj;
        }
        for(InvesQuest invesQuest : invesQuests){
            JSONObject jj = new JSONObject();
            jj.put( "type", invesQuest.getType() );
            jj.put( "question", invesQuest.getQuestion() );
            jj.put( "isRequired", invesQuest.getIsRequired());
            // 解析选项
            List<InvesOption> invesOptions = invesService.getOptionList( invesQuest.getId() );
            invesOptions = invesOptions == null ? new ArrayList <>() : invesOptions;
            JSONArray ja = JSONArray.fromObject( invesOptions );
            jj.put( "options", ja );
            jj.put( "sequence", invesQuest.getSequence() );
            jarr.add( jj );
        }
        jobj.put( "quests", jarr );
        return jobj;
    }

    @Override
    public JSONObject transformToPaperViewJson(InvesPaper invesPaper, InvesTemp invesTemp, List <InvesQuest> invesQuests) {
        JSONObject jobj = new JSONObject();
        if(invesPaper == null){
            return jobj;
        }
        jobj.put( "name", invesPaper.getName() );
        JSONObject tempJson = transformToViewJson( invesTemp, invesQuests );
        jobj.put( "temp", tempJson );
        return jobj;
    }
}
