package com.amway.acti.transform.impl;

import com.amway.acti.dao.ClassDrawMapper;
import com.amway.acti.dto.DrawResultDataDto;
import com.amway.acti.model.DrawResultData;
import com.amway.acti.transform.DrawResultDataTransform;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class DrawResultDataTransformImpl implements DrawResultDataTransform {

    //演讲内容
    @Autowired
    private ClassDrawMapper classDrawMapper;

    @Override
    public List<DrawResultDataDto> transformDrawResultDataToDto(List<DrawResultData> drawResultDataList) {
        List<DrawResultDataDto> result = new ArrayList<>();
        for(DrawResultData d : drawResultDataList){
            DrawResultDataDto drawResultDataDto = new DrawResultDataDto();
            drawResultDataDto.setClassName(d.getClassName());
            drawResultDataDto.setTeacher(d.getTeacher());
            drawResultDataDto.setCode(d.getCode());
            drawResultDataDto.setAdaNumber(d.getAdaNumber());
            drawResultDataDto.setStudent(d.getStudent());
            drawResultDataDto.setSex(d.getSex());
            drawResultDataDto.setContent(classDrawMapper.selectByPrimaryKey(d.getClassDrawId()).getContent());
            result.add(drawResultDataDto);
        }
        return result;
    }
}
