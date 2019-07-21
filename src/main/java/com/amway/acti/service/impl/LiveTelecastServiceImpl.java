package com.amway.acti.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.amway.acti.base.redis.RedisComponent;
import com.amway.acti.base.exception.AmwayLogicException;
import com.amway.acti.base.util.Constants;
import com.amway.acti.base.util.DateUtil;
import com.amway.acti.base.util.WXSendTemplateMessageUtil;
import com.amway.acti.dao.*;
import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.dto.CourseDto;
import com.amway.acti.dto.MessageTemplate;
import com.amway.acti.dto.MessageTemplateData;
import com.amway.acti.model.Course;
import com.amway.acti.model.CourseApprStatus;
import com.amway.acti.model.CourseApproval;
import com.amway.acti.model.CourseSignUp;
import com.amway.acti.model.CourseUser;
import com.amway.acti.model.User;
import com.amway.acti.service.LiveTelecastService;
import com.amway.acti.service.RedisService;
import com.amway.acti.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:直播课程
 * @Date:2018/3/15 10:40
 * @Author:wsc
 */
@Service
@Slf4j
public class LiveTelecastServiceImpl implements LiveTelecastService{

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private CourseSignUpMapper courseSignUpMapper;

    @Autowired
    private CourseApprovalMapper courseApprovalMapper;
    @Value("${custom.sendMessage.templateId}")
    private String templateId;
    @Autowired
    private WXSendTemplateMessageUtil messageUtil;
    @Autowired
    private RedisService redisService;
    @Autowired
    private UserAnswerMapper userAnswerMapper;
    @Autowired
    private InvesResultMapper invesResultMapper;
    @Autowired
    private UserCertMapper userCertMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisComponent redisComponent;
    @Autowired
    private DrawResultMapper drawResultMapper;
    @Autowired
    private MclassMapper mclassMapper;

    /**
     * @Description:查询直播课程
     * @Date: 2018/3/29 14:07
     * @param: pageNo
     * @param: pageSize
     * @param: courseUser
     * @Author: wsc
     */
    @Override
    public PageInfo<User> selectLiveTeleacstByCourseId(Integer pageNo, Integer pageSize, CourseUser courseUser) {
        log.info("pageNo:{},pageSize:{},courseId:{},name:{},sex:{},signupState:{},viaState:{}",
                pageNo,pageSize,courseUser.getCourseId(),courseUser.getName(),courseUser.getSex(),
                courseUser.getSignState(),courseUser.getViaState());
        List<User> userList;
        //课程
        Course course = courseMapper.selectByPrimaryKey(courseUser.getCourseId());
        log.info("course:{}",course);
        courseUser.setCurrentTime(new Date(System.currentTimeMillis()));
        PageHelper.startPage(pageNo,pageSize);
        //判断该课程是否需要审核 1：需要 0：不需要
        if (Constants.Number.INT_NUMBER1 == course.getIsVerify()) {
            userList = userMapper.selectApprovalByCourseId(courseUser);
        } else {
            userList = userMapper.selectUserAndSignupByCourseId(courseUser);
        }
        log.info("userList:{}",userList);
        //获取当前时间
        Date date = DateUtil.getCurrentDate();
        for (User u : userList) {
            //报名成功后开始时间判断 进行中和完成
            if (null == u.getSignState() || u.getSignState() == Constants.Number.INT_NUMBER1) {
                if (date.after(u.getStartTime()) && date.before(u.getEndTime())) {
                    u.setSignState(Constants.Number.INT_NUMBER1);
                } else if (date.after(u.getEndTime())) {
                    u.setSignState(Constants.Number.INT_NUMBER4);
                    //通过状态 课程已完成后，该状态从“未通过”变为“已通过”。
                    CourseSignUp courseSignUp = courseSignUpMapper.selectByUserAndCourse(courseUser.getCourseId(),u.getAdainfoMd5());
                    courseSignUp.setViaState(Constants.Number.SHORT_NUMBER1);
                    courseSignUpMapper.updateByPrimaryKeySelective(courseSignUp);
                }
            }
        }
        return new PageInfo<>(userList);
    }

    /**
     * @Description:查询总数
     * @Date: 2018/3/15 13:27
     * @param: courseUser
     * @Author: wsc
     */
    @Override
    public int selectLiveTelecastCount(CourseUser courseUser) {
        log.info("courseId:{},name:{},sex:{},signupState:{},viaState:{}",
                courseUser.getCourseId(),courseUser.getName(),courseUser.getSex(),
                courseUser.getSignState(),courseUser.getViaState());
        //课程
        Course course = courseMapper.selectByPrimaryKey(courseUser.getCourseId());
        courseUser.setCurrentTime(new Date(System.currentTimeMillis()));
        if (Constants.Number.INT_NUMBER1 == course.getIsVerify()) {
            return userMapper.selectLiveTelecastCount(courseUser);
        } else {
            return userMapper.selectUserSignCount(courseUser);
        }
    }

    /**
     * @Description:修改状态
     * @Date: 2018/3/15 16:01
     * @param: siState 报名状态
     * @param: viState 通过状态
     * @param: ids 学员id
     * @Author: wsc
     */
    @Override
    @Transactional
    public CommonResponseDto updateState(Integer siState, Integer viState, String ids, Integer courseId) {
        log.info("siState:{},viState:{},ids:{},courseId:{}", siState, viState, ids, courseId);
        checkState(siState, viState, ids, courseId);
        String[] id = ids.split(",");
        CommonResponseDto commonResponseDto;
        for (String s : id) {
            //修改报名状态
            if (null != siState) {
                Integer prState = courseApprovalMapper.selectApprResultByUserIdAndCourseId(courseId,Integer.parseInt(s));
                commonResponseDto = checkMaxApplyNum(siState, courseId, s, id.length);
                if (!ObjectUtils.isEmpty(commonResponseDto)) {
                    return commonResponseDto;
                } else {
                    courseApprovalMapper.updateApprResult(courseId, siState, s);
                    //修改课程列表缓存中的数据
                    updateRedis(courseId, s,siState);
                }

                //更新缓存课程的报名数
                /*if (1 == siState && prState != 1) {
                    courseSignUpService.upadteCourseApplyNumRedisCache(courseId, true);
                } else if (siState != 1 && prState == 1) {
                    courseSignUpService.upadteCourseApplyNumRedisCache(courseId, false);
                }*/
            }
            //修改通过状态
            if (null != viState) {
                userMapper.updateSignState(viState, s);
            }
        }
        return CommonResponseDto.ofSuccess();
    }

    private void updateRedis(Integer courseId,String userId,int siState){
        Object obj = redisService.getValue(Constants.COURSE_CACHE_KEY+courseId+"#"+userId);
        if (null != obj) {
            String applyStatus;
            switch (siState) {
                case 0:
                    applyStatus = Constants.CourseApplyStatus.FAILED;
                    break;
                case 1:
                    applyStatus = Constants.CourseApplyStatus.APPROVED;
                    break;
                case 2:
                    applyStatus = Constants.CourseApplyStatus.PENDING;
                    break;
                default:
                    applyStatus = null;
            }
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                CourseDto courseDto = objectMapper.readValue(obj.toString(),CourseDto.class);
                courseDto.setApplyStatus(applyStatus);
                redisService.set(Constants.COURSE_CACHE_KEY+courseId+"#"+userId, net.sf.json.JSONObject.fromObject(courseDto).toString(),3600*24*5);
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }



    //校验修改的状态
    private void checkState(Integer siState, Integer viState, String ids, Integer courseId) {
        if (null == siState &&  null == viState) {
            throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"请选择要修改的状态");
        }
        if (StringUtils.isEmpty(ids)) {
            throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"请选择要修改的学员");
        }
        if (StringUtils.isEmpty(courseId)) {
            throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"课程id为null");
        }
//        Course course = courseMapper.selectByPrimaryKey(courseId);
//        if (null != siState && new Date().after(course.getStartTime()) && siState != Constants.Number.INT_NUMBER1) {
//            throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"课程已开始，报名状态不可修改");
//        }
//        if (null != siState && new Date().after(course.getEndTime()) && siState != Constants.Number.INT_NUMBER1) {
//            throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"课程已结束，报名状态不可修改");
//        }
    }

    private CommonResponseDto checkMaxApplyNum(Integer siState,Integer courseId,String userId,Integer idLength){
        if (siState == Constants.Number.INT_NUMBER1) {
            CourseApproval approval = courseApprovalMapper.selectApprResultByCourseId(courseId,Integer.parseInt(userId));
            if (idLength > 0 && approval.getApprResult() != Constants.Number.SHORT_NUMBER1) {
                //根据课程id查询课程详情
                Course course = courseMapper.selectByPrimaryKey(courseId);
                //查询已报名成功的人数
                int count = courseApprovalMapper.selectAppreovalCountByCourseId(course.getSysId());
                if (null != course.getMaxApplyNum() && course.getMaxApplyNum() > 0 && (course.getMaxApplyNum() - count) < idLength) {
                    throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,
                            "该课程报名人数限制: "+course.getMaxApplyNum()+" 人,已报名成功: "+count+" 人");
                }
            }
        }
        return null;
    }

    @Override
    public List<String> sendTemplateMessage(Integer courseId, Integer[] uids) {
        String accessToken = getAccessToken();
        List<String> resultList = new ArrayList<>();
        for(Integer uid:uids){
            User user = userMapper.selectByPrimaryKey(uid);
            if(user == null){
                continue;
            }
            CourseApprStatus courseApprStatus = courseMapper.selectApplyStatusByCourseAndUser(courseId,uid,user.getAdainfoMd5());
            MessageTemplate messageTemplate = new MessageTemplate();
            messageTemplate.setTouser(user.getOpenId());
            messageTemplate.setTemplate_id(templateId);
            messageTemplate.setForm_id(courseApprStatus.getFormId());
            Map<String,MessageTemplateData> data = new HashMap<>();
            data.put("keyword1",new MessageTemplateData(courseApprStatus.getTitle()));
            data.put("keyword2",new MessageTemplateData(courseApprStatus.getStartTime()));
            data.put("keyword3",new MessageTemplateData(courseApprStatus.getName()));
            data.put("keyword4",new MessageTemplateData(courseApprStatus.getApprResult()));
            messageTemplate.setData(data);
            String response = sendMessage(messageTemplate,accessToken);
            if(!StringUtils.isEmpty(response)){
                resultList.add(user.getName()+" 推送消息失败:"+response);
            }
        }
        return resultList;
    }

    public String getAccessToken(){
        String accessToken;
        try{
            accessToken = messageUtil.getAccessToken();
        } catch (Exception ex){
            log.error(ex.getMessage(),ex);
            throw new AmwayLogicException(Constants.ErrorCode.FAIL_SEND_MESSAGE,"发送消息失败");
        }
        return accessToken;
    }

    public String sendMessage(MessageTemplate messageTemplate,String accessToken){
        String response;
        try{
            response = messageUtil.sendTemplateMessage(messageTemplate,accessToken);
        }catch (Exception ex){
            log.error(ex.getMessage(),ex);
            return "网络异常";
        }
        if(StringUtils.isEmpty(response)){
            return "发送消息失败";
        }
        JSONObject jsonObject = JSON.parseObject(response);
        String errcode = jsonObject.getString("errcode");
        if("0".equals(errcode)){
            return null;
        } else if("41029".equals(errcode)){
            return "该学员已经推送过消息";
        } else {
            return "发生消息失败";
        }
    }

    /***
     * 批量删除
     * @param ids
     * @return
     */
    @Transactional
    @Override
    public CommonResponseDto batchDel(String ids, Integer courseId) {
        List<String> resultList = new ArrayList<>();
        log.info("ids.length:{}", ids);
        boolean code = false;
        for (String userId : ids.split(",")) {
            Integer uid = Integer.parseInt(userId);
            User user = userService.getUser(uid);

            //已提交测试不能删除
            if (userAnswerMapper.selectCountByCourseIdAndUserId(courseId, uid) > 0) {
                resultList.add(user.getName() + "-学员已提交测试,不能刪除.");
                continue;
            }
            //已提交问卷不能删除
            if (invesResultMapper.selectCountByCourseIdAndUserId(courseId, uid) > 0) {
                resultList.add(user.getName() + "-学员已提交问卷,不能刪除.");
                continue;
            }
            //已发布证书不能删除
            if (userCertMapper.selectCountByCourseIdAndUserId(courseId, uid) > 0) {
                resultList.add(user.getName() + "-学员已颁发证书,不能刪除.");
                continue;
            }
            //删除已报名学员
            courseSignUpMapper.deleteByCourseAndAdainfoMd5(courseId, user.getAdainfoMd5());

            //如果需要审批，则删除审批记录
            CourseApproval courseApproval = courseApprovalMapper.selectApprResultByCourseId(courseId, uid);
            if (null != courseApproval) {
                courseApprovalMapper.deleteApprResultByCourseId(courseId, uid);
            }
            resultList.add(user.getName() + "-已报名学员刪除成功.");
            code = true;
            //清理课程列表缓存
            String key = Constants.COURSE_CACHE_KEY + courseId;
            String[] keys = redisComponent.keys( key );
            redisComponent.del( keys );

            //清理课程详情缓存
            String key0 = Constants.COURSE_USER_INFO + courseId;
            String[] keys0 = redisComponent.keys( key0 );
            redisComponent.del( keys0 );
            //删除证书
            userCertMapper.delByCourseIdAndUid(courseId, uid);
        }
        if (code) {
            return CommonResponseDto.ofSuccess(resultList);
        } else {
            return CommonResponseDto.ofFailure(Constants.ErrorCode.TEACHAR_NULL,"",resultList);
        }
    }
}
