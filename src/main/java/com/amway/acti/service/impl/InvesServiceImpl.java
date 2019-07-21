package com.amway.acti.service.impl;

import com.amway.acti.base.redis.RedisComponent;
import com.amway.acti.base.util.Constants;
import com.amway.acti.dao.InvesOptionMapper;
import com.amway.acti.dao.InvesPaperMapper;
import com.amway.acti.dto.InvesDto;
import com.amway.acti.model.InvesOption;
import com.amway.acti.model.InvesPaper;
import com.amway.acti.service.InvesService;
import com.amway.acti.service.RedisService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:问卷
 * @Date:2018/3/8 9:50
 * @Author:wsc
 */
@Service
@Slf4j
public class InvesServiceImpl implements InvesService {

    @Autowired
    private InvesPaperMapper invesPaperMapper;

    @Autowired
    private InvesOptionMapper invesOptionMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private RedisComponent redisComponent;

    @Value("${redisSwitch.flag}")
    private boolean flag;

    /**
     * @Description:问卷列表查询
     * @Date:2018/3/8 14:53
     * @param courseId 课程id
     * @param userId 用户id
     * @Author:wsc
     */
    @Override
    public List<InvesDto> selectInvesByCourseId(Integer courseId, Integer userId) {
        log.info("courseId:{},userId:{}",courseId,userId);
        if (flag) {
            if (redisComponent.hasKey(Constants.INVES_CACHE_KEY+courseId+"#"+userId)) {
                Object obj = redisService.getValue(Constants.INVES_CACHE_KEY+courseId+"#"+userId);
                ObjectMapper objectMapper = new ObjectMapper();
                JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, InvesDto.class);
                try {
                    List<InvesDto> list = objectMapper.readValue(obj.toString(),javaType);
                    return list;
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
        List<InvesDto> list = invesPaperMapper.selectInvesByCourseId(courseId, Constants.States.VALID,userId);
        if (flag) {
            if (null != list && !list.isEmpty()) {
                redisService.set(Constants.INVES_CACHE_KEY+courseId+"#"+userId, JSONArray.fromObject(list).toString(),3600*24*5);

                Map<String,String> map = new HashMap <>();
                if (redisComponent.hasKey(Constants.INVES_CACHE_KEY+"map:"+courseId)) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    Object obj = redisService.getValue(Constants.INVES_CACHE_KEY+"map:"+courseId);
                    try {
                        map = objectMapper.readValue(obj.toString(),new TypeReference<HashMap<String, String>>() {});
                    } catch (IOException e) {
                        log.error(e.getMessage());
                    }
                }
                map.put(courseId+"#"+userId,courseId+"#"+userId);

                redisService.set(Constants.INVES_CACHE_KEY+"map:"+courseId,JSONObject.fromObject(map).toString(),3600*24*5);
            }
        }
        return list;
    }

    /**
     * @Description:问卷详情查询
     * @Date:2018/3/8 11:37
     * @param courseId 课程ID
     * @param paperId 问卷ID
     * @Author:wsc
     */
    @Override
    public InvesPaper selectInvesResult(Integer courseId, Integer paperId) {
        log.info("courseId:{},paperId:{}",courseId,paperId);
        return invesPaperMapper.selectInvesResult(courseId, Constants.States.VALID,paperId);
    }

    @Override
    public List<InvesOption> getOptionList(Integer questId){
        return invesOptionMapper.selectByQuestId( questId );
    }

    @Override
    public List<InvesPaper> getPapersListByTempId(Integer tempId){
        return invesPaperMapper.selectByTempId( tempId );
    }
}
