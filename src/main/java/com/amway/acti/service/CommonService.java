package com.amway.acti.service;

import com.amway.acti.dto.CommonResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface CommonService {
    String upload(MultipartFile file,String directory);

    String localUpload(MultipartFile file, String directory);

    String azureUpload(MultipartFile file, String directory);
}
