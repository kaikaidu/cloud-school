/**
 * Created by Will Zhang on 2018/2/23.
 */

package com.amway.acti.service;

import com.amway.acti.base.util.Constants.CourseSearchType;
import com.amway.acti.base.validation.constraints.StringRange;
import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.dto.CourseListSearchDto;
import com.amway.acti.model.Course;
import com.amway.acti.model.User;
import com.github.pagehelper.PageInfo;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Validated
public interface CourseService
{
  PageInfo<Course> selectByTypeAndUser(Integer userId,String adaInfoMd5,Integer pageNo,Integer pageSize,@NotBlank(message = "取值类型为必填项")@StringRange(range = {
      CourseSearchType.REGISTERED,CourseSearchType.PARTICIPATING,CourseSearchType.FINISHED},message = "类型取值错误") String type);

  /**
   * 检查学员与课程之间的关系
   * @param userId
   * @param courseId
   * @return
   */
  Boolean checkUserAndCourse(@NotNull(message = "学员ID不能为空") Integer userId, @NotNull(message = "课程ID不能为空") Integer courseId);

  PageInfo<Course> selectByTeacher(int userId,Integer pageNo,Integer pageSize);

  /**
   * 课程详情查询
   * @param courseId
   * @return
   */
  Course selectDetailCourse(@NotNull(message = "课程id不能为空") Integer courseId,Integer userId,Short ident);

  /**
   * 新增课程，返回课程id
   * @param course
   * @return
   */
  Integer insertOrUpdateCourse(Course course,String[] teacherStr,String[] assists,Integer courseId);

  /**
   * 查询课程列表
   * @return
   */
  PageInfo<Course> selectCourseList(User user,CourseListSearchDto dto);

  Long countCourse(User user,CourseListSearchDto dto);

  /**
   * 课程上下架
   * @param user
   * @param courseIds
   * @param shelve  true:上架；false:下架
   */
  void shelveCourse(User user, @NotNull(message = "课程不能为空") @Size(min = 1,message = "课程不能为空") List<Integer> courseIds, Boolean shelve);

  Boolean checkUserAndCourses(@NotNull(message = "课程不能为空") @Size(min = 1,message = "课程不能为空") List<Integer> courseIds
          ,@NotNull(message = "userId不能为空") Integer userId,@NotNull(message = "用户身份不能为空") Short ident);

  void deleteCourse(User user, @NotNull(message = "课程不能为空") @Size(min = 1,message = "课程不能为空") List<Integer> courseIds);

  Course selectCourseById(@NotNull(message = "课程id不能为空") Integer courseId);

  Integer countByTitle(String title,Integer courseId);

  void updateCourse(Course course);

  /**
   * 获取课程是否开始
   * @param courseId
   * @return
   */
  Boolean isCourseStart(@NotNull(message = "课程ID不能为空") Integer courseId);

    void updateCourseForClassState(Integer courseId);

  Course getCourseById(Integer courseId);

    CommonResponseDto checkSignImport(Integer courseId, Integer id);

    CommonResponseDto checkSign(Integer courseId);
}
