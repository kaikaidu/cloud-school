package com.amway.acti.service;

import com.amway.acti.model.InvesQuest;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Description:问卷题目
 * @Date:2018/3/21 10:47
 * @Author:wsc
 */
@Validated
public interface InvesQuestService {
    List<InvesQuest> selectQuestByInvesId(@NotNull(message = "模板id不能为空") Integer tempId);
}
