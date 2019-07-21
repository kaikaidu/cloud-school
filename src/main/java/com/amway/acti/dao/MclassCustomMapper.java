package com.amway.acti.dao;

import com.amway.acti.model.MclassCustom;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description:班级扩展
 * @Date:2018/4/29 17:18
 * @Author:wsc
 */
public interface MclassCustomMapper {
    List<MclassCustom> selectByCourseId(@Param("courseId") Integer courseId);
}
