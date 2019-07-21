package com.amway.acti.service.impl;

import com.amway.acti.base.exception.AmwayParamException;
import com.amway.acti.dao.UserCertMapper;
import com.amway.acti.model.UserCert;
import com.amway.acti.service.UserCertService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@Service
public class UserCertServiceImpl implements UserCertService {

    @Autowired
    private UserCertMapper userCertMapper;

    @Override
    public PageInfo<UserCert> getByUserId(Integer userId, Integer pageNo, Integer pageSize) {
        log.info("getByUserId userId:{}, pageNo:{}, pageSize:{}", userId, pageNo, pageSize);
        PageHelper.startPage(pageNo, pageSize);
        List<UserCert> list = userCertMapper.selectByUserId(userId);
        log.info("list:{}", list);
        return new PageInfo<>(list);
    }

    @Override
    public UserCert getById(Integer id) {
        log.info("getById id:{}", id);
        UserCert userCert = userCertMapper.selectById(id);
        log.info("userCert:{}", userCert);
        return userCert;
    }

    @Override
    public UserCert getByCourseIdAndUserId(Integer courseId, Integer userId){
        log.info("getByCourseIdAndUserId couseId:{}, userId:{}", courseId, userId);
        UserCert userCert = userCertMapper.selectByCourseIdAndUserId(courseId, userId);
        log.info("userCert:{}", userCert);
        return userCert;
    }

    @Override
    public void updateIsRead(Integer id){
        userCertMapper.updateIsRead(id);
    }
}
