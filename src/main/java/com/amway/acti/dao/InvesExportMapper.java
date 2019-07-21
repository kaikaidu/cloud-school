package com.amway.acti.dao;

import com.amway.acti.model.InvesExport;

import java.util.List;

/**
 * @Description:问卷导出
 * @Date:2018/3/29 17:30
 * @Author:wsc
 */
public interface InvesExportMapper {
    List<InvesExport> selectInvesByCourseId(Integer id);
}
