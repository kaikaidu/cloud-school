package com.amway.acti.dao;

import com.amway.acti.model.Cert;
import org.apache.ibatis.annotations.Param;

/**
 * @Description:
 * @Date:2018/6/6 15:24
 * @Author:wsc
 */
public interface CertCustomMapper {
    Cert selectCertByCourseId(@Param("courseId") Integer courseId);
}
