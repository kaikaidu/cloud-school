package com.amway.acti.base.util;

import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * author:qj 2018.5.24
 * 文件下载特殊字符编码设置(穷举出所有的URLEncoder.encode后不同的字符),后期如有组合不同产生的编码不同需手动更改
 */
@Slf4j
public class URLTransForm {

    private static final String ALLOWED_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_.!~*'()";

    private URLTransForm(){}

    public static String transForm(String resutlName){
        String url = resutlName.substring(0,52);
        String name = resutlName.substring(52,resutlName.length());
        try {
            name = URLEncoder.encode( name, "UTF-8" );
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        }
        String result;
        result = name.replaceAll("\\+", "%20");
        result.replaceAll("%7E", "~");
        result.replaceAll("%21", "!");
        result.replaceAll("%40", "@");
        result.replaceAll("%24", "\\$");
        result.replaceAll("%26", "&");
        result.replaceAll("%28", "(");
        result.replaceAll("%29", ")");
        result.replaceAll("%2B", "+");
        result.replaceAll("%3D", "=");
        result.replaceAll("%27", "'");
        result.replaceAll("%3B", ";");
        result.replaceAll("%2C", ",");
        result.replaceAll("%7E", "~");
        return url+result;
    }
}
