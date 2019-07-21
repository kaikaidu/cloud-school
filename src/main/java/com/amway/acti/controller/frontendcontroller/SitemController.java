/**
 * Created by dk on 2018/3/5.
 */

package com.amway.acti.controller.frontendcontroller;

import com.amway.acti.base.context.MiniProgramRequestContextHolder;
import com.amway.acti.base.util.Constants;
import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.dto.SubmitScoreDto;
import com.amway.acti.service.RedisService;
import com.amway.acti.service.SitemService;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/frontend/score")
public class SitemController {

    @Autowired
    private SitemService sitemService;

    @Autowired
    private RedisService redisService;

    @RequestMapping(value = {"/sitemSearch"}, method = RequestMethod.GET)
    public CommonResponseDto sitemSearch(@RequestParam int courseId) {
        return CommonResponseDto.ofSuccess(sitemService.sitemSearch(MiniProgramRequestContextHolder.getRequestUser().getUid(), courseId));
    }

    @ApiParam
    @RequestMapping(value = {"/sitemSubmit"}, method = RequestMethod.POST)
    public CommonResponseDto sitemSubmit(@RequestBody SubmitScoreDto submitScore) {
        Integer userId = MiniProgramRequestContextHolder.getRequestUser().getUid();

        String key = Constants.SAFETYLOCK_SITEMSUBMIT + submitScore.getCourseId() + "_" + submitScore.getTraineeId();
        try {
            while (!redisService.setNx0(key, submitScore.getCourseId() +"_" + submitScore.getTraineeId())) {
                log.info("user 尚未获取到锁:{}", userId);
            }
            sitemService.sitemSubmit(userId,
                submitScore.getCourseId(), submitScore.getTraineeId(), submitScore.getScores());
        } catch (Exception ex) {
            redisService.delete(key);
            throw ex;
        } finally {
            redisService.delete(key);
        }
        return CommonResponseDto.ofSuccess();
    }
}
