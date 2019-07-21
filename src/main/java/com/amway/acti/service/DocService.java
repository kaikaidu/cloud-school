package com.amway.acti.service;

import com.amway.acti.dto.CourseDocSearchDto;
import com.amway.acti.dto.DocSearchDto;
import com.amway.acti.model.Course;
import com.amway.acti.model.Doc;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Map;

@Validated
public interface DocService {

    List<Doc> selectByCourseId(@NotNull(message = "课程id不能为空")Integer courseId);

    void saveUpdateDoc(@Valid Doc doc);

    Boolean hasDoc(Integer courseId);

    Integer countDoc(@Valid DocSearchDto docSearchDto);

    PageInfo<Doc> selectDoc(@Valid DocSearchDto docSearchDto);

    Map<String, List<? extends Doc>> deleteDoc(@NotNull(message = "资料不能为空")@Size(min = 1,message = "资料不能为空") List<Integer> docIds);

    Doc selectById(@NotNull(message = "资料id不能为空") Integer docId);

    Map<String, Object> addCourseDoc(@NotNull(message = "课程id不能为空") Integer courseId,@NotNull(message = "资料不能为空")@Size(min = 1,message = "资料不能为空") Integer[] docIds);

    Integer countCourseDocByCondition(@Valid CourseDocSearchDto dto);

    PageInfo<Doc> selectCourseDocByCondition(@Valid CourseDocSearchDto dto);

    void uplateCourseDocShelve(@NotNull(message = "课程id不能为空") Integer courseId,@NotNull(message = "资料不能为空")@Size(min = 1,message = "资料不能为空") Integer[] docIds,@NotNull(message = "上下架不能为空") Integer shelve);

    void deleteCourseDoc(@NotNull(message = "课程id不能为空") Integer courseId,@NotNull(message = "资料不能为空")@Size(min = 1,message = "资料不能为空") Integer[] docIds );

    List<Course> selectCourseByDocId(@NotNull(message = "资料id不能为空") Integer docId);
}
