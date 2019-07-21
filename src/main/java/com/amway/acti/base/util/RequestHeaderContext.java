/**
 * Created by dk on 2018/2/22.
 */

package com.amway.acti.base.util;

public class RequestHeaderContext {

    /*private static final ThreadLocal<RequestHeaderContext> REQUEST_HEADER_CONTEXT_THREAD_LOCAL = new ThreadLocal<>();
    private String openId;
    private String unionId;
    private Integer uid;

    public String getOpenId() {
        return openId;
    }

    public String getUnionId() {
        return unionId;
    }

    public Integer getUid() {
        return uid;
    }

    public static RequestHeaderContext getInstance() {
        return REQUEST_HEADER_CONTEXT_THREAD_LOCAL.get();
    }

    public void setContext(RequestHeaderContext context) {
        REQUEST_HEADER_CONTEXT_THREAD_LOCAL.set(context);
    }

    public static void clean() {
        REQUEST_HEADER_CONTEXT_THREAD_LOCAL.remove();
    }

    private RequestHeaderContext(RequestHeaderContextBuild requestHeaderContextBuild) {
        this.unionId = requestHeaderContextBuild.unionId;
        this.openId = requestHeaderContextBuild.openId;
        this.uid = requestHeaderContextBuild.uid;
        setContext(this);
    }

    public static class RequestHeaderContextBuild {
        private String unionId;
        private String openId;
        private Integer uid;

        public RequestHeaderContextBuild unionId(String unionId) {
            this.unionId = unionId;
            return this;
        }

        public RequestHeaderContextBuild openId(String openId) {
            this.openId = openId;
            return this;
        }

        public RequestHeaderContextBuild uid(Integer uid) {
            this.uid = uid;
            return this;
        }

        public RequestHeaderContext bulid() {
            return new RequestHeaderContext(this);
        }
    }*/
}
