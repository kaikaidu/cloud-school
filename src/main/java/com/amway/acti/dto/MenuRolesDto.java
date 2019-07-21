/**
 * Created by dk on 2018/6/5.
 */

package com.amway.acti.dto;

import com.amway.acti.model.Menu;
import com.amway.acti.model.User;
import lombok.Data;

import java.util.List;

@Data
public class MenuRolesDto {
    private List<Menu> menuFirstList;
    private List<Menu> menuChildList;
    private User user;
}
