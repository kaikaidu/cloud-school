package com.amway.acti.transform.impl;

import com.amway.acti.dto.PageDto;
import com.amway.acti.dto.v2.fronted.MessageDetailDto;
import com.amway.acti.dto.v2.fronted.MessageDto;
import com.amway.acti.model.Message;
import com.amway.acti.transform.MessageTransform;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class MessageTransformImpl implements MessageTransform {

    @Override
    public PageDto<MessageDto> transformInfoToMessageDto(PageInfo<Message> info) {
        PageDto<MessageDto> pageDto = new PageDto<>();
        pageDto.setTotalPages(info.getPages());
        pageDto.setCurrentPage(info.getPageNum());
        List<MessageDto> list = new ArrayList<>();
        for (Message message : info.getList()) {
            MessageDto messageDto = new MessageDto();
            messageDto.setId(message.getId());
            messageDto.setTitle(message.getTitle());
            messageDto.setIsRead(message.getIsRead());
            list.add( messageDto );
        }
        pageDto.setDataList( list );
        return pageDto;
    }

    @Override
    public MessageDetailDto transformModelToMessageDetailDto(Message message) {
        MessageDetailDto messageDetailDto = new MessageDetailDto();
        messageDetailDto.setContent(message.getContent());
        messageDetailDto.setName(message.getTitle());
        messageDetailDto.setType(message.getType());
        return messageDetailDto;
    }
}
