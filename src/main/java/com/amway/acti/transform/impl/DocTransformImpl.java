package com.amway.acti.transform.impl;

import com.amway.acti.base.util.URLTransForm;
import com.amway.acti.dto.DocDto;
import com.amway.acti.model.Doc;
import com.amway.acti.transform.DocTransform;
import com.google.common.collect.Lists;
import com.sun.org.apache.xpath.internal.SourceTree;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class DocTransformImpl implements DocTransform {

    @Override
    public List<DocDto> transformPageInfoToDto(List<Doc> docList) {

        List<DocDto> docDtoList = Lists.newArrayList();
        for(Doc doc : docList){
            DocDto docDto = new DocDto();
            docDto.setFileName(doc.getName());
            String url = doc.getUrl();
            Map<String, String> map = split(url);
            docDto.setUrl(map.get("before")+getEnableUrl(map.get("after")));
            docDtoList.add(docDto);
        }
        return docDtoList;
    }

    private String getEnableUrl(String encodeFileName){
        ScriptEngineManager sem = new ScriptEngineManager();
        ScriptEngine se = sem.getEngineByName("js");
        try {
            String script = buildFunction();
            se.eval(script);
            Invocable inv2 = (Invocable) se;
            String result = (String) inv2.invokeFunction("getWXUrl", encodeFileName);
            return result;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private String buildFunction() {
        return "function getWXUrl(url) {\r\n" +
                "	var url = decodeURIComponent(url);\r\n" +
                "	var encodeUrl = encodeURIComponent(url);\r\n" +
                "	return encodeUrl;\r\n" +
                "}";
    }

    private Map<String, String> split(String url){
        int index = url.lastIndexOf("/");
        String before = url.substring(0, index+1);
        String after = url.substring(index+1);
        Map<String, String> map = new HashMap<>();
        map.put("before", before);
        map.put("after", after);
        return map;
    }

}
