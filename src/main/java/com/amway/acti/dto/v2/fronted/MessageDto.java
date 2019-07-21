package com.amway.acti.dto.v2.fronted;

import lombok.Data;

@Data
public class MessageDto {

    /**
     * 消息ID
     */
    private int id;
    /**
     * 消息名称
     */
    private String title;
    /**
     * 消息是否已读 0-未读 1-已读
     */
    private int isRead;
}
