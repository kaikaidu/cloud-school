package com.amway.acti.dto;

import lombok.Data;

@Data
public class MessageTemplateData {
    private String value;

    private String color;

    public MessageTemplateData() {
        //空构造函数
    }

    public MessageTemplateData(String value) {
        this.value = value;
    }

    public MessageTemplateData(String value, String color) {
        this.value = value;
        this.color = color;
    }
}
