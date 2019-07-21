package com.amway.acti.base.redis;

import com.amway.acti.base.util.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.TimeoutUtils;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Wei.Li1
 * @create 2018-05-22 20:33
 **/
@Component
@Slf4j
public class RedisComponent {

    @Resource
    @Qualifier("myRedisTemplate")
    private RedisTemplate<String, ?> redisTemplate;

    public boolean hasKey(String key){
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error( e.getMessage(), e );
            return false;
        }
    }

    public String[] keys(String part){
        Set<String> keys = redisTemplate.keys( part+"*" );
        return keys.toArray(new String[keys.size()]);
    }

    public boolean set(final String key, final String value) {
        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                connection.set(serializer.serialize(key), serializer.serialize(value));
                return true;
            }
        });
        return result;
    }

    public boolean set(final String key, final String value, long expired, TimeUnit timeUnit) {
        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                connection.set(serializer.serialize(key), serializer.serialize(value));
                return true;
            }
        });
        redisTemplate.expire( key, expired, timeUnit );
        return result;
    }

    public String get(final String key){
        String result = redisTemplate.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                byte[] value =  connection.get(serializer.serialize(key));
                return serializer.deserialize(value);
            }
        });
        return result;
    }

    public boolean del(final String... key) {
        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                for(String s : key){
                    connection.del( serializer.serialize( s ) );
                }
                return true;
            }
        });
        return result;
    }

    public boolean expire(final String key, long expire, TimeUnit timeUnit) {
        return redisTemplate.expire(key, expire, timeUnit);
    }

    public <T> T get(String key, Class<T> clz){
        String value = get( key );
        return JSONUtil.toBean( value, clz );
    }

    public <T> boolean set(String key, T t) {
        String value = JSONUtil.toJson(t);
        return set(key,value);
    }

    public <T> boolean set(String key, T t, long expire, TimeUnit timeUnit) {
        String value = JSONUtil.toJson(t);
        return set(key,value,expire,timeUnit);
    }

    public <T> boolean setList(String key, List<T> list) {
        String value = JSONUtil.toJson(list);
        return set(key,value);
    }

    public <T> boolean setList(String key, List<T> list, long expire, TimeUnit timeUnit) {
        String value = JSONUtil.toJson(list);
        return set(key,value,expire,timeUnit);
    }

    public <T> List<T> getList(String key, Class<T> clz) {
        String json = get( key );
        if (json != null) {
            List <T> list = JSONUtil.toList( json, clz );
            return list;
        }
        return null;
    }

    public long lpush(final String key, Object obj) {
        final String value = JSONUtil.toJson(obj);
        long result = redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                long count = connection.lPush(serializer.serialize(key), serializer.serialize(value));
                return count;
            }
        });
        return result;
    }

    public long rpush(final String key, Object obj) {
        final String value = JSONUtil.toJson(obj);
        long result = redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                long count = connection.rPush(serializer.serialize(key), serializer.serialize(value));
                return count;
            }
        });
        return result;
    }

    public String lpop(final String key) {
        String result = redisTemplate.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                byte[] res =  connection.lPop(serializer.serialize(key));
                return serializer.deserialize(res);
            }
        });
        return result;
    }

    private Map<byte[], byte[]> toBytes(Map<String, ?> map){
        RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
        Map<byte[], byte[]> mm = new HashMap <>( map.size() );
        for(String key : map.keySet()){
            String json = JSONUtil.toJson( map.get( key ) );
            mm.put( serializer.serialize( key ), serializer.serialize( json ) );
        }
        return mm;
    }

    public <T> boolean setHMap(String key, Map<String, T> map){
        if(map == null || map.isEmpty()){
            return false;
        }
        Boolean result = redisTemplate.execute( new RedisCallback <Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection redisConnection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                redisConnection.hMSet( serializer.serialize( key ), toBytes( map ) );
                return true;
            }
        } );
        return result;
    }

    public <T> boolean setHMap(String key, Map<String, T> map, long expire, TimeUnit timeUnit){
        if(map == null || map.isEmpty()){
            return false;
        }
        Boolean result = redisTemplate.execute( new RedisCallback <Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection redisConnection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                redisConnection.hMSet( serializer.serialize( key ), toBytes( map ) );
                return true;
            }
        } );
        redisTemplate.expire( key, expire, timeUnit );
        return result;
    }

    public <T> Map<String, T> getHMap(String key, Class<T> clz) {
        String json = get( key );
        if (json != null) {
            Map<String, T> map = JSONUtil.toMap( json, clz );
            return map;
        }
        return null;
    }

    public <T> T getHMapItem(String key, String item, Class<T> clz) {
        String result = redisTemplate.execute( new RedisCallback <String>() {
            @Override
            public String doInRedis(RedisConnection redisConnection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                byte[] res =  redisConnection.hGet( serializer.serialize( key ), serializer.serialize( item ) );
                return serializer.deserialize(res);
            }
        } );
        if(result != null){
            return JSONUtil.toBean( result, clz );
        }
        return null;
    }

    public <T> boolean delHMapItem(String key, String item) {
        Boolean result = redisTemplate.execute( new RedisCallback <Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection redisConnection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                redisConnection.hDel( serializer.serialize( key ), serializer.serialize( item ) );
                return true;
        }
        } );
        return result;
    }

    public <T> boolean delHMapItems(String key, List<String> items) {
        Boolean result = redisTemplate.execute( new RedisCallback <Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection redisConnection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                byte[] keyByte = serializer.serialize( key );
                for(String item : items){
                    redisConnection.hDel( keyByte, serializer.serialize( item ) );
                }
                return true;
            }
        } );
        return result;
    }

    public <T> boolean setHMapItem(String key, String item, T t) {
        if(t == null){
            return false;
        }
        String json = JSONUtil.toJson( t );
        Boolean result = redisTemplate.execute( new RedisCallback <Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection redisConnection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                return redisConnection.hSet( serializer.serialize( key ), serializer.serialize( item ), serializer.serialize( json ) );
            }
        } );
        return result;
    }

    public <T> boolean setHMapItem(String key, String item, T t, long expired, TimeUnit timeUnit) {
        if(t == null){
            return false;
        }
        String json = JSONUtil.toJson( t );
        Boolean result = redisTemplate.execute( new RedisCallback <Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection redisConnection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                return redisConnection.hSet( serializer.serialize( key ), serializer.serialize( item ), serializer.serialize( json ) );
            }
        } );
        redisTemplate.expire( key, expired, timeUnit );
        return result;
    }

    public <T> List<T>getHMapList(String key, Class<T> clz){
        RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
        List<byte[]> result = redisTemplate.execute( new RedisCallback <List <byte[]>>() {
            @Override
            public List <byte[]> doInRedis(RedisConnection redisConnection) throws DataAccessException {
                return redisConnection.hVals( serializer.serialize( key ) );
            }
        } );
        if(result == null){
            return null;
        }
        List<T> list = new ArrayList <>( result.size() );
        for(byte[] bytes : result){
            list.add( JSONUtil.toBean( serializer.deserialize( bytes ), clz ) );
        }
        return list;
    }
}
