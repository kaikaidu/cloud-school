package com.amway.acti.service.impl;

import com.amway.acti.base.exception.AmwayLogicException;
import com.amway.acti.base.util.Constants;
import com.amway.acti.dao.CourseMapper;
import com.amway.acti.dao.SitemMapper;
import com.amway.acti.dao.SitemTempMapper;
import com.amway.acti.model.Course;
import com.amway.acti.model.Sitem;
import com.amway.acti.model.SitemTemp;
import com.amway.acti.service.SitemTempService;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author Jie.Qiu
 * @create 2018-03-18
 **/
@Service
@Slf4j
public class SitemTempServiceImpl implements SitemTempService {

    @Autowired
    private SitemTempMapper sitemTempMapper;

    @Autowired
    private SitemMapper sitemMapper;

    @Autowired
    private CourseMapper courseMapper;

   /* private static final String GRADE_A = "优秀（75~100）,";

    private static final String GRADE_B = "良好（50~74）,";

    private static final String GRADE_C = "一般（25~49）,";

    private static final String GRADE_D = "待改进（0~24）";*/

   private static final String MS="60分-表现标准、80分-表现优秀、100分表现卓越";

    public List<SitemTemp> selectSitemTempList(String name,String createTimeType){
        log.info("name:{}",name);
        return sitemTempMapper.selectSitemTempList(name,createTimeType);
    }

    @Transactional
    public int addSitemTemp(JSONObject jsonObject){
        //判断该模板名称是否被占用
        SitemTemp temp = sitemTempMapper.selectSitemByName(jsonObject.getString("name"));
        if(!StringUtils.isEmpty(temp)){
            throw new AmwayLogicException(Constants.ErrorCode.PARAM_EXCEPTION, "模板名称已存在");
        }
        SitemTemp sitemTemp = new SitemTemp();
        sitemTemp.setName(jsonObject.getString("name"));
        sitemTemp.setStand(MS);
        sitemTemp.setState(Constants.Number.SHORT_NUMBER1);
        Date date = new Date();
        sitemTemp.setCreateTime(date);
        sitemTempMapper.insertSelective(sitemTemp);

        JSONArray jsonArray = jsonObject.getJSONArray("questions");
        List<Sitem> list = new ArrayList<>();
        for(int i = 0; i < jsonArray.size(); i++){
            JSONObject item = jsonArray.getJSONObject(i);
            String question = item.getString("question");
            String ratio = item.getString("ratio");
            Sitem sitem = new Sitem();
            sitem.setQuestion(question);
            sitem.setOptions(MS);
            sitem.setState(Constants.States.VALID);
            sitem.setSitemTempId(sitemTemp.getId());
            sitem.setOrder(i+1);
            sitem.setCreateTime(new Date());
            sitem.setRate(new BigDecimal(ratio));
            list.add(sitem);
        }
        //  入库操作
        return sitemMapper.addSitem(list);
    }

    @Transactional
    public List<Map<String, Object>> batchSitemTemp(String sitemTempIds){
        log.info("sitemTempIds:{}",sitemTempIds);
        List<Map<String, Object>> list = new ArrayList <>();
        String[] ids = sitemTempIds.split(",");
        for(String id : ids){
            Map<String, Object> map = new HashMap<>();
            map.put("id",id);
            SitemTemp temp = sitemTempMapper.selectByPrimaryKey(Integer.parseInt(id));
            map.put("name",temp.getName()== null ? "模板":temp.getName());
            //查询是否被课程引用
            List<Course> courseList = courseMapper.selectCourseBySitemTemp(Integer.parseInt(id));
            if(courseList == null || courseList.isEmpty()){
                SitemTemp sitemTemp = new SitemTemp();
                sitemTemp.setId(Integer.parseInt(id));
                sitemTemp.setState(Constants.States.NO_AVAI);
                sitemTempMapper.updateByPrimaryKeySelective(sitemTemp);
                map.put( "isUsed", false);
            }else {
                map.put( "isUsed", true);
            }
            list.add(map);
        }
        return list;
    }

    public SitemTemp querySitemTempData(String sitemTempId,String type){
        log.info("sitemTempId:{},type:{}",sitemTempId,type);
        SitemTemp sitemTemp = sitemTempMapper.selectByPrimaryKey(Integer.parseInt(sitemTempId));
        sitemTemp.setType(type);
        return sitemTemp;
    }

    public List<Sitem> querySitemData(String sitemTempId){
        log.info("sitemTempId:{}",sitemTempId);
        return sitemMapper.selectBySitemTempId(Integer.parseInt(sitemTempId));
    }

    @Transactional
    public void editSitemTemp(String data){
        log.info("data:{}",data);
        JSONObject jsonObject = JSONObject.fromObject(data);
        //克隆操作
        if("copy".equals(jsonObject.getString("sitemTempType"))){
            addSitemTemp(jsonObject);
            return;
        }
        String sitemTempName = jsonObject.getString("name");
        int sitemTempId = jsonObject.getInt("sitemTempId");
        SitemTemp tempOld = sitemTempMapper.selectByPrimaryKey(sitemTempId);
        SitemTemp temp = sitemTempMapper.selectSitemByName(sitemTempName);
        //判断编辑时是否修改了模板名称,判断该模板名称是否被占用
        if(!tempOld.getName().equals(sitemTempName) && !StringUtils.isEmpty(temp)){
            throw new AmwayLogicException(Constants.ErrorCode.PARAM_EXCEPTION, "模板名称已存在");
        }
        //按照id先将模板下所有数据逻辑删除，然后根据传入的数据更新展示
        SitemTemp sitemTemp = new SitemTemp();
        sitemTemp.setId(sitemTempId);
        sitemTemp.setName(sitemTempName);
        //更新模板名称
        sitemTempMapper.updateByPrimaryKeySelective(sitemTemp);

        JSONArray questionsArray = jsonObject.getJSONArray("questions");
        Short sitemState = 1;
        List<Sitem> sitemsOld = sitemMapper.selectBySitemTempId(sitemTempId);
        List<Integer> sitemIdsOld = new ArrayList<>();
        for(Sitem sitem : sitemsOld){
            sitemIdsOld.add(sitem.getId());
        }
        for(int i=0; i<questionsArray.size(); i++){
            JSONObject jobj = questionsArray.getJSONObject(i);
            Sitem sitemLast = new Sitem();
            sitemLast.setState(sitemState);
            sitemLast.setOrder(i+1);
            sitemLast.setQuestion(jobj.getString("question"));
            sitemLast.setRate(new BigDecimal(jobj.getString("ratio")));
            if(StringUtils.isEmpty(jobj.getString("sitemId"))){
                sitemLast.setCreateTime(new Date());
                sitemLast.setSitemTempId(sitemTempId);
                sitemLast.setOptions(MS);
                //如果是空执行新增操作
                sitemMapper.insertSelective(sitemLast);
            }else {
                // 删除id
                sitemIdsOld.remove(Integer.valueOf(jobj.getInt("sitemId")));
                sitemLast.setId(jobj.getInt("sitemId"));
                //通过id更新修改后的数据
                sitemMapper.updateByPrimaryKeySelective(sitemLast);
            }
        }
        for(Integer id : sitemIdsOld){
            sitemMapper.deleteByPrimaryKey(id);
        }
    }

    public List<SitemTemp> selectSitemTempListByData(String name,String createTimeType,Integer pageNo,Integer pageSize){
        log.info("name:{},createTimeType{},pageNo:{},pageSize:{}",name,createTimeType,pageNo,pageSize);
        PageHelper.startPage(pageNo,pageSize);
        return sitemTempMapper.selectSitemTempList(name,createTimeType);
    }

    public  List<Course> batchDelSitemTempDetail(String sitemTempId){
        List<Course> list = courseMapper.selectCourseBySitemTemp(Integer.parseInt(sitemTempId));
        return list == null ? null : list;
    }
}
