/**
 * Created by dk on 2018/6/6.
 */

package com.amway.acti.service.impl;

import com.amway.acti.base.exception.AmwayLogicException;
import com.amway.acti.base.util.AzureBlobUploadUtil;
import com.amway.acti.base.util.Constants;
import com.amway.acti.base.util.FileUtil;
import com.amway.acti.base.util.UnZipUtils;
import com.amway.acti.dao.CertMapper;
import com.amway.acti.dao.CertTempMapper;
import com.amway.acti.dao.CourseMapper;
import com.amway.acti.dao.UserCertMapper;
import com.amway.acti.dto.CertTempDelDto;
import com.amway.acti.dto.CertTemplateSaveDto;
import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.dto.TemUploadDto;
import com.amway.acti.model.CertTemp;
import com.amway.acti.service.BackendCertTemplateService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.microsoft.azure.storage.StorageException;
import gui.ava.html.image.generator.HtmlImageGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class BackendCertTemplateImpl implements BackendCertTemplateService {

    @Autowired
    private CertTempMapper certTempMapper;

    @Autowired
    private AzureBlobUploadUtil azureBlobUploadUtil;

    @Autowired
    private CertMapper certMapper;

    @Autowired
    private UserCertMapper userCertMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Value("${localFolder.url}")
    private String localFolder;

    private static final String INDEX_HTML = "index.htm";

    private static final String BG_PNG = "Diploma7.gif";

    public static final String HTML_TEM="<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />";

    /**
     * 查询证书模板列表
     *
     * @param pageNo
     * @param pageSize
     * @param name
     * @param sort
     * @return
     */
    @Override
    public PageInfo getCertTemplateList(Integer pageNo, Integer pageSize, String name, String sort) {
        PageHelper.startPage(pageNo, pageSize);
        List<CertTemp> resultList = certTempMapper.selectCertTemplateListByName(name, sort);
        return new PageInfo(resultList);
    }

    /***
     * 批量删除
     * @param ids
     * @return
     */
    @Transactional
    @Override
    public CommonResponseDto del(Integer[] ids) {
        log.info("ids:{},state:{}", ids.length);
        List<CertTempDelDto> resultList = new ArrayList<>();
        for (Integer id : ids) {
            Map map = isDel(id);
            map.get("flag");
            if (Boolean.parseBoolean(map.get("flag").toString())) {
                CertTemp certTemp = certTempMapper.selectByPrimaryKey(id);
                if (null == certTemp) {
                    throw new AmwayLogicException(Constants.ErrorCode.CRET_TEMP_20000001, "证书模板不存在");
                }
                CertTempDelDto delDto = new CertTempDelDto();
                delDto.setCertTempId(id);
                delDto.setIsDel(0);
                delDto.setMessage("【 " + certTemp.getName() + "】删除失败，该模板已被一个或多个课程使用。");
                resultList.add(delDto);
            } else {
                CertTemp certTemp = certTempMapper.selectByPrimaryKey(id);
                if (null == certTemp) {
                    throw new AmwayLogicException(Constants.ErrorCode.CRET_TEMP_20000001, "证书模板不存在");
                }
                if (1 == certTemp.getState()) {
                    certTempMapper.updateStateById(id);
                    certMapper.updateStateByCertTempId(id);
                    List<Integer> certIds = certMapper.selectIdByTempId(id);
                    if (null != certIds && certIds.size() > 0) {
                        userCertMapper.delByCertId(certIds);
                    }
                    CertTempDelDto delDto = new CertTempDelDto();
                    delDto.setCertTempId(id);
                    delDto.setIsDel(1);
                    delDto.setMessage("【 " + certTemp.getName() + "】 模板成功删除!");
                    resultList.add(delDto);
                } else {
                    CertTempDelDto delDto = new CertTempDelDto();
                    delDto.setCertTempId(id);
                    delDto.setMessage("【 " + certTemp.getName() + "】 模板已被删除!");
                    delDto.setIsDel(1);
                    resultList.add(delDto);
                }
            }
        }
        return CommonResponseDto.ofSuccess(resultList);
    }

    @Override
    public List<String> getCourseNameList(Integer certTempId) {
        List<String> resultList = new ArrayList<>();
        List<Integer> courseIdsList = certMapper.selectCourseIdByTempId(certTempId);
        for (Integer courseId : courseIdsList) {
            resultList.add(courseMapper.selectTitleBySysId(courseId));
        }
        return resultList;
    }

    /***
     * 根据ID查询
     * @param id
     * @return
     */
    @Override
    public CertTemp getCertTemplateById(Integer id) {
        return certTempMapper.selectByPrimaryKey(id);
    }


    /**
     * 保存证书模板
     *
     * @param certTemplateSaveDto
     */
    @Override
    public void save(CertTemplateSaveDto certTemplateSaveDto) {
        log.info("certTemplateSaveDto:{}", certTemplateSaveDto.toString());
        if (null != certTemplateSaveDto.getId()) {
            CertTemp certTemp = certTempMapper.selectByPrimaryKey(certTemplateSaveDto.getId());
            if (null == certTemp) {
                throw new AmwayLogicException(Constants.ErrorCode.CRET_TEMP_20000001, "证书模板不存在");
            }
            certTemp.setName(certTemplateSaveDto.getCertTemplateName());
            if(!StringUtils.isEmpty(certTemplateSaveDto.getUrl())){
                certTemp.setUrl(certTemplateSaveDto.getUrl());
            }
            if(!StringUtils.isEmpty(certTemplateSaveDto.getZipUrl())){
                certTemp.setZip(certTemplateSaveDto.getZipUrl());
            }
            if(!StringUtils.isEmpty(certTemplateSaveDto.getHtmlUrl())){
                certTemp.setHtmlUrl(certTemplateSaveDto.getHtmlUrl());
            }
            certTemp.setCreateTime(new Date());
            CertTemp certTemp0 = certTempMapper.selectByPrimaryKey(certTemplateSaveDto.getId());
            if (!certTemplateSaveDto.getCertTemplateName().equals(certTemp0.getName())) {
                if (certTempMapper.selectByName(certTemp.getName()) > 0) {
                    throw new AmwayLogicException(Constants.ErrorCode.CRET_TEMP_20000001, "证书模板名称已存在");
                }
            }
            certTempMapper.updateByPrimaryKeySelective(certTemp);
        } else {
            if (StringUtils.isEmpty(certTemplateSaveDto.getZipUrl())) {
                throw new AmwayLogicException(Constants.ErrorCode.CRET_TEMP_20000001, "请上传证书zip！");
            }
            CertTemp certTemp = new CertTemp();
            certTemp.setName(certTemplateSaveDto.getCertTemplateName());
            certTemp.setUrl(certTemplateSaveDto.getUrl());
            certTemp.setZip(certTemplateSaveDto.getZipUrl());
            certTemp.setState(1);
            certTemp.setCreateTime(new Date());
            certTemp.setHtmlUrl(certTemplateSaveDto.getHtmlUrl());
            if (certTempMapper.selectByName(certTemp.getName()) > 0) {
                throw new AmwayLogicException(Constants.ErrorCode.CRET_TEMP_20000001, "证书模板名称已存在");
            }
            certTempMapper.insertSelective(certTemp);
        }
        log.info("save cert template success.");
    }

    /***
     * 判断该模板关联的课程下是否已经颁发学员证书了
     * @param certTempId
     * @return
     */
    private Map isDel(Integer certTempId) {
        boolean flag = false;
        String coursetitle = "";
        Map<String, Object> resultMap = new HashMap<>();
        List<Integer> resultList = certMapper.selectCourseIdByTempId(certTempId);
        for (Integer courseId : resultList) {
            if (userCertMapper.selectIsAwardByCourseId(courseId) > 0) {
                coursetitle += courseMapper.selectTitleBySysId(courseId) + ",";
                flag = true;
            }
        }
        resultMap.put("flag", flag);
        resultMap.put("coursetitle", coursetitle);
        return resultMap;
    }

    /***
     * 判断该模板关联的课程下是否已经颁发学员证书了
     * @param certTempId
     * @return
     */
    @Override
    public boolean isAward(Integer certTempId) {
        List<Integer> resultList = certMapper.selectCourseIdByTempId(certTempId);
        for (Integer courseId : resultList) {
            if (userCertMapper.selectIsAwardByCourseId(courseId) > 0) {
                return true;
            }
        }
        return false;
    }


    /***
     * 上传zip
     * @param file
     * @param directory
     * @return
     */
    @Override
    public TemUploadDto upload(MultipartFile file, String directory) {
        String zipUrl = "";
        String htmlUrl = "";
        String url = "";

        //上传过程生成的临时文件
        String delZip = "";
        String zipFilePath = "";
        try {
            String basePath = localFolder + "/zip";
            File fileDir = new File(basePath);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            String zipNewName = System.currentTimeMillis() + "";
            zipFilePath = basePath + "/" + zipNewName;
            delZip = zipFilePath + ".zip";
            File tempFile = new File(zipFilePath + ".zip");
            //上传zip
            file.transferTo(tempFile);
            //解压
            UnZipUtils.unZipFiles(tempFile, zipFilePath + "/");

            //上传zip到云盘
            try (InputStream in = new FileInputStream(tempFile)) {
                byte[] bb0 = new byte[in.available()];
                zipUrl = azureBlobUploadUtil.upload(in, tempFile.getName(), bb0.length, directory);
            } catch (StorageException | URISyntaxException | InvalidKeyException e) {
                log.error(e.getMessage(), e);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new AmwayLogicException(Constants.ErrorCode.CRET_TEMP_20000001, "上传zip文档失败，请重试!");
        }

        //上传背景图到云盘
        String backgroundImgUrl = "";
        File bgImgFile = null;
        try {
            bgImgFile = new File(zipFilePath + "/" + BG_PNG);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new AmwayLogicException(Constants.ErrorCode.CRET_TEMP_20000001, "Zip格式错误，请导入正确模板!");
        }
        try (InputStream in = new FileInputStream(bgImgFile)) {
            byte[] bb1 = new byte[in.available()];
            backgroundImgUrl = azureBlobUploadUtil.upload(in, System.currentTimeMillis() + "_" + bgImgFile.getName(), bb1.length, directory);
        } catch (StorageException | URISyntaxException | IOException | InvalidKeyException e) {
            log.error(e.getMessage(), e);
        }

       String htmlstr = "";
        try {
            //读取html源代码
            htmlstr = FileUtil.readfile(zipFilePath + "/" + INDEX_HTML);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new AmwayLogicException(Constants.ErrorCode.CRET_TEMP_20000001, "Zip格式错误，请导入正确模板!");
        }
        String certImg = "";
        try {
            String html="";
            html = htmlstr.replace(BG_PNG, backgroundImgUrl);
            htmlstr = htmlstr.replace(BG_PNG, backgroundImgUrl).replace(HTML_TEM,"");
            log.info("html:{}", htmlstr);
            HtmlImageGenerator htmlImageGenerator = new HtmlImageGenerator();
            htmlImageGenerator.loadHtml(htmlstr);
            htmlImageGenerator.getBufferedImage();

            //生成证书图片
            certImg = zipFilePath + "/" + System.currentTimeMillis() + ".png";
            htmlImageGenerator.saveAsImage(certImg);
            String certHtml = zipFilePath + "/" + System.currentTimeMillis() + ".html";

            File f = new File(certHtml);
            if (!f.exists()) {
                f.createNewFile();
            }

            OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(f), "UTF-8");
            BufferedWriter writer = new BufferedWriter(write);
            writer.write(html);
            writer.close();

            /*File file00 = new File(certHtml);
            if (!file00.exists()) {
                file00.getParentFile().mkdirs();
            }
            file00.createNewFile();
            FileWriter fw = new FileWriter(file00, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(html);
            bw.flush();
            bw.close();
            fw.close();*/

            //上传html到云盘
            try (InputStream in = new FileInputStream(f)) {
                byte[] bb2 = new byte[in.available()];
                htmlUrl = azureBlobUploadUtil.upload(in, f.getName(), bb2.length, directory);
            } catch (StorageException | URISyntaxException | InvalidKeyException e) {
                log.error(e.getMessage(), e);
            }

            //上传证书图片到云盘
            File f1 = new File(certImg);
            try (InputStream in = new FileInputStream(f1)) {
                byte[] bb3 = new byte[in.available()];
                url = azureBlobUploadUtil.upload(in, f1.getName(), bb3.length, directory);
            } catch (StorageException | URISyntaxException | InvalidKeyException e) {
                log.error(e.getMessage(), e);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new AmwayLogicException(Constants.ErrorCode.CRET_TEMP_20000001, "上传zip文档失败，请重试!");
        }
        TemUploadDto temUploadDto = new TemUploadDto();
        temUploadDto.setUrl(url);
        temUploadDto.setZipUrl(zipUrl);
        temUploadDto.setHtmlUrl(htmlUrl);
        log.info(temUploadDto.toString());

        //删除生成的临时文件
        FileUtil.delZipFile(delZip);
        FileUtil.deleteDir(new File(zipFilePath));
        return temUploadDto;
    }

}
