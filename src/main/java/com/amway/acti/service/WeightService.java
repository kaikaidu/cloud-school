package com.amway.acti.service;

import com.amway.acti.dto.ScoreDetailDto;
import com.amway.acti.model.SitemTemp;
import com.amway.acti.model.WeightExport;
import com.amway.acti.model.WeightResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface WeightService {
    boolean hasWeight(int courseId);

    List<WeightResult> queryWeightResult(WeightResult weightResult, Integer pageNo, Integer pageSize);

    String queryWeightResultCount(WeightResult weightResult);

    void exportWeightListExcel(HttpServletResponse response,
                               HttpServletRequest request,WeightResult weightResult);

    List<SitemTemp> selectSitemTemp(String name,String createTimeTye, Integer pageNo,Integer pageSize);

    void saveOrEditWeight(String data);

    Map<String,Object> searchWeightData(String courseId);

    List<ScoreDetailDto> selectScoreDetail(String courseId,String adaNumber, Integer pageNo, Integer pageSize);

    String queryScoreDetailCount(String courseId,String adaNumber);

    boolean hasResult(int courseId);

    List<WeightExport> exportDataListSelf(WeightResult weightResult);

    /////////////////李伟新增  修改评分导出////////////////

    List<Map<String, Object>> getExportInfo(int courseId);
}
