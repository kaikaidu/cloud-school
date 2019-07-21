package com.amway.acti.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:导出报表对象
 * @Date:2018/5/8 11:25
 * @Author:wsc
 */
@Data
public class ExportReportForms{
    //课程
    private List<CourseExport> courseList = new ArrayList <>();
    //学员统计
    private List<StuStatisticsExport> stuStatisticsExports = new ArrayList <>();
    //报名学员
    private List<SignStuExport> signStuExports = new ArrayList <>();
    //签到
    private List<RegisterExport> registerExports = new ArrayList <>();
    //在线测试
    private List<TestExport> testExports = new ArrayList <>();
    //问卷结果
    private List<InvesExport> invesExports = new ArrayList <>();
    //评分
    private List<WeightExport> weightExports = new ArrayList <>();
    //证书
    private List<CertExport> certExports = new ArrayList <>();
}
