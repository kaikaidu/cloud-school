package com.amway.acti.controller.backendcontroller;

import com.amway.acti.base.exception.AmwaySystemException;
import com.amway.acti.base.util.Constants;
import com.amway.acti.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Wei.Li1
 * @create 2018-03-13 9:56
 **/
@Slf4j
public class BaseController {

    protected HttpServletRequest request;

    protected HttpServletResponse response;

    protected HttpSession session;

    @ModelAttribute
    public void setReqAndResp(HttpServletRequest request, HttpServletResponse response){
        this.request = request;
        this.response = response;
        this.session = request.getSession();
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new CustomDateEditor(
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"), true));
        binder.registerCustomEditor(Date.class, new CustomDateEditor(
                new SimpleDateFormat("yyyy-MM-dd"), true));
    }

    /**
     * 输出信息到页面
     *
     * @param msg : 要输出的信息（可以是js脚本等）
     */
    public void printOutMsg(String msg){
        try {
            this.response.setCharacterEncoding("UTF-8");
            this.response.setContentType("text/html;charset=utf-8");
            PrintWriter out = this.response.getWriter();
            out.print(msg);
            out.flush();
            out.close();
        } catch (IOException e) {
            log.error( e.getMessage() );
            throw new AmwaySystemException("printOutMsg exception");
        }
    }

    /**
     * 输出信息到页面
     * @param response
     * @param msg
     */
    public void printOutMsg(HttpServletResponse response, String msg){
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html;charset=utf-8");
            PrintWriter out = null;
            out = response.getWriter();
            out.print(msg);
            out.flush();
            out.close();
        } catch (IOException e) {
            log.error( e.getMessage() );
        }
    }

    /**
     * 获取请求完整路径
     * @param request
     * @return
     */
    public String getUrl(HttpServletRequest request){
        String url = request.getRequestURI();
        String params = "";
        if(request.getQueryString()!=null){
            params = request.getQueryString();
        }
        if(!"".equals(params)){
            url = url+"?"+params;
        }
        return url;
    }

    /**
     * 获取请求完整路径
     * @return
     */
    public String getUrl(){
        String url = this.request.getRequestURI();
        String params = "";
        if(this.request.getQueryString()!=null){
            params = this.request.getQueryString();
        }
        if(!"".equals(params)){
            url = url+"?"+params;
        }
        return url;
    }

    /**
     * 转换统计的map
     * @param statMap       统计的map
     * @param constMap      常量的map
     * @return
     */
    public Map<String, Long> getFmtMap(Map<String, Long> statMap, Map<Integer, String> constMap){
        Map<String, Long> dataMap = null;
        if(statMap != null){
            dataMap = new LinkedHashMap<>();
            for(Map.Entry<String, Long> entry : statMap.entrySet()){
                dataMap.put(constMap.get(Integer.valueOf(entry.getKey()))+"&"+Integer.valueOf(entry.getKey()), entry.getValue());
            }
        }
        return dataMap;
    }

    /**
     * 获取sessionAdmin
     *
     * @return
     */
    public User getSessionAdmin(){
        return (User) this.session.getAttribute( Constants.BACKEND_USER_SESSSION_KEY );
    }

    /**
     * 在课程管理木块中，所有的页面跳转添加courseId
     * @param courseId
     */
    public void setCourseId(int courseId){
        String requestType = this.request.getHeader("X-Requested-With");
        if(!StringUtils.equals( "XMLHttpRequest", requestType )){
            this.request.setAttribute( "courseId", courseId );
        }
    }

    /**
     * 获取客户端IP
     * @param request
     * @return
     */
    public String getClientIp(HttpServletRequest request) {
        String clientIP = request.getHeader("X-Forwarded-For") != null ? request.getHeader("X-Forwarded-For") : request.getRemoteAddr();
        if (!org.springframework.util.StringUtils.isEmpty(clientIP)) {
            if(clientIP.indexOf(",") != -1){
                return clientIP.substring(0, clientIP.indexOf(","));
            }else{
                return clientIP;
            }
        }
        return null;
    }


}
