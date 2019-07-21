package com.amway.acti.controller.backendcontroller;

import com.amway.acti.base.util.Constants;
import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.dto.MenuDto;
import com.amway.acti.model.CourseExport;
import com.amway.acti.model.ExportReportForms;
import com.amway.acti.model.User;
import com.amway.acti.service.BackendUserService;
import com.amway.acti.service.ReportFormsService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @Description:报表自定义
 * @Date:2018/3/27 19:06
 * @Author:wsc
 */
@Controller
@RequestMapping(value = "/backend/platform")
public class ReportFormsController {

    @Autowired
    private ReportFormsService reportFormsService;

    @Autowired
    private BackendUserService backendUserService;

    /**
     * @Description:跳转报表自定义
     * @Date: 2018/3/27 19:13
     * @Author: wsc
     */
    @GetMapping(value = "/reportForms")
    public String reportForms(Model model, HttpSession session,Integer courseId){
        User user = (User)session.getAttribute(Constants.BACKEND_USER_SESSSION_KEY);
        MenuDto menuDto = backendUserService.findMenu(user);
        MenuDto menuChildDto = backendUserService.findChildMenuByParentCodeAndUser(user,"04");
        model.addAttribute("menuFirstList", menuDto.getMenuList());
        model.addAttribute("menuChildList", menuChildDto.getMenuList());
        model.addAttribute("courseId", courseId);
        model.addAttribute("curFirstMenu","04");
        model.addAttribute("curChildMenu","0401");
        return "platform/reportForms";
    }

    /**
     * @Description:获取课程总数
     * @Date: 2018/3/27 19:16
     * @param: name 课程名称
     * @Author: wsc
     */
    @PostMapping(value = "/getCourseCount")
    @ResponseBody
    public CommonResponseDto getCourseCount(String name, String label){
        return reportFormsService.getCourseCount(name,label);
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
    @PostMapping(value = "/courseList")
    @ResponseBody
    public CommonResponseDto<PageInfo<CourseExport>> courseList(Integer pageNo, Integer pageSize, String name, String sort, String label){
        return reportFormsService.courseList(pageNo,pageSize,name,sort,label);
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
    @GetMapping(value = "/exportReportForms")
    public void exportReportForms(HttpServletResponse response, HttpServletRequest request,String ids ,String name, String label) throws Exception{
        reportFormsService.exportReportForms(response,request,ids,name,label);
    }

    /**
     * @Description:获取导出报表需要的数据
     * @Date: 2018/5/8 11:13
     * @param: ids 课程id
     * @param: name 课程名称
     * @param: label 课程标签
     * @Author: wsc
     */
    @PostMapping("/getExportList")
    @ResponseBody
    public ExportReportForms getExportList(String ids, String name, String label) throws Exception {
        return reportFormsService.getExportList(ids,name,label);
    }
}
