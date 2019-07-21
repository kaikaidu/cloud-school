package com.amway.acti.dao;

import com.amway.acti.model.Message;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MessageMapper {

    int selectCountWithNotReadedByUserId(int userId);

    List<Message> selectByUserId(int userId);

    Message selectById(int id);

    int updateReadStateById(int id);

    //////////////////上面是liwei小程序接口//////////////////////

    int deleteByPrimaryKey(Integer id);

    int insert(Message record);

    int insertSelective(Message record);

    Message selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Message record);

    int updateByPrimaryKey(Message record);
    //---------------- 下面是平台接口------------------------
    void insertList(@Param("list") List<Message> messageList);

    void deleteMessageByCourseIdAndUserId(@Param("courseId") Integer courseId, @Param("userId") Integer userId,@Param("type") Integer type);

    List<Message> selectMessageByUserIdAndCourseId(@Param("userId") Integer id, @Param("courseId") Integer sysId);

    void updateList(@Param("list") List<Message> messageList);

    List<Message> selectMessageById(@Param("courseId") Integer courseId);
}

