package com.amway.acti.controller.backendcontroller;

import com.amway.acti.base.exception.AmwayLogicException;
import com.amway.acti.base.util.Constants;
import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.service.CommonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author Weishun.Zheng
 */
@Slf4j
@RestController
@RequestMapping("/backend")
public class BackendCommonController {

    @Autowired
    private CommonService commonService;

    @RequestMapping("/upload")
    public CommonResponseDto upload(MultipartFile file, String directory) {
        log.info("backend/upload 开始文件上传：{}|{}", file.getName(), directory);
        //如果文件为空 并且是图片上传 则可以设定默认值
        if (file == null && Constants.FileUploadLimit.IMG.equals(directory)) {
            return CommonResponseDto.ofSuccess(Constants.DEFAULT_COURSE_PICTURE);
        }
        String fileUrl = "";
        try {
            if (checkFileSize(file)) {
                fileUrl = commonService.azureUpload(file, directory);
            } else {
                throw new AmwayLogicException(Constants.ErrorCode.UPLOAD_FILE_ERROR, Constants.FileUploadLimit.TOP_LIMIT_MSG);
            }
        } catch (AmwayLogicException e) {
            log.error("/resource/doc/saveDoc error:{}", e);
            throw e;
        } catch (Exception e) {
            log.error("/resource/doc/saveDoc error:{}", e);
            throw new AmwayLogicException(Constants.ErrorCode.UPLOAD_FILE_ERROR, "文件上传失败！");
        }
        return CommonResponseDto.ofSuccess(fileUrl);
    }

    /**
     * <校验文件大小>
     *
     * @param file
     * @return
     */
    private boolean checkFileSize(MultipartFile file) {
        try {
            if (file.getBytes().length > Constants.FileUploadLimit.TOP_LIMIT) {
                return false;
            }
        } catch (IOException e) {
            log.error("/resource/doc/saveDoc->checkFileSize error:{}", e);
            return false;
        }
        return true;
    }
}
