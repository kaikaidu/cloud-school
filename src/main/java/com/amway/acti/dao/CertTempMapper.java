package com.amway.acti.dao;

import com.amway.acti.model.CertTemp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CertTempMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CertTemp record);

    int insertSelective(CertTemp record);

    CertTemp selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CertTemp record);

    int updateByPrimaryKey(CertTemp record);

    List<CertTemp> selectCertTemplateListByName(@Param("name") String name,
                                                @Param("sort") String sort);

    int updateStateById(@Param("id") Integer id);

    int selectByName(@Param("name") String name);
}
