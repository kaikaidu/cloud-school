package com.amway.acti.dao;

import com.amway.acti.model.CertExport;
import com.amway.acti.model.CourseExport;
import org.apache.ibatis.annotations.Param;

import java.util.List; /**
 * @Description:
 * @Date:2018/5/30 16:08
 * @Author:wsc
 */
public interface CertExportMapper {
    List<CertExport> selectCertExport(@Param("list") List<CourseExport> courseList);
}
