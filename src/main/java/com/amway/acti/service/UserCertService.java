package com.amway.acti.service;

import com.amway.acti.model.UserCert;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Validated
public interface UserCertService {

    /**
     * 获取学员证书列表
     * @param userId
     * @param pageNo
     * @param pageSize
     * @return
     */
    PageInfo<UserCert> getByUserId(@NotNull(message = "学员ID不能为空") Integer userId,
                                   @NotNull(message = "pageNo不能为空") @Min(value = 1, message = "pageNo必须大于0") Integer pageNo,
                                   @NotNull(message = "pageSize不能为空") @Min(value = 1, message = "pageSize必须大于0") Integer pageSize);

    /**
     * 获取学员证书详情
     * @param id
     * @return
     */
    UserCert getById(@NotNull(message = "学员证书ID不能为空") Integer id);

    /**
     * 获取学员证书详情
     * @param courseId
     * @param userId
     * @return
     */
    UserCert getByCourseIdAndUserId(@NotNull(message = "课程ID不能为空") @Min(value = 1, message = "课程ID必须大于0") Integer courseId,
                                    @NotNull(message = "学员ID不能为空") Integer userId);

    /**
     * 更新已读状态
     * @param id
     */
    void updateIsRead(@NotNull(message = "学员证书ID不能为空") Integer id);

}
