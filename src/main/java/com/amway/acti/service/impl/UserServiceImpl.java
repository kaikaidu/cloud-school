/**
 * Created by dk on 2018/2/11.
 */

package com.amway.acti.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.amway.acti.base.exception.AmwayLogicException;
import com.amway.acti.base.exception.AmwaySystemException;
import com.amway.acti.base.jwt.JwtTokenFactory;
import com.amway.acti.base.property.ObsAboInfoProperty;
import com.amway.acti.base.property.StuloginProperty;
import com.amway.acti.base.util.Constants;
import com.amway.acti.base.util.Constants.ErrorCode;
import com.amway.acti.base.util.EncrypDES;
import com.amway.acti.base.util.ExcelImportUtils;
import com.amway.acti.base.util.HttpClientUtil;
import com.amway.acti.base.util.Pkcs7Encoder;
import com.amway.acti.base.util.SHA256EncodeUtil;
import com.amway.acti.dao.CourseSignUpMapper;
import com.amway.acti.dao.UserAnswerMapper;
import com.amway.acti.dao.UserMapper;
import com.amway.acti.dao.UserMarkMapper;
import com.amway.acti.dto.FrontendTearchLoginDto;
import com.amway.acti.dto.LoginDto;
import com.amway.acti.dto.StuloginResponseDto;
import com.amway.acti.model.AboInfo;
import com.amway.acti.model.AccessJwtTokenRequest;
import com.amway.acti.model.CourseSignUp;
import com.amway.acti.model.EncryptedData;
import com.amway.acti.model.OsbAboInfo;
import com.amway.acti.model.OsbAboInfoConfig;
import com.amway.acti.model.TestDk;
import com.amway.acti.model.User;
import com.amway.acti.model.UserAnswer;
import com.amway.acti.model.UserMark;
import com.amway.acti.service.RedisService;
import com.amway.acti.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CourseSignUpMapper courseSignUpMapper;



    @Autowired
    private JwtTokenFactory jwtTokenFactory;

    @Autowired
    private StuloginProperty stuloginProperty;

    @Autowired
    private ObsAboInfoProperty obsAboInfoProperty;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisService redisService;

    private static final String LOGIN_ERROR_MESSAGE = "登录失败，请重试!";
    private static final String LOGIN_ERROR_MESSAGE_07 = "登录失败，请进入 \"安利云服务\" 公众号进行初次登录绑定!";
    private static final String LOGIN_ERROR_MESSAGE_40163 = "请重新进入小程序并进行登录!";

    /**
     * 校验手机号
     */
    public static final String REGEX_MOBILE = "^((17[0-9])|(14[0-9])|(13[0-9])|(15[^4,\\D])|(147)|(166)|(198)|(199)|(18[0,2,3,5-9]))\\d{8}$";

    @Value("${druid.pwd}")
    private String druidPwd;

    @Override
    public String flushDB(String name) {
        if (!druidPwd.equals(name)) {
            return null;
        }
        return (String) redisTemplate.execute(new RedisCallback() {
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                connection.flushDb();
                return "ok";
            }
        });
    }

    /*@Override
    public String getToken(AccessJwtTokenRequest accessJwtTokenRequest) {
        String token = jwtTokenFactory.createAccessJwtToken(accessJwtTokenRequest).getToken();
        return token;
    }*/

    /**
     * 判断是否已登录
     *
     * @param token
     * @return
     */
    @Override
    public User checkFirstLogin(String token) {
        User user = null;
        Jws<Claims> claimsJws = null;
        try {
            claimsJws = jwtTokenFactory.parseClaims(token);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
        Integer uid = (Integer) claimsJws.getBody().get(Constants.UID);
        Integer ident = (Integer) claimsJws.getBody().get(Constants.IDENT);
        log.info("uid:{}", uid);
        log.info("ident:{}", ident);
        if (Constants.USER_IDENT_STUDENT == ident.shortValue()) {
            user = userMapper.selectByPrimaryKey(uid);
            if (user != null) {
                User owbUser = getOsbUserByFirstLogin(user.getName(), user.getSsoAdanumber());
                if (null != owbUser) {
                    if (user.getSex() != owbUser.getSex()) {
                        String adaStr = this.getAdainfoMd5(user.getSsoAdanumber(), owbUser.getSex());
                        courseSignUpMapper.updateAdainfoMd5(user.getAdainfoMd5(), adaStr);
                        user.setAdainfoMd5(adaStr);
                        user.setSex(owbUser.getSex());
                    }
                    user.setName(owbUser.getName());
                    user.setAge(owbUser.getAge());
                    user.setAreaCode(owbUser.getAreaCode());
                    user.setVocationalLevel(owbUser.getVocationalLevel());
                    user.setShop(owbUser.getShop());
                    user.setShopProvince(owbUser.getShopProvince());
                    user.setShopCity(owbUser.getShopCity());
                    user.setShopProvinceCode(owbUser.getShopProvinceCode());
                    user.setShopCityCode(owbUser.getShopCityCode());
                    userMapper.updateByPrimaryKeySelective(user);
                }
            }
        } else if (Constants.USER_IDENT_LECTURER == ident.shortValue()) {
            user = userMapper.selectByEmailAndPassword((String) claimsJws.getBody().get(Constants.EMAIL),
                (String) claimsJws.getBody().get(Constants.TPASSWORD));
        }
        return user;
    }

    /***
     * 讲师登录
     * @param dto
     * @return
     */
    @Override
    public String teacherLogin(FrontendTearchLoginDto dto) {
        String password = null;
        try {
            password = EncrypDES.getInstance().setEncString(dto.getPassword());
        } catch (Exception ex) {
            throw new AmwaySystemException(Constants.ErrorCode.SYSTEM_EXCEPTION, ex);
        }
        //add qj
        User u = userMapper.checkByEmail(dto.getEmail());
        if (ObjectUtils.isEmpty(u)) {
            throw new AmwayLogicException(Constants.ErrorCode.ERROR_ACCOUNT_OR_PAS, "邮箱不存在");
        }
        Integer num = Integer.parseInt(redisTemplate.opsForValue().get(dto.getEmail()) == null ? "0" : redisTemplate.opsForValue().get(dto.getEmail()).toString());
        if (num > 5) {
            throw new AmwayLogicException(Constants.ErrorCode.ERROR_ACCOUNT_OR_PAS, "密码错误已达六次,您的账号将被锁定,如需解锁,请联系管理员");
        }
        log.info("password after encode:{}", password);
        User user = userMapper.selectByEmailAndPassword(dto.getEmail(), password);
        if (ObjectUtils.isEmpty(user)) {
            num += 1;
            //后期需要添加失效期的话可以在此处添加
            redisTemplate.opsForValue().set(dto.getEmail(), num);
            throw new AmwayLogicException(ErrorCode.ERROR_ACCOUNT_OR_PAS, "密码错误");
        }

        userMapper.updateByPrimaryKeySelective(user);
        AccessJwtTokenRequest accessJwtTokenRequest = new AccessJwtTokenRequest();
        accessJwtTokenRequest.setUid(user.getId());
        accessJwtTokenRequest.setIdent(user.getIdent());
        accessJwtTokenRequest.setEmail(user.getEmail());
        accessJwtTokenRequest.setTpassword(user.getPassword());
        log.info("accessJwtTokenRequest:{}", accessJwtTokenRequest.toString());

        String token = jwtTokenFactory.createAccessJwtToken(accessJwtTokenRequest).getToken();
        log.info("token:{}", token);
        log.info("teacher login success.");
        //成功后将记录清空 qj
        redisTemplate.delete(dto.getEmail());
        return token;
    }

    /***
     * 学员登录
     * @param dto
     * @return
     */
    @Override
    public StuloginResponseDto stuLogin(LoginDto dto) {
        // 1.根据code从微信开发平台取sessionKey(会话密钥)
        String sessionKey = getSessionKey(dto.getCode());
        if (sessionKey == null) {
            log.error("sessionKey获取错误");
            throw new AmwayLogicException(ErrorCode.GET_SESSIONKEY_ERROR, LOGIN_ERROR_MESSAGE);
        }
        log.info("sessionKey:{}", sessionKey);
        // 2.根据sessionKey对encryptedData进行解密
        EncryptedData encryptedData = decrypt(dto.getEncryptedData(), sessionKey, dto.getIv());
        if (encryptedData == null || StringUtils.isEmpty(encryptedData.getUnionId())) {
            log.error("EncryptedData解密错误");
            throw new AmwayLogicException(ErrorCode.ENCRYPTEDDATA_ERROR, LOGIN_ERROR_MESSAGE);
        }
        log.info("encryptedData:{}", encryptedData.toString());
        AboInfo aboInfo = null;
        String amToken = null;
        log.info("isPro:{}", stuloginProperty.getIsProd());
        // 3.获取安利微信框架token
        // 4.根据token和unionId获取Abo信息
        if ("true".equals(stuloginProperty.getIsProd())) {
            amToken = getAmwayToken(stuloginProperty.getProdGetTokenUrl(), stuloginProperty.getComponentProdAppid(), stuloginProperty.getComponentProdSecret());
            aboInfo = getAboInfo(stuloginProperty.getProdGetAdaInfoUrl(), amToken, encryptedData.getUnionId());
        } else {
            amToken = getAmwayToken(stuloginProperty.getQaGetTokenUrl(), stuloginProperty.getComponentQaAppid(), stuloginProperty.getComponentQaSecret());
            aboInfo = getAboInfo(stuloginProperty.getQaGetAdaInfoUrl(), amToken, encryptedData.getUnionId());
        }
        if ("07".equals(aboInfo.getCode())) {
            throw new AmwayLogicException(Constants.ErrorCode.GET_ABOINFO_ERROR, LOGIN_ERROR_MESSAGE_07);
        }
        if (!"1".equals(aboInfo.getCode())) {
            throw new AmwayLogicException(Constants.ErrorCode.GET_ABOINFO_ERROR, LOGIN_ERROR_MESSAGE);
        }
        log.info("amway token:{}", amToken);
        log.info("amway aboInfo:{}", aboInfo.toString());
        OsbAboInfo osbAboInfo = getObsAboInfo(aboInfo.getAda());
        if (null == osbAboInfo) {
            throw new AmwayLogicException(Constants.ErrorCode.GET_ABOINFO_ERROR, LOGIN_ERROR_MESSAGE);
        }
        StuloginResponseDto response = null;
        // 夫妻帐号，需要选择性别
        if (!StringUtils.isEmpty(osbAboInfo.getMainName())
            && !StringUtils.isEmpty(osbAboInfo.getSpouseName())) {
            response = new StuloginResponseDto();
            AccessJwtTokenRequest accessJwtTokenRequest = new AccessJwtTokenRequest();
            accessJwtTokenRequest.setOpenId(encryptedData.getOpenId());
            accessJwtTokenRequest.setUnionId(encryptedData.getUnionId());
            accessJwtTokenRequest.setEmail(aboInfo.getAda());
            //accessJwtTokenRequest.setTpassword(aboInfo.getMaskname());
            accessJwtTokenRequest.setIdent(Constants.USER_IDENT_STUDENT);
            // isSpouse=1代表是双性别，小程序端需选择性别
            response.setIsSpouse("1");
            // 临时token封装登录所需必要信息到选择性别时使用
            response.setToken(jwtTokenFactory.createAccessJwtToken(accessJwtTokenRequest).getToken());
            log.info("accessJwtTokenRequest:{}", accessJwtTokenRequest.toString());
            return response;
        }
        User osbUser = getOsbUser(osbAboInfo, aboInfo.getSex(), aboInfo.getAda(), false);
        String adaStr = this.getAdainfoMd5(aboInfo.getAda(), osbUser.getSex());
        log.info("adaStr:{}",adaStr);
        User user = null;
        String key = Constants.SAFETYLOCK_STULOGIN + adaStr;
        try {
            while (!redisService.setNx0(key, adaStr)) {
                log.info("同一个用户登录尚未获取到锁:{}", adaStr);
            }
            user = userMapper.selectByAdaEncode(adaStr);
            // 5.根据用户性别和AboInfo的ADA卡号调用OSB接口并更新User对象,最终保存
            user = saveUser(user, adaStr, osbUser, encryptedData);
        } catch (Exception ex) {
            redisService.delete(key);
            log.error(ex.getMessage(), ex);
            throw new AmwayLogicException(ErrorCode.GET_SESSIONKEY_ERROR, LOGIN_ERROR_MESSAGE);
        } finally {
            redisService.delete(key);
        }
        AccessJwtTokenRequest accessJwtTokenRequest = new AccessJwtTokenRequest();
        accessJwtTokenRequest.setOpenId(encryptedData.getOpenId());
        accessJwtTokenRequest.setUnionId(encryptedData.getUnionId());
        accessJwtTokenRequest.setUid(user.getId());
        accessJwtTokenRequest.setIdent(user.getIdent());
        // 6.登录成功返回用户令牌
        String token = jwtTokenFactory.createAccessJwtToken(accessJwtTokenRequest).getToken();
        response = new StuloginResponseDto();
        response.setIsSpouse("0");
        response.setToken(token);
        log.info("stuResponse:{}", response.toString());
        log.info("student login success.");
        return response;
    }

    /***
     * 选择性别
     * @param tempToken
     * @param sex
     */
    @Override
    public StuloginResponseDto confirmSex(String tempToken, String sex) {
        Jws<Claims> claimsJws = null;
        try {
            claimsJws = jwtTokenFactory.parseClaims(tempToken);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        String openId = (String) claimsJws.getBody().get(Constants.OPENID);
        String unionId = (String) claimsJws.getBody().get(Constants.UNIONID);
        String adaNumber = (String) claimsJws.getBody().get(Constants.EMAIL);
        String adaStr = this.getAdainfoMd5(adaNumber, Short.parseShort(sex));

        //封装Abo对象
        AboInfo aboInfo = new AboInfo();
        aboInfo.setSex(Short.parseShort(sex));
        aboInfo.setAda(adaNumber);
        //封装encryptedData对象
        EncryptedData encryptedData = new EncryptedData();
        encryptedData.setUnionId(unionId);
        encryptedData.setOpenId(openId);
        OsbAboInfo osbAboInfo = getObsAboInfo(aboInfo.getAda());
        if (null == osbAboInfo) {
            throw new AmwayLogicException(Constants.ErrorCode.GET_ABOINFO_ERROR, LOGIN_ERROR_MESSAGE);
        }
        User osbUser = getOsbUser(osbAboInfo, aboInfo.getSex(), aboInfo.getAda(), true);
        User user = null;
        String key = Constants.SAFETYLOCK_STUCONFIRMSEX + adaStr;
        try {
            while (!redisService.setNx0(key, adaStr)) {
                log.info("同一个用户选择性别尚未获取到锁:{}", adaStr);
            }
            user = userMapper.selectByAdaEncode(adaStr);
            // 根据OSB接口更新Abo信息并保存User对象
            user = saveUser(user, adaStr, osbUser, encryptedData);
            log.info("save user model:{}", user.toString());
        } catch (Exception ex) {
            redisService.delete(key);
            log.error(ex.getMessage(), ex);
            throw new AmwayLogicException(ErrorCode.GET_SESSIONKEY_ERROR, LOGIN_ERROR_MESSAGE);
        } finally {
            redisService.delete(key);
        }

        String token = "";
        if (user != null) {
            AccessJwtTokenRequest accessJwtTokenRequest = new AccessJwtTokenRequest();
            accessJwtTokenRequest.setOpenId(encryptedData.getOpenId());
            accessJwtTokenRequest.setUnionId(encryptedData.getUnionId());
            accessJwtTokenRequest.setUid(user.getId());
            accessJwtTokenRequest.setIdent(user.getIdent());
            log.info("accessJwtTokenRequest:{}", accessJwtTokenRequest.toString());
            token = jwtTokenFactory.createAccessJwtToken(accessJwtTokenRequest).getToken();
        }
        // 性别选择成功，返回用户令牌
        StuloginResponseDto response = new StuloginResponseDto();
        response.setToken(token);
        log.info("stuResponse:{}", response.toString());
        log.info("spouse student login success.");
        return response;
    }

    /***
     * 从oBS 取abo信息（EBS时时更新的）
     * @return
     */
    @Override
    public OsbAboInfo getObsAboInfo(String adaNumber) {
        log.info("getObsAboInfo adaNumber:{}", adaNumber);
        OsbAboInfoConfig osbAboInfoConfig = getOsbAboInfo();
        OsbAboInfo osbAboInfo = null;
        try {
            String param = "appid=" + osbAboInfoConfig.getOsbappid()
                + "&appkey=" + osbAboInfoConfig.getOsbappkey()
                + "&service=" + osbAboInfoConfig.getService()
                + "&operation=" + osbAboInfoConfig.getOperation() + "&data="
                + "{ada:" + adaNumber + "}&version="
                + osbAboInfoConfig.getVersion()
                + "&mode=" + osbAboInfoConfig.getMode();
            log.info(param);
            String jStr = HttpClientUtil.sendPost(osbAboInfoConfig.getUrl(), param);
            log.info("jStr:{}", jStr);
            if("{\"returnCode\":\"NO_RECORD\"}".equals(jStr)){
                osbAboInfo = new OsbAboInfo();
                osbAboInfo.setResultCode("NO_RECORD");
                return osbAboInfo;
            }
            JSONObject obj = JSON.parseObject(jStr);
            if (null != obj) {
                if (!StringUtils.isEmpty(obj.get("returnCode"))) {
                    if ("0".equals(obj.get("returnCode").toString())) {
                        JSONObject jsonstr = JSON.parseObject(obj.get("data").toString());
                        osbAboInfo = new OsbAboInfo();
                        osbAboInfo.setAda(jsonstr.get("ada").toString());
                        osbAboInfo.setMainAge(jsonstr.get("mainAge") == null ? "" : jsonstr.get("mainAge").toString());
                        osbAboInfo.setMainName(jsonstr.get("mainName") == null ? "" : jsonstr.get("mainName").toString());
                        osbAboInfo.setMainSex(jsonstr.get("mainSex") == null ? "" : jsonstr.get("mainSex").toString());
                        osbAboInfo.setSpouseSex(jsonstr.get("spouseSex") == null ? "" : jsonstr.get("spouseSex").toString());
                        osbAboInfo.setSpouseAge(jsonstr.get("spouseAge") == null ? "" : jsonstr.get("spouseAge").toString());
                        osbAboInfo.setSpouseName(jsonstr.get("spouseName") == null ? "" : jsonstr.get("spouseName").toString());

                        osbAboInfo.setProvinceName(jsonstr.get("provinceName") == null ? "" : jsonstr.get("provinceName").toString());
                        osbAboInfo.setPinLvl(jsonstr.get("pinLvl") == null ? "" : jsonstr.get("pinLvl").toString());
                        osbAboInfo.setAreaCode(jsonstr.get("areaCode") == null ? "" : jsonstr.get("areaCode").toString());
                        osbAboInfo.setShopName(jsonstr.get("shopName") == null ? "" : jsonstr.get("shopName").toString());
                        osbAboInfo.setCityName(jsonstr.get("cityName") == null ? "" : jsonstr.get("cityName").toString());
                        osbAboInfo.setProvinceCode(jsonstr.get("provinceCode") == null ? "" : jsonstr.get("provinceCode").toString());
                        osbAboInfo.setCityCode(jsonstr.get("cityCode") == null ? "" : jsonstr.get("cityCode").toString());
                        osbAboInfo.setCityCode(jsonstr.get("returnCode") == null ? "" : jsonstr.get("returnCode").toString());
                    }
                }
            }
            log.info("osbAboInfo model-d:{}", osbAboInfo.toString());
            return osbAboInfo;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            log.error("get ObsAboInfo error!");
            throw new AmwaySystemException(LOGIN_ERROR_MESSAGE);
        }
    }

    /**
     * 取配置信息
     *
     * @return
     */
    private OsbAboInfoConfig getOsbAboInfo() {
        OsbAboInfoConfig osbAboInfo = null;
        if ("true".equals(stuloginProperty.getIsProd())) {
            osbAboInfo = new OsbAboInfoConfig();
            osbAboInfo.setOsbappid(obsAboInfoProperty.getProdosbappid());
            osbAboInfo.setOsbappkey(obsAboInfoProperty.getProdosbappkey());
            osbAboInfo.setService(obsAboInfoProperty.getProdservice());
            osbAboInfo.setOperation(obsAboInfoProperty.getProdoperation());
            osbAboInfo.setVersion(obsAboInfoProperty.getProdversion());
            osbAboInfo.setMode(obsAboInfoProperty.getProdmode());
            osbAboInfo.setUrl(obsAboInfoProperty.getProdurl());
        } else {
            osbAboInfo = new OsbAboInfoConfig();
            osbAboInfo.setOsbappid(obsAboInfoProperty.getQaosbappid());
            osbAboInfo.setOsbappkey(obsAboInfoProperty.getQaosbappkey());
            osbAboInfo.setService(obsAboInfoProperty.getQaservice());
            osbAboInfo.setOperation(obsAboInfoProperty.getQaoperation());
            osbAboInfo.setVersion(obsAboInfoProperty.getQaversion());
            osbAboInfo.setMode(obsAboInfoProperty.getQamode());
            osbAboInfo.setUrl(obsAboInfoProperty.getQaurl());
        }
        return osbAboInfo;
    }


    /**
     * 微信平台获取sessionKey
     *
     * @param code
     * @return
     */
    private String getSessionKey(String code) {
        log.info("getSessionKey code:{}", code);
        String url = stuloginProperty.getGetSessionKeyUrl() + "?appid=" + stuloginProperty.getAppid() + "&secret=" + stuloginProperty.getSecret() + "&js_code=" + code + "&grant_type=" + Constants.WX_GRANT_TYPE;
        log.info("getSessionKey url:{}", url);
        String jsonStr = null;
        try {
            jsonStr = HttpClientUtil.doGetIgnoreVerifySSL(url);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new AmwayLogicException(Constants.ErrorCode.GET_ABOINFO_ERROR, LOGIN_ERROR_MESSAGE);
        }
        log.info("sessionKeyStr:{}", jsonStr);
        if (jsonStr.contains("code been used")) {
            throw new AmwayLogicException(Constants.ErrorCode.GET_ABOINFO_ERROR, LOGIN_ERROR_MESSAGE_40163);
        }
        JSONObject ojb = JSON.parseObject(jsonStr);
        return ojb.get("session_key").toString();
    }

    /**
     * 解密后取得openId与unionId
     *
     * @param encryptedData
     * @param sessionKey
     * @param iv
     * @return
     */
    private EncryptedData decrypt(String encryptedData, String sessionKey, String iv) {
        log.info("decrypt encryptedData:{}", encryptedData);
        log.info("decrypt sessionKey:{}", sessionKey);
        log.info("decrypt iv:{}", iv);
        EncryptedData encryptedData0 = null;
        try {
            byte[] encryptedDataBy = Base64.decodeBase64(encryptedData);
            byte[] sessionKeyBy = Base64.decodeBase64(sessionKey);
            byte[] ivBy = Base64.decodeBase64(iv);
            log.info("decrypt encryptedData_base64_length:{}", encryptedDataBy.length);
            log.info("decrypt sessionKey_base64_length:{}", sessionKeyBy.length);
            log.info("decrypt iv_base64_length:{}", ivBy.length);

            // 对加密数据( encryptedData )进行对称解密
            byte[] dec = Pkcs7Encoder.decryptOfDiyIV(encryptedDataBy, sessionKeyBy, ivBy);
            JSONObject object = JSON.parseObject(new String(dec));
            log.info("decryptData:{}", object.toString());
            encryptedData0 = new EncryptedData();
            encryptedData0.setOpenId(object.get("openId").toString());
            encryptedData0.setUnionId(object.get("unionId").toString());
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new AmwaySystemException(LOGIN_ERROR_MESSAGE);
        }
        return encryptedData0;
    }


    /***
     * 取amway token
     * @param url
     * @param appId
     * @param secret
     * @return
     */
    private String getAmwayToken(String url, String appId, String secret) {
        try {
            String url0 = url + "?appId=" + appId + "&appSecret=" + secret;
            log.info("getAmwayToken url0:{}", url0);
            String jsonStr = HttpClientUtil.doGetIgnoreVerifySSL(url0);
            log.info("amwayToken:{}", jsonStr);
            JSONObject ojb = JSON.parseObject(jsonStr);
            if ("1".equals(ojb.get("status"))) {
                return ojb.get("token").toString();
            }
            return null;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            log.error("get amway token error!");
            throw new AmwaySystemException(LOGIN_ERROR_MESSAGE);
        }
    }

    /**
     * 保存或更新用户
     *
     * @param user
     * @return
     */
    private User saveUser(User user, String adaStr, User osbUser, EncryptedData encryptedData) {
        if (user == null) {
            user = new User();
            user.setIdent(Constants.USER_IDENT_STUDENT);
            user.setUnionId(encryptedData.getUnionId());
            user.setOpenId(encryptedData.getOpenId());
            user.setAdainfoMd5(adaStr);
            user.setState(Constants.USER_IDENT_STUDENT);
            user.setCreateTime(new Date(System.currentTimeMillis()));
            if (null != osbUser) {
                user.setName(osbUser.getName());
                user.setAdaNumber(this.completion(osbUser.getSsoAdanumber(), osbUser.getSex()));
                user.setSsoAdanumber(osbUser.getSsoAdanumber());
                user.setSex(osbUser.getSex());
                user.setAge(osbUser.getAge());
                user.setAreaCode(osbUser.getAreaCode());
                user.setVocationalLevel(osbUser.getVocationalLevel());
                user.setShop(osbUser.getShop());
                user.setShopProvince(osbUser.getShopProvince());
                user.setShopCity(osbUser.getShopCity());
                user.setShopProvinceCode(osbUser.getShopProvinceCode());
                user.setShopCityCode(osbUser.getShopCityCode());
            }
            userMapper.insertSelective(user);
            log.info("saveUser user:{}", user.toString());
        } else {
            user.setOpenId(encryptedData.getOpenId());
            user.setUnionId(encryptedData.getUnionId());
            if (null != osbUser) {
                user.setName(osbUser.getName());
                user.setAge(osbUser.getAge());
                user.setAreaCode(osbUser.getAreaCode());
                user.setVocationalLevel(osbUser.getVocationalLevel());
                user.setShop(osbUser.getShop());
                user.setShopProvince(osbUser.getShopProvince());
                user.setShopCity(osbUser.getShopCity());
                user.setShopProvinceCode(osbUser.getShopProvinceCode());
                user.setShopCityCode(osbUser.getShopCityCode());
            }
            userMapper.updateByPrimaryKeySelective(user);
            log.info("updateUser user:{}", user.toString());
        }
        return user;
    }


    /***
     * 获取osb更新的user
     * @param sex
     * @param adaNumber
     * @return
     */
    private User getOsbUser(OsbAboInfo osbAboInfo, Short sex, String adaNumber, boolean isSelectSex) {
        log.info("getOsbUser sex:{}", sex);
        log.info("getOsbUser adaNumber:{}", adaNumber);
        //OsbAboInfo osbAboInfo = getObsAboInfo(adaNumber);
        if (null == osbAboInfo) {
            throw new AmwayLogicException(Constants.ErrorCode.SOAMEMBERINFO_NULL, LOGIN_ERROR_MESSAGE);
        }
        User user = new User();
        if (isSelectSex) {
            String choiceSex = (sex == 0 ? "M" : "F");
            if (choiceSex.equals(osbAboInfo.getMainSex())) {
                user.setName(osbAboInfo.getMainName());
                user.setAge(Short.parseShort(osbAboInfo.getMainAge()));
                if ("M".equals(osbAboInfo.getMainSex())) {
                    user.setSex((short) 0);
                } else if ("F".equals(osbAboInfo.getMainSex())) {
                    user.setSex((short) 1);
                }
            } else if (choiceSex.equals(osbAboInfo.getSpouseSex())) {
                user.setName(osbAboInfo.getSpouseName());
                user.setAge(Short.parseShort(osbAboInfo.getSpouseAge()));
                if ("M".equals(osbAboInfo.getSpouseSex())) {
                    user.setSex((short) 0);
                } else if ("F".equals(osbAboInfo.getSpouseSex())) {
                    user.setSex((short) 1);
                }
            }
        } else {
            if (!StringUtils.isEmpty(osbAboInfo.getMainSex())) {
                user.setName(osbAboInfo.getMainName());
                user.setAge(Short.parseShort(osbAboInfo.getMainAge()));
                if ("M".equals(osbAboInfo.getMainSex())) {
                    user.setSex((short) 0);
                } else if ("F".equals(osbAboInfo.getMainSex())) {
                    user.setSex((short) 1);
                }
            } else if (!StringUtils.isEmpty(osbAboInfo.getSpouseSex())) {
                user.setName(osbAboInfo.getSpouseName());
                user.setAge(Short.parseShort(osbAboInfo.getSpouseAge()));
                if ("M".equals(osbAboInfo.getSpouseSex())) {
                    user.setSex((short) 0);
                } else if ("F".equals(osbAboInfo.getSpouseSex())) {
                    user.setSex((short) 1);
                }
            }
        }
        user.setSsoAdanumber(adaNumber);
        user.setVocationalLevel(osbAboInfo.getPinLvl());
        user.setShop(osbAboInfo.getShopName());
        user.setShopProvince(osbAboInfo.getProvinceName());
        user.setShopCity(osbAboInfo.getCityName());
        user.setShopProvinceCode(osbAboInfo.getProvinceCode());
        user.setShopCityCode(osbAboInfo.getCityCode());
        user.setAreaCode(osbAboInfo.getAreaCode());
        return user;
    }


    /***
     * 获取osb更新的user
     * @param adaNumber
     * @return
     */
    private User getOsbUserByFirstLogin(String name, String adaNumber) {
        OsbAboInfo osbAboInfo = getObsAboInfo(adaNumber);
        if (null == osbAboInfo) {
            throw new AmwayLogicException(Constants.ErrorCode.SOAMEMBERINFO_NULL, LOGIN_ERROR_MESSAGE);
        }
        User user = new User();
       /* String choiceSex = (sex == 0 ? "M" : "F");
        if (choiceSex.equals(osbAboInfo.getMainSex())) {
            user.setName(osbAboInfo.getMainName());
            user.setAge(Short.parseShort(osbAboInfo.getMainAge()));
        } else if (choiceSex.equals(osbAboInfo.getSpouseSex())) {
            user.setName(osbAboInfo.getSpouseName());
            user.setAge(Short.parseShort(osbAboInfo.getSpouseAge()));
        }*/
        if (osbAboInfo.getMainName().equals(name)) {
            user.setAge(Short.parseShort(osbAboInfo.getMainAge()));
            Short sex = 1;
            if (osbAboInfo.getMainSex().equals("M")) {
                sex = 0;
            }
            user.setSex(sex);
        }
        if (osbAboInfo.getSpouseName().equals(name)) {
            user.setAge(Short.parseShort(osbAboInfo.getSpouseAge()));
            Short sex = 1;
            if (osbAboInfo.getSpouseSex().equals("M")) {
                sex = 0;
            }
            user.setSex(sex);
        }
        user.setVocationalLevel(osbAboInfo.getPinLvl());
        user.setShop(osbAboInfo.getShopName());
        user.setShopProvince(osbAboInfo.getProvinceName());
        user.setShopCity(osbAboInfo.getCityName());
        user.setShopProvinceCode(osbAboInfo.getProvinceCode());
        user.setShopCityCode(osbAboInfo.getCityCode());
        user.setAreaCode(osbAboInfo.getAreaCode());
        return user;
    }

    /**
     * 获取amwar abo信息
     *
     * @param url
     * @param token
     * @param unionId
     * @return
     */
    private AboInfo getAboInfo(String url, String token, String unionId) {
        try {
            String url0 = url + "?token=" + token + "&unionid=" + unionId;
            log.info("getAboInfo url0:{}", url0);
            String jsonStr = HttpClientUtil.doGetIgnoreVerifySSL(url0);
            log.info("aboInfo:{}", jsonStr);
            JSONObject obj = JSON.parseObject(jsonStr);
            log.info("aboInfo.msg:{}", obj.get("errmsg"));
            log.info("aboInfo.code:{}", obj.get("code"));
            if ("1".equals(obj.get("status"))) {
                AboInfo aboInfo = new AboInfo();
                aboInfo.setAda(obj.get("ada") == null ? "" : obj.get("ada").toString());
                //aboInfo.setMaskname(obj.get("maskname") == null ? "" : obj.get("maskname").toString());
                //aboInfo.setSex(getSex(obj.get("sex") == null ? "" : obj.get("sex").toString()));
                //aboInfo.setProvince(obj.get("province") == null ? "" : obj.get("province").toString());
                //aboInfo.setCity(obj.get("city") == null ? "" : obj.get("city").toString());
                aboInfo.setStatus(obj.get("status") == null ? "" : obj.get("status").toString());
                aboInfo.setCode(obj.get("code") == null ? "" : obj.get("code").toString());
                return aboInfo;
            } else if ("0".equals(obj.get("status"))) {
                AboInfo aboInfo = new AboInfo();
                aboInfo.setCode(obj.get("code") == null ? "" : obj.get("code").toString());
                aboInfo.setStatus(obj.get("status") == null ? "" : obj.get("status").toString());
                return aboInfo;
            }
            return null;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            log.error("get amway aboinfo error");
            throw new AmwaySystemException(LOGIN_ERROR_MESSAGE);
        }
    }

    @Override
    public User selectByPrimary(Integer uid) {
        return userMapper.selectByPrimaryKey(uid);
    }

    /**
     * @param phone
     * @param uid
     * @Author:qj
     */
    @Override
    @CachePut(value = Constants.USER_CACHE_NAME, key = "'" + Constants.USER_CACHE_NAME + "'+#uid")
    public User savePhone(String phone, Integer uid) {
        log.info("phone:{},uid:{}", phone.toString(), uid);
        if (!StringUtils.isEmpty(phone)) {
            if (!Pattern.matches(REGEX_MOBILE, phone)) {
                throw new AmwayLogicException("手机号格式不对");
            }
        }
        User user = userMapper.selectByPrimaryKey(uid);
        user.setPhone(phone);
        log.info("user:{}", user.toString());
        userMapper.updateByPrimaryKeySelective(user);
        return user;
    }

    /**
     * @param province
     * @param city
     * @param region
     * @param delAddress
     * @param uid
     * @Author:qj
     */
    @Override
    @CachePut(value = Constants.USER_CACHE_NAME, key = "'" + Constants.USER_CACHE_NAME + "'+#uid")
    public User saveAddr(String province, String city, String region, String delAddress, Integer uid) {
        log.info("province:{},city:{},region:{},delAddress:{},uid:{}", province, city, region, delAddress, uid);
        User user = userMapper.selectByPrimaryKey(uid);
        user.setProvCode(province);
        user.setCityCode(city);
        user.setRegionCode(region);
        user.setAddress(delAddress);
        log.info("user:{}", user.toString());
        userMapper.updateByPrimaryKeySelective(user);
        return user;
    }


    /**
     * @Description:根据班级id查询讲师
     * @Date: 2018/3/20 10:58
     * @param: classId 班级id
     * @Author: wsc
     */
    @Override
    public String selectLecturerByClassId(Integer classId) {
        return userMapper.selectLecturerByClassId(classId);
    }


    public User findWithApproed(Integer userId, Integer courseId) {
        return userMapper.selectWithApproed(userId, courseId);
    }


    public User findWithNotApproed(Integer userId, Integer courseId) {
        return userMapper.selectWithNotApproed(userId, courseId);
    }

    /**
     * @param uid
     * @param remark
     * @Author:qj
     */
    @CachePut(value = Constants.USER_CACHE_NAME, key = "'" + Constants.USER_CACHE_NAME + "'+#uid")
    public User saveRemark(Integer uid, String remark) {
        log.info("remark:{},uid:{}", remark, uid);
        User user = userMapper.selectByPrimaryKey(uid);
        user.setRemark(remark);
        log.info("user:{}", user.toString());
        userMapper.updateByPrimaryKeySelective(user);
        return user;
    }

    /**
     * 获取性别
     * 性别：0-男  1-女 2-夫妻
     *
     * @param sex
     * @return
     */
    private Short getSex(String sex) {
        if ("2".equals(sex)) {
            return 0;
        } else if ("3".equals(sex)) {
            return 2;
        }
        return 1;
    }

    /**
     * Ada卡号和性别加密
     *
     * @param adaNumber
     * @param sex
     * @return
     */
    public String getAdainfoMd5(String adaNumber, Short sex) {
        return SHA256EncodeUtil.encode(completion(adaNumber, sex));
    }

   /* public static void main(String[] args) {
        UserServiceImpl userService = new UserServiceImpl();
        Short a = 1;
        System.out.println(userService.getAdainfoMd5("59391609",a));
    }*/

    /**
     * @Description:校验安利卡号是否满足14位，不满足自动补全
     * @Date: 2018/3/13 10:31
     * @param: adaNumber 安利卡号
     * @Author: wsc
     */
    @Override
    public String completion(String adaNumber, Short sex) {
        //log.info("adaNumber:{}", adaNumber);
        int size = adaNumber.length();
        int num = 14 - size - 3;
        StringBuilder sb;
        if (num >= 0) {
            sb = new StringBuilder();
            for (int i = 0; i < num; i++) {
                sb.append("0");
            }
            adaNumber = Constants.AdaNumber.ADANUMBER + sb.toString() + adaNumber;
        } else {
            sb = new StringBuilder();
            num = 14 - size;
            for (int i = 0; i < num; i++) {
                sb.append("0");
            }
            adaNumber = sb.toString() + adaNumber;
        }

        if (1 == sex) {
            return adaNumber + "00";
        } else if (0 == sex) {
            return adaNumber + "01";
        }
        log.info("adaNumber:{}", adaNumber);
        return adaNumber;
    }

    @Override
    public User getUserByEmailAndPwd(String email, String password) {
        return userMapper.selectByEmailAndPassword(email,
            password);
    }

    @Cacheable(value = Constants.USER_CACHE_NAME, key = "'" + Constants.USER_CACHE_NAME + "'+#uid")
    @Override
    public User getUser(Integer uid) {
        User user = userMapper.selectByPrimaryKey(uid);
        return user;
    }

    @Autowired
    private UserAnswerMapper userAnswerMapper;

    @Autowired
    private UserMarkMapper userMarkMapper;

    public List <TestDk> getTestlit()throws Exception {

        File tempFile = new File("E:\\01.xlsx");
        //初始化输入流
        InputStream is;

        //根据新建的文件实例化输入流
        is = new FileInputStream(tempFile);

        //根据版本选择创建Workbook的方式

        Workbook wb;
        //根据文件名判断文件是2003版本还是2007版本

        if (ExcelImportUtils.isExcel2007("01.xlsx")) {
            wb = new XSSFWorkbook(is);

        } else {
            wb = new HSSFWorkbook(is);
        }
        //根据excel里面的内容读取并校验格式
        // List<User> userList =

        Sheet sheet = wb.getSheetAt(0);
        //得到Excel的行数
        int totalRows = sheet.getPhysicalNumberOfRows();

        List<TestDk> userList = new ArrayList<>();
        TestDk user;
        //循环Excel行数,从第二行开始。标题不入库
        for (int r = 0; r < totalRows; r++) {
            log.info("rows:{}", r);

            Row row = sheet.getRow(r);
            if (null == row){
                continue;
            }
            //校验整行是否为空
            /*if (null == row || !ExcelImportUtils.checkCellIsNull(totalCells, row)) {
                continue;
            }*/

            user = new TestDk();
            //循环Excel的列
            for (int c = 0; c < 1; c++) {

                Cell cell = row.getCell(c);
                //校验Excel每行列中值的格式是否正确
                //19checkCells(cell, c, user, r);
                if (c == 0) {
                    DecimalFormat df = new DecimalFormat("0");
                    user.setSsonum(df.format(cell.getNumericCellValue())+"");
                   // user.setSsonum(cell.getStringCellValue());
                }/* else if (c == 1) {
                    DecimalFormat df = new DecimalFormat("0");
                    user.setSsonum(df.format(cell.getNumericCellValue())+"");
                } else if (c == 2) {
                    user.setMd5(cell.getStringCellValue());
                }*/
            }
            userList.add(user);
        }

        if (!StringUtils.isEmpty(is)) {
            is.close();
        }
        return userList;
    }

    public  void contentToTxt(String filePath, String content) {
        try{

            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filePath),true));
            writer.write("\n"+content);
            writer.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    @Transactional
    public void test(String a) {
        try {
            List<TestDk> testDks = this.getTestlit();
            System.out.println("---------------:testDks Size:" + testDks.size());
            for (TestDk dk : testDks) {
                String ada = dk.getSsonum();
                OsbAboInfo osbAboInfo = this.getObsAboInfo(ada);
                if (!StringUtils.isEmpty(osbAboInfo.getMainSex()) && !StringUtils.isEmpty(osbAboInfo.getSpouseSex())) {
                    if (!StringUtils.isEmpty(osbAboInfo.getMainName()) && !StringUtils.isEmpty(osbAboInfo.getSpouseName())) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(ada);
                        this.contentToTxt("E:\\01.txt",sb.toString());
                        System.out.println("夫妻==================");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 只处理测试的
     * @return
     */
    @Transactional
    public void updateForTest(String a) {
        String adaNumber =a;
        OsbAboInfo osbAboInfo = this.getObsAboInfo(adaNumber);

        if (!StringUtils.isEmpty(osbAboInfo.getMainName()) && !StringUtils.isEmpty(osbAboInfo.getSpouseName())) {
            StringBuilder sb = new StringBuilder();
            sb.append(adaNumber);
            this.contentToTxt("E:\\03.txt",sb.toString());
            return;
        }
        short sex = -1;

        if (!StringUtils.isEmpty(osbAboInfo.getMainSex())) {
            if (!StringUtils.isEmpty(osbAboInfo.getMainSex()) && osbAboInfo.getMainSex().equals("M")) {
                sex = 0;
            } else {
                sex = 1;
            }
        } else {
            if (!StringUtils.isEmpty(osbAboInfo.getSpouseSex()) && osbAboInfo.getSpouseSex().equals("M")) {
                sex = 0;
            } else {
                sex = 1;
            }
        }

        System.out.println("----------------sex:" + sex);
        String md5_true = getAdainfoMd5(adaNumber, sex);
        System.out.println("-----------md5_true:" + md5_true);
        String md5_false = null;

        if (sex == 1) {
            md5_false = getAdainfoMd5(adaNumber, (short) 0);
        } else {
            md5_false = getAdainfoMd5(adaNumber, (short) 1);
        }


        List<User> list = userMapper.selectdkkkk01(adaNumber);
        if(list.size()>2){
            StringBuilder sb = new StringBuilder();
            sb.append(adaNumber);
            this.contentToTxt("E:\\03.txt",sb.toString());
            System.out.println("++++++++++++++@$%^&*()%^$#$%^&*()报警了。。。。。。。。");
            return;
        }

        if(list.size()==1){
            StringBuilder sb = new StringBuilder();
            sb.append(adaNumber);
            this.contentToTxt("E:\\03.txt",sb.toString());
            return;

        }

        String md5_false_new = "";
        if (list.size() == 2) {
            User u1 = list.get(0);
            User u2 = list.get(1);
            if (!u1.getAdainfoMd5().equals(md5_true)) {
                md5_false_new = u1.getAdainfoMd5();
            }

            if (!u2.getAdainfoMd5().equals(md5_true)) {
                md5_false_new = u2.getAdainfoMd5();
            }

        } else if (list.size() == 1) {
            User u1 = list.get(0);
            if (!u1.getAdainfoMd5().equals(md5_true)) {
                md5_false_new = u1.getAdainfoMd5();
            }
        }

        List<CourseSignUp> md5trueList = courseSignUpMapper.selectByUserAndCourseList000(md5_true);

        System.out.println("-------------------:md5_false:"+md5_false);
        System.out.println("-------------------:md5_false_new:"+md5_false_new);

        List<CourseSignUp> md5falseList = courseSignUpMapper.selectByUserAndCourseList000(md5_false_new);
        User userTrue = userMapper.selectByAdaEncode(md5_true);

        User userFalse = userMapper.selectByAdaEncode(md5_false_new);
        System.out.println("--------------------下面是：singup");


        userFalse.setState((short) 0);
        userMapper.updateByPrimaryKeySelective(userFalse);

        userTrue.setSex(sex);
        userTrue.setAdaNumber(this.completion(adaNumber,sex));
        userMapper.updateByPrimaryKeySelective(userTrue);

        for (CourseSignUp cfalse : md5falseList) {
            boolean flag = false;
            for (CourseSignUp ctrue : md5trueList) {
                if (cfalse.getCourseId().equals(ctrue.getCourseId())) {
                    try {
                        courseSignUpMapper.deleteByPrimaryKey(cfalse.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    flag = true;
                }
            }
            if (!flag) {
                Integer id = cfalse.getId();
                cfalse.setAdainfoMd5(md5_true);
                cfalse.setId(null);
                courseSignUpMapper.insertSelective(cfalse);
                courseSignUpMapper.deleteByPrimaryKey(id);
            }
        }

        System.out.println("--------------------下面是：UserAnswer");
        List<UserAnswer> userAnswersTrueLit = userAnswerMapper.selectdk001(userTrue.getId());
        List<UserAnswer> userAnswersFalseLit = userAnswerMapper.selectdk001(userFalse.getId());
        for (UserAnswer answerFalse : userAnswersFalseLit) {
            boolean flag1 = false;
            for (UserAnswer answerTrue : userAnswersTrueLit) {
                if (answerFalse.getPaperId().equals(answerTrue.getPaperId())) {
                    try {
                        userAnswerMapper.deleteByPrimaryKey(answerFalse.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    flag1 = true;
                }
            }
            if (!flag1) {
                Integer id = answerFalse.getId();
                answerFalse.setUserId(userTrue.getId());
                answerFalse.setId(null);
                userAnswerMapper.insert(answerFalse);
                userAnswerMapper.deleteByPrimaryKey(id);
            }
        }


        System.out.println("--------------------下面是userMark");
        List<UserMark> userMarkTrueLit = userMarkMapper.selectdk002(userTrue.getId());
        List<UserMark> userMarkFalseLit = userMarkMapper.selectdk002(userFalse.getId());

        for (UserMark userMarkFalse : userMarkFalseLit) {
            boolean flag1 = false;
            for (UserMark userMarkTrue : userMarkTrueLit) {
                if (userMarkFalse.getPaperId().equals(userMarkTrue.getPaperId())) {
                    try {
                        userMarkMapper.deleteByPrimaryKey(userMarkFalse.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    flag1 = true;
                }
            }
            if (!flag1) {
                Integer id = userMarkFalse.getId();
                userMarkFalse.setUserId(userTrue.getId());
                userMarkFalse.setId(null);
                userMarkMapper.insert(userMarkFalse);
                userMarkMapper.deleteByPrimaryKey(id);
            }
        }
    }

    public static void main(String[] args) {
        try {
            String appid="hybris";
            String appsecret="hybris123";
            String origin=appid+":"+appsecret;
            String credential= Base64.encodeBase64String(origin.getBytes("UTF-8"));
            System.out.println(credential);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
