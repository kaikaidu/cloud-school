package com.amway.acti.transform;

import com.amway.acti.dto.PageDto;
import com.amway.acti.dto.v2.fronted.CertDetailDto;
import com.amway.acti.dto.v2.fronted.CertDto;
import com.amway.acti.model.UserCert;
import com.github.pagehelper.PageInfo;


import com.amway.acti.dto.*;
import com.amway.acti.model.Cert;
import com.amway.acti.model.CertTemp;
import com.amway.acti.model.UserCertCustom;


import java.util.List;

/**
 * @Description:
 * @Date:2018/6/6 14:32
 * @Author:wsc
 */
public interface UserCertTransform {

    PageDto<CertDto> transformInfoToMessageDto(PageInfo<UserCert> info);

    CertDetailDto transformModelToMessageDetailDto(UserCert userCert);

    PageDto<CertTempDto> transformToCertTempDto(PageInfo<CertTemp> pageInfo);

    CourseCertDto transformToCourseCertDto(Cert cert);

    com.amway.acti.dto.CertDto transformToCertDto(Cert cert);

    PageDto transformToUserCertDto(PageInfo<UserCertCustom> pageInfo);

    List<UserCertExportDto> transformUserCertExportDto(List<UserCertCustom> userCertCustomList);
}
