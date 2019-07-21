/**
 * Created by dk on 2018/7/5.
 */

package com.amway.acti.base.initializing;

import com.amway.acti.base.redis.RedisComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ClearRedisKeyInitializingBean implements InitializingBean {

    @Autowired
    private RedisComponent redisComponent;

    @Override
    public void afterPropertiesSet() throws Exception {
        /*String[] keys = null;
        keys = redisComponent.keys(Constants.UPDATE_COURSE_CERT);
        redisComponent.del(keys);

        keys = redisComponent.keys(Constants.UPDATE_CERT);
        redisComponent.del(keys);
        log.info("已成功清除-颁发证书的缓存标识");

        keys = redisComponent.keys(Constants.UPDATE_CERT_COUNT);
        redisComponent.del(keys);
        log.info("已成功清除-颁发证书的成功数量");

        keys = redisComponent.keys(Constants.UPDATE_CERT_TOTAL);
        redisComponent.del(keys);
        log.info("已成功清除-颁发证书的总数");

        keys = redisComponent.keys(Constants.SAFETYLOCK_STULOGIN);
        redisComponent.del(keys);

        keys = redisComponent.keys(Constants.SAFETYLOCK_STUCONFIRMSEX);
        redisComponent.del(keys);
        log.info("已成功清除-同步锁-学员登录");

        keys = redisComponent.keys(Constants.SAFETYLOCK_AGREE);
        redisComponent.del(keys);
        log.info("已成功清除-同步锁-学员点赞");

        keys = redisComponent.keys(Constants.SAFETYLOCK_SIGN);
        redisComponent.del(keys);
        log.info("已成功清除-同步锁-学员报名");

        keys = redisComponent.keys(Constants.SAFETYLOCK_SITEMSUBMIT);
        redisComponent.del(keys);
        log.info("已成功清除-同步锁-评分提交");

        keys = redisComponent.keys(Constants.SAFETYLOCK_TESTSUBMIT);
        redisComponent.del(keys);
        log.info("已成功清除-同步锁-测试提交");

        keys = redisComponent.keys(Constants.SAFETYLOCK_INVESSUBMIT);
        redisComponent.del(keys);
        log.info("已成功清除-同步锁-问卷提交");

        keys = redisComponent.keys(Constants.STUDENT_COUNT);
        redisComponent.del(keys);
        log.info("已成功清除-同步锁-报名成功数量");

        keys = redisComponent.keys(Constants.STUDENT_TOTAL);
        redisComponent.del(keys);
        log.info("已成功清除-同步锁-报名总数");

        keys = redisComponent.keys(Constants.UPDATE_COURSE_SIGN);
        redisComponent.del(keys);
        log.info("已成功清除-同步锁-报名的缓存标识");*/
    }
}
