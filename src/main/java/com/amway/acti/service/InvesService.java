package com.amway.acti.service;

import com.amway.acti.dto.InvesDto;
import com.amway.acti.model.InvesOption;
import com.amway.acti.model.InvesPaper;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Description:问卷列表查询接口
 * @Date:2018/3/8 9:45
 * @Author:wsc
 */
@Validated
public interface InvesService {

    /**
     * @Description:问卷列表查询
     * @Date:2018/3/8 14:53
     * @param courseId 课程id
     * @param userId 用户id
     * @Author:wsc
     */
    List<InvesDto> selectInvesByCourseId(@NotNull(message = "课程ID不能为空")Integer courseId, @NotNull(message = "用户ID不能为空")Integer userId);

    /**
     * @Description:问卷详情查询
     * @Date:2018/3/8 11:37
     * @param courseId 课程ID
     * @param paperId 问卷ID
     * @Author:wsc
     */
    InvesPaper selectInvesResult(@NotNull(message = "课程ID不能为空")Integer courseId, @NotNull(message = "问卷ID不能为空")Integer paperId);

    /**
     * 根据问卷ID查询所有的选项
     * @param questId
     * @return
     */
    List<InvesOption> getOptionList(@NotNull(message = "问卷ID不能为空") Integer questId);

    List<InvesPaper> getPapersListByTempId(@NotNull(message = "问卷模板ID不能为空") Integer tempId);

}
