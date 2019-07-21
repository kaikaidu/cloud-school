package com.amway.acti.controller.backendcontroller;

import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.model.DrawCuts;
import com.amway.acti.service.DrawService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * @Description:分班抽签
 * @Date:2018/3/16 17:27
 * @Author:wsc
 */
@Controller
@RequestMapping(value = "/backend/course")
public class DrawCutsController {

    @Autowired
    private DrawService drawService;

    /**
     * @Description: 根据学员姓名查询分班列表
     * @Date: 2018/5/1 11:34
     * @param: pageNo 当前页
     * @param: pageSize 每页数量
     * @param: name 学员姓名
     * @Author: wsc
     */
    @RequestMapping(value = "/findDrawUser",method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto<PageInfo<DrawCuts>> findDrawUser(Integer pageNo, Integer pageSize, Integer courseId){
        PageInfo<DrawCuts> pageInfo = drawService.findDrawUser(pageNo,pageSize,courseId);
        return CommonResponseDto.ofSuccess(pageInfo);
    }

    /**
     * @Description:获取分班的总记录数
     * @Date: 2018/3/20 9:18
     * @param: name 姓名
     * @param: courseId 课程id
     * @Author: wsc
     */
    @RequestMapping(value = "/getDrawCount",method = RequestMethod.GET)
    @ResponseBody
    public CommonResponseDto getDrawCount(String name, Integer courseId){
        return drawService.getDrawCount(name,courseId);
    }


    /**
     * @Description:创建班级
     * @Date: 2018/3/21 10:06
     * @param: userCount 学员人数
     * @param: classCount 每班人数
     * @param: model
     * @param: courseId 课程id
     * @Author: wsc
     */
    @RequestMapping(value = "/addClass",method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto addClass(Integer userCount, Integer classCount, Integer courseId) throws Exception{
        return drawService.addClass(userCount,classCount,courseId);
    }


    /**
     * @Description:更改学员班级
     * @Date: 2018/5/2 10:56
     * @param: classId 班级id
     * @param: userId 学员id
     * @param: courseId 课程id
     * @param: initClassId 原来的班级id
     * @Author: wsc
     */
    @PostMapping("/changeClass")
    @ResponseBody
    public CommonResponseDto changeClass(Integer classId,Integer userId,Integer courseId,Integer initClassId){
        return drawService.changeClass(classId,userId,courseId,initClassId);
    }

    /**
     * @Description:根据学员ID查询基本信息
     * @Date: 2018/5/2 9:41
     * @param: userId 学员id
     * @param: courseId 课程id
     * @Author: wsc
     */
    @PostMapping("/findClassUserInfo")
    @ResponseBody
    public CommonResponseDto findClassUserInfo(Integer userId,Integer courseId) {
        return drawService.findClassUserInfo(userId,courseId);
    }

    /**
     * 演讲抽签的默认页面
     * @param courseId
     * @return
     */
    @RequestMapping(value = "/speedAndDrawIndex", method = RequestMethod.GET)
    public String speedAndDrawIndex(Integer courseId){
        //判断是否分班


        return "";
    }

    /**
     * 获取演讲抽签列表数据
     * @param courseId
     * @return
     * @Author: qj
     */
    @PostMapping("/selectSpeedAndDrawList")
    @ResponseBody
    public CommonResponseDto selectSpeedAndDrawList(Integer courseId){
        return drawService.selectSpeedAndDrawList(courseId);
    }

    public CommonResponseDto selectTeacList(){
        return null;
    }

    /**
     * 立即抽签
     * @param courseId
     * @return
     * @Author: qj
     */
    @RequestMapping(value = "/drawImmediately",method = RequestMethod.POST)
    public CommonResponseDto drawImmediately(Integer courseId){
        return drawService.drawImmediately(courseId);
    }

    //抽签结果页面
}
