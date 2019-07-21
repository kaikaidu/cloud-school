package com.amway.acti.service.impl;

import com.amway.acti.dao.OperationLogMapper;
import com.amway.acti.model.OperationLog;
import com.amway.acti.service.OperationLogService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class OperationLogServiceImpl implements OperationLogService{

    @Autowired
    private OperationLogMapper operationLogMapper;

    @Override
    public List<OperationLog> findOperationLogList(Integer pageNo,Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        return operationLogMapper.findOperationLogList();
    }

    @Override
    public String queryCount() {
        return String.valueOf(operationLogMapper.findOperationLogList().size());
    }
}
