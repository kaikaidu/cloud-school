package com.amway.acti.controller.backendcontroller.course;

import com.amway.acti.base.exception.AmwaySystemException;
import com.amway.acti.base.util.DownloadUtil;
import com.amway.acti.base.util.JSONUtil;
import com.amway.acti.controller.backendcontroller.BaseController;
import com.amway.acti.dao.CourseMapper;
import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.dto.MenuDto;
import com.amway.acti.model.*;
import com.amway.acti.service.BackendUserService;
import com.amway.acti.service.SitemTempService;
import com.amway.acti.service.WeightService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qiujie
 * @create 2018-03-24
 */
@Slf4j
@Controller
@RequestMapping("/backend/course/weight")
public class WeightController extends BaseController{

    @Autowired
    private WeightService weightService;

    @Autowired
    private BackendUserService backendUserService;

    @Autowired
    private SitemTempService sitemTempService;

    @Autowired
    private CourseMapper courseMapper;

    private static final String WEIGHTCOURSEID = "courseId";
    /**
     *  跳转到新增页面
     * @param request
     * @return
     */
    @RequestMapping(value = "/addWeight", method = RequestMethod.GET)
    public String weight(HttpServletRequest request, Model model){
        Integer courseId = Integer.valueOf( request.getParameter(WEIGHTCOURSEID));
        User user = getSessionAdmin();
        getMenu(user,model,courseId);
        return "course/weightAdd";
    }

    /**
     * 跳转到默认页面
     * @param request
     * @return
     */
    @RequestMapping(value = "/weighIndex", method = RequestMethod.GET)
    public String weightIndex(HttpServletRequest request,Model model){
        if(StringUtils.isEmpty(request.getParameter(WEIGHTCOURSEID))){
            return "redirect:/backend/course/basic";
        }else{
            Integer courseId = Integer.valueOf( request.getParameter(WEIGHTCOURSEID));
            boolean hasWeight = weightService.hasWeight(courseId);
            User user = getSessionAdmin();
            getMenu(user,model,courseId);
            model.addAttribute("hasWeight",hasWeight);
            model.addAttribute( "hasResult", weightService.hasResult( courseId ) );
            return "course/weightIndex";
        }
    }

    /**
     * 跳转到列表页面
     * @param request
     * @return
     */
    @RequestMapping(value = "/forwardWeightList", method = RequestMethod.GET)
    public String forwardWeightList(HttpServletRequest request,Model model){
        Integer courseId = Integer.valueOf( request.getParameter(WEIGHTCOURSEID) );
        User user = getSessionAdmin();
        getMenu(user,model,courseId);
        return "/course/weightList";
    }

    /**
     * 查询列表数据
     * @param weightResult
     * @param pageNo
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/weightList", method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto weightResult(WeightResult weightResult,
                                          @RequestParam(value = "pageNo") Integer pageNo,
                                          @RequestParam(value = "pageSize") Integer pageSize){
        List<WeightResult> weightResultList = weightService.queryWeightResult(weightResult,pageNo,pageSize);
        return CommonResponseDto.ofSuccess(weightResultList);
    }

    /**
     * 查询总记录数
     * @param weightResult
     * @return
     */
    @RequestMapping(value = "queryCount", method = RequestMethod.POST)
    @ResponseBody
    public String queryCount(WeightResult weightResult){
        return weightService.queryWeightResultCount(weightResult);
    }

    /**
     * 批量导出
     * @param response
     * @param request
     * @param
     */
    @RequestMapping(value = "/exportWeightListExcel",method = RequestMethod.GET)
    public void exportWeightListExcel(HttpServletResponse response,
                                      HttpServletRequest request,
                                      int courseId){
        try {
            String utf8 = "UTF-8";
            Course course = courseMapper.selectByPrimaryKey( courseId );
            List<Map<String, Object>> list = weightService.getExportInfo(courseId);
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("data", list);
            // 1.设置文件ContentType类型，这样设置，会自动判断下载文件类型
            response.setContentType("multipart/form-data");
            // 2.设置文件头：最后一个参数是设置下载文件名
            response.setHeader("Content-Disposition",
                    "attachment;fileName=" + DownloadUtil.encodeDownloadFilename(course.getTitle()+"评分结果.xls", request ));

            Configuration configuration = new Configuration(Configuration.VERSION_2_3_27);
            configuration.setDefaultEncoding(utf8);

            configuration.setClassForTemplateLoading(TestOnlineController.class,"/");
            Template t = configuration.getTemplate("sitem-template.xml");
            Writer out = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), utf8));
            t.process(dataMap, out);
            out.flush();
            out.close();
        } catch (IOException | TemplateException e){
            log.error( e.getMessage(), e );
            throw new AmwaySystemException( "数据处理异常" );
        }


    }

    /**
     * 查询模板数据
     * @param name,createTimeTye
     * @return
     */
    @RequestMapping(value = "/selectTemp",method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto selectTemp(String name,String createTimeTye,
                                        @RequestParam(value = "pageNo") Integer pageNo,
                                        @RequestParam(value = "pageSize") Integer pageSize){
        return CommonResponseDto.ofSuccess(weightService.selectSitemTemp(name,createTimeTye,pageNo,pageSize));
    }

    /**
     * 保存评分数据
     * @param data
     */
    @RequestMapping(value = "/saveOrEditWeight", method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto saveOrEditWeight(String data){
        weightService.saveOrEditWeight(data);
        return CommonResponseDto.ofSuccess();
    }

    /**
     * 默认页面加载数据
     * @param courseId
     * @return
     */
    @RequestMapping(value = "/searchWeightData", method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto searchWeightData(String courseId){
        return CommonResponseDto.ofSuccess(weightService.searchWeightData(courseId));
    }

    /**
     * 查询评分详情数据
     * @param adaNumber
     * @return
     */
    @RequestMapping(value = "/searchScoreDetail", method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto searchScoreDetail(String courseId,String adaNumber,
                                               @RequestParam(value = "pageNo") Integer pageNo,
                                               @RequestParam(value = "pageSize") Integer pageSize){
        return CommonResponseDto.ofSuccess(weightService.selectScoreDetail(courseId,adaNumber,pageNo,pageSize));
    }

    /**
     * 查询详情数据记录数
     * @param adaNumber
     * @return
     */
    @RequestMapping(value = "/queryScoreDetailCount", method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto queryScoreDetailCount(String courseId,String adaNumber){
        return CommonResponseDto.ofSuccess(weightService.queryScoreDetailCount(courseId,adaNumber));
    }

    /**
     * 查询菜单的公共方法
     * @param user
     * @param model
     */
    public void getMenu(User user,Model model,Integer courseId){
        MenuDto menuDto = backendUserService.findMenu(user);
        MenuDto menuChildDto = backendUserService.findChildMenuByParentCodeAndUser(user,"01");
        model.addAttribute("menuFirstList", menuDto.getMenuList());
        model.addAttribute("menuChildList", menuChildDto.getMenuList());
        model.addAttribute("curFirstMenu","01");
        model.addAttribute("curChildMenu","0108");
        model.addAttribute(WEIGHTCOURSEID,courseId);
    }

    /**
     * 查询list页面数据
     * @return
     */
    @RequestMapping("/templetList")
    @ResponseBody
    public CommonResponseDto getList(String name, String createTimeType,
                                     @RequestParam(value = "pageNo") Integer pageNo,
                                     @RequestParam(value = "pageSize") Integer pageSize){
        List<SitemTemp> sitemTempList = sitemTempService.selectSitemTempListByData(name,createTimeType,pageNo,pageSize);
        return CommonResponseDto.ofSuccess(sitemTempList);
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
     * 查询总数据
     * @return
     */
    @RequestMapping(value = "/queryTempCount",method = RequestMethod.POST)
    @ResponseBody
    public String queryTempCount(String name,String createTimeType){
        List<SitemTemp> sitemTempList = sitemTempService.selectSitemTempList(name,createTimeType);
        return String.valueOf(sitemTempList.size());
    }
}
