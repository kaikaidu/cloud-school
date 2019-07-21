package com.amway.acti.controller.frontendcontroller;

import com.amway.acti.base.context.MiniProgramRequestContextHolder;
import com.amway.acti.base.util.Constants;
import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.dto.InvesDto;
import com.amway.acti.dto.InvesInfoDto;
import com.amway.acti.dto.InvesResultDto;
import com.amway.acti.model.InvesPaper;
import com.amway.acti.service.InvesResultService;
import com.amway.acti.service.InvesService;
import com.amway.acti.service.RedisService;
import com.amway.acti.transform.InvesTransform;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description:问卷
 * @Date:2018/3/8 9:41
 * @Author:wsc
 */
@Slf4j
@RestController
@RequestMapping("/frontend/inves")
public class InvesController {

    @Autowired
    private InvesService invesService;

    @Autowired
    private InvesTransform invesTransform;

    @Autowired
    private InvesResultService invesResultService;

    @Autowired
    private RedisService redisService;

    /**
     * @Description: 问卷列表查询
     * @Date:2018/3/8 9:39
     * @param courseId 课程ID
     * @Author:wsc
     */
    @RequestMapping(value = {"/invesList"}, method = RequestMethod.GET)
    public CommonResponseDto<List<InvesDto>> invesListSearch(@RequestParam("courseId") Integer courseId){
        log.info("courseId:{}",courseId);
        //获取用户id
        int userId = MiniProgramRequestContextHolder.getRequestUser().getUid();
        log.info("userId:{}",userId);
        //获取问卷列表
        List<InvesDto> invesPaperList = invesService.selectInvesByCourseId(courseId,userId);
        log.info("invesPaperList:{}",invesPaperList);
        return CommonResponseDto.ofSuccess(invesPaperList);
    }

    /**
     * @Description:问卷详情查询
     * @Date:2018/3/8 11:37
     * @param courseId 课程ID
     * @param paperId 问卷ID
     * @Author:wsc
     */
    @RequestMapping(value = {"/invesResult"}, method = RequestMethod.GET)
    public CommonResponseDto<InvesInfoDto> invesResult(@RequestParam("courseId") Integer courseId ,
                                                       @RequestParam("paperId") Integer paperId){

        log.info("courseId:{},paperId:{}",courseId,paperId);
        //获取问卷
        InvesPaper invesPaper = invesService.selectInvesResult(courseId,paperId);
        InvesInfoDto invesInfoDto = invesTransform.invesfromInvesInfoDto(invesPaper);
        return CommonResponseDto.ofSuccess(invesInfoDto);
    }

    /**
     * @Description:问卷提交
     * @Date:2018/3/8 18:56
     * @param invesResultDto 问卷调查结果对象
     * @Author:wsc
     */
    @RequestMapping(value = {"/submit"}, method = RequestMethod.POST)
    public CommonResponseDto invesSubmit(@RequestBody InvesResultDto invesResultDto) {
        log.info("invesResultDto:{}", invesResultDto);
        int courseId = invesResultDto.getCourseId();
        int userId = MiniProgramRequestContextHolder.getRequestUser().getUid();
        String lockKey = Constants.SAFETYLOCK_INVESSUBMIT + courseId + ":" + userId;
        try {
            log.info("INVESSUBMIT setNx:{}", lockKey);
            while (!redisService.setNx0(lockKey, "" + userId)) {
                log.info("user 尚未获取到锁:{}", userId);
            }
            invesResultService.insertInvesResult(invesResultDto, MiniProgramRequestContextHolder.getRequestUser().getUid());
        }catch (Exception ex) {
            redisService.delete(lockKey);
            throw ex;
        } finally {
            redisService.delete(lockKey);
        }
        return CommonResponseDto.ofSuccess();
    }
}
