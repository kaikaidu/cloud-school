/**
 * Created by dk on 2018/3/5.
 */

package com.amway.acti.service;

import com.amway.acti.dto.ScoreDto;
import com.amway.acti.dto.SitemUserDto;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;

@Validated
public interface SitemService {

    List<SitemUserDto> sitemSearch(Integer uid, @NotNull(message = "课程ID不能为空") Integer courseId);

    void sitemSubmit(Integer uid, @NotNull(message = "课程ID不能为空") Integer courseId, @NotNull(message = "被评分人不能为空") Integer traineeId, @NotEmpty(message = "分数集合不能为空") List<ScoreDto> scores);
}
