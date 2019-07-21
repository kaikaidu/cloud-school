package com.amway.acti.controller.backendcontroller;

import com.amway.acti.base.util.Constants;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Description:证书设置，证书模板跳转
 * @Date:2018/6/26 17:36
 * @Author:wsc
 */
@Controller
@RequestMapping(value = "/backend/v2")
public class RedirectController {

    /**
     * @Description:跳转证书列表
     * @Date: 2018/6/26 18:12
     * @param: request
     * @param: response
     * @Author: wsc
     */
    @RequestMapping("/cert-setting")
    public void certSetting(HttpServletRequest request, HttpServletResponse response) throws IOException {
        redirect(request,response, Constants.Redirect.CERT_SETTING);
    }

    /**
     * @Description:跳转新增证书
     * @Date: 2018/6/26 18:12
     * @param: request
     * @param: response
     * @Author: wsc
     */
    @RequestMapping("/cert-setting/add")
    public void addCertSetting(HttpServletRequest request, HttpServletResponse response) throws IOException {
        redirect(request,response,Constants.Redirect.CERT_SETTING_ADD);
    }

    /**
     * @Description:跳转证书模板
     * @Date: 2018/6/26 18:12
     * @param: request
     * @param: response
     * @Author: wsc
     */
    @RequestMapping("/cert-templates")
    public void certTemplates(HttpServletRequest request, HttpServletResponse response) throws IOException {
        redirect(request,response,Constants.Redirect.CERT_TEMPLATES);
    }

    /**
     * @Description:跳转新增证书模板
     * @Date: 2018/6/26 18:12
     * @param: request
     * @param: response
     * @Author: wsc
     */
    @RequestMapping("/cert-templates/add")
    public void addCertTemplates(HttpServletRequest request, HttpServletResponse response) throws IOException {
        redirect(request,response,Constants.Redirect.CERT_TEMPLATES_ADD);
    }

    //跳转路由
    public void redirect (HttpServletRequest request, HttpServletResponse response,String redirect) throws IOException {
        String refUrl = "";
        if (null != request.getHeader("Referer") && request.getHeader("Referer").contains("courseId")) {
            refUrl = request.getHeader("Referer").toString();
            refUrl = refUrl.substring(refUrl.indexOf("courseId=")+9);
        }
        if (null != request.getHeader("Referer") && request.getHeader("Referer").contains("courseId")) {
            response.sendRedirect(request.getContextPath() + "/backend/index?redirect="+redirect+"&courseId="+refUrl);
        } else {
            if (redirect.equals(Constants.Redirect.CERT_SETTING)) {
                if (!StringUtils.isEmpty(request.getQueryString())) {
                    response.sendRedirect(request.getContextPath() + "/backend/index?redirect="+redirect+"&"+request.getQueryString());
                } else {
                    response.sendRedirect(request.getContextPath() + "/backend/course/basic");
                }
            } else {
                if (!StringUtils.isEmpty(request.getQueryString())) {
                    response.sendRedirect(request.getContextPath() + "/backend/index?redirect="+redirect+"&"+request.getQueryString());
                } else {
                    response.sendRedirect(request.getContextPath() + "/backend/index?redirect="+redirect);
                }
            }
        }
    }
}
