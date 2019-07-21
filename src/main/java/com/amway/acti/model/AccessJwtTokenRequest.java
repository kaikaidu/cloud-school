/**
 * Created by dk on 2018/4/27.
 */

package com.amway.acti.model;

import lombok.Data;

@Data
public class AccessJwtTokenRequest {
    private String openId;
    private String unionId;
    private Integer uid;
    private String email;
    private String tpassword;
    private Short ident;

}
