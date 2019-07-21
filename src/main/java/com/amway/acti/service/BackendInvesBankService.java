package com.amway.acti.service;

import com.amway.acti.model.*;
import com.github.pagehelper.PageInfo;
import net.sf.json.JSONObject;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * @author Wei.Li1
 * @create 2018-03-13 16:16
 **/

@Validated
public interface BackendInvesBankService {

    /**
     * 根据用户查询对应的问卷模板
     * @param user
     * @param pageNo
     * @param pageSize
     * @param isDesc
     * @return
     */
    PageInfo<InvesTemp> findByUser(@NotNull(message = "用户不能为空") User user, String search, int pageNo, int pageSize, boolean isDesc);

    /**
     * 查询所有的问卷模板
     * @param pageNo
     * @param pageSize
     * @param isDesc
     * @return
     */
    PageInfo<InvesTemp> findAll(String search, int pageNo, int pageSize, boolean isDesc);

    /**
     * 判断用户是否有问卷模板
     * @param user
     * @return
     */
    boolean hasInvesTemp(@NotNull(message = "用户不能为空") User user);

    /**
     * 查询用户的问卷模板数
     * @param user
     * @param search
     * @return
     */
    int count(@NotNull(message = "用户不能为空") User user, String search);

    /**
     * 查询所有问卷模板数
     * @param search
     * @return
     */
    int countAll(String search);

    /**
     * 删除问卷模板
     * @param ids
     */
    List<Map<String, Object>> removeTemps(String ids);

    /**
     * 根据temp json字符串新增问卷模板（包含克隆）
     * @param user
     * @param data
     */
    void addTemp(@NotNull(message = "用户不能为空") User user, @NotNull(message = "数据不能为空") String data);

    /**
     *根据ID查询相应问卷模板
     * @param id
     * @return
     */
    InvesTemp findById(@NotNull(message = "模板ID不能为空") int id);

    /**
     * 根据模板ID查询相应的问卷试题
     * @param tempId
     * @returne
     */
    List<InvesQuest> findByTempId(@NotNull(message = "模板ID不能为空") int tempId);

    /**
     * 根据temp json字符串编辑问卷模板
     * @param user
     * @param data
     */
    void editTemp(@NotNull(message = "用户不能为空") User user, @NotNull(message = "数据不能为空") String data);

    /**
     * 根据模板id查询详情，如，课程名，问卷名
     * @param id
     * @return
     */
    List<Map<String, Object>> findInvesDeatil(@NotNull(message = "问卷模板ID不能为空") Integer id);

    /**
     * 根据模板id查询详情
     * @param tempId
     * @return
     */
    JSONObject findTempInfoById(@NotNull(message = "问卷模板ID不能为空") Integer tempId);
}
