package com.amway.acti.dto;

import lombok.Data;

@Data
public class UserAdminDto {

    private Integer id;

    private String name;

    private String email;

    private String password;

    private Short ident;

    private boolean deblock;
}
