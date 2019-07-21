package com.amway.acti.controller.backendcontroller.course;

import com.amway.acti.base.redis.RedisComponent;
import com.amway.acti.base.exception.AmwaySystemException;
import com.amway.acti.base.util.Constants;
import com.amway.acti.base.util.DownloadUtil;
import com.amway.acti.controller.backendcontroller.BaseController;
import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.dto.MenuDto;
import com.amway.acti.dto.PageDto;
import com.amway.acti.dto.test.TestTempDto;
import com.amway.acti.model.*;
import com.amway.acti.service.BackendTestBankService;
import com.amway.acti.service.BackendTestOnlineService;
import com.amway.acti.service.BackendUserService;
import com.amway.acti.service.CourseService;
import com.amway.acti.transform.TestTransform;
import com.github.pagehelper.PageInfo;
import freemarker.template.*;
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
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Wei.Li1
 * @create 2018-03-16 12:46
 **/
@Controller
@RequestMapping("/backend/course/test")
@Slf4j
public class TestOnlineController extends BaseController{

    @Autowired
    private BackendTestOnlineService backendTestOnlineService;

    @Autowired
    private BackendTestBankService backendTestBankService;

    @Autowired
    private TestTransform testTransform;

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
        boolean hasTestPaper = backendTestOnlineService.hasTestPaper( user, courseId );
        request.setAttribute( "hasTestPaper", hasTestPaper );
        getMeum( request );
        return "course/testOnline-list";
    }

    @RequestMapping(value = "/online/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResponseDto<PageInfo<TestPaper>> list(String search, int courseId, int state, int pageNo, int pageSize){
        PageInfo<TestPaper> info = backendTestOnlineService.findByUser( getSessionAdmin(), courseId, search, state, pageNo, pageSize );
        return CommonResponseDto.ofSuccess( info );
    }

    @RequestMapping(value = "/online/count", method = RequestMethod.GET)
    @ResponseBody
    public CommonResponseDto<Integer> count(String search, int courseId, int state){
        return CommonResponseDto.ofSuccess( backendTestOnlineService.count( getSessionAdmin(), courseId, search, state ) );
    }

    @RequestMapping(value = "/online/onsale", method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto<Integer> onsale(String ids, int state, int courseId){
        backendTestOnlineService.onsale( ids, state, courseId );
        delRedis(courseId);
        return CommonResponseDto.ofSuccess();
    }

    @RequestMapping(value = "/online/add", method = RequestMethod.GET)
    public String add(HttpServletRequest request,Model model){
        int courseId = Integer.parseInt( request.getParameter( COURSEID ) );
        setCourseId( courseId );
        getMeum( request );
        return "course/testOnline-add";
    }

    @RequestMapping(value = "/online/add", method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto add(TestPaper testPaper){
        backendTestOnlineService.addPaper( getSessionAdmin(), testPaper );
        delRedis(testPaper.getCourseId());
        return CommonResponseDto.ofSuccess();
    }

    @RequestMapping(value = "/online/remove", method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto remove(int id, int courseId){
        backendTestOnlineService.removePaper( id, Constants.TestOnline.DELETE, courseId );
        delRedis(courseId);
        return CommonResponseDto.ofSuccess();
    }

    private void delRedis(int courseId){
        String key = Constants.COURSE_TEST_PAPER_USER_LIST + courseId;
        String[] keys = redisComponent.keys( key );
        redisComponent.del( keys );
        String key2 = Constants.COURSE_TEST_PAPER_USER_INFO + courseId;
        String[] keys2 = redisComponent.keys( key2 );
        redisComponent.del( keys2 );
    }

    @RequestMapping(value = "/online/edit", method = RequestMethod.GET)
    public String edit(HttpServletRequest request){
        int courseId = Integer.parseInt( request.getParameter( COURSEID ) );
        setCourseId( courseId );
        int id = Integer.parseInt( request.getParameter( "id" ) );
        TestPaper testPaper = backendTestOnlineService.findById( id );
        request.setAttribute( "testPaper", testPaper );
        TestTemp testTemp = backendTestBankService.findById( testPaper.getTempId() );
        request.setAttribute( "tempName", testTemp.getName() );
        // 设置试卷是否有答题记录标志
        request.setAttribute( "hasResult", backendTestOnlineService.hasResult( id ) );
        getMeum( request );
        return "course/testOnline-edit";
    }

    @RequestMapping(value = "/online/edit", method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto edit(TestPaper testPaper){
        backendTestOnlineService.editPaper( getSessionAdmin(), testPaper );
        delRedis(testPaper.getCourseId());
        return CommonResponseDto.ofSuccess();
    }

    @RequestMapping(value = "/online/single", method = RequestMethod.GET)
    @ResponseBody
    public CommonResponseDto<JSONObject> single(int id){
        TestPaper testPaper = backendTestOnlineService.findById( id );
        TestTemp testTemp = backendTestBankService.findById( testPaper.getTempId() );
        List<TestQuest> testQuests = backendTestBankService.findByTempId( testPaper.getTempId() );
        JSONObject jobj = testTransform.transformToPaperViewJson( testPaper, testTemp, testQuests );
        return CommonResponseDto.ofSuccess( jobj );
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

    @RequestMapping(value = "/temp/single", method = RequestMethod.GET)
    @ResponseBody
    public CommonResponseDto<JSONObject> tempSingle(int id){
        TestTemp testTemp = backendTestBankService.findById( id );
        List<TestQuest> testQuests = backendTestBankService.findByTempId( id );
        JSONObject jobj = testTransform.transformToViewJson( testTemp, testQuests );
        return CommonResponseDto.ofSuccess( jobj );
    }

    @RequestMapping(value = "/score/listPage", method = RequestMethod.GET)
    public String scoreListPage(HttpServletRequest request){
        int courseId = Integer.parseInt( request.getParameter( COURSEID ) );
        List<TestPaper> list = backendTestOnlineService.findOnByCourseId( courseId );
        request.setAttribute( "testPapers", list );
        setCourseId( courseId );
        getMeum( request );
        return "course/testOnline-score";
    }

    private void getMeum(HttpServletRequest request){
        MenuDto menuDto = backendUserService.findMenu(getSessionAdmin());
        MenuDto menuChildDto = backendUserService.findChildMenuByParentCodeAndUser(getSessionAdmin(),"01");
        request.setAttribute("menuFirstList", menuDto.getMenuList());
        request.setAttribute("menuChildList", menuChildDto.getMenuList());
        request.setAttribute("curFirstMenu","01");
        request.setAttribute("curChildMenu","0104");
    }

    @RequestMapping(value = "/score/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResponseDto<List<Map<String, Object>>> scoreList(@RequestParam Map<String, Object> map, int pageNo, int pageSize){
        return CommonResponseDto.ofSuccess(backendTestOnlineService.getScoreResultByInfo( map, pageNo, pageSize ));
    }

    @RequestMapping(value = "/score/count", method = RequestMethod.GET)
    @ResponseBody
    public CommonResponseDto<Integer> scoreCount(@RequestParam Map<String, Object> map){
        int count = backendTestOnlineService.countScoreResultByInfo( map );
        return CommonResponseDto.ofSuccess(count);
    }

    @RequestMapping(value = "/score/view", method = RequestMethod.GET)
    @ResponseBody
    public CommonResponseDto<JSONObject> scoreView(int userId, int paperId){
        JSONObject jsonObject = backendTestOnlineService.getScoreInfo( userId, paperId );
        return CommonResponseDto.ofSuccess(jsonObject);
    }

    @RequestMapping(value = "/score/export", method = RequestMethod.GET)
    public void scoreExport(int courseId, HttpServletResponse response) {
        try {
            String utf8 = "UTF-8";
            Course course = courseService.selectCourseById( courseId );
            List<Map<String, Object>> list = backendTestOnlineService.getScoreResult(courseId);
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("data", list);
            // 1.设置文件ContentType类型，这样设置，会自动判断下载文件类型
            response.setContentType("multipart/form-data");
            // 2.设置文件头：最后一个参数是设置下载文件名
            response.setHeader("Content-Disposition",
                    "attachment;fileName=" + DownloadUtil.encodeDownloadFilename(course.getTitle()+"测试结果.xls", request ));

            Configuration configuration = new Configuration(Configuration.VERSION_2_3_27);
            configuration.setDefaultEncoding(utf8);

            configuration.setClassForTemplateLoading(TestOnlineController.class,"/");
            Template t = configuration.getTemplate("score-template.xml");
            Writer out = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), utf8));
            t.process(dataMap, out);
            out.flush();
            out.close();
        } catch (IOException | TemplateException e){
            log.error( e.getMessage(), e );
            throw new AmwaySystemException( "数据处理异常" );
        }

    }



}
