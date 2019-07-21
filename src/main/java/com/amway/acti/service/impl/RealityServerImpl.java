package com.amway.acti.service.impl;

import com.amway.acti.base.exception.AmwayLogicException;
import com.amway.acti.base.exception.AmwaySystemException;
import com.amway.acti.base.redis.RedisComponent;
import com.amway.acti.base.util.CheckUtils;
import com.amway.acti.base.util.Constants;
import com.amway.acti.base.util.DateUtil;
import com.amway.acti.base.util.DownloadUtil;
import com.amway.acti.base.util.ExcelExportUtils;
import com.amway.acti.base.util.ExcelImportUtils;
import com.amway.acti.base.util.IDCardUtils;
import com.amway.acti.dao.AddrMapper;
import com.amway.acti.dao.CertCustomMapper;
import com.amway.acti.dao.ClassDrawMapper;
import com.amway.acti.dao.CourseApprovalMapper;
import com.amway.acti.dao.CourseMapper;
import com.amway.acti.dao.CourseRegisterMapper;
import com.amway.acti.dao.CourseSignUpMapper;
import com.amway.acti.dao.CourseTeacherMapper;
import com.amway.acti.dao.DrawResultMapper;
import com.amway.acti.dao.InvesResultMapper;
import com.amway.acti.dao.MclassMapper;
import com.amway.acti.dao.ScoreAnswerMapper;
import com.amway.acti.dao.ScoreResultMapper;
import com.amway.acti.dao.SpeeMarkMapper;
import com.amway.acti.dao.UserAnswerMapper;
import com.amway.acti.dao.UserCertCustomMapper;
import com.amway.acti.dao.UserCertMapper;
import com.amway.acti.dao.UserMapper;
import com.amway.acti.dao.WeightMapper;
import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.dto.CourseDetailDto;
import com.amway.acti.dto.CourseDto;
import com.amway.acti.model.Addr;
import com.amway.acti.model.Cert;
import com.amway.acti.model.Course;
import com.amway.acti.model.CourseApproval;
import com.amway.acti.model.CourseSignUp;
import com.amway.acti.model.CourseUser;
import com.amway.acti.model.Mclass;
import com.amway.acti.model.RealityUser;
import com.amway.acti.model.ScoreResult;
import com.amway.acti.model.User;
import com.amway.acti.model.UserCert;
import com.amway.acti.service.RealityServer;
import com.amway.acti.service.RedisService;
import com.amway.acti.service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description:导入学员
 * @Date:2018/3/12 9:59
 * @Author:wsc
 */
@Service
@Slf4j
public class RealityServerImpl implements RealityServer {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CourseSignUpMapper courseSignUpMapper;
    @Autowired
    private AddrMapper addrMapper;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private CourseApprovalMapper courseApprovalMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private MclassMapper mclassMapper;
    @Autowired
    private RedisService redisService;
    @Autowired
    private RedisComponent redisComponent;
    @Autowired
    private CourseRegisterMapper courseRegisterMapper;
    @Autowired
    private UserAnswerMapper userAnswerMapper;
    @Autowired
    private InvesResultMapper invesResultMapper;
    @Autowired
    private UserCertMapper userCertMapper;
    @Autowired
    private DrawResultMapper drawResultMapper;
    @Autowired
    private UserCertCustomMapper userCertCustomMapper;
    @Autowired
    private CertCustomMapper certCustomMapper;
    @Autowired
    private ClassDrawMapper classDrawMapper;
    @Autowired
    private WeightMapper weightMapper;
    @Autowired
    private ScoreResultMapper scoreResultMapper;
    @Autowired
    private ScoreAnswerMapper scoreAnswerMapper;
    @Autowired
    private SpeeMarkMapper speeMarkMapper;
    @Autowired
    private CourseTeacherMapper courseTeacherMapper;

    @Value("${localFolder.url}")
    private String localFolder;


    /**
     * @Description: 校验学员信息并插入学员
     * @Date: 2018/3/13 9:46
     * @param: map 学员集合
     * @param: courseId 课程id
     * @Author: wsc
     */
    @Override
    @Transactional
    public CommonResponseDto  checkInsertUser(Integer courseId, String state,Integer dataLength,User user,MultipartFile file) throws ParseException, IOException {
        CommonResponseDto commonResponseDto = new CommonResponseDto();
        //判断课程是否已评分
        List<ScoreResult> scoreResults = scoreResultMapper.selectScoreResultByCourseId(courseId);
        if (null != scoreResults && !scoreResults.isEmpty()) {
            throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"该课程已评分，不能再导入学员!");
        }
        //获取文件名
        String fileName = file.getOriginalFilename();
        //校验Excel文件格式
        String message = checkExcel(file, fileName);
        List<User> userList;
        if (StringUtils.isEmpty(message)) {
            userList = temporary(file, fileName, commonResponseDto,courseId,user.getId());
        } else {
            throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,message);
        }
        //校验当前课程是否正在导入报名
        if (redisComponent.hasKey(Constants.UPDATE_COURSE_SIGN + courseId)) {
            String courseSign = redisService.getValue(Constants.UPDATE_COURSE_SIGN + courseId).toString();
            if (!StringUtils.isEmpty(courseSign)) {
                if (courseSign.equals(Constants.Number.STRING_NUMBER1)) {
                    throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"该课程已有用户正在导入报名学员，请稍后再试!");
                }
            }
        }
        //把总数存入redis中
        redisComponent.set(Constants.STUDENT_TOTAL + courseId + "#" + user.getId(),userList.size());
        //往缓存中存放标识，防止修改课程证书时数据混乱
        redisComponent.set(Constants.UPDATE_COURSE_SIGN + courseId,Constants.Number.INT_NUMBER1);
        //创建可缓存线程池
        ExecutorService pool = Executors.newSingleThreadExecutor();
        //创建一个异步子线程
        try {
            pool.execute(this.insertUser(userList,courseId,user));
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            //关闭线程池
            pool.shutdown();
        }
        return CommonResponseDto.ofSuccess();

    }

    //新增报名学员子线程
    private Runnable insertUser(List<User> userList,Integer courseId,User user){
        return () -> {
            try {
                //判断从缓存中是否拿到数据，没拿到抛出异常
                if (null != userList) {
                    List<CourseSignUp> addCourseSignUpList = new ArrayList <>();//报名用户新增的集合
                    //获取当前课程
                    Course course = courseMapper.selectByPrimaryKey(courseId);
                    //创建可缓存线程池
                    ExecutorService pool = Executors.newSingleThreadExecutor();
                    //记数
                    CountDownLatch cdl = new CountDownLatch(userList.size());
                    for (int i = 0 ; i < userList.size() ; i ++) {
                        //数据处理 把数据封装到4个集合中
                        pool.execute(addListData(addCourseSignUpList,userList.get(i),courseId,course,cdl,i,user));
                    }
                    cdl.await();
                    pool.shutdown();
                    //根据课程id从缓存中取出数据 修改报名人数 在存入缓存中 add wsc
                    updateRedisCourse(courseId,addCourseSignUpList.size());
                    //删除缓存
                    redisService.delete(Constants.STUDENT_LIST + courseId + "#" + user.getId());
                } else {
                    throw new AmwaySystemException("内部异常!");
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            } finally {
                log.info("redis_update_course_sign");
                //修改缓存中的标识，用于判断是否可以修改课程证书,避免数据混乱
                redisComponent.del(Constants.UPDATE_COURSE_SIGN + courseId);
                redisComponent.del(Constants.STUDENT_COUNT + courseId +"#"+user.getId());
                redisComponent.del(Constants.STUDENT_TOTAL + courseId + "#"+user.getId());
            }
        };
    }

    //修改缓存中的基本信息 模糊查询
    private void updateRedisCourse(Integer courseId,int applyNum){
        Set<String> strings = redisService.keys(Constants.COURSE_CACHE_KEY+courseId+"#");
        Iterator<String> it = strings.iterator();
        Object obj;
        String str;
        CourseDto courseDto;
        ObjectMapper objectMapper = new ObjectMapper();
        while (it.hasNext()) {
            str = it.next();
            obj = redisService.getValue(str);
            //更改缓存中课程的数据
            if (null != obj) {
                try {
                    courseDto = objectMapper.readValue(obj.toString(),CourseDto.class);
                    courseDto.setApplyNum(courseDto.getApplyNum() + applyNum);
                    redisService.set(str, JSONObject.fromObject(courseDto).toString(),3600*24*5);
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }

    //数据处理 把数据封装到4个集合中
    private Runnable addListData(List<CourseSignUp> addCourseSignUpList, User u,
                             Integer courseId, Course course,CountDownLatch cdl,int i,User us) {
        return () -> {
            if (null != u) {
                String adaInfoMd5 = userService.getAdainfoMd5(u.getAdaNumber(),u.getSex());
                User user = userMapper.selectByAdaEncode(adaInfoMd5);

                //获取需要新增或修改的用户
                getUpdateUserList(u,user,adaInfoMd5);

                //获取需要报名的学员
                CourseSignUp courseSignUp = courseSignUpMapper.selectByUserAndCourse(courseId, adaInfoMd5);
                if (null == courseSignUp) {
                    courseSignUp = new CourseSignUp();
                    courseSignUp.setCourseId(courseId);//课程id
                    courseSignUp.setAdainfoMd5(adaInfoMd5);//ada帐号+性别的md5加密
                    courseSignUp.setViaState(Constants.Number.SHORT_NUMBER0);//通过状态 0-未通过 1-通过
                    courseSignUp.setSingupTime(new Date(System.currentTimeMillis()));
                    addCourseSignUpList.add(courseSignUp);
                    courseSignUpMapper.insertSelective(courseSignUp);
                    //更新缓存中课程的报名数
                    this.upadteCourseApplyNumRedisCache(courseId);
                    //更新课程详情是否已报名
                    upadteCourseInfoIsSignUpRedisCache(u.getId(),courseId);
                }

                //判断该课程是否需要审批
                getCourseApprovalList(course,u);

                //获取需要新增的学员证书
                getUserCertList(u,courseId);
            }
            redisComponent.set(Constants.STUDENT_COUNT + courseId + "#" + us.getId(),(i+1));
            cdl.countDown();
        };
    }

    private void getUserCertList(User u, Integer courseId) {
        //查询该课程下的学员是否已新增学员证书
        UserCert userCert = userCertCustomMapper.selectUserCertByCourseIdAndUserId(courseId,u.getId());
        if (ObjectUtils.isEmpty(userCert)) {
            //查询课程证书
            Cert cert = certCustomMapper.selectCertByCourseId(courseId);
            userCert = new UserCert();
            userCert.setCourseId(courseId);
            userCert.setUserId(u.getId());
            userCert.setIsAward(Constants.Number.INT_NUMBER0);//未颁发
            userCert.setCreateTime(new Date());
            userCert.setState(Constants.Number.INT_NUMBER1);//可用
            if (!ObjectUtils.isEmpty(cert)) {
                userCert.setCertId(cert.getId());//证书id
                userCert.setName(cert.getName());//证书名称
            }
            userCertMapper.insertSelective(userCert);
        }
    }

    private void getCourseApprovalList(Course course, User u) {
        //判断该课程是否需要审批
        if (Constants.Number.BYTE_NUMBER == course.getIsVerify()) {
            CourseApproval courseApproval = courseApprovalMapper.selectApprResultByCourseId(course.getSysId(),u.getId());
            if (null == courseApproval) {
                courseApproval = new CourseApproval();
                //新增审核表
                courseApproval.setCourseId(course.getSysId());
                courseApproval.setApprResult(Constants.Number.SHORT_NUMBER1);//审批状态 0:未通过 1:已通过
                courseApproval.setUserId(u.getId());
                courseApprovalMapper.insertSelective(courseApproval);
            }
        }
    }

    //获取需要新增或修改的用户
    private void getUpdateUserList(User u, User user,String adaInfoMd5) {
        //获取需要修改的学员
        if (null != user) {
            u.setId(user.getId());
            u.setAdaNumber(user.getAdaNumber());
            u.setAdainfoMd5(adaInfoMd5);
            u.setState(Constants.Number.SHORT_NUMBER1);
            userMapper.updateByPrimaryKeySelective(u);
        } else {
            //获取需要新增的学员
            u.setId(null);
            u.setIdent(Constants.Number.SHORT_NUMBER1);//用户身份(角色)：1-学员  2-讲师  3-助教 0-管理员
            u.setState(Constants.Number.SHORT_NUMBER1);//有效
            u.setSsoAdanumber(u.getAdaNumber());//原始卡号
            u.setAdainfoMd5(adaInfoMd5);//ada帐号+性别的md5加密
            u.setAdaNumber(userService.completion(u.getAdaNumber(),u.getSex()));//补全16位卡号
            u.setCreateTime(new Date(System.currentTimeMillis()));
            userMapper.insertSelective(u);
        }
    }

    /***
     * 更新课程详情缓存，是否已报名
     * @param userId
     * @param courseId
     */
    private void upadteCourseInfoIsSignUpRedisCache(Integer userId, Integer courseId) {
        try {
            if (redisComponent.hasKey(Constants.COURSE_USER_INFO + courseId + ":" + userId)) {
                ObjectMapper objectMapper = new ObjectMapper();
                Object obj0 = redisService.getValue(Constants.COURSE_USER_INFO + courseId + ":" + userId);
                CourseDetailDto courseDetailDto = objectMapper.readValue(obj0.toString(), new TypeReference<CourseDetailDto>() {
                });
                if (null != courseDetailDto) {
                    courseDetailDto.setSignUp(0);
                    redisComponent.set(Constants.COURSE_USER_INFO + courseId + ":" + userId, courseDetailDto,
                        Constants.REDIS_EXPIRED_TIME, Constants.REDIS_EXPIRED_UNIT);
                }
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    /**
     * 更新缓存
     * @param courseId
     */
    private void upadteCourseApplyNumRedisCache(Integer courseId) {
        if (redisComponent.hasKey(Constants.COURSE_APPLY_NUM + courseId)) {
            Integer applyNum = Integer.parseInt(redisService.
                getValue(Constants.COURSE_APPLY_NUM + courseId).toString());
            redisComponent.set(Constants.COURSE_APPLY_NUM + courseId, ++applyNum);
        }
    }

    /**
     * @Description:条件分页查询学员
     * @Date: 2018/3/14 10:43
     * @param: courseId 课程id
     * @param: name 学员名称
     * @param: pageNo 当前页
     * @param: sortSex 性别
     * @param: sortSignupState 报名状态
     * @param: sortViaState 通过状态
     * @Author: wsc
     */
    @Override
    public PageInfo <User> selectUserAndSignupByCourseId(Integer pageNo,
                                                         Integer pageSize, CourseUser courseUser) {
        log.info("pageNo:{},pageSize:{},courseUser:{}",pageNo,pageSize,courseUser);
        courseUser.setCurrentTime(new Date(System.currentTimeMillis()));
        PageHelper.startPage(pageNo,pageSize);
        List <User> userList = userMapper.selectUserAndSignupByCourseId(courseUser);
        //获取当前时间
        Date date = DateUtil.getCurrentDate();
        for (User u : userList) {
            //当前时间小于课程开始时间
            if (date.before(u.getStartTime()) || (date.after(u.getStartTime()) && date.before(u.getEndTime()))) {
                u.setSignState(Constants.Number.INT_NUMBER1);
            } else if (date.after(u.getEndTime())) {
                //通过状态 课程已完成后，该状态从“未通过”变为“已通过”。
                CourseSignUp courseSignUp = courseSignUpMapper.selectByUserAndCourse(courseUser.getCourseId(),u.getAdainfoMd5());
                courseSignUp.setViaState(Constants.Number.SHORT_NUMBER1);
                courseSignUpMapper.updateByPrimaryKeySelective(courseSignUp);
                u.setSignState(Constants.Number.INT_NUMBER4);
                u.setViaState(Constants.Number.INT_NUMBER1);
            }
        }
        return new PageInfo <>(userList);
    }
    /**
     * @Description:获取学员总记录数
     * @Date: 2018/3/14 13:59
     * @param: courseId 课程id
     * @Author: wsc
     */
    @Override
    public int selectUserSignCount(CourseUser courseUser) {
        log.info("courseId:{},name:{},sex:{},signState:{},viaState:{}",courseUser.getCourseId(),courseUser.getName(),
                courseUser.getSex(),courseUser.getSignState(),courseUser.getViaState());
        courseUser.setCurrentTime(new Date(System.currentTimeMillis()));
        return userMapper.selectUserSignCount(courseUser);
    }
    /**
     * Description : 修改报名通过状态
     * @Date: 2018/3/14 15:29
     * @param: ids 学员id数组
     * @Author: wsc
     */
    @Override
    @Transactional
    public CommonResponseDto updateSignState(String ids, Integer state) {
        log.info("ids:{},state:{}",ids,state);
        String[] id = ids.split(",");
        if (id.length > 0) {
            for (String s : id) {
                userMapper.updateSignState(state,s);
            }
        } else {
            CommonResponseDto.ofFailure(Constants.ErrorCode.TEACHAR_NULL,"学员id为空");
        }
        return CommonResponseDto.ofSuccess();
    }
    /**
     * @Description:导出学员
     * @Date: 2018/3/14 16:47
     * @param: response
     * @param: courseUser 参数对象
     * @Author: wsc
     */
    @Override
    public void exportUser(HttpServletResponse response, HttpServletRequest request, CourseUser courseUser) throws Exception {
        log.info("courseUser:{}",courseUser.toString());
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.YYYY_MM_DD);
        String[] headers = { "姓名", "安利卡号","性别", "报名状态", "通过状态"};
        //学员id集合
        List <String> list = null;
        //导出数据集合
        List <RealityUser> userList;
        //根据课程id查询课程
        Course course = courseMapper.selectByPrimaryKey(courseUser.getCourseId());
        //学员id为空则导出全部学员
        if (!StringUtils.isEmpty(courseUser.getIds())) {
            list = new ArrayList <>();
            String[] id = courseUser.getIds().split(",");
            for (String s : id) {
                list.add(s);
            }
        }
        courseUser.setList(list);
        courseUser.setCurrentTime(new Date(System.currentTimeMillis()));
        log.info("courseUser:{}",courseUser);
        if (course.getLabel() != Constants.Number.INT_NUMBER3) {
            //导出线下课程
            userList = userMapper.selectExportUser(courseUser);
        } else {
            //判断该直播课程是否需要审核 1：需要 0：不需要
            if (Constants.Number.INT_NUMBER1 == course.getIsVerify()) {
                //导出直播课程(需审核)
                userList = userMapper.selectExportLiveUser(courseUser);
            } else {
                //导出线下课程(无需审核)
                userList = userMapper.selectExportUser(courseUser);
            }
        }
        log.info("userList:{}",userList);
        OutputStream out = response.getOutputStream();// 取得输出流
        Date dste = new Date();
        String tis = sdf.format(dste);
        tis = "报名学员" + tis + ".xls";
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-disposition", "attachment;filename=" + DownloadUtil.encodeDownloadFilename(tis,request));

        ExcelExportUtils.exportExcel(headers, userList, out);
    }
    /**
     * @Description:根据课程id查询课程关联的标签
     * @Date: 2018/4/11 11:23
     * @param: courseId
     * @Author: wsc
     */
    @Override
    public CommonResponseDto getLabelByCourseId(Integer courseId) {
        String label = courseMapper.getLabelByCourseId(courseId);
        log.info("label:{}",label);
        return CommonResponseDto.ofSuccess(label);
    }
    /**
     * @Description:获取excel导入完成进度
     * @Date: 2018/4/16 14:39
     * @Author: wsc
     */
    @Override
    public CommonResponseDto getCompletedProgress(Integer courseId,Integer userId) {
        Integer excelCount = Integer.parseInt(redisService.getValue(Constants.PERCENTAGE_EXCELCOUNT + courseId + "#" + userId).toString());
        Integer excelTotal = Integer.parseInt(redisService.getValue(Constants.PERCENTAGE_EXCELTOTAL + courseId + "#" + userId).toString());

        if (0 == excelCount && 0 == excelTotal) {
            return clearValue(courseId,userId);
        }
        if (excelCount == excelTotal && excelCount > 0 && excelTotal > 0) {
            return clearValue(courseId,userId);
        }
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(Constants.Number.INT_NUMBER2);
        double num = (float) excelCount / (float) excelTotal * 100;
        log.info("Percentage:{}",numberFormat.format(num) +"%");
        return CommonResponseDto.ofSuccess(numberFormat.format(num));
    }

    /**
     * @Description:更换Redis中课程列表的值
     * @Date: 2018/5/21 16:11
     * @param: courseId 课程id
     * @param: applyNum 报名数
     * @param: isAgree 是否已点赞：0-未点赞 1-已点赞
     * @param: agreeNum 点赞数
     * @Author: wsc
     */
    @Override
    public void updateRedisValue(Integer userId,Integer courseId, int applyNum, int agreeNum,String symbol,String applyStatus) {
        Object obj = redisService.getValue(Constants.COURSE_CACHE_KEY+courseId+"#"+userId);
        if (null != obj) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                CourseDto courseDto = objectMapper.readValue(obj.toString(),CourseDto.class);
                //判断点赞数
                if (agreeNum > Constants.Number.INT_NUMBER0) {
                    if (symbol.equals("add")) {
                        courseDto.setIsAgree(Constants.Number.INT_NUMBER1);
                    } else {
                        courseDto.setIsAgree(Constants.Number.INT_NUMBER0);
                    }
                }
                //报名状态
                if (!StringUtils.isEmpty(applyStatus)) {
                    courseDto.setApplyStatus(applyStatus);
                }
                redisService.set(Constants.COURSE_CACHE_KEY+courseId+"#"+userId, JSONObject.fromObject(courseDto).toString(),3600*24*5);
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }

    private CommonResponseDto clearValue(Integer courseId,Integer userId){
        redisComponent.set(Constants.PERCENTAGE_EXCELTOTAL + courseId + "#" + userId,Constants.Number.INT_NUMBER0);
        redisComponent.set(Constants.PERCENTAGE_EXCELCOUNT + courseId + "#" + userId,Constants.Number.INT_NUMBER0);
        return CommonResponseDto.ofSuccess(0);
    }
    /**
     * @Description: 上传excel文件到临时目录后并开始解析
     * @Date: 2018/3/13 9:42
     * @param: file Excel
     * @param: fileName 文件名称
     * @param: commonResponseDto 返回对象
     * @Author: wsc
     */
    private List<User> temporary(MultipartFile file, String fileName, CommonResponseDto commonResponseDto, Integer courseId,Integer userId) throws ParseException, IOException {
        log.info("file:{},fileName:{}", file, fileName);
        //不存在文件夹时创建文件夹
        File fileDir = new File(localFolder);
        if(!fileDir.exists()){
            fileDir.mkdirs();
        }
        File uploadDir = new File(Constants.UPLOAD_URL);
        //创建一个目录 （它的路径名由当前 File 对象指定，包括任一必须的父路径。）
        if (!uploadDir.exists()) uploadDir.mkdirs();
        //新建一个文件
        File tempFile = new File(localFolder + new Date().getTime() + ".xlsx");
        //初始化输入流
        InputStream is;
        //将上传的文件写入新建的文件中
        file.transferTo(tempFile);
        //根据新建的文件实例化输入流
        is = new FileInputStream(tempFile);
        //根据版本选择创建Workbook的方式
        Workbook wb;
        //根据文件名判断文件是2003版本还是2007版本
        if (ExcelImportUtils.isExcel2007(fileName)) {
            wb = new XSSFWorkbook(is);
        } else {
            wb = new HSSFWorkbook(is);
        }
        //根据excel里面的内容读取并校验格式
        List<User> userList = readExcelValue(wb, tempFile, commonResponseDto,courseId,userId);

        if (!StringUtils.isEmpty(is)) {
            is.close();
        }
        return userList;
    }
    /**
     * @Description: 解析Excel里面的数据
     * @Date: 2018/3/13 9:42
     * @param: wb Excel版本
     * @param: tempFile Excel
     * @param: commonResponseDto 返回对象
     * @Author: wsc
     */
    private List<User> readExcelValue(Workbook wb, File tempFile, CommonResponseDto commonResponseDto, Integer courseId,Integer userId) throws ParseException, IOException {
        log.info("we:{},tempFile:{}", wb, tempFile);
        //得到第一个shell
        Sheet sheet = wb.getSheetAt(0);
        //得到Excel的行数
        int totalRows = sheet.getPhysicalNumberOfRows();
        //根据课程id查询课程详情
        Course course = courseMapper.selectByPrimaryKey(courseId);
        //总列数
        int totalCells = 0;
        //得到Excel的列数(前提是有行数)，从第二行算起
        if (totalRows >= 2 && sheet.getRow(1) != null) {
            totalCells = sheet.getRow(1).getLastCellNum ();
        }
        //校验Excel表头和人数
        //19 checkExcelTitleAndNum(sheet,course,totalRows,totalCells);
        List <User> userList = new ArrayList <>();
        User user;
        //循环Excel行数,从第二行开始。标题不入库
        for (int r = 1; r < totalRows; r++) {
            log.info("rows:{}",r);
            Row row = sheet.getRow(r);
            //校验整行是否为空
            if (null == row || !ExcelImportUtils.checkCellIsNull(totalCells,row) ) {
                continue;
            }
            user = new User();
            //循环Excel的列
            for (int c = 0; c < totalCells; c++) {
                Cell cell = row.getCell(c);
                //校验Excel每行列中值的格式是否正确
                checkCells(cell, c, user, r);
            }
            userList.add(user);
        }
        //删除文件 判断校验是否成功
        deleteFile(tempFile,commonResponseDto);
        return userList;
    }
    //校验Excel表头和人数
    private void checkExcelTitleAndNum(Sheet sheet, Course course, int totalRows,int totalCells) {
        //校验第一行表头是否合格
        String[] header = {"分组标示","安利卡号","姓名","是否法人","职级","区域","省份","城市","店铺","性别","身份证号","手机号","身份类型","地址","邮编"};
        if (!ExcelImportUtils.checkExcelTitle(sheet.getRow(0),header)) {
            throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"EXCEL格式错误，请导入正确模板");
        }
        int index = 0;
        //循环Excel行数,从第二行开始。标题不入库
        for (int r = 1; r < totalRows; r++) {
            Row row = sheet.getRow(r);
            //校验整行是否为空
            if (null == row || !ExcelImportUtils.checkCellIsNull(totalCells, row)) {
                continue;
            }
            index++;
        }
        //校验人数
        if (!checkMaxNum(course,index)) {
            throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"该课程有报名人数限制，人数限制:"+course.getMaxApplyNum()+"人");
        }
    }
    //删除文件 判断校验是否成功
    private void deleteFile(File tempFile, CommonResponseDto commonResponseDto) throws IOException {
        //删除上传的临时文件
        if (tempFile.exists()) {
            Files.delete(tempFile.toPath());
        }
        commonResponseDto.setCode(Constants.ErrorCode.SUCCESS_CODE);
        commonResponseDto.setMessage("success");
    }
    //校验直播课程最大人数是否超出excel的上传人数
    private boolean checkMaxNum(Course course, int totalRows){
        boolean flag = true;
        //判断是否是直播课程 判断人数是否大于Excel行数
        if (!ObjectUtils.isEmpty(course) && null != course.getMaxApplyNum() && course.getMaxApplyNum() > 0 && course.getMaxApplyNum() <= totalRows) {
            //查询已报名成功的人数
            int count = courseApprovalMapper.selectAppreovalCountByCourseId(course.getSysId());
            //最大报名人数 - 已成功报名人数 <= Excel传入过来的人数
            if ((course.getMaxApplyNum() - count) < totalRows) {
                flag = false;
            }
        }
        return flag;
    }


    /**
     * @Description: 校验Excel每行列中值的格式是否正确
     * @Date: 2018/3/13 9:43
     * @param: cell Excel列
     * @param: c 每列
     * @param: commonResponseDto 返回对象
     * @param: user 用户对象
     * @param: r 每行
     * @Author: wsc
     */
    private void checkCells(Cell cell, int c,User user, int r) throws ParseException {
        if (null != cell) {
            if (c <= 7) {
                checkValue(c,cell,user);
            } else {
                checkExcelValue(c,cell,user);
            }
        }
    }

    private void checkExcelValue(int c, Cell cell, User user) throws ParseException {
        switch (c) {
            case 8:
                checkShop(cell,user);
                break;
            case 9:
                checkSex(cell,user);
                break;
            case 10:
                checkIdNumber(cell,user);
                break;
            case 11:
                checkPhone(cell,user);
                break;
            case 12:
                checkIdentityType(cell,user);
                break;
            case 13:
                checkAddress(cell,user);
                break;
            case 14:
                checkZipCode(cell,user);
                break;
            default:
                break;
        }
    }

    private void checkValue(int c, Cell cell, User user) {
        switch (c) {
            case 0:
                checkGrouping(cell,user);
                break;
            case 1:
                checkAdaNumber(cell,user);
                break;
            case 2:
                checkName(cell,user);
                break;
            case 3:
                checkIsLegalperson(cell,user);
                break;
            case 4:
                checkVocationalLevel(cell,user);
                break;
            case 5:
                checkRegion(cell,user);
                break;
            case 6:
                checkProvince(cell,user);
                break;
            case 7:
                checkCity(cell,user);
                break;
            default:
                break;
        }
    }

    //校验邮编
    private void checkZipCode(Cell cell, User user) {
        String zipCode = ExcelImportUtils.getValueByType(cell);
        Pattern pattern = Pattern.compile("[0-9]{1,}");
        Matcher aMatcher = pattern.matcher(zipCode);
        if (!StringUtils.isEmpty(zipCode)) {
            if (zipCode.length() > 16) {
                throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"邮编的字数不能超过16位");
            } else if (!aMatcher.matches()){
                throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"邮编只能是数字");
            }  else {
                user.setZipCode(zipCode);
            }
        }
    }

    //校验地址
    private void checkAddress(Cell cell, User user) {
        String address = ExcelImportUtils.getValueByType(cell);
        if (!StringUtils.isEmpty(address)) {
            if (address.length() > 120) {
                throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"地址的字数不能超过120位");
            } else {
                user.setAddress(address);
            }
        }
    }

    //校验身份类型
    private void checkIdentityType(Cell cell, User user) {
        String identityType = ExcelImportUtils.getValueByType(cell);
        if (!StringUtils.isEmpty(identityType)) {
            if (identityType.length() > 16) {
                throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"身份类型的字数不能超过16位");
            } else {
                user.setIdentityType(identityType);
            }
        }
    }

    //校验手机号
    private void checkPhone(Cell cell, User user) {
        String phone = ExcelImportUtils.getValueByType(cell);
        if (!StringUtils.isEmpty(phone)) {
            if (phone.length() > 11) {
                throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"手机号的字数不能超过11位");
            } else if (!CheckUtils.isPhoneLegal(phone)) {
                throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"手机号格式不正确");
            } else {
                user.setPhone(phone);
            }
        }
    }

    //校验身份证
    private void checkIdNumber(Cell cell, User user) throws ParseException {
        String idNumber = ExcelImportUtils.getValueByType(cell);
        if (!StringUtils.isEmpty(idNumber)) {
            if (idNumber.length() > 18) {
                throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"身份证号的字数不能超过18位");
            } else if (!IDCardUtils.iDCardValidate(idNumber)) {
                throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"身份证号的格式不正确");
            } else {
                user.setIdNumber(idNumber);
            }
        }
    }

    //校验性别
    @Override
    public void checkSex(Cell cell, User user) {
        String sex = ExcelImportUtils.getValueByType(cell);
        if (StringUtils.isEmpty(sex)) {
            throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"性别不能为空");
        } else if (sex.length() > 1) {
            throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"性别只能输入: 男/女");
        } else if (Constants.Sex.MAN.equals(sex) || Constants.Sex.WOMEN.equals(sex)) {
            if (Constants.Sex.MAN.equals(sex)) {
                user.setSex(Constants.Number.SHORT_NUMBER0);
            } else {
                user.setSex(Constants.Number.SHORT_NUMBER1);
            }
        } else {
            throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"性别只能输入: 男/女");
        }
    }

    //校验店铺
    private void checkShop(Cell cell, User user) {
        String shop = ExcelImportUtils.getValueByType(cell);
        if (!StringUtils.isEmpty(shop)) {
            if (shop.length() > 32) {
                throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"店铺的字数不能超过32位");
            } else {
                user.setShop(shop);
            }
        }
    }

    //校验城市
    private void checkCity(Cell cell, User user) {
        String city = ExcelImportUtils.getValueByType(cell);
        if (!StringUtils.isEmpty(city)) {
            if (city.length() > 16) {
                throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"城市的字数不能超过16位");
            } else {
                //根据城市名称查询相应的code
                Addr cityAddr = addrMapper.selectAddrByName(city, Constants.Number.INT_NUMBER2);
                if (!ObjectUtils.isEmpty(cityAddr)) {
                    user.setCityCode(cityAddr.getCode());
                }
            }
        }
    }

    //校验省份
    private void checkProvince(Cell cell, User user) {
        String province = ExcelImportUtils.getValueByType(cell);
        if (!StringUtils.isEmpty(province)) {
            if (province.length() > 16) {
                throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"省份的字数不能超过16位");
            } else {
                //根据省份名称查询出相应的code
                Addr provAddr = addrMapper.selectAddrByName(province, Constants.Number.INT_NUMBER1);
                if (!ObjectUtils.isEmpty(provAddr)) {
                    user.setProvCode(provAddr.getCode());
                }
            }
        }
    }

    //校验区域
    private void checkRegion(Cell cell, User user) {
        String region = ExcelImportUtils.getValueByType(cell);
        if (!StringUtils.isEmpty(region)) {
            if (region.length() > 16) {
                throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"区域的字数不能超过16位");
            } else {
                //根据省份名称查询出相应的code
                Addr provAddr = addrMapper.selectAddrByName(region, Constants.Number.INT_NUMBER3);
                if (!ObjectUtils.isEmpty(provAddr)) {
                    user.setRegionCode(provAddr.getCode());
                }
            }
        }
    }

    //校验职业
    private void checkVocationalLevel(Cell cell, User user) {
        String vocationalLevel = ExcelImportUtils.getValueByType(cell);
        if (!StringUtils.isEmpty(vocationalLevel)) {
            if (vocationalLevel.length() > 16) {
                throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"职业的字数不能超过16位");
            } else {
                user.setVocationalLevel(vocationalLevel);
            }
        }
    }

    private void checkIsLegalperson(Cell cell, User user) {
        String isLegalperson = ExcelImportUtils.getValueByType(cell);
        if (!StringUtils.isEmpty(isLegalperson)) {
            if (isLegalperson.length() > 1) {
                throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"是否法人的字数不能超过1位");
            } else if (Constants.IsLegalperson.YES.equals(isLegalperson) || Constants.IsLegalperson.NO.equals(isLegalperson)) {
                if (Constants.IsLegalperson.YES.equals(isLegalperson)) {
                    user.setIsLegalperson(Constants.Number.SHORT_NUMBER1);
                } else {
                    user.setIsLegalperson(Constants.Number.SHORT_NUMBER0);
                }
            } else {
                throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"是否法人只能输入：是/否");
            }
        }
    }

    //校验姓名
    @Override
    public void checkName(Cell cell, User user) {
        String name = ExcelImportUtils.getValueByType(cell);
        if (StringUtils.isEmpty(name)) {
            throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"姓名不能为空");
        } else if (name.length() > 25) {
            throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"姓名的字数不能超过25位");
        } else {
            user.setName(name);
        }
    }

    //校验卡号
    @Override
    public void checkAdaNumber(Cell cell, User user) {
        String adaNumber = ExcelImportUtils.getValueByType(cell);
        Pattern pattern = Pattern.compile("[0-9]{1,}");
        Matcher aMatcher = pattern.matcher(adaNumber);
        if (StringUtils.isEmpty(adaNumber)) {
            throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"安利用户卡号不能为空");
        } else if (adaNumber.length() > 14) {
            throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"安利用户卡号不能超过14位");
        } else if (!aMatcher.matches()){
            throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"安利用户卡号只能是数字");
        } else {
            user.setAdaNumber(adaNumber);
        }
    }

    /**
     * @Description: 获取excel导入完成数量
     * @Date: 2018/8/28 10:49
     * @param: courseId
     * @param: request
     * @Author: wsc
     */
    @Override
    public CommonResponseDto getSuccessCount(Integer courseId, Integer userId) {
        Integer successCount = Integer.parseInt(redisService.getValue(Constants.STUDENT_COUNT + courseId + "#" + userId).toString());
        Integer successTotal = Integer.parseInt(redisService.getValue(Constants.STUDENT_TOTAL + courseId + "#" + userId).toString());
        if (null != successTotal && null != successCount && successCount < successTotal) {
            return CommonResponseDto.ofSuccess(successCount + "/" + successTotal);
        } else {
            return CommonResponseDto.ofSuccess();
        }
    }

    //校验分组提示
    private void checkGrouping(Cell cell, User user) {
        //根据cell类型获取值
        String grouping = ExcelImportUtils.getValueByType(cell);
        if (!StringUtils.isEmpty(grouping)) {
            if (grouping.length() > 16) {
                throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"分组提示的字数不能超过16位");
            } else {
                user.setGrouping(grouping);
            }
        }
    }

    /**
     * @Description: 校验Excel文件格式
     * @Date: 2018/3/13 9:44
     * @param: file Excel
     * @param: fileName 文件名称
     * @Author: wsc
     */
    private String checkExcel(MultipartFile file, String fileName) {
        log.info("file:{},fileName:{}", file, fileName);
        //判断文件是否为空
        if (file == null) {
            return "文件不能为空!";
        }
        //验证文件名是否合格
        if (!ExcelImportUtils.validateExcel(fileName)) {
            return "文件必须是excel格式！";
        }
        //进一步判断文件内容是否为空（即判断其大小是否为0或其名称是否为null）
        long size = file.getSize();
        if (StringUtils.isEmpty(fileName) || size == 0) {
            return "文件不能为空！";
        }
        return null;
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
        boolean flag = false;
        boolean flagScore = false;
        boolean code = false;
        for (String userId : ids.split(",")) {
            Integer uid = Integer.parseInt(userId);
            User user = userMapper.selectByPrimaryKey(uid);

            //签到后不可以删除
            if (null != courseRegisterMapper.selectByUserAndCourse(uid, courseId)) {
                resultList.add(user.getName() + "-学员已签到,不能刪除.");
                continue;
            }
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
            //已提交评分不能删除
            if (scoreAnswerMapper.selectCountByUserIdAndCourseId(courseId, uid) > 0) {
                resultList.add(user.getName() + "-学员已提交评分,不能刪除.");
                flagScore = true;
                continue;
            }
            //已被评分不能删除
            if (scoreAnswerMapper.selectCountByStuIdAndCourseId(courseId, uid) > 0) {
                resultList.add(user.getName() + "-学员已被评分,不能刪除.");
                flagScore = true;
                continue;
            }
            //已发布证书不能删除
            if (userCertMapper.selectCountByCourseIdAndUserId(courseId, uid) > 0) {
                resultList.add(user.getName() + "-学员已颁发证书,不能刪除.");
                continue;
            }
            //删除已报名学员
            courseSignUpMapper.deleteByCourseAndAdainfoMd5(courseId, user.getAdainfoMd5());
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
            flag = true;
            //删除证书
            userCertMapper.delByCourseIdAndUid(courseId, uid);
        }
        //课程下只要有学员被评分或者已评分就不能删除班级，抽取等信息
        if(flag && !flagScore){
            //删除课程下的评分权重设置
            weightMapper.deleteWeightByCourseId(courseId);
            //删除课程下所有的评分成绩表
            speeMarkMapper.deleteByCourseId(courseId);
            //删除课程下所有的评分记录表
            scoreResultMapper.deleteByCourseId(courseId);
            //删除课程下所有的评分答案表
            scoreAnswerMapper.deleteByCourseId(courseId);
            //删除课程下班级的讲师
            courseTeacherMapper.deleteByCourseId(courseId);

        }
        if (code) {
            return CommonResponseDto.ofSuccess(resultList);
        } else {
            return CommonResponseDto.ofFailure(Constants.ErrorCode.TEACHAR_NULL,"",resultList);
        }
    }

    /**
     * @Description:删除班级表和演讲表
     * @Date: 2018/8/31 10:44
     * @param: ids 用户id
     * @param: courseId 课程id
     * @param: type 0：删除全部 1：剔除删除的用户
     * @Author: wsc
     */
    @Override
    public CommonResponseDto deleteClassAndDraw(String ids, Integer courseId, Integer type) {
        try {
            if (Constants.Number.INT_NUMBER0 == type) {
                //删除课程下所有的班级
                mclassMapper.deleteClassByCourseId(courseId);
                //删除课程下所有抽签信息
                drawResultMapper.deleteByCourseId(courseId);
                //删除课程下所有演讲内容
                classDrawMapper.deleteClassDrawByCourseId(courseId);
            } else if (Constants.Number.INT_NUMBER1 == type) {
                for (String userId : ids.split(",")) {
                    //修改班级人数
                    mclassMapper.updateMClassById(courseId,userId);
                    //删除班级里当前学员
                    drawResultMapper.deleteByUserIdAndCourseId(courseId,Integer.parseInt(userId));
                }
            }
            return CommonResponseDto.ofSuccess();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return CommonResponseDto.ofFailure(Constants.ErrorCode.TEACHAR_NULL,"deleteClassAndDraw Erroe");
    }

    /**
     * @Description:根据课程id查询是否分班
     * @Date: 2018/8/31 11:05
     * @param: courseId
     * @Author: wsc
     */
    @Override
    public CommonResponseDto checkClass(Integer courseId) {
        List<Mclass> mclassList = mclassMapper.selectByCourseId(courseId);
        if (null != mclassList && !mclassList.isEmpty()) {
            return CommonResponseDto.ofSuccess();
        } else {
            return CommonResponseDto.ofFailure(Constants.ErrorCode.TEACHAR_NULL,"");
        }
    }
}
