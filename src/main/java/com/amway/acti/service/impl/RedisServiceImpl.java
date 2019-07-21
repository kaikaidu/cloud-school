package com.amway.acti.service.impl;

import com.amway.acti.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * <redis实现类>
 * <功能详细描述>
 *
 * @author zhengweishun
 * @version [版本号, 2018/4/24]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
@Slf4j
@Service
public class RedisServiceImpl implements RedisService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * setNx
     *
     * @param key
     * @param value
     * @param timeout
     * @return
     */
    @Override
    public boolean setNx(String key, String value, long timeout) {
        Boolean b = stringRedisTemplate.opsForValue().setIfAbsent(key, value);
        if (b) {
            stringRedisTemplate.expire(key, timeout, TimeUnit.SECONDS);
        }
        return b;
    }

    @Override
    public boolean setNx0(String key, String value) {
        return stringRedisTemplate.opsForValue().setIfAbsent(key, value);
    }

    /**
     * del
     * 根据key删除缓存中的记录
     *
     * @param key
     */
    @Override
    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }

    /**
     * 根据key获取value
     *
     * @param key
     * @return
     */
    @Override
    public Object getValue(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 存入
     *
     * @param key
     */
    @Override
    public void set(String key, String value, long timeout) {
        stringRedisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);//向redis里存入数据和设置缓存时间
    }

    /**
     * @Description:模糊查询key 极耗资源 慎用
     * @Date: 2018/5/21 18:34
     * @param: key
     * @Author: wsc
     */
    @Override
    public Set<String> keys(String key) {
        return stringRedisTemplate.keys(key + "*");
    }
}
