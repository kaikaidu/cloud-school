package com.amway.acti.service.impl;

import com.amway.acti.base.redis.RedisComponent;
import com.amway.acti.base.exception.AmwayLogicException;
import com.amway.acti.base.util.*;
import com.amway.acti.dao.CertCustomMapper;
import com.amway.acti.dao.CertMapper;
import com.amway.acti.dao.CertTempCustomMapper;
import com.amway.acti.dao.CertTempMapper;
import com.amway.acti.dao.CourseMapper;
import com.amway.acti.dao.MessageMapper;
import com.amway.acti.dao.UserCertCustomMapper;
import com.amway.acti.dao.UserCertMapper;
import com.amway.acti.dao.UserMapper;
import com.amway.acti.dto.CertDto;
import com.amway.acti.dto.CertParamDto;
import com.amway.acti.dto.CertStateDto;
import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.dto.UploadFailUserDto;
import com.amway.acti.dto.UploadInfoDto;
import com.amway.acti.dto.UserCertExportDto;
import com.amway.acti.model.Cert;
import com.amway.acti.model.CertTemp;
import com.amway.acti.model.Course;
import com.amway.acti.model.ExcelCustom;
import com.amway.acti.model.Message;
import com.amway.acti.model.User;
import com.amway.acti.model.UserCert;
import com.amway.acti.model.UserCertCustom;
import com.amway.acti.service.BackendUserCertService;
import com.amway.acti.service.RealityServer;
import com.amway.acti.service.RedisService;
import com.amway.acti.service.UserService;
import com.amway.acti.transform.UserCertTransform;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.microsoft.azure.storage.StorageException;
import gui.ava.html.image.generator.HtmlImageGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
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
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description:证书设置
 * @Date:2018/6/5 19:13
 * @Author:wsc
 */
@Slf4j
@Service
public class BackendUserCertServiceImpl implements BackendUserCertService {

    @Autowired
    private CertTempCustomMapper certTempCustomMapper;

    @Autowired
    private CertCustomMapper certCustomMapper;

    @Autowired
    private CertMapper certMapper;

    @Autowired
    private UserCertCustomMapper userCertCustomMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private UserCertTransform userCertTransform;

    @Autowired
    private UserMapper userMapper;

    @Value("${localFolder.url}")
    private String localFolder;

    @Autowired
    private AzureBlobUploadUtil azureBlobUploadUtil;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private RealityServer realityServer;

    @Autowired
    private UserCertMapper userCertMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private CertTempMapper certTempMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private RedisComponent redisComponent;

    //线程安全的计数器
    private static AtomicInteger succeNum = new AtomicInteger(Constants.Number.INT_NUMBER0);
    private static AtomicInteger failNum = new AtomicInteger(Constants.Number.INT_NUMBER0);

    /**
     * @Description:查询证书模板列表
     * @Date: 2018/6/5 19:10
     * @param: pageNo 当前页
     * @param: pageSize 每页显示数量
     * @param: timeSort 正序：asc 倒序：desc 默认倒序
     * @param: name 证书模板名称
     * @Author: wsc
     */
    @Override
    public PageInfo<CertTemp> findCertTempList(Integer pageNo,
                                               Integer pageSize,
                                               String timeSort,
                                               String name) {
        log.info("pageNo:{},pageSize:{},timeSort:{},name:{}",pageNo,pageSize,timeSort,name);
        PageHelper.startPage(pageNo,pageSize);
        List<CertTemp> certTemps = certTempCustomMapper.findCertTempList(timeSort,name);
        log.info("certTemps:{}",certTemps);
        return new PageInfo <>(certTemps);
    }

    /**
     * @Description:新增证书/修改证书
     * @Date: 2018/6/6 14:46
     * @param: cert 证书信息
     * @Author: wsc
     */
    @Override
    @Transactional
    public CommonResponseDto addOrUpdateCert(CertDto certDto,User user) {
        log.info("certDto:{}",certDto.toString());
        Cert cert = new Cert();
        cert.setName(certDto.getName());//证书名称
        cert.setUrl(certDto.getUrl());  //证书模板预览地址
        cert.setCertTempId(certDto.getCertTempId());//模板id
        cert.setCourseId(certDto.getCourseId());    //课程id
        //判断是新增还是修改
        if (null != certDto.getCertId()) {
            Cert c = certMapper.selectByPrimaryKey(certDto.getCertId());
            if (ObjectUtils.isEmpty(c)) {
                redisComponent.del(Constants.UPDATE_CERT + certDto.getCourseId());
                throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"未查询到证书!");
            }
            //获取证书模板信息-原数据
            CertTemp certTemp1 = certTempCustomMapper.selectCertTempByCourseId(certDto.getCourseId());
            //修改课程关联证书
            cert.setId(certDto.getCertId());//证书id
            //判断是否更改了证书模板
            if (certTemp1.getId() == certDto.getCertTempId()) {
                certMapper.updateByPrimaryKeySelective(cert);
                //设置消息信息
                List<Message> messages = messageMapper.selectMessageById(certDto.getCourseId());
                if (null != messages && !messages.isEmpty()) {
                    for (Message m : messages) {
                        m.setTitle(certDto.getName() + " 可以领取啦!");
                    }
                    messageMapper.updateList(messages);
                }
                userCertCustomMapper.updateUserCertByCourseIdForName(certDto.getCourseId(),certDto.getName());
            } else {
                //判断是否有学员已颁发过证书，批量修改学员证书
                updateUserCert(certDto,cert,certTemp1,user);
            }
            log.info("update-->cert:{}",cert);
        } else {
            //根据课程id查询是否存在
            Cert c = certCustomMapper.selectCertByCourseId(certDto.getCourseId());
            if (!ObjectUtils.isEmpty(c)) {
                return CommonResponseDto.ofFailure(Constants.ErrorCode.TEACHAR_NULL,"请勿重复添加证书模板!");
            }

            cert.setCreateTime(new Date());//创建时间
            certMapper.insertSelective(cert);

            //批量修改学员证书
            userCertCustomMapper.updateUserCertByCourseId(certDto.getCourseId(),cert.getId(),cert.getName());
            log.info("innert-->cert:{}",cert);
        }
        return CommonResponseDto.ofSuccess();
    }

    /**
     * @Description:查询课程关联证书
     * @Date: 2018/6/6 15:15
     * @param: courseId 课程id
     * @Author: wsc
     */
    @Override
    public Cert findCourseCert(Integer courseId) {
        log.info("courseId:{}",courseId);
        Course c = courseMapper.selectByPrimaryKey(courseId);
        if (ObjectUtils.isEmpty(c)) {
            throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"课程不存在!");
        }
        return certCustomMapper.selectCertByCourseId(courseId);
    }

    /**
     * @Description:删除课程证书
     * @Date: 2018/6/6 16:36
     * @param: certId 证书id
     * @param: courseId 课程id
     * @Author: wsc
     */
    @Override
    @Transactional
    public CommonResponseDto deleteCourseCert(Integer certId) {
        log.info("certId:{}",certId);
        Cert cert = certMapper.selectByPrimaryKey(certId);
        if (ObjectUtils.isEmpty(cert)) {
            throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"未查询到证书!");
        }
        //删除课程证书
        certMapper.deleteByPrimaryKey(certId);
        //修改课程证书已关联的学员
        userCertCustomMapper.upUserCertByCourseId(cert.getCourseId());
        //删除消息
        messageMapper.deleteMessageByCourseIdAndUserId(cert.getCourseId(),null,Constants.Number.INT_NUMBER1);
        return CommonResponseDto.ofSuccess();
    }

    /**
     * @Description:查询课程证书详情
     * @Date: 2018/6/6 17:02
     * @param: certId 证书id
     * @Author: wsc
     */
    @Override
    public Cert findCertById(Integer certId) {
        log.info("certId:{}",certId);
        Cert cert = certMapper.selectByPrimaryKey(certId);
        if (ObjectUtils.isEmpty(cert)) {
            throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"未查询到证书!");
        }
        return cert;
    }

    /**
     * @Description:查询学员证书列表
     * @Date: 2018/6/6 19:25
     * @param: certParamDto 条件
     * @Author: wsc
     */
    @Override
    public PageInfo<UserCertCustom> findUserCertList(CertParamDto certParamDto) {
        log.info("certParamDto:{}",certParamDto.toString());
        PageHelper.startPage(certParamDto.getPageNo(),certParamDto.getPageSize());
        List<UserCertCustom> userCertCustomList = userCertCustomMapper.selectUserCert(certParamDto,null);
        return new PageInfo <>(userCertCustomList);
    }

    /**
     * @Description:查询学员证书列表
     * @Date: 2018/6/6 19:25
     * @param: certParamDto 条件
     * @Author: wsc
     */
    @Override
    public Integer findUserCertListCount(Integer courseId) {
        return userCertCustomMapper.selectUserCertCount(courseId);
    }

    /**
     * @Description:批量导出证书设置
     * @Date: 2018/6/7 9:54
     * @param: courseId 课程ID
     * @param: userIds 学员id数组，为空导出全部
     * @param: request 请求
     * @param: response 响应
     * @Author: wsc
     */
    @Override
    public void exportUserCert(Integer courseId, String userIds, HttpServletRequest request, HttpServletResponse response) throws ReflectiveOperationException, IOException {
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.YYYY_MM_DD);
        String[] headers = { "姓名", "安利卡号","性别", "报名状态", "通过状态","证书状态"};
        CertParamDto certParamDto = new CertParamDto();
        certParamDto.setCourseId(courseId);
        String[] uIds = null;
        if (!StringUtils.isEmpty(userIds)) {
            uIds = userIds.split(",");
        }
        //获取课程信息
        Course course = courseMapper.selectByPrimaryKey(courseId);
        if (ObjectUtils.isEmpty(course)) {
            throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"课程不存在!");
        }
        //原始数据
        List<UserCertCustom> userCertCustomList = userCertCustomMapper.selectUserCert(certParamDto,uIds);
        //导出数据
        List<UserCertExportDto> userCertExportDtos = userCertTransform.transformUserCertExportDto(userCertCustomList);
        if (null == userCertExportDtos || userCertExportDtos.isEmpty()) {
            userCertExportDtos = new ArrayList <>();
        }
        OutputStream out = response.getOutputStream();// 取得输出流
        Date dste = new Date();
        String tis = sdf.format(dste);
        tis = "证书设置" + tis + ".xls";
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-disposition", "attachment;filename=" + DownloadUtil.encodeDownloadFilename(tis,request));

        ExcelExportUtils.exportExcel(headers, userCertExportDtos, out);
    }

    /**
     * @Description:修改证书颁发状态
     * @Date: 2018/6/7 11:04
     * @param: userIds 学员ID数组
     * @param: courseId 课程ID
     * @param: state 修改状态[0:未颁发 1：已颁发]
     * @Author: wsc
     */
    @Override
    public CommonResponseDto updateCertState(CertStateDto certStateDto) {
        log.info("certStateDto:{}",certStateDto);
        //获取证书模板信息
        CertTemp certTemp = certTempCustomMapper.selectCertTempByCourseId(certStateDto.getCourseId());
        log.info("certTemp:{}",certTemp);
        if (!ObjectUtils.isEmpty(certTemp)) {
            List<UserCert> userCertList = null;
            if (certStateDto.getState() == Constants.Number.INT_NUMBER1) {
                try {
                    userCertList = insertUserCert(certTemp,certStateDto);
                } catch (InterruptedException e) {
                    log.error(e.getMessage());
                }
            } else {
                userCertList = deleteUserCert(certStateDto);
            }

            //修改证书颁发状态或证书预览地址
            userCertCustomMapper.updateUserCertByList(userCertList);
        } else {
            return CommonResponseDto.ofFailure(Constants.ErrorCode.TEACHAR_NULL,"请先关联证书");
        }
        return CommonResponseDto.ofSuccess();
    }

    /**
     * @Description:上传证书名单
     * @Date: 2018/6/7 16:23
     * @param: file Execl文件
     * @param: courseId 课程ID
     * @Author: wsc
     */
    @Override
    public CommonResponseDto uploadUserCert(MultipartFile file, Integer courseId,User u) throws IOException {
        if (redisComponent.hasKey(Constants.UPDATE_COURSE_CERT + courseId)) {
            String courseCert = redisService.getValue(Constants.UPDATE_COURSE_CERT + courseId).toString();
            if (!StringUtils.isEmpty(courseCert)) {
                if (courseCert.equals(Constants.Number.STRING_NUMBER1)) {
                    throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"该课程已有用户正在颁发证书，请稍后再试!");
                }
            }
        }
        //判断该课程下是否有用户正在修改证书模板
        if (redisComponent.hasKey(Constants.UPDATE_CERT + courseId)) {
            String courseCert = redisService.getValue(Constants.UPDATE_CERT + courseId).toString();
            if (!StringUtils.isEmpty(courseCert)) {
                if (courseCert.equals(Constants.Number.STRING_NUMBER1)) {
                    throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"该课程有用户修改了证书模板，正在重新生成证书，请稍后再试!");
                }
            }
        }

        Course course = courseMapper.selectByPrimaryKey(courseId);
        if (course.getIsShelve() == Constants.Number.SHORT_NUMBER0) {
            redisComponent.del(Constants.UPDATE_COURSE_CERT + courseId);
            throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"课程未上架，不能颁发证书!");
        }
        //获取证书模板信息
        CertTemp certTemp = certTempCustomMapper.selectCertTempByCourseId(courseId);
        if (ObjectUtils.isEmpty(certTemp)) {
            redisComponent.del(Constants.UPDATE_COURSE_CERT + courseId);
            throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"未获取到证书模板!");
        }

        //往缓存中存放标识，防止修改课程证书时数据混乱
        redisComponent.set(Constants.UPDATE_COURSE_CERT + courseId,Constants.Number.INT_NUMBER1);

        //获取导入必要的参数
        ExcelCustom excelCustom = ExcelImportUtils.getSheet(localFolder,file);
        //校验Excel表头
        checkExcelTitleAndNum(excelCustom);
        User user;
        List<User> userList = new ArrayList <>();
        //循环Excel行数,从第二行开始。标题不入库
        for (int r = 1; r < excelCustom.getTotalRows(); r++) {
            Row row = excelCustom.getSheet().getRow(r);
            //校验整行是否为空
            if (null == row || !ExcelImportUtils.checkCellIsNull(excelCustom.getTotalCells(),row) ) {
                continue;
            }
            user = new User();
            //循环Excel的列
            for (int c = 0; c < excelCustom.getTotalCells(); c++) {
                Cell cell = row.getCell(c);
                //校验Excel每行列中值的格式是否正确
                checkCells(cell, c, user);
            }
            userList.add(user);
        }
        //删除上传的临时文件
        if (excelCustom.getFile().exists()) {
            Files.delete(excelCustom.getFile().toPath());
        }
        //关闭流
        if (!StringUtils.isEmpty(excelCustom.getIs())) {
            excelCustom.getIs().close();
        }
        //记录需要生成证书的总数
        redisComponent.set(Constants.UPDATE_CERT_TOTAL + courseId + "#" + u.getId() ,userList.size());

        //批量生成证书 修改状态
        return createCertUpdateCertState(userList,courseId,course,certTemp,u.getId());
    }

    /**
     * @Description: 获取完成数量
     * @Date: 2018/8/29 11:20
     * @param: courseId
     * @param: request
     * @Author: wsc
     */
    @Override
    public CommonResponseDto getCertSuccCount(Integer courseId, Integer id) {
        Integer successCount = Integer.parseInt(redisService.getValue(Constants.UPDATE_CERT_COUNT + courseId + "#" + id).toString());
        Integer successTotal = Integer.parseInt(redisService.getValue(Constants.UPDATE_CERT_TOTAL + courseId + "#" + id).toString());
        if (null != successTotal && null != successCount && successCount < successTotal) {
            return CommonResponseDto.ofSuccess(successCount + "/" + successTotal);
        } else {
            return CommonResponseDto.ofSuccess();
        }
    }

    //批量生成证书 修改状态
    private CommonResponseDto createCertUpdateCertState(List<User> userList,Integer courseId,
                                                        Course course,CertTemp certTemp,Integer userId) {
        succeNum.set(Constants.Number.INT_NUMBER0);
        failNum.set(Constants.Number.INT_NUMBER0);
        UploadInfoDto uploadInfoDto = new UploadInfoDto();
        //获取证书模板html
        String tempHtml = HttpClientUtil.getJsonByInternet(certTemp.getHtmlUrl());
        //乱码转换
        tempHtml = tempHtml.replace(BackendCertTemplateImpl.HTML_TEM,"");
        List<UploadFailUserDto> uploadFailUserDtoList = new ArrayList <>();
        String basePath = getBasePath();
        //查询证书
        Cert cert = certCustomMapper.selectCertByCourseId(courseId);
        //创建可缓存线程池
        ExecutorService pool = Executors.newSingleThreadExecutor();
        long time1 = System.currentTimeMillis();
        log.info("--->Runnable-->start");
        UploadFailUserDto uploadFailUserDto = null;
        for (User u : userList) {
            String adaNumber = u.getAdaNumber();
            //判断卡号是否满足14位
            if (u.getAdaNumber().length() <= 14) {
                u.setAdaNumber(userService.completion(u.getAdaNumber(),u.getSex()));
            }
            //根据卡号，性别，性别 查询是否报名成功
            User user = userMapper.selectUserByNameAndSexAndAdaNum(u.getName(),u.getSex(),u.getAdaNumber(),courseId);
            if (!ObjectUtils.isEmpty(user)) {
                //成功的数量
                uploadInfoDto.setSucceNum(succeNum.addAndGet(Constants.Number.INT_NUMBER1));
                log.info("success_count:{}",succeNum.get());
            } else {
                uploadFailUserDto = new UploadFailUserDto();
                //失败的数量
                uploadInfoDto.setFailNum(failNum.addAndGet(Constants.Number.INT_NUMBER1));
                //失败人员信息
                uploadFailUserDto.setName(u.getName());
                uploadFailUserDto.setAdaNumber(adaNumber);
                uploadFailUserDto.setSex(u.getSex());
                uploadFailUserDtoList.add(uploadFailUserDto);
                log.info("failNum_count:{}",failNum.get());
            }
            uploadInfoDto.setUploadFailUserDtoList(uploadFailUserDtoList);
        }
        //创建一个异步子线程
        try {
            pool.execute(this.getUploadInfoDto(userList,courseId,tempHtml,basePath,course,cert,userId));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        log.info("--->CountDownLatch-->await");
        //关闭线程池
        pool.shutdown();
        long time2 = System.currentTimeMillis();
        log.info("--->Runnable-->end--------------time:{}",time2-time1);
        return CommonResponseDto.ofSuccess(uploadInfoDto);
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
    private void checkCells(Cell cell, int c,User user){
        switch (c) {
            case 0:
                realityServer.checkName(cell,user);
                break;
            case 1:
                checkAdaNumber(cell,user);
                break;
            case 2:
                realityServer.checkSex(cell,user);
                break;
        }
    }

    //校验卡号
    private void checkAdaNumber(Cell cell, User user) {
        String adaNumber = ExcelImportUtils.getValueByType(cell);
        Pattern pattern = Pattern.compile("[0-9]{1,}");
        Matcher aMatcher = pattern.matcher(adaNumber);
        if (StringUtils.isEmpty(adaNumber)) {
            throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"安利用户卡号不能为空");
        } else if (adaNumber.length() > 16) {
            throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"安利用户卡号不能超过16位");
        } else if (!aMatcher.matches()){
            throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"安利用户卡号只能是数字");
        } else {
            user.setAdaNumber(adaNumber);
        }
    }

    //设置消息
    private Message getMessage(Course course, Integer i, String url,Cert cert) {
        Message message = new Message();
        message.setCourseId(course.getSysId());
        message.setCreateTime(new Date());
        message.setIsRead(Constants.Number.INT_NUMBER0);
        message.setType(Constants.Number.INT_NUMBER1);
        message.setUserId(i);
        message.setContent(url);
        message.setTitle(cert.getName()+" 可以领取啦!");
        return message;
    }

    //生成证书 userId:用户id name：用户名称 tempHtml：模板html basePath：上传路径
    private String generateCert(User user,String tempHtml,String basePath,Course course) {
        //生成证书图片
        tempHtml = tempHtml.replace("%user_name%",user.getName()).
                replace("%gender%",user.getSex() == 0 ? Constants.Sex.MAN : Constants.Sex.WOMEN).
                replace("%activity_name%",course.getTitle());

        HtmlImageGenerator htmlImageGenerator = new HtmlImageGenerator();
        htmlImageGenerator.loadHtml(tempHtml);
        htmlImageGenerator.getBufferedImage();
        String certImg = basePath + "/" + System.currentTimeMillis() + user.getId() +".png";
        htmlImageGenerator.saveAsImage(certImg);

        //上传证书图片到云盘
        File f1 = new File(certImg);
        String url = null;
        try (InputStream in = new FileInputStream(f1)) {
            byte[] bb = new byte[in.available()];
            url = azureBlobUploadUtil.upload(in, f1.getName(), bb.length, "cert");
        } catch (StorageException |  IOException | URISyntaxException | InvalidKeyException e) {
            log.error(e.getMessage(), e);
        } finally {
            //删除文件
            try {
                Files.delete(f1.toPath());
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        return url;
    }

    //校验Excel表头
    private void checkExcelTitleAndNum(ExcelCustom excelCustom) {
        //校验第一行表头是否合格
        String[] header = {"姓名","安利卡号","性别"};
        if (!ExcelImportUtils.checkExcelTitle(excelCustom.getSheet().getRow(0),header)) {
            throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"EXCEL格式错误，请导入正确模板");
        }
    }

    private String getBasePath(){
        String basePath = localFolder + "/zip";
        File fileDir = new File(basePath);
        if(!fileDir.exists()){
            fileDir.mkdirs();
        }
        return basePath;
    }


    //批量修改学员证书
    private void updateUserCert(CertDto certDto,Cert cert,CertTemp certTemp1,User user) {
        //查询当前课程下是否有已颁发证书的学员
        List<UserCert> userCertList = userCertCustomMapper.findUserCertByCourseId(certDto.getCourseId());
        //修改课程证书
        certMapper.updateByPrimaryKeySelective(cert);
        if (null != userCertList && !userCertList.isEmpty()) {
            log.info("userCertList:{}",userCertList);
            //当前课程下有学员已颁发证书，则重新生成证书
            if (null != userCertList && !userCertList.isEmpty()) {
                //判断该课程下是否有用户正在上传证书
                if (redisComponent.hasKey(Constants.UPDATE_COURSE_CERT + certDto.getCourseId())) {
                    String courseCert = redisService.getValue(Constants.UPDATE_COURSE_CERT + certDto.getCourseId()).toString();
                    if (!StringUtils.isEmpty(courseCert)) {
                        if (courseCert.equals(Constants.Number.STRING_NUMBER1)) {
                            throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"该课程已有用户正在颁发证书，请稍后再试!");
                        }
                    }
                }
                //判断该课程下是否有用户正在修改证书模板
                if (redisComponent.hasKey(Constants.UPDATE_CERT + certDto.getCourseId())) {
                    String courseCert = redisService.getValue(Constants.UPDATE_CERT + certDto.getCourseId()).toString();
                    if (!StringUtils.isEmpty(courseCert)) {
                        if (courseCert.equals(Constants.Number.STRING_NUMBER1)) {
                            throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"该课程有用户修改了证书模板，正在重新生成证书，请稍后再试!");
                        }
                    }
                }
                //获取课程
                Course course = courseMapper.selectByPrimaryKey(certDto.getCourseId());
                if (course.getIsShelve() == Constants.Number.SHORT_NUMBER0) {
                    redisComponent.del(Constants.UPDATE_CERT + certDto.getCourseId());
                    throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"课程未上架，不能颁发证书!");
                }
                //防止数据混乱标识
                redisComponent.set(Constants.UPDATE_CERT + certDto.getCourseId(),Constants.Number.INT_NUMBER0);
                //记录需要修改的总数
                redisComponent.set(Constants.UPDATE_CERT_TOTAL + certDto.getCourseId() + "#" + user.getId(),userCertList.size());

                //获取证书模板信息-新
                CertTemp certTemp = certTempMapper.selectByPrimaryKey(certDto.getCertTempId());
                //获取证书模板html
                String tempHtml = HttpClientUtil.getJsonByInternet(certTemp.getHtmlUrl());
                //乱码转换
                tempHtml = tempHtml.replace(BackendCertTemplateImpl.HTML_TEM,"");

                String basePath = getBasePath();
                //创建线程池
                ExecutorService pool = Executors.newSingleThreadExecutor();
                try {
                    pool.execute(this.getUserCertThread(userCertList,tempHtml,basePath,course,certDto,certTemp,certTemp1,user.getId()));
                } catch (Exception e) {
                    log.error(e.getMessage());
                } finally {
                    //关闭线程池
                    pool.shutdown();
                }
            }
        } else {
            userCertCustomMapper.updateUserCertByCourseIdForName(certDto.getCourseId(),certDto.getName());
        }
    }

    private Runnable getUserCertThread(List<UserCert> userCertList, String tempHtml, String basePath,
                                       Course course, CertDto certDto,
                                       CertTemp certTemp,CertTemp certTemp1,Integer userId) {
        return () -> {
            //创建线程池
            ExecutorService pool1 = Executors.newSingleThreadExecutor();
            ExecutorService pool2 = Executors.newSingleThreadExecutor();
            ExecutorService pool3 = Executors.newSingleThreadExecutor();
            try {
                //记数
                CountDownLatch cdl = new CountDownLatch(userCertList.size());
                for (int i = 0; i < userCertList.size(); i++) {
                    pool1.execute(userCertThread(userCertList.get(i),certDto,tempHtml,basePath,course,certTemp,certTemp1,i,cdl,userId,userCertList.size()));
                    if (userCertList.size()-1 >= (i+1)) {
                        i += 1;
                        pool2.execute(userCertThread(userCertList.get(i),certDto,tempHtml,basePath,course,certTemp,certTemp1,i,cdl,userId,userCertList.size()));
                    }
                    if (userCertList.size()-1 >= (i+2)) {
                        i += 1;
                        pool3.execute(userCertThread(userCertList.get(i),certDto,tempHtml,basePath,course,certTemp,certTemp1,i,cdl,userId,userCertList.size()));
                    }
                }
                cdl.await();
                log.info("update_cert__end");
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            } finally {
                //修改缓存中的标识，用于判断是否可以修改课程证书,避免数据混乱
                redisComponent.del(Constants.UPDATE_CERT + certDto.getCourseId());
                redisComponent.del(Constants.UPDATE_CERT_TOTAL + certDto.getCourseId() + "#" + userId);
                redisComponent.del(Constants.UPDATE_CERT_COUNT + certDto.getCourseId() + "#" + userId);
                pool1.shutdown();
                pool2.shutdown();
                pool3.shutdown();
            }
        };
    }

    private Runnable userCertThread(UserCert userCert, CertDto certDto, String tempHtml,
                                    String basePath, Course course, CertTemp certTemp,
                                    CertTemp certTemp1, int i,CountDownLatch cdl,Integer userId,int length) {
        return () -> {
            try{
                log.info("i:{}",i);
                User user = userMapper.selectByPrimaryKey(userCert.getUserId());
                String url = generateCert(user,tempHtml,basePath,course);
                userCert.setUrl(url);
                userCert.setCertId(certDto.getCertId());
                userCert.setName(certDto.getName());
                userCert.setAwardTime(new Date());
                userCertCustomMapper.updateUserCertByBean(userCert);
                //如更改了证书模板
                if (certTemp.getId() != certTemp1.getId() || !certTemp.getName().equals(certTemp1.getName())) {
                    //设置消息信息
                    List<Message> messages = messageMapper.selectMessageByUserIdAndCourseId(user.getId(),course.getSysId());
                    if (null != messages && !messages.isEmpty()) {
                        Message message = messages.get(0);
                        message.setContent(url);
                        message.setTitle(certDto.getName() + " 可以领取啦!");
                        messageMapper.updateByPrimaryKeySelective(message);
                    }
                }
                //记录颁发成功的数量
                redisComponent.set(Constants.UPDATE_CERT_COUNT + certDto.getCourseId() + "#" + userId ,(length - cdl.getCount()));
                cdl.countDown();
            }catch (Exception e) {
                //修改缓存中的标识，用于判断是否可以修改课程证书,避免数据混乱
                redisComponent.del(Constants.UPDATE_CERT + certDto.getCourseId());
                log.error(e.getMessage());
            }
        };
    }

    //删除证书
    private List<UserCert> deleteUserCert(CertStateDto certStateDto) {
        List<UserCert> userCertList = new ArrayList <>();//学员证书集合
        UserCert userCert;//学员证书对象
        for (Integer i : certStateDto.getUserIds()) {
            userCert = new UserCert();
            //设置修改的通用信息
            userCert.setCourseId(certStateDto.getCourseId());
            userCert.setIsAward(certStateDto.getState());
            userCert.setUserId(i);
            userCert.setUrl(null);
            userCert.setAwardTime(null);
            userCertList.add(userCert);
            //删除消息
            messageMapper.deleteMessageByCourseIdAndUserId(certStateDto.getCourseId(),i,Constants.Number.INT_NUMBER1);
        }
        return userCertList;
    }

    //新增证书
    private List<UserCert> insertUserCert(CertTemp certTemp,CertStateDto certStateDto) throws InterruptedException {
        List<UserCert> userCertList = new ArrayList <>();//学员证书集合
        List<Message> messageList = new ArrayList <>();
        Course course = courseMapper.selectByPrimaryKey(certStateDto.getCourseId());
        if (course.getIsShelve() == Constants.Number.SHORT_NUMBER0) {
            throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"课程未上架，不能颁发证书!");
        }
        //获取证书模板html
        String tempHtml = HttpClientUtil.getJsonByInternet(certTemp.getHtmlUrl());
        //乱码转换
        tempHtml = tempHtml.replace(BackendCertTemplateImpl.HTML_TEM,"");
        Cert cert = certCustomMapper.selectCertByCourseId(certStateDto.getCourseId());
        String basePath = getBasePath();
        //创建可缓存线程池
        ExecutorService pool = Executors.newFixedThreadPool(50);
        //记数
        CountDownLatch cdl = new CountDownLatch(certStateDto.getUserIds().length);
        long time1 = System.currentTimeMillis();
        log.info("--->Runnable-->start");
        for (Integer i : certStateDto.getUserIds()) {
            try {
                pool.execute(this.getThread(i,certStateDto,tempHtml,course,basePath,cdl,cert,messageList,userCertList));
            } catch (Exception e) {
                log.error(e.getMessage());
                continue;
            }
        }
        log.info("--->CountDownLatch-->await");
        //主线程阻塞，等待子线程全部完成
        cdl.await();
        //关闭线程池
        pool.shutdown();
        long time2 = System.currentTimeMillis();
        log.info("--->Runnable-->end--------------time:{}",time2-time1);
        log.info("userCertList:{}",userCertList);
        if (null != messageList && !messageList.isEmpty()) {
            //新增消息
            messageMapper.insertList(messageList);
        }
        return userCertList;
    }

    private Runnable getThread(int i,CertStateDto certStateDto,String tempHtml,
                               Course course,String basePath,CountDownLatch cdl,
                               Cert cert,List<Message> messageList,List<UserCert> userCertList) {
        return () -> {
            UserCert userCert = new UserCert();//学员证书对象
            if (certStateDto.getState() == Constants.Number.INT_NUMBER1) {
                //获取用户信息
                User user = userMapper.selectByPrimaryKey(i);
                //生成证书 上传服务器
                String url = generateCert(user,tempHtml,basePath,course);
                userCert.setUrl(url);
                userCert.setAwardTime(new Date());
                //根据userid和课程id查询是否已有消息
                List<Message> messages = messageMapper.selectMessageByUserIdAndCourseId(user.getId(),course.getSysId());
                if (null == messages || messages.isEmpty()) {
                    //设置消息信息
                    Message message = getMessage(course,i,url,cert);
                    messageList.add(message);
                }
            }
            //设置修改的通用信息
            userCert.setCourseId(certStateDto.getCourseId());
            userCert.setIsAward(certStateDto.getState());
            userCert.setUserId(i);
            userCertList.add(userCert);
            cdl.countDown();
        };
    }

    //异步一个子线程
    private Runnable getUploadInfoDto(List<User> userList, Integer courseId, String tempHtml,String basePath,
                                      Course course,Cert cert,Integer userId){
        return () -> {
            //在子线程中创建子线程
            ExecutorService pool1 = Executors.newSingleThreadExecutor();
            ExecutorService pool2 = Executors.newSingleThreadExecutor();
            ExecutorService pool3 = Executors.newSingleThreadExecutor();
            //记数
            CountDownLatch cdl = new CountDownLatch(userList.size());
            for (int i = 0; i < userList.size(); i++) {
                pool1.execute(updateInfo(userList.get(i),courseId,tempHtml,basePath,course,cert,i,cdl,userId,userList.size()));
                if (userList.size()-1 >= (i+1)) {
                    i += 1;
                    pool2.execute(updateInfo(userList.get(i),courseId,tempHtml,basePath,course,cert,i,cdl,userId,userList.size()));
                }
                if (userList.size()-1 >= (i+2)) {
                    i += 1;
                    pool3.execute(updateInfo(userList.get(i),courseId,tempHtml,basePath,course,cert,i,cdl,userId,userList.size()));
                }
            }
            try {
                cdl.await();
                log.info("cdl.await__end");
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            } finally {
                log.info("redis_update_course_cert");
                //修改缓存中的标识，用于判断是否可以修改课程证书,避免数据混乱
                redisComponent.del(Constants.UPDATE_COURSE_CERT + courseId);
                redisComponent.del(Constants.UPDATE_CERT_COUNT + courseId + "#" + userId);
                redisComponent.del(Constants.UPDATE_CERT_TOTAL + courseId + "#" + userId);
                //关闭线程池
                pool1.shutdown();
                pool2.shutdown();
                pool3.shutdown();
            }
        };
    }

    //生成证书
    private Runnable updateInfo(User u, Integer courseId, String tempHtml,String basePath,
                                      Course course,Cert cert,int i,CountDownLatch cdl,Integer userId,int length){
        return () -> {
            try {
                log.info("i:{}",i);
                long time = System.currentTimeMillis();
                //判断卡号是否满足14位
                if (u.getAdaNumber().length() <= 14) {
                    u.setAdaNumber(userService.completion(u.getAdaNumber(),u.getSex()));
                }
                //根据卡号，性别，性别 查询是否报名成功
                User user = userMapper.selectUserByNameAndSexAndAdaNum(u.getName(),u.getSex(),u.getAdaNumber(),courseId);
                if (null == user || ObjectUtils.isEmpty(user)) {
                    cdl.countDown();
                    return;
                }
                UserCert userCert = userCertCustomMapper.selectUserCertByCourseIdAndUserId(courseId,user.getId());
                //判断是否已颁发过证书
                if (null != userCert && !StringUtils.isEmpty(userCert.getUrl())) {
                    userCert.setIsAward(Constants.Number.INT_NUMBER1);
                    userCert.setAwardTime(new Date());
                    userCertMapper.updateByPrimaryKeySelective(userCert);
                    log.info("return--isAward:{}",System.currentTimeMillis() - time);
                    cdl.countDown();
                    return;
                }
                //生成证书 上传服务器
                String url = generateCert(user,tempHtml,basePath,course);
                userCert.setUrl(url);
                userCert.setIsAward(Constants.Number.INT_NUMBER1);
                userCert.setAwardTime(new Date());
                userCertMapper.updateByPrimaryKeySelective(userCert);
                //根据userid和课程id查询是否已有消息
                List<Message> messages = messageMapper.selectMessageByUserIdAndCourseId(user.getId(),course.getSysId());
                if (null == messages || messages.isEmpty()) {
                    //新增消息
                    messageMapper.insertSelective(getMessage(course,user.getId(),url,cert));
                }
                //记录颁发成功的数量
                redisComponent.set(Constants.UPDATE_CERT_COUNT + courseId + "#" + userId ,(length - cdl.getCount()));
                cdl.countDown();
                log.info("time:{}",System.currentTimeMillis() - time);
            } catch (Exception e) {
                log.info("redis_update_course_cert");
                redisComponent.del(Constants.UPDATE_COURSE_CERT + courseId);
                redisComponent.del(Constants.UPDATE_CERT_COUNT + courseId + "#" + userId);
                redisComponent.del(Constants.UPDATE_CERT_TOTAL + courseId + "#" + userId);
                log.error(e.getMessage());
            }
        };
    }
}
