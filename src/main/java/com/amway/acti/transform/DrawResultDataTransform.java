package com.amway.acti.transform;

import com.amway.acti.dto.DrawResultDataDto;
import com.amway.acti.model.DrawResultData;

import java.util.List;

public interface DrawResultDataTransform {

    List<DrawResultDataDto> transformDrawResultDataToDto(List<DrawResultData> drawResultDataList);
}
