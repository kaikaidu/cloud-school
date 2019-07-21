package com.amway.acti.dao;

import com.amway.acti.model.CourseExport;
import org.apache.ibatis.annotations.Param;

import java.util.List;
/**
 * @Description:课程导出
 * @Date:2018/3/28 20:30
 * @Author:wsc
 */
public interface CourseExportMapper {

    List<CourseExport> exportCourseById(@Param("list") List<Integer> list,
                                        @Param("name") String name,
                                        @Param("label") String label);
}
