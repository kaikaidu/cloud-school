/**
 * Created by dk on 2018/3/2.
 */

package com.amway.acti.service;

import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.dto.DrawResultDto;
import com.amway.acti.model.DrawCuts;
import com.amway.acti.model.Teacher;
import com.amway.acti.model.*;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;

@Validated
public interface DrawService {
    DrawResultDto drawResult(Integer uid, @NotNull(message = "课程ID不能为空") Integer courseId);

    List<Teacher> findDrawByCourseId(@NotNull(message = "课程id不能为空") Integer courseId);

    PageInfo<DrawCuts> findDrawUser(Integer pageNo, Integer pageSize,@NotNull(message = "课程id不能为空") Integer courseId);

    CommonResponseDto getDrawCount(String name, Integer courseId);

    int selectSignupCount(@NotNull(message = "课程ID不能为空") Integer courseId);

    CommonResponseDto addClass(Integer userCount, Integer classCount,@NotNull(message = "课程ID不能为空") Integer courseId) throws Exception;

    CommonResponseDto changeClass(Integer classId,Integer userId,Integer courseId,Integer initClassId);

    CommonResponseDto findClassUserInfo(Integer userId,Integer courseId);

    CommonResponseDto selectSpeedAndDrawList(Integer courseId);

    CommonResponseDto drawImmediately(Integer courseId);

    List<Mclass> selectMclassByCourseId(Integer courseId);

    CommonResponseDto findTeacList(String type, Integer mClassId, Integer courseId, String name);

    void editOrAddTeac(String data);

    List<ClassDraw> viewOrAddSub(Integer mClassId);

    void editOrAddSub(String data);

    List<DrawResultData> selectDrawResult(Integer pageNo,Integer pageSize, Integer courseId);

    List<DrawResultData> selectDrawResultCount(Integer courseId);

    ClassDraw selectSubDetail(Integer classDrawId);

    void editIsBallot(Integer courseId, Integer isBallot);

    boolean isSpeekAndDraw(Integer courseId);

    void reEdit(Integer courseId);

    List<DrawResultData> selectDrawResultByCoursePreview(Integer pageNo,Integer pageSize,Integer courseId);

    Integer selectDrawResultCountByCoursePreview(Integer courseId);
}
