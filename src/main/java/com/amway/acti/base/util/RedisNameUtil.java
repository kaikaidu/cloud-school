package com.amway.acti.base.util;

import java.util.List;

/**
 * @author Wei.Li1
 * @create 2018-05-22 15:39
 **/
public class RedisNameUtil {

    private RedisNameUtil(){}

    private static String getName(String type, int... ids){
        StringBuffer sb = new StringBuffer( type );
        for(int id : ids){
            sb.append( id ).append( ":" );
        }
        return sb.substring( 0, sb.length() - 1 );
    }

    public static String[] getCourseInfoName(List<Integer> courseIds, int userId){
        String[] names = new String[courseIds.size()];
        for(int i = 0; i<courseIds.size(); i++){
            names[i] = getName( Constants.COURSE_USER_INFO , courseIds.get( i ), userId );
        }
        return names;
    }

    public static String getTestPaperListName(int courseId, int userId){
        return getName( Constants.COURSE_TEST_PAPER_USER_LIST, courseId, userId );
    }

    public static String getTestPaperInfoName(int courseId, int userId, int paperId){
        return getName( Constants.COURSE_TEST_PAPER_USER_INFO, courseId, userId, paperId );
    }
}
