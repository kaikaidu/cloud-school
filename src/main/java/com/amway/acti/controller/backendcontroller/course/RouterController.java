package com.amway.acti.controller.backendcontroller.course;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Description:路由
 * @Date:2018/6/12 10:29
 * @Author:wsc
 */
@Controller
@RequestMapping("/backend")
public class RouterController {

    @GetMapping("/index")
    public String getIndex(){
        return "pages/index";
    }
}
