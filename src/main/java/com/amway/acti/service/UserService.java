/**
 * Created by DK on 2018/2/11.
 */

package com.amway.acti.service;

import com.amway.acti.dto.FrontendTearchLoginDto;
import com.amway.acti.dto.LoginDto;
import com.amway.acti.dto.StuloginResponseDto;
import com.amway.acti.model.OsbAboInfo;
import com.amway.acti.model.User;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Validated
public interface UserService {

     void test(String adaNumber);
    OsbAboInfo getObsAboInfo(String adaNumber);

    User getUser(Integer uid);

    User checkFirstLogin(@NotBlank(message = "token不能为空") String token);

    String teacherLogin(@Valid FrontendTearchLoginDto dto);

    User selectByPrimary(@NotNull(message = "用户id不能为空") Integer uid);

    User savePhone(String phone,Integer uid);

    User saveAddr(String province,
                  String city,
                  String region,
                  @Length(max = 25, message = "地址输入超过25个字符") String delAddress,
                  Integer uid);

    StuloginResponseDto stuLogin(@Valid LoginDto dto);


    /**
     * 选择性别
     * @param tempToken
     * @param sex
     */
    StuloginResponseDto confirmSex(@NotNull(message = "token不能为空") String tempToken ,@NotBlank(message = "性别不能为空") String sex);

    String selectLecturerByClassId(@NotNull(message = "班级id不能为空") Integer classId);

    User saveRemark(@NotNull(message = "用户id不能为空") Integer uid,String remark);
    ////////////////李伟新增////////////////
    User findWithApproed(@NotNull(message = "用户ID不能为空") Integer userId,
                         @NotNull(message = "课程ID不能为空") Integer courseId);

    User findWithNotApproed(@NotNull(message = "用户ID不能为空") Integer userId,
                         @NotNull(message = "课程ID不能为空") Integer courseId);

    String flushDB(String name);

    User getUserByEmailAndPwd(String email,String password);

    String getAdainfoMd5(String adaNumber, Short sex);

    String completion(String adaNumber ,Short sex);

    //String getToken(AccessJwtTokenRequest accessJwtTokenRequest);

}
