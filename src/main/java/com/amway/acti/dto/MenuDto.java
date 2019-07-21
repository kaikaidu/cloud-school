package com.amway.acti.dto;

import com.amway.acti.model.Menu;
import lombok.Data;

import java.util.List;

@Data
public class MenuDto {

    private List<Menu> menuList;

    public List <Menu> getMenuList() {
        return menuList;
    }

    public void setMenuList(List <Menu> menuList) {
        this.menuList = menuList;
    }
}
