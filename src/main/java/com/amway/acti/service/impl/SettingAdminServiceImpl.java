package com.amway.acti.service.impl;

import com.amway.acti.base.exception.AmwayLogicException;
import com.amway.acti.base.util.Constants;
import com.amway.acti.base.util.EncrypDES;
import com.amway.acti.dao.UserMapper;
import com.amway.acti.dto.UserAdminDto;
import com.amway.acti.model.User;
import com.amway.acti.service.SettingAdminService;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class SettingAdminServiceImpl implements SettingAdminService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public int selectAdminUserList(){
       List<User> userList =  userMapper.selectAdminUserList();
       return userList.size();
    }

    public List<UserAdminDto> selectUserForAdmin (String name,String ident,
                                                  @RequestParam(value = "pageNo") Integer pageNo,
                                                  @RequestParam(value = "pageSize") Integer pageSize) throws Exception {
        log.info("name:{},ident:{},pageNo:{},pageSize:{}",name,ident,pageNo,pageSize);
        PageHelper.startPage(pageNo,pageSize);
        List<User> userList = userMapper.selectUserForAdmin(name,ident);
        List<UserAdminDto> userAdminDtoList = new ArrayList<>();
        for(User user:userList){
            UserAdminDto userAdminDto = new UserAdminDto();
            userAdminDto.setId(user.getId());
            userAdminDto.setName(user.getName());
            userAdminDto.setEmail(user.getEmail());
            userAdminDto.setPassword(EncrypDES.getInstance().setDesString(user.getPassword()));
           if(Constants.USER_IDENT_ADMIN == user.getIdent()){
               userAdminDto.setIdent(Constants.USER_IDENT_ADMIN);
           }else if(Constants.USER_IDENT_ASSISTANT == user.getIdent()){
               userAdminDto.setIdent(Constants.USER_IDENT_ASSISTANT);
           }
            userAdminDto.setDeblock(Integer.parseInt(stringRedisTemplate.opsForValue().get(user.getEmail())==null?"0":stringRedisTemplate.opsForValue().get(user.getEmail()))>5);
            userAdminDtoList.add(userAdminDto);
        }
        return userAdminDtoList;
    }

    public String queryCount(String name,String ident){
        log.info("name:{},ident:{}",name,ident);
        List<User> userList = userMapper.selectUserForAdmin(name,ident);
        return String.valueOf(userList.size());
    }

    public User selectEditAdminData(String uid) throws Exception {
        log.info("uid:{}",uid);
        User user = userMapper.selectByPrimaryKey(Integer.parseInt(uid));
        user.setPassword(EncrypDES.getInstance().setDesString(user.getPassword()));
        return user;
    }

    @Transactional
    public void editOrAddAdmin(User user) throws Exception {
        log.info("user:{}",user.toString());
        String password = EncrypDES.getInstance().setEncString(user.getPassword());
        user.setPassword(password);
        User userCheck = userMapper.selectByEmail(user);

        if(StringUtils.isEmpty(user.getId())){
            checkEmail(userCheck);
            user.setState(Constants.States.VALID);
            user.setCreateTime(new Date());
            userMapper.insertSelective(user);
        }else {
            User userOld = userMapper.selectByPrimaryKey(user.getId());
            if(!userOld.getEmail().equals(user.getEmail())){
                checkEmail(userCheck);
                //修改邮箱后重新处理登录错误的次数
                stringRedisTemplate.opsForValue().set(user.getEmail(),
                        stringRedisTemplate.opsForValue().get(userOld.getEmail())==null?"0":stringRedisTemplate.opsForValue().get(userOld.getEmail()));
                stringRedisTemplate.delete(userOld.getEmail());
            }
            userMapper.updateByPrimaryKeySelective(user);

            // 将redis中的修改用户的session删除
            Object object = stringRedisTemplate.opsForValue().get( Constants.ADMIN_CACHE_NAME + user.getId() );
            if(!isSame( user, userOld ) && object != null){
                releaseSession(object,user);
            }
        }
    }

    private boolean isSame(User u1, User u2){
        if(u1.getIdent() != u2.getIdent()){
            return false;
        }
        if(!org.apache.commons.lang.StringUtils.equals( u1.getName(), u2.getName() )){
            return false;
        }
        if(!org.apache.commons.lang.StringUtils.equals( u1.getEmail(), u2.getEmail() )){
            return false;
        }
        if(!org.apache.commons.lang.StringUtils.equals( u1.getPassword(), u2.getPassword() )){
            return false;
        }
        return true;
    }

    public void delBatch(String uids){
        log.info("uids:{}",uids);
        String[] ids = uids.split(",");
        for(String uid:ids){
            User user = new User();
            user.setId(Integer.parseInt(uid));
            user.setState(Constants.States.NO_AVAI);
            userMapper.updateByPrimaryKeySelective(user);
            // 将redis中的修改用户的session删除
            Object object = stringRedisTemplate.opsForValue().get( Constants.ADMIN_CACHE_NAME + user.getId() );
            if(object != null){
                releaseSession(object,user);
            }
        }
    }

    public void checkEmail(User userCheck){
        if(!StringUtils.isEmpty(userCheck)){
            throw new AmwayLogicException(Constants.ErrorCode.ERROR_ACCOUNT_OR_PAS, "邮箱已经被占用!");
        }
    }

    //清除缓存的公共方法
    public void releaseSession(Object object,User user){
        String sessionId = object.toString();
        stringRedisTemplate.delete( Constants.SPRING_SESSION_NAME + sessionId );
        stringRedisTemplate.delete( Constants.SPRING_SESSION_EXPIRES_NAME + sessionId );
        stringRedisTemplate.delete( Constants.ADMIN_CACHE_NAME + user.getId() );
        stringRedisTemplate.delete( Constants.USER_CACHE_NAME + user.getId() );
    }
}
