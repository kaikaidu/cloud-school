package com.amway.acti.dao;

import com.amway.acti.model.OperationLog;

import java.util.List;

public interface OperationLogMapper {

    int insertSelective(OperationLog record);

    List<OperationLog> findOperationLogList();

}
