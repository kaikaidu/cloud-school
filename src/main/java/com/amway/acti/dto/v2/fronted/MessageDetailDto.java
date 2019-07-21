package com.amway.acti.dto.v2.fronted;

import lombok.Data;

@Data
public class MessageDetailDto {

    /**
     * 消息名称
     */
    private String name;
    /**
     * 消息类型：1-证书
     */
    private int type;
    /**
     * 消息为证书时的证书地址
     */
    private String content;
}
