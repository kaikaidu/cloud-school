package com.amway.acti.dao;

import com.amway.acti.model.CourseUser;
import com.amway.acti.model.RealityUser;
import com.amway.acti.model.User;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;


public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    User selectByEmailAndPassword(@Param("email") String email, @Param("password") String password);

    User selectByAdaEncode(@Param("adaInfoEncode") String adaInfoEncode);

    List<User> selectAllTeachers();

    List<User> selectAllAssists();

    Integer countTeacherByList(@Param("teacherIds") List<String> teacherIds);

    List<User> selectByOpenIdAndUnionId(@Param("openId") String openId, @Param("unionId") String unionId);

    List<User> selectUserAndSignupByCourseId(CourseUser courseUser);

    int selectUserSignCount(CourseUser courseUser);

    void updateSignState(@Param("state") Integer viState, @Param("id") String id);

    List<RealityUser> selectExportUser(CourseUser courseUser);

    List<User> selectApprovalByCourseId(CourseUser courseUser);

    int selectLiveTelecastCount(CourseUser courseUser);

    String selectLecturerByClassId(Integer classId);

    ////////////////李伟新增//////////////////

    /**
     * 课程需要审核，且审核通过的
     *
     * @param userId
     * @param courseId
     * @return
     */
    User selectWithApproed(@Param("userId") int userId, @Param("courseId") int courseId);

    /**
     * 课程不需要审核的
     *
     * @param userId
     * @param courseId
     * @return
     */
    User selectWithNotApproed(@Param("userId") int userId, @Param("courseId") int courseId);

    List<User> selectUserByCourseIdAndApproval(Integer courseId);

    List<User> selectUserByCourseId(Integer courseId);

    List<User> selectLecturerCondition(@Param("id") Integer id, @Param("courseId") Integer courseId, @Param("name") String name);

    int selectUserCount(@Param("name") String name, @Param("ident") short ident);

    List<User> selectUserByNameAndIdent(@Param("name") String name, @Param("ident") short ident);

    List<User> selectUserForAdmin(@Param(value = "name") String name, @Param(value = "ident") String ident);

    /////////////李伟新增///////////////

    /**
     * 课程需要审核，且审核通过的所有学员
     *
     * @param courseId
     * @return
     */
    List<User> selectUsersWithApproed(int courseId);

    /**
     * 课程不需要审核的所有学员
     *
     * @param courseId
     * @return
     */
    List<User> selectUsersWithNotApproed(int courseId);


    User selectByEmail(User user);

    List<User> selectAdminUserList();

    int getStudentCount(@Param("name") String name);

    List<RealityUser> selectExportLiveUser(CourseUser courseUser);

    List<User> getTeacherByEmail(@Param("email") String email);

    User checkByEmail(@Param("email") String email);

    void updateUserByList(@Param("list") List<User> updateUserList);

    Integer countCourses(@Param("userId") int userId,@Param("currentTime") Date currentTime);

    List<User> selectUsersInIds(@Param("list") List<? extends Object> list);

    List<User> findTeacList(@Param("name") String name);

    List<String> selectTeacIdByCourseIdAndClassId(@Param("mClassId") Integer mClassId, @Param("courseId") Integer courseId);

    List<User> selectByUserList(@Param("list") List<String> strings,@Param("currentTime") Date currentTime);

    User selectUserByNameAndSexAndAdaNum(@Param("name") String name, @Param("sex") Short sex, @Param("adaNumber") String adaNumber,@Param("courseId") Integer courseId);

    List<User> selectSignSuccUserByCourseId(@Param("courseId") Integer courseId);

    List<User> selectUserByCourseIdAndApprResult(@Param("courseId") Integer courseId);

    List<User> selectdkkkk01(@Param("ada") String ada);


}
