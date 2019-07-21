/**
 * Created by dk on 2018/7/3.
 */

package com.amway.acti.base.queue;

import com.amway.acti.base.util.Constants;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import java.util.concurrent.CountDownLatch;

//@Configuration
public class QueueListenerConfig {

    //@Bean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
                                            MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter, new PatternTopic(Constants.QUEUE_AWARDCERT));
        return container;
    }

    //@Bean
    MessageListenerAdapter listenerAdapter(AwardCertReceiver receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }

    //@Bean
    AwardCertReceiver receiver(CountDownLatch latch) {
        return new AwardCertReceiver(latch);
    }

    //@Bean
    CountDownLatch latch() {
        return new CountDownLatch(1);
    }
}
