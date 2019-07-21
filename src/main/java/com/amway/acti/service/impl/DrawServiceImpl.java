/**
 * Created by dk on 2018/3/2.
 */

package com.amway.acti.service.impl;

import com.amway.acti.base.exception.AmwaySystemException;
import com.amway.acti.base.redis.RedisComponent;
import com.amway.acti.base.util.Constants;
import com.amway.acti.base.util.RandomUtil;
import com.amway.acti.dao.ClassDrawMapper;
import com.amway.acti.dao.CourseApprovalMapper;
import com.amway.acti.dao.CourseMapper;
import com.amway.acti.dao.CourseSignUpMapper;
import com.amway.acti.dao.CourseTeacherMapper;
import com.amway.acti.dao.DrawResultMapper;
import com.amway.acti.dao.MclassMapper;
import com.amway.acti.dao.ScoreResultMapper;
import com.amway.acti.dao.UserMapper;
import com.amway.acti.dao.WeightMapper;
import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.dto.DrawResultDto;
import com.amway.acti.model.ClassDraw;
import com.amway.acti.model.Course;
import com.amway.acti.model.CourseTeacher;
import com.amway.acti.model.DrawCuts;
import com.amway.acti.model.DrawResult;
import com.amway.acti.model.DrawResultData;
import com.amway.acti.model.Mclass;
import com.amway.acti.model.MclassTeacSpee;
import com.amway.acti.model.Teacher;
import com.amway.acti.model.User;
import com.amway.acti.service.ClassSettingService;
import com.amway.acti.service.DrawService;
import com.amway.acti.service.RedisService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Slf4j
@Service

public class DrawServiceImpl implements DrawService {

    //班级
    @Autowired
    private MclassMapper mclassMapper;

    //演讲结果
    @Autowired
    private DrawResultMapper drawResultMapper;

    //用户
    @Autowired
    private UserMapper userMapper;

    //课程
    @Autowired
    private CourseMapper courseMapper;

    //报名审批
    @Autowired
    private CourseApprovalMapper courseApprovalMapper;

    //课程报名
    @Autowired
    private CourseSignUpMapper courseSignUpMapper;

    //班级讲师关联
    @Autowired
    private CourseTeacherMapper courseTeacherMapper;


    @Autowired
    private ClassSettingService classSettingService;

    //演讲内容
    @Autowired
    private ClassDrawMapper classDrawMapper;

    //评分结果
    @Autowired
    private ScoreResultMapper scoreResultMapper;

    //权重(评分模板)
    @Autowired
    private WeightMapper weightMapper;

    @Autowired
    private RedisComponent redisComponent;

    @Autowired
    private RedisService redisService;

    /***
     * 抽签结果查询
     * @param uid
     * @param courseId
     * @return
     */
    @Override
    public DrawResultDto drawResult(Integer uid, Integer courseId) {
        log.info(">>>Begin drawResult");
        log.info("uid:{}", uid);
        log.info("courseId:{}", courseId);
        List<Mclass> classList = mclassMapper.selectByCourseId(courseId);
        for (Mclass mclass : classList) {
            DrawResult drawResult = new DrawResult();
            drawResult.setUserId(uid);
            drawResult.setClassId(mclass.getId());
            DrawResult dr = drawResultMapper.selectByClassIdAndUserId(drawResult);
            if (dr != null) {
                //add wsc
                ClassDraw classDraw = classDrawMapper.selectByPrimaryKey(dr.getClassDrawId());
                DrawResultDto drawResultDto = new DrawResultDto();
                drawResultDto.setClassName(mclass.getName());
                //update wsc
                drawResultDto.setContent(classDraw.getContent());
                drawResultDto.setNumber(Integer.parseInt(classDraw.getCode()));
                return drawResultDto;
            }
        }
        log.info(">>>End drawResult");
        return null;
    }

    /**
     * @Description:根据课程id查询班级
     * @Date: 2018/3/16 17:33
     * @param: courseId 课程id
     * @Author: wsc
     */
    @Override
    public List <Teacher> findDrawByCourseId(Integer courseId) {
        log.info("findDrawByCourseId-->courseId:{}",courseId);
        List<Teacher> teacherList = new ArrayList <>();
        List<Mclass> mclassList = mclassMapper.selectByCourseId(courseId);
        for (Mclass c : mclassList) {
            Teacher teacher = new Teacher();
            List<CourseTeacher> courseTeachers = courseTeacherMapper.selectByCourseIdAndClassId(courseId,c.getId());
            //封装数据
            mclassforTeachar(c,courseTeachers,teacher);
            teacherList.add(teacher);
        }
        log.info("teacherList:{}",teacherList);
        return teacherList;
    }

    //封装对象
    private void mclassforTeachar(Mclass c, List<CourseTeacher> courseTeachers, Teacher teacher) {
        teacher.setId(c.getId());
        teacher.setCourseId(c.getCourseId());
        teacher.setCreateTime(c.getCreateTime());
        teacher.setName(c.getName());
        teacher.setNumber(c.getNumber());
        teacher.setState(c.getState());
        teacher.setInitNumber(c.getInitNumber());
        if (null != courseTeachers && !courseTeachers.isEmpty()) {
            teacher.setIsDistribution(Constants.Number.INT_NUMBER1);
        }
    }

    /**
     * @Description: 根据学员姓名查询分班
     * @Date: 2018/3/19 17:36
     * @param: pageNo 当前页
     * @param: pageSize 每页数量
     * @param: name 姓名
     * @Author: wsc
     */
    @Override
    public PageInfo<DrawCuts> findDrawUser(Integer pageNo, Integer pageSize, Integer courseId) {
        log.info("pageNo:{},pageSize:{}",pageNo,pageSize);
        PageHelper.startPage(pageNo,pageSize);
        List<DrawCuts> drawCutsList = drawResultMapper.selectDrawUser(courseId);
        return new PageInfo <>(drawCutsList);
    }

    /**
     * @Description:获取分班的总记录数
     * @Date: 2018/3/20 9:18
     * @param: name 姓名
     * @param: courseId 课程id
     * @Author: wsc
     */
    @Override
    public CommonResponseDto getDrawCount(String name, Integer courseId) {
        log.info("name:{},courseId:{}",name,courseId);
        int count = drawResultMapper.selectDrawCount(name,courseId);
        log.info("count:{}",count);
        return CommonResponseDto.ofSuccess(count);
    }


    /**
     * @Description: 根据课程id获取已报名学员数量
     * @Date: 2018/3/20 13:51
     * @param: courseId 课程id
     * @Author: wsc
     */
    @Override
    public int selectSignupCount(Integer courseId) {
        log.info("selectSignupCount-->courseId:{}",courseId);
        //根据课程id查询课程详细信息 判断是否需要审核
        Course course = courseMapper.selectByPrimaryKey(courseId);
        log.info("course:{}",course);
        //需要审核：关联审核表校验是否审核通过 1：需要 0：不需要
        if (course.getIsVerify() == Constants.Number.SHORT_NUMBER1) {
            return courseApprovalMapper.selectSignupCount(courseId);
        } else {
            return courseSignUpMapper.selectSignupCount(courseId);
        }
    }

    /**
     * @Description:创建班级
     * @Date: 2018/3/21 10:06
     * @param: userCount 学员总人数
     * @param: classCount 每班人数
     * @param: model
     * @Author: wsc
     */
    @Override
    @Transactional
    public CommonResponseDto addClass(Integer userCount, Integer classCount, Integer courseId) throws Exception{
        long time1 = System.currentTimeMillis();
        log.info("userCount:{},classCount:{},courseId:{}",userCount,classCount,courseId);
        //根据课程id查询班级 存在：重建班级，删除关联数据 -- 不存在：新建班级
        List<Mclass> mclassList = mclassMapper.selectByCourseId(courseId);
        //班级存在
        if (null != mclassList && !mclassList.isEmpty()) {
            //删除班级及关联的学员，演讲内容，讲师
            classSettingService.deleteClassByCourseId(courseId);
        }
        //添加班级关联学员
        addClassRelationStudent(userCount,classCount,courseId);
        //删除缓存中的评分
        deleteRedisScoreMap(courseId);
        String key = Constants.COURSE_USER_INFO + courseId;
        String[] keys = redisComponent.keys( key );
        redisComponent.del( keys );

        //修改课程表的抽签，评分状态，删除权重表
        Course course = new Course();
        course.setSysId(courseId);
        course.setIsBallot(Constants.Number.BYTE_NUMBER0);//抽签
        course.setIsScore(Constants.Number.BYTE_NUMBER0);//评分
        courseMapper.updateByPrimaryKeySelective(course);
        //删除权重
        weightMapper.deleteWeightByCourseId(courseId);
        long time2 = System.currentTimeMillis();
        log.info("addClassTime:{}",time2 - time1);
        return CommonResponseDto.ofSuccess();
    }

    //删除缓存中评分
    private void deleteRedisScoreMap(int courseId) {
        if (redisComponent.hasKey(Constants.SITEM_CACHE_KEY + courseId)) {
            ObjectMapper objectMapper = new ObjectMapper();
            Object obj = redisService.getValue(Constants.SITEM_CACHE_KEY + courseId);
            Map<String, String> map = new HashMap<>();
            try {
                map = objectMapper.readValue(obj.toString(), new TypeReference<HashMap<String, String>>() {});
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            for(String s:map.keySet()){
                redisComponent.del(Constants.SITEM_CACHE_KEY+map.get(s));
            }
            redisComponent.del(Constants.SITEM_CACHE_KEY + courseId);
        }
    }

    //添加班级关联学员
    private void addClassRelationStudent(Integer userCount, Integer classCount, Integer courseId){
        int j = userCount;
        //得到几个班级
        double count = Math.ceil(userCount.doubleValue() / classCount.doubleValue());
        //得到班级名称
        String[] letter = this.getLetter(count);
        String className;
        int first;//最开始下标
        int last = 0;//最后的下标
        Mclass mclass;
        //根据课程id查询所有学员
        List<User> userList = selectUserByCourseId(courseId);
        List<DrawResult> drawResultList = new ArrayList <>();
        for (int i = 0; i < count ; i++) {
            mclass = new Mclass();
            className = letter[i] +"班";
            first = last;
            last = last + (classCount > j ? j : classCount);
            if ((i+1) < count) {
                j = j - classCount;
                insertClass(className,classCount,courseId,mclass);
            } else {
                insertClass(className,j,courseId,mclass);
            }

            List<User> users = userList.subList(first,last);
            for (User u : users ) {
                DrawResult drawResult = new DrawResult();
                drawResult.setCourseId(courseId);
                drawResult.setCreateTime(new Date());
                drawResult.setState(Constants.States.VALID);
                drawResult.setClassId(mclass.getId());
                drawResult.setUserId(u.getId());
                drawResultList.add(drawResult);
            }
        }
        //批量新增
        insertDrawResultList(drawResultList);
    }

    //批量新增
    private void insertDrawResultList(List<DrawResult> drawResultList) {
        int pointsDataLimit = 250;//限制条数
        int size = drawResultList.size();
        //数量大于2000 分批处理 sqlserver 执行sql时有参数个数限制，参数不能超过2100个
        if (size > pointsDataLimit) {
            int partNum = (int)Math.ceil((double)size/(double)pointsDataLimit);//分批数
            log.info("drawResultList.size:{},partNum:{}",size,partNum);
            for (int i = 0 ; i < partNum ; i ++) {
                if (i+1 == partNum) {
                    pointsDataLimit = drawResultList.size();
                }
                List<DrawResult> listPage = drawResultList.subList(0, pointsDataLimit);
                //批量新增
                drawResultMapper.insertList(listPage);
                //剔除
                drawResultList.subList(0,pointsDataLimit).clear();
            }
        } else {
            //批量新增
            drawResultMapper.insertList(drawResultList);
        }
    }

    //获取26个英文字母,超出长度循环字母后面加数字
    private String[] getLetter(double count){
        int letterLength = 26;//英文字母个数
        String[] classNames = new String[(int)count];
        double num =  count > letterLength ? Math.ceil(count) / letterLength : 1;
        Integer classNum = 0;
        int b = 0;
        for (int i = 0 ; i < num ; i ++) {
            b = forLetter(i,classNames,b,classNum);

            classNum ++;
        }
        return classNames;
    }

    //循环字母
    private int forLetter(int i, String[] classNames,int b,Integer classNum) {
        for (int a = 'A' ; a <= 'Z' ; a ++,b++) {
            if (i == 0) {
                if (b < classNames.length) {
                    classNames[b] = String.valueOf((char) a);
                }
            } else {
                if (b < classNames.length) {
                    classNames[b] =  String.valueOf((char)a) + String.valueOf(classNum);
                }
            }
            if (b > classNames.length) {
                break;
            }
        }
        return b;
    }

    /**
     * @Description:更改学员班级
     * @Date: 2018/5/2 10:56
     * @param: classId 班级id
     * @param: userId 学员id
     * @Author: wsc
     */
    @Override
    public CommonResponseDto changeClass(Integer classId,Integer userId,Integer courseId,Integer initClassId) {
        log.info("userId:{},classId:{}",userId,classId);
        if (null != classId && classId > 0) {
            //修改当前课程下该学员的班级id
            drawResultMapper.updateClassIdForCourseIdAndUserId(userId,courseId,classId);
        } else {
            //删除当前课程下该班级的学员
            drawResultMapper.deleteUserByClassId(userId,initClassId);
            //清空学员和演讲的关联
            drawResultMapper.updateDrawResultByCourseId(courseId);
        }
        //修改班级人数 原来的班级人数-1 现在的班级人数+1
        updateClassNumber(initClassId,classId);

        //修改课程的抽签状态
        Course course = new Course();
        course.setSysId(courseId);
        course.setIsBallot(Constants.Number.BYTE_NUMBER0);//抽签
        course.setIsScore(Constants.Number.BYTE_NUMBER0);//评分
        courseMapper.updateByPrimaryKeySelective(course);
        //删除评分模板
        weightMapper.deleteWeightByCourseId(courseId);
        //删除缓存中的评分
        deleteRedisScoreMap(courseId);
        //删除缓存课程详情
        deleteRedisCourseInfo(courseId,userId);
        return CommonResponseDto.ofSuccess();
    }

    /**
     * @Description:根据学员ID查询基本信息
     * @Date: 2018/5/2 9:41
     * @param: userId 学员id
     * @Author: wsc
     */
    @Override
    public CommonResponseDto findClassUserInfo(Integer userId,Integer courseId) {
        Map<String,Object> map = new HashMap <>();
        //查询基本信息
        DrawCuts drawCuts = drawResultMapper.selectClassUserInfo(courseId,userId);
        //查询所有班级名称
        List<Mclass> mclassList = mclassMapper.selectByCourseId(courseId);
        map.put("drawCuts",drawCuts);
        map.put("mclassList",mclassList);
        return CommonResponseDto.ofSuccess(map);
    }

    //修改班级人数
    private void updateClassNumber(Integer initClassId, Integer classId) {
        //原来的班级人数-1
        Mclass mclass = mclassMapper.selectByPrimaryKey(initClassId);
        mclass.setNumber(mclass.getNumber() <= 0 ? 0 : mclass.getNumber() - 1);
        mclassMapper.updateByPrimaryKeySelective(mclass);
        if (null != classId && classId > 0) {
            //更改的班级人数+1
            mclass = mclassMapper.selectByPrimaryKey(classId);
            mclass.setNumber(mclass.getNumber() + 1);
            mclassMapper.updateByPrimaryKeySelective(mclass);
        }
    }



    /**
     * @Description:根据课程id查询所有已报名学员
     * @Date: 2018/3/21 17:10
     * @param: courseId 课程id
     * @Author: wsc
     */
    private List<User> selectUserByCourseId(Integer courseId) {
        log.info("courseId:{}",courseId);
        //根据课程id查询课程详细信息 判断是否需要审核
        Course course = courseMapper.selectByPrimaryKey(courseId);
        List<User> userList;
        //需要审核：关联审核表校验是否审核通过 1：需要 0：不需要
        if (course.getIsVerify() == Constants.Number.SHORT_NUMBER1) {
            userList =  userMapper.selectUserByCourseIdAndApproval(courseId);
        } else {
            userList =  userMapper.selectUserByCourseId(courseId);
        }
        return userList;

    }

    /**
     * @Description:插入班级
     * @Date: 2018/3/21 14:06
     * @param: className 班级名称
     * @param: number 班级人数
     * @param: courseId 课程id
     * @Author: wsc
     */
    private void insertClass(String className,Integer number ,Integer courseId,Mclass mclass){
        mclass.setCourseId(courseId);
        mclass.setCreateTime(new Date());
        mclass.setName(className);
        mclass.setNumber(number);
        mclass.setState(Constants.Number.SHORT_NUMBER1);
        mclass.setInitNumber(number);
        mclassMapper.insertSelective(mclass);
    }

    @Override
    public CommonResponseDto selectSpeedAndDrawList(Integer courseId) {
        log.info("courseId:{}",courseId);
        List<MclassTeacSpee> mclassTeacSpeeList = mclassMapper.selectSpeedAndDrawList(courseId);
        Iterator<MclassTeacSpee> iter = mclassTeacSpeeList.iterator();
        //班级里面人数为0或者空的不显示
        while (iter.hasNext()){
            MclassTeacSpee m = iter.next();
            if(m.getNumber() == null || m.getNumber() == 0){
                iter.remove();
            }
        }
        return CommonResponseDto.ofSuccess(mclassTeacSpeeList);
    }

    @Override
    public CommonResponseDto drawImmediately(Integer courseId) {
        log.info("courseId:{}",courseId);
        //判断所有报名学员是否都分班
        List<Mclass> mclassList = mclassMapper.selectByCourseId(courseId);
        /*int number = 0;
        for(Mclass m : mclassList){
            number += null!=m.getNumber()?m.getNumber():0;
        }
        if(courseSignUpMapper.selectSignupCount(courseId)>number){
            throw new AmwaySystemException("还有已报名学员未分班,请先分班!");
        }*/

        for (Mclass mclass : mclassList){
            //判断讲师是否分配完成
            if(courseTeacherMapper.selectByCourseIdAndClassId(courseId,mclass.getId()).isEmpty() &&
                    (null!=mclass.getNumber()?mclass.getNumber():0) > 0){
                throw new AmwaySystemException("讲师还未分配完毕,请分配讲师!");
            }
            //判断演讲主题是否都创建
            if(classDrawMapper.selectClassDrawListByClassId(mclass.getId()).isEmpty() &&
                    (null!=mclass.getNumber()?mclass.getNumber():0) > 0){
                throw new AmwaySystemException("还有班级演讲主题未创建,请先创建演讲主题!");
            }
        }
        //进行抽签操作
        for(Mclass mclass : mclassList){
            //手动分班后给出提示
            if(null != mclass.getNumber() && mclass.getNumber()>classDrawMapper.selectClassDrawListByClassId(mclass.getId()).size()){
                throw new AmwaySystemException("还有班级演讲主题未创建,请先创建演讲主题!");
            }
            List<DrawResult> drawResultList = drawResultMapper.selectDrawResByClassId(mclass.getId());
            List<ClassDraw> classDrawList = classDrawMapper.selectClassDrawListByClassId(mclass.getId());
            int[] rand = RandomUtil.randomCommon(Constants.Number.INT_NUMBER1,drawResultList.size()+1,drawResultList.size());
            //int[] rand1 = RandomUtil.randomCommon(Constants.Number.INT_NUMBER1,drawResultList.size()+1,drawResultList.size());
            for(int i=0; i<drawResultList.size(); i++){
                DrawResult drawResult = drawResultList.get(i);
                drawResult.setClassDrawId(classDrawList.get(rand[i]-1).getId());

                /*ClassDraw classDraw = classDrawList.get(rand[i]-1);
                classDraw.setCode(String.valueOf(rand1[i]));

                classDrawMapper.updateByPrimaryKeySelective(classDraw);*/
                drawResultMapper.updateByPrimaryKeySelective(drawResult);
            }
        }
        return CommonResponseDto.ofSuccess();
    }

    @Override
    public List<Mclass> selectMclassByCourseId(Integer courseId) {
        log.info("courseId:{}",courseId);
        return mclassMapper.selectClassByCourseId(courseId);
    }

    @Override
    public List<ClassDraw> viewOrAddSub(Integer mClassId) {
        log.info("mClassId:{}",mClassId);
        //查询演讲内容记录
        List<ClassDraw> classDrawList = classDrawMapper.selectClassDrawListByClassId(mClassId);
        //用来查看班级人数
        Mclass mclass = mclassMapper.selectByPrimaryKey(mClassId);
        //创建时候返回需要填写的记录数
        List<ClassDraw> classDrawResult = new ArrayList<>();
        if(classDrawList.isEmpty()){
            for(int i=0;i<mclass.getNumber();i++){
                ClassDraw classDraw = new ClassDraw();
                classDrawResult.add(classDraw);
            }
            return classDrawResult;
        }else {
            if(mclass.getNumber() < classDrawList.size()){
                return classDrawList.subList(0,mclass.getNumber());
            }else if(mclass.getNumber() > classDrawList.size()){
                classDrawResult.addAll(classDrawList);
                for(int i=classDrawList.size();i<mclass.getNumber();i++){
                    ClassDraw c = new ClassDraw();
                    classDrawResult.add(c);
                }
                return classDrawResult;
            }else {
                return classDrawList;
            }
        }
    }

    @Override
    public void editOrAddSub(String data) {
        log.info("data:{}", data);
        JSONArray jsonArray = JSONArray.fromObject(data);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            ClassDraw classDraw = new ClassDraw();
            Integer mClassId = Integer.parseInt(jsonObject.get("mClassId").toString());
            classDraw.setClassId(mClassId);
            classDraw.setContent(jsonObject.get("content").toString());
            classDraw.setCode(jsonObject.get("code").toString());
            classDraw.setCourseId(Integer.parseInt(jsonObject.get("courseId").toString()));
            //新增
            if ("".equals(jsonObject.get("id"))) {
                classDraw.setCreateTime(new Date());
                classDrawMapper.insertSelective(classDraw);
            } else {
                //更新
                classDraw.setId(Integer.parseInt(jsonObject.get("id").toString()));
                classDraw.setUpdateTime(new Date());
                classDrawMapper.updateByClassIdSelective(classDraw);
            }
        }
    }

    @Override
    public void editOrAddTeac(String data) {
        log.info("data:{}",data);
        JSONArray jsonArray = JSONArray.fromObject(data);
        if(jsonArray == null || jsonArray.isEmpty()){
            return;
        }
        JSONObject j = jsonArray.getJSONObject(0);
        courseTeacherMapper.deleteByCourseIdAndClassId(Integer.parseInt(j.get("mClassId").toString()),Integer.parseInt(j.get("courseId").toString()));
        for(int i=0; i<jsonArray.size(); i++){
            JSONObject jsonObject=jsonArray.getJSONObject(i);
            CourseTeacher courseTeacher = new CourseTeacher();
            courseTeacher.setCourseId(Integer.parseInt(jsonObject.get("courseId").toString()));
            courseTeacher.setUserId(Integer.parseInt(jsonObject.get("userId").toString()));
            courseTeacher.setClassId(Integer.parseInt(jsonObject.get("mClassId").toString()));
            courseTeacherMapper.insertSelective(courseTeacher);
        }
    }

    @Override
    public CommonResponseDto findTeacList(String type, Integer mClassId, Integer courseId, String name) {
        log.info("type:{},mClassId{},courseId{},name{}",type,mClassId,courseId,name);
        List<User> userList = userMapper.findTeacList(name);
        //判断置灰的讲师id
        List<CourseTeacher> teacherIdListGrey = courseTeacherMapper.selectTeacByCourseId(courseId);
        //判断选中的讲师id
        List<String> teacherIdListSelected = userMapper.selectTeacIdByCourseIdAndClassId(mClassId,courseId);

        List<User> listGrey = new ArrayList<>();
        //判断置灰
        for(User u : userList){
            u.setAssign(false);
            for(CourseTeacher ct : teacherIdListGrey){
                if(ct.getUserId().equals(u.getId()) && null != ct.getClassId()){
                    u.setAssign(true);
                }
            }
            listGrey.add(u);
        }
        //判断选中并返回结果
        List<User> resultList = new ArrayList<>();
        for(User user : listGrey){
            user.setSelected(false);
            for(String s : teacherIdListSelected){
                if(s.equals(user.getId().toString())){
                    user.setSelected(true);
                }
            }
            resultList.add(user);
        }
        return CommonResponseDto.ofSuccess(resultList);
    }

    @Override
    public List<DrawResultData> selectDrawResult(Integer pageNo,Integer pageSize,Integer courseId) {
        log.info("pageNo:{},pageSize{},courseId{}",pageNo,pageSize,courseId);
        PageHelper.startPage(pageNo, pageSize);
        List<DrawResultData> drawResultDataList = drawResultMapper.selectDrawResult(courseId);
        List<Mclass> mclassList = mclassMapper.selectByCourseId(courseId);
        List<DrawResultData> resultData = new ArrayList<>();
        Map<Integer,String> map = new HashMap<>();
        for(Mclass m : mclassList){
            List<String> teacherList = courseTeacherMapper.selectTeacherByCourseIdAndClassId(m.getId(),courseId);
            StringBuilder teacherName = new StringBuilder();
            //将讲师的数据拼成一行
            if(teacherList.size()>1){
                for(String s : teacherList){
                    teacherName.append(s).append(",");
                }
            }else{
                teacherName.append(teacherList.isEmpty()?"":teacherList.get(0));
            }
            map.put(m.getId(),teacherName.toString());
            //按照班级并分页显示数据
            assembleData(m,drawResultDataList,resultData);
        }
        assembleTeacher(resultData, map);
        return resultData;
    }

    @Override
    public List<DrawResultData> selectDrawResultByCoursePreview(Integer pageNo,Integer pageSize,Integer courseId) {
        log.info("pageNo:{},pageSize{},courseId{}",pageNo,pageSize,courseId);
        PageHelper.startPage(pageNo, pageSize);
        List<DrawResultData> drawResultDataList = drawResultMapper.selectDrawResultByCoursePreview(courseId);
        List<Mclass> mclassList = mclassMapper.selectByCourseId(courseId);
        List<DrawResultData> resultData = new ArrayList<>();
        Map<Integer,String> map = new HashMap<>();
        for(Mclass m : mclassList){
            List<String> teacherList = courseTeacherMapper.selectTeacherByCourseIdAndClassId(m.getId(),courseId);
            StringBuilder teacherName = new StringBuilder();
            //将讲师的数据拼成一行
            if(teacherList.size()>1){
                for(String s : teacherList){
                    teacherName.append(s).append(",");
                }
            }else{
                teacherName.append(teacherList.isEmpty()?"":teacherList.get(0));
            }
            map.put(m.getId(),teacherName.toString());
            //按照班级并分页显示数据
            assembleData(m,drawResultDataList,resultData);
        }
        assembleTeacher(resultData, map);
        return resultData;
    }

    @Override
    public List<DrawResultData> selectDrawResultCount(Integer courseId) {
        log.info("courseId:{}",courseId);
        return drawResultMapper.selectDrawResultCount(courseId);
    }

    @Override
    public Integer selectDrawResultCountByCoursePreview(Integer courseId) {
        log.info("courseId:{}",courseId);
        return drawResultMapper.selectDrawResultCountByCoursePreview(courseId);
    }

    @Override
    public ClassDraw selectSubDetail(Integer id) {
        log.info("id:{}",id);
        return classDrawMapper.selectByPrimaryKey(id);
    }

    @Override
    public void editIsBallot(Integer courseId, Integer isBallot) {
        log.info("courseId:{},isBallot:{}",courseId,isBallot);
        if(!scoreResultMapper.selectScoreResultByCourseId(courseId).isEmpty()){
            throw new AmwaySystemException("演讲已经开始,当前无法操作!");
        }
        Course course = courseMapper.selectByPrimaryKey(courseId);
        course.setIsBallot(isBallot.byteValue());
        String key = Constants.COURSE_USER_INFO + courseId;
        String[] keys = redisComponent.keys( key );
        redisComponent.del( keys );
        courseMapper.updateByPrimaryKeySelective(course);
    }

    @Override
    public boolean isSpeekAndDraw(Integer courseId) {
        log.info("courseId:{}",courseId);
        boolean flag = false;

        //判断是否抽签
        List<DrawResult> drawResultList = drawResultMapper.selectDrawResultByCourseId(courseId);
        for(DrawResult d : drawResultList){
            if(null != d.getClassDrawId()){
                flag = false;
            }else {
                flag = true;
                break;
            }
        }
        if(drawResultList.isEmpty() || flag){
            return true;
        }else if(null != drawResultList.get(0).getClassDrawId()){
            return false;
        }else {
            return true;
        }
    }

    public void assembleData(Mclass m,List<DrawResultData> drawResultDataList,List<DrawResultData> resultData){
        //新建一个list存放班级为单位的数据
        List<DrawResultData> drawResultDataListTmp = new ArrayList<>();
        for(DrawResultData d : drawResultDataList){
            if(m.getId().equals(d.getClassId())){
                drawResultDataListTmp.add(d);
            }
        }
        //为每条数据加索引
        for(DrawResultData d : drawResultDataListTmp){
            if(m.getId().equals(d.getClassId())){
                d.setIndex(drawResultDataListTmp.indexOf(d));
                d.setNumber(drawResultDataListTmp.size());
            }
        }
        resultData.addAll(drawResultDataListTmp);
    }

    public void assembleTeacher(List<DrawResultData> resultData,Map<Integer,String> map){
        for(DrawResultData d : resultData){
            for (Map.Entry<Integer,String> entry : map.entrySet()){
                if(entry.getKey().equals(d.getClassId())){
                    d.setTeacher(entry.getValue());
                }
            }
        }
    }

    @Override
    public void reEdit(Integer courseId){
        if(!scoreResultMapper.selectScoreResultByCourseId(courseId).isEmpty()){
            throw new AmwaySystemException("演讲已经开始,当前无法操作!");
        }
    }

    //删除缓存中课程详情
    private void deleteRedisCourseInfo(Integer courseId,Integer userId){
        if (redisComponent.hasKey(Constants.COURSE_USER_INFO + courseId + ":" + userId)) {
            redisComponent.del(Constants.COURSE_USER_INFO + courseId + ":" + userId);
        }
    }
}
