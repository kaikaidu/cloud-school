/**
 * Created by dk on 2018/6/8.
 */

package com.amway.acti.base.util;

import java.util.ArrayList;
import java.util.List;

public class IPWhitelistUtil {

    public static List<String> ipWhiteList = null;

    /***
     * 管理身份ip白名单
     */
    static {

        /** 183.62.18.192/28 **/
        ipWhiteList = new ArrayList<>();
        ipWhiteList.add("183.62.18.193");
        ipWhiteList.add("183.62.18.194");
        ipWhiteList.add("183.62.18.195");
        ipWhiteList.add("183.62.18.196");
        ipWhiteList.add("183.62.18.197");
        ipWhiteList.add("183.62.18.198");
        ipWhiteList.add("183.62.18.199");
        ipWhiteList.add("183.62.18.200");
        ipWhiteList.add("183.62.18.201");
        ipWhiteList.add("183.62.18.202");
        ipWhiteList.add("183.62.18.203");
        ipWhiteList.add("183.62.18.204");
        ipWhiteList.add("183.62.18.205");
        ipWhiteList.add("183.62.18.206");

        /** 58.248.178.96/28 **/
        ipWhiteList.add("58.248.178.97");
        ipWhiteList.add("58.248.178.98");
        ipWhiteList.add("58.248.178.99");
        ipWhiteList.add("58.248.178.100");
        ipWhiteList.add("58.248.178.101");
        ipWhiteList.add("58.248.178.102");
        ipWhiteList.add("58.248.178.103");
        ipWhiteList.add("58.248.178.104");
        ipWhiteList.add("58.248.178.105");
        ipWhiteList.add("58.248.178.106");
        ipWhiteList.add("58.248.178.107");
        ipWhiteList.add("58.248.178.108");
        ipWhiteList.add("58.248.178.109");
        ipWhiteList.add("58.248.178.110");

        /** 58.248.10.80/29 **/
        ipWhiteList.add("58.248.10.81");
        ipWhiteList.add("58.248.10.82");
        ipWhiteList.add("58.248.10.83");
        ipWhiteList.add("58.248.10.84");
        ipWhiteList.add("58.248.10.85");
        ipWhiteList.add("58.248.10.86");

        /** 59.41.187.8/29 **/
        ipWhiteList.add("59.41.187.9");
        ipWhiteList.add("59.41.187.10");
        ipWhiteList.add("59.41.187.11");
        ipWhiteList.add("59.41.187.12");
        ipWhiteList.add("59.41.187.13");
        ipWhiteList.add("59.41.187.14");

        //other
        ipWhiteList.add("205.252.237.97");
        ipWhiteList.add("218.1.16.86");
        ipWhiteList.add("61.135.33.126");
        ipWhiteList.add("222.190.113.250");
        ipWhiteList.add("10.129.223.133");
        ipWhiteList.add("101.231.62.66");

        ipWhiteList.add("49.87.252.27");
        ipWhiteList.add("0:0:0:0:0:0:0:1");
        ipWhiteList.add("0");
    }
}
