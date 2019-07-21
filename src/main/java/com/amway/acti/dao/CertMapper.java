package com.amway.acti.dao;

import com.amway.acti.model.Cert;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CertMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cert record);

    int insertSelective(Cert record);

    Cert selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cert record);

    int updateByPrimaryKey(Cert record);

    List<Integer> selectCourseIdByTempId(@Param("certTempId") Integer certTempId);

    void updateStateByCertTempId(@Param("certTempId") Integer certTempId);

    List<Integer> selectIdByTempId(@Param("certTempId") Integer certTempId);

}
