/**
 * Created by dk on 2018/6/6.
 */

package com.amway.acti.transform.impl;

import com.amway.acti.base.util.DateUtil;
import com.amway.acti.dto.CertTempDto;
import com.amway.acti.dto.PageDto;
import com.amway.acti.model.CertTemp;
import com.amway.acti.service.BackendCertTemplateService;
import com.amway.acti.transform.BackendCertTemplateTransform;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class BackendCertTemplateTransformImpl implements BackendCertTemplateTransform{

    @Autowired
    private BackendCertTemplateService backendCertTemplateService;

    @Override
    public PageDto<CertTempDto> modelToDtoByGetList(PageInfo pageInfo) {
        List<CertTempDto> resultList = new ArrayList<>();
        List<CertTemp> list = pageInfo.getList();
        for (CertTemp certTemp : list) {
            boolean isAward = backendCertTemplateService.isAward(certTemp.getId());
            CertTempDto certTempDto = new CertTempDto();
            certTempDto.setCertTempId(certTemp.getId());
            certTempDto.setCreateTime(DateUtil.format(certTemp.getCreateTime(),DateUtil.YYYY_MM_DD));
            certTempDto.setName(certTemp.getName());
            certTempDto.setUrl(certTemp.getUrl());
            certTempDto.setIsEdit(isAward == true ? 0 : 1);
            certTempDto.setIsDel(isAward == true ? 0 : 1);
            resultList.add(certTempDto);
        }
        PageDto<CertTempDto> pageDto = new PageDto<>();
        pageDto.setTotalPages(pageInfo.getPages());
        pageDto.setTotalCount(pageInfo.getTotal());
        pageDto.setDataList(resultList);
        return pageDto;
    }

    @Override
    public CertTempDto modelToDtoByGetInfo(CertTemp certTemp) {
        CertTempDto dto = new CertTempDto();
        dto.setUrl(certTemp.getUrl());
        dto.setName(certTemp.getName());
        dto.setCertTempId(certTemp.getId());
        return dto;
    }
}
