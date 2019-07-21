package com.amway.acti.service;

import com.amway.acti.model.OperationLog;

import java.util.List;

public interface OperationLogService {

    List<OperationLog> findOperationLogList(Integer pageNo, Integer pageSize);

    String queryCount();
}
