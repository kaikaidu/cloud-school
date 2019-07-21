package com.amway.acti.controller.backendcontroller;

import com.amway.acti.base.exception.AmwayLogicException;
import com.amway.acti.base.util.Constants;
import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.dto.CourseDocSearchDto;
import com.amway.acti.dto.DocSearchDto;
import com.amway.acti.dto.MenuDto;
import com.amway.acti.model.Course;
import com.amway.acti.model.Doc;
import com.amway.acti.model.User;
import com.amway.acti.service.BackendUserService;
import com.amway.acti.service.DocService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sound.midi.Soundbank;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
@RequestMapping("/backend")
public class BackendDocController {

    @Autowired
    private BackendUserService backendUserService;

    @Autowired
    private DocService docService;

    @Value("${upload.file.type}")
    private String[] types;

    @RequestMapping(value = "/resource/doc/list", method = RequestMethod.GET)
    public String docList(HttpServletRequest request, Model model) {
        getMeum(request, "02");
        model.addAttribute("hasData", docService.hasDoc(null));
        model.addAttribute("curFirstMenu", "02");
        model.addAttribute("curChildMenu", "0205");
        return "resource/docDefault";
    }

    @RequestMapping(value = "/resource/doc/saveDoc", method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto saveDto(@RequestBody Doc doc) {
        try {
            URL url = new URL(doc.getUrl());
            HttpURLConnection httpconn = (HttpURLConnection) url.openConnection();
            if (httpconn.getHeaderField("Content-Type").equals("text/html")) {
                throw new AmwayLogicException(Constants.ErrorCode.UPLOAD_URL_ERROR, "文件上传地址不可用！");
            }
            String filePath = httpconn.getURL().getFile();
            String type = filePath.substring(filePath.lastIndexOf(".") + 1);
            if (!Arrays.asList(types).contains(type)) {
                throw new AmwayLogicException(Constants.ErrorCode.UPLOAD_FILE_TYPE_ERROR, "上传文件类型必须为doc,xls,ppt,pdf,docx,xlsx,pptx,mp4,mp3！");
            }
            int fileSize = httpconn.getContentLength();
            log.info("/resource/doc/saveDoc 资料大小:{}", fileSize);
            if (fileSize < 0) {
                throw new AmwayLogicException(Constants.ErrorCode.UPLOAD_URL_ERROR, "文件上传地址不可用！");
            } else if (fileSize > Constants.FileUploadLimit.TOP_LIMIT) {
                throw new AmwayLogicException(Constants.ErrorCode.UPLOAD_FILE_ERROR, Constants.FileUploadLimit.TOP_LIMIT_MSG);
            }
        } catch (AmwayLogicException e) {
            log.error("/resource/doc/saveDoc error:{}", e);
            throw e;
        } catch (Exception e) {
            log.error("/resource/doc/saveDoc error:{}", e);
            throw new AmwayLogicException(Constants.ErrorCode.UPLOAD_URL_ERROR, "文件上传地址不可用！");
        }
        docService.saveUpdateDoc(doc);
        return CommonResponseDto.ofSuccess();
    }

    @RequestMapping(value = "/resource/doc/countDoc", method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto countDoc(@RequestBody DocSearchDto docSearchDto) {
        Integer docCount = docService.countDoc(docSearchDto);
        return CommonResponseDto.ofSuccess(docCount);
    }

    @RequestMapping(value = "/resource/doc/docList", method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto docList(@RequestBody DocSearchDto docSearchDto) {
        PageInfo<Doc> pageInfo = docService.selectDoc(docSearchDto);
        return CommonResponseDto.ofSuccess(pageInfo);
    }

    @RequestMapping(value = "/resource/doc/delete", method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto delete(@RequestBody List<Integer> docIds) {
        Map<String, List<? extends Doc>> resultMap = docService.deleteDoc(docIds);
        return CommonResponseDto.ofSuccess(resultMap);
    }

    @RequestMapping(value = "/resource/doc/selectById", method = RequestMethod.GET)
    @ResponseBody
    public CommonResponseDto selectById(Integer docId) {
        Doc doc = docService.selectById(docId);
        return CommonResponseDto.ofSuccess(doc);
    }

    public void getMeum(HttpServletRequest request, String firstMenu) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(Constants.BACKEND_USER_SESSSION_KEY);
        log.info("user:{}", user.toString());
        MenuDto menuDto = backendUserService.findMenu(user);
        MenuDto menuChildDto = backendUserService.findChildMenuByParentCodeAndUser(user, firstMenu);
        request.setAttribute("menuFirstList", menuDto.getMenuList());
        request.setAttribute("menuChildList", menuChildDto.getMenuList());
    }

    @RequestMapping(value = "/course/doc/list", method = RequestMethod.GET)
    public String courseDocList(Integer courseId, HttpServletRequest request, Model model) {
        getMeum(request, "01");
        if (courseId == null) {
            return "redirect:/backend/course/basic";
        }
        model.addAttribute("hasDoc", docService.hasDoc(courseId));
        model.addAttribute("courseId", courseId);
        model.addAttribute("curFirstMenu", "01");
        model.addAttribute("curChildMenu", "0109");
        return "course/docCourse";
    }

    @RequestMapping(value = "/course/doc/add", method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto addCourseDoc(Integer courseId, Integer[] docIds) {
        Map<String, Object> map = docService.addCourseDoc(courseId, docIds);
        return CommonResponseDto.ofSuccess(map);
    }

    @RequestMapping(value = "/course/doc/docCount", method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto countCourseDoc(@RequestBody CourseDocSearchDto dto) {
        Integer result = docService.countCourseDocByCondition(dto);
        return CommonResponseDto.ofSuccess(result);
    }

    @RequestMapping(value = "/course/doc/docList", method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto courseDocList(@RequestBody CourseDocSearchDto dto) {
        PageInfo<Doc> pageInfo = docService.selectCourseDocByCondition(dto);
        return CommonResponseDto.ofSuccess(pageInfo);
    }

    @RequestMapping(value = "/course/doc/shelve", method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto courseShelve(Integer courseId, Integer[] docIds, Integer shelve) {
        docService.uplateCourseDocShelve(courseId, docIds, shelve);
        return CommonResponseDto.ofSuccess();
    }

    @RequestMapping(value = "/course/doc/delete", method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto deleteCourseDoc(Integer courseId, Integer[] docIds) {
        docService.deleteCourseDoc(courseId, docIds);
        return CommonResponseDto.ofSuccess();
    }

    @RequestMapping(value = "/resource/doc/course/list", method = RequestMethod.POST)
    @ResponseBody
    public CommonResponseDto selectCourseByDoc(Integer docId) {
        List<Course> courses = docService.selectCourseByDocId(docId);
        return CommonResponseDto.ofSuccess(courses);
    }

}
