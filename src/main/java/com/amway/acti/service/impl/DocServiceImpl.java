package com.amway.acti.service.impl;

import com.amway.acti.base.redis.RedisComponent;
import com.amway.acti.base.exception.AmwayLogicException;
import com.amway.acti.base.util.Constants;
import com.amway.acti.dao.CourseDocMapper;
import com.amway.acti.dao.CourseMapper;
import com.amway.acti.dao.DocMapper;
import com.amway.acti.dto.CourseDocSearchDto;
import com.amway.acti.dto.DocSearchDto;
import com.amway.acti.model.Course;
import com.amway.acti.model.Doc;
import com.amway.acti.service.DocService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class DocServiceImpl implements DocService {

    @Autowired
    private DocMapper docMapper;

    @Autowired
    private CourseDocMapper courseDocMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private RedisComponent redisComponent;

    @Override
    public List<Doc> selectByCourseId(Integer courseId) {
        log.info("courseId:{}", courseId);
        return docMapper.selectByCourseId(courseId);
    }

    @Override
    public void saveUpdateDoc(Doc doc) {
        log.info("doc:{}", doc.toString());
        Integer countByName = docMapper.countByParam(doc);
        if (countByName != null && countByName > 0) {
            throw new AmwayLogicException(Constants.ErrorCode.DUMPLICATE_TITLE, "存在同名或同资料地址的资料");
        }
        Integer result;
        if (doc.getId() == null) {
            doc.setCreateTime(new Date());
            result = docMapper.insertSelective(doc);
        } else {
            Doc doc1 = docMapper.selectByPrimaryKey(doc.getId());
            if (doc1 == null) {
                throw new AmwayLogicException(Constants.ErrorCode.DOC_NOT_EXIST, "资料不存在");
            }
            result = docMapper.updateByPrimaryKeySelective(doc);
        }

        log.info("insert result:{}", result);
    }

    @Override
    public Boolean hasDoc(Integer courseId) {
        log.info("courseId:{}", courseId);
        Integer docCount;
        if (courseId == null) {
            docCount = docMapper.countDoc();
            log.info("count t_doc");
        } else {
            docCount = courseDocMapper.countByCourseId(courseId);
            log.info("count t_course_doc by courseId:{}", courseId);
        }
        log.info("docCount:{}", docCount);
        return docCount > 0;
    }

    @Override
    public Integer countDoc(DocSearchDto docSearchDto) {
        Integer result = docMapper.countByCondition(docSearchDto.getName(), docSearchDto.getType(), docSearchDto.getAddType());
        log.info("countResult:{}", result);
        return result;
    }

    @Override
    public PageInfo<Doc> selectDoc(DocSearchDto docSearchDto) {
        Integer pageNo = docSearchDto.getPageNo() == null ? Integer.valueOf(1) : docSearchDto.getPageNo();
        Integer pageSize = docSearchDto.getPageSize() == null ? Integer.valueOf(10) : docSearchDto.getPageSize();
        log.info("pageNo:{},pageSize:{}", pageNo, pageSize);
        PageHelper.startPage(pageNo, pageSize);
        List<Doc> docs = docMapper.selectByCondition(docSearchDto.getName(), docSearchDto.getType(), docSearchDto.getAddType(), docSearchDto.getOrderType());
        log.info("result size:{}", docs.size());
        return new PageInfo<>(docs);
    }

    @Override
    @Transactional
    public Map<String, List<? extends Doc>> deleteDoc(List<Integer> docIds) {
        Map<String, List<? extends Doc>> resultMap = new HashMap<>();
        log.info("docIds:{}", docIds.toString());
        List<Doc> all = docMapper.selectInIds(docIds);
        if (CollectionUtils.isEmpty(all)) {
            log.info("deleteDoc selectInIds:{}", "id查询为空");
            return resultMap;
        }
        //查询可删除的资源
        List<Doc> notexits = docMapper.selectCourseInconnect(docIds);
        if (!CollectionUtils.isEmpty(notexits)) {
            Integer result = docMapper.deleteDocs(notexits);
            log.info("delete doc result:{}", result);
            Integer result1 = courseDocMapper.deleteByDocs(notexits);
            log.info("delete course_doc result:{}", result1);
            resultMap.put("exits", this.removeAll(all, notexits));
            resultMap.put("dels", notexits);
        } else {
            resultMap.put("exits", all);
        }
        return resultMap;
    }

    @Override
    public Doc selectById(Integer docId) {
        log.info("docId:{}", docId);
        Doc doc = docMapper.selectByPrimaryKey(docId);
        if (doc == null) {
            throw new AmwayLogicException(Constants.ErrorCode.DOC_NOT_EXIST, "资料不存在");
        }
        return doc;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> addCourseDoc(Integer courseId, Integer[] docIds) {
        Map<String, Object> map = new HashMap<>();
        log.info("courseId:{},docIds:{}", courseId, Arrays.toString(docIds));
        //查询当前课程下已经绑定的资源
        List<Integer> addedDocs = courseDocMapper.selectDocIdsByCourse(courseId);
        List<Doc> newDocs = docMapper.selectInIds(Arrays.asList(docIds));
        //如果不存在已绑定的资源
        if (CollectionUtils.isEmpty(addedDocs)) {
            Integer result = courseDocMapper.addCourseDocs(courseId, Arrays.asList(docIds));
            log.info("docIds size:{},insert result:{}", docIds.length, result);
            map.put("new", newDocs);
        } else {
            Set<Doc> exists = new HashSet<>();
            Set<Doc> sussess = new HashSet<>();
            Set<Integer> adds = new HashSet<>();
            for (Doc doc : newDocs) {
                //遍历待新增的资源 是否存在于已绑定的资源中
                if (addedDocs.contains(doc.getId())) {
                    exists.add(doc);
                } else {
                    adds.add(doc.getId());
                    sussess.add(doc);
                }
            }
            map.put("old", exists);
            if (!CollectionUtils.isEmpty(sussess)) {
                Integer result = courseDocMapper.addCourseDocs(courseId, new ArrayList<>(adds));
                log.info("docIds size:{},insert result:{}", docIds.length, result);
                map.put("new", sussess);
            }
        }
        return map;
    }

    @Override
    public Integer countCourseDocByCondition(CourseDocSearchDto dto) {
        log.info("CourseDocSearchDto:{}", dto.toString());
        Integer result = docMapper.countCourseDocByCondition(dto.getName(), dto.getType(), dto.getAddType(), dto.getShelve(), dto.getCourseId());
        log.info("count result:{}", result);
        return result;
    }

    @Override
    public PageInfo<Doc> selectCourseDocByCondition(CourseDocSearchDto dto) {
        int pageSize = dto.getPageSize() == null ? 10 : dto.getPageSize();
        int pageNo = dto.getPageNo() == null ? 1 : dto.getPageNo();
        PageHelper.startPage(pageNo, pageSize);
        List<Doc> docs = docMapper.selectCourseDocByCondition(dto.getName(), dto.getType(), dto.getAddType(), dto.getOrderType(), dto.getShelve(), dto.getCourseId());
        return new PageInfo<>(docs);
    }

    @Override
    public void uplateCourseDocShelve(Integer courseId, Integer[] docIds, Integer shelve) {
        log.info("courseId:{},shelve:{},docIds:{}", courseId, shelve, Arrays.toString(docIds));
        Integer result = courseDocMapper.updateShelveByCourseAndDoc(shelve, courseId, docIds);
        log.info("result:{}", result);
        isDownloadUpdate(courseId);
    }

    @Override
    public void deleteCourseDoc(Integer courseId, Integer[] docIds) {
        log.info("courseId:{},docIds:{}", courseId, Arrays.toString(docIds));
        Integer result = courseDocMapper.deleteByCourseAndDoc(courseId, docIds);
        log.info("result:{}", result);
        isDownloadUpdate(courseId);
    }

    @Override
    public List<Course> selectCourseByDocId(Integer docId) {
        return courseMapper.selectCourseByDocId(docId);
    }

    private void isDownloadUpdate(Integer courseId) {
        log.info("update isDownload,courseId:{}", courseId);
        Integer count = courseDocMapper.countByCourseIdAndShelve(courseId);
        log.info("count result:{}", count);
        if (Constants.Number.INT_NUMBER0 == count) {
            log.info("update is_download = 0");
            courseMapper.updateCourseActivity("is_download", Constants.Number.INT_NUMBER0, courseId);
        } else {
            log.info("update is_download = 1");
            courseMapper.updateCourseActivity("is_download", Constants.Number.INT_NUMBER1, courseId);
        }
    }

    private List<? extends Doc> removeAll(List<? extends Doc> src, List<? extends Doc> target) {
        LinkedList<? extends Doc> result = new LinkedList<>(src);
        Iterator<? extends Doc> resultItr = result.iterator();
        while (resultItr.hasNext()) {
            Integer id = resultItr.next().getId();
            if (target.size() == 1) {
                if (target.get(0).getId().equals(id)) {
                    resultItr.remove();
                }
            } else {
                Iterator<? extends Doc> targetItr = target.iterator();
                while (targetItr.hasNext()) {
                    if (targetItr.next().getId().equals(id)) {
                        resultItr.remove();
                    }
                }
            }
        }
        return result;
    }
}
