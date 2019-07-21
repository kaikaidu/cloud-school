/**
 * Created by dk on 2018/2/23.
 */

package com.amway.acti.service;

import com.amway.acti.dto.MenuDto;
import com.amway.acti.dto.TeacherLoginDto;
import com.amway.acti.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public interface BackendUserService {

    /**
     * 获取讲师名单
     * @return
     */
    List<User> getTeachers();

    /**
     * 获取助教名单
     * @return
     */
    List<User> getAssists();

    /**
     * 检查讲师是否存在
     * @param teacherIds
     * @return
     */
    Boolean checkTeachersExist(List<String> teacherIds);

    /**
     * @Author Jie.Qiu
     * @param dto
     * @return
     */
    User checkByEmailAndPwd(TeacherLoginDto dto,HttpServletRequest request) throws GeneralSecurityException, IOException;

    /**
     * @Author Jie.Qiu
     * 查询一级菜单
     * @param user
     * @return
     */
    MenuDto findMenu(User user);

    /**
     * @Author Jie.Qiu
     * 查询二级菜单
     * @param user
     * @param code
     * @return
     */
    MenuDto findChildMenuByParentCodeAndUser(User user, String code);

    /**
     * 邮箱登陆
     * @param email
     * @return
     */
    User findByEmail(@NotNull(message = "邮箱不能为空") String email);
}
