package com.amway.acti.service;

import com.amway.acti.dto.CertDto;
import com.amway.acti.dto.CertParamDto;
import com.amway.acti.dto.CertStateDto;
import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.model.Cert;
import com.amway.acti.model.CertTemp;
import com.amway.acti.model.User;
import com.amway.acti.model.UserCertCustom;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * @Description:证书设置
 * @Date:2018/6/5 19:12
 * @Author:wsc
 */
@Validated
public interface BackendUserCertService {
    /**
     * @Description:查询证书模板列表
     * @Date: 2018/6/5 19:10
     * @param: pageNo 当前页
     * @param: pageSize 每页显示数量
     * @param: timeSort 正序：asc 倒序：desc 默认倒序
     * @param: name 证书模板名称
     * @Author: wsc
     */
    PageInfo<CertTemp> findCertTempList(@NotNull(message = "当前页不能为空") Integer pageNo, @NotNull(message = "每页数量不能为空")Integer pageSize, String timeSort, String name);

    /**
     * @Description:新增证书/修改证书
     * @Date: 2018/6/6 14:46
     * @param: cert 证书信息
     * @Author: wsc
     */
    CommonResponseDto addOrUpdateCert(@Valid CertDto cert,User user);

    /**
     * @Description:查询课程关联证书
     * @Date: 2018/6/6 15:15
     * @param: courseId 课程id
     * @Author: wsc
     */
    Cert findCourseCert(@NotNull(message = "课程id不能为空") Integer courseId);

    /**
     * @Description:删除课程证书
     * @Date: 2018/6/6 16:36
     * @param: certId 证书id
     * @param: courseId 课程id
     * @Author: wsc
     */
    CommonResponseDto deleteCourseCert(@NotNull(message = "证书id不能为空") Integer certId);

    /**
     * @Description:查询课程证书详情
     * @Date: 2018/6/6 17:02
     * @param: certId 证书id
     * @Author: wsc
     */
    Cert findCertById(@NotNull(message = "证书id不能为空") Integer certId);

    /**
     * @Description:查询学员证书列表
     * @Date: 2018/6/6 19:25
     * @param: certParamDto 条件
     * @Author: wsc
     */
    PageInfo<UserCertCustom> findUserCertList(@Valid CertParamDto certParamDto);

    /**
     * @Description:批量导出证书设置
     * @Date: 2018/6/7 9:54
     * @param: courseId 课程ID
     * @param: userIds 学员id数组，为空导出全部
     * @param: request 请求
     * @param: response 响应
     * @Author: wsc
     */
    void exportUserCert(@NotNull(message = "课程id不能为空") Integer courseId, String userIds, HttpServletRequest request, HttpServletResponse response) throws ReflectiveOperationException, IOException;

    /**
     * @Description:修改证书颁发状态
     * @Date: 2018/6/7 11:04
     * @param: userIds 学员ID数组
     * @param: courseId 课程ID
     * @param: state 修改状态[0:未颁发 1：已颁发]
     * @Author: wsc
     */
    CommonResponseDto updateCertState(@Valid CertStateDto certStateDto);

    Integer findUserCertListCount(Integer courseId);

    /**
     * @Description:上传证书名单
     * @Date: 2018/6/7 16:23
     * @param: file Execl文件
     * @param: courseId 课程ID
     * @Author: wsc
     */
    CommonResponseDto uploadUserCert(MultipartFile file, @NotNull(message = "课程id不能为空") Integer courseId, User user) throws IOException, InterruptedException;

    /**
     * @Description: 获取完成数量
     * @Date: 2018/8/29 11:20
     * @param: courseId
     * @param: request
     * @Author: wsc
     */
    CommonResponseDto getCertSuccCount(Integer courseId, Integer id);
}
