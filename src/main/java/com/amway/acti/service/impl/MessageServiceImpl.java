package com.amway.acti.service.impl;

import com.amway.acti.base.exception.AmwayParamException;
import com.amway.acti.base.util.Constants;
import com.amway.acti.dao.MessageMapper;
import com.amway.acti.model.Message;
import com.amway.acti.service.MessageService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.List;

@Service
@Slf4j
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Override
    public int getCountWithNotReadedByUserId(Integer userId) {
        log.info("getCountWithNotReadedByUserId userId:{}", userId);
        int result = messageMapper.selectCountWithNotReadedByUserId(userId);
        log.info("result:{}", result);
        return result;
    }

    @Override
    public PageInfo<Message> getByUserId(Integer userId, Integer pageNo, Integer pageSize) {
        log.info("getByUserId userId:{}, pageNo:{}, pageSize:{}", userId, pageNo, pageSize);
        PageHelper.startPage(pageNo, pageSize);
        List<Message> list = messageMapper.selectByUserId(userId);
        log.info("list:{}", list);
        return new PageInfo<>(list);
    }

    @Override
    public Message getById(Integer id) {
        log.info("getById id:{}", id);
        Message message = messageMapper.selectById(id);
        log.info("message:{}", message);
        return message;
    }

    @Override
    public void updateIsRead(Integer id){
        messageMapper.updateReadStateById(id);
    }

}
