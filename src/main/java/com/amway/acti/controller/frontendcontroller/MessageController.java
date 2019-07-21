package com.amway.acti.controller.frontendcontroller;

import com.amway.acti.base.context.MiniProgramRequestContextHolder;
import com.amway.acti.base.exception.AmwayParamException;
import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.model.Message;
import com.amway.acti.service.MessageService;
import com.amway.acti.transform.MessageTransform;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/frontend/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private MessageTransform messageTransform;

    /**
     * 获取个人中心未读消息个数
     * @return
     */
    @GetMapping("getNewMessages")
    public CommonResponseDto getNewMessages(){
        return CommonResponseDto.ofSuccess(messageService.getCountWithNotReadedByUserId(MiniProgramRequestContextHolder.getRequestUser().getUid()));
    }

    /**
     * 获取消息列表
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping("getMessageList")
    public CommonResponseDto getMessageList(Integer pageNo, Integer pageSize){
        int userId = MiniProgramRequestContextHolder.getRequestUser().getUid();
        PageInfo<Message> info = messageService.getByUserId(userId, pageNo, pageSize);
        return CommonResponseDto.ofSuccess(messageTransform.transformInfoToMessageDto(info));
    }

    /**
     * 获取消息详情
     * @param messageId
     * @return
     */
    @GetMapping("getMessageDetail")
    public CommonResponseDto getMessageDetail(Integer messageId){
        Message message = messageService.getById(messageId);
        if(message == null){
            throw new AmwayParamException("messageId不存在");
        }
        if(message.getIsRead() == 0){
            messageService.updateIsRead(message.getId());
        }
        return CommonResponseDto.ofSuccess(messageTransform.transformModelToMessageDetailDto(message));
    }
}
