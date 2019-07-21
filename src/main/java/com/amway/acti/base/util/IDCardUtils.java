package com.amway.acti.base.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description:校验身份证
 * @Date:2018/3/12 11:40
 * @Author:wsc
 */
public class IDCardUtils {
    private IDCardUtils(){}
    private static boolean flag = false;

    /**
     * @Description: 身份证的有效验证
     * @Date: 2018/3/12 11:43
     * @param: IDStr 身份证号
     * @Author: wsc
     */
    public static boolean iDCardValidate(String iDStr) throws ParseException {
        String ai = "";
        GregorianCalendar gc = new GregorianCalendar();
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");

        Map h = getAreaCode();
        //判断最后一位的值
        int totalmulAiWi = 0;
        //号码的长度18位
        if (iDStr.length() != 18) {
            return flag;
        }
        if (iDStr.length() == 18) {
            //数字 除最后以为都为数字
            ai = iDStr.substring(0, 17);
        }
        //出生年月是否有效
        String strYear = ai.substring(6, 10);// 年份
        String strMonth = ai.substring(10, 12);// 月份
        String strDay = ai.substring(12, 14);// 日

        if (isNumeric(ai) == flag) {
            return flag;
        }
        if (isDate(strYear + "-" + strMonth + "-" + strDay) == flag) {
            return flag;
        }
        if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 150 || (gc.getTime().getTime() - s.parse(strYear + "-" + strMonth + "-" + strDay).getTime()) < 0) {
            //身份证生日不在有效范围。
            return flag;
        }
        if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0) {
            //身份证月份无效
            return flag;
        }
        if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {
            //身份证日期无效
            return flag;
        }
        if (h.get(ai.substring(0, 2)) == null) {
            //地区码时候有效
            return flag;
        }
        for (int i = 0; i < 17; i++) {
            totalmulAiWi = totalmulAiWi + Integer.parseInt(String.valueOf(ai.charAt(i))) * Integer.parseInt(Constants.WI[i]);
        }
        int modValue = totalmulAiWi % 11;
        String strVerifyCode = Constants.VALCODEARR[modValue];
        ai = ai + strVerifyCode;

        if (iDStr.length() == 18 && ai.equals(iDStr) == flag) {
            //身份证无效，不是合法的身份证号码
            return flag;
        }
        return true;
    }

    /**
     * @Description: 设置地区编码
     * @Date: 2018/3/12 11:42
     * @param:
     * @Author: wsc
     */
    private static Map<String, String> getAreaCode() {
        Map<String,String> map = new HashMap <>();
        map.put("11", "北京");
        map.put("12", "天津");
        map.put("13", "河北");
        map.put("14", "山西");
        map.put("15", "内蒙古");
        map.put("21", "辽宁");
        map.put("22", "吉林");
        map.put("23", "黑龙江");
        map.put("31", "上海");
        map.put("32", "江苏");
        map.put("33", "浙江");
        map.put("34", "安徽");
        map.put("35", "福建");
        map.put("36", "江西");
        map.put("37", "山东");
        map.put("41", "河南");
        map.put("42", "湖北");
        map.put("43", "湖南");
        map.put("44", "广东");
        map.put("45", "广西");
        map.put("46", "海南");
        map.put("50", "重庆");
        map.put("51", "四川");
        map.put("52", "贵州");
        map.put("53", "云南");
        map.put("54", "西藏");
        map.put("61", "陕西");
        map.put("62", "甘肃");
        map.put("63", "青海");
        map.put("64", "宁夏");
        map.put("65", "新疆");
        return map;
    }

    /**
     * @Description: 判断字符串是否为数字
     * @Date: 2018/3/12 11:42
     * @param: str
     * @Author: wsc
     */
    private static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (isNum.matches()) {
            return true;
        } else {
            return flag;
        }
    }

    /**
     * @Description: 判断字符串是否为日期格式
     * @Date: 2018/3/12 11:42
     * @param: strDate
     * @Author: wsc
     */
    public static boolean isDate(String strDate) {
        Pattern pattern = Pattern
                .compile("^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
        Matcher m = pattern.matcher(strDate);
        if (m.matches()) {
            return true;
        } else {
            return flag;
        }
    }
}
