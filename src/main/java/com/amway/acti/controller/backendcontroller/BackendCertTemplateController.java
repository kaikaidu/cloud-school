/**
 * Created by dk on 2018/6/6.
 */

package com.amway.acti.controller.backendcontroller;

import com.amway.acti.dto.CertTempDelRequestDto;
import com.amway.acti.dto.CertTemplateSaveDto;
import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.model.CertTemp;
import com.amway.acti.service.BackendCertTemplateService;
import com.amway.acti.transform.BackendCertTemplateTransform;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/backend/cert/template")
public class BackendCertTemplateController {

    @Autowired
    private BackendCertTemplateTransform backendCertTemplateTransform;

    @Autowired
    private BackendCertTemplateService backendCertTemplateService;

    /**
     * @Description:根据id删除
     */
    @PostMapping("/del")
    public CommonResponseDto del(@RequestBody CertTempDelRequestDto dto) {
        return backendCertTemplateService.del(dto.getIds());
    }

    /***
     * 保存or修改
     * @param dto
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResponseDto save(@RequestBody CertTemplateSaveDto dto) {
        backendCertTemplateService.save(dto);
        return CommonResponseDto.ofSuccess();
    }

    @RequestMapping("/upload")
    public CommonResponseDto upload(MultipartFile file) throws Exception {
        return CommonResponseDto.ofSuccess(backendCertTemplateService.upload(file, "cert"));
    }

    @RequestMapping(value = "/getCertTemplateList", method = RequestMethod.GET)
    public CommonResponseDto getCertTemplateList(Integer pageNo, Integer pageSize, String name, String timeSort) {
        PageInfo pageInfoList = backendCertTemplateService.getCertTemplateList(pageNo, pageSize, name, timeSort);
        return CommonResponseDto.ofSuccess(backendCertTemplateTransform.modelToDtoByGetList(pageInfoList));
    }

    @RequestMapping(value = "/getCertTemplateById", method = RequestMethod.GET)
    public CommonResponseDto getCertTemplateById(@RequestParam Integer tempId) {
        CertTemp certTemp = backendCertTemplateService.getCertTemplateById(tempId);
        return CommonResponseDto.ofSuccess(backendCertTemplateTransform.modelToDtoByGetInfo(certTemp));
    }

    @RequestMapping(value = "/getCourseNameList", method = RequestMethod.GET)
    public CommonResponseDto getCourseNameList(@RequestParam Integer certTempId) {
        return CommonResponseDto.ofSuccess(backendCertTemplateService.getCourseNameList(certTempId));
    }

}
