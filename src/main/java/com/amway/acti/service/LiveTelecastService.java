package com.amway.acti.service;

import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.model.CourseUser;
import com.amway.acti.model.User;
import com.github.pagehelper.PageInfo;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @Description:直播课程
 * @Date:2018/3/15 10:38
 * @Author:wsc
 */
public interface LiveTelecastService {
    PageInfo<User> selectLiveTeleacstByCourseId(Integer pageNo, Integer pageSize, CourseUser courseUser);

    int selectLiveTelecastCount(CourseUser courseUser);

    CommonResponseDto updateState(Integer siState, Integer viState, String ids, Integer courseId);

    List<String> sendTemplateMessage(@NotNull(message = "课程不能为空") Integer courseId, @NotNull(message = "学员不能为空")@Size(min = 1,message = "学员不能为空") Integer[] uids);
    CommonResponseDto batchDel(String ids, Integer courseId);
}
