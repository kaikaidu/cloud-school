package com.amway.acti.transform;

import com.amway.acti.dto.SitemTempDto;
import com.amway.acti.model.SitemTemp;

import java.util.List;

public interface SitemTempTransform {

    List<SitemTempDto> transformToSitemTempDto(List<SitemTemp> sitemTempList);
}
