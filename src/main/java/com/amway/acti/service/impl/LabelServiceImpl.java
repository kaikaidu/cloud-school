package com.amway.acti.service.impl;

import com.amway.acti.base.exception.AmwayLogicException;
import com.amway.acti.base.util.Constants;
import com.amway.acti.dao.CourseMapper;
import com.amway.acti.dao.LabelMapper;
import com.amway.acti.dto.CommonResponseDto;
import com.amway.acti.model.Course;
import com.amway.acti.model.Label;
import com.amway.acti.model.LabelCustom;
import com.amway.acti.service.LabelService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.*;

@Service
@Slf4j
public class LabelServiceImpl implements LabelService {
    @Autowired
    private LabelMapper labelMapper;

    @Autowired
    private CourseMapper courseMapper;
    /**
     * 根据id查询标签
     */
    @Override
    public Label selectById(Integer id) {
        log.info("id:{}",id);
        long bt = System.currentTimeMillis();
        Label label = labelMapper.selectByPrimaryKey(id);
        long et2 = System.currentTimeMillis();
        log.info("courseList2:"+(et2 - bt)+ "ms");
        if(label == null){
            throw new AmwayLogicException(Constants.ErrorCode.LABEL_NOT_EXIST,"标签不存在");
        }
        return label;
    }

    @Override
    public List<Label> selectByType(String type) {
        List<Label> labels = labelMapper.selectByType(type);
        log.info("labels:{}",labels.toString());
        return labels;
    }

    /**
     * @Description:获取标签总数
     * @Date: 2018/3/27 9:50
     * @param: name 标签名称
     * @param: type 标签类型
     * @Author: wsc
     */
    @Override
    public CommonResponseDto getLabelCount(String name, String type) {
        log.info("name:{},type:{}",name,type);
        int count = labelMapper.getLabelCount(name,type);
        log.info("count:{}",count);
        return CommonResponseDto.ofSuccess(count);
    }

    /**
     * @Description: 获取标签列表
     * @Date: 2018/3/27 10:06
     * @param: pageNo 当前页
     * @param: pageSize 每页数量
     * @param: name 标签名称
     * @param: sort 排序标识 1：最新 2：最早
     * @param: type 标签类型
     * @Author: wsc
     */
    @Override
    public CommonResponseDto findLabelByName(Integer pageNo, Integer pageSize , String name, String sort, String type) {
        log.info("pageNo:{},pageSize:{},name:{},sort:{},type:{}",pageNo,pageSize,name,sort,type);
        PageHelper.startPage(pageNo,pageSize);
        List<LabelCustom> labelList = labelMapper.selectLabelByName(name,sort,type);
        log.info("labelList:{}",labelList);
        return CommonResponseDto.ofSuccess(new PageInfo<>(labelList));
    }

    /**
     * @Description:新增标签
     * @Date: 2018/3/27 9:54
     * @param: name 标签名称
     * @param: type 标签类型
     * @Author: wsc
     */
    @Override
    @Transactional
    public CommonResponseDto addLabel(String name, String type) {
        log.info("name:{},type:{}",name,type);
        Label label = new Label();
        label.setName(name);
        label.setCreateTime(new Date());
        label.setType(type);
        labelMapper.insertSelective(label);
        return CommonResponseDto.ofSuccess();
    }

    /**
     * @Description:根据id删除标签
     * @Date: 2018/3/27 9:54
     * @param: id 标签id
     * @param: state 全删标识 1：全部删除 -1：根据id删除
     * @Author: wsc
     */
    @Override
    @Transactional
    public CommonResponseDto deleteLabel(String ids, String state) {
        log.info("ids:{},state:{}",ids,state);
        List<LabelCustom> labelList = null;
        List<String> list = null;
        //判断是根据id删除还是全部删除
        if (Constants.Number.STRING_NUMBER1.equals(state)) {
            //关联课程查询已关联的标签
            labelList = labelMapper.selectLabelById(null);
        } else {
            list = new ArrayList <>();
            String[] idsArray = ids.split(",");
            for (String s : idsArray) {
                list.add(s);
            }
            //关联课程查询已关联的标签
            labelList = labelMapper.selectLabelById(list);
        }
        List<Map<String,Object>> mapList = new ArrayList <>();
        //有关联的不能删除给出提示，没有关联的删除
        for (LabelCustom c : labelList) {
            Map<String,Object> map = new HashMap <>();
            map.put("id",c.getId());
            map.put("name",c.getName());
            if (null == c.getLabelId()) {
                map.put("label",false);
                labelMapper.deleteByPrimaryKey(c.getId());
            } else {
                map.put("label",true);
            }
            mapList.add(map);
        }
        return CommonResponseDto.ofSuccess(mapList);
    }

    /**
     * @Description:修改标签
     * @Date: 2018/3/27 9:54
     * @param: id 标签id
     * @param: name 标签名称
     * @Author: wsc
     */
    @Override
    @Transactional
    public CommonResponseDto updateLabel(Integer id, String name) {
        log.info("id:{},name:{}",id,name);
        Label label = new Label();
        label.setId(id);
        label.setName(name);
        labelMapper.updateByPrimaryKeySelective(label);
        return CommonResponseDto.ofSuccess();
    }

    /**
     * @Description:根据名称查询标签
     * @Date: 2018/4/9 10:06
     * @param: name
     * @Author: wsc
     */
    @Override
    public CommonResponseDto getLabelByName(String name) {
        Label label = labelMapper.selectLabelByNameAndType(name, Constants.LabelType.COURSE);
        if (null != label && !ObjectUtils.isEmpty(label)) {
            throw new AmwayLogicException(Constants.ErrorCode.TEACHAR_NULL,"标签已存在");
        }
        return CommonResponseDto.ofSuccess();
    }

    /**
     * @Description:获取全部标签
     * @Date: 2018/4/9 17:36
     * @Author: wsc
     */
    @Override
    public CommonResponseDto getLabelAll() {
        List<Label> labelList = labelMapper.selectByType(Constants.LabelType.COURSE);
        return CommonResponseDto.ofSuccess(labelList);
    }

    /**
     * @Description:根据标签id查询关联的课程
     * @Date: 2018/4/10 16:42
     * @param: labelId
     * @Author: wsc
     */
    @Override
    public CommonResponseDto findCourse(Integer labelId) {
        List<Map<String,Object>> mapList = new ArrayList <>();
        List<Course> courseList = courseMapper.selectCourseByLabel(labelId);
        for (Course c: courseList) {
            Map<String,Object> map = new HashMap <>();
            map.put("name",c.getTitle());
            mapList.add(map);
        }
        return CommonResponseDto.ofSuccess(mapList);
    }
}
