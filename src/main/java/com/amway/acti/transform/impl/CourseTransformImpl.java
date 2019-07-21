/**
 * Created by Will Zhang on 2018/2/24.
 */

package com.amway.acti.transform.impl;

import com.amway.acti.base.redis.RedisComponent;
import com.amway.acti.base.exception.AmwayLogicException;
import com.amway.acti.base.exception.AmwayParamException;
import com.amway.acti.base.exception.AmwaySystemException;
import com.amway.acti.base.util.Constants;
import com.amway.acti.base.util.Constants.ApprResult;
import com.amway.acti.base.util.Constants.CourseApplyStatus;
import com.amway.acti.base.util.Constants.CourseSearchType;
import com.amway.acti.base.util.Constants.ErrorCode;
import com.amway.acti.base.util.DateUtil;
import com.amway.acti.dao.DrawResultMapper;
import com.amway.acti.dto.CourseBackendDto;
import com.amway.acti.dto.CourseBasicDto;
import com.amway.acti.dto.CourseCommonDto;
import com.amway.acti.dto.CourseDetailDto;
import com.amway.acti.dto.CourseDto;
import com.amway.acti.dto.CourseListBackendDto;
import com.amway.acti.dto.PageDto;
import com.amway.acti.model.Course;
import com.amway.acti.model.User;
import com.amway.acti.model.UserCert;
import com.amway.acti.service.*;
import com.amway.acti.transform.CourseTransform;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class CourseTransformImpl implements CourseTransform {
    @Autowired
    private CourseAgreeService courseAgreeService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseApprovalService courseApprovalService;

    @Autowired
    private CourseSignUpService courseSignUpService;

    @Autowired
    private CourseTeacherService courseTeacherService;

    @Autowired
    private CourseRegisterService courseRegisterService;

    @Autowired
    private LabelService labelService;

    @Autowired
    private UserCertService userCertService;

    @Autowired
    private DrawResultMapper drawResultMapper;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");

    private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy.MM.dd HH:mm");

    @Value("${custom.liveCourse.preUrl}")
    private String liveCoursePreUrl;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private RedisComponent redisComponent;

    @Value("${redisSwitch.flag}")
    private boolean redisSwitch;

    @Override
    public PageDto<CourseDto> transformPageInfoToDto(PageInfo<Course> pageInfo, String searchType, Integer userId) {
        long bt1 = System.currentTimeMillis();
        PageDto<CourseDto> pageDto = new PageDto<>();
        pageDto.setTotalPages(pageInfo.getPages());
        pageDto.setCurrentPage(pageInfo.getPageNum());
        if (CollectionUtils.isEmpty(pageInfo.getList())) {
            return pageDto;
        }
        //校验redis中是否有数据,存在数据直接使用缓存中的 不存在再继续查询 add wsc
        try {
            checkRedisCourseList(pageInfo.getList(), pageDto, userId ,searchType);
            if (null != pageDto.getDataList() && !pageDto.getDataList().isEmpty()) {
                long et2 = System.currentTimeMillis();
                log.info("checkRedisCourseList:"+(et2 - bt1)+ "ms");
                return pageDto;
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        List<CourseDto> courseDtos = new ArrayList<>();
        for (Course course : pageInfo.getList()) {
            CourseDto dto = transformCoursesToCourseDtos(course, searchType, userId);
            courseDtos.add(dto);
        }
        if (null != courseDtos && !courseDtos.isEmpty()) {
            //数据放入缓存中 add wsc
            for (CourseDto c : courseDtos) {
                redisService.set(Constants.COURSE_CACHE_KEY + c.getCourseId() + "#" + userId, JSONObject.fromObject(c).toString(), 3600 * 24 * 5);
            }
        }
        long et2 = System.currentTimeMillis();
        log.info(Thread.currentThread().getName()+"_uncheckRedisCourseList:"+(et2 - bt1)+ "ms");
        pageDto.setDataList(courseDtos);
        return pageDto;
    }


    //校验redis中是否有数据 有就从redis中获取，没有就查询
    private void checkRedisCourseList(List<Course> courses, PageDto<CourseDto> pageDto, Integer userId,String searchType) throws IOException {
        Object obj;
        int index = 0;
        List<CourseDto> courseDtos = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        for (Course c : courses) {
            courseAgreeService.initRedisCacheAgreeNumByCourseId(c.getSysId());
            courseSignUpService.initRedisCacheApplyNumByCourseId(c.getSysId());
            //从缓存中获取数据
            obj = redisService.getValue(Constants.COURSE_CACHE_KEY + c.getSysId() + "#" + userId);
            if (null != obj) {
                //转换数据为Javabean 并放入list中
                CourseDto courseDto = objectMapper.readValue(obj.toString(), CourseDto.class);
                //获取全局的点赞数和报名数
                courseDto.setAgreeNum(courseAgreeService.getAgreeNumFromRedisCache(c.getSysId()));
                courseDto.setApplyNum(courseSignUpService.getApplyNumFromRedisCache(c.getSysId()));
                //判断当前课程是否已完成 完成修改缓存中的ApplyStatus
                if (DateUtil.getCurrentDate().after(c.getEndTime()) && !StringUtils.isEmpty(searchType)) {
                    courseDto.setApplyStatus(CourseApplyStatus.FINISHED);
                }
                courseDtos.add(courseDto);
                index++;
            }
        }
        //判断从缓存中获取的数据和数据库中查询的数量是否一致，防止数据不统一
        if (index == courses.size() && index != 0 && !ObjectUtils.isEmpty(courseDtos)) {
            pageDto.setDataList(courseDtos);
        }
    }


    public CourseDto transformCoursesToCourseDtos(Course course, String searchType, Integer userId) {
        CourseDto courseDto = new CourseDto();
        courseDto.setTitle(course.getTitle());
        courseDto.setPicture(course.getPicture());
        courseDto.setDescription(course.getOverview());
        courseDto.setLabel(labelService.selectById(course.getLabel()).getName());
        //add wsc
        if (course.getLabel() == Constants.Number.INT_NUMBER3) {
            courseDto.setStartTime(sdf3.format(course.getStartTime()) + "-" + sdf3.format(course.getEndTime()));
        } else {
            courseDto.setStartTime(sdf.format(course.getStartTime()) + "-" + sdf.format(course.getEndTime()));
        }
        courseDto.setIsAgree(isAgree(course.getSysId(), userId) ? 1 : 0);
        courseDto.setCourseId(course.getSysId());
        //已报名课程需要报名状态
        if (CourseSearchType.REGISTERED.equals(searchType)) {
            courseDto.setApplyStatus(getCourseApplyStatus(userId, course.getSysId(), course.getIsVerify()));
        }
        //可参加课程显示我要报名按钮
        if (CourseSearchType.PARTICIPATING.equals(searchType)) {
            courseDto.setApplyStatus(CourseApplyStatus.SIGN);
        }
        //已结束课程显示已完成按钮
        if (CourseSearchType.FINISHED.equals(searchType)) {
            courseDto.setApplyStatus(CourseApplyStatus.FINISHED);
        }
        if (null == searchType) {
            courseDto.setApplyStatus("");
        }
        return (CourseDto) getCourseNums(course.getSysId(), course.getIsVerify(), courseDto);
    }

    /**
     * 获取课程的点赞，报名，收藏数
     */
    public CourseCommonDto getCourseNums(Integer courseId, Byte verity, CourseCommonDto dto) {
        boolean isVerity = verity != null && 1 == verity;
        log.info("courseId:{},isVerity:{}", courseId, isVerity);
        //点赞数
        dto.setAgreeNum(courseAgreeService.getAgreeNum(courseId));
        //报名数
        dto.setApplyNum(courseSignUpService.getSignUpNum(courseId));
        log.info("agreeNum:{},applyNum:{}", dto.getAgreeNum(), dto.getApplyNum());
        return dto;
    }

    /**
     * 获取课程的审批状态
     *
     * @param userId
     * @param courseId
     * @return
     */
    public String getCourseApplyStatus(int userId, int courseId, Byte verity) {
        boolean isVerity = verity != null && Constants.Number.INT_NUMBER1 == verity;
        if (!isVerity) {
            return CourseApplyStatus.APPROVED;
        }
        Integer apprResult = courseApprovalService.getCourseApplyStatus(userId, courseId);
        if (ObjectUtils.isEmpty(apprResult)) {
            return CourseApplyStatus.FAILED;
        }
        switch (apprResult) {
            case 0:
                return CourseApplyStatus.FAILED;
            case 1:
                return CourseApplyStatus.APPROVED;
            case 2:
                return CourseApplyStatus.PENDING;
            default:
                return null;
        }
    }

    /**
     * 是否点赞判断
     *
     * @param courseId
     * @param userId
     * @return
     */
    public boolean isAgree(int courseId, int userId) {
        return courseAgreeService.selectIsAgreed(courseId, userId);
    }


    /**
     * 从缓存中取课程详情
     * @param courseId
     * @param userId
     * @return
     */
    private CourseDetailDto getCourseDetailRedisCache(Integer courseId, Integer userId){
        try {
            if (redisComponent.hasKey(Constants.COURSE_USER_INFO + courseId + ":" + userId)) {
                return redisComponent.get(Constants.COURSE_USER_INFO + courseId + ":" + userId, CourseDetailDto.class);
            }
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
        return null;
    }

    /**
     * set课程详情缓存
     * @param courseId
     * @param userId
     * @param dto
     */
    private void settCourseDetailRedisCache(Integer courseId, Integer userId,CourseDetailDto dto){
        try {
        redisComponent.set(Constants.COURSE_USER_INFO + courseId + ":" + userId, dto,
            Constants.REDIS_EXPIRED_TIME, Constants.REDIS_EXPIRED_UNIT);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }

    @Override
    public CourseDetailDto transformModelToDto(Integer courseId, Integer userId) {
        if (redisSwitch) {
            CourseDetailDto courseDetailDto = this.getCourseDetailRedisCache(courseId, userId);
            if (null != courseDetailDto) {
                courseDetailDto.setAgreeNum(courseAgreeService.getAgreeNumFromRedisCache(courseId));
                courseDetailDto.setApplyNum(courseSignUpService.getApplyNumFromRedisCache(courseId));
                Course course = courseService.getCourseById(courseId);
                checkCourseActivity(course, userService.getUser(userId), courseDetailDto);
                log.info("CourseInfo-getRedisCache-success");
                return courseDetailDto;
            }
        }

        Course course = courseService.getCourseById(courseId);
        CourseDetailDto dto = new CourseDetailDto();
        dto.setTitle(course.getTitle());
        dto.setLabel(labelService.selectById(course.getLabel()).getName());
        dto.setPicture(course.getPicture());
        dto.setDescription(course.getOverview());
        List<String> teacher = selectTeacherByCourse(course.getSysId());
        log.info("courseId:{},teachers:{}", course.getSysId(), teacher.toString());
        dto.setTeacher(teacher.isEmpty()?"无":StringUtils.collectionToDelimitedString(teacher, "、"));
        //add wsc
        if (course.getLabel() == Constants.Number.INT_NUMBER3) {
            dto.setStartTime(sdf3.format(course.getStartTime()) + "-" + sdf3.format(course.getEndTime()));
        } else {
            dto.setStartTime(sdf.format(course.getStartTime()) + "-" + sdf.format(course.getEndTime()));
        }
        dto.setAddress(course.getAddress());
        dto.setCode(course.getCode());
        dto.setIsShare(course.getIsShare());
        setValuesForCourseDetailDto(course,userService.getUser(userId),dto);
        //set cache
        this.settCourseDetailRedisCache(courseId, userId, dto);
        return dto;
    }

    @Override
    public void setValuesForCourseDetailDto(Course course, User user, CourseDetailDto dto) {
        getCourseNums(course.getSysId(), course.getIsVerify(), dto);
        dto.setAgree(isAgree(course.getSysId(), user.getId()) ? 1 : 0);
        checkCourseActivity(course, user, dto);
    }


    public List<String> selectTeacherByCourse(Integer courseId) {
        return courseTeacherService.selectTeacherByCourse(courseId);
    }

    /**
     * 课程可参加活动
     */
    public CourseDetailDto checkCourseActivity(Course course, User user, CourseDetailDto dto) {
        log.info(">>>Begin checkCourseActivity");
        //讲师，对于已经开始的课程可以评分，其他显示课程基本信息
        if (Constants.USER_IDENT_LECTURER == user.getIdent()) {
            checkShelve(course);
            //讲师可见二维码
            dto.setQrCode(course.getQrCode());
            Boolean flag = courseTeacherService.checkIsSitem(course.getSysId(), user.getId());
            if (flag && System.currentTimeMillis() >= course.getStartTime().getTime() && System.currentTimeMillis() < course.getEndTime().getTime()) {
                if (Constants.Number.INT_NUMBER3 != course.getLabel()) {
                    dto.setSitem(course.getIsScore());
                }
                dto.setLiveUrl(course.getUrl());
            }
            return dto;
        }

        /**
         * 学生
         * 可参加课程只显示我要报名
         * 已结束课程只显示资料下载
         * 已报名课程中，只有已参加课程且课程已经开始，显示活动按钮，其他只显示基本资料
         */
        //可参加课程
        Boolean isSignUp = courseSignUpService.checkIsSignUp(course.getSysId(), user.getAdainfoMd5());
        if (!isSignUp) {
            checkShelve(course);
            if (checkSign(course)) {
                dto.setSignUp(1);
            }else{
                dto.setSignUp(0);
            }
            return dto;
        }
        //审核中，审核失败课程只显示课程基本资料
        if (Constants.Number.INT_NUMBER3 == course.getLabel()) {
            Integer apprResult;
            //不需要审核课程，视为审核通过
            if (0 == course.getIsVerify()) {
                apprResult = Integer.valueOf(ApprResult.APPROVED);
            } else {
                apprResult = courseApprovalService.getCourseApplyStatus(user.getId(), course.getSysId());
            }
            log.info("apprResult:{}", apprResult);
            if (apprResult == null || ApprResult.NOT_APPROVED == apprResult || ApprResult.PENDING == apprResult) {
                checkShelve(course);
                return dto;
            }
        }

        //审核成功课程，显示二维码
        dto.setQrCode(course.getQrCode());

        //直播课程
        if (course.getLabel() == Constants.Number.INT_NUMBER3) {
            dto = liveCourseActivity(course, user, dto);
        } else {
            dto = notLiveCourseActivity(course, user, dto);
        }
        return dto;
    }

    //审核通过非直播课程按钮判断
    public CourseDetailDto notLiveCourseActivity(Course course, User user, CourseDetailDto dto) {
        checkShelve(course);
        //审核成功课程，已完成只显示资料下载，未完成，显示活动，下架课程在已结束课程页依旧显示
        //未开始课程，只显示课程基本资料
        if (System.currentTimeMillis() < course.getStartTime().getTime()) {
            return dto;
        }
        // 开始和结束都有资料下载、证书
        dto.setDoc(course.getIsDownload());
        // 证书按钮
        UserCert userCert = userCertService.getByCourseIdAndUserId(course.getSysId(), user.getId());
        if(userCert == null){
            dto.setCert(0);
        }else{
            dto.setCert(1);
            dto.setCertIsRead(userCert.getIsRead());
        }
        if (System.currentTimeMillis() > course.getEndTime().getTime()) {
            return dto;
        }
        // 正在进行中的课程 加上签到、问卷、测试、抽签、评分
        //签到
        dto.setRegister(courseRegisterService.checkIsRegister(user.getId(), course.getSysId()) ? 0 : course.getIsSign());
        //问卷
        dto.setInves(course.getIsQuestionnaire());
        //测试
        dto.setTest(course.getIsTest());
        //抽签
        dto.setDraw(0);
        if ((drawResultMapper.selectBallotByUserIdAndCourseId(course.getSysId(),user.getId()) > 0 ? 1 : 0) == 1) {
            if (course.getIsBallot() == 1) {
                dto.setDraw(1);
            }
        }
        //评分
        dto.setSitem(drawResultMapper.selectSitemByUserId(user.getId(),course.getSysId()) > 0 ? 1 : 0);
        return dto;
    }

    //审核通过直播课程按钮判断
    public CourseDetailDto liveCourseActivity(Course course, User user, CourseDetailDto dto) {
        checkShelve(course);
        //课程未开始,开始学习按钮置灰,课程结束，开始学习按钮置灰，0：按钮不可见，暂时用不到
        // 课程未开始 显示基本信息和灰色按钮
        if (System.currentTimeMillis() < course.getStartTime().getTime()) {
            dto.setStartLearn(1);
            return dto;
        }
        // 课程开始和结束都有资料和证书
        //资料下载
        dto.setDoc(course.getIsDownload());
        // 证书按钮
        UserCert userCert = userCertService.getByCourseIdAndUserId(course.getSysId(), user.getId());
        if(userCert == null){
            dto.setCert(0);
        }else{
            dto.setCert(1);
            dto.setCertIsRead(userCert.getIsRead());
        }
        // 课程结束 显示灰色按钮
        if (System.currentTimeMillis() > course.getEndTime().getTime()) {
            dto.setStartLearn(1);
            return dto;
        }
        // 课程正在进行中，还要显示问卷、测试、红色按钮
        dto.setLiveUrl(course.getUrl());
        dto.setStartLearn(2);
        //问卷
        dto.setInves(course.getIsQuestionnaire());
        //测试
        dto.setTest(course.getIsTest());
        return dto;
    }

    /**
     * 判断课程是否下架
     */
    public void checkShelve(Course course) {
        if (Constants.Number.INT_NUMBER0 == course.getIsShelve()) {
            throw new AmwayLogicException(ErrorCode.COURSE_NOT_EXIST, "课程已下架");
        }
    }

    /**
     * 判断课程是否能报名
     *
     * @param course
     * @return
     */
    public Boolean checkSign(Course course) {
        Boolean flag = false;
        //非直播课程，不属于可参加课程
        if (Constants.Number.INT_NUMBER3 != course.getLabel().intValue()) {
            throw new AmwayLogicException(ErrorCode.COURSE_NOT_EXIST, "课程不存在");
        }
        if (System.currentTimeMillis() >= course.getSignUpBeginTime().getTime()
            && System.currentTimeMillis() < course.getSignUpEndTime().getTime()) {
            Integer applyNum = courseSignUpService.getSignSuccessNum(course.getSysId(), course.getIsVerify());
            if (applyNum < course.getMaxApplyNum()) {
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 新增课程基本信息
     */
    @Override
    public Course transformDtoToModel(CourseBasicDto dto, Integer userId) {
        //数据校验
        checkBasicInfo(dto);
        Course course = new Course();
        course.setCreateTime(new Date());
        course.setTitle(dto.getTitle());
        course.setLabel(dto.getLabel());
        course.setCode(dto.getCode());
        course.setPicture(StringUtils.isEmpty(dto.getPicture()) ? Constants.DEFAULT_COURSE_PICTURE : dto.getPicture());
        course.setOverview(dto.getDescription());
        course.setAddress(dto.getAddress());
        course.setStartTime(transformDate(dto.getStartTime(), sdf2));
        course.setEndTime(transformDate(dto.getEndTime(), sdf2));
        if (course.getStartTime().getTime() >= course.getEndTime().getTime()) {
            throw new AmwayLogicException(ErrorCode.DATE_NOT_CORRECT, "课程开始日期不小于结束日期");
        }
        course.setIsSign(dto.getLabel() == Constants.Number.INT_NUMBER3 ? (byte) 0 : (byte) 1);
        course.setQrCode(dto.getQrCode());
        course.setMaxApplyNum(transformMaxApplyNum(dto.getMaxApplyNum()));
        course.setUrl(dto.getLiveUrl());
        course.setIsShare(dto.getIsShare().byteValue());
        //如果是否审批不做设定 则数据库默认给0
        course.setIsVerify(dto.getIsAppr() == null ? ((Integer) 0).byteValue() : dto.getIsAppr().byteValue());
        course.setSignUpBeginTime(transformDate(dto.getSignStartTime(), sdf2));
        course.setSignUpEndTime(transformDate(dto.getSignEndTime(), sdf2));
        if (course.getSignUpEndTime() != null && course.getSignUpBeginTime() != null) {
            if (course.getSignUpBeginTime().getTime() >= course.getSignUpEndTime().getTime()) {
                throw new AmwayLogicException(ErrorCode.DATE_NOT_CORRECT, "课程报名日期不小于结束日期");
            }
//            if (course.getSignUpEndTime().getTime() > course.getStartTime().getTime()) {
//                throw new AmwayLogicException(ErrorCode.DATE_NOT_CORRECT, "课程报名结束时间大于课程开始时间");
//            }
            if (course.getSignUpEndTime().getTime() > course.getEndTime().getTime()) {
                throw new AmwayLogicException(ErrorCode.DATE_NOT_CORRECT, "课程报名结束时间大于课程结束时间");
            }
        }
        log.info(dto.getCourseId() + "");
        if (dto.getCourseId() == null) {
            course.setCreateMan(userId);
        }
        log.info("Course:{}", course.toString());
        return course;
    }

    public Integer transformMaxApplyNum(String maxApplyNum) {
        if (StringUtils.isEmpty(maxApplyNum)) {
            return null;
        }
        try {
            Integer result = Integer.parseInt(maxApplyNum);
            if (result <= 0) {
                throw new AmwayParamException("报名人数需要大于0的正整数");
            }
            return result;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new AmwayParamException("报名人数需要大于0的正整数");
        }
    }

    public void checkBasicInfo(CourseBasicDto dto) {
        Integer result = courseService.countByTitle(dto.getTitle(), dto.getCourseId());
        if (result != null && result > 0) {
            throw new AmwayLogicException(ErrorCode.DUMPLICATE_TITLE, "存在同名课程");
        }
        //直播课程
        if (Constants.Number.INT_NUMBER3 == dto.getLabel()) {
            if (StringUtils.isEmpty(dto.getSignStartTime())) {
                throw new AmwayParamException("报名开始时间为必填项");
            }
            if (StringUtils.isEmpty(dto.getSignEndTime())) {
                throw new AmwayParamException("报名结束时间为必填项");
            }
            if (null == dto.getIsAppr()) {
                throw new AmwayParamException("是否需要审批为必填项");
            }
            if (StringUtils.isEmpty(dto.getLiveUrl())) {
                throw new AmwayParamException("直播链接地址不能为空");
            }
        }
        //必修课程，线下课程
        else if (dto.getLabel() == Constants.Number.INT_NUMBER1 || dto.getLabel() == Constants.Number.INT_NUMBER2) {
            if (StringUtils.isEmpty(dto.getAddress())) {
                throw new AmwayParamException("上课地点不能为空");
            }
        }
    }

    /**
     * 日期转换
     * end为true时，表示输入日期的24点，即第二天0点
     *
     * @param str
     * @return
     */
    public Date transformDate(String str, SimpleDateFormat format) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        try {
            Date date = format.parse(str);
            return date;
        } catch (ParseException ex) {
            log.error(ex.getMessage(), ex);
            throw new AmwayParamException("日期格式错误");
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new AmwaySystemException("系统内部错误");
        }
    }

    @Override
    public PageDto<CourseListBackendDto> transformPageInfoToDto(PageInfo<Course> pageInfo) {
        PageDto<CourseListBackendDto> pageDto = new PageDto();
        pageDto.setCurrentPage(pageInfo.getPageNum());
        pageDto.setTotalPages(pageInfo.getPages());
        List<CourseListBackendDto> dtos = new ArrayList<>();
        for (Course course : pageInfo.getList()) {
            CourseListBackendDto dto = new CourseListBackendDto();
            dto.setCourseId(course.getSysId());
            dto.setTitle(course.getTitle());
            dto.setCreateTime(sdf2.format(course.getCreateTime()));
            dto.setStartTime(sdf2.format(course.getStartTime()));
            dto.setEndTime(sdf2.format(course.getEndTime()));
            String label = labelService.selectById(course.getLabel()).getName();
            log.info("courseId:{},labelId:{},labelName:{}", course.getSysId(), course.getLabel(), label);
            dto.setLabel(label);
            dto.setShelve(course.getIsShelve());
            User user = userService.getUser(course.getCreateMan());
            if (user != null) {
                dto.setCreateMan(user.getName());
            }
            dtos.add(dto);
        }
        pageDto.setDataList(dtos);
        return pageDto;
    }

    @Override
    public CourseBackendDto transformModelToDto(Course course, Boolean value) {
        CourseBackendDto dto = new CourseBackendDto();
        dto.setTitle(course.getTitle());
        dto.setCourseId(course.getSysId());
        if (value) {
            dto.setLabel(labelService.selectById(course.getLabel()).getName());
            List<String> teacher = courseTeacherService.selectTeacherByCourse(course.getSysId());
            dto.setTeachers(teacher.toArray(new String[] {}));
            List<String> assist = courseTeacherService.selectAssistByCourse(course.getSysId());
            dto.setAssists(assist.toArray(new String[]{}));
            dto = (CourseBackendDto) getCourseNums(course.getSysId(), course.getIsVerify(), dto);
        } else {
            dto.setLabel(course.getLabel().toString());
            List<Integer> teacher = courseTeacherService.selectTeacherIdByCourse(course.getSysId());
            dto.setTeachers(teacher.toArray(new String[] {}));

            List<Integer> assist = courseTeacherService.selectAssistIdByCourse(course.getSysId());
            dto.setAssists(assist.toArray(new String[] {}));
        }
        dto.setPicture(course.getPicture());
        dto.setDescription(course.getOverview());
        dto.setStartTime(sdf2.format(course.getStartTime()));
        //课程是否已经开始
        dto.setStarted(course.getStartTime().before(new Date()));
        dto.setEndTime(sdf2.format(course.getEndTime()));
        dto.setAddress(course.getAddress());
        dto.setCode(course.getCode());
        dto.setQrCode(course.getQrCode());
        dto.setIsShare(course.getIsShare().intValue());
        dto.setSignStartTime(course.getSignUpBeginTime() == null ? "" : sdf2.format(course.getSignUpBeginTime()));
        dto.setSignEndTime(course.getSignUpEndTime() == null ? "" : sdf2.format(course.getSignUpEndTime()));
        dto.setIsAppr(course.getIsVerify() == null ? null : course.getIsVerify().intValue());
        dto.setApplyMaxNum(course.getMaxApplyNum());
        dto.setLiveUrl(course.getUrl());
        return dto;
    }
}
