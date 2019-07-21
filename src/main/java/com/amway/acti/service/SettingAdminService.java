package com.amway.acti.service;

import com.amway.acti.dto.UserAdminDto;
import com.amway.acti.model.User;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface SettingAdminService {

    int selectAdminUserList();

    List<UserAdminDto> selectUserForAdmin (String name,String ident,
                                           @RequestParam(value = "pageNo") Integer pageNo,
                                           @RequestParam(value = "pageSize") Integer pageSize) throws Exception;

    String queryCount(String name,String ident);

    User selectEditAdminData(@NotNull(message = "用户id不能为空") String uid) throws Exception;

    void editOrAddAdmin(User user) throws Exception;

    void delBatch(String uids);
}
