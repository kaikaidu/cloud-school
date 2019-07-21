package com.amway.acti.service;

import com.amway.acti.model.Course;
import com.amway.acti.model.Sitem;
import com.amway.acti.model.SitemTemp;
import net.sf.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * @author Jie.Qiu
 * @create 2018-03-18
 **/
public interface SitemTempService {
    List<SitemTemp> selectSitemTempList(String name,String createTimeType);

    int addSitemTemp(JSONObject jsonObject);


    List<Map<String, Object>> batchSitemTemp(String sitemTempIds);

    SitemTemp querySitemTempData(String sitemTempId,String type);

    List<Sitem> querySitemData(String sitemTempId);

    void editSitemTemp(String data);

    List<SitemTemp> selectSitemTempListByData(String name,String createTimeType,Integer pageNo,Integer pageSize);

    List<Course> batchDelSitemTempDetail(String sitemId);
}
