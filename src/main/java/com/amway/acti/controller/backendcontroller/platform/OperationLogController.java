package com.amway.acti.controller.backendcontroller.platform;


import com.amway.acti.base.util.Constants;
import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.dto.MenuDto;
import com.amway.acti.model.User;
import com.amway.acti.service.BackendUserService;
import com.amway.acti.service.OperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@Slf4j
@RequestMapping("/backend/platform")
public class OperationLogController{

    @Autowired
    private BackendUserService backendUserService;

    @Autowired
    private OperationLogService operationLogService;

    /**
     * 进入默认页面
     * @param model
     * @return
     */
    @GetMapping("/operationLogIndex")
    public String adminIndex(Model model,HttpSession session){
        User user = (User)session.getAttribute(Constants.BACKEND_USER_SESSSION_KEY);
        MenuDto menuDto = backendUserService.findMenu(user);
        MenuDto menuChildDto = backendUserService.findChildMenuByParentCodeAndUser(user,"04");
        model.addAttribute("menuFirstList", menuDto.getMenuList());
        model.addAttribute("menuChildList", menuChildDto.getMenuList());
        model.addAttribute("curFirstMenu","04");
        model.addAttribute("curChildMenu","0403");
        return "platform/operationLogList";
    }

    /**
     * 查询默认页面数据
     * @param pageNo
     * @param pageSize
     * @return
     * @throws Exception
     */
    @RequestMapping("/operationLogList")
    @ResponseBody
    public CommonResponseDto adminList(@RequestParam(value = "pageNo") Integer pageNo,
                                       @RequestParam(value = "pageSize") Integer pageSize) throws Exception {
        return CommonResponseDto.ofSuccess(operationLogService.findOperationLogList(pageNo,pageSize));
    }

    /**
     * 查询分页记录数
     * @return
     */
    @RequestMapping("/queryOperationLogListCount")
    @ResponseBody
    public String queryOperationLogListCount(){
        return operationLogService.queryCount();
    }

}
