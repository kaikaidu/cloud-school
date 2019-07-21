package com.amway.acti.controller.frontendcontroller;

import com.amway.acti.base.context.MiniProgramRequestContextHolder;
import com.amway.acti.base.exception.AmwayParamException;
import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.model.UserCert;
import com.amway.acti.service.UserCertService;
import com.amway.acti.transform.UserCertTransform;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/frontend/cert")
public class UserCertController {

    @Autowired
    private UserCertService userCertService;

    @Autowired
    private UserCertTransform userCertTransform;

    @GetMapping("getCertList")
    public CommonResponseDto getCertList(Integer pageNo, Integer pageSize){
        int userId = MiniProgramRequestContextHolder.getRequestUser().getUid();
        PageInfo<UserCert> info = userCertService.getByUserId(userId, pageNo, pageSize);
        return CommonResponseDto.ofSuccess(userCertTransform.transformInfoToMessageDto(info));
    }

    @GetMapping("getCertDetail")
    public CommonResponseDto getCertDetail(Integer certId){
        UserCert userCert = userCertService.getById(certId);
        if(userCert == null){
            throw new AmwayParamException("certId不存在");
        }
        if(userCert.getIsRead() == 0){
            userCertService.updateIsRead(userCert.getId());
        }
        return CommonResponseDto.ofSuccess(userCertTransform.transformModelToMessageDetailDto(userCert));
    }

    @GetMapping("getCertDetailByCourseId")
    public CommonResponseDto getCertDetailByCourseIdAndUserId(Integer courseId){
        int userId = MiniProgramRequestContextHolder.getRequestUser().getUid();
        UserCert userCert = userCertService.getByCourseIdAndUserId(courseId, userId);
        if(userCert == null){
            throw new AmwayParamException("courseId不存在");
        }
        if(userCert.getIsRead() == 0){
            userCertService.updateIsRead(userCert.getId());
        }
        return CommonResponseDto.ofSuccess(userCertTransform.transformModelToMessageDetailDto(userCert));
    }
}
