/**
 * Created by dk on 2018/2/21.
 */

package com.amway.acti.base.filter;

import com.amway.acti.base.context.MiniProgramRequestContextHolder;
import com.amway.acti.base.util.Constants;
import com.amway.acti.base.jwt.JwtTokenFactory;
import com.amway.acti.base.util.SpringContextUtil;
import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.model.MiniProgramRequest;
import com.amway.acti.model.User;
import com.amway.acti.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Slf4j
public class UserAuthInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {
        if(1==1){
            return true;
        }
        String url = request.getRequestURL().toString();
        log.info("UserAuthInterceptor.url：{}", url);
        //排除不需要拦截的URL
        if (excludePathPatterns(url)) {
            return true;
        }
        if (url.contains(Constants.FRONTEND_URL)) {
            return validateAuthorization(request, response);
        } else if (url.contains(Constants.BACKEND_URL)) {
            response.setHeader("Set-Cookie", setCookieSecure(request));
            if (url.contains(Constants.BACKEND_LOGIN)) {
                return true;
            }
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute(Constants.BACKEND_USER_SESSSION_KEY);
            if (user == null) {
                response.sendRedirect(request.getContextPath() + Constants.REDIRECT_LOGIN);
                return false;
            }
            return true;
        } else {
            response.sendRedirect(request.getContextPath() + Constants.REDIRECT_LOGIN);
            //writeResponse(response, Constants.ErrorCode.AUTHORIZATION_05, "Invalid access!");
            return false;
        }
    }

    /**
     * 令牌身份验证
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */

    private boolean validateAuthorization(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String token = request.getHeader(Constants.HEADER_STRING);
        log.info("authinterceptor token:{}", token);
        if (!StringUtils.hasText(token)) {
            writeResponse(response, Constants.ErrorCode.AUTHORIZATION_01, Constants.INTERCEPTOR_AUTHORIZATION_01);
            return false;
        }
        if (!token.startsWith(Constants.TOKEN_PREFIX)) {
            writeResponse(response, Constants.ErrorCode.AUTHORIZATION_02, Constants.INTERCEPTOR_AUTHORIZATION_02);
            return false;
        }
        try {
            MiniProgramRequest miniProgramRequest = getMiniProgramRequestModel(token);
            MiniProgramRequestContextHolder.setRequestUser(miniProgramRequest);
            return validateLecturer(miniProgramRequest.getIdent(),
                miniProgramRequest.getEmail(),
                miniProgramRequest.getPwd(), response);
        } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException | SignatureException | NullPointerException ex) {
            log.error(ex.getMessage(), ex);
            writeResponse(response, Constants.ErrorCode.AUTHORIZATION_03, Constants.INTERCEPTOR_AUTHORIZATION_03);
            return false;
        } catch (ExpiredJwtException ex) {
            log.error(ex.getMessage(), ex);
            writeResponse(response, Constants.ErrorCode.AUTHORIZATION_04, Constants.INTERCEPTOR_AUTHORIZATION_04);
            return false;
        }
    }

    /**
     * out json
     *
     * @param response
     * @param code
     * @param message
     * @throws Exception
     */

    private void writeResponse(HttpServletResponse response, String code, String message) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType(Constants.INTERCEPTOR_CONTENTTYPE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ObjectMapper mapper = new ObjectMapper();
        CommonResponseDto rsp = new CommonResponseDto();
        rsp.setCode(code);
        rsp.setMessage(message);
        response.getWriter().write(mapper.writeValueAsString(rsp));
    }


    /**
     * setCookie Secure
     * @param request
     * @return
     */
    private String setCookieSecure(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        StringBuilder builder = null;
        if (cookies != null) {
            Cookie cookie = cookies[0];
            if (cookie != null) {
                String value = cookie.getValue();
                builder = new StringBuilder();
                builder.append("JSESSIONID=" + value + "; ");
                builder.append("Secure; ");
                builder.append("HttpOnly; ");
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.HOUR, 1);
                Date date = cal.getTime();
                Locale locale = Locale.CHINA;
                SimpleDateFormat sdf =
                    new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", locale);
                builder.append("Expires=" + sdf.format(date));
                return builder.toString();
            }
        }
        return null;
    }

    /***
     * 讲师登录验证邮箱或密码错误
     * @param ident
     * @param email
     * @param password
     * @param response
     * @return
     * @throws IOException
     */

    private boolean validateLecturer(short ident, String email, String password, HttpServletResponse response) throws IOException {
        if (Constants.USER_IDENT_LECTURER == ident) {
            UserService userService = SpringContextUtil.getBean(UserService.class);
            User user = userService.getUserByEmailAndPwd(email, password);
            if (null == user) {
                log.info("lecturer email or password is error");
                writeResponse(response, Constants.ErrorCode.AUTHORIZATION_06, Constants.ERROR_MESSAGE_AUTHORIZATION_06);
                return false;
            }
        }
        return true;
    }

    /**
     * 封装request token信息
     * @param token
     * @return
     */
    private MiniProgramRequest getMiniProgramRequestModel(String token){
        JwtTokenFactory factory = SpringContextUtil.getBean(JwtTokenFactory.class);
        Jws<Claims> claimsJws = factory.parseClaims(token);

        MiniProgramRequest miniProgramRequest = new MiniProgramRequest();
        miniProgramRequest.setUid((Integer) claimsJws.getBody().get(Constants.UID));
        log.info("authinterceptor uid:{}",claimsJws.getBody().get(Constants.UID));
        //miniProgramRequest.setOpenId((String) claimsJws.getBody().get(Constants.OPENID));
        //miniProgramRequest.setUnionId((String) claimsJws.getBody().get(Constants.UNIONID);
        if(!StringUtils.isEmpty(claimsJws.getBody().get(Constants.EMAIL))){
            miniProgramRequest.setEmail(claimsJws.getBody().get(Constants.EMAIL).toString());
        }
        if(!StringUtils.isEmpty(claimsJws.getBody().get(Constants.TPASSWORD))){
            miniProgramRequest.setPwd(claimsJws.getBody().get(Constants.TPASSWORD).toString());
        }
        miniProgramRequest.setIdent(Short.parseShort(claimsJws.getBody().get(Constants.IDENT).toString()));
        return miniProgramRequest;
    }

    /**
     * 排除url
     * @param requestUrl
     * @return
     */
    private boolean excludePathPatterns(String requestUrl){
        for (String url: unUrlFilterList) {
            if(requestUrl.contains(url)){
                return true;
            }
        }
        return false;
    }

    public static List<String> unUrlFilterList = null;
    static {
        unUrlFilterList = new ArrayList<>();
        unUrlFilterList.add(Constants.excludePathPatterns_0);
        unUrlFilterList.add(Constants.excludePathPatterns_1);
        unUrlFilterList.add(Constants.excludePathPatterns_2);
        unUrlFilterList.add(Constants.excludePathPatterns_3);
        unUrlFilterList.add(Constants.excludePathPatterns_4);
        unUrlFilterList.add(Constants.excludePathPatterns_5);
        unUrlFilterList.add(Constants.excludePathPatterns_6);

        unUrlFilterList.add(Constants.FRONTEND_LOGIN_URL);
        unUrlFilterList.add(Constants.FRONTEND_BIND_URL);
        unUrlFilterList.add(Constants.CHECKFIRSTLOGIN_URL);
    }

}

