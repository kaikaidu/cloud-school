package com.amway.acti.transform;

import com.amway.acti.dto.PageDto;
import com.amway.acti.dto.v2.fronted.MessageDetailDto;
import com.amway.acti.dto.v2.fronted.MessageDto;
import com.amway.acti.model.Message;
import com.github.pagehelper.PageInfo;

public interface MessageTransform {

    PageDto<MessageDto> transformInfoToMessageDto(PageInfo<Message> info);

    MessageDetailDto transformModelToMessageDetailDto(Message message);
}
