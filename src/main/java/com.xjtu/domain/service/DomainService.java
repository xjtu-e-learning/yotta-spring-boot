package com.xjtu.domain.service;

import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.domain.domain.Domain;
import com.xjtu.domain.repository.DomainRepository;
import com.xjtu.facet.domain.Facet;
import com.xjtu.facet.repository.FacetRepository;
import com.xjtu.subject.domain.Subject;
import com.xjtu.subject.domain.SubjectContainDomain;
import com.xjtu.subject.repository.SubjectRepository;
import com.xjtu.topic.domain.Topic;
import com.xjtu.topic.repository.TopicRepository;
import com.xjtu.utils.ResultUtil;
import org.apache.xalan.xsltc.DOM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.*;

/**
 * 处理domain课程数据
 * @author yangkuan
 * @date 2018/03/07 13:14
 */

@Service
public class DomainService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private DomainRepository domainRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private FacetRepository facetRepository;


    /**
     * 插入课程信息
     * @param domain 需要插入的课程
     * @return 插入结果
     * */
    public Result insertDomain(Domain domain){
        String domainName = domain.getDomainName();

        //插入课程的课程名必须存在且不能为空
        if(domainName == null||("").equals(domainName)||domainName.length()==0){
            logger.error("课程信息插入失败：课程名不存在或者为空");
            return ResultUtil.error(ResultEnum.DOMAIN_INSERT_ERROR.getCode(),ResultEnum.DOMAIN_INSERT_ERROR.getMsg());
        }
        //保证插入课程不存在数据库中
        else if(domainRepository.findByDomainIdAndDomainName(domain.getDomainId(), domainName)==null){
            Domain domainInsert = domainRepository.save(domain);
            if(domainInsert != null){
                logger.info("插入课程信息成功");
                return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "课程:"+domainName+"插入成功");
            }
            else {
                logger.error("课程信息插入失败：数据库插入语句失败");
                return ResultUtil.error(ResultEnum.DOMAIN_INSERT_ERROR_2.getCode(), ResultEnum.DOMAIN_INSERT_ERROR_2.getMsg());
            }
        }
        //课程已经在数据库中
        logger.error("课程信息插入失败：课程已在数据库中");
        return ResultUtil.error(ResultEnum.DOMAIN_INSERT_ERROR_1.getCode(), ResultEnum.DOMAIN_INSERT_ERROR_1.getMsg());
    }
    /**
     * 指定课程名，插入一门课程
     * @param domainName 需要插入的课程名
     * @return 插入结果
     * */
    public Result insertDomainByName(String domainName){
        Domain domain = new Domain();
        domain.setDomainName(domainName);
        return insertDomain(domain);
    }

    /**
     * 删除课程：根据课程Id
     * @param domainId 课程id
     * @return 删除结果
     */
    public Result deleteDomain(Long domainId){
        try {
            domainRepository.delete(domainId);
            logger.info("删除课程成功");
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "删除课程成功");
        }
        catch (Exception e){
            logger.error("删除课程失败");
            return ResultUtil.error(ResultEnum.DOMAIN_DELETE_ERROR.getCode(), ResultEnum.DOMAIN_DELETE_ERROR.getMsg());
        }
    }


    /**
     * 更新课程：根据课程Id
     * @param domainId 课程Id
     * @param newDomain 新增加的课程信息
     * @return 更新结果
     */
    public Result updateDomain(Long domainId, Domain newDomain){
        Long newDomainId = newDomain.getDomainId();
        String newDomainName = newDomain.getDomainName();
        Long newSubjectId = newDomain.getSubjectId();
        if(newDomainName==null||newDomainName.equals("")||newDomainName.length()==0){
            logger.error("课程更新失败：课程名不存在或为空");
            return ResultUtil.error(ResultEnum.DOMAIN_UPDATE_ERROR_1.getCode(),ResultEnum.DOMAIN_UPDATE_ERROR_1.getMsg());
        }
        try {
            domainRepository.updateByDomainId(domainId, newDomainId, newDomainName, newSubjectId);
            logger.info("课程更新成功");
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(),ResultEnum.SUCCESS.getMsg(),"课程更新成功");
        }
        catch (Exception err){
            logger.error("课程更新失败：更新语句执行失败");
            return ResultUtil.error(ResultEnum.DOMAIN_UPDATE_ERROR.getCode(),ResultEnum.DOMAIN_UPDATE_ERROR.getMsg());
        }
    }

    /**
     * 更新课程名：根据课程Id
     * @param oldDomainName 课程Id
     * @param newDomainName 新增加的课程信息
     * @return 更新结果
     */
    public Result updateDomainByDomainName(String oldDomainName, String newDomainName){
        if(newDomainName==null||newDomainName.equals("")||newDomainName.length()==0){
            logger.error("课程名更新失败：新课程名不存在或为空");
            return ResultUtil.error(ResultEnum.DOMAIN_UPDATE_ERROR_1.getCode(),ResultEnum.DOMAIN_UPDATE_ERROR_1.getMsg());
        }
        try {
            domainRepository.updateDomainByDomainName(oldDomainName, newDomainName);
            logger.info("课程名更新成功");
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(),ResultEnum.SUCCESS.getMsg(),"课程名更新成功");
        }
        catch (Exception err){
            logger.error("课程名更新失败：更新语句执行失败");
            return ResultUtil.error(ResultEnum.DOMAIN_UPDATE_ERROR.getCode(),ResultEnum.DOMAIN_UPDATE_ERROR.getMsg());
        }
    }


    /**
     * 查询课程：所有课程信息
     * @return 查询结果
     */
    public Result findDomains(){
        List<Domain> domains = domainRepository.findAll();
        if(domains.size()>0){
            logger.info("课程查询成功");
            domains.forEach(domain -> logger.info("查询结果为：" + domain.toString()));
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), domains);
        }
        else {
            logger.error("课程查询失败：没有课程记录");
            return ResultUtil.error(ResultEnum.DOMAIN_SEARCH_ERROR.getCode(),ResultEnum.DOMAIN_SEARCH_ERROR.getMsg());
        }
    }

    /**
     * 根据课程名，查询该课程下面主题，以及分面按树状组织
     * @param domainName 课程名
     * @return
     */
    public Result findDomainTreeByDomainName(String domainName){
        Domain domain = domainRepository.findByDomainName(domainName);
        if(domain==null){
            logger.error("分面查询失败：对应课程不存在");
            return ResultUtil.error(ResultEnum.FACET_SEARCH_ERROR_3.getCode(), ResultEnum.FACET_SEARCH_ERROR_3.getMsg());
        }
        List<Topic> topics = topicRepository.findByDomainId(domain.getDomainId());
        List<Map<String,Object>> topicNameContainFacets = new ArrayList<>();
        //1.主题循环
        for(Topic topic:topics){
            Map<String,Object> topicNameContainFacet = new LinkedHashMap<>();
            //设置课程名
            topicNameContainFacet.put("domainName",domainName);
            //设置主题名
            topicNameContainFacet.put("topicName",topic.getTopicName());
            //设置主题下分面名
            List<Facet> firstLayerFacets = facetRepository.findByTopicIdAndFacetLayer(topic.getTopicId(),1);
            List<Map<String,Object>> firstLayerFacetNameContainChildrens = new ArrayList<>();
            //2.一级分面循环
            for(Facet firstLayerFacet:firstLayerFacets){
                Map<String,Object> firstLayerFacetNameContainChildren = new LinkedHashMap<>();
                firstLayerFacetNameContainChildren.put("firstLayerFacetName",firstLayerFacet.getFacetName());
                //一级分面下的二级分面获取
                List<Facet> secondLayerFacets = facetRepository.findByParentFacetIdAndFacetLayer(firstLayerFacet.getFacetId()
                        ,2);
                List<Map<String,Object>> secondLayerFacetNameContainChildrens = new ArrayList<>();
                //3.二级分面循环
                for(Facet secondLayerFacet:secondLayerFacets){
                    Map<String,Object> secondLayerFacetNameContainChildren = new LinkedHashMap<>();
                    secondLayerFacetNameContainChildren.put("secondLayerFacetName", secondLayerFacet.getFacetName());
                    //4.三级分面循环
                    List<Map<String,Object>> thirdLayerFacetNames = new ArrayList<>();
                    //二级分面下的三级分面获取
                    List<Facet> thirdLayerFacets = facetRepository.findByParentFacetIdAndFacetLayer(secondLayerFacet.getFacetId()
                            ,3);
                    for(Facet thirdLayerFacet:thirdLayerFacets){
                        Map<String,Object> thirdLayerFacetName  = new LinkedHashMap<>();
                        thirdLayerFacetName.put("thirdLayerFacetName",thirdLayerFacet.getFacetName());
                        thirdLayerFacetNames.add(thirdLayerFacetName);
                    }
                    secondLayerFacetNameContainChildren.put("thirdLayerFacets",thirdLayerFacetNames);
                    secondLayerFacetNameContainChildrens.add(secondLayerFacetNameContainChildren);
                }
                firstLayerFacetNameContainChildren.put("secondLayerFacets",secondLayerFacetNameContainChildrens);
                firstLayerFacetNameContainChildrens.add(firstLayerFacetNameContainChildren);
            }
            topicNameContainFacet.put("firstLayerFacets",firstLayerFacetNameContainChildrens);
            topicNameContainFacets.add(topicNameContainFacet);
        }
        //考虑有没有什么降低复杂度的方法，此处显然循环太多（4层）
        logger.info("课程树状信息查询成功");
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(),ResultEnum.SUCCESS.getMsg(),topicNameContainFacets);
    }
    /**
     * 查询课程：根据课程Id
     * @param domainId 指定课程Id
     * @return 查询结果
     */
    public Result findDomainById(Long domainId){
        try{
            Domain domain = domainRepository.findOne(domainId);
            logger.info("查询成功" + domain.toString());
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), domain);
        }
        catch (Exception err){
            logger.error("课程查询失败：没有课程记录");
            return ResultUtil.error(ResultEnum.DOMAIN_SEARCH_ERROR.getCode(), ResultEnum.DOMAIN_SEARCH_ERROR.getMsg());
        }
    }

    /**
     * 查询课程：根据学科Id
     * @param subjectId 指定学科Id
     * @return 查询结果
     */
    public Result findDomainsBySubjectId(Long subjectId){
        try{
            List<Domain> domains = domainRepository.findBySubjectId(subjectId);
            domains.forEach(domain -> logger.info("查询结果为：" + domain.toString()));
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), domains);
        }
        catch (Exception err){
            logger.error("课程查询失败：没有课程记录");
            return ResultUtil.error(ResultEnum.DOMAIN_SEARCH_ERROR.getCode(), ResultEnum.DOMAIN_SEARCH_ERROR.getMsg());
        }
    }


    /**
     * 获取分页课程数据，按照课程Id排序 (不带查询条件)
     * @param page 第几页的数据
     * @param size 每页数据的大小
     * @param ascOrder 是否升序
     * @return 分页排序的数据
     */
    public Result findDomainByPagingAndSorting(Integer page, Integer size, boolean ascOrder){
        Sort.Direction direction = Sort.Direction.ASC;
        if(!ascOrder){
            direction = Sort.Direction.DESC;
        }
        Pageable pageable = new PageRequest(page, size, direction, "domainId");
        Page<Domain> domainPage = domainRepository.findAll(pageable);
        return domainPageJudge(domainPage);
    }


    /**
     * 获取分页课程数据，按照课程Id排序 (带查询条件：根据同一学科Id下的数据)
     * @param page 第几页的数据
     * @param size 每页数据的大小
     * @param ascOrder 是否升序
     * @param subjectId 学科Id (查询条件)
     * @return 分页排序的数据
     */
    public Result findDomainByIdAndPagingAndSorting(Integer page, Integer size, boolean ascOrder, Long subjectId){
        //页数从0开始计数
        Sort.Direction direction = Sort.Direction.ASC;
        if(!ascOrder){
            direction = Sort.Direction.DESC;
        }
        Pageable pageable = new PageRequest(page, size, direction, "domainId");
        Page<Domain> domainPage = domainRepository.findAll(new Specification<Domain>() {
            @Override
            public Predicate toPredicate(Root<Domain> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate p = criteriaBuilder.equal(root.get("subjectId").as(Long.class),subjectId);
                return p;
            }
        }, pageable);
        return domainPageJudge(domainPage);
    }

    /**
     * 根据分页查询的结果，返回不同的状态。
     * 1. totalElements为 0：说明没有查到数据，查询失败
     * 2. number大于totalPages：说明查询的页数大于最大页数，返回失败
     * 3. 成功返回分页数据
     * @param domainPage 分页查询结果
     * @return 返回查询结果
     */
    public Result domainPageJudge(Page<Domain> domainPage){
        if(domainPage.getNumber() >= domainPage.getTotalPages()){
            logger.error("课程分页查询失败：查询的页数超过最大页数");
            return ResultUtil.error(ResultEnum.DOMAIN_SEARCH_ERROR_2.getCode(),ResultEnum.DOMAIN_SEARCH_ERROR_2.getMsg());
        }
        else if(domainPage.getTotalElements()==0){
            logger.error("课程分页查询失败：没有课程信息记录");
            return ResultUtil.error(ResultEnum.DOMAIN_SEARCH_ERROR_1.getCode(),ResultEnum.DOMAIN_SEARCH_ERROR_1.getMsg());
        }
        //查询成功，返回查询的内容
        logger.info("课程分页查询成功"+domainPage);
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), domainPage);
    }
    /**
     * 查询课程：由学科Id组织
     * @return 查询结果
     */
    public Result findDomainsGroupBySubject(){
        List<Subject> subjects = subjectRepository.findAll();
        if(subjects.size()==0){
            logger.error("课程查询失败：没有课程信息记录");
            return ResultUtil.error(ResultEnum.DOMAIN_SEARCH_ERROR.getCode(),ResultEnum.DOMAIN_SEARCH_ERROR.getMsg());
        }
        List<SubjectContainDomain> subjectContainDomains = new ArrayList<>();
        for (Subject subject:subjects){
            List<Domain> domains = domainRepository.findBySubjectId(subject.getSubjectId());
            SubjectContainDomain subjectContainDomain = new SubjectContainDomain(subject.getSubjectId(),
                    subject.getSubjectName(), subject.getNote(), domains);
            subjectContainDomains.add(subjectContainDomain);
        }
        logger.info("课程查询成功");
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), subjectContainDomains);
    }

    /**
     * 课程数量统计
     * @return 查询结果
     */
    public Result countDomains(){
        try {
            List<Domain> domains = domainRepository.findAll();
            Map<String, Object> domainMap = new HashMap<>();
            domainMap.put("domainNumber",domains.size());
            logger.info("课程数量统计查询成功");
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), domainMap);
        }
        catch (Exception err){
            logger.error("课程数量统计查询失败");
            return ResultUtil.error(ResultEnum.DOMAIN_SEARCH_ERROR.getCode(), ResultEnum.DOMAIN_SEARCH_ERROR.getMsg());
        }
    }



}
