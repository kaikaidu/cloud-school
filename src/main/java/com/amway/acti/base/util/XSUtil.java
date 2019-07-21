/**
 * Created by dk on 2018/3/20.
 */

package com.amway.acti.base.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class XSUtil{

    public static String loadXML(String name){
        if(StringUtils.isBlank(name)){
            return null;
        }
        InputStream stream = XSUtil.class.getClassLoader().getResourceAsStream(name);
        List<String> list = null;
        try {
            list = IOUtils.readLines(stream);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        for(String s : list){
            sb.append(s);
        }
        String xml = sb.toString();
        return xml;
    }

    public static String send(String url, String param){
        if(StringUtils.isBlank(url) || StringUtils.isBlank(param)){
            return null;
        }
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        URL realUrl;
        try {
            realUrl = new URL(url);
            URLConnection conn = realUrl.openConnection();
            conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());
            out.print(param);
            out.flush();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            String line;
            while((line=in.readLine()) != null){
                result += line;
            }
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return result;
    }

    public static String getNodeContext(String node, String xml) {
        Pattern p = Pattern.compile(node+">([^</]+)</"+node);//正则表达式 commend by danielinbiti
        Matcher m = p.matcher(xml);
        String result = null;
        while (m.find()) {
            result = m.group(1);
            break;
        }
        return result;
    }

    public static String getNodeContextWithCDATA(String node, String xml) {
        Pattern p = Pattern.compile(node+"><!\\[CDATA\\[([^</]+)\\]\\]></"+node);//正则表达式 commend by danielinbiti
        Matcher m = p.matcher(xml);
        String result = null;
        while (m.find()) {
            result = m.group(1);
            break;
        }
        return result;
    }

    public static String beanToString(Object object){
        return ToStringBuilder.reflectionToString(object);
    }


}
