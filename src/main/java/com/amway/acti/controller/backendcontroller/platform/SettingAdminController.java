package com.amway.acti.controller.backendcontroller.platform;

import com.amway.acti.controller.backendcontroller.BaseController;
import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.dto.MenuDto;
import com.amway.acti.model.User;
import com.amway.acti.service.BackendUserService;
import com.amway.acti.service.SettingAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/backend/platform")
public class SettingAdminController extends BaseController {

    @Autowired
    private BackendUserService backendUserService;

    @Autowired
    private SettingAdminService settingAdminService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 进入默认页面
     * @param model
     * @return
     */
    @RequestMapping("/adminIndex")
    public String adminIndex(Model model){
        User user = getSessionAdmin();
        MenuDto menuDto = backendUserService.findMenu(user);
        MenuDto menuChildDto = backendUserService.findChildMenuByParentCodeAndUser(user,"04");
        model.addAttribute("menuFirstList", menuDto.getMenuList());
        model.addAttribute("menuChildList", menuChildDto.getMenuList());
        model.addAttribute("curFirstMenu","04");
        model.addAttribute("curChildMenu","0402");
        int result = settingAdminService.selectAdminUserList();
        if(result == 0){
            return "platform/adminAdd";
        }else {
            return "platform/adminList";
        }
    }

    /**
     * 查询默认页面数据
     * @param name
     * @param ident
     * @param pageNo
     * @param pageSize
     * @return
     * @throws Exception
     */
    @RequestMapping("/adminList")
    @ResponseBody
    public CommonResponseDto adminList(String name,String ident,
                                       @RequestParam(value = "pageNo") Integer pageNo,
                                       @RequestParam(value = "pageSize") Integer pageSize) throws Exception {
        return CommonResponseDto.ofSuccess(settingAdminService.selectUserForAdmin(name,ident,pageNo,pageSize));
    }

    /**
     * 查询分页记录数
     * @param name
     * @param ident
     * @return
     */
    @RequestMapping("/queryCount")
    @ResponseBody
    public String queryTotalCount(String name,String ident){
        return settingAdminService.queryCount(name,ident);
    }

    /**
     * 获取需要修改的数据
     * @param uid
     * @return
     */
    @RequestMapping("/selectEditAdminData")
    @ResponseBody
    public CommonResponseDto selectEditAdminData(String uid) throws Exception {
        return CommonResponseDto.ofSuccess(settingAdminService.selectEditAdminData(uid));
    }

    /**
     * 编辑/新增数据
     * @param user
     * @return
     * @throws Exception
     */
    @RequestMapping("/editOrAddAdmin")
    @ResponseBody
    public CommonResponseDto editOrAddAdmin(User user) throws Exception {
        settingAdminService.editOrAddAdmin(user);
        return CommonResponseDto.ofSuccess();
    }

    /**
     * 批量修改
     * @param uids
     * @return
     */
    @RequestMapping("/delBatch")
    @ResponseBody
    public CommonResponseDto delBatch(String uids){
        settingAdminService.delBatch(uids);
        return CommonResponseDto.ofSuccess();
    }

    /**
     * 解锁
     * @param email
     * @return
     */
    @PostMapping("/deblocking")
    @ResponseBody
    public CommonResponseDto deblocking(String email){
        stringRedisTemplate.delete(email);
        return CommonResponseDto.ofSuccess();
    }
}
