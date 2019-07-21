package com.amway.acti.controller.backendcontroller;

import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.dto.MenuDto;
import com.amway.acti.dto.SitemTempDto;
import com.amway.acti.model.Sitem;
import com.amway.acti.model.SitemTemp;
import com.amway.acti.model.User;
import com.amway.acti.service.BackendUserService;
import com.amway.acti.service.SitemTempService;
import com.amway.acti.transform.SitemTempTransform;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.ui.Model;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Jie.Qiu
 * @create 2018-03-18
 **/
@Controller
@Slf4j
@RequestMapping("/backend/resource/sitemTemp")
public class SitemTempController extends BaseController{

    @Autowired
    private SitemTempService sitemTempService;

    @Autowired
    private BackendUserService backendUserService;

    @Autowired
    private SitemTempTransform sitemTempTransform;

    @RequestMapping("/templet")
    public String templetIndex(String name,String createTimeType, Model model){
        List<SitemTemp> sitemTempList = sitemTempService.selectSitemTempList(name,createTimeType);
        User user = getSessionAdmin();
        getMenu(user,model);
        int result = sitemTempList.size();
        if(result == 0){
            return "resource/indexTemplet";
        }else {
            return "resource/scoreTempletList";
        }
    }

    /**
     * 查询list页面数据
     * @return
     */
    @RequestMapping("/templetList")
    @ResponseBody
    public CommonResponseDto getList(String name, String createTimeTye,
                                     @RequestParam(value = "pageNo") Integer pageNo,
                                     @RequestParam(value = "pageSize") Integer pageSize){
        List<SitemTemp> sitemTempList = sitemTempService.selectSitemTempListByData(name,createTimeTye,pageNo,pageSize);
        List<SitemTempDto> sitemTempDtoList = sitemTempTransform.transformToSitemTempDto(sitemTempList);
        return CommonResponseDto.ofSuccess(sitemTempDtoList);
    }

    /**
     * 查询总数据
     * @return
     */
    @RequestMapping("/queryCount")
    @ResponseBody
    public String queryCount(String name,String createTimeType){
        List<SitemTemp> sitemTempList = sitemTempService.selectSitemTempList(name,createTimeType);
        return String.valueOf(sitemTempList.size());
    }

    /**
     * 跳转到默认页
     * @return
     */
    @RequestMapping("/indexTemplet")
    public String indexTemplet(HttpServletRequest request, Model model){
        Integer courseId = Integer.valueOf( request.getParameter( "courseId" ) );
        User user = getSessionAdmin();
        getMenu(user,model);
        request.setAttribute( "courseId", courseId );
        return "resource/indexTemplet";

    }

    /**
     * 跳转到新增页面
     * @return
     */
    @RequestMapping("/addTemplet")
    public String add(HttpServletRequest request, Model model){
        User user = getSessionAdmin();
        getMenu(user,model);
        return "resource/addTemplet";
    }

    /**
     * 新增数据
     * @param data
     * @return
     */
    @RequestMapping("/addSitem")
    @ResponseBody
    public CommonResponseDto addSitemTemp(String data){
        JSONObject jsonObject = JSONObject.fromObject(data);
        sitemTempService.addSitemTemp(jsonObject);
        return CommonResponseDto.ofSuccess();
    }

    /**
     * 删除模板
     * @param sitemIds
     * @return
     */
    @RequestMapping("/batchDelSitemTemp")
    @ResponseBody
    public CommonResponseDto batchDelSitemTemp(String sitemIds){
        return CommonResponseDto.ofSuccess(sitemTempService.batchSitemTemp(sitemIds));
    }

    /**
     * 删除模板时查询具体引用的数据
     * @param sitemTempId
     * @return
     */
    @RequestMapping("/batchDelSitemTempDetail")
    @ResponseBody
    public CommonResponseDto batchDelSitemTempDetail(String sitemTempId){
        return CommonResponseDto.ofSuccess(sitemTempService.batchDelSitemTempDetail(sitemTempId));
    }

    /**
     * 编辑保存数据
     * @param data
     * @return
     */
    @RequestMapping("/editSitemTemp")
    @ResponseBody
    public CommonResponseDto editSitemTemp(String data){
        sitemTempService.editSitemTemp(data);
        return CommonResponseDto.ofSuccess();
    }

    /**
     * 跳转到编辑页面
     * @param sitemTempId
     * @return
     */
    @RequestMapping("/editTemplet")
    public String editSitemTempView(HttpServletRequest request, Model model,String sitemTempId,String type){
        User user = getSessionAdmin();
        getMenu(user,model);
        model.addAttribute("sitemTemp",sitemTempService.querySitemTempData(sitemTempId,type));
        return "resource/editTemp";
    }

    /**
     * 获取编辑页面题干数据
     * @param sitemTempId
     * @return
     */
    @RequestMapping("/querySitemData")
    @ResponseBody
    public CommonResponseDto querySitemData(String sitemTempId){
        List<Sitem> sitemList = sitemTempService.querySitemData(sitemTempId);
        return CommonResponseDto.ofSuccess(sitemList);
    }

    /**
     * 查询菜单的公共方法
     * @param user
     * @param model
     */
    public void getMenu(User user,Model model){
        MenuDto menuDto = backendUserService.findMenu(user);
        MenuDto menuChildDto = backendUserService.findChildMenuByParentCodeAndUser(user,"02");
        model.addAttribute("menuFirstList", menuDto.getMenuList());
        model.addAttribute("menuChildList", menuChildDto.getMenuList());
        model.addAttribute("curFirstMenu","02");
        model.addAttribute("curChildMenu","0204");
    }
}
