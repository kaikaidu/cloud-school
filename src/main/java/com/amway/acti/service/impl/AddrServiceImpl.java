package com.amway.acti.service.impl;

import com.amway.acti.dao.AddrMapper;
import com.amway.acti.model.Addr;
import com.amway.acti.service.AddrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddrServiceImpl implements AddrService {

    @Autowired
    private AddrMapper addrMapper;

    @Override
    public List<Addr> findCityData() {
        return addrMapper.findCityData();
    }

    @Override
    public List<Addr> findCityDataByProvince(String province) {
        return addrMapper.findCityDataByProvince(province);
    }

    @Override
    public List<Addr> findRegion(String city){
        return addrMapper.findCityDataByProvince(city);
    }
}
