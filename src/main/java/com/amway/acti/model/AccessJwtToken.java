/**
 * Created by dk on 2018/2/21.
 */

package com.amway.acti.model;

public class AccessJwtToken {
    public AccessJwtToken(String token) {
        this.token = token;
    }

    private String token;
    private String expireDate;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }
}
