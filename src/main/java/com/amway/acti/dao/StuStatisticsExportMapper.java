package com.amway.acti.dao;

import com.amway.acti.model.CourseExport;
import com.amway.acti.model.StuStatisticsExport;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface StuStatisticsExportMapper {
    List<StuStatisticsExport> selectStuStatistics(@Param("list") List<CourseExport> courseList, @Param("date") String date);
}
