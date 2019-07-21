package com.amway.acti.service.impl;

import com.amway.acti.base.redis.RedisComponent;
import com.amway.acti.base.exception.AmwayLogicException;
import com.amway.acti.base.util.Constants;
import com.amway.acti.dao.InvesQuestMapper;
import com.amway.acti.dao.InvesResultMapper;
import com.amway.acti.dto.InvesAnswerDto;
import com.amway.acti.dto.InvesDto;
import com.amway.acti.dto.InvesResultDto;
import com.amway.acti.model.InvesResult;
import com.amway.acti.service.InvesResultService;
import com.amway.acti.service.RedisService;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description:学员问卷调查结果
 * @Date:2018/3/8 18:30
 * @Author:wsc
 */
@Service
@Slf4j
public class InvesResultServiceImpl implements InvesResultService {

    @Autowired
    private InvesResultMapper invesResultMapper;

    @Autowired
    private InvesQuestMapper invesQuestMapper;

    @Value("${redisSwitch.flag}")
    private boolean flag;

    @Autowired
    private RedisComponent redisComponent;

    @Autowired
    private RedisService redisService;

    /**
     * @Description:问卷提交
     * @Date:2018/3/8 18:56
     * @param invesResultDto 问卷调查结果对象
     * @param userId 用户id
     * @Author:wsc
     */
    @Override
    @Transactional
    public void insertInvesResult(InvesResultDto invesResultDto, int userId) {
        log.info("invesResultDto:{},userId:{}",invesResultDto,userId);
        if (redisComponent.hasKey(Constants.INVES_CACHE_KEY+invesResultDto.getCourseId()+"#"+userId)) {
            List<InvesDto> list = getRedisInvesDto(invesResultDto,userId);
            for (InvesDto c:list) {
                if (invesResultDto.getInvesId() == c.getId() && c.getIsSubmit() == 1) {
                    throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"该学员这套问卷已回答");
                }
            }
        } else {
            //根据学员id 问卷id查询是否已答题
            List<InvesResult> invesResults = invesResultMapper.selectByUserIdAndPaperId(userId,invesResultDto.getInvesId());
            if (null != invesResults && !invesResults.isEmpty()) {
                throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"该学员这套问卷已回答");
            }
        }
        List<InvesAnswerDto> invesAnswerDtos = invesResultDto.getInvesResult();
        if (null == invesAnswerDtos || invesAnswerDtos.isEmpty()) {
            throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"答案参数为null");
        }
        // 判断必答是否已答过
        // 1、查出该问卷所有必答题目questId
        List<Integer> requriedIds = invesQuestMapper.selectRequiredIdsByPaperId(invesResultDto.getInvesId());
        // 2、遍历invesAnswerDtos，判断答案是null的试题Id是否在requriedIds中
        for(InvesAnswerDto invesAnswerDto : invesAnswerDtos){
            String ans = invesAnswerDto.getAnswer();
            if(StringUtils.isBlank(ans)){
                int id = invesAnswerDto.getQuestId();
                if(requriedIds.contains(id)){
                    throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"存在必答题没有作答！");
                }
            }
        }

        new Thread(() -> {
            //批量新增问卷答案
            invesResultMapper.insertList(invesAnswerDtos,invesResultDto.getInvesId(),userId,new Date());
        }, "thread-name" + System.currentTimeMillis()).start();

        //修改缓存中的是否提交状态
        if (flag) {
            if (redisComponent.hasKey(Constants.INVES_CACHE_KEY+invesResultDto.getCourseId()+"#"+userId)) {
                List<InvesDto> list = getRedisInvesDto(invesResultDto,userId);
                updateRedisForInvesList(list,invesResultDto,userId);
            }
        }
    }

    //存入缓存
    private void updateRedisForInvesList(List<InvesDto> list,InvesResultDto invesResultDto,int userId) {
        for (InvesDto c:list) {
            if (invesResultDto.getInvesId() == c.getId()) {
                c.setIsSubmit(1);
            }
        }
        redisService.set(Constants.INVES_CACHE_KEY+invesResultDto.getCourseId()+"#"+userId, JSONArray.fromObject(list).toString(),3600*24*5);
    }

    //获取缓存中的问卷
    private List<InvesDto> getRedisInvesDto(InvesResultDto invesResultDto,int userId){
        Object obj = redisService.getValue(Constants.INVES_CACHE_KEY+invesResultDto.getCourseId()+"#"+userId);
        ObjectMapper objectMapper = new ObjectMapper();
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, InvesDto.class);
        List<InvesDto> list = null;
        try {
            list = objectMapper.readValue(obj.toString(),javaType);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return list;
    }
}
