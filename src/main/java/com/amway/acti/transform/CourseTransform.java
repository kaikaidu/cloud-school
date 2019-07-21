/**
 * Created by Will Zhang on 2018/2/24.
 */

package com.amway.acti.transform;

import com.amway.acti.dto.*;
import com.amway.acti.model.Course;
import com.amway.acti.model.User;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Validated
public interface CourseTransform
{
  PageDto<CourseDto> transformPageInfoToDto(PageInfo<Course> pageInfo,String searchType,Integer userId);

  CourseDetailDto transformModelToDto(Integer courseId,Integer userId);

  /**
   * 新增课程基本信息
   */
  Course transformDtoToModel(@Valid CourseBasicDto dto,Integer userId);

  PageDto<CourseListBackendDto> transformPageInfoToDto(PageInfo<Course> pageInfo);

  CourseBackendDto transformModelToDto(Course course,Boolean value);


  void setValuesForCourseDetailDto(Course course, User user, CourseDetailDto dto);
}
