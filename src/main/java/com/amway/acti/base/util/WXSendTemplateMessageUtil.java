package com.amway.acti.base.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.amway.acti.dto.MessageTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

@Component
@Slf4j
public class WXSendTemplateMessageUtil {

    //获取 access_token 填写 client_credential
    private String grant_type = "client_credential";

    //第三方用户唯一凭证
    @Value("${stu.login.appid}")
    private String appid;

    //第三方用户唯一凭证密钥，即appsecret
    @Value("${stu.login.secret}")
    private String secret;

    @Value("${custom.sendMessage.getAccessTokenUrl}")
    private String getAccessTokenUrl;

    @Value("${custom.sendMessage.sendTemplateMessageUrl}")
    private String sendTemplateMessageUrl;

    public String getAccessToken() throws KeyManagementException,NoSuchAlgorithmException {
        String url = getAccessTokenUrl+"?grant_type="+grant_type+"&appid="+appid+"&secret="+secret;
        log.info("url:{}",url);
        String response = HttpClientUtil.doGetIgnoreVerifySSL(url);
        JSONObject jsonObject = JSON.parseObject(response);
        String accessToken = jsonObject.getString("access_token");
        log.info("access_token:{}",accessToken);
        return accessToken;
    }

    public String sendTemplateMessage (MessageTemplate messageTemplate,String access_token) throws KeyManagementException,NoSuchAlgorithmException,IOException{
        log.info("access_token:{}",access_token);
        String url = sendTemplateMessageUrl+"?access_token="+access_token;
        log.info("url:{}",url);
        String response = HttpClientUtil.doPostIgnoreVerifySSL(url,messageTemplate);
        log.info("response:{}",response);
        return response;
    }
}
