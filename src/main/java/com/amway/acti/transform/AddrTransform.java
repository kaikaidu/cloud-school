package com.amway.acti.transform;

import com.amway.acti.dto.CityDto;
import com.amway.acti.model.Addr;

import java.util.List;

public interface AddrTransform {

    List<CityDto> transformToDto(List<Addr> addrList);
}
