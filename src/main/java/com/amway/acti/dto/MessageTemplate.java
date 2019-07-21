package com.amway.acti.dto;

import lombok.Data;

import java.util.Map;

@Data
public class MessageTemplate {
    //接收者（用户）的 openid
    private String touser;

    //所需下发的模板消息的id
    private String template_id;

    //表单提交场景下，为 submit 事件带上的 formId
    private String form_id;

    //点击模板卡片后的跳转页面，仅限本小程序内的页面。支持带参数,（示例index?foo=bar）。该字段不填则模板无跳转。
    private String page;

    //模板内容，不填则下发空模板
    private Map<String,MessageTemplateData> data;

    //模板内容字体的颜色，不填默认黑色
    private String color;

    //模板需要放大的关键词，不填则默认无放大
    private String emphasis_keyword;
}
