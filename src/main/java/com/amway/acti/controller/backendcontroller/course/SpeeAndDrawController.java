package com.amway.acti.controller.backendcontroller.course;

import com.amway.acti.controller.backendcontroller.BaseController;
import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.dto.MenuDto;
import com.amway.acti.model.User;
import com.amway.acti.service.BackendUserService;
import com.amway.acti.service.CourseService;
import com.amway.acti.service.DrawService;
import com.amway.acti.transform.DrawResultDataTransform;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;



/**
 * @Description:演讲抽签
 * @Date:2018/4/27 10:27
 * @Author:qj
 */
@Controller
@RequestMapping(value = "/backend/course/speeAndDraw")
public class SpeeAndDrawController extends BaseController {

    @Autowired
    private BackendUserService backendUserService;

    @Autowired
    private DrawService drawService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private DrawResultDataTransform drawResultDataTransform;

    private static final String DRAWCOURSEID = "courseId";

    private static final String ISASSIGNCLASS = "isAssignClass";

    /**
     * 演讲抽签的默认页面
     * @return
     */
    @GetMapping(value = "/speedAndDrawIndex")
    public ModelAndView speedAndDrawIndex(HttpServletRequest request, Model model){
        ModelAndView mv = new ModelAndView();
        String courseId = request.getParameter( DRAWCOURSEID );
        if(StringUtils.isBlank(courseId)){
            mv.setViewName("redirect:/backend/course/basic");
            return mv;
        }
        User user = getSessionAdmin();
        getMenu(user,model);
        model.addAttribute(DRAWCOURSEID,courseId);
        //判断是否分班
        if(drawService.selectMclassByCourseId(Integer.valueOf(courseId)).isEmpty()){
            //未分班
            mv.addObject(ISASSIGNCLASS,"no");
        }else {
            //已分班
            mv.addObject(ISASSIGNCLASS,"yes");
        }
        if(drawService.isSpeekAndDraw(Integer.valueOf(courseId))){
            mv.setViewName("course/drawClassDetail");
        }else {
            model.addAttribute("isActivation",courseService.selectCourseById(Integer.valueOf(courseId)).getIsBallot() == 1?true:false);
            mv.setViewName("course/drawResult");
        }
        return mv;
    }

    /**
     * 返回到重新编辑页面
     * @param request
     * @param model
     * @return
     */
    @GetMapping(value = "/reEdit")
    public ModelAndView reEdit(HttpServletRequest request, Model model){
        ModelAndView mv = new ModelAndView();
        Integer courseId = Integer.valueOf( request.getParameter( DRAWCOURSEID ) );
        User user = getSessionAdmin();
        getMenu(user,model);
        model.addAttribute(DRAWCOURSEID,courseId);
        //判断是否分班
        if(drawService.selectMclassByCourseId(courseId).isEmpty()){
            //未分班
            mv.addObject(ISASSIGNCLASS,"no");
        }else {
            //已分班
            mv.addObject(ISASSIGNCLASS,"yes");
        }
        mv.setViewName("/course/drawClassDetail");
        return mv;
    }

    /**
     * 获取演讲抽签列表数据
     * @param courseId
     * @return
     * @Author: qj
     */
    @GetMapping(value = "/selectSpeedAndDrawList")
    @ResponseBody
    public CommonResponseDto selectSpeedAndDrawList(Integer courseId){
        return drawService.selectSpeedAndDrawList(courseId);
    }

    /**
     *
     * @param type
     * @param mClassId
     * @param courseId
     * @param name
     * @return
     */
    @GetMapping(value = "/findTeacList")
    @ResponseBody
    public CommonResponseDto findTeacList(String type,Integer mClassId, Integer courseId, String name){
        return drawService.findTeacList(type,mClassId,courseId,name);
    }

    /**
     * 保存讲师
     * @param data
     * @return
     */
    @PostMapping(value = "/editOrAddTeac")
    @ResponseBody
    public CommonResponseDto editOrAddTeac(String data){
        drawService.editOrAddTeac(data);
        return CommonResponseDto.ofSuccess();
    }

    /**
     * 查看或创建主题
     * @param mClassId
     * @return
     */
    @GetMapping(value = "/viewOrAddSub")
    @ResponseBody
    public CommonResponseDto viewOrAddSub(Integer mClassId){
        return CommonResponseDto.ofSuccess(drawService.viewOrAddSub(mClassId));
    }

    /**
     * 保存主题
     * @param data
     * @return
     */
    @PostMapping(value = "/editOrAddSub")
    @ResponseBody
    public CommonResponseDto editOrAddSub(String data){
        drawService.editOrAddSub(data);
        return CommonResponseDto.ofSuccess();
    }

    /**
     * 立即抽签
     * @param courseId
     * @return
     * @Author: qj
     */
    @PostMapping(value = "/drawImmediately")
    @ResponseBody
    public CommonResponseDto drawImmediately(Integer courseId){
        return drawService.drawImmediately(courseId);
    }

    /**
     * 跳转抽签结果页面
     * @return
     */
    @RequestMapping(value = "/gotoDrawResult")
    public ModelAndView gotoDrawResult(HttpServletRequest request,Model model){
        ModelAndView mv = new ModelAndView();
        Integer courseId = Integer.valueOf( request.getParameter( DRAWCOURSEID ) );
        User user = getSessionAdmin();
        getMenu(user,model);
        model.addAttribute("isActivation",courseService.selectCourseById(courseId).getIsBallot() == 1?true:false);
        model.addAttribute(DRAWCOURSEID,courseId);
        mv.setViewName("course/drawResult");
        return mv;
    }

    /**
     * 抽签结果数据
     * @return
     */
    @GetMapping(value = "/selectDrawResult")
    @ResponseBody
    public CommonResponseDto selectDrawResult(Integer pageNo,Integer pageSize,Integer courseId){
        return CommonResponseDto.ofSuccess(drawService.selectDrawResult(pageNo,pageSize,courseId));
    }

    /**
     * 抽签结果数据-预览
     * @return
     */
    @GetMapping(value = "/selectDrawResulPre")
    @ResponseBody
    public CommonResponseDto selectDrawResultPre(Integer pageNo,Integer pageSize,Integer courseId){
        return CommonResponseDto.ofSuccess(drawResultDataTransform.transformDrawResultDataToDto(drawService.selectDrawResultByCoursePreview(pageNo,pageSize,courseId)));
    }

    /**
     * 抽签结果count-预览
     * @return
     */
    @GetMapping(value = "/selectDrawResulPreCount")
    @ResponseBody
    public CommonResponseDto selectDrawResultPreCount(Integer courseId){
        return CommonResponseDto.ofSuccess(drawService.selectDrawResultCountByCoursePreview(courseId));
    }

    /**
     * 抽签结果数量
     * @return
     */
    @GetMapping(value = "/selectDrawResultCount")
    @ResponseBody
    public CommonResponseDto selectDrawResultCount(Integer courseId){
        return CommonResponseDto.ofSuccess(drawService.selectDrawResultCount(courseId).size());
    }

    /**
     * 查看演讲主题
     * @param classDrawId
     * @return
     */
    @GetMapping(value = "/selectSub")
    @ResponseBody
    public CommonResponseDto selectSub(Integer classDrawId){
        return CommonResponseDto.ofSuccess(drawService.selectSubDetail(classDrawId));
    }

    /**
     * 激活/取消抽签
     * @param courseId
     * @return
     */
    @PostMapping(value = "/editIsBallot")
    @ResponseBody
    public CommonResponseDto editIsBallot(Integer courseId,Integer isBallot){
        drawService.editIsBallot(courseId,isBallot);
        return CommonResponseDto.ofSuccess();
    }

    /**
     * 重新编辑页面跳转判断是否可以操作
     * @param courseId
     * @return
     */
    @GetMapping(value = "/isReEdit")
    @ResponseBody
    public CommonResponseDto isReEdit(Integer courseId){
        drawService.reEdit(courseId);
        return CommonResponseDto.ofSuccess();
    }

    /**
     * 查询菜单的公共方法
     * @param user
     * @param model
     */
    public void getMenu(User user,Model model){
        MenuDto menuDto = backendUserService.findMenu(user);
        MenuDto menuChildDto = backendUserService.findChildMenuByParentCodeAndUser(user,"01");
        model.addAttribute("menuFirstList", menuDto.getMenuList());
        model.addAttribute("menuChildList", menuChildDto.getMenuList());
        model.addAttribute("curFirstMenu","01");
        model.addAttribute("curChildMenu","0107");
    }
}
