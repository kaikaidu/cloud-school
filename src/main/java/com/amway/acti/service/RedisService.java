package com.amway.acti.service;

import java.util.Set;

/**
 * <redis接口>
 * <功能详细描述>
 *
 * @author zhengweishun
 * @version [版本号, 2018/4/24]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public interface RedisService {

    /**
     * setNx
     * 通过redis实现分布式锁
     * @param key
     * @param value
     * @param timeout
     * @return
     */
    boolean setNx(String key, String value, long timeout);

    /**
     * del
     * 删除key
     * @param key
     */
    void delete(String key);

    /**
     * 根据key获取value
     * @param key
     * @return
     */
    Object getValue(String key);

    /**
     * 存入
     * @param key
     */
    void set(String key,String value,long timeout);

    /**
     * 模糊查询key
     * @param key
     * @return
     */
    Set<String> keys(String key);

    boolean setNx0(String key, String value);

}
