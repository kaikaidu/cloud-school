package com.amway.acti.controller.backendcontroller.course;

import com.amway.acti.base.redis.CourseRedisComponent;
import com.amway.acti.base.util.Constants;
import com.amway.acti.dto.CertDto;
import com.amway.acti.dto.CertParamDto;
import com.amway.acti.dto.CertStateDto;
import com.amway.acti.dto.CertTempDto;
import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.dto.CourseCertDto;
import com.amway.acti.dto.PageDto;
import com.amway.acti.dto.UserCertDto;
import com.amway.acti.model.Cert;
import com.amway.acti.model.CertTemp;
import com.amway.acti.model.User;
import com.amway.acti.model.UserCertCustom;
import com.amway.acti.service.BackendUserCertService;
import com.amway.acti.transform.UserCertTransform;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @Description:证书设置
 * @Date:2018/6/5 19:07
 * @Author:wsc
 */
@RestController
@RequestMapping("/backend/course")
public class BackendUserCertController {

    @Autowired
    private BackendUserCertService userCertService;

    @Autowired
    private UserCertTransform userCertTransform;

    @Autowired
    private CourseRedisComponent courseRedisComponent;

    /**
     * @Description:查询证书模板列表
     * @Date: 2018/6/5 19:10
     * @param: pageNo 当前页
     * @param: pageSize 每页显示数量
     * @param: timeSort 正序：asc 倒序：desc 默认倒序
     * @param: name 证书模板名称
     * @Author: wsc
     */
    @GetMapping("/certset/findCertTempList")
    public CommonResponseDto<PageDto<CertTempDto>> findCertTempList(Integer pageNo,
                                                                    Integer pageSize,
                                                                    String timeSort,
                                                                    String name){
        PageInfo<CertTemp> pageInfo = userCertService.findCertTempList(pageNo,pageSize,timeSort,name);
        PageDto<CertTempDto> certTempDtoPageDto = userCertTransform.transformToCertTempDto(pageInfo);
        return CommonResponseDto.ofSuccess(certTempDtoPageDto);
    }

    /**
     * @Description:新增证书/修改证书
     * @Date: 2018/6/6 14:46
     * @param: cert 证书信息
     * @Author: wsc
     */
    @PostMapping("/certset/addOrUpdateCert")
    public CommonResponseDto addOrUpdateCert(@RequestBody CertDto cert,HttpSession session) {
        User user = (User)session.getAttribute(Constants.BACKEND_USER_SESSSION_KEY);
        CommonResponseDto responseDto = userCertService.addOrUpdateCert(cert,user);
        courseRedisComponent.delCourseRedisByCourseId(cert.getCourseId());
        return responseDto;
    }

    /**
     * @Description:查询课程关联证书
     * @Date: 2018/6/6 15:15
     * @param: courseId 课程id
     * @Author: wsc
     */
    @GetMapping("/certset/findCourseCert")
    public CommonResponseDto<CourseCertDto> findCourseCert(Integer courseId){
        Cert cert = userCertService.findCourseCert(courseId);
        CourseCertDto courseCertDto = userCertTransform.transformToCourseCertDto(cert);
        return CommonResponseDto.ofSuccess(courseCertDto);
    }

    /**
     * @Description:查询课程证书详情
     * @Date: 2018/6/6 17:02
     * @param: certId 证书id
     * @Author: wsc
     */
    @GetMapping("/certset/findCertInfo")
    public CommonResponseDto<CertDto> findCertInfo(Integer certId){
        Cert cert = userCertService.findCertById(certId);
        CertDto certDto = userCertTransform.transformToCertDto(cert);
        return CommonResponseDto.ofSuccess(certDto);
    }


    /**
     * @Description:删除课程证书
     * @Date: 2018/6/6 16:36
     * @param: certId 证书id
     * @param: courseId 课程id
     * @Author: wsc
     */
    @PostMapping("/certset/deleteCourseCert")
    public CommonResponseDto deleteCourseCert(@RequestBody CertDto cert){
        CommonResponseDto responseDto = userCertService.deleteCourseCert(cert.getCertId());
        courseRedisComponent.delCourseRedisByCourseId(cert.getCourseId());
        return responseDto;
    }


    /**
     * @Description:查询学员证书列表
     * @Date: 2018/6/6 19:25
     * @param: certParamDto 条件
     * @Author: wsc
     */
    @PostMapping("/certset/findUserCertList")
    public CommonResponseDto<PageDto<UserCertDto>> findUserCertList(@RequestBody CertParamDto certParamDto ){
        PageInfo<UserCertCustom> pageInfo = userCertService.findUserCertList(certParamDto);
        PageDto pageDto = userCertTransform.transformToUserCertDto(pageInfo);
        return CommonResponseDto.ofSuccess(pageDto);
    }

    /**
     * @Description:查询学员证书列表
     * @Date: 2018/6/6 19:25
     * @param: certParamDto 条件
     * @Author: wsc
     */
    @GetMapping("/certset/findUserCertListCount")
    public CommonResponseDto findUserCertListCount(Integer courseId){
        return CommonResponseDto.ofSuccess(userCertService.findUserCertListCount(courseId));
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
    @GetMapping("/certset/exportUserCert")
    public void exportUserCert(Integer courseId ,
                               String userIds,
                               HttpServletRequest request,
                               HttpServletResponse response) throws IOException, ReflectiveOperationException {
        userCertService.exportUserCert(courseId,userIds,request,response);
    }

    /**
     * @Description:修改证书颁发状态
     * @Date: 2018/6/7 11:04
     * @param: userIds 学员ID数组
     * @param: courseId 课程ID
     * @param: state 修改状态[0:未颁发 1：已颁发]
     * @Author: wsc
     */
    @PostMapping("/certset/updateCertState")
    public CommonResponseDto updateCertState(@RequestBody CertStateDto certStateDto) {
        CommonResponseDto responseDto = userCertService.updateCertState(certStateDto);
        courseRedisComponent.delCourseRedisByCourseId(certStateDto.getCourseId());
        return responseDto;
    }

    /**
     * @Description:上传证书名单
     * @Date: 2018/6/7 16:23
     * @param: file Execl文件
     * @param: courseId 课程ID
     * @Author: wsc
     */
    @RequestMapping("/certset/uploadUserCert")
    public CommonResponseDto uploadUserCert(MultipartFile file, Integer courseId, HttpSession session) throws Exception {
        User user = (User)session.getAttribute(Constants.BACKEND_USER_SESSSION_KEY);
        CommonResponseDto responseDto = userCertService.uploadUserCert(file,courseId,user);
        courseRedisComponent.delCourseRedisByCourseId(courseId);
        return responseDto;
    }

    /**
     * @Description: 获取完成数量
     * @Date: 2018/8/29 11:20
     * @param: courseId
     * @param: request
     * @Author: wsc
     */
    @PostMapping("/certset/getCertSuccCount")
    @ResponseBody
    public CommonResponseDto getCertSuccCount(@RequestBody CertStateDto certStateDto,HttpServletRequest request){
        User user = (User) request.getSession().getAttribute(Constants.BACKEND_USER_SESSSION_KEY);
        return userCertService.getCertSuccCount(certStateDto.getCourseId(),user.getId());
    }
}
