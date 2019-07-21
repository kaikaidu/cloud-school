/**
 * Created by dk on 2018/5/25.
 */

package com.amway.acti.base.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "osb.aboInfo")
public class ObsAboInfoProperty {
    private String qaosbappid;
    private String qaosbappkey;
    private String qaservice;
    private String qaoperation;
    private String qaversion;
    private String qamode;
    private String qaurl;
    private String prodosbappid;
    private String prodosbappkey;
    private String prodservice;
    private String prodoperation;
    private String prodversion;
    private String prodmode;
    private String produrl;
}
