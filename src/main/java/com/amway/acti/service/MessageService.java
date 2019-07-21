package com.amway.acti.service;

import com.amway.acti.model.Message;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Validated
public interface MessageService {

    /**
     * 获取个人中心未读消息个数
     * @param userId
     * @return
     */
    int getCountWithNotReadedByUserId(@NotNull(message = "学员ID不能为空") Integer userId);

    /**
     * 获取消息列表
     * @param userId
     * @param pageNo
     * @param pageSize
     * @return
     */
    PageInfo<Message> getByUserId(@NotNull(message = "学员ID不能为空") Integer userId,
                                  @NotNull(message = "pageNo不能为空") @Min(value = 1, message = "pageNo必须大于0") Integer pageNo,
                                  @NotNull(message = "pageSize不能为空") @Min(value = 1, message = "pageSize必须大于0") Integer pageSize);

    /**
     * 获取消息详情
     * @param id
     * @return
     */
    Message getById(@NotNull(message = "消息ID不能为空") Integer id);

    /**
     * 更新已读状态
     * @param id
     */
    void updateIsRead(@NotNull(message = "消息ID不能为空") Integer id);

}
