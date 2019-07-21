package com.amway.acti.controller.frontendcontroller;

import com.amway.acti.base.util.HttpClientUtil;
import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.dto.DocDto;
import com.amway.acti.model.Doc;
import com.amway.acti.service.DocService;
import com.amway.acti.transform.DocTransform;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.regex.Pattern;

@Api
@Slf4j
@RestController
@RequestMapping(value = "/frontend")
public class DocController {

    @Autowired
    private DocService docService;

    @Autowired
    private DocTransform docTransform;

    @RequestMapping(value = "/docList", method = RequestMethod.GET)
    public CommonResponseDto<List<DocDto>> findDocList(Integer courseId) {
        List<Doc> docList = docService.selectByCourseId(courseId);
        List<DocDto> docDtoList = docTransform.transformPageInfoToDto(docList);
        return CommonResponseDto.ofSuccess(docDtoList);
    }

    @RequestMapping(value = "/test/article1", method = RequestMethod.GET)
    public CommonResponseDto article1(HttpServletResponse response) throws Exception {
        String html = HttpClientUtil.doGet("https://bizqa.amwaynet.com.cn/content/china/accl/ch/training/cloud-school/nutrilite/0026.html");
        String str32 = html.replace("/etc/", "https://bizqa.amwaynet.com.cn/etc/").replace("src=\"/content", "src=\"https://bizqa.amwaynet.com.cn/content");
        String str = new String(str32.getBytes("ISO-8859-1"), "utf-8");
        return CommonResponseDto.ofSuccess(str);
    }

    @RequestMapping(value = "/test/article2", method = RequestMethod.GET)
    public void article2(HttpServletResponse response) throws Exception {
        log.info("Begin article2");
        String a = "<a href=\"https://cstest.connext.com.cn/frontend/test/article5\">test1<a/></br><a href=\"https://cstest.connext.com.cn/frontend/test/article6\">test2<a/></br><script type=\"text/javascript\">\n" +
            "\t\tfunction addSc(){\n" +
            "\t\t\talert(1111);\n" +
            "\t\t var ajax = new XMLHttpRequest();\n" +
            "\t\t\tajax.open('get','https://cstest.connext.com.cn/frontend/test/article4?id=123');\n" +
            "\t\t\tajax.send();\n" +
            "\t\t\tajax.onreadystatechange = function () {\n" +
            "\t\t\t\tif (ajax.readyState==4 &&ajax.status==200) {\n" +
            "\t\t\t\t\talert(\"收藏成功\");\n" +
            "\t\t\t\t}\n" +
            "\t\t\t}\n" +
            "\t\t}\n" +
            "\t</script>";

        String html = HttpClientUtil.doGet("https://bizqa.amwaynet.com.cn/content/china/accl/ch/training/cloud-school/nutrilite/0026.html");
        html = java.util.regex.Pattern.compile("<script[\\s\\S]*?>[\\s\\S]*?<\\/script>", Pattern.CASE_INSENSITIVE).matcher(html).replaceAll("")
            .replace("class=\"hidden\"", "")
            .replace("<link rel=\"stylesheet\" href=\"/etc/", "<link rel=\"stylesheet\" href=\"https://bizqa.amwaynet.com.cn/etc/")
            .replace("src=\"/content", "src=\"https://bizqa.amwaynet.com.cn/content")
            //.replace("<footer class=\"tc f12 mt20 mb20\"></footer>", "<a href=\"https://cstest.connext.com.cn/frontend/article3\">相关推荐111<a/></br><a href=\"https://cstest.connext.com.cn/frontend/article3\">相关推荐222<a/>")
            .replace("<footer class=\"tc f12 mt20 mb20\"></footer>", a)
            .replace("培训</a>","收藏</a>")
            .replace("href=\"https://bizqa.amwaynet.com.cn/content/china/accl/solr/searchResults.html?category=%E5%9F%B9%E8%AE%AD&categoryType=sourceTag"," style=\"cursor:pointer\" onclick=\"javascript:addSc();")
            ;
        log.info(html);
        response.getWriter().write(html);
    }

    @RequestMapping(value = "/test/article3", method = RequestMethod.GET)
    public void article3(HttpServletResponse response) throws Exception {
        log.info("Begin article3");
        String html = HttpClientUtil.doGet("https://bizqa.amwaynet.com.cn/content/china/accl/ch/training/cloud-school/nutrilite/0038.html");
        html = java.util.regex.Pattern.compile("<script[\\s\\S]*?>[\\s\\S]*?<\\/script>", Pattern.CASE_INSENSITIVE).matcher(html).replaceAll("")
            .replace("class=\"hidden\"", "")
            .replace("<link rel=\"stylesheet\" href=\"/etc/", "<link rel=\"stylesheet\" href=\"https://bizqa.amwaynet.com.cn/etc/")
            .replace("src=\"/content", "src=\"https://bizqa.amwaynet.com.cn/content")
            .replace("<footer class=\"tc f12 mt20 mb20\"></footer>", "<a href=\"https://cstest.connext.com.cn/frontend/test/article2\">相关推荐333<a/>");
        response.getWriter().write(html);
    }

    @RequestMapping(value = "/test/article4", method = RequestMethod.GET)
    public CommonResponseDto article4(String id) {
        log.info("Begin article4");
        log.info("id:{}", id);
        log.info("End article4");
        return CommonResponseDto.ofSuccess();
    }

    @RequestMapping(value = "/test/article5", method = RequestMethod.GET)
    public void article5(HttpServletResponse response) throws Exception {
        log.info("Begin article3");
        String html = HttpClientUtil.doGet("http://ch.amwaynet.com.cn/content/china/accl/ch/training/cloud-school/home-care/0018.html");
        html = java.util.regex.Pattern.compile("<script[\\s\\S]*?>[\\s\\S]*?<\\/script>", Pattern.CASE_INSENSITIVE).matcher(html).replaceAll("")
            .replace("class=\"hidden\"", "")
            .replace("<link rel=\"stylesheet\" href=\"/etc/", "<link rel=\"stylesheet\" href=\"https://ch.amwaynet.com.cn/etc/")
            .replace("src=\"/content", "src=\"https://ch.amwaynet.com.cn/content")
            .replace("src=\"http://ch.amway.com.cn","src=\"https://ch.amway.com.cn")
                    .replace("<footer class=\"tc f12 mt20 mb20\"></footer>", "<a href=\"https://cstest.connext.com.cn/frontend/test/article2\">test<a/>");

        response.getWriter().write(html);
    }

    @RequestMapping(value = "/test/article6", method = RequestMethod.GET)
    public void article6(HttpServletResponse response) throws Exception {
        log.info("Begin article3");
        String html = HttpClientUtil.doGet("http://ch.amwaynet.com.cn/content/china/accl/ch/training/cloud-school/nutrilite/0403.html");
        html = java.util.regex.Pattern.compile("<script[\\s\\S]*?>[\\s\\S]*?<\\/script>", Pattern.CASE_INSENSITIVE).matcher(html).replaceAll("")
            .replace("class=\"hidden\"", "")
            .replace("<link rel=\"stylesheet\" href=\"/etc/", "<link rel=\"stylesheet\" href=\"https://ch.amwaynet.com.cn/etc/")
            .replace("src=\"/content", "src=\"https://ch.amwaynet.com.cn/content")
            .replace("href=\"/content","src=\"https://ch.amwaynet.com.cn/content")
            .replace("src=\"http://ch.amway.com.cn","src=\"https://ch.amway.com.cn")
            .replace("<footer class=\"tc f12 mt20 mb20\"></footer>", "<a href=\"https://cstest.connext.com.cn/frontend/test/article2\">test<a/>")
            .replace("attachmentPath\" src=\"https://ch.amwaynet.com.cn/content","attachmentPath\" href=\"https://ch.amwaynet.com.cn/content");
        log.info(html);
        response.getWriter().write(html);
    }

}
