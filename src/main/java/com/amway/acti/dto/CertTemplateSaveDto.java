/**
 * Created by dk on 2018/6/6.
 */

package com.amway.acti.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class CertTemplateSaveDto {
    private Integer id;
    @NotBlank(message = "请输入证书模板")
    @Length(max = 50, message = "证书模板名称过长")
    private String certTemplateName;
    private String url;
    private String zipUrl;
    private String htmlUrl;
}
