package com.amway.acti.controller.frontendcontroller;

import com.amway.acti.base.redis.RedisComponent;
import com.amway.acti.base.context.MiniProgramRequestContextHolder;
import com.amway.acti.base.util.Constants;
import com.amway.acti.base.util.RedisNameUtil;
import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.dto.test.PaperResultDto;
import com.amway.acti.dto.test.TestPaperDto;
import com.amway.acti.dto.test.TestResultDto;
import com.amway.acti.model.TestPaper;
import com.amway.acti.model.TestQuest;
import com.amway.acti.service.RedisService;
import com.amway.acti.service.TestService;
import com.amway.acti.transform.TestTransform;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Wei.Li1
 * @create 2018-03-05 10:26
 **/
@Slf4j
@RestController
@RequestMapping("frontend/test")
public class TestController {

    @Autowired
    private TestService testService;

    @Autowired
    private TestTransform testTransform;

    @Autowired
    private RedisComponent redisComponent;

    @Value( "${redisSwitch.flag}" )
    private boolean redisSwitch;

    @Autowired
    private RedisService redisService;

    /**
     * 测试试卷列表查询接口
     *
     * @param courseId
     * @return
     */
    @RequestMapping(value = "/paperList", method = RequestMethod.GET)
    public CommonResponseDto<List<TestPaperDto>> paperList(@RequestParam("courseId") Integer courseId) {
        log.info( "courseId:{}", courseId );
        int userId = MiniProgramRequestContextHolder.getRequestUser().getUid();
        testService.checkUserAndCourse( userId, courseId );

        String key = RedisNameUtil.getTestPaperListName( courseId, userId );
        List<TestPaperDto> testPaperDtos;
        if(redisComponent.hasKey( key )){
            testPaperDtos = redisComponent.getHMapList( key, TestPaperDto.class );
        }else{
            List<TestPaper> list = testService.getPaperList( courseId );
            testPaperDtos = testTransform.transformToTestPaperDtos( list, userId );
            Map<String, TestPaperDto> map = new HashMap <>( testPaperDtos.size() );
            for(TestPaperDto testPaperDto : testPaperDtos){
                int paperId = testPaperDto.getId();
                map.put( paperId+"", testPaperDto );
            }
            if(redisSwitch){
                redisComponent.setHMap( key, map, Constants.REDIS_EXPIRED_TIME, Constants.REDIS_EXPIRED_UNIT );
            }
        }
        return CommonResponseDto.ofSuccess( testPaperDtos );
    }

    /**
     * 测试详情or结果查询接口
     *
     * @param courseId
     * @param paperId
     * @return
     */
    @RequestMapping(value = "/paperResult", method = RequestMethod.GET)
    public CommonResponseDto<PaperResultDto> paperResult(@RequestParam(name = "courseId") Integer courseId, @RequestParam("paperId") Integer paperId) {
        log.info( "courseId:{}, paperId:{}", courseId, paperId );
        int userId = MiniProgramRequestContextHolder.getRequestUser().getUid();
        testService.checkUserAndCourse( userId, courseId );
        testService.checkCourseAndPaper( courseId, paperId );

        String key = RedisNameUtil.getTestPaperInfoName( courseId, userId, paperId );
        PaperResultDto paperResultDto;
        if(redisComponent.hasKey( key )){
            paperResultDto = redisComponent.get( key, PaperResultDto.class );
        }else{
            List<TestQuest> testQuestList = testService.getQuestList( paperId );
            paperResultDto = testTransform.transformToPaperResultDto( userId, paperId, testQuestList );
            if(redisSwitch){
                redisComponent.set( key, paperResultDto, Constants.REDIS_EXPIRED_TIME, Constants.REDIS_EXPIRED_UNIT );
            }
        }
        return CommonResponseDto.ofSuccess( paperResultDto );
    }

    /**
     * 测试试卷提交接口
     * @param testResultDto
     * @return
     */
    @RequestMapping(value = "/paperSubmit", method = RequestMethod.POST)
    public CommonResponseDto paperSubmit(@RequestBody TestResultDto testResultDto) {
        log.info("testResultDto:{}", testResultDto);
        int userId = MiniProgramRequestContextHolder.getRequestUser().getUid();
        int courseId = testResultDto.getCourseId();
        int paperId = testResultDto.getPaperId();
        String lockKey = Constants.SAFETYLOCK_TESTSUBMIT+ courseId + ":" + userId;
        try{
            log.info("TESTSUBMIT setNx:{}", lockKey);
            while (!redisService.setNx0(lockKey, "" + userId)) {
                log.info("user 尚未获取到锁:{}", userId);
            }
            testService.checkUserAndCourse(userId, courseId);
            testService.checkCourseAndPaper(courseId, paperId);
            testService.testSubmit(userId, testResultDto);
        }catch (Exception ex) {
            redisService.delete(lockKey);
            throw ex;
        } finally {
            redisService.delete(lockKey);
        }
        // 修改测试列表里对应的试卷提交状态
        String key = RedisNameUtil.getTestPaperListName(courseId, userId);
        if (redisComponent.hasKey(key)) {
            TestPaperDto testPaperDto = redisComponent.getHMapItem(key, paperId + "", TestPaperDto.class);
            testPaperDto.setIsSubmit(1);
            if (redisSwitch) {
                redisComponent.setHMapItem(key, paperId + "", testPaperDto, Constants.REDIS_EXPIRED_TIME, Constants.REDIS_EXPIRED_UNIT);
            }
        }
        // 修改测试详情里的详情
        String infoKey = RedisNameUtil.getTestPaperInfoName(courseId, userId, paperId);
        redisComponent.del(infoKey);
        if (redisComponent.hasKey(infoKey)) {
            List<TestQuest> testQuestList = testService.getQuestList(paperId);
            PaperResultDto paperResultDto = testTransform.transformToPaperResultDto(userId, paperId, testQuestList);
            if (redisSwitch) {
                redisComponent.set(infoKey, paperResultDto, Constants.REDIS_EXPIRED_TIME, Constants.REDIS_EXPIRED_UNIT);
            }
        }
        return CommonResponseDto.ofSuccess();
    }

}
