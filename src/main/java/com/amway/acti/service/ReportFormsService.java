package com.amway.acti.service;

import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.model.*;
import com.github.pagehelper.PageInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Description:报表自定义
 * @Date:2018/3/27 19:12
 * @Author:wsc
 */
public interface ReportFormsService {
    CommonResponseDto getCourseCount(String name,String label);

    CommonResponseDto<PageInfo<CourseExport>> courseList(Integer pageNo, Integer pageSize, String name, String sort, String label);

    void exportReportForms(HttpServletResponse response, HttpServletRequest request,String ids ,String name, String label) throws Exception;

    void exportTestByCourseId(List<TestExport> testExports, Integer id);

    void exportInvesByCourseId(List<InvesExport> invesExports, Integer id);

    void exportWeightByCourseId(List<WeightExport> weightExports, Integer id);

    ExportReportForms getExportList(String ids, String name, String label) throws Exception;
}
