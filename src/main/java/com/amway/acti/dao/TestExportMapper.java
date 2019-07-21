package com.amway.acti.dao;

import com.amway.acti.model.TestExport;

import java.util.List;

/**
 * @Description:测试导出
 * @Date:2018/3/29 17:29
 * @Author:wsc
 */
public interface TestExportMapper {
    List<TestExport> selectTestByCourseId(Integer id);
}
