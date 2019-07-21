package com.amway.acti.service;

import com.amway.acti.model.Addr;

import java.util.List;

public interface AddrService {

    //查询省级数据
    List<Addr> findCityData();
    //查询市级数据
    List<Addr> findCityDataByProvince(String province);
    //查询区级数据
    List<Addr> findRegion(String city);
}
