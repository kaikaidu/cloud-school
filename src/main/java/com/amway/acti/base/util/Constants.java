/**
 * Created by DK on 2018/2/11.
 */

package com.amway.acti.base.util;

import java.util.concurrent.TimeUnit;

public class Constants {

    /**jwt token**/
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "token";

    public static final String FRONTEND_URL = "/frontend";
    public static final String BACKEND_URL = "/backend";
    public static final String FRONTEND_LOGIN_URL = "/frontend/login/stuLogin";
    public static final String FRONTEND_BIND_URL = "/frontend/login/teacherLogin";
    public static final String CHECKFIRSTLOGIN_URL = "/frontend/login/checkFirstLogin";
    public static final String BACKEND_LOGIN = "/backend/login";
    public static final String UNIONID = "unionId";
    public static final String OPENID = "openId";
    public static final String UID = "UID";
    public static final String IDENT = "ident";
    public static final String EMAIL = "email";
    public static final String TPASSWORD = "tpassword";
    public static final String TIMESTAMP = "timestamp";
    public static final String USER_CACHE_NAME = "CLOUDSCHOOL:USER:";
    public static final String BACKEND_USER_SESSSION_KEY = "BACKENDUSER";
    public static final String REPORTFORMS_CACHE_KEY = "export_reportforms";
    public static final String COURSE_CACHE_KEY = "COURSE:COURSELIST:";
    public static final String SITEM_CACHE_KEY = "COURSE:SITEM:";
    public static final String INVES_CACHE_KEY = "INVESLIST:INVESLIST:";
    public static final String INVESINFO_CACHE_KEY = "INVESINFO:INVESINFO:";

    public static final String DEFAULT_COURSE_PICTURE = "https://yunxuetangpd.blob.core.chinacloudapi.cn/doc/defaultCourse.jpg";

    public static final String ADMIN_CACHE_NAME = "CLOUDSCHOOL:ADMIN:";
    public static final String SPRING_SESSION_NAME = "spring:session:sessions:";
    public static final String SPRING_SESSION_EXPIRES_NAME = "spring:session:sessions:expires:";
    public static final String SHIRO_CACHE_NAME = "shiro_redis_session:";

    /*******************添加一些redis key********************/
    public static final String COURSE_USER_INFO = "course:user:info:";// course:user:info:COURSEID:USERID
    public static final String COURSE_TEST_PAPER_USER_LIST = "course:test:paper:user:list:";// course:test:paper:user:list:COURSEID:USERID
    public static final String COURSE_TEST_PAPER_USER_INFO = "course:test:paper:user:info:";// course:test:paper:user:info:COURSEID:USERID:PAPERID
    public static final String COURSE_AGREE_NUM = "course:agree:num:";
    public static final String COURSE_APPLY_NUM = "course:apply:num:";
    public static final String STUDENT_LIST = "studentlist:studentlist:";
    public static final String STUDENT_TOTAL = "studenttotal:studenttotal:";
    public static final String STUDENT_COUNT = "studentcount:studentcount:";
    public static final String PERCENTAGE_EXCELTOTAL = "percentage:excelTotal:";
    public static final String PERCENTAGE_EXCELCOUNT = "percentage:excelCount:";
    public static final String QUEUE_AWARDCERT = "queue_awardcert";
    public static final String UPDATE_COURSE_CERT = "update_course_cert:course_cert:";
    public static final String UPDATE_COURSE_SIGN = "update_course_sign:course_sign:";

    public static final String UPDATE_CERT = "update_cert:cert:";
    public static final String UPDATE_CERT_TOTAL = "update_cert_total:certTotal:";
    public static final String UPDATE_CERT_COUNT = "update_cert_count:certCount:";
    public static final String SAFETYLOCK_STULOGIN = "C::STULOGIN:";
    public static final String SAFETYLOCK_STUCONFIRMSEX = "C::STUCONFIRMSEX:";
    public static final String SAFETYLOCK_AGREE = "C:AGREE:STR:";
    public static final String SAFETYLOCK_SITEMSUBMIT = "C:SITEMSUBMIT:STR:";
    public static final String SAFETYLOCK_SIGN = "C:SIGN:STR:";
    public static final String SAFETYLOCK_TESTSUBMIT = "C:TESTSUBMIT:STR:";
    public static final String SAFETYLOCK_INVESSUBMIT = "C:INVESSUBMIT:STR:";

    //拦截器不过滤的url
    public static final String excludePathPatterns_0 = "test/redisFlushdb";
    public static final String excludePathPatterns_1 = "test/getToken";
    public static final String excludePathPatterns_2 = "70qUwDTdi4";
    public static final String excludePathPatterns_3 = "0zM5RsNZhA";
    public static final String excludePathPatterns_4 = "swagger";
    public static final String excludePathPatterns_5 = "druid";
    public static final String excludePathPatterns_6 = "backend/index";

    public static final String INTERCEPTOR_AUTHORIZATION_01 = "Authorization header cannot be blank";
    public static final String INTERCEPTOR_AUTHORIZATION_02 = "Authorization header must start with 'Bearer '.";
    public static final String INTERCEPTOR_AUTHORIZATION_03 = "Invalid token";
    public static final String INTERCEPTOR_AUTHORIZATION_04 = "Token expired";
    public static final String ERROR_MESSAGE_AUTHORIZATION_06 = "lecturer email or password is error";

    public static final String INTERCEPTOR_CONTENTTYPE = "application/json; charset=utf-8";

    public static final String REDIRECT_LOGIN = "/backend/login";
    public static final int REDIS_EXPIRED_TIME = 5;
    public static final TimeUnit REDIS_EXPIRED_UNIT = TimeUnit.DAYS;

    /**管理员**/
    public static final short USER_IDENT_ADMIN = 0;
    /**学员**/
    public static final short USER_IDENT_STUDENT = 1;
    /**讲师**/
    public static final short USER_IDENT_LECTURER = 2;
    /**助教**/
    public static final short USER_IDENT_ASSISTANT = 3;

    /** DES加密key **/
    public static final String DES_KEY = "diax%oft@201Y!10";
    /** DES **/
    public static final String DES = "DES";

    /** 编码格式 **/
    public static final String UTF = "UTF-8";

    public static final String CLASS_ADD = "course/class-add";

    public static final String UPLOAD_URL = "C:\\fileupload";

    public static final String REPORT_FORMS = "报表自定义";

    public static final String CLASSCOUNT = "classCount";

    public static final String[] VALCODEARR = { "1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2" };
    public static final String[] WI = { "7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7", "9", "10", "5", "8", "4", "2" };
    /**异常信息**/
    public static class ErrorCode {
        private ErrorCode() {}
        public static final String SUCCESS_CODE = "00";
        public static final String PARAM_EXCEPTION = "33";
        public static final String SYSTEM_EXCEPTION = "55";
        public static final String ERROR_ACCOUNT_OR_PAS = "10001";
        public static final String COURSE_NOT_EXIST = "10002";
        public static final String USER_NOT_SIGN = "10003";
        public static final String USER_REGISTERED = "10004";
        public static final String USER_SIGNED = "10005";
        public static final String NOT_SUPPORT_ACTIVITY = "10006";
        public static final String ERROR_TIME_RANGE = "10007";
        public static final String FULL_APPLY_NUM = "10008";
        public static final String LABEL_NOT_EXIST = "10009";
        public static final String UPLOAD_FAIL = "10010";
        public static final String COURSE_NOT_OPERATE = "10011";
        public static final String DOC_NOT_EXIST = "10012";
        public static final String DATE_NOT_CORRECT = "10013";
        public static final String SOAMEMBERINFO_NULL = "10014";
        public static final String FAIL_SEND_MESSAGE = "10015";
        public static final String DUMPLICATE_TITLE = "10016";

        public static final String ENCRYPTEDDATA_ERROR = "11001";
        public static final String GET_ABOINFO_ERROR = "11002";
        public static final String GET_SESSIONKEY_ERROR = "11003";

        //文件上传异常返回码
        public static final String UPLOAD_URL_ERROR = "11004";
        public static final String UPLOAD_FILE_ERROR = "11005";
        public static final String UPLOAD_FILE_TYPE_ERROR = "11006";

        public static final String AUTHORIZATION_01 = "11000001";
        public static final String AUTHORIZATION_02 = "11000002";
        public static final String AUTHORIZATION_03 = "11000003";
        public static final String AUTHORIZATION_04 = "11000004";
        public static final String AUTHORIZATION_05 = "11000005";
        public static final String AUTHORIZATION_06 = "11000006";
        public static final String TEACHAR_NULL = "-1";

        public static final String CRET_TEMP_20000001 = "20000001";
    }

    public static class CourseSearchType{
        public static final String REGISTERED = "REGISTERED";
        public static final String PARTICIPATING = "PARTICIPATING";
        public static final String FINISHED = "FINISHED";
    }

    /**
     * 课程审核状态
     */
    public static class CourseApplyStatus{
        public static final String APPROVED = "approved";
        public static final String PENDING = "pending";
        public static final String FAILED = "failed";
        public static final String SIGN = "sign";
        public static final String FINISHED = "finished";
    }

    public static class CourseLabel{
        //必修课程
        public static final String COMPULSORY = "COMPULSORY";
        //线下课程
        public static final String OFFLINE = "OFFLINE";
        //直播课程
        public static final String LIVE = "LIVE";

        public static final String COMPULSORY_COURSE = "必修课程";
        public static final String OFFLINE_COURSE = "线下课程";
        public static final String LIVE_COURSE = "线上学习";
    }

    public static class ApprResult{
        //未通过
        public static final short NOT_APPROVED = 0;
        //已通过
        public static final short APPROVED = 1;
        //审核中
        public static final short PENDING = 2;
    }

    /**
     * 测试试卷问答状态
     */
    public static class PaperStatus{
        public static final Integer TODO = 0;
        public static final Integer DONE = 1;
    }

    /**
     * 查询测试结果状态，0未做过  1已做过
     */
    public static class PaperResultStatus{
        public static final Integer TODO = 0;
        public static final Integer DONE = 1;
    }

    /**
     * 测试、问卷题目类型 单选 1  多选 2  问答 3
     */
    public static class TestQuestType{
        public static final Integer REDIO = 1;
        public static final Integer CHECKBOX = 2;
        public static final Integer QA = 3;
    }

    public static class States{
        //无效
        public static final short NO_AVAI = 0;
        //有效
        public static final short VALID = 1;
    }

    public static class IsLegalperson{
        //是
        public static final String YES = "是";
        //否
        public static final String NO = "否";
    }

    public static class Sex{
        public static final String MAN = "男";

        public static final String WOMEN = "女";

        public static final String SPOUSE = "夫妻";
    }

    //公共数字
    public static class Number{
        public static final short SHORT_NUMBER0 = 0;
        public static final short SHORT_NUMBER1 = 1;
        public static final String STRING_NUMBER0 = "0";
        public static final String STRING_NUMBER1 = "1";
        public static final String STRING_NUMBER2 = "2";
        public static final int INT_NUMBER0 = 0;
        public static final int INT_NUMBER1 = 1;
        public static final int INT_NUMBER2 = 2;
        public static final int INT_NUMBER3 = 3;
        public static final int INT_NUMBER4 = 4;
        public static final Byte BYTE_NUMBER = 1;
        public static final Byte BYTE_NUMBER0 = 0;
    }

    public static class AdaNumber{
        public static final String ADANUMBER = "360";
    }
    public static final String WX_GRANT_TYPE = "authorization_code";

    public static class LabelType{
        //课程
        public static final String COURSE = "course";
    }

    public static class CourseStatus{
        public static final String REMOVED = "已下架";
        public static final String SHELF = "上架中";
    }
    public static class TestBank{
        public static final int PAGE_SIZE_DEFAULT = 5;
        public static final int PAGE_NO_DEFAULT = 1;
    }

    public static class InvesBank{
        public static final int PAGE_SIZE_DEFAULT = 5;
        public static final int PAGE_NO_DEFAULT = 1;
    }

    public static class TestOnline{
        public static final int DELETE = 0;
        public static final int ONSALE = 1;
        public static final int NOT_ONSALE = 2;

        public static final int PAGE_SIZE_DEFAULT = 5;
        public static final int PAGE_NO_DEFAULT = 1;
    }

    public static class InvesOnline{
        public static final int DELETE = 0;
        public static final int ONSALE = 1;
        public static final int NOT_ONSALE = 2;

        public static final int PAGE_SIZE_DEFAULT = 5;
        public static final int PAGE_NO_DEFAULT = 1;
    }

    public static class CourseShelve{
        public static final String PUBLISHED = "已发布";
        public static final String Unpublished = "未发布";
    }

    public static class ViaState{
        public static final String ADOPT = "已通过";
        public static final String NOT_THROUGH = "未通过";
    }

    public static class SignState{
        public static final String SUCCESS = "报名成功";
        public static final String AUDIT = "审核中";
        public static final String AUDIT_FAILURE = "审核失败";
        public static final String FINISH = "已完成";
    }

    public static class Award{
        public static final String AWARD = "已颁发";
        public static final String NOT_AWARD = "未颁发";
    }

    /**
     * 文件上传限定值
     */
    public static class FileUploadLimit{

        /**
         * 文件上传上限 10MB
         */
        public static final int TOP_LIMIT = 10 * 1024 * 1024;
        public static final String TOP_LIMIT_MSG = "上传的文件不得超过10MB！";

        public static final String IMG = "img";

        public static final String DOC = "doc";
    }

    public static class Redirect{
        public static final String CERT_SETTING = "cert-setting";
        public static final String CERT_SETTING_ADD = "cert-setting/add";
        public static final String CERT_TEMPLATES = "cert-templates";
        public static final String CERT_TEMPLATES_ADD = "cert-templates/add";
    }
}

