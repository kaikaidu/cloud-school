package com.amway.acti.service.impl;

import com.amway.acti.base.util.Constants;
import com.amway.acti.base.util.DateUtil;
import com.amway.acti.base.util.DownloadUtil;
import com.amway.acti.base.util.ExcelExportUtils;
import com.amway.acti.dao.*;
import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.model.*;
import com.amway.acti.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @Description:报表自定义
 * @Date:2018/3/27 19:15
 * @Author:wsc
 */
@Service
@Slf4j
public class ReportFormsServiceImpl implements ReportFormsService {

    //课程
    @Autowired
    private CourseMapper courseMapper;

    //自定义课程Mapper
    @Autowired
    private CourseExportMapper courseExportMapper;

    //签到管理
    @Autowired
    private RegisterExportMapper registerExportMapper;

    //在线测试
    @Autowired
    private BackendTestOnlineService backendTestOnlineService;

    //问卷调查
    @Autowired
    private BackendInvesOnlineService backendInvesOnlineService;

    //评分管理
    @Autowired
    private WeightExportMapper weightExportMapper;

    @Autowired
    private ReportFormsService reportFormsService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private WeightService weightService;

    @Autowired
    private StuStatisticsExportMapper stuStatisticsExportMapper;

    @Autowired
    private SignStuExportMapper signStuExportMapper;

    @Autowired
    private CertExportMapper certExportMapper;

    private SimpleDateFormat sdf1 = new SimpleDateFormat(DateUtil.YYYY_MM_DD_HH_MM);

    /**
     * @Description:获取课程总数
     * @Date: 2018/3/27 19:16
     * @param: name
     * @Author: wsc
     */
    @Override
    public CommonResponseDto getCourseCount(String name, String label) {
        log.info("name:{},label:{}",name,label);
        int count = courseMapper.selectCourseCount(name,label);
        log.info("count:{}",count);
        return CommonResponseDto.ofSuccess(count);
    }

    /**
     * @Description:根据课程名称查询课程列表
     * @Date: 2018/3/27 19:54
     * @param: pageNo 当前页
     * @param: pageSize 每页数量
     * @param: name 课程名称
     * @param: sort 排序
     * @param: label 标签
     * @Author: wsc
     */
    @Override
    public CommonResponseDto<PageInfo<CourseExport>> courseList(Integer pageNo, Integer pageSize, String name, String sort, String label) {
        log.info("pageNo:{},pageSize:{},name:{},sort:{},label:{}",pageNo,pageSize,name,sort,label);
        PageHelper.startPage(pageNo,pageSize);
        List<CourseExport> courseList = courseMapper.selectCourseByName(name,sort,label);
        log.info("courseList:{}",courseList);
        return CommonResponseDto.ofSuccess(new PageInfo <>(courseList));
    }

    /**
     * @Description:导出课程相关联的所有信息
     * @Date: 2018/3/27 20:57
     * @param: ids 课程id
     * @param: name 课程名称
     * @param: label 课程标签
     * @param: response
     * @Author: wsc
     */
    @Override
    public void exportReportForms(HttpServletResponse response, HttpServletRequest request,
                                  String ids ,String name, String label) throws Exception{
        // 声明一个工作薄
        HSSFWorkbook workbook = new HSSFWorkbook();
        OutputStream out = response.getOutputStream();// 取得输出流
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.YYYY_MM_DD);
        long bt = System.currentTimeMillis();
        Object obj = redisService.getValue(Constants.REPORTFORMS_CACHE_KEY);
        ExportReportForms exportReportForms;
        //判断从缓存中是否拿到数据，没拿到重新查询一边
        if (null != obj) {
            //json转换javabean时使用ObjectMapper可以使父子bean都到转换
            ObjectMapper objectMapper = new ObjectMapper();
            exportReportForms = objectMapper.readValue(obj.toString(),ExportReportForms.class);
        } else {
            exportReportForms = this.getExportList(ids,name,label);
        }
        long et2 = System.currentTimeMillis();
        log.info("exportReportForms:"+(et2 - bt)+ "ms");

        //导出课程相关的全部数据
        exportAll(workbook,exportReportForms);

        ExcelExportUtils.setSheelIndex(0);
        Date dste = new Date();
        String tis = sdf.format(dste);
        tis = Constants.REPORT_FORMS + tis + ".xls";
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-disposition", "attachment;filename=" + DownloadUtil.encodeDownloadFilename(tis,request));
        workbook.write(out);
        out.flush();
        out.close();
    }


    //导出 签到,评分
    private void exportAll(HSSFWorkbook workbook,ExportReportForms exportReportForms) throws ReflectiveOperationException {
        //课程
        String[] courseHeaders = {"课程ID","课程名称","创建时间","开始时间","结束时间","标签","课程状态","地址","人数限制","报名人数","报名成功","签到人数","点赞次数"};
        ExcelExportUtils.exportExcel(workbook,"课程", Constants.REPORT_FORMS,courseHeaders,exportReportForms.getCourseList());
        //学员统计
        String[] stuHeaders = {"截止日期","学员姓名","安利卡号","性别","报名课程数","审核通过数","签到课程数","测试完成数","问卷完成数","颁发证书数"};
        ExcelExportUtils.exportExcel(workbook,"学员统计",Constants.REPORT_FORMS,stuHeaders,exportReportForms.getStuStatisticsExports());
        //报名学员
        String[] singStuHeaders = {"日期","课程ID","姓名","安利卡号","性别","报名状态","通过状态"};
        ExcelExportUtils.exportExcel(workbook,"报名学员", Constants.REPORT_FORMS,singStuHeaders,exportReportForms.getSignStuExports());
        //签到
        String[] headers = {"日期","课程ID","学员姓名","安利卡号","性别","上课状态"};
        ExcelExportUtils.exportExcel(workbook,"签到管理", Constants.REPORT_FORMS,headers,exportReportForms.getRegisterExports());
        //测试
        String[] headers1 = {"日期","课程ID","学员姓名","安利卡号","测试名称","性别","状态","得分"};
        ExcelExportUtils.exportExcel(workbook,"测试结果", Constants.REPORT_FORMS,headers1,exportReportForms.getTestExports());
        //问卷
        String[] headers2 = {"日期","课程ID","学员姓名","安利卡号","性别","问卷名称","状态"};
        ExcelExportUtils.exportExcel(workbook,"问卷结果", Constants.REPORT_FORMS,headers2,exportReportForms.getInvesExports());
        //测试
        String[] headers3 = {"日期","课程ID","学员姓名","安利卡号","性别","状态","得分"};
        //评分
        ExcelExportUtils.exportExcel(workbook,"评分结果", Constants.REPORT_FORMS,headers3,exportReportForms.getWeightExports());
        //证书
        String[] certHeaders = {"日期","课程ID","学员姓名","安利卡号","性别","状态"};
        ExcelExportUtils.exportExcel(workbook,"证书", Constants.REPORT_FORMS,certHeaders,exportReportForms.getCertExports());
    }

    //查询在线测试，把所有签到放入一个集合中
    @Override
    public void exportTestByCourseId(List<TestExport> testExports, Integer id) {
        long bt = System.currentTimeMillis();
        List<Map<String,Object>> mapList = backendTestOnlineService.getScoreResult(id);
        long et2 = System.currentTimeMillis();
        log.info("mapList:"+(et2 - bt)+ "ms,mapList",mapList);
        if (null != mapList && !mapList.isEmpty()) {
            List<TestExport> testExportList = this.mapforTestList(mapList,id);
            testExports.addAll(testExportList);
        }
    }


    //查询问卷，把所有签到放入一个集合中
    @Override
    public void exportInvesByCourseId(List<InvesExport> invesExports, Integer id) {
        long bt = System.currentTimeMillis();
        List<Map<String,Object>> mapList = backendInvesOnlineService.getResult(id);
        long et2 = System.currentTimeMillis();
        log.info("invesExportList:"+(et2 - bt)+ "ms,invesExportList",mapList);
        if (null != mapList && !mapList.isEmpty()) {
            List<InvesExport> invesExportList = this.mapforInvesList(mapList,id);
            invesExports.addAll(invesExportList);
        }
    }


    //查询评分，把所有签到放入一个集合中
    @Override
    public void exportWeightByCourseId(List<WeightExport> weightExports, Integer id) {
        long bt = System.currentTimeMillis();
        WeightResult weightResult = new WeightResult();
        weightResult.setCourseId(id);
        List<WeightExport> weightExportList = weightService.exportDataListSelf(weightResult);
        long et2 = System.currentTimeMillis();
        log.info("weightExportList:"+(et2 - bt)+ "ms,weightExportList.size:{}",weightExportList.size());
        weightExports.addAll(weightExportList);
    }

    /**
     * @Description:组装导出数据 存入redis
     * @Date: 2018/5/8 18:07
     * @param: ids
     * @param: name
     * @param: label
     * @Author: wsc
     */
    @Override
    public ExportReportForms getExportList(String ids, String name, String label) throws Exception{
        log.info("ids:{},name:{},label:{}",ids,name,label);
        long bt = System.currentTimeMillis();
        //导出课程
        List<CourseExport> courseList = this.exportCourseById(ids,name,label);

        //签到
        List<RegisterExport> registerExports = registerExportMapper.selectRegisterByCourseId(ids.length() < Constants.Number.INT_NUMBER1 ? null : courseList);
        //学员统计
        List<StuStatisticsExport> stuStatistics = stuStatisticsExportMapper.selectStuStatistics(ids.length() < Constants.Number.INT_NUMBER1 ? null : courseList,sdf1.format(new Date()));
        //报名学员
        List<SignStuExport> signStuExports = signStuExportMapper.selectSignStuExports(ids.length() < Constants.Number.INT_NUMBER1 ? null : courseList);
        //证书
        List<CertExport> certExports = certExportMapper.selectCertExport(ids.length() < Constants.Number.INT_NUMBER1 ? null : courseList);
        long btend = System.currentTimeMillis();
        log.info("courseList,registerExports,stuStatistics,signStuExports,certExports Time==>:{}",btend-bt);
        bt = System.currentTimeMillis();
        //在线测试
        List<TestExport> testExports = new ArrayList <>();
        //问卷结果
        List<InvesExport> invesExports = new ArrayList <>();
        //评分
        List<WeightExport> weightExports = new ArrayList <>();

        if (null != courseList && !courseList.isEmpty()) {
            //创建可缓存线程池
            ExecutorService pool = Executors.newFixedThreadPool(20);
            //记数
            CountDownLatch cdl = new CountDownLatch(courseList.size());
            log.info("--->Runnable-->start");
            //根据课程id导出相关信息
            for (CourseExport c : courseList) {
                try {
                    pool.execute(this.getThread(testExports,invesExports,weightExports,c.getId(),cdl));
                } catch (Exception e) {
                    log.error(e.getMessage());
                    continue;
                }
            }
            log.info("--->CountDownLatch-->await");

            //主线程阻塞，等待子线程全部完成
            cdl.await();
            //关闭线程池
            pool.shutdown();
            long et2 = System.currentTimeMillis();
            log.info("getExportList:"+(et2 - bt)+ "ms");
            log.info("--->Runnable-->end");
            //封装数据 排序
            log.info("courseList:{},registerExports:{},testExports:{},invesExports:{},weightExports:{}",courseList.size(),registerExports.size(),testExports.size(),invesExports.size(),weightExports.size());
        }
        ExportReportForms exportReportForms = getExportReportForms(courseList,stuStatistics,signStuExports,registerExports,testExports,invesExports,weightExports,certExports);

        redisService.set(Constants.REPORTFORMS_CACHE_KEY,JSONObject.fromObject(exportReportForms).toString(),60*10);
        return exportReportForms;
    }


    private Runnable getThread(List<TestExport> testExports,List<InvesExport> invesExports,
                               List<WeightExport> weightExports,Integer id,CountDownLatch cdl) {
        return () -> {
            //查询在线测试
            reportFormsService.exportTestByCourseId(testExports,id);
            //查询问卷
            reportFormsService.exportInvesByCourseId(invesExports,id);
            //查询评分
            reportFormsService.exportWeightByCourseId(weightExports,id);
            cdl.countDown();
        };
    }

    //封装导出报表数据，排序
    private ExportReportForms getExportReportForms(List<CourseExport> courseList,
                                                   List<StuStatisticsExport> stuStatistics,
                                                   List<SignStuExport> signStuExports,
                                                   List<RegisterExport> registerExports,
                                                   List<TestExport> testExports,
                                                   List<InvesExport> invesExports,
                                                   List<WeightExport> weightExports,
                                                   List<CertExport> certExports) {
        ExportReportForms exportReportForms = new ExportReportForms();
        long bt = System.currentTimeMillis();
        if (null != testExports && !testExports.isEmpty()) {
            testExports.removeAll(Collections.singleton(null));
            testExports = testExports.stream().sorted(Comparator.comparing(TestExport::getCourseId)).collect(Collectors.toList());
        }
        if (null != invesExports && !invesExports.isEmpty()) {
            invesExports.removeAll(Collections.singleton(null));
            invesExports = invesExports.stream().sorted(Comparator.comparing(InvesExport::getCourseId)).collect(Collectors.toList());
        }
        if (null != weightExports && !weightExports.isEmpty()) {
            weightExports.removeAll(Collections.singleton(null));
            weightExports = weightExports.stream().sorted(Comparator.comparing(WeightExport::getCourseId)).collect(Collectors.toList());
        }
        long et2 = System.currentTimeMillis();
        log.info("sortTime:"+(et2 - bt)+ "ms");
        exportReportForms.setCourseList(courseList);
        exportReportForms.setStuStatisticsExports(stuStatistics);
        exportReportForms.setSignStuExports(signStuExports);
        exportReportForms.setInvesExports(invesExports);
        exportReportForms.setRegisterExports(registerExports);
        exportReportForms.setTestExports(testExports);
        exportReportForms.setWeightExports(weightExports);
        exportReportForms.setCertExports(certExports);
        return exportReportForms;
    }

    //导出课程
    private List<CourseExport> exportCourseById(String ids, String name, String label) {
        log.info("courseIds:{},name:{},label:{}",ids,name,label);
        String[] idArray;
        List<Integer> list = null;
        if (ids.length() > 1) {
            list = new ArrayList <>();
            idArray = ids.split(",");
            for (String s : idArray) {
                list.add(Integer.parseInt(s));
            }
        }
        List<CourseExport> courseList = courseExportMapper.exportCourseById(list,name,label);
        return courseList.size() > 0 ? courseList : null;
    }

    //测试map转换list
    private List<TestExport> mapforTestList(List<Map<String, Object>> mapList, Integer courseId) {
        List<TestExport> testExports = new ArrayList <>();
        TestExport testExport;
        Iterator<Map<String, Object>> it = mapList.iterator();
        String answer;
        String createTime;
        List<Map<String,Object>> list;
        while (it.hasNext()) {
            Map<String, Object> next = it.next();
            list = (List <Map <String, Object>>) next.get("scoreInfo");
            Iterator<Map<String, Object>> it1 = list.iterator();
            while (it1.hasNext()) {
                Map<String, Object> next1 = it1.next();
                testExport = new TestExport();
                testExport.setCourseId(courseId);
                createTime = String.valueOf(next1.get("createTime"));
                testExport.setCreateTime(createTime.equals("null") ? "" : createTime);
                testExport.setAdaNumber(String.valueOf(next1.get("user_ada")));
                answer = String.valueOf(next1.get("score"));
                testExport.setAnswer(answer.equals("null") ? "" : answer);
                testExport.setSex(String.valueOf(next1.get("user_sex_show")));
                testExport.setTestName(String.valueOf(next1.get("paper_name")));
                testExport.setState(String.valueOf(next1.get("score_state_show")));
                testExport.setUserName(String.valueOf(next1.get("user_name")));
                testExports.add(testExport);
            }
        }
        return testExports;
    }

    //问卷map转换list
    private List<InvesExport> mapforInvesList(List<Map<String, Object>> mapList, Integer courseId) {
        List<InvesExport> invesExports = new ArrayList <>();
        InvesExport invesExport;
        Iterator<Map<String, Object>> it = mapList.iterator();
        String createTime;
        List<Map<String,Object>> list;
        while (it.hasNext()) {
            Map<String, Object> next = it.next();
            list = (List <Map <String, Object>>) next.get("resultInfo");
            Iterator<Map<String, Object>> it1 = list.iterator();
            while (it1.hasNext()) {
                Map<String, Object> next1 = it1.next();
                invesExport = new InvesExport();
                invesExport.setCourseId(courseId);
                createTime = String.valueOf(next1.get("createTime"));
                invesExport.setCreateTime(createTime.equals("null") ? "" : createTime);
                invesExport.setAdaNumber(String.valueOf(next1.get("user_ada")));
                invesExport.setSex(String.valueOf(next1.get("user_sex_show")));
                invesExport.setState(String.valueOf(next1.get("result_state_show")));
                invesExport.setUserName(String.valueOf(next1.get("user_name")));
                invesExport.setInvesName(String.valueOf(next1.get("paper_name")));
                invesExports.add(invesExport);
            }
        }
        return invesExports;
    }
}


