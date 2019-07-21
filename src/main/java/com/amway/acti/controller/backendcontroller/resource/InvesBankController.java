package com.amway.acti.controller.backendcontroller.resource;

import com.amway.acti.base.exception.AmwaySystemException;
import com.amway.acti.controller.backendcontroller.BaseController;
import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.dto.MenuDto;
import com.amway.acti.dto.PageDto;
import com.amway.acti.dto.inves.InvesTempDto;
import com.amway.acti.model.InvesTemp;
import com.amway.acti.model.User;
import com.amway.acti.service.BackendInvesBankService;
import com.amway.acti.service.BackendUserService;
import com.amway.acti.transform.InvesTransform;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author Wei.Li1
 * @create 2018-03-21 10:08
 **/
@Controller
@RequestMapping("/backend/resource/inves")
@Slf4j
public class InvesBankController extends BaseController {

    @Autowired
    private BackendInvesBankService backendInvesBankService;

    @Autowired
    private InvesTransform invesTransform;

    @Autowired
    private BackendUserService backendUserService;

    @RequestMapping(value = "/temp/listPage", method = RequestMethod.GET)
    public String listPage(HttpServletRequest request, Model model) {
        User user = getSessionAdmin();
        boolean hasInvesTemp = backendInvesBankService.hasInvesTemp( user );
        request.setAttribute( "hasInvesTemp", hasInvesTemp );
        getMeum( request );
        return "resource/invesBank-list";
    }

    @RequestMapping(value = "/temp/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResponseDto <PageDto <InvesTempDto>> list(String search, boolean isDesc, int pageNo, int pageSize) {
        PageInfo <InvesTemp> info = backendInvesBankService.findByUser( getSessionAdmin(), search, pageNo, pageSize, isDesc );
        PageDto <InvesTempDto> pageDto = invesTransform.transformInfoToInvesTempDto( info );
        return CommonResponseDto.ofSuccess( pageDto );
    }

    @RequestMapping(value = "/temp/single", method = RequestMethod.GET)
    @ResponseBody
    public CommonResponseDto <JSONObject> single(int id) {
        return CommonResponseDto.ofSuccess( backendInvesBankService.findTempInfoById( id ) );
    }

    @RequestMapping(value = "/temp/count", method = RequestMethod.GET)
    @ResponseBody
    public CommonResponseDto <Integer> count(String search) {
        return CommonResponseDto.ofSuccess( backendInvesBankService.count( getSessionAdmin(), search ) );
    }

    @RequestMapping(value = "/temp/remove", method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto remove(String ids) {
        List<Map<String, Object>> list = backendInvesBankService.removeTemps( ids );
        return CommonResponseDto.ofSuccess(list);
    }

    @RequestMapping(value = "/temp/remove/detail", method = RequestMethod.GET)
    @ResponseBody
    public CommonResponseDto removeDetail(Integer id){
        List<Map<String, Object>> list = backendInvesBankService.findInvesDeatil( id );
        return CommonResponseDto.ofSuccess(list);
    }

    @RequestMapping(value = "/temp/add", method = RequestMethod.GET)
    public String add(HttpServletRequest request) {
        getMeum( request );
        return "resource/invesBank-add";
    }

    @RequestMapping(value = "/temp/add", method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto add(String data) {
        backendInvesBankService.addTemp( getSessionAdmin(), data );
        return CommonResponseDto.ofSuccess();
    }

    @RequestMapping(value = "/temp/clone", method = RequestMethod.GET)
    public String clone(HttpServletRequest request, int id) {
        JSONObject jsonObject = backendInvesBankService.findTempInfoById( id );
        log.info( "jsonObject:{}", jsonObject );
        request.setAttribute( "temp", jsonObject );
        getMeum( request );
        return "resource/invesBank-clone";
    }

    @RequestMapping(value = "/temp/edit", method = RequestMethod.GET)
    public String edit(HttpServletRequest request, int id) {
        JSONObject jsonObject = backendInvesBankService.findTempInfoById( id );
        log.info( "jsonObject:{}", jsonObject );
        request.setAttribute( "temp", jsonObject );
        getMeum( request );
        return "resource/invesBank-edit";
    }

    @RequestMapping(value = "/temp/edit", method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto edit(String data) {
        backendInvesBankService.editTemp( getSessionAdmin(), data );
        return CommonResponseDto.ofSuccess();
    }


    @RequestMapping(value = "/temp/list/admin", method = RequestMethod.GET)
    @ResponseBody
    public CommonResponseDto <PageDto <InvesTempDto>> listAdmin(String search, boolean isDesc, int pageNo, int pageSize) {
        PageInfo <InvesTemp> info = backendInvesBankService.findAll( search, pageNo, pageSize, isDesc );
        PageDto <InvesTempDto> pageDto = invesTransform.transformInfoToInvesTempDto( info );
        return CommonResponseDto.ofSuccess( pageDto );
    }

    @RequestMapping(value = "/temp/count/admin", method = RequestMethod.GET)
    @ResponseBody
    public CommonResponseDto <Integer> countAdmin(String search) {
        return CommonResponseDto.ofSuccess( backendInvesBankService.countAll( search ) );
    }

    private void getMeum(HttpServletRequest request) {
        MenuDto menuDto = backendUserService.findMenu( getSessionAdmin() );
        MenuDto menuChildDto = backendUserService.findChildMenuByParentCodeAndUser( getSessionAdmin(), "02" );
        request.setAttribute( "menuFirstList", menuDto.getMenuList() );
        request.setAttribute( "menuChildList", menuChildDto.getMenuList() );
        request.setAttribute("curFirstMenu","02");
        request.setAttribute("curChildMenu","0203");
    }

}