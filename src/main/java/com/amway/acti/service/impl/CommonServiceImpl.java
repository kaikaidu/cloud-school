package com.amway.acti.service.impl;

import com.amway.acti.base.exception.AmwayLogicException;
import com.amway.acti.base.util.AzureBlobUploadUtil;
import com.amway.acti.base.util.Constants;
import com.amway.acti.base.util.FtpUtil;
import com.amway.acti.service.CommonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

/**
 * @Description:通用业务实现类
 * @Date:2018/3/14 16:41
 * @Author:wsc
 */
@Slf4j
@Service
public class CommonServiceImpl implements CommonService {

//    @Autowired
    private FtpUtil ftpUtil;

    @Autowired
    private AzureBlobUploadUtil azureBlobUploadUtil;

//    @Value("${ftp.basepath}")
    private String basepath;

//    @Value("${domain.url}")
    private String domain;

    @Override
    public String upload(MultipartFile file, String directory) {
        String fileName = file.getOriginalFilename();
        String storePath = basepath + File.separator + directory;
        try (InputStream in = new ByteArrayInputStream(file.getBytes())) {
            boolean result = ftpUtil.storeFile(storePath, fileName, in);
            if (!result) {
                throw new AmwayLogicException(Constants.ErrorCode.UPLOAD_FAIL, "文件上传失败");
            }
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
            throw new AmwayLogicException(Constants.ErrorCode.UPLOAD_FAIL, "文件上传失败");
        }
        return storePath + fileName;
    }

    @Override
    public String localUpload(MultipartFile file, String directory) {
        String orName = file.getOriginalFilename();
        orName = orName.substring(orName.lastIndexOf(".") + 1);
        String fileName = orName + "_" + System.currentTimeMillis() + "." + orName;
        String storePath = basepath + "/" + directory;
        // 新的图片名称
        String newFileName = storePath + "/" + fileName;
        // 新图片
        File newFile = new File(newFileName);
        // 将内存中的数据写入磁盘
        try {
            file.transferTo(newFile);
            return domain + "/" + directory + "/" + fileName;
        } catch (IOException e) {
            log.error(e.getMessage(),e);
        }
        return "";
    }

    @Override
    public String azureUpload(MultipartFile file, String directory) {
        String orName = file.getOriginalFilename();
//        orName = orName.substring(orName.lastIndexOf(".") + 1);
        String fileName = orName;
        String resultUrl = null;
        try {
            InputStream in = new ByteArrayInputStream(file.getBytes());
            byte[] bb = new byte[in.available()];
            resultUrl = azureBlobUploadUtil.upload(in, fileName, bb.length, directory);
        } catch (Exception ex) {
            log.error("CommonService.azureUpload:error-------->" + ex.getMessage(), ex);
        }
        return resultUrl;
    }
}
