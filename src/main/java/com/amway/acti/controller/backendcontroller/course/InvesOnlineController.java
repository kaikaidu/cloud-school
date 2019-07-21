package com.amway.acti.controller.backendcontroller.course;

import com.amway.acti.base.redis.RedisComponent;
import com.amway.acti.base.exception.AmwaySystemException;
import com.amway.acti.base.util.Constants;
import com.amway.acti.base.util.DownloadUtil;
import com.amway.acti.controller.backendcontroller.BaseController;
import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.dto.MenuDto;
import com.amway.acti.dto.PageDto;
import com.amway.acti.dto.inves.InvesTempDto;
import com.amway.acti.model.*;
import com.amway.acti.service.BackendInvesBankService;
import com.amway.acti.service.BackendInvesOnlineService;
import com.amway.acti.service.BackendUserService;
import com.amway.acti.service.CourseService;
import com.amway.acti.transform.InvesTransform;
import com.github.pagehelper.PageInfo;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Wei.Li1
 * @create 2018-03-16 12:46
 **/
@Controller
@RequestMapping("/backend/course/inves")
@Slf4j
public class InvesOnlineController extends BaseController{

    @Autowired
    private BackendInvesOnlineService backendInvesOnlineService;

    @Autowired
    private BackendInvesBankService backendInvesBankService;

    @Autowired
    private InvesTransform invesTransform;

    @Autowired
    private BackendUserService backendUserService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private RedisComponent redisComponent;


    private static final String COURSEID = "courseId";

    @RequestMapping(value = "/online/listPage", method = RequestMethod.GET)
    public String listPage(HttpServletRequest request,Model model){
        String courseIdStr = request.getParameter( COURSEID );
        if(StringUtils.isBlank( courseIdStr )){
            return "redirect:/backend/course/basic";
        }
        int courseId = Integer.parseInt( courseIdStr );
        setCourseId( courseId );
        User user = getSessionAdmin();
        boolean hasInvesPaper = backendInvesOnlineService.hasInvesPaper( user, courseId );
        request.setAttribute( "hasInvesPaper", hasInvesPaper );
        getMeum( request );
        return "course/invesOnline-list";
    }

    @RequestMapping(value = "/online/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResponseDto<PageInfo<InvesPaper>> list(String search, int courseId, int state, int pageNo, int pageSize){
        PageInfo<InvesPaper> info = backendInvesOnlineService.findByUser( getSessionAdmin(), courseId, search, state, pageNo, pageSize );
        return CommonResponseDto.ofSuccess( info );
    }

    @RequestMapping(value = "/online/count", method = RequestMethod.GET)
    @ResponseBody
    public CommonResponseDto<Integer> count(String search, int courseId, int state){
        return CommonResponseDto.ofSuccess( backendInvesOnlineService.count( getSessionAdmin(), courseId, search, state ) );
    }

    @RequestMapping(value = "/online/onsale", method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto<Integer> onsale(String ids, int state, int courseId){
        backendInvesOnlineService.onsale( ids, state, courseId );
        delRedis(courseId);
        return CommonResponseDto.ofSuccess();
    }

    @RequestMapping(value = "/online/add", method = RequestMethod.GET)
    public String add(HttpServletRequest request,Model model){
        int courseId = Integer.parseInt( request.getParameter( COURSEID ) );
        setCourseId( courseId );
        getMeum( request );
        return "course/invesOnline-add";
    }

    @RequestMapping(value = "/online/add", method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto add(InvesPaper invesPaper){
        backendInvesOnlineService.addPaper( getSessionAdmin(), invesPaper );
        delRedis(invesPaper.getCourseId());
        return CommonResponseDto.ofSuccess();
    }

    @RequestMapping(value = "/online/remove", method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto remove(int id, int courseId){
        backendInvesOnlineService.removePaper( id, Constants.InvesOnline.DELETE, courseId );
        delRedis(courseId);
        return CommonResponseDto.ofSuccess();
    }

    @RequestMapping(value = "/online/edit", method = RequestMethod.GET)
    public String edit(HttpServletRequest request,Model model){
        int courseId = Integer.parseInt( request.getParameter( COURSEID ) );
        setCourseId( courseId );
        int id = Integer.parseInt( request.getParameter( "id" ) );
        InvesPaper invesPaper = backendInvesOnlineService.findById( id );
        request.setAttribute( "invesPaper", invesPaper );
        InvesTemp invesTemp = backendInvesBankService.findById( invesPaper.getTempId() );
        request.setAttribute( "tempName", invesTemp.getName() );
        // 设置问卷是否有答题记录标志
        request.setAttribute( "hasResult", backendInvesOnlineService.hasResult( id ) );
        getMeum( request );
        return "course/invesOnline-edit";
    }

    @RequestMapping(value = "/online/edit", method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto edit(InvesPaper invesPaper){
        backendInvesOnlineService.editPaper( getSessionAdmin(), invesPaper );
        delRedis(invesPaper.getCourseId());
        return CommonResponseDto.ofSuccess();
    }

    @RequestMapping(value = "/online/single", method = RequestMethod.GET)
    @ResponseBody
    public CommonResponseDto<JSONObject> single(int id){
        InvesPaper invesPaper = backendInvesOnlineService.findById( id );
        InvesTemp invesTemp = backendInvesBankService.findById( invesPaper.getTempId() );
        List<InvesQuest> invesQuests = backendInvesBankService.findByTempId( invesPaper.getTempId() );
        JSONObject jobj = invesTransform.transformToPaperViewJson( invesPaper, invesTemp, invesQuests );
        return CommonResponseDto.ofSuccess( jobj );
    }

    @RequestMapping(value = "/temp/list/admin", method = RequestMethod.GET)
    @ResponseBody
    public CommonResponseDto <PageDto<InvesTempDto>> listAdmin(String search, boolean isDesc, int pageNo, int pageSize) {
        PageInfo <InvesTemp> info = backendInvesBankService.findAll( search, pageNo, pageSize, isDesc );
        PageDto <InvesTempDto> pageDto = invesTransform.transformInfoToInvesTempDto( info );
        return CommonResponseDto.ofSuccess( pageDto );
    }

    @RequestMapping(value = "/temp/count/admin", method = RequestMethod.GET)
    @ResponseBody
    public CommonResponseDto <Integer> countAdmin(String search) {
        return CommonResponseDto.ofSuccess( backendInvesBankService.countAll( search ) );
    }

    @RequestMapping(value = "/temp/single", method = RequestMethod.GET)
    @ResponseBody
    public CommonResponseDto <JSONObject> tempSingle(int id) {
        InvesTemp invesTemp = backendInvesBankService.findById( id );
        List <InvesQuest> invesQuests = backendInvesBankService.findByTempId( id );
        JSONObject jobj = invesTransform.transformToViewJson( invesTemp, invesQuests );
        return CommonResponseDto.ofSuccess( jobj );
    }

    @RequestMapping(value = "/result/listPage", method = RequestMethod.GET)
    public String resultListPage(HttpServletRequest request){
        int courseId = Integer.parseInt( request.getParameter( COURSEID ) );
        List<InvesPaper> list = backendInvesOnlineService.findOnByCourseId( courseId );
        request.setAttribute( "invesPapers", list );
        setCourseId( courseId );
        getMeum( request );
        return "course/invesOnline-result";
    }

    private void getMeum(HttpServletRequest request){
        MenuDto menuDto = backendUserService.findMenu(getSessionAdmin());
        MenuDto menuChildDto = backendUserService.findChildMenuByParentCodeAndUser(getSessionAdmin(),"01");
        request.setAttribute("menuFirstList", menuDto.getMenuList());
        request.setAttribute("menuChildList", menuChildDto.getMenuList());
        request.setAttribute("curFirstMenu","01");
        request.setAttribute("curChildMenu","0105");
    }

    @RequestMapping(value = "/result/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResponseDto<List<Map<String, Object>>> resultList(@RequestParam Map<String, Object> info, int pageNo, int pageSize){
        return CommonResponseDto.ofSuccess(backendInvesOnlineService.getResultByInfo( info, pageNo, pageSize ));
    }

    @RequestMapping(value = "/result/count", method = RequestMethod.GET)
    @ResponseBody
    public CommonResponseDto<Integer> resultCount(@RequestParam Map<String, Object> info){
        int count = backendInvesOnlineService.countResultByInfo( info );
        return CommonResponseDto.ofSuccess(count);
    }

    @RequestMapping(value = "/result/export", method = RequestMethod.GET)
    public void resultExport(int courseId) {
        try {
            String utf8 = "UTF-8";
            Course course = courseService.selectCourseById( courseId );
            List<Map<String, Object>> list = backendInvesOnlineService.getResult(courseId);
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("data", list);
            // 1.设置文件ContentType类型，这样设置，会自动判断下载文件类型
            response.setContentType("multipart/form-data");
            // 2.设置文件头：最后一个参数是设置下载文件名
            response.setHeader("Content-Disposition",
                    "attachment;fileName=" + DownloadUtil.encodeDownloadFilename(course.getTitle()+"问卷结果.xls", request ));

            Configuration configuration = new Configuration(Configuration.VERSION_2_3_27);
            configuration.setDefaultEncoding(utf8);

            configuration.setClassForTemplateLoading(InvesOnlineController.class,"/");
            Template t = configuration.getTemplate("result-template.xml");
            Writer out = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), utf8));
            t.process(dataMap, out);
            out.flush();
            out.close();
        } catch (IOException | TemplateException e){
            log.error( e.getMessage() );
            throw new AmwaySystemException( "数据处理异常" );
        }

    }

    @RequestMapping(value = "/result/single", method = RequestMethod.GET)
    @ResponseBody
    public CommonResponseDto<JSONObject> resultSingle(int userId, int paperId){
        return CommonResponseDto.ofSuccess( backendInvesOnlineService.buildViewData( userId, paperId ) );
    }

    private void delRedis(int courseId){
        String key = Constants.INVES_CACHE_KEY+courseId;
        String[] keys = redisComponent.keys( key );
        redisComponent.del( keys );
    }



}
