package com.amway.acti.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.amway.acti.base.redis.RedisComponent;
import com.amway.acti.base.exception.AmwayLogicException;
import com.amway.acti.base.util.AzureBlobUploadUtil;
import com.amway.acti.base.util.Constants;
import com.amway.acti.dao.CourseRegisterMapper;
import com.amway.acti.dao.RegisterManageMapper;
import com.amway.acti.model.CourseRegister;
import com.amway.acti.model.Register;
import com.amway.acti.service.RegisterManageService;
import com.github.pagehelper.PageHelper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.microsoft.azure.storage.StorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.util.*;

@Slf4j
@Service
public class RegisterManageServiceImpl implements RegisterManageService {
    private static final String SIGNUP = "已签到";
    private static final String SIGNOUT = "未签到";

    @Autowired
    private RegisterManageMapper registerManageMapper;

    @Autowired
    private CourseRegisterMapper courseRegisterMapper;

    @Autowired
    private AzureBlobUploadUtil azureBlobUploadUtil;

    @Autowired
    private RedisComponent redisComponent;

    @Value("${localFolder.url}")
    private String filepath;

    @Override
    public List<Register> findRegisterList(Register register, Integer pageNo, Integer pageSize){
        log.info("register:{},pageNo:{},pageSize:{}",register,pageNo,pageSize);
        PageHelper.startPage(pageNo,pageSize);
        return queryRegisterList(register);
    }

    public List<Register> queryRegisterList(Register register){
        log.info("register:{}",register);
        List<Register> registerList = registerManageMapper.findRegisterList(register);
        int size = registerList.size();
        if(Constants.Number.STRING_NUMBER0.equals(register.getRegisterStatus())){
            for(int i = size-1;i >= 0;i--){
                String registerStatus = registerList.get(i).getRegisterStatus();
                if(SIGNUP.equals(registerStatus)){
                    registerList.remove(registerList.get(i));
                }
            }
            return registerList;
        }else if(Constants.Number.STRING_NUMBER1.equals(register.getRegisterStatus())){
            for(int i = size-1;i >= 0;i--){
                String registerStatus = registerList.get(i).getRegisterStatus();
                if(SIGNOUT.equals(registerStatus)){
                    registerList.remove(registerList.get(i));
                }
            }
            return registerList;
        }else {
            return registerList;
        }
    }

    @Override
    public void batchRegisterUp(String uIds, String courseId) {
        log.info("uIds:{},courseId:{}",uIds,courseId);
        List<Integer> userIds = new ArrayList<>();
        String[] u = uIds.split(",");
        for (String userId : u) {
            Integer uid = Integer.parseInt(userId);
            Integer cid = Integer.parseInt(courseId);
            //签到表(t_course_register)建议用联合主键,出现脏数据此处会报错
            CourseRegister courseRegister = courseRegisterMapper.selectByUserAndCourse(uid, cid);
            if(courseRegister == null){
                //将页面全选的学员进行非空校验，赛选未签到学员
                userIds.add(Integer.parseInt(userId));
            }
            //删除课程详情缓存
            deleteRedisCourseInfo(uid,cid);
        }
        if(userIds.isEmpty()){
            throw new AmwayLogicException(Constants.ErrorCode.PARAM_EXCEPTION, "选中的学员都已经签到");
        }
        CourseRegister c = new CourseRegister();
        c.setCourseId(Integer.parseInt(courseId));
        for (Integer userId:userIds){
            c.setUserId(userId);
            c.setRegisterTime(new Date());
            //批量签到
            courseRegisterMapper.insertSelective(c);
        }
    }

    //删除课程详情缓存
    private void deleteRedisCourseInfo(Integer userId,Integer courseId){
        if (redisComponent.hasKey(Constants.COURSE_USER_INFO + courseId + ":" + userId)) {
            redisComponent.del(Constants.COURSE_USER_INFO + courseId + ":" + userId);
        }
    }

    /**
     * 生成图像
     *
     * @throws WriterException
     * @throws IOException
     */
    public String codeCreate(String courseId) throws WriterException, IOException {
        //不存在文件夹时创建文件夹
        File fileDir = new File(filepath);
        if(!fileDir.exists()){
            fileDir.mkdirs();
        }
        //定义生成图片的名称
        String fileName = "code"+courseId+".jpg";
        int width = 200; // 图像宽度
        int height = 200; // 图像高度
        String format = "jpg";// 图像类型
        JSONObject json = new JSONObject();
        json.put("courseId",courseId);
        String content = json.toJSONString();// 内容
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);// 生成矩阵
        Path path = FileSystems.getDefault().getPath(filepath, fileName);
        MatrixToImageWriter.writeToPath(bitMatrix, format, path);// 输出图像
        //获取文件
        File file=new File(filepath+"/"+fileName);
        //初始化返回的url
        String resultUrl = null;
        try (InputStream in = new FileInputStream(file)) {
            byte[] bb = new byte[in.available()];
            resultUrl = azureBlobUploadUtil.upload(in, fileName, bb.length, "img");
        } catch (StorageException | URISyntaxException | InvalidKeyException e) {
            log.error(e.getMessage(),e);
        }
        //上传微软云成功后删除本地图片,否则保存
        if(!StringUtils.isEmpty(resultUrl)){
            Files.delete(path);
        }
        return resultUrl;
    }
}
