package com.amway.acti.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class MenuChild implements Serializable {
    private List<Menu> menuList;
}
