package com.amway.acti.service;

import com.amway.acti.model.TestQuest;
import com.amway.acti.model.TestTemp;
import com.amway.acti.model.User;
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
public interface BackendTestBankService {

    /**
     * 根据用户查询对应的试卷模板
     * @param pageNo
     * @param pageSize
     * @param isDesc
     * @return
     */
    PageInfo<TestTemp> findByUser(@NotNull(message = "用户不能为空") User user, String search, int pageNo, int pageSize, boolean isDesc);

    /**
     * 查询所有的测试卷模板
     * @param pageNo
     * @param pageSize
     * @param isDesc
     * @return
     */
    PageInfo<TestTemp> findAll(String search, int pageNo, int pageSize, boolean isDesc);

    /**
     * 判断用户是否有试卷模板
     * @return
     */
    boolean hasTestTemp(@NotNull(message = "用户不能为空") User user);

    /**
     * 查询用户的试卷模板数
     * @param search
     * @return
     */
    int count(@NotNull(message = "用户不能为空") User user, String search);

    /**
     * 查询所有测试卷模板数
     * @param search
     * @return
     */
    int countAll(String search);

    /**
     * 删除试卷模板
     * @param ids
     */
    List<Map<String, Object>> removeTemps(String ids);

    /**
     * 根据temp json字符串新增试卷模板（包含克隆）
     * @param data
     */
    void addTemp(@NotNull(message = "用户不能为空") User user, @NotNull(message = "数据不能为空") String data);

    /**
     *根据ID查询相应模板
     * @param id
     * @return
     */
    TestTemp findById(@NotNull(message = "模板ID不能为空") int id);

    /**
     * 根据id查询模板的所有信息，包括试题和选项
     * @param id
     * @return
     */
    JSONObject findTempInfoById(@NotNull(message = "模板ID不能为空") int id);

    /**
     * 根据模板ID查询相应的试题
     * @param tempId
     * @return
     */
    List<TestQuest> findByTempId(@NotNull(message = "模板ID不能为空") int tempId);

    /**
     * 根据temp json字符串编辑试卷模板
     * @param data
     */
    void editTemp(@NotNull(message = "用户不能为空") User user, @NotNull(message = "数据不能为空") String data);

    /**
     * 根据模板id查询详情，如，课程名，试卷名
     * @param id
     * @return
     */
    List<Map<String, Object>> findTestDeatil(@NotNull(message = "试卷模板ID不能为空") Integer id);

}
