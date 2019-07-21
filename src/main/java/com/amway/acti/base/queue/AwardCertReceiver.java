/**
 * Created by dk on 2018/7/3.
 */

package com.amway.acti.base.queue;

import com.amway.acti.service.BackendUserCertService;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;

@Slf4j
public class AwardCertReceiver {

    private CountDownLatch latch;

    //@Autowired
    private BackendUserCertService backendUserCertService;

   // @Autowired
    public AwardCertReceiver(CountDownLatch latch) {
        this.latch = latch;
    }

    public void receiveMessage(String message) throws InterruptedException {
        log.info("开始处理队列,message:{}",message);
        long time = System.currentTimeMillis();
        //backendUserCertService.createCertUpdateCertState(message);
        log.info("endTime:{}",System.currentTimeMillis() - time);
        latch.countDown();
    }
}
