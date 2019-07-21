package com.amway.acti.dao;

import com.amway.acti.model.CourseExport;
import com.amway.acti.model.SignStuExport;
import org.apache.ibatis.annotations.Param;

import java.util.List; /**
 * @Description:
 * @Date:2018/5/30 13:32
 * @Author:wsc
 */
public interface SignStuExportMapper {
    List<SignStuExport> selectSignStuExports(@Param("list") List<CourseExport> courseList);
}
