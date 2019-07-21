package com.amway.acti.controller.backendcontroller;

import com.amway.acti.base.util.Constants;
import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.dto.MenuDto;
import com.amway.acti.model.Register;
import com.amway.acti.model.User;
import com.amway.acti.service.BackendUserService;
import com.amway.acti.service.RegisterManageService;
import com.google.zxing.WriterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * @Author Jie.Qiu
 */
@Controller
@Slf4j
@RequestMapping("/backend/course/registerManage")
public class BackendRegisterManageController {

    @Autowired
    private RegisterManageService registerManageService;

    @Autowired
    private BackendUserService backendUserService;


    /**
     * 跳转到签到管理页面
     * @return
     */
    @RequestMapping("/Index")
    public ModelAndView forwardSignManage(HttpSession session, Register register, @RequestParam(required = false) Integer courseId) {
        ModelAndView mv = new ModelAndView();
        if (null == courseId || courseId < 1) {
            mv.setViewName("redirect:/backend/course/basic");
            return mv;
        }
        User user = (User) session.getAttribute(Constants.BACKEND_USER_SESSSION_KEY);
        MenuDto menuDto = backendUserService.findMenu(user);
        MenuDto menuChildDto = backendUserService.findChildMenuByParentCodeAndUser(user,"01");
        if(courseId != null){
            mv.addObject("courseId",courseId);
        }
        mv.addObject("menuFirstList", menuDto.getMenuList());
        mv.addObject("menuChildList", menuChildDto.getMenuList());
        mv.addObject("curFirstMenu","01");
        mv.addObject("curChildMenu","0103");
        mv.setViewName("course/registerManage");
        return mv;
    }

    /**
     * 查询列表页面
     * @param register
     * @param pageNo
     * @param pageSize
     * @return
     * @throws IOException
     * @throws WriterException
     */
    @RequestMapping(value = "/registerList",method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto findSignByCourse(Register register, @RequestParam(value = "pageNo") Integer pageNo, @RequestParam("pageSize") Integer pageSize){
        List<Register> registerList = registerManageService.findRegisterList(register,pageNo,pageSize);
        return CommonResponseDto.ofSuccess(registerList);
    }

    /**
     * 查询总记录数
     * @param register
     * @return
     */
    @RequestMapping(value = "/queryCount",method = RequestMethod.POST)
    @ResponseBody
    public String querySignListCount(Register register){
        List<Register> registerList = registerManageService.queryRegisterList(register);
        return String.valueOf(registerList.size());
    }

    /**
     * 批量签到
     * @param uIds
     * @param courseId
     * @return
     */
    @RequestMapping(value = "/registerUp", method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto registerUp(String uIds, String courseId) {
        registerManageService.batchRegisterUp(uIds, courseId);
        return CommonResponseDto.ofSuccess();
    }

    /**
     * 查看二维码
     * @param courseId
     * @return
     * @throws IOException
     * @throws WriterException
     */
    @RequestMapping(value = "/findQRCode", method = RequestMethod.GET)
    @ResponseBody
    public CommonResponseDto findQRCode(String courseId) throws IOException, WriterException {
        String url = registerManageService.codeCreate(courseId);
        return CommonResponseDto.ofSuccess(url);
    }

}
