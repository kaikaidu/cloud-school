package com.amway.acti.transform;

import com.amway.acti.dto.InvesDto;
import com.amway.acti.dto.InvesInfoDto;
import com.amway.acti.dto.PageDto;
import com.amway.acti.dto.inves.InvesTempDto;
import com.amway.acti.model.InvesPaper;
import com.amway.acti.model.InvesQuest;
import com.amway.acti.model.InvesTemp;
import com.github.pagehelper.PageInfo;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * @Description:问卷DTO转换接口
 * @Date:2018/3/8 10:10
 * @Author:wsc
 */
public interface InvesTransform {
    /////////////////前端wsc新增////////////////
    List<InvesDto> invesformPageDto(List<InvesPaper> pageInfo);

    InvesInfoDto invesfromInvesInfoDto(InvesPaper invesPaper);

    /////////////////////李伟新增后台相关////////////////////
    /**
     * 转换得到PageDto<InvesTempDto>
     *
     * @param pageInfo
     * @return
     */
    PageDto<InvesTempDto> transformInfoToInvesTempDto(PageInfo<InvesTemp> pageInfo);

    /**
     * 转换得到模板对象（包含试题）
     * @param invesTemp
     * @param invesQuest
     * @return
     */
    JSONObject transformToJson(InvesTemp invesTemp, List<InvesQuest> invesQuest);

    /**
     * 转换得到预览模板对象（包含试题）
     * @param invesTemp
     * @param invesQuest
     * @return
     */
    JSONObject transformToViewJson(InvesTemp invesTemp, List<InvesQuest> invesQuest);

    /**
     * 转换得到预览测试卷对象（包含试题）
     * @param invesPaper
     * @param invesTemp
     * @param invesQuest
     * @return
     */
    JSONObject transformToPaperViewJson(InvesPaper invesPaper, InvesTemp invesTemp, List<InvesQuest> invesQuest);

}
