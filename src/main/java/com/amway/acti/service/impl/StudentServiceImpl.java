package com.amway.acti.service.impl;

import com.amway.acti.base.exception.AmwayLogicException;
import com.amway.acti.base.util.Constants;
import com.amway.acti.dao.AddrMapper;
import com.amway.acti.dao.CourseApprovalMapper;
import com.amway.acti.dao.CourseMapper;
import com.amway.acti.dao.UserCustomMapper;
import com.amway.acti.dao.UserMapper;
import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.model.Addr;
import com.amway.acti.model.User;
import com.amway.acti.model.UserCustom;
import com.amway.acti.service.StudentService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:学员管理
 * @Date:2018/3/29 19:39
 * @Author:wsc
 */
@Service
@Slf4j
public class StudentServiceImpl implements StudentService{

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AddrMapper addrMapper;

    @Autowired
    private UserCustomMapper userCustomMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private CourseApprovalMapper courseApprovalMapper;
    /**
     * @Description:查询学员
     * @Date: 2018/3/29 19:37
     * @param: pageNo
     * @param: pageSize
     * @param: name
     * @Author: wsc
     */
    @Override
    public CommonResponseDto<PageInfo<UserCustom>> studentServicestudentList(Integer pageNo, Integer pageSize, String name) {
        log.info("pageNo:{},pageSize:{},name:{}",pageNo,pageSize,name);
        PageHelper.startPage(pageNo,pageSize);
        List<UserCustom> userList = userCustomMapper.selectUserByName(name);
        return CommonResponseDto.ofSuccess(new PageInfo <>(userList));
    }

    /**
     * @Description:获取总数
     * @Date: 2018/3/29 20:03
     * @param: name
     * @Author: wsc
     */
    @Override
    public CommonResponseDto getStudentCount(String name) {
        int count = userMapper.getStudentCount(name);
        return CommonResponseDto.ofSuccess(count);
    }

    /**
     * @Description:根据id查询学员
     * @Date: 2018/3/29 21:08
     * @param: id
     * @Author: wsc
     */
    @Override
    public CommonResponseDto getStudentById(Integer id) {
        User user = userMapper.selectByPrimaryKey(id);
        return CommonResponseDto.ofSuccess(user);
    }

    /**
     * @Description:根据id修改学员
     * @Date: 2018/3/30 9:44
     * @param: id
     * @param: phone
     * @param: address
     * @Author: wsc
     */
    @Override
    @CachePut(value = Constants.USER_CACHE_NAME, key = "'" + Constants.USER_CACHE_NAME + "'+#user.id")
    @Transactional
    public User updateStudent(User user) {
        User u =null;
        try { log.info("user:{}",user);
            u = userMapper.selectByPrimaryKey(user.getId());
            u.setPhone(user.getPhone());
            u.setAddress(user.getAddress());
            u.setProvCode(user.getProvCode());
            u.setCityCode(user.getCityCode());
            u.setRegionCode(user.getRegionCode());

            userMapper.updateByPrimaryKey(u);
        } catch (Exception e) {
            throw new AmwayLogicException("更新学员信息，SQL执行错误");
        }
        return u;
    }

    /**
     * @Description:获取省份
     * @Date: 2018/4/2 15:34
     * @param:
     * @Author: wsc
     */
    @Override
    public CommonResponseDto provinceList() {
        return CommonResponseDto.ofSuccess(addrMapper.findCityData());
    }

    /**
     * @Description:根据code查询地址
     * @Date: 2018/4/2 15:34
     * @param: code
     * @Author: wsc
     */
    @Override
    public CommonResponseDto getAddr(String code) {
        List<Addr> addrList = addrMapper.selectAddrByParentCode(code);
        return CommonResponseDto.ofSuccess(addrList);
    }

    /**
     * @Description:软删除学员
     * @Date: 2018/3/29 20:57
     * @param: ids
     * @Author: wsc
     */
    @Override
    @Transactional
    public CommonResponseDto updateStudentById(String ids) {
        String[] arrayIds = ids.split(",");
        Map<String, List<User>> map = new HashMap<>(2);
        List<User> dels = new ArrayList<>();
        List<User> nodels = new ArrayList<>();
        List<User> userList = userMapper.selectByUserList(Arrays.asList(arrayIds),new Date(System.currentTimeMillis()));
        for (User user : userList) {
            if (user.getSignState() == 1) {
                nodels.add(user);
            } else {
                user.setState(Constants.States.NO_AVAI);
                dels.add(user);
            }
        }
        //根据用户
        map.put("nodels", nodels);
        if (!CollectionUtils.isEmpty(dels)) {
            userMapper.updateUserByList(dels);
            map.put("dels", dels);
        }
        return CommonResponseDto.ofSuccess(map);
    }
}
