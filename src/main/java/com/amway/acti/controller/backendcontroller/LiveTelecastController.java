package com.amway.acti.controller.backendcontroller;

import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.model.CourseUser;
import com.amway.acti.model.User;
import com.amway.acti.service.LiveTelecastService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @Description:直播课程
 * @Date:2018/3/15 10:08
 * @Author:wsc
 */
@Controller
@RequestMapping("/backend/course")
@Api("/backend/course")
@Slf4j
public class LiveTelecastController {

    @Autowired
    private LiveTelecastService liveTelecastService;

    /**
     * @Description:条件分页查询直播课程
     * @Date: 2018/3/15 10:35
     * @param: pageNo
     * @param: pageSize
     * @param: courseUser
     * @Author: wsc
     */
    @RequestMapping(value = "/selectLiveTeleacstByCourseId", method = RequestMethod.GET)
    @ResponseBody
    public CommonResponseDto <PageInfo <User>> selectLiveTeleacstByCourseId(Integer pageNo, Integer pageSize, CourseUser courseUser) {
        PageInfo <User> pageInfo = liveTelecastService.selectLiveTeleacstByCourseId(pageNo, pageSize, courseUser);
        return CommonResponseDto.ofSuccess(pageInfo);
    }

    /**
     * @Description:查询总数
     * @Date: 2018/3/15 13:27
     * @param: courseUser
     * @Author: wsc
     */
    @RequestMapping(value = "/selectLiveTelecastCount", method = RequestMethod.GET)
    @ResponseBody
    public CommonResponseDto selectLiveTelecastCount(CourseUser courseUser) {
        int count = liveTelecastService.selectLiveTelecastCount(courseUser);
        return CommonResponseDto.ofSuccess(count);
    }

    /**
     * @Description:修改状态
     * @Date: 2018/3/15 16:01
     * @param: siState 报名状态
     * @param: viState 通过状态
     * @param: ids 学员id
     * @Author: wsc
     */
    @RequestMapping(value = "/updateState", method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto updateState(Integer siState, Integer viState, String ids, Integer courseId) {
        // 内部已经更改了缓存
        return liveTelecastService.updateState(siState, viState, ids, courseId);
    }

    @RequestMapping(value = "/sendTemplateMessage", method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto sendTemplateMessage(Integer courseId, Integer[] userIds) {
        List <String> resultList = liveTelecastService.sendTemplateMessage(courseId, userIds);
        return CommonResponseDto.ofSuccess(resultList);
    }

    /***
     * 批量删除
     * @param ids
     * @return
     */
    @RequestMapping(value = "/state/batchDelOflive",method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto batchDelOflive(String ids, Integer courseId){
        return liveTelecastService.batchDel(ids, courseId);
    }
}
