package com.amway.acti.base.util;

/**
 * @author Wei.Li1
 * @create 2018-04-12 21:24
 **/
public class ScoreUtil {

    private ScoreUtil(){}

    public static String format(String str){
        if(str.endsWith( ".00" )){
            return str.substring(0, str.length()-3 );
        }
        if(str.indexOf( '.' ) != -1){
            String aa = str;
            for(int i = aa.length()-1; i > 0; i--){
                if(aa.toCharArray()[i] == '0' || aa.toCharArray()[i] == '.'){
                    str = str.substring(0, str.length() -1 );
                }else{
                    break;
                }
            }
        }
        return str;
    }
}
