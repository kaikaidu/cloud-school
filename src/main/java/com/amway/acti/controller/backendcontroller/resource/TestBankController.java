package com.amway.acti.controller.backendcontroller.resource;

import com.amway.acti.base.exception.AmwaySystemException;
import com.amway.acti.controller.backendcontroller.BaseController;
import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.dto.MenuDto;
import com.amway.acti.dto.PageDto;
import com.amway.acti.dto.test.TestTempDto;
import com.amway.acti.model.TestTemp;
import com.amway.acti.service.BackendTestBankService;
import com.amway.acti.service.BackendUserService;
import com.amway.acti.transform.TestTransform;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author Wei.Li1
 * @create 2018-03-13 11:20
 **/

@Controller
@RequestMapping("/backend/resource/test")
@Slf4j
public class TestBankController extends BaseController {

    @Autowired
    private BackendTestBankService backendTestBankService;

    @Autowired
    private TestTransform testTransform;

    @Autowired
    private BackendUserService backendUserService;

    @RequestMapping(value = "/temp/listPage", method = RequestMethod.GET)
    public String listPage(HttpServletRequest request){
        boolean hasTestTemp = backendTestBankService.hasTestTemp(getSessionAdmin());
        request.setAttribute( "hasTestTemp", hasTestTemp );
        getMeum(request);
        return "resource/testBank-list";
    }

    @RequestMapping(value = "/temp/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResponseDto<PageDto<TestTempDto>> list(String search, boolean isDesc, int pageNo, int pageSize){
        PageInfo<TestTemp> info = backendTestBankService.findByUser( getSessionAdmin(), search, pageNo, pageSize, isDesc );
        PageDto<TestTempDto> pageDto = testTransform.transformInfoToTestTempDto( info );
        return CommonResponseDto.ofSuccess( pageDto );
    }

    @RequestMapping(value = "/temp/single", method = RequestMethod.GET)
    @ResponseBody
    public CommonResponseDto<JSONObject> single(int id){
        return CommonResponseDto.ofSuccess( backendTestBankService.findTempInfoById( id ));
    }

    @RequestMapping(value = "/temp/count", method = RequestMethod.GET)
    @ResponseBody
    public CommonResponseDto<Integer> count(String search){
        return CommonResponseDto.ofSuccess( backendTestBankService.count( getSessionAdmin(), search ) );
    }

    @RequestMapping(value = "/temp/remove", method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto remove(String ids){
        List<Map<String, Object>> list = backendTestBankService.removeTemps( ids );
        return CommonResponseDto.ofSuccess(list);
    }

    @RequestMapping(value = "/temp/remove/detail", method = RequestMethod.GET)
    @ResponseBody
    public CommonResponseDto removeDetail(Integer id){
        List<Map<String, Object>> list = backendTestBankService.findTestDeatil( id );
        return CommonResponseDto.ofSuccess(list);
    }

    @RequestMapping(value = "/temp/add", method = RequestMethod.GET)
    public String add(HttpServletRequest request){
        getMeum(request);
        return "resource/testBank-add";
    }

    @RequestMapping(value = "/temp/add", method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto add(String data){
        backendTestBankService.addTemp( getSessionAdmin(), data );
        return CommonResponseDto.ofSuccess();
    }

    @RequestMapping(value = "/temp/clone", method = RequestMethod.GET)
    public String clone(HttpServletRequest request, int id){
        JSONObject jsonObject = backendTestBankService.findTempInfoById( id );
        log.info( "jsonObject:{}", jsonObject );
        request.setAttribute( "temp", jsonObject );
        getMeum(request);
        return "resource/testBank-clone";
    }

    @RequestMapping(value = "/temp/edit", method = RequestMethod.GET)
    public String edit(HttpServletRequest request, int id){
        JSONObject jsonObject = backendTestBankService.findTempInfoById( id );
        log.info( "jsonObject:{}", jsonObject );
        request.setAttribute( "temp", jsonObject );
        getMeum(request);
        return "resource/testBank-edit";
    }

    @RequestMapping(value = "/temp/edit", method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto edit(String data){
        backendTestBankService.editTemp( getSessionAdmin(), data );
        return CommonResponseDto.ofSuccess();
    }

    @RequestMapping(value = "/temp/list/admin", method = RequestMethod.GET)
    @ResponseBody
    public CommonResponseDto<PageDto<TestTempDto>> listAdmin(String search, boolean isDesc, int pageNo, int pageSize){
        PageInfo<TestTemp> info = backendTestBankService.findAll( search, pageNo, pageSize, isDesc );
        PageDto<TestTempDto> pageDto = testTransform.transformInfoToTestTempDto( info );
        return CommonResponseDto.ofSuccess( pageDto );
    }

    @RequestMapping(value = "/temp/count/admin", method = RequestMethod.GET)
    @ResponseBody
    public CommonResponseDto<Integer> countAdmin(String search){
        return CommonResponseDto.ofSuccess( backendTestBankService.countAll( search ) );
    }

    private void getMeum(HttpServletRequest request){
        MenuDto menuDto = backendUserService.findMenu(getSessionAdmin());
        MenuDto menuChildDto = backendUserService.findChildMenuByParentCodeAndUser(getSessionAdmin(),"02");
        request.setAttribute("menuFirstList", menuDto.getMenuList());
        request.setAttribute("menuChildList", menuChildDto.getMenuList());
        request.setAttribute("curFirstMenu","02");
        request.setAttribute("curChildMenu","0202");
    }


}
