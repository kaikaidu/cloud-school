package com.amway.acti.service;

import com.amway.acti.model.TestPaper;
import com.amway.acti.model.User;
import com.amway.acti.model.UserMark;
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
public interface BackendTestOnlineService {

    /**
     * 根据用户查询对应的测试卷
     * @param user
     * @param courseId
     * @param search
     * @param state
     * @param pageNo
     * @param pageSize
     * @return
     */
    PageInfo<TestPaper> findByUser(@NotNull(message = "用户不能为空")User user,
                                   @NotNull(message = "课程ID不能为空") Integer courseId,
                                   String search, int state, int pageNo, int pageSize);

    /**
     * 判断用户是否有测试卷
     * @param user
     * @return
     */
    boolean hasTestPaper(@NotNull(message = "用户不能为空")User user,
                         @NotNull(message = "课程ID不能为空") Integer courseId);

    /**
     * 查询用户的测试卷数量
     * @param user
     * @param courseId
     * @param search
     * @param state
     * @return
     */
    int count(@NotNull(message = "用户不能为空") User user,
              @NotNull(message = "课程ID不能为空") Integer courseId,
              String search, int state);

    /**
     * 删除测试卷
     * @param id
     * @param state
     * @param courseId
     */
    void removePaper(@NotNull(message = "测试卷ID不能为空") Integer id, int state, int courseId);

    /**
     * 批量删除测试卷
     * @param ids
     */
    void removePapers(List<Integer> ids);

    /**
     * 根据课程ids删除下面的测试卷
     * @param courseIds
     */
    void removePapersByCourseIds(List<Integer> courseIds);

    /**
     * 新增测试卷
     * @param user
     * @param testPaper
     */
    void addPaper(@NotNull(message = "用户不能为空") User user, @NotNull(message = "测试卷不能为空") TestPaper testPaper);

    /**
     *根据ID查询相应测试卷
     * @param id
     * @return
     */
    TestPaper findById(@NotNull(message = "测试卷ID不能为空") Integer id);

    /**
     * 编辑测试卷
     * @param user
     * @param testPaper
     */
    void editPaper(@NotNull(message = "用户不能为空") User user, @NotNull(message = "测试卷不能为空") TestPaper testPaper);

    /**
     * 上下架
     * @param ids
     * @param state
     */
    void onsale(String ids, @NotNull(message = "状态不能为空") Integer state, @NotNull(message = "课程ID不能为空") Integer courseId);

    /**
     * 根据courseId获取试卷列表
     * @param courseId
     * @return
     */
    List<TestPaper> findOnByCourseId(@NotNull(message = "课程ID不能为空") Integer courseId);

    /**
     * 根据课程ID查询有效用户
     * @param courseId
     * @return
     */
    List<User> findUsersByCourseId(@NotNull(message = "课程ID不能为空") Integer courseId);

    /**
     * 根据userId和paperId获取学员分数
     * @param userId
     * @param paperId
     * @return
     */
    UserMark findUserMarkByUserIdAndPaperId(@NotNull(message = "学员ID不能为空") Integer userId,
                                            @NotNull(message = "试卷ID不能为空") Integer paperId);

    /**
     * 根据条件获取list
     * @param info
     * @return
     */
    List<Map<String, Object>> getScoreResultByInfo(Map<String, Object> info, int pageNo, int pageSize);

    /**
     * 获取score总量
     * @param info
     * @return
     */
    int countScoreResultByInfo(Map<String, Object> info);

    /**
     * 获取所有数据
     * @param courseId
     * @return
     */
    List<Map<String, Object>> getScoreResult(int courseId);

    /**
     * 获取试卷详情和做题情况
     * @param userId
     * @param paperId
     * @return
     */
    JSONObject getScoreInfo(int userId, int paperId);

    /**
     * 判断该测试卷能否被编辑
     * @param paperId
     * @return
     */
    boolean hasResult(int paperId);

}
