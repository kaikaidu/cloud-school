package com.amway.acti.base.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

/**
 * Created by Harry Xu on 2017/9/26.
 */
public class SpringContextUtil {

    private static ApplicationContext applicationContext;

    public static void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        synchronized (SpringContextUtil.class) {
            SpringContextUtil.applicationContext = applicationContext;
        }
    }

    public static ApplicationContext getApplicationContext() {
        return SpringContextUtil.applicationContext;
    }

    public static Object getBean(String name) {
        return SpringContextUtil.applicationContext.getBean(name);
    }

    public static <T> T getBean(Class<T> clazz) {
        return SpringContextUtil.applicationContext.getBean(clazz);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return SpringContextUtil.applicationContext.getBean(name, clazz);
    }
}
