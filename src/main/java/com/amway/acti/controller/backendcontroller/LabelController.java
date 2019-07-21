package com.amway.acti.controller.backendcontroller;

import com.amway.acti.base.util.Constants;
import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.dto.MenuDto;
import com.amway.acti.model.Label;
import com.amway.acti.model.User;
import com.amway.acti.service.BackendUserService;
import com.amway.acti.service.LabelService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * @Description:标签管理
 * @Date:2018/3/27 9:30
 * @Author:wsc
 */
@Controller
    @RequestMapping("/backend/resource")
public class LabelController {

    @Autowired
    private LabelService labelService;

    @Autowired
    private BackendUserService backendUserService;

    /**
     * @Description:跳转标签管理
     * @Date: 2018/3/27 9:38
     * @param: model
     * @Author: wsc
     */
    @GetMapping("/label")
    public String label(Model model,HttpSession session,Integer courseId){
        User user = (User)session.getAttribute(Constants.BACKEND_USER_SESSSION_KEY);
        MenuDto menuDto = backendUserService.findMenu(user);
        MenuDto menuChildDto = backendUserService.findChildMenuByParentCodeAndUser(user,"02");
        model.addAttribute("menuFirstList", menuDto.getMenuList());
        model.addAttribute("menuChildList", menuChildDto.getMenuList());
        model.addAttribute("courseId",courseId);
        model.addAttribute("curFirstMenu","02");
        model.addAttribute("curChildMenu","0201");
        return "resource/label";
    }

    /**
     * @Description:获取标签总数
     * @Date: 2018/3/27 9:50
     * @param: name 标签名称
     * @Author: wsc
     */
    @GetMapping("/getLabelCount")
    @ResponseBody
    public CommonResponseDto getLabelCount(String name){
        return labelService.getLabelCount(name,Constants.LabelType.COURSE);
    }

    /**
     * @Description: 获取标签列表
     * @Date: 2018/3/27 10:06
     * @param: pageNo 当前页
     * @param: pageSize 每页数量
     * @param: name 标签名称
     * @param: sort 排序标识 1：最新 2：最早
     * @Author: wsc
     */
    @PostMapping("/findLabelByName")
    @ResponseBody
    public CommonResponseDto<PageInfo<Label>> findLabelByName(Integer pageNo, Integer pageSize , String name, String sort){
        return labelService.findLabelByName(pageNo,pageSize,name,sort,Constants.LabelType.COURSE);
    }

    /**
     * @Description:新增标签
     * @Date: 2018/3/27 9:54
     * @param: name 标签名称
     * @Author: wsc
     */
    @PostMapping("/addLabel")
    @ResponseBody
    public CommonResponseDto addLabel(String name){
        return labelService.addLabel(name,Constants.LabelType.COURSE);
    }

    /**
     * @Description:根据id删除标签
     * @Date: 2018/3/27 9:54
     * @param: id 标签id
     * @Author: wsc
     */
    @PostMapping("/deleteLabel")
    @ResponseBody
    public CommonResponseDto deleteLabel(String ids,String state){
        return labelService.deleteLabel(ids,state);
    }

    /**
     * @Description:修改标签
     * @Date: 2018/3/27 9:54
     * @param: id 标签id
     * @param: name 标签名称
     * @Author: wsc
     */
    @PostMapping("/updateLabel")
    @ResponseBody
    public CommonResponseDto updateLabel(Integer id,String name){
        return labelService.updateLabel(id,name);
    }

    /**
     * @Description:根据id查询
     * @Date: 2018/3/27 14:25
     * @param: id 标签id
     * @Author: wsc
     */
    @PostMapping(value = "/getLabel")
    @ResponseBody
    public CommonResponseDto getLabel(Integer id){
        Label label = labelService.selectById(id);
        return CommonResponseDto.ofSuccess(label);
    }

    /**
     * @Description:根据名称查询标签
     * @Date: 2018/4/9 10:06
     * @param: name
     * @Author: wsc
     */
    @PostMapping("/getLabelByName")
    @ResponseBody
    public CommonResponseDto getLabelByName(String name){
        return labelService.getLabelByName(name);
    }

    /**
     * @Description:获取全部标签
     * @Date: 2018/4/9 17:36
     * @Author: wsc
     */
    @PostMapping("/getLabelAll")
    @ResponseBody
    public CommonResponseDto getLabelAll(){
        return labelService.getLabelAll();
    }

    /**
     * @Description:根据标签id查询关联的课程
     * @Date: 2018/4/10 16:42
     * @param: labelId
     * @Author: wsc
     */
    @PostMapping("/findCourse")
    @ResponseBody
    public CommonResponseDto findCourse(Integer labelId){
        return labelService.findCourse(labelId);
    }
}
