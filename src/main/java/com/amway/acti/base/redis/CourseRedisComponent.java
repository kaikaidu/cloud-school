package com.amway.acti.base.redis;

import com.amway.acti.base.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CourseRedisComponent {

    @Autowired
    private RedisComponent redisComponent;

    public void delCourseRedisByCourseId(int courseId){
        String key1 = Constants.COURSE_CACHE_KEY+courseId;
        String[] keys1 = redisComponent.keys( key1 );
        redisComponent.del( keys1 );
        String key2 = Constants.COURSE_USER_INFO+courseId;
        String[] keys2 = redisComponent.keys( key2 );
        redisComponent.del( keys2 );
    }
}
