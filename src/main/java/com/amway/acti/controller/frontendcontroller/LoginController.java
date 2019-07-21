/**
 * Created by dk on 2018/3/13.
 */

package com.amway.acti.controller.frontendcontroller;

import com.amway.acti.base.util.Constants;
import com.amway.acti.base.util.HttpClientUtil;
import com.amway.acti.base.util.HttpUtils;
import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.dto.FrontendTearchLoginDto;
import com.amway.acti.dto.LoginDto;
import com.amway.acti.service.UserService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api
@RestController
@RequestMapping(value = "/frontend/login")
@Slf4j
public class LoginController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/test/redisFlushdb", method = RequestMethod.GET)
    public CommonResponseDto redisFlushdb() {
        try {
            //String str = HttpClientUtil.doGet("https://www.instagram.com/explore/tags/cat?__a=1");

            //String str = HttpClientUtil.doGet("https://www.instagram.com/sechin.du/");
            System.out.println("----------111");
            String str = HttpClientUtil.doGet("https://www.instagram.com/sechin.du");
            System.out.println(str);
            int index0 = str.indexOf("<meta content=");
            String nstr =  str.substring(index0,index0+100);
            System.out.println(nstr);
            int index1 = nstr.indexOf(",");
            int index2 = nstr.indexOf("Following");
            String r = nstr.substring(index1+1,index2).trim();
            System.out.println("-----------------:"+r);
            /*HttpUtils httpUtils = HttpUtils.getInstance();
            httpUtils.setTimeout(30000);
            httpUtils.setWaitForBackgroundJavaScript(30000);
            try {
                String htmlPageStr = httpUtils.getHtmlPageResponse("https://www.instagram.com/sechin.du");
                //TODO
                System.out.println(htmlPageStr);
            } catch (Exception e) {
                e.printStackTrace();
            }*/

            //String str = HttpClientUtil.sendGet("https://www.instagram.com/sechin.du/",null);

        }catch (Exception ex){
            log.error(ex.getMessage(),ex);
        }


       // userService.test(name);
        return null;
    }

    @RequestMapping(value = "/aa", method = RequestMethod.GET)
    public void aa(){
        try {
            //String str = HttpClientUtil.sendGet("https://www.instagram.com/explore/tags/cat?__a=1",null);
            //String str =HttpClientUtil.doGetIgnoreVerifySSL("https://www.instagram.com/explore/tags/cat?__a=1");
            //System.out.println("str:"+str);

            HttpUtils httpUtils = HttpUtils.getInstance();
            httpUtils.setTimeout(30000);
            httpUtils.setWaitForBackgroundJavaScript(30000);
            try {
                String htmlPageStr = httpUtils.getHtmlPageResponse("https://www.instagram.com/sechin.du");
                //TODO
                System.out.println(htmlPageStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }catch (Exception ex){
            log.error(ex.getMessage(),ex);
        }
    }

    /**
     * 首次登录判断
     */
    @RequestMapping(value = "/checkFirstLogin", method = RequestMethod.POST)
    public CommonResponseDto checkFirstLogin(@RequestParam String token) {
        if (StringUtils.isEmpty(userService.checkFirstLogin(token))) {
            log.error("token is null, please log in again!");
            return CommonResponseDto.ofFailure(Constants.ErrorCode.ERROR_ACCOUNT_OR_PAS, "请登录");
        }
        return CommonResponseDto.ofSuccess();
    }

    /**
     * 讲师首次登录
     */
    @RequestMapping(value = "/teacherLogin", method = RequestMethod.POST)
    public CommonResponseDto teacherLogin(@RequestBody FrontendTearchLoginDto dto) {
        return CommonResponseDto.ofSuccess(userService.teacherLogin(dto));
    }

    /**
     * 学员首次登录
     */
    @RequestMapping(value = "/stuLogin", method = RequestMethod.POST)
    public CommonResponseDto stuLogin(@RequestBody LoginDto dto) {
        return CommonResponseDto.ofSuccess(userService.stuLogin(dto));
    }

    /**
     * 选择性别
     */
    @RequestMapping(value = "/confirmSex", method = RequestMethod.POST)
    public CommonResponseDto confirmSex(@RequestParam String sex, @RequestParam String token) {
        return CommonResponseDto.ofSuccess(userService.confirmSex(token, sex));
    }


        public static void main(String[] args) {
            String a="OO123456-01";
            System.out.println(a.substring(0,a.indexOf("-")));
    }


}
