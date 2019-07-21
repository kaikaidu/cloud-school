/**
 * Created by dk on 2018/2/23.
 */

package com.amway.acti.service.impl;

import com.amway.acti.base.exception.AmwayLogicException;
import com.amway.acti.base.util.Constants;
import com.amway.acti.base.util.EncrypDES;
import com.amway.acti.base.util.IPWhitelistUtil;
import com.amway.acti.dao.MenuMapper;
import com.amway.acti.dao.UserMapper;
import com.amway.acti.dto.MenuDto;
import com.amway.acti.dto.TeacherLoginDto;
import com.amway.acti.model.Menu;
import com.amway.acti.model.User;
import com.amway.acti.service.BackendUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@Service
@Slf4j
public class BackendUserServiceImpl implements BackendUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public static final String X_ORWARDED_FOR = "X-Forwarded-For";

    /**
     * 获取讲师名单
     *
     * @return
     */
    @Override
    public List<User> getTeachers() {
        return userMapper.selectAllTeachers();
    }

    /**
     * 获取助教名单
     * @return
     */
    @Override
    public List<User> getAssists() {
        return userMapper.selectAllAssists();
    }

    @Override
    public Boolean checkTeachersExist(List<String> teacherIds) {
        Integer teacherNum = userMapper.countTeacherByList(teacherIds);
        log.info("teacherNum:{}", teacherNum);
        if (teacherNum != null && teacherNum == teacherIds.size()) {
            log.info("checkResult::{}", true);
            return true;
        }
        log.info("checkResult::{}", false);
        return false;
    }

    @Override
    public User checkByEmailAndPwd(TeacherLoginDto dto, HttpServletRequest request) throws GeneralSecurityException, IOException {

        //log.info("TeacherLoginDto:{}", dto.toString());
        String password = EncrypDES.getInstance().setEncString(dto.getPassword());
        //log.info("password after encode:{}", password);
        User u = userMapper.checkByEmail(dto.getEmail());
        if (ObjectUtils.isEmpty(u)) {
            throw new AmwayLogicException(Constants.ErrorCode.ERROR_ACCOUNT_OR_PAS, "邮箱或密码错误");
        }
        //add qj
        Integer num = Integer.parseInt(stringRedisTemplate.opsForValue().get(dto.getEmail()) == null ? "0" : stringRedisTemplate.opsForValue().get(dto.getEmail()));
        if (num > 5) {
            throw new AmwayLogicException(Constants.ErrorCode.ERROR_ACCOUNT_OR_PAS, "密码错误已达六次,您的账号将被锁定,如需解锁,请联系管理员");
        }
        User user = userMapper.selectByEmailAndPassword(dto.getEmail(), password);
        if (ObjectUtils.isEmpty(user)) {
            num += 1;
            //后期需要添加失效期的话可以在此处添加 qj
            stringRedisTemplate.opsForValue().set(dto.getEmail(), num.toString());
            throw new AmwayLogicException(Constants.ErrorCode.ERROR_ACCOUNT_OR_PAS, "邮箱或密码错误");
        }
        //管理员校验白名单 add wsc
        if (user.getIdent() == Constants.USER_IDENT_ADMIN) {
            this.validateWhitelistIp(request);
        }
        if (user.getIdent() == Constants.USER_IDENT_LECTURER) {
            throw new AmwayLogicException(Constants.ErrorCode.ERROR_ACCOUNT_OR_PAS, "您无权限登录");
        }
        //成功后将记录清空 qj
        stringRedisTemplate.delete(dto.getEmail());
        return user;
    }

    /***
     * 验证访问的ip是否为白名单
     * @param request
     */
    private void validateWhitelistIp(HttpServletRequest request) {
        String clientIP = request.getHeader(X_ORWARDED_FOR) != null ? request.getHeader(X_ORWARDED_FOR) : request.getRemoteAddr();
        log.info("ip--------------------:{}", clientIP);
        if (!StringUtils.isEmpty(clientIP)) {
            String temIp = "";
            if (clientIP.indexOf(",") != -1) {
                log.info("substring-ip:{}", clientIP.substring(0, clientIP.indexOf(",")));
                temIp = clientIP.substring(0, clientIP.indexOf(","));
                if (temIp.indexOf(":") != -1) {
                    temIp = clientIP.substring(0, clientIP.indexOf(":"));
                }
            } else {
                if (clientIP.indexOf(":") != -1) {
                    temIp = clientIP.substring(0, clientIP.indexOf(":"));
                } else {
                    temIp = clientIP;
                }
            }
            log.info("temIp:{}", temIp);
            if (!StringUtils.isEmpty(temIp)) {
                boolean flag = false;
                for (String ip : IPWhitelistUtil.ipWhiteList) {
                    if (ip.equals(temIp.trim())) {
                        flag = true;
                    }
                }
                log.info("flag:{}", flag);
                if (!flag) {
                    throw new AmwayLogicException(Constants.ErrorCode.ERROR_ACCOUNT_OR_PAS, "未经授权，无法登录.");
                }
            }
        }
    }

    @Override
    public MenuDto findMenu(User user) {
        log.info("user:{}", user.toString());
        Short ident = user.getIdent();
        return transFormByIdent(ident);
    }

    //通过传入的角色不同查询并整理菜单数据
    public MenuDto transFormByIdent(Short ident) {
        MenuDto menuDto = new MenuDto();
        //根据ident和parentCode查出一级目录
        List<Menu> menuList = menuMapper.selectByIdentAndParentCode(ident);
        menuDto.setMenuList(menuList);
        return menuDto;
    }

    //查询二级菜单
    @Override
    public MenuDto findChildMenuByParentCodeAndUser(User user, String parentCode) {
        log.info("user{},parentCode{}", user.toString(), parentCode);
        MenuDto menuDto = new MenuDto();
        Short ident = user.getIdent();
        List<Menu> childMenuList = menuMapper.selectChildMenuByParentCode(parentCode, ident);
        menuDto.setMenuList(childMenuList);
        return menuDto;
    }

    @Override
    public User findByEmail(String email) {
        User user = new User();
        user.setEmail(email);
        return userMapper.selectByEmail(user);
    }
}
