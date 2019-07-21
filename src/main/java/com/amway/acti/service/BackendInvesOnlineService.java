package com.amway.acti.service;

import com.amway.acti.model.InvesPaper;
import com.amway.acti.model.User;
import com.github.pagehelper.PageInfo;
import net.sf.json.JSONObject;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * @author Wei.Li1
 * @create 2018-03-16 13:56
 **/

@Validated
public interface BackendInvesOnlineService {

    /**
     * 根据用户查询对应的问卷
     * @param user
     * @param courseId
     * @param search
     * @param state
     * @param pageNo
     * @param pageSize
     * @return
     */
    PageInfo<InvesPaper> findByUser(@NotNull(message = "用户不能为空") User user, @NotNull(message = "课程ID不能为空") Integer courseId, String search, int state, int pageNo, int pageSize);

    /**
     * 判断用户是否有问卷
     * @param user
     * @return
     */
    boolean hasInvesPaper(@NotNull(message = "用户不能为空") User user, @NotNull(message = "课程ID不能为空") Integer courseId);

    /**
     * 查询用户的问卷数量
     * @param user
     * @param courseId
     * @param search
     * @param state
     * @return
     */
    int count(@NotNull(message = "用户不能为空") User user, @NotNull(message = "课程ID不能为空") Integer courseId, String search, int state);

    /**
     * 删除问卷
     * @param id
     * @param state
     * @param courseId
     */
    void removePaper(@NotNull(message = "问卷ID不能为空") Integer id, int state, int courseId);

    /**
     * 批量删除问卷
     * @param ids
     */
    void removePapers(List<Integer> ids);

    /**
     * 根据课程ids删除相应问卷
     * @param courseIds
     */
    void removePapersByCourseIds(List<Integer> courseIds);

    /**
     * 新增问卷
     * @param user
     * @param invesPaper
     */
    void addPaper(@NotNull(message = "用户不能为空") User user, @NotNull(message = "问卷不能为空") InvesPaper invesPaper);

    /**
     *根据ID查询相应问卷
     * @param id
     * @return
     */
    InvesPaper findById(@NotNull(message = "问卷ID不能为空") Integer id);

    /**
     * 编辑问卷
     * @param user
     * @param invesPaper
     */
    void editPaper(@NotNull(message = "用户不能为空") User user, @NotNull(message = "问卷不能为空") InvesPaper invesPaper);

    /**
     * 上下架
     * @param ids
     * @param state
     */
    void onsale(String ids, @NotNull(message = "状态不能为空") Integer state, @NotNull(message = "课程ID不能为空") Integer courseId);

    /**
     * 根据courseId获取所有问卷试卷
     * @param courseId
     * @return
     */
    List<InvesPaper> findOnByCourseId(Integer courseId);

    /**
     * 根据课程ID查询有效用户
     * @param courseId
     * @return
     */
    List<User> findUsersByCourseId(@NotNull(message = "课程ID不能为空") Integer courseId);

    /**
     * 根据条件获取list
     * @param info
     * @return
     */
    List<Map<String, Object>> getResultByInfo(Map<String, Object> info, int pageNo, int pageSize);

    /**
     * 获取score总量
     * @param info
     * @return
     */
    int countResultByInfo(Map<String, Object> info);

    /**
     * 获取所有数据
     * @param courseId
     * @return
     */
    List<Map<String, Object>> getResult(int courseId);

    /**
     * 根据tempId和paperId构建预览数据
     * @param userId
     * @param paperId
     * @return
     */
    JSONObject buildViewData(int userId, int paperId);

    /**
     * 判断该问卷能否被编辑
     * @param paperId
     * @return
     */
    boolean hasResult(int paperId);
}
