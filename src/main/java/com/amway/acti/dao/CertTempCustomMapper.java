package com.amway.acti.dao;

import com.amway.acti.model.Cert;
import com.amway.acti.model.CertTemp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description:证书设置
 * @Date:2018/6/6 11:36
 * @Author:wsc
 */
public interface CertTempCustomMapper {
    List<CertTemp> findCertTempList(@Param("time") String timeSort, @Param("name") String name);

    CertTemp selectCertTempByCourseId(@Param("courseId") Integer courseId);
}
