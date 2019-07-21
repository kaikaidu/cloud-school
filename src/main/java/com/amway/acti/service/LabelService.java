package com.amway.acti.service;

import com.amway.acti.base.util.Constants;
import com.amway.acti.base.validation.constraints.StringRange;
import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.model.Label;
import com.github.pagehelper.PageInfo;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;

@Validated
public interface LabelService {
    /**
     * 根据id查询标签
     */
    Label selectById(@NotNull(message = "标签id不能为空") Integer id);

    List<Label> selectByType(@NotBlank(message = "标签类型为必填项")@StringRange(range = {Constants.LabelType.COURSE},message = "标签类型错误") String type);

    CommonResponseDto getLabelCount(String name,String type);

    CommonResponseDto<PageInfo<Label>> findLabelByName(Integer pageNo, Integer pageSize , String name, String sort, String type);

    CommonResponseDto addLabel(String name,String type);

    CommonResponseDto deleteLabel(String ids,String state);

    CommonResponseDto updateLabel(@NotNull(message = "标签id不能为空") Integer id, String name);

    CommonResponseDto getLabelByName(String name);

    CommonResponseDto getLabelAll();

    CommonResponseDto findCourse(Integer labelId);
}
