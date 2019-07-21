package com.amway.acti.service.impl;

import com.amway.acti.base.exception.AmwayLogicException;
import com.amway.acti.base.util.Constants;
import com.amway.acti.base.util.EncrypDES;
import com.amway.acti.dao.UserMapper;
import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.model.User;
import com.amway.acti.service.TeacherService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:讲师
 * @Date:2018/3/23 11:23
 * @Author:wsc
 */
@Service
@Slf4j
public class TeacherServiceImpl implements TeacherService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * @Description:新增讲师
     * @Date: 2018/3/23 11:22
     * @param: name 姓名
     * @param: email 邮箱
     * @param: password 密码
     * @Author: wsc
     */
    @Override
    @Transactional
    public CommonResponseDto addTeacher(String name, String email, String password) throws Exception {
        log.info("name:{},email:{},password:{}", name, email, password);
        User user = new User();
        EncrypDES encrypDES = EncrypDES.getInstance();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(encrypDES.setEncString(password));
        user.setIdent(Constants.USER_IDENT_LECTURER);
        user.setState(Constants.States.VALID);
        user.setCreateTime(new Date(System.currentTimeMillis()));
        userMapper.insertSelective(user);
        log.info("--------->addTeacher-End");
        return CommonResponseDto.ofSuccess();
    }

    /**
     * @Description:获取讲师总数
     * @Date: 2018/3/23 14:00
     * @param: name 讲师名称
     * @Author: wsc
     */
    @Override
    public CommonResponseDto getTeacherCount(String name) {
        log.info("name:{}", name);
        int teacherCount = userMapper.selectUserCount(name, Constants.USER_IDENT_LECTURER);
        log.info("teacherCount:{}", teacherCount);
        return CommonResponseDto.ofSuccess(teacherCount);
    }

    /**
     * @Description:根据讲师名称查询讲师列表
     * @Date: 2018/3/23 14:11
     * @param: pageNo 当前页
     * @param: pageSize 每页数量
     * @param: name 讲师名称
     * @Author: wsc
     */
    @Override
    public PageInfo<User> findTeacherByName(Integer pageNo, Integer pageSize, String name) throws Exception {
        log.info("pageNo:{},pageSize:{},name:{}", pageNo, pageSize, name);
        EncrypDES encrypDES = EncrypDES.getInstance();
        String password;
        PageHelper.startPage(pageNo, pageSize);
        List<User> userList = userMapper.selectUserByNameAndIdent(name, Constants.USER_IDENT_LECTURER);
        log.info("userList:{}", userList);
        for (User u : userList) {
            password = encrypDES.setDesString(u.getPassword());
            u.setPassword(password);
            //判断是否需要解锁 qj
            u.setDeblock(Integer.parseInt(stringRedisTemplate.opsForValue().get(u.getEmail())==null?"0":stringRedisTemplate.opsForValue().get(u.getEmail()))>5);
        }
        log.info("userList:{}", userList);
        return new PageInfo<>(userList);
    }

    /**
     * @Description:根据身份查询用户
     * @Date: 2018/3/23 14:27
     * @param: ident 用户身份 1-学员  2-讲师  3-助教 0-管理员
     * @Author: wsc
     */
    @Override
    public List<User> findTeacherByIdent(short ident) {
        log.info("ident:{}", ident);
        return userMapper.selectUserByNameAndIdent(null, ident);
    }

    /**
     * @Description:根据id删除讲师
     * @Date: 2018/3/23 14:49
     * @param: ids 讲师id
     * @Author: wsc
     */
    @Override
    @CacheEvict(value = Constants.USER_CACHE_NAME, beforeInvocation = true)
    @Transactional
    public CommonResponseDto updateTeacherById(String ids) {
        log.info("ids:{}", ids);
        String[] arrayIds = ids.split(",");
        List<User> dels = new ArrayList<>();
        List<User> nodels = new ArrayList<>();
        List<User> users = userMapper.selectUsersInIds(Arrays.asList(arrayIds));
        Map<String, List<User>> map = new HashMap<>(2);
        for (User user : users) {
            log.info("updateTeacherById:{}", user.toString());
            Integer courses = userMapper.countCourses(user.getId(),new Date(System.currentTimeMillis()));
            if (courses > 0) {
                nodels.add(user);
            } else {
                user.setState(Constants.States.NO_AVAI);
                dels.add(user);
            }
        }
        map.put("nodels", nodels);
        if (!CollectionUtils.isEmpty(dels)) {
            userMapper.updateUserByList(dels);
            map.put("dels", dels);
        }
        return CommonResponseDto.ofSuccess(map);
    }

    /**
     * @Description:根据讲师id查询讲师详情
     * @Date: 2018/3/26 10:11
     * @param: id 讲师id
     * @Author: wsc
     */
    @Override
    public CommonResponseDto getTeacherById(Integer id) throws Exception {
        log.info("id:{}", id);
        User user = userMapper.selectByPrimaryKey(id);
        log.info("selectByPrimaryKey:{}", user.toString());
        if (!ObjectUtils.isEmpty(user)) {
            EncrypDES encrypDES = EncrypDES.getInstance();
            user.setPassword(encrypDES.setDesString(user.getPassword()));
        }
        return CommonResponseDto.ofSuccess(user);
    }

    /**
     * @Description:修改讲师
     * @Date: 2018/3/26 11:35
     * @param: id 讲师id
     * @param: name 讲师名字
     * @param: email 邮箱
     * @param: pwd 密码
     * @Author: wsc
     */
    @Override
    @CachePut(value = Constants.USER_CACHE_NAME, key = "'" + Constants.USER_CACHE_NAME + "'+#id")
    @Transactional
    public User updateTeacher(Integer id, String name, String email, String pwd) throws Exception {
        log.info("id:{},name:{},email:{},pwd:{}", id, name, email, pwd);
        User user = userMapper.selectByPrimaryKey(id);
        EncrypDES encrypDES = EncrypDES.getInstance();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(encrypDES.setEncString(pwd));
        log.info("user:{}", user);
        userMapper.updateByPrimaryKeySelective(user);
        return user;
    }

    /**
     * @Description:根据邮箱查询讲师
     * @Date: 2018/4/8 10:08
     * @param: email
     * @Author: wsc
     */
    @Override
    public CommonResponseDto getTeacherByEmail(String email) {
        List<User> users = userMapper.getTeacherByEmail(email);
        if (null != users && !users.isEmpty()) {
            throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"邮箱已存在");
        }
        return CommonResponseDto.ofSuccess(users);
    }

}
