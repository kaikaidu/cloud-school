package com.amway.acti.transform;

import com.amway.acti.dto.DocDto;
import com.amway.acti.model.Doc;

import java.util.List;

public interface DocTransform {

    List<DocDto> transformPageInfoToDto(List<Doc> docList);
}
