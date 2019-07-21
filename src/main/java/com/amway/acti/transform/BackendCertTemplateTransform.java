/**
 * Created by dk on 2018/6/6.
 */

package com.amway.acti.transform;

import com.amway.acti.dto.CertTempDto;
import com.amway.acti.dto.PageDto;
import com.amway.acti.model.CertTemp;
import com.github.pagehelper.PageInfo;

public interface BackendCertTemplateTransform {

    PageDto<CertTempDto> modelToDtoByGetList(PageInfo pageInfo);

    CertTempDto modelToDtoByGetInfo(CertTemp certTemp);
}
