/**
 * Created by dk on 2018/3/12.
 */

package com.amway.acti.base.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "stu.login")
public class StuloginProperty {
    private String appid;
    private String secret;
    private String getSessionKeyUrl;
    private String isProd;
    private String componentQaAppid;
    private String componentQaSecret;
    private String qaGetTokenUrl;
    private String qaGetAdaInfoUrl;
    private String componentProdAppid;
    private String componentProdSecret;
    private String prodGetTokenUrl;
    private String prodGetAdaInfoUrl;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getGetSessionKeyUrl() {
        return getSessionKeyUrl;
    }

    public void setGetSessionKeyUrl(String getSessionKeyUrl) {
        this.getSessionKeyUrl = getSessionKeyUrl;
    }

    public String getIsProd() {
        return isProd;
    }

    public void setIsProd(String isProd) {
        this.isProd = isProd;
    }

    public String getComponentQaAppid() {
        return componentQaAppid;
    }

    public void setComponentQaAppid(String componentQaAppid) {
        this.componentQaAppid = componentQaAppid;
    }

    public String getComponentQaSecret() {
        return componentQaSecret;
    }

    public void setComponentQaSecret(String componentQaSecret) {
        this.componentQaSecret = componentQaSecret;
    }

    public String getComponentProdAppid() {
        return componentProdAppid;
    }

    public void setComponentProdAppid(String componentProdAppid) {
        this.componentProdAppid = componentProdAppid;
    }

    public String getComponentProdSecret() {
        return componentProdSecret;
    }

    public void setComponentProdSecret(String componentProdSecret) {
        this.componentProdSecret = componentProdSecret;
    }

    public String getQaGetTokenUrl() {
        return qaGetTokenUrl;
    }

    public void setQaGetTokenUrl(String qaGetTokenUrl) {
        this.qaGetTokenUrl = qaGetTokenUrl;
    }

    public String getQaGetAdaInfoUrl() {
        return qaGetAdaInfoUrl;
    }

    public void setQaGetAdaInfoUrl(String qaGetAdaInfoUrl) {
        this.qaGetAdaInfoUrl = qaGetAdaInfoUrl;
    }

    public String getProdGetTokenUrl() {
        return prodGetTokenUrl;
    }

    public void setProdGetTokenUrl(String prodGetTokenUrl) {
        this.prodGetTokenUrl = prodGetTokenUrl;
    }

    public String getProdGetAdaInfoUrl() {
        return prodGetAdaInfoUrl;
    }

    public void setProdGetAdaInfoUrl(String prodGetAdaInfoUrl) {
        this.prodGetAdaInfoUrl = prodGetAdaInfoUrl;
    }
}
