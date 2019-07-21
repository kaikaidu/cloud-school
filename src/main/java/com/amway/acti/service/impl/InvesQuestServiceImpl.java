package com.amway.acti.service.impl;

import com.amway.acti.dao.InvesQuestMapper;
import com.amway.acti.model.InvesQuest;
import com.amway.acti.service.InvesQuestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description:题目
 * @Date:2018/3/8 17:06
 * @Author:wsc
 */
@Service
public class InvesQuestServiceImpl implements InvesQuestService {
    @Autowired
    private InvesQuestMapper invesQuestMapper;

    /**
     * @Description:获取题目列表
     * @Date:2018/3/8 17:09
     * @param tempId 问卷模板id
     * @Author:wsc
     */
    @Override
    public List<InvesQuest> selectQuestByInvesId(Integer tempId) {
        return invesQuestMapper.selectInvesQuestByInvesId(tempId);
    }
}
