package com.amway.acti.transform.impl;

import com.amway.acti.dao.CourseMapper;
import com.amway.acti.dto.SitemTempDto;
import com.amway.acti.model.Course;
import com.amway.acti.model.SitemTemp;
import com.amway.acti.transform.SitemTempTransform;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SitemTempTransformImpl implements SitemTempTransform{

    @Autowired
    private CourseMapper courseMapper;

    @Override
    public List<SitemTempDto> transformToSitemTempDto(List<SitemTemp> sitemTempList) {
        List<SitemTempDto> sitemTempDtoList = new ArrayList<>();
        for(SitemTemp sitemTemp : sitemTempList){
            List<Course> courseList = courseMapper.selectCourseBySitemTemp(sitemTemp.getId());
            SitemTempDto sitemTempDto = new SitemTempDto();
            sitemTempDto.setId(sitemTemp.getId());
            sitemTempDto.setName(sitemTemp.getName());
            sitemTempDto.setCreateTime(sitemTemp.getCreateTime());
            sitemTempDto.setStand(sitemTemp.getStand());
            sitemTempDto.setState(sitemTemp.getState());
            if(courseList.isEmpty()){
                sitemTempDto.setApply(true);
            }else {
                sitemTempDto.setApply(false);
            }
            sitemTempDtoList.add(sitemTempDto);
        }

        return sitemTempDtoList;
    }
}
