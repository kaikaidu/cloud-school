package com.amway.acti.base.util;

import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author Wei.Li1
 * @create 2018-05-07 15:33
 **/
public class DownloadUtil {

    private DownloadUtil(){}

    /**
     * 下载文件时，针对不同浏览器，进行附件名的编码
     * @param filename 下载文件名
     * @param request 客户端浏览器(通过request.getHeader("user-agent")获得)
     * @return 编码后的下载附件名
     */
    public static String encodeDownloadFilename(String filename, HttpServletRequest request) throws UnsupportedEncodingException {
        String agent = request.getHeader( "user-agent" );
        if(agent.contains("Firefox")){ // 火狐浏览器
            filename = "=?UTF-8?B?"+new BASE64Encoder().encode(filename.getBytes("utf-8"))+"?=";
        }else{ // IE及其他浏览器
            filename = URLEncoder.encode(filename,"utf-8");
        }
        return filename;
    }
}
