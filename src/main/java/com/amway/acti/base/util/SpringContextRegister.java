/**
 * Created by dk on 2018/2/22.
 */

package com.amway.acti.base.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContextRegister implements ApplicationContextAware {

    private final Logger log = LoggerFactory.getLogger(SpringContextRegister.class);

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        log.info("register the ApplicationContext");
        SpringContextUtil.setApplicationContext(applicationContext);
    }
}
