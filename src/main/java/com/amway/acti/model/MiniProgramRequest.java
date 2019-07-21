/**
 * Created by dk on 2018/7/6.
 */

package com.amway.acti.model;

import lombok.Data;

@Data
public class MiniProgramRequest {
    private String unionId;
    private String openId;
    private Integer uid;
    private String email;
    private String pwd;
    private Short ident;
}
