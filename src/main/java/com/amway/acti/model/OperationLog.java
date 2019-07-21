package com.amway.acti.model;

import lombok.Data;

@Data
public class OperationLog {

    private String id;

    private String userName;

    private String beforeTime;

    private String url;

    private String httpMethod;

    private String ip;

    private String classMethod;

    private String afterTime;
}
