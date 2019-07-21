package com.amway.acti.transform.impl;

import com.amway.acti.dto.CityDto;
import com.amway.acti.model.Addr;
import com.amway.acti.transform.AddrTransform;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AddrTransformImpl implements AddrTransform {
    @Override
    public List<CityDto> transformToDto(List<Addr> addrList) {
        List<CityDto> cityDtoList = Lists.newArrayList();
        for (Addr addr :addrList) {
            CityDto cityDto = new CityDto();
            cityDto.setName(addr.getName());
            cityDto.setCode(addr.getCode());
            cityDtoList.add(cityDto);
        }
        return cityDtoList;
    }
}
