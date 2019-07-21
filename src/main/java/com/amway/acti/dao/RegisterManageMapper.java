package com.amway.acti.dao;

import com.amway.acti.model.Register;

import java.util.List;

public interface RegisterManageMapper {

    List<Register> findSignFirstList(Register sign);

    List<Register> findRegisterList(Register sign);

}
