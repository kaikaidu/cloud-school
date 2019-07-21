package com.amway.acti.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.amway.acti.base.redis.RedisComponent;
import com.amway.acti.base.exception.AmwayLogicException;
import com.amway.acti.base.exception.AmwaySystemException;
import com.amway.acti.base.util.Constants;
import com.amway.acti.base.util.DownloadUtil;
import com.amway.acti.base.util.ExcelExportUtils;
import com.amway.acti.dao.*;
import com.amway.acti.dto.ScoreDetailDto;
import com.amway.acti.model.*;
import com.amway.acti.service.WeightService;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class WeightServiceImpl implements WeightService{

    private static final String RELEASE = "release";

    private static final String SITEMTEMPID = "sitemTempId";

    private static final String WEIGHTIMPLCOURSEID = "courseId";

    @Autowired
    private WeightMapper weightMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private SitemTempMapper sitemTempMapper;

    @Autowired
    private ScoreResultMapper scoreResultMapper;

    @Autowired
    private SpeeMarkMapper speeMarkMapper;

    @Autowired
    private MclassMapper mclassMapper;

    @Autowired
    private CourseTeacherMapper courseTeacherMapper;

    @Autowired
    private ScoreAnswerMapper scoreAnswerMapper;

    @Autowired
    private DrawResultMapper drawResultMapper;

    @Autowired
    private RedisComponent redisComponent;

    @Override
    public boolean hasWeight(int courseId) {
        log.info("courseId:{}", courseId);
        return !weightMapper.queryWeightListByCourseId(courseId).isEmpty();
    }

    @Override
    public List<WeightResult> queryWeightResult(WeightResult weightResult,Integer pageNo,Integer pageSize) {
        log.info("WeightResult:{},pageNo:{},pageSize{}", weightResult.toString(),pageNo,pageSize);
        PageHelper.startPage(pageNo,pageSize);
        List<WeightResult> weightResultList = weightMapper.queryWeightList(weightResult);
        List<WeightResult> weightResultList0 = assemblyData(weightResult,weightResultList);
        List<Mclass> mclassList = mclassMapper.selectByCourseId(weightResult.getCourseId());
        List<WeightResult> resultList = new ArrayList<>();

        for(Mclass mclass : mclassList){
            mergeClass(mclass,weightResultList0,resultList);
        }
        return resultList;
    }

    /**
     * 按班级合并
     * @param mclass
     * @param weightResultList
     * @param resultList
     * @return
     */
    private List<WeightResult> mergeClass(Mclass mclass,List<WeightResult> weightResultList,List<WeightResult> resultList){

        List<WeightResult> tempList = new ArrayList<>();
        for(WeightResult weightResult : weightResultList){
            if(mclass.getId().equals(weightResult.getClassId())){
                tempList.add(weightResult);
            }
        }

        //为每条数据加索引
        for(WeightResult w : tempList){
            if(mclass.getId().equals(w.getClassId())){
                w.setIndex(tempList.indexOf(w));
                w.setNumber(tempList.size());
            }
        }
        resultList.addAll(tempList);
        return null;
    }

    @Override
    public String queryWeightResultCount(WeightResult weightResult){
        log.info("WeightResult:{}", weightResult.toString());
        List<WeightResult> weightResultList = weightMapper.queryWeightList(weightResult);
        List<WeightResult> weightResultList0 = assemblyData(weightResult,weightResultList);
        List<Mclass> mclassList = mclassMapper.selectByCourseId(weightResult.getCourseId());
        List<WeightResult> resultList = new ArrayList<>();

        for(Mclass mclass : mclassList){
            mergeClass(mclass,weightResultList0,resultList);
        }
        int count = resultList.size();
        return String.valueOf(count);
    }

    @Override
    public void exportWeightListExcel(HttpServletResponse response, HttpServletRequest request,WeightResult weightResult){
        log.info("weightResult:{}", weightResult.toString());
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String[] headers = {"日期","课程ID","学员姓名", "安利卡号", "性别", "状态", "得分"};
            OutputStream out = response.getOutputStream();// 取得输出流
            Date dste = new Date();
            String tis = sdf.format(dste);
            tis = "评分结果" + tis + ".xls";
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-disposition", "attachment;filename=" + DownloadUtil.encodeDownloadFilename(tis,request));
            ExcelExportUtils.exportExcel(headers, exportDataListSelf(weightResult), out);

        }catch (Exception e){
            log.info("WeightController---->评分结果导出异常");
            log.error(e.getMessage(),e);
        }
    }

    @Override
    public List<SitemTemp> selectSitemTemp(String name,String createTimeTye,Integer pageNo,Integer pageSize){
        log.info("name:{},createTimeTye{},pageNo:{},pageSize:{}",name,createTimeTye,pageNo,pageSize);
        PageHelper.startPage(pageNo,pageSize);
        return sitemTempMapper.selectSitemTempList(name,createTimeTye);
    }

    @Override
    @Transactional
    public void saveOrEditWeight(String data){
        log.info("data:{}", data);
        JSONObject jsonObject = JSONObject.parseObject(data);
        //抽签后结果
        List<DrawResult> drawResultList = drawResultMapper.selectDrawResultByCourseId(Integer.parseInt(jsonObject.getString(WEIGHTIMPLCOURSEID)));
        if(jsonObject.get(RELEASE).toString().equals("1") && drawResultList.isEmpty()){
            throw new AmwaySystemException("当前课程还未进行抽签,无法上架!");
        }
        //如果没有分班则不能上架
        if(jsonObject.get(RELEASE).toString().equals("1") && null == drawResultList.get(0).getClassDrawId()){
            throw new AmwaySystemException("当前课程还未进行抽签,无法上架!");
        }
        //用来将传入的数据整除处理
        Course course = courseMapper.selectByPrimaryKey(Integer.parseInt(jsonObject.getString(WEIGHTIMPLCOURSEID)));
        //通过课程id找原来的模板id
        List<Weight>  weightList =weightMapper.queryWeightListByCourseId(course.getSysId());
        //判断有没有修改权重的值
        boolean isEditWeight = false;
        if(weightList.size() == 2){
            //修改
            for(Weight tmp:weightList){
                if(tmp.getIdent() == Constants.USER_IDENT_STUDENT){
                    Weight weight = new Weight();
                    weight.setId(tmp.getId());
                    weight.setCourseId(course.getSysId());
                    weight.setIdent(Constants.USER_IDENT_STUDENT);
                    weight.setTempId(Integer.parseInt(jsonObject.getString(SITEMTEMPID)));
                    weight.setRelease(Short.parseShort(jsonObject.getString(RELEASE)));
                    weight.setWeight(new BigDecimal(jsonObject.getString("stu")));
                    isEditWeight = isEditWeight(tmp.getWeight(),weight.getWeight());
                    weightMapper.updateByPrimaryKeySelective(weight);
                }else if(tmp.getIdent() == Constants.USER_IDENT_LECTURER){
                    Weight weight = new Weight();
                    weight.setId(tmp.getId());
                    weight.setCourseId(course.getSysId());
                    weight.setIdent(Constants.USER_IDENT_LECTURER);
                    weight.setTempId(Integer.parseInt(jsonObject.getString(SITEMTEMPID)));
                    weight.setRelease(Short.parseShort(jsonObject.getString(RELEASE)));
                    weight.setWeight(new BigDecimal(jsonObject.getString("teac")));
                    weightMapper.updateByPrimaryKeySelective(weight);
                }
            }
            //修改权重后重新计算结果
            if(!isEditWeight){
                List<SpeeMark> speeMarkList = speeMarkMapper.selectByCourseId(course.getSysId().toString());
                for (SpeeMark speeMark : speeMarkList){
                    speeMark.setScore(scoreResultMapper.selectTotalScoreByStuId(speeMark.getCourseId(),speeMark.getUserId()));
                    speeMarkMapper.updateByPrimaryKeySelective(speeMark);
                }
            }
        }else if(weightList.isEmpty()){
            //新增
            Weight weight = new Weight();
            weight.setCourseId(course.getSysId());
            weight.setTempId(Integer.parseInt(jsonObject.getString(SITEMTEMPID)));
            weight.setRelease(Short.parseShort(jsonObject.getString(RELEASE)));
            weight.setIdent(Constants.USER_IDENT_LECTURER);
            weight.setState(Constants.States.VALID);
            weight.setWeight(new BigDecimal(jsonObject.getString("teac")));
            weightMapper.insertSelective(weight);
            weight.setIdent(Constants.USER_IDENT_STUDENT);
            weight.setWeight(new BigDecimal(jsonObject.getString("stu")));
            weightMapper.insertSelective(weight);
        }else {
            throw new AmwayLogicException(Constants.ErrorCode.PARAM_EXCEPTION,"课程号:"+course.getSysId()+" 的相关权重数据错误,请联系管理员!");
        }
        //小程序端展示(0:否 1:是)
        course.setIsScore(Byte.valueOf(jsonObject.getString(RELEASE)));
        //模板赋值到课程中
        course.setSitemTempId(Integer.parseInt(jsonObject.getString(SITEMTEMPID)));
        courseMapper.updateByPrimaryKeySelective(course);

        //清理课程详情缓存
        String key = Constants.COURSE_TEST_PAPER_USER_LIST + course.getSysId();
        String[] keys = redisComponent.keys( key );
        redisComponent.del( keys );

        //清理评分缓存
        key = Constants.SITEM_CACHE_KEY + course.getSysId();
        keys = redisComponent.keys( key );
        redisComponent.del( keys );
    }

    @Override
    public Map<String,Object> searchWeightData(String courseId) {
        log.info("courseId:{}", courseId);
        //将查询出的数据乘以100在页面显示
        BigDecimal b = new BigDecimal(100);
        Course course = courseMapper.selectByPrimaryKey(Integer.parseInt(courseId));
        SitemTemp sitemTemp = sitemTempMapper.selectByPrimaryKey(course.getSitemTempId());
        List<Weight> weightList = weightMapper.queryWeightListByCourseId(course.getSysId());
        Map<String,Object> map = new HashMap<>();
        map.put("name",sitemTemp.getName());
        map.put(SITEMTEMPID,sitemTemp.getId());
        for(Weight weight:weightList){
            map.put(RELEASE,weight.getRelease());
            if(weight.getIdent() == Constants.USER_IDENT_STUDENT){
                map.put("stu",weight.getWeight().multiply(b));
            }else if(weight.getIdent() == Constants.USER_IDENT_LECTURER){
                map.put("teac",weight.getWeight().multiply(b));
            }
        }
        return map;
    }

    public List<ScoreDetailDto> selectScoreDetail(String courseId,String adaNumber,Integer pageNo, Integer pageSize){
        log.info("courseId:{},adaNumber:{},pageNo:{},pageSize{]",courseId,adaNumber,pageNo,pageSize);
        PageHelper.startPage(pageNo,pageSize);
        return scoreResultMapper.selectScoreDetail(courseId,adaNumber);
    }

    public String queryScoreDetailCount(String courseId,String adaNumber){
        log.info("courseId:{},adaNumber:{}", courseId,adaNumber);
        return String.valueOf(scoreResultMapper.selectScoreDetail(courseId,adaNumber).size());
    }

    //判断有没有修改权重的值
    public boolean isEditWeight(BigDecimal a,BigDecimal b){
       return a.compareTo(b)==0;
    }

    //分班抽签演讲后计算分数
    public void calculateScore(WeightResult weightResult,WeightResult tmp){
        //查看给演讲者打分的记录
        List<ScoreResult> scoreResultList = scoreResultMapper.selectByCourseIdAndStuId(weightResult.getCourseId(),tmp.getId());
        Integer classId = speeMarkMapper.selectByCourseId(weightResult.getCourseId().toString()).get(0).getClassId();
        if (null == classId) {
            throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"课程ID:"+weightResult.getCourseId()+"未查询到班级ID");
        }
        //查询班级的人数
        Integer stuNum = mclassMapper.selectByPrimaryKey(classId).getNumber();
        //查询讲师数量
        Integer teacNum = courseTeacherMapper.selectByCourseIdAndClassId(weightResult.getCourseId(),classId).size();
        if((stuNum+teacNum) > scoreResultList.size()){
            tmp.setScore(new BigDecimal("0.00"));
            tmp.setStatus("未完成");
            tmp.setCreateTime("");
        }else {
            tmp.setStatus("已完成");
        }
    }

    @Override
    public boolean hasResult(int courseId){
        return scoreAnswerMapper.countScoreBySitemId( courseId ) > 0;
    }

    //处理数据
    public List<WeightResult> assemblyData(WeightResult weightResult,List<WeightResult> weightResultList){
        List<Mclass> mclassList = mclassMapper.selectByCourseId(weightResult.getCourseId());
        List<WeightResult> resultList = new ArrayList<>();
        //未分班
        if (mclassList.isEmpty()) {
            for (WeightResult tmp : weightResultList) {
                tmp.setScore(new BigDecimal("0.00"));
                tmp.setStatus("未完成");
                resultList.add(tmp);
            }
        }else{
            for(WeightResult tmp : weightResultList){
                SpeeMark speeMark = new SpeeMark();
                speeMark.setUserId(tmp.getId());
                speeMark.setCourseId(tmp.getCourseId());
                //按班里分有没有演讲
                if(StringUtils.isEmpty(speeMarkMapper.selectByCourseIdAndClassIdAndUid(speeMark))){
                    tmp.setStatus("未完成");
                    tmp.setScore(new BigDecimal("0.00"));
                }else {
                    //分班抽签演讲后计算分数
                    calculateScore(weightResult,tmp);
                }
                resultList.add(tmp);
            }
        }
        return resultList;
    }

    //自定义导出接口数据
    @Override
    public List<WeightExport> exportDataListSelf(WeightResult weightResult){
        List<WeightResult> weightResultList = weightMapper.queryWeightList(weightResult);
        List<WeightResult> resultList = assemblyData(weightResult,weightResultList);
        List<WeightExport> exportResultList = new ArrayList<>();
        for(WeightResult w : resultList){
            WeightExport weightExport = new WeightExport();
            weightExport.setCreateTime(w.getCreateTime());
            weightExport.setCourseId(w.getCourseId());
            weightExport.setUserName(w.getName());
            weightExport.setAdaNumber(w.getAdaNumber());
            weightExport.setSex(w.getSex());
            weightExport.setState(w.getStatus());
            weightExport.setAnswer(w.getScore().toString());
            exportResultList.add(weightExport);
        }
        return exportResultList;
    }

    @Override
    public List<Map<String, Object>> getExportInfo(int courseId){
        List<Mclass> mclasses = mclassMapper.selectByCourseId(courseId);
        if(mclasses == null || mclasses.isEmpty()){
            return null;
        }
        List<Map<String, Object>> list = new ArrayList<>();
        // 遍历classes
        for (Mclass mclass : mclasses){
            Map<String, Object> info = new HashMap<>();
            info.put("className", mclass.getName());
            /*// 查询此班级下的学员
            List<Map<String, Object>> stuInfos = drawResultMapper.selectStuInfosByClassId(mclass.getId());
            info.put("students", stuInfos);
            // 查询此班级下的讲师
            List<Map<String, Object>> teaInfos = drawResultMapper.selectTeaInfosByClassId(mclass.getId());
            info.put("teachers", teaInfos);
            // 设置学员和讲师总和
            info.put("totalSize", (stuInfos != null ? stuInfos.size() : 0) + (teaInfos != null ? teaInfos.size() : 0) );
            // 查询此班级下其他学员和讲师对本学员的评价*/
            info.put("gradeInfos", getGradeInfosByClassId(courseId, mclass.getId()));
            list.add(info);
        }
        return list;
    }

    private List<Map<String, Object>> getGradeInfosByClassId(int courseId, int classId){
        List<Map<String, Object>> stuBaseInfos = drawResultMapper.selectStuBaseInfosByClassId(classId);
        for(Map<String, Object> baseInfo : stuBaseInfos){
            int userId = (int) baseInfo.get("userId");
            List<Map<String, Object>> stuScores = drawResultMapper.selectStuScoreInfos(courseId, classId, userId);
            baseInfo.put("stuScoreInfos", stuScores);
            List<Map<String, Object>> teaScores = drawResultMapper.selectTeaScoreInfos(courseId, classId, userId);
            baseInfo.put("teaScoreInfos", teaScores);
            // 判断评分完成情况，只要有一个人没有评分，就是未完成，否则已完成
            boolean flag = true;
            if(stuScores != null && !stuScores.isEmpty()){
                for(Map<String, Object> temp : stuScores){
                    if(temp.get("score") == null){
                        flag = false;
                        break;
                    }
                }
            }
            if(flag){
                if(teaScores != null && !teaScores.isEmpty()){
                    for(Map<String, Object> temp : teaScores){
                        if(temp.get("score") == null){
                            flag = false;
                            break;
                        }
                    }
                }
            }else{
                // 将成绩改成null
                baseInfo.put("score", null);
            }
            baseInfo.put("state", flag ? "已完成" : "未完成");
        }
        return stuBaseInfos;
    }
}
