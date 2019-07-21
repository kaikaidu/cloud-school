package com.amway.acti.dao;

import com.amway.acti.model.Label;
import com.amway.acti.model.LabelCustom;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LabelMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Label record);

    int insertSelective(Label record);

    Label selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Label record);

    int updateByPrimaryKey(Label record);

    List<Label> selectByType(String type);

    int getLabelCount(@Param("name") String name,@Param("type") String type);

    List<LabelCustom> selectLabelByName(@Param("name") String name,
                                  @Param("sort") String sort,
                                  @Param("type") String type);

    List<LabelCustom> selectLabelById(@Param("list") List<String> list);

    Label selectLabelByNameAndType(@Param("name") String name,@Param("type") String type);
}