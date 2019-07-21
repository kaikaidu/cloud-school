package com.amway.acti.dao;

import com.amway.acti.model.CourseExport;
import com.amway.acti.model.RegisterExport;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description:导出签到
 * @Date:2018/3/28 20:44
 * @Author:wsc
 */
public interface RegisterExportMapper {
    List<RegisterExport> selectRegisterByCourseId(@Param("list") List<CourseExport> list);
}
