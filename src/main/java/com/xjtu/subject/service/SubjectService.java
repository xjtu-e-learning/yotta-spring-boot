package com.xjtu.subject.service;

import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.domain.domain.Domain;
import com.xjtu.domain.repository.DomainRepository;
import com.xjtu.subject.domain.Subject;
import com.xjtu.subject.repository.SubjectRepository;
import com.xjtu.topic.domain.Topic;
import com.xjtu.topic.repository.TopicRepository;
import com.xjtu.utils.ResultUtil;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 处理subject学科数据
 *
 * @author yangkuan
 * @date 2018/03/06 16:01
 */

@Service
public class SubjectService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private DomainRepository domainRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Value("${subjectGexfpath}")
    private String gexfPath;

    /**
     * 插入学科信息
     *
     * @param subject 需要插入的学科
     * @return 插入结果
     */
    public Result insertSubject(Subject subject) {
        String subjectName = subject.getSubjectName();

        //插入学科的学科名必须存在且不能为空
        if (subjectName == null || ("").equals(subjectName) || subjectName.length() == 0) {
            logger.error("学科信息插入失败：学科名不存在或者为空");
            return ResultUtil.error(ResultEnum.SUBJECT_INSERT_ERROR.getCode(), ResultEnum.SUBJECT_INSERT_ERROR.getMsg());
        }
        //保证插入学科不存在数据库中
        if (subjectRepository.findBySubjectIdAndSubjectName(subject.getSubjectId(), subject.getSubjectName()) == null) {
            Subject subjectInsert = subjectRepository.save(subject);
            if (subjectInsert != null) {
                return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), subjectInsert);
            } else {
                logger.error("学科信息插入失败：数据库插入语句失败");
                return ResultUtil.error(ResultEnum.SUBJECT_INSERT_ERROR_2.getCode(), ResultEnum.SUBJECT_INSERT_ERROR_2.getMsg());
            }
        }

        //学科信息已经存在数据库
        logger.error("学科信息插入失败：插入已经存在的学科信息");
        return ResultUtil.error(ResultEnum.SUBJECT_INSERT_ERROR_1.getCode(), ResultEnum.SUBJECT_INSERT_ERROR_1.getMsg());
    }

    /**
     * 删除学科：根据学科Id
     *
     * @param subjectId 学科id
     * @return 删除结果
     */
    public Result deleteSubject(Long subjectId) {
        try {
            subjectRepository.delete(subjectId);
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "删除学科成功");
        } catch (Exception e) {
            logger.error("删除学科失败");
            return ResultUtil.error(ResultEnum.SUBJECT_DELETE_ERROR.getCode(), ResultEnum.SUBJECT_DELETE_ERROR.getMsg());
        }
    }

    /**
     * 更新学科：根据学科Id
     *
     * @param subjectId  学科Id
     * @param newSubject 新增加的学科信息
     * @return 更新结果
     */
    public Result updateSubject(Long subjectId, Subject newSubject) {
        try {
            subjectRepository.updateBySubjectId(subjectId, newSubject.getSubjectId(), newSubject.getSubjectName(), newSubject.getNote());
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "更新成功");
        } catch (Exception e) {
            logger.error("更新失败");
            return ResultUtil.error(ResultEnum.SUBJECT_UPDATE_ERROR.getCode(), ResultEnum.SOURCE_UPDATE_ERROR.getMsg());
        }
    }

    /**
     * 查询学科信息：所有学科信息
     *
     * @return 查询结果
     */
    public Result findSubjects() {
        List<Subject> subjects = subjectRepository.findAll();
        if (subjects.size() > 0) {
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), subjects);
        } else {
            logger.error("查询学科信息失败");
            return ResultUtil.error(ResultEnum.SUBJECT_SEARCH_ERROR.getCode(), ResultEnum.SUBJECT_INSERT_ERROR.getMsg());
        }
    }

    /**
     * 查询学科信息：根据学科Id
     *
     * @param subjectId 指定学科Id
     * @return 查询结果
     */
    public Result findSubjectById(Long subjectId) {
        try {
            Subject subject = subjectRepository.findBySubjectId(subjectId);
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), subject);
        } catch (Exception e) {
            logger.error("学科查询失败：没有学科信息记录");
            return ResultUtil.error(ResultEnum.SUBJECT_SEARCH_ERROR.getCode(), ResultEnum.SUBJECT_SEARCH_ERROR.getMsg());
        }
    }

    /**
     * 获取分页学科数据，按照学科Id排序 (不带查询条件)
     *
     * @param page     第几页的数据
     * @param size     每页数据的大小
     * @param ascOrder 是否升序
     * @return 分页排序的数据
     */
    public Result findSubjectByPagingAndSorting(Integer page, Integer size, Boolean ascOrder) {
        //页数从0开始计数
        Sort.Direction direction = Sort.Direction.ASC;
        if (!ascOrder) {
            direction = Sort.Direction.DESC;
        }
        Pageable pageable = new PageRequest(page, size, direction, "subjectId");
        Page<Subject> subjectPage = subjectRepository.findAll(pageable);
        return subjectPageJudge(subjectPage);
    }

    /**
     * 获得所有学科、课程和主题信息
     *
     * @return
     */
    public Result findSubjectTree() {
        //查找学科
        List<Subject> subjects = subjectRepository.findAll();
        List<Domain> allDomains = domainRepository.findAll();
        List<Topic> allTopics = topicRepository.findAll();
        //key:subjectId
        Map<Long, List<Domain>> domainsMap = new HashMap<>();
        for (Domain domain : allDomains) {
            Long subjectId = domain.getSubjectId();
            if (domainsMap.containsKey(subjectId)) {
                List<Domain> tmpDomains = domainsMap.get(subjectId);
                tmpDomains.add(domain);
                domainsMap.put(subjectId, tmpDomains);
            } else {
                List<Domain> tmpDomains = new ArrayList<>();
                tmpDomains.add(domain);
                domainsMap.put(subjectId, tmpDomains);
            }
        }
        //key:domainId
        Map<Long, List<Topic>> topicsMap = new HashMap<>();
        for (Topic topic : allTopics) {
            Long domainId = topic.getDomainId();
            if (topicsMap.containsKey(domainId)) {
                List<Topic> tmpTopics = topicsMap.get(domainId);
                tmpTopics.add(topic);
                topicsMap.put(domainId, tmpTopics);
            } else {
                List<Topic> tmpTopics = new ArrayList<>();
                tmpTopics.add(topic);
                topicsMap.put(domainId, tmpTopics);
            }
        }
        List<Map<String, Object>> subjectTrees = new ArrayList<>();
        for (Subject subject : subjects) {
            Map<String, Object> subjectTree = new HashMap<>(4);
            subjectTree.put("subjectId", subject.getSubjectId());
            subjectTree.put("subjectName", subject.getSubjectName());
            subjectTree.put("note", subject.getNote());
            //查找课程
            List<Domain> domains = domainsMap.get(subject.getSubjectId());
            List<Map<String, Object>> domainTrees = new ArrayList<>();
            if (domains != null && domains.size() != 0) {
                for (Domain domain : domains) {
                    Map<String, Object> domainTree = new HashMap<>(5);
                    domainTree.put("domainId", domain.getDomainId());
                    domainTree.put("domainName", domain.getDomainName());
                    domainTree.put("topics", topicsMap.get(domain.getDomainId()));
                    domainTrees.add(domainTree);
                }
            }
            subjectTree.put("domains", domainTrees);
            subjectTrees.add(subjectTree);
        }
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), subjectTrees);
    }

    /**
     * 获取分页学科数据，按照学科Id排序 (带查询条件：根据同一学科Id下的数据)
     *
     * @param page      第几页的数据
     * @param size      每页数据的大小
     * @param ascOrder  是否升序
     * @param subjectId 学科Id (查询条件)
     * @return 分页排序的数据
     */
    public Result findSubjectByIdAndPagingAndSorting(Integer page, Integer size, Boolean ascOrder, Long subjectId) {
        //页数从0开始计数
        Sort.Direction direction = Sort.Direction.ASC;
        if (!ascOrder) {
            direction = Sort.Direction.DESC;
        }
        Pageable pageable = new PageRequest(page, size, direction, "subjectId");

        Page<Subject> subjectPage = subjectRepository.findAll(new Specification<Subject>() {
            @Override
            public Predicate toPredicate(Root<Subject> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get("subjectId").as(Long.class), subjectId);
            }
        }, pageable);
        return subjectPageJudge(subjectPage);
    }


    /**
     * 根据分页查询的结果，返回不同的状态。
     * 1. totalElements为 0：说明没有查到数据，查询失败
     * 2. number大于totalPages：说明查询的页数大于最大页数，返回失败
     * 3. 成功返回分页数据
     *
     * @param subjectPage 分页查询结果
     * @return 返回查询结果
     */
    private Result subjectPageJudge(Page<Subject> subjectPage) {
        if (subjectPage.getNumber() >= subjectPage.getTotalPages()) {
            logger.error("学科分页查询失败：查询的页数超过最大页数");
            return ResultUtil.error(ResultEnum.SUBJECT_SEARCH_ERROR_2.getCode(), ResultEnum.SUBJECT_INSERT_ERROR_2.getMsg());
        } else if (subjectPage.getTotalElements() == 0) {
            logger.error("学科分页查询失败：没有学科信息记录");
            return ResultUtil.error(ResultEnum.SUBJECT_SEARCH_ERROR_1.getCode(), ResultEnum.SUBJECT_SEARCH_ERROR_1.getMsg());
        }
        //查询成功，返回查询的内容
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), subjectPage);
    }

    /**
     * 根据学科名，返回学科图谱
     *
     * @param subjectName
     * @return
     */
    public Result getSubjectGraphByName(String subjectName) {
        new File(gexfPath).mkdir();
        File gexfFile = new File(gexfPath + "\\" + subjectName + ".gexf");
        if (gexfFile.exists()) {
            // 如果存在，就直接调用本地gexf文件的内容，返回给前台
            try {
                String subjectGraph = FileUtils.readFileToString(gexfFile, "UTF-8");
                return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), subjectGraph);
            } catch (IOException e) {
                logger.error(e.getMessage());
                return ResultUtil.error(ResultEnum.SUBJECT_GRAPH_ERROR.getCode(), ResultEnum.SUBJECT_GRAPH_ERROR.getMsg());
            }
        }
        return ResultUtil.error(ResultEnum.SUBJECT_GRAPH_ERROR.getCode(), ResultEnum.SUBJECT_GRAPH_ERROR.getMsg());
    }

}
