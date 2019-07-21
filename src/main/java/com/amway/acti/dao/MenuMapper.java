package com.amway.acti.dao;

import com.amway.acti.model.Menu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MenuMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Menu record);

    int insertSelective(Menu record);

    Menu selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Menu record);

    int updateByPrimaryKey(Menu record);

    List<Menu> selectByIdent(int ident);

    List<Menu> selectChildMenuByParentCode(@Param("parentCode") String parentCode, @Param("ident") int ident);

    List<Menu> selectByIdentAndParentCode(int ident);
}
