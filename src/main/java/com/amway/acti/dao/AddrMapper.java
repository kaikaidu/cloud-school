package com.amway.acti.dao;

import com.amway.acti.model.Addr;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AddrMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Addr record);

    int insertSelective(Addr record);

    Addr selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Addr record);

    int updateByPrimaryKey(Addr record);

    List<Addr> findCityDataByProvince(String province);

    List<Addr> findCityData();

    String selectCodeByNameLike(@Param("name") String name);

    Addr selectAddrByName(@Param("name") String name,@Param("level") Integer level);

    List<Addr> selectAddrByParentCode(@Param("code") String code);

    String selectNameByCode(@Param("code") String code);
}
