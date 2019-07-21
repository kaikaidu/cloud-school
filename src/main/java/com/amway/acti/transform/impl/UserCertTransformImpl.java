package com.amway.acti.transform.impl;

import com.amway.acti.base.util.Constants;
import com.amway.acti.base.util.DateUtil;
import com.amway.acti.dao.CertTempMapper;
import com.amway.acti.dao.CourseMapper;
import com.amway.acti.dao.UserCertCustomMapper;
import com.amway.acti.dto.*;
import com.amway.acti.dto.v2.fronted.CertDetailDto;
import com.amway.acti.dto.v2.fronted.CertDto;
import com.amway.acti.model.*;
import com.amway.acti.transform.UserCertTransform;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class UserCertTransformImpl implements UserCertTransform {

    @Autowired
    private CertTempMapper certTempMapper;

    @Autowired
    private UserCertCustomMapper userCertCustomMapper;

    @Autowired
    private CourseMapper courseMapper;


    @Override
    public PageDto<CertDto> transformInfoToMessageDto(PageInfo<UserCert> info) {
        PageDto<CertDto> pageDto = new PageDto<>();
        pageDto.setTotalPages(info.getPages());
        pageDto.setTotalCount(info.getTotal());

        List<CertDto> list = new ArrayList<>();
        for (UserCert userCert : info.getList()) {
            CertDto certDto = new CertDto();
            certDto.setId(userCert.getId());
            certDto.setAwardTime(userCert.getAwardTime());
            certDto.setName(userCert.getName());
            list.add(certDto);
        }
        pageDto.setDataList( list );
        return pageDto;
    }

    @Override
    public CertDetailDto transformModelToMessageDetailDto(UserCert userCert) {
        if(userCert == null){
            return null;
        }
        CertDetailDto certDetailDto = new CertDetailDto();
        certDetailDto.setAwardTime(userCert.getAwardTime());
        certDetailDto.setName(userCert.getName());
        certDetailDto.setUrl(userCert.getUrl());
        return certDetailDto;
    }


    //转换证书模板列表
    @Override
    public PageDto<CertTempDto> transformToCertTempDto(PageInfo<CertTemp> pageInfo) {
        log.info("pageInfo:{}",pageInfo);
        PageDto<CertTempDto> pageDto = new PageDto <>();
        pageDto.setTotalPages(pageInfo.getPages());
        pageDto.setTotalCount(pageInfo.getTotal());

        if (null != pageInfo.getList() && !pageInfo.getList().isEmpty()) {
            List<CertTempDto> certTempDtos = new ArrayList <>();
            CertTempDto certTempDto;
            for (CertTemp c : pageInfo.getList()) {
                certTempDto = new CertTempDto();
                certTempDto.setCertTempId(c.getId());//证书模板ID
                certTempDto.setCreateTime(DateUtil.format(c.getCreateTime(),DateUtil.YYYY_MM_DD));//创建时间
                certTempDto.setName(c.getName());//证书模板名称
                certTempDto.setUrl(c.getUrl());//预览地址
                certTempDtos.add(certTempDto);
            }
            pageDto.setDataList(certTempDtos);
        }
        log.info("pageDto:{}",pageDto);
        return pageDto;
    }

    //转换学员关联证书信息
    @Override
    public CourseCertDto transformToCourseCertDto(Cert cert) {
        CourseCertDto courseCertDto = null;
        if (!ObjectUtils.isEmpty(cert)) {
            courseCertDto = new CourseCertDto();
            Course course = courseMapper.selectByPrimaryKey(cert.getCourseId());
            courseCertDto.setCertId(cert.getId());  //证书ID
            courseCertDto.setName(cert.getName());  //证书名称
            courseCertDto.setUrl(cert.getUrl());    //预览地址
            courseCertDto.setIsDel(userCertCustomMapper.selectUserCertByCourseId(cert.getCourseId()) > 0 ? 0 : 1);//是否可以删除 0：不可删除 1：可删除
            //当前时间大于课程开始时间
            if (new Date().after(course.getStartTime())) {
                courseCertDto.setIsStart(Constants.Number.INT_NUMBER1);//课程已开始
            } else {
                courseCertDto.setIsStart(Constants.Number.INT_NUMBER0);//课程未开始
            }
        }
        log.info("courseCertDto:{}",courseCertDto);
        return courseCertDto;
    }

    //转换证书详情
    @Override
    public com.amway.acti.dto.CertDto transformToCertDto(Cert cert) {
        com.amway.acti.dto.CertDto certDto = new com.amway.acti.dto.CertDto();
        certDto.setCertId(cert.getId());
        certDto.setName(cert.getName());
        certDto.setCertTempId(cert.getCertTempId());
        certDto.setUrl(cert.getUrl());
        CertTemp certTemp = certTempMapper.selectByPrimaryKey(cert.getCertTempId());
        certDto.setTempName(certTemp.getName());
        log.info("certDto:{}",certDto);
        return certDto;
    }

    //转换学员证书列表
    @Override
    public PageDto transformToUserCertDto(PageInfo<UserCertCustom> pageInfo) {
        PageDto<UserCertDto> pageDto = new PageDto <>();
        pageDto.setTotalPages(pageInfo.getPages());
        pageDto.setTotalCount(pageInfo.getTotal());

        List<UserCertDto> userCertDtoList = new ArrayList <>();
        if (null != pageInfo.getList() && !pageInfo.getList().isEmpty()) {
            Date date = new Date();
            for (UserCertCustom c : pageInfo.getList()) {
                UserCertDto userCertDto = new UserCertDto();
                userCertDto.setUserId(c.getId());           //用户id
                userCertDto.setAdaNumber(c.getAdaNumber()); //安利卡号
                userCertDto.setName(c.getName());           //姓名
                userCertDto.setSex(c.getSex());             //性别
                userCertDto.setSexName(c.getSex()==0?"男":"女");
                userCertDto.setAwardState(c.getIsAward() == null ? 0 : c.getIsAward());  //是否颁发证书
                userCertDto.setAwardStateName(userCertDto.getAwardState()==1?"已颁发":"未颁发");
                userCertDto.setViaState(c.getViaState());   //通过状态
                userCertDto.setViaStateName(c.getViaState()==1?"已通过":"未通过");
                //判断报名状态
                userCertDto.setSignState(getSign(c.getIsVerify(),c.getApprResult(),c.getEndTime(),date));
                if(userCertDto.getSignState()==0){
                    userCertDto.setSignStateName("报名失败");
                }else if(userCertDto.getSignState()==1){
                    userCertDto.setSignStateName("报名成功");
                }else if(userCertDto.getSignState()==2){
                    userCertDto.setSignStateName("审核中");
                }else if(userCertDto.getSignState()==3){
                    userCertDto.setSignStateName("已完成");
                }

                userCertDto.setUrl(c.getUrl());             //浏览地址
                userCertDtoList.add(userCertDto);
            }

        }
        pageDto.setDataList(userCertDtoList);
        log.info("userCertDtoList:{}",userCertDtoList);
        return pageDto;
    }

    //转换证书设置导出数据
    @Override
    public List<UserCertExportDto> transformUserCertExportDto(List <UserCertCustom> userCertCustomList) {
        List<UserCertExportDto> userCertExportDtoList = null;
        if (null != userCertCustomList && !userCertCustomList.isEmpty()) {
            userCertExportDtoList = new ArrayList <>();
            UserCertExportDto userCertExportDto;
            Date date = new Date();
            int sign;
            for (UserCertCustom c : userCertCustomList) {
                userCertExportDto = new UserCertExportDto();
                userCertExportDto.setName(c.getName());     //姓名
                userCertExportDto.setAdaNumber(c.getAdaNumber());//安利卡号
                //性别
                if (c.getSex() == Constants.Number.INT_NUMBER0) {
                    userCertExportDto.setSex(Constants.Sex.MAN);//男
                } else {
                    userCertExportDto.setSex(Constants.Sex.WOMEN);//女
                }
                sign = getSign(c.getIsVerify(),c.getApprResult(),c.getEndTime(),date);
                //报名状态
                if (sign == Constants.Number.INT_NUMBER0) {//审核失败
                    userCertExportDto.setSign(Constants.SignState.AUDIT_FAILURE);
                } else if (sign == Constants.Number.INT_NUMBER1) {//报名成功
                    userCertExportDto.setSign(Constants.SignState.SUCCESS);
                } else if (sign == Constants.Number.INT_NUMBER2) {//审核中
                    userCertExportDto.setSign(Constants.SignState.AUDIT);
                } else {//已完成
                    userCertExportDto.setSign(Constants.SignState.FINISH);
                }
                //通过状态
                if (c.getViaState() == Constants.Number.INT_NUMBER0) {
                    userCertExportDto.setVia(Constants.ViaState.NOT_THROUGH);
                } else {
                    userCertExportDto.setVia(Constants.ViaState.ADOPT);
                }
                //颁发状态
                if ((c.getIsAward() == null ? 0 : c.getIsAward()) == Constants.Number.INT_NUMBER0) {
                    userCertExportDto.setAwaward(Constants.Award.NOT_AWARD);
                } else {
                    userCertExportDto.setAwaward(Constants.Award.AWARD);
                }
                userCertExportDtoList.add(userCertExportDto);
            }
        }
        return userCertExportDtoList;
    }

    //判断报名状态 isVerify:是否需要审批 apprResult:审核状态 endTime：课程结束时间 date：当前时间
    private Integer getSign(Integer isVerify, Integer apprResult,Date endTime,Date date) {
        //判断是否需要审批 1：需要 0：不需要
        if (isVerify == Constants.Number.INT_NUMBER0) {
            if (date.after(endTime)) {
                return Constants.Number.INT_NUMBER3;//已完成
            } else {
                return Constants.Number.INT_NUMBER1;//报名成功
            }
        } else {
            //当前时间大于课程结束时间 并且 审批状态等于报名成功
            if (date.after(endTime) && apprResult == Constants.Number.INT_NUMBER1) {
                return Constants.Number.INT_NUMBER3;//已完成
            } else {
                return apprResult;
            }
        }
    }

}
