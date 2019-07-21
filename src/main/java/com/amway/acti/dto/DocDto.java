package com.amway.acti.dto;

import lombok.Data;

@Data
public class DocDto {

    /**
     * 资料名
     */
    private String fileName;

    /**
     * 资料下载链接
     */
    private String url;
}
