/**
 * Created by DK on 2018/2/11.
 */

package com.amway.acti.controller.frontendcontroller;

import com.amway.acti.base.context.MiniProgramRequestContextHolder;
import com.amway.acti.base.util.Constants;
import com.amway.acti.dto.CityDto;
import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.model.Addr;
import com.amway.acti.service.AddrService;
import com.amway.acti.service.UserService;
import com.amway.acti.transform.AddrTransform;
import com.amway.acti.transform.UserInfoTransform;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@Api
@RestController
@RequestMapping(value = "/frontend/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AddrService addrService;

    @Autowired
    private AddrTransform addrTransform;

    @Autowired
    private UserInfoTransform userInfoTransform;

    /**
     * 获取用户信息
     * @return
     */
    @RequestMapping(value = "/userInfo", method = RequestMethod.GET)
    public CommonResponseDto findUserInfo() throws GeneralSecurityException, IOException {
        Integer uid = MiniProgramRequestContextHolder.getRequestUser().getUid();
        return CommonResponseDto.ofSuccess(userInfoTransform.transformUserToUserInfoDto(userService.getUser(uid)));
    }

    /**
     * 级联地址查询
     * 为空查省，传参则查级联市
     * @param province
     * @return
     */
    @RequestMapping(value = "/addr", method = RequestMethod.GET)
    public CommonResponseDto findAddr(String province){
        if(StringUtils.isEmpty(province)){
            List<Addr> addrList =  addrService.findCityData();
            List<CityDto> cityDtoList =addrTransform.transformToDto(addrList);
            return CommonResponseDto.ofSuccess(cityDtoList);
        }else {
            List<Addr> addrList =  addrService.findCityDataByProvince(province);
            List<CityDto> cityDtoList =addrTransform.transformToDto(addrList);
            return CommonResponseDto.ofSuccess(cityDtoList);
        }
    }

    /**
     * 级联地址查询-区级
     * @param city
     * @return
     */
    @RequestMapping(value = "/region", method = RequestMethod.GET)
    public CommonResponseDto findRegion(String city){
        if(StringUtils.isEmpty(city)){
            return CommonResponseDto.ofFailure(Constants.ErrorCode.PARAM_EXCEPTION,"city值为空");
        }else {
            List<Addr> addrList = addrService.findRegion(city);
            List<CityDto> cityDtoList =addrTransform.transformToDto(addrList);
            return CommonResponseDto.ofSuccess(cityDtoList);
        }
    }

    /**
     * 修改手机号
     *
     * @param phone
     * @return
     */
    @RequestMapping(value = "/savePhone", method = RequestMethod.POST)
    public CommonResponseDto savePhone(String phone) {
        Integer uid = MiniProgramRequestContextHolder.getRequestUser().getUid();
        userService.savePhone(phone, uid);
        return CommonResponseDto.ofSuccess();
    }

    /**
     * 修改地址
     *
     * @param province
     * @param city
     * @param region
     * @param delAddress
     * @return
     */
    @RequestMapping(value = "/saveAddr", method = RequestMethod.POST)
    public CommonResponseDto saveAddr(String province, String city, String region, String delAddress) {
        Integer uid = MiniProgramRequestContextHolder.getRequestUser().getUid();
        userService.saveAddr(province, city, region, delAddress, uid);
        return CommonResponseDto.ofSuccess();
    }

    /**
     * 修改备注
     * @param remark
     * @return
     */
    @RequestMapping(value = "/saveRemark", method = RequestMethod.POST)
    public CommonResponseDto saveRemark(String remark) throws GeneralSecurityException, IOException {
        Integer uid = MiniProgramRequestContextHolder.getRequestUser().getUid();
        userService.saveRemark(uid,remark);
        return CommonResponseDto.ofSuccess(userInfoTransform.transformUserToUserInfoDto(userService.selectByPrimary(uid)));
    }
}
