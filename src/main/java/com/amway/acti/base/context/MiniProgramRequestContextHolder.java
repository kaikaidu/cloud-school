/**
 * Created by dk on 2018/7/6.
 */

package com.amway.acti.base.context;

import com.amway.acti.model.MiniProgramRequest;
import org.springframework.util.Assert;

public class MiniProgramRequestContextHolder {

    private static final ThreadLocal<MiniProgramRequest> contextHolder = new ThreadLocal<>();

    public void clearContext() {
        contextHolder.remove();
    }

    public static MiniProgramRequest getRequestUser() {
        MiniProgramRequest ctx = contextHolder.get();
        return ctx;
    }

    public static void setRequestUser(MiniProgramRequest miniProgramRequest) {
        Assert.notNull(miniProgramRequest, "Only non-null MiniProgramRequest context instances are permitted");
        contextHolder.set(miniProgramRequest);
    }
}
