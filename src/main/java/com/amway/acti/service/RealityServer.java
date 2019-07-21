package com.amway.acti.service;

import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.model.CourseUser;
import com.amway.acti.model.User;
import com.github.pagehelper.PageInfo;
import org.apache.poi.ss.usermodel.Cell;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.text.ParseException;

/**
 * @Description:导入学员
 * @Date:2018/3/12 9:53
 * @Author:wsc
 */
@Validated
public interface RealityServer {
    CommonResponseDto checkInsertUser(@NotNull(message = "课程id不能为空") Integer courseId,
                                      String state,Integer dataLength,User user,MultipartFile file)  throws ParseException, IOException;

    PageInfo <User> selectUserAndSignupByCourseId(@NotNull(message = "当前页不能为空") Integer pageNo,
                                                  @NotNull(message = "每页行数不能为空") Integer pageSize,
                                                  CourseUser courseUser);

    int selectUserSignCount(CourseUser courseUser);

    CommonResponseDto updateSignState(@NotBlank(message = "请选择要修改的学员") String ids,
                                      @NotNull(message = "请选择要修改的状态") Integer state);

    void exportUser(HttpServletResponse response, HttpServletRequest request, CourseUser courseUser) throws Exception;

    CommonResponseDto getLabelByCourseId(Integer courseId);

    CommonResponseDto getCompletedProgress(Integer courseId,Integer userId);

    void updateRedisValue(Integer userId,Integer courseId,int applyNum,int agreeNum,String symbol,String applyStatus);

    CommonResponseDto batchDel(@NotBlank(message = "请选择要删除的学员") String ids, @NotNull(message = "课程ID不能为空") Integer courseId);

    void checkSex(Cell cell, User user);

    void checkName(Cell cell, User user);

    void checkAdaNumber(Cell cell, User user);

    CommonResponseDto getSuccessCount(Integer courseId, Integer userId);

    CommonResponseDto deleteClassAndDraw(String ids, Integer courseId, Integer type);

    CommonResponseDto checkClass(Integer courseId);
}
