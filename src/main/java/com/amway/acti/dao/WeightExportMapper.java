package com.amway.acti.dao;

import com.amway.acti.model.WeightExport;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description:评分导出
 * @Date:2018/3/28 20:30
 * @Author:wsc
 */
public interface WeightExportMapper {
    List<WeightExport> selectWeightByCourseId(@Param("id") Integer id);
}
