/**
 * Created by dk on 2018/6/6.
 */

package com.amway.acti.service;

import com.amway.acti.dto.CertTemplateSaveDto;
import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.dto.TemUploadDto;
import com.amway.acti.model.CertTemp;
import com.github.pagehelper.PageInfo;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@Validated
public interface BackendCertTemplateService {

    void save(@Valid CertTemplateSaveDto certTemplateSaveDto);

    TemUploadDto upload(MultipartFile file, String directory);

    PageInfo<List<CertTemp>> getCertTemplateList(Integer pageNo, Integer pageSize , String name, String sort);

    boolean isAward(Integer certTempId);

    CommonResponseDto del(@NotEmpty(message = "证书模板id不能为空") Integer[] ids);

    CertTemp getCertTemplateById(Integer id);

    List<String> getCourseNameList(Integer certTempId);

}
