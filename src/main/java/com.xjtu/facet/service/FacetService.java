package com.xjtu.facet.service;

import com.xjtu.assemble.domain.Assemble;
import com.xjtu.assemble.repository.AssembleRepository;
import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.domain.domain.Domain;
import com.xjtu.domain.repository.DomainRepository;
import com.xjtu.facet.domain.Facet;
import com.xjtu.facet.repository.FacetRepository;
import com.xjtu.topic.domain.Topic;
import com.xjtu.topic.repository.TopicRepository;
import com.xjtu.utils.ResultUtil;
import io.swagger.models.auth.In;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 处理facet分面数据
 * @author yangkuan
 * @date 2018/03/12 10:29
 */

@Service
public class FacetService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AssembleRepository assembleRepository;

    @Autowired
    private FacetRepository facetRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private DomainRepository domainRepository;

    /**
     * 插入分面信息
     * @param facet 需要插入的分面
     * @return 插入结果
     * */
    public Result insertFacet(Facet facet){
        String facetName = facet.getFacetName();
        if(facetName==null||facetName.length()==0||facetName.equals("")){
            logger.error("分面信息插入失败：分面名不存在或者为空");
            return ResultUtil.error(ResultEnum.FACET_INSERT_ERROR.getCode(),ResultEnum.FACET_INSERT_ERROR.getMsg());
        }
        try{
            facetRepository.save(facet);
            logger.info("分面信息插入成功");
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "分面信息插入成功");
        }
        catch (Exception err){
            logger.error("分面信息插入失败：分面插入语句执行失败");
            return ResultUtil.error(ResultEnum.FACET_INSERT_ERROR_1.getCode(),ResultEnum.FACET_INSERT_ERROR_1.getMsg());
        }
    }
    /**
     * 插入分面信息
     * @param domainName 课程名
     * @param topicName 主题名
     * @param facetName 分面名
     * @param facetLayer 分面所在层
     * @return 插入结果
     * */
    public Result insertFacetByDomainAndTopic(String domainName, String topicName
            , String facetName, Integer facetLayer, Long parentFacetId){
        Domain domain = domainRepository.findByDomainName(domainName);
        if(domain==null){
            logger.error("分面插入失败：对应课程不存在");
            return ResultUtil.error(ResultEnum.FACET_INSERT_ERROR_2.getCode(), ResultEnum.FACET_INSERT_ERROR_2.getMsg());
        }
        Long domainId = domain.getDomainId();
        Topic topic = topicRepository.findByDomainIdAndTopicName(domainId, topicName);
        if(topic==null){
            logger.error("分面插入失败：对应主题不存在");
            return ResultUtil.error(ResultEnum.FACET_INSERT_ERROR_3.getCode(), ResultEnum.FACET_INSERT_ERROR_3.getMsg());
        }
        Facet facet = new Facet();
        facet.setFacetName(facetName);
        facet.setFacetLayer(facetLayer);
        facet.setTopicId(topic.getTopicId());
        facet.setParentFacetId(parentFacetId);
        return insertFacet(facet);
    }

    /**
     *插入二级分面分面信息
     * @param domainName
     * @param topicName
     * @param firstLayerFacetName 一级分面名
     * @param secondLayerFacetName 二级分面名
     * @return
     */
    public Result insertSecondLayerFacet(String domainName, String topicName
            , String firstLayerFacetName, String secondLayerFacetName){
        Domain domain = domainRepository.findByDomainName(domainName);
        if(domain==null){
            logger.error("分面插入失败：对应课程不存在");
            return ResultUtil.error(ResultEnum.FACET_INSERT_ERROR_2.getCode(), ResultEnum.FACET_INSERT_ERROR_2.getMsg());
        }
        Long domainId = domain.getDomainId();
        Topic topic = topicRepository.findByDomainIdAndTopicName(domainId, topicName);
        if(topic==null){
            logger.error("分面插入失败：对应主题不存在");
            return ResultUtil.error(ResultEnum.FACET_INSERT_ERROR_3.getCode(), ResultEnum.FACET_INSERT_ERROR_3.getMsg());
        }
        Facet firstLayerFacet = facetRepository.findByTopicIdAndFacetNameAndFacetLayer(topic.getTopicId()
                ,firstLayerFacetName,1);
        if(firstLayerFacet==null){
            logger.error("分面插入失败：对应父分面不存在");
            return ResultUtil.error(ResultEnum.FACET_INSERT_ERROR_4.getCode(), ResultEnum.FACET_INSERT_ERROR_4.getMsg());
        }
        logger.error("分面插入成功");
        return insertFacetByDomainAndTopic(domainName,topicName,secondLayerFacetName
                ,2,firstLayerFacet.getFacetId());
    }

    /**
     * 在指定的课程、主题和一级分面、二级分面下添加三级分面
     * @param domainName
     * @param topicName
     * @param firstLayerFacetName
     * @param secondLayerFacetName
     * @param thirdLayerFacetName
     * @return
     */
    public Result insertThirdLayerFacet(String domainName, String topicName
            , String firstLayerFacetName, String secondLayerFacetName, String thirdLayerFacetName){
        Domain domain = domainRepository.findByDomainName(domainName);
        if(domain==null){
            logger.error("分面插入失败：对应课程不存在");
            return ResultUtil.error(ResultEnum.FACET_INSERT_ERROR_2.getCode(), ResultEnum.FACET_INSERT_ERROR_2.getMsg());
        }
        Long domainId = domain.getDomainId();
        Topic topic = topicRepository.findByDomainIdAndTopicName(domainId, topicName);
        if(topic==null){
            logger.error("分面插入失败：对应主题不存在");
            return ResultUtil.error(ResultEnum.FACET_INSERT_ERROR_3.getCode(), ResultEnum.FACET_INSERT_ERROR_3.getMsg());
        }
        //查询一级分面
        Facet firstLayerFacet = facetRepository.findByTopicIdAndFacetNameAndFacetLayer(topic.getTopicId()
                ,firstLayerFacetName,1);
        if(firstLayerFacet==null){
            logger.error("分面插入失败：对应父分面不存在");
            return ResultUtil.error(ResultEnum.FACET_INSERT_ERROR_4.getCode(), ResultEnum.FACET_INSERT_ERROR_4.getMsg());
        }
        //查询二级分面
        Facet secondLayerFacet = facetRepository.findByTopicIdAndFacetNameAndFacetLayer(topic.getTopicId()
                ,secondLayerFacetName,2);
        if(secondLayerFacet==null){
            logger.error("分面插入失败：对应父分面不存在");
            return ResultUtil.error(ResultEnum.FACET_INSERT_ERROR_4.getCode(), ResultEnum.FACET_INSERT_ERROR_4.getMsg());
        }
        logger.error("分面插入成功");
        return insertFacetByDomainAndTopic(domainName,topicName,thirdLayerFacetName
                ,3,secondLayerFacet.getFacetId());
    }

    /**
     * 删除分面信息
     * @param facetId 需要删除的分面Id
     * @return 删除结果
     * */
    public Result deleteFacet(Long facetId){
        if(facetRepository.findOne(facetId)==null){
            logger.error("分面信息删除失败：分面不存在");
            return ResultUtil.error(ResultEnum.FACET_DELETE_ERROR.getCode(), ResultEnum.FACET_DELETE_ERROR.getMsg());
        }
        try {
            facetRepository.delete(facetId);
            logger.info("分面信息删除成功");
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "分面信息删除成功");
        }
        catch (Exception err){
            logger.error("分面信息删除失败：分面删除语句执行失败");
            return ResultUtil.error(ResultEnum.FACET_DELETE_ERROR_1.getCode(), ResultEnum.FACET_DELETE_ERROR_1.getMsg());
        }
    }

    public Result deleteFacet(Iterable<? extends Facet> facets){
        try {
            List<Long> facetIds = new ArrayList<>();
            for(Facet facet:facets){
                facetIds.add(facet.getFacetId());
            }
            //删除碎片
            assembleRepository.deleteByFacetIdIsIn(facetIds);
            //删除分面
            facetRepository.delete(facets);
            logger.info("分面信息删除成功");
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(),ResultEnum.SUCCESS.getMsg(),"分面信息删除成功");

        }
        catch (Exception exception){
            logger.error("分面信息删除失败：删除语句执行失败");
            return ResultUtil.error(ResultEnum.FACET_DELETE_ERROR_4.getCode(),ResultEnum.FACET_DELETE_ERROR_4.getMsg());
        }
    }

    /**
     * 指定课程、主题和一级分面，删除一级分面
     * @param domainName 课程名
     * @param topicName 主题名
     * @param firstLayerFacetName 一级分面名
     * @return
     */
    public Result deleteFirstLayerFacet(String domainName, String topicName, String firstLayerFacetName){
        //查询课程
        Domain domain = domainRepository.findByDomainName(domainName);
        if(domain==null){
            logger.error("分面删除失败：对应课程不存在");
            return ResultUtil.error(ResultEnum.FACET_DELETE_ERROR_2.getCode(), ResultEnum.FACET_DELETE_ERROR_2.getMsg());
        }
        Long domainId = domain.getDomainId();
        //查询主题
        Topic topic = topicRepository.findByDomainIdAndTopicName(domainId, topicName);
        if(topic==null){
            logger.error("分面删除失败：对应主题不存在");
            return ResultUtil.error(ResultEnum.FACET_DELETE_ERROR_3.getCode(), ResultEnum.FACET_DELETE_ERROR_3.getMsg());
        }
        //删除一级分面，需要删除它的子分面
        //查找一级分面
        Facet firstLayerFacet = facetRepository.findByTopicIdAndFacetNameAndFacetLayer(topic.getTopicId(),firstLayerFacetName,1);
        //查找二级分面
        List<Facet> secondLayerFacets = facetRepository.findByParentFacetId(firstLayerFacet.getFacetId());
        //查找三级分面
        List<Facet> thirdLayerFacets = new ArrayList<>();
        for(Facet secondLayerFacet:secondLayerFacets){
            thirdLayerFacets.addAll(facetRepository.findByParentFacetId(secondLayerFacet.getFacetId()));
        }
        //所有分面合并
        List<Facet> facets = new ArrayList<>();
        facets.add(firstLayerFacet);
        facets.addAll(secondLayerFacets);
        facets.addAll(thirdLayerFacets);
        return deleteFacet(facets);
    }

    /**
     * 指定课程、主题和二级分面，删除二级分面
     * @param domainName 课程名
     * @param topicName 主题名
     * @param secondLayerFacetName 分面名
     * @return
     */
    public Result deleteSecondLayerFacet(String domainName, String topicName, String secondLayerFacetName){
        //查询课程
        Domain domain = domainRepository.findByDomainName(domainName);
        if(domain==null){
            logger.error("分面删除失败：对应课程不存在");
            return ResultUtil.error(ResultEnum.FACET_DELETE_ERROR_2.getCode(), ResultEnum.FACET_DELETE_ERROR_2.getMsg());
        }
        Long domainId = domain.getDomainId();
        //查询主题
        Topic topic = topicRepository.findByDomainIdAndTopicName(domainId, topicName);
        if(topic==null){
            logger.error("分面删除失败：对应主题不存在");
            return ResultUtil.error(ResultEnum.FACET_DELETE_ERROR_3.getCode(), ResultEnum.FACET_DELETE_ERROR_3.getMsg());
        }
        //删除二级分面，需要删除它的子分面
        //查询二级分面
        Facet secondLayerFacet = facetRepository.findByTopicIdAndFacetNameAndFacetLayer(topic.getTopicId(),secondLayerFacetName,2);
        //查询三级分面
        List<Facet> thirdLayerFacets = facetRepository.findByParentFacetId(secondLayerFacet.getFacetId());
        //合并分面
        List<Facet> facets = new ArrayList<>();
        facets.add(secondLayerFacet);
        facets.addAll(thirdLayerFacets);
        return deleteFacet(facets);
    }

    /**
     * 指定课程、主题和三级分面，删除三级分面
     * @param domainName
     * @param topicName
     * @param thirdLayerFacetName
     * @return
     */
    public Result deleteThirdLayerFacet(String domainName, String topicName, String thirdLayerFacetName){
        //查询课程
        Domain domain = domainRepository.findByDomainName(domainName);
        if(domain==null){
            logger.error("分面删除失败：对应课程不存在");
            return ResultUtil.error(ResultEnum.FACET_DELETE_ERROR_2.getCode(), ResultEnum.FACET_DELETE_ERROR_2.getMsg());
        }
        Long domainId = domain.getDomainId();
        //查询主题
        Topic topic = topicRepository.findByDomainIdAndTopicName(domainId, topicName);
        if(topic==null){
            logger.error("分面删除失败：对应主题不存在");
            return ResultUtil.error(ResultEnum.FACET_DELETE_ERROR_3.getCode(), ResultEnum.FACET_DELETE_ERROR_3.getMsg());
        }
        //删除三级分面
        Facet thirdLayerFacet = facetRepository.findByTopicIdAndFacetNameAndFacetLayer(topic.getTopicId(),thirdLayerFacetName,3);
        List<Facet> facets = new ArrayList<>();
        facets.add(thirdLayerFacet);
        return deleteFacet(facets);
    }

    /**
     * 更新分面信息:根据分面Id
     * @param facet 需要更新的分面
     * @return 更新结果
     * */
    public Result updateFacet(Facet facet){
        try {
            Long facetId = facet.getFacetId();
            String facetName = facet.getFacetName();
            Integer facetLayer = facet.getFacetLayer();
            Long parentFacetId = facet.getParentFacetId();
            Long topicId = facet.getTopicId();
            facetRepository.updateFacetById(facetId, facetName, facetLayer,
                    parentFacetId, topicId);
            logger.info("分面更新成功");
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "分面更新成功");
        }
        catch (Exception err){
            logger.error("分面更新失败：更新语句执行失败");
            return ResultUtil.error(ResultEnum.FACET_UPDATE_ERROR.getCode(), ResultEnum.FACET_UPDATE_ERROR.getMsg());
        }
    }

    /**
     * 更新某一层分面的分面名
     * @param domainName
     * @param topicName
     * @param facetName
     * @param newFacetName
     * @param facetLayer
     * @return
     */
    public Result updateSomeLayerFacet(String domainName, String topicName
            , String facetName, String newFacetName, Integer facetLayer){
        //查找旧分面
        Domain domain = domainRepository.findByDomainName(domainName);
        if(domain==null){
            logger.error("分面更新失败：对应课程不存在");
            return ResultUtil.error(ResultEnum.FACET_UPDATE_ERROR_1.getCode(), ResultEnum.FACET_UPDATE_ERROR_1.getMsg());
        }
        Long domainId = domain.getDomainId();
        Topic topic = topicRepository.findByDomainIdAndTopicName(domainId, topicName);
        if(topic==null){
            logger.error("分面更新失败：对应主题不存在");
            return ResultUtil.error(ResultEnum.FACET_UPDATE_ERROR_2.getCode(), ResultEnum.FACET_UPDATE_ERROR_2.getMsg());
        }
        Facet facet = facetRepository.findByTopicIdAndFacetNameAndFacetLayer(topic.getTopicId(),facetName, facetLayer);
        if(facet==null){
            logger.error("分面更新失败：原分面不存在");
            return ResultUtil.error(ResultEnum.FACET_UPDATE_ERROR_3.getCode(), ResultEnum.FACET_UPDATE_ERROR_3.getMsg());
        }
        facet.setFacetName(newFacetName);
        return updateFacet(facet);
    }

    /**
     * 查询所有分面信息
     * @return 查询结果
     * */
    public Result findFacets(){
        try{
            List<Facet> facets = facetRepository.findAll();
            logger.info("分面查询成功");
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), facets);
        }
        catch (Exception err){
            logger.error("分面查询失败：查询语句执行失败");
            return ResultUtil.error(ResultEnum.FACET_SEARCH_ERROR.getCode(), ResultEnum.FACET_SEARCH_ERROR.getMsg());
        }
    }

    /**
     *根据课程名及主题，查询推荐主题列表下所有分面(网院示范应用)
     * @param domainName
     * @param topicNames
     * @return
     */
    public Result findFacetsByDomainNameAndTopicNames(String domainName,String topicNames){
        List<String> topicNameList = Arrays.asList(topicNames.split(","));
        //查询主题
        List<Topic> topics = topicRepository.findByDomainName(domainName);
        if(topics==null){
            logger.error("分面更新失败：对应主题不存在");
            return ResultUtil.error(ResultEnum.FACET_UPDATE_ERROR_2.getCode(), ResultEnum.FACET_UPDATE_ERROR_2.getMsg());
        }

        Map<String,Object> result = new HashMap<>();
        for(Topic topic:topics){
            if(topicNameList.contains(topic.getTopicName())){
                //查询分面
                List<Facet> facets = facetRepository.findByTopicId(topic.getTopicId());
                List<Facet> firstLayerFacets = new ArrayList<>();
                List<Facet> secondLayerFacets = new ArrayList<>();
                List<Facet> thirdLayerFacets = new ArrayList<>();
                for(Facet facet:facets){
                    //一级分面
                    if(facet.getFacetLayer()==1){
                        firstLayerFacets.add(facet);
                    }
                    //二级分面
                    else if(facet.getFacetLayer()==2){
                        secondLayerFacets.add(facet);
                    }
                    else {
                        thirdLayerFacets.add(facet);
                    }
                }
                List<Map<String,Object>> firstLayerFacetNameContainChildrens = new ArrayList<>();
                //一级分面
                for(Facet firstLayerFacet:firstLayerFacets){
                    Map<String,Object> firstLayerFacetNameContainChildren = new HashMap<>();
                    firstLayerFacetNameContainChildren.put("firstLayerFacetName",firstLayerFacet.getFacetName());
                    firstLayerFacetNameContainChildren.put("topicName",topic.getTopicName());
                    List<Map<String,Object>> secondLayerFacetNameContainChildrens = new ArrayList<>();
                    //二级分面
                    for(Facet secondLayerFacet:secondLayerFacets){
                        if(secondLayerFacet.getParentFacetId()!=null&&secondLayerFacet.getParentFacetId().equals(firstLayerFacet.getFacetId())){
                            Map<String,Object> secondLayerFacetNameContainChildren = new LinkedHashMap<>();
                            secondLayerFacetNameContainChildren.put("secondLayerFacetName", secondLayerFacet.getFacetName());
                            secondLayerFacetNameContainChildren.put("topicName",topic.getTopicName());
                            //三级分面循环
                            List<Map<String,Object>> thirdLayerFacetNames = new ArrayList<>();
                            for(Facet thirdLayerFacet:thirdLayerFacets){
                                if(thirdLayerFacet.getParentFacetId()!=null&&thirdLayerFacet.getParentFacetId().equals(secondLayerFacet.getFacetId())){
                                    Map<String,Object> thirdLayerFacetName  = new LinkedHashMap<>();
                                    thirdLayerFacetName.put("thirdLayerFacetName",thirdLayerFacet.getFacetName());
                                    thirdLayerFacetName.put("topicName",topic.getTopicName());
                                    thirdLayerFacetNames.add(thirdLayerFacetName);
                                }
                            }
                            secondLayerFacetNameContainChildren.put("thirdLayerFacets",thirdLayerFacetNames);
                            secondLayerFacetNameContainChildrens.add(secondLayerFacetNameContainChildren);
                        }
                    }
                    firstLayerFacetNameContainChildren.put("secondLayerFacets",secondLayerFacetNameContainChildrens);
                    firstLayerFacetNameContainChildrens.add(firstLayerFacetNameContainChildren);
                }
                result.put(topic.getTopicName(),firstLayerFacetNameContainChildrens);
            }
        }
        logger.info("课程主题下的分面查询成功");
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(),ResultEnum.SUCCESS.getMsg(),result);
    }

    /**
     * 指定课程名和主题名，查询所有分面信息
     * @param domainName 课程名
     * @param topicName 主题名
     * @return 查询结果
     * */
    public Result findFacetsInTopic(String domainName, String topicName){
        Domain domain = domainRepository.findByDomainName(domainName);
        if(domain==null){
            logger.error("分面查询失败：对应课程不存在");
            return ResultUtil.error(ResultEnum.FACET_SEARCH_ERROR_3.getCode(), ResultEnum.FACET_SEARCH_ERROR_3.getMsg());
        }
        Long domainId = domain.getDomainId();
        Topic topic = topicRepository.findByDomainIdAndTopicName(domainId, topicName);
        if(topic==null){
            logger.error("分面查询失败：对应主题不存在");
            return ResultUtil.error(ResultEnum.FACET_SEARCH_ERROR_4.getCode(), ResultEnum.FACET_SEARCH_ERROR_4.getMsg());
        }
        List<Facet> facets = facetRepository.findByTopicId(topic.getTopicId());
        logger.info("分面查询成功");
        return  ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(),facets);

    }
    /**
     *根据课程名和主题名、一级分面名，获得该一级分面的所有分面信息
     * @param domainName
     * @param topicName
     * @param facetName
     * @param facetLayer
     * @return
     */
    public Result findFacetsInSomeLayerFacet(String domainName, String topicName, String facetName, Integer facetLayer){
        Domain domain = domainRepository.findByDomainName(domainName);
        if(domain==null){
            logger.error("分面查询失败：对应课程不存在");
            return ResultUtil.error(ResultEnum.FACET_SEARCH_ERROR_3.getCode(), ResultEnum.FACET_SEARCH_ERROR_3.getMsg());
        }
        Long domainId = domain.getDomainId();
        Topic topic = topicRepository.findByDomainIdAndTopicName(domainId, topicName);
        if(topic==null){
            logger.error("分面查询失败：对应主题不存在");
            return ResultUtil.error(ResultEnum.FACET_SEARCH_ERROR_4.getCode(), ResultEnum.FACET_SEARCH_ERROR_4.getMsg());
        }
        //查找对应的一级分面
        Facet facet = facetRepository.findByTopicIdAndFacetNameAndFacetLayer(topic.getTopicId()
                , facetName
                , facetLayer);
        //保存结果
        List<Map<String,Object>> facetMaps = new ArrayList<>();
        //查询分面下的子分面
        List<Facet> childFacets = facetRepository.findByParentFacetId(facet.getFacetId());
        List<Facet> grandChildFacets = new ArrayList<>();
        while (childFacets.size()!=0&&childFacets!=null){
            for(Facet childFacet:childFacets){
                Map<String,Object> facetMap = new HashMap<>(7);
                facetMap.put("facetName",childFacet.getFacetName());
                facetMap.put("facetLayer",childFacet.getFacetLayer());
                facetMap.put("parentFacetName",facetName);
                facetMap.put("topicId",topic.getTopicId());
                facetMap.put("topicName",topicName);
                facetMap.put("domainName",domainName);
                //添加至Map队列
                facetMaps.add(facetMap);
                //递归查找子分面
                grandChildFacets.addAll(facetRepository.findByParentFacetId(childFacet.getFacetId()));
            }
            childFacets = grandChildFacets;
        }
        logger.info("子分面查询成功");
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(),ResultEnum.SUCCESS.getMsg(),facetMaps);
    }
    /**
     * 指定课程名、主题名和父分面名，查询所有子分面数量
     * @param domainName 课程名
     * @param topicName 主题名
     * @param parentLayerFacetName 父分面名
     * @param facetLayer 父分面所在层
     * @return 查询结果
     * */
    public Result findChildLayerFacetNumber(String domainName, String topicName, String parentLayerFacetName ,Integer facetLayer){
        Domain domain = domainRepository.findByDomainName(domainName);
        if(domain==null){
            logger.error("分面查询失败：对应课程不存在");
            return ResultUtil.error(ResultEnum.FACET_SEARCH_ERROR_3.getCode(), ResultEnum.FACET_SEARCH_ERROR_3.getMsg());
        }
        Long domainId = domain.getDomainId();
        Topic topic = topicRepository.findByDomainIdAndTopicName(domainId, topicName);
        if(topic==null){
            logger.error("分面查询失败：对应主题不存在");
            return ResultUtil.error(ResultEnum.FACET_SEARCH_ERROR_4.getCode(), ResultEnum.FACET_SEARCH_ERROR_4.getMsg());
        }
        Facet parentLayerFacet = facetRepository.findByTopicIdAndFacetNameAndFacetLayer(topic.getTopicId(), parentLayerFacetName,facetLayer);
        if(parentLayerFacet == null||parentLayerFacet.getFacetLayer()!=1){
            logger.error("分面查询失败：对应课程和主题下没有对应分面");
            return ResultUtil.error(ResultEnum.FACET_SEARCH_ERROR_6.getCode(), ResultEnum.FACET_SEARCH_ERROR_6.getMsg());
        }
        List<Facet> childLayerFacets = facetRepository.findByParentFacetId(parentLayerFacet.getFacetId());
        if(childLayerFacets.size()==0){
            logger.error("分面查询失败：对应课程、主题下以及分面下没有子分面");
            return ResultUtil.error(ResultEnum.FACET_SEARCH_ERROR_7.getCode(), ResultEnum.FACET_SEARCH_ERROR_7.getMsg());
        }
        Map<String, Object> searchResult = new HashMap<String, Object>();
        searchResult.put("domainName", domainName);
        searchResult.put("topicName", topicName);
        searchResult.put("parentLayerFacetName", parentLayerFacetName);
        searchResult.put("childLayerFacetsNumber", childLayerFacets.size());
        logger.info("子分面查询成功");
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), searchResult);
    }

    /**
     * 指定课程名、主题名和父分面名，查询所有子分面数量
     * @param domainName 课程名
     * @param topicName 主题名
     * @return 查询结果
     * */
    public Result findSecondLayerFacetGroupByFirstLayerFacet(String domainName, String topicName){
        Domain domain = domainRepository.findByDomainName(domainName);
        if(domain==null){
            logger.error("分面查询失败：对应课程不存在");
            return ResultUtil.error(ResultEnum.FACET_SEARCH_ERROR_3.getCode(), ResultEnum.FACET_SEARCH_ERROR_3.getMsg());
        }
        Long domainId = domain.getDomainId();
        Topic topic = topicRepository.findByDomainIdAndTopicName(domainId, topicName);
        if(topic==null){
            logger.error("分面查询失败：对应主题不存在");
            return ResultUtil.error(ResultEnum.FACET_SEARCH_ERROR_4.getCode(), ResultEnum.FACET_SEARCH_ERROR_4.getMsg());
        }
        //查找到主题下的所有一级分面
        List<Facet> firstLayerFacets = facetRepository.findByTopicIdAndFacetLayer(topic.getTopicId(),1);
        List<Map<String,Object>> results = new ArrayList<>();
        //根据一级分面作为父分面查找对应的二级分面
        for(Facet firstLayerFacet : firstLayerFacets){
            Map<String,Object> result = new HashMap<>();
            result.put("firstLayerFacetName",firstLayerFacet.getFacetName());
            List<Facet> secondLayerFacets = facetRepository.findByParentFacetId(firstLayerFacet.getFacetId());
            result.put("secondLayerFacetsNumber",secondLayerFacets.size());
            List<String> secondLayerFacetNames = new ArrayList<>();
            for(Facet secondLayerFacet:secondLayerFacets){
                secondLayerFacetNames.add(secondLayerFacet.getFacetName());
            }
            result.put("secondLayerFacetNames",secondLayerFacetNames);
            results.add(result);
        }
        logger.info("分面查询成功");
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), results);
    }

    /**
     * 指定课程名、主题名和一级分面名，查询所有二级分面数量
     * @param domainName 课程名
     * @param topicName 主题名
     * @param firstLayerFacetName 一级分面名
     * @return 查询结果
     * */
    public Result findSecondLayerFacetNumber(String domainName, String topicName, String firstLayerFacetName){
        Result result = findChildLayerFacetNumber(domainName, topicName, firstLayerFacetName, 1);
        return result;
    }

    /**
     * 指定课程名、主题名和二级分面名，查询所有三级分面数量
     * @param domainName 课程名
     * @param topicName 主题名
     * @param secondLayerFacetName 二级分面名
     * @return 查询结果
     * */
    public Result findThirdLayerFacetNumber(String domainName, String topicName, String secondLayerFacetName){
        Result result = findChildLayerFacetNumber(domainName, topicName, secondLayerFacetName, 2);
        return result;
    }



    /**
     * 获取对应分面下的碎片数量
     * @param domainName
     * @param topicName
     * @param facetName
     * @return
     */
    public Result findAssembleNumberInFacet(String domainName, String topicName, String facetName, Integer facetLayer){
        Domain domain = domainRepository.findByDomainName(domainName);
        if(domain==null){
            logger.error("分面查询失败：对应课程不存在");
            return ResultUtil.error(ResultEnum.FACET_SEARCH_ERROR_3.getCode(), ResultEnum.FACET_SEARCH_ERROR_3.getMsg());
        }
        Long domainId = domain.getDomainId();
        Topic topic = topicRepository.findByDomainIdAndTopicName(domainId, topicName);
        if(topic==null){
            logger.error("分面查询失败：对应主题不存在");
            return ResultUtil.error(ResultEnum.FACET_SEARCH_ERROR_4.getCode(), ResultEnum.FACET_SEARCH_ERROR_4.getMsg());
        }
        Long topicId = topic.getTopicId();
        Facet facet = facetRepository.findByTopicIdAndFacetNameAndFacetLayer(topicId,facetName,facetLayer);
        if(facet==null){
            logger.error("分面查询失败：对应课程和主题下没有对应分面");
            return ResultUtil.error(ResultEnum.FACET_SEARCH_ERROR_6.getCode(), ResultEnum.FACET_SEARCH_ERROR_6.getMsg());
        }
        int assembleNumber = 0;
        List<Assemble> assembles = assembleRepository.findByFacetId(facet.getFacetId());
        assembleNumber += assembles.size();
        //查询该分面是否有子分面
        List<Facet> childFacets = facetRepository.findByParentFacetId(facet.getFacetId());
        while (childFacets.size()!=0&&childFacets!=null){
            List<Facet> grandchildFacets = new ArrayList<>();
            for(Facet childFacet:childFacets){
                List<Assemble> childAssembles = assembleRepository.findByFacetId(childFacet.getFacetId());
                assembleNumber += childAssembles.size();
                grandchildFacets.addAll(facetRepository.findByParentFacetId(childFacet.getFacetId()));
            }
            childFacets = grandchildFacets;
        }
        Map<String,Object> facetInformation = new HashMap<>(3);
        facetInformation.put("facetName",facetName);
        facetInformation.put("facetLayer",facet.getFacetLayer());
        facetInformation.put("assembleNumber",assembleNumber);
        logger.info("分面查询成功");
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(),facetInformation);
    }

    /**
     * 分页查询查询所有分面信息，按照分面Id排序 (不带查询条件)
     * @param page 第几页的数据
     * @param size 每页数据的大小
     * @param ascOrder 是否升序
     * @return 分页排序的数据
     */
    public Result findFacetByPagingAndSorting(Integer page, Integer size, boolean ascOrder){
        // 页数是从0开始计数的
        Sort.Direction direction = Sort.Direction.ASC;
        if (!ascOrder) {
            direction = Sort.Direction.DESC;
        }
        // 分页和排序条件，默认按照id排序
        Pageable pageable = new PageRequest(page, size, direction, "sourceId");
        Page<Facet> facetPage = facetRepository.findAll(pageable);
        return facetPageJudge(facetPage);
    }

    /**
     * 根据分页查询的结果，返回不同的状态。
     * 1. totalElements为 0：说明没有查到数据，查询失败
     * 2. number大于totalPages：说明查询的页数大于最大页数，返回失败
     * 3. 成功返回分页数据
     * @param facetPage 分页查询结果
     * @return 返回查询结果
     */
    public Result facetPageJudge(Page<Facet> facetPage) {
        if (facetPage.getTotalElements() == 0) {
            logger.error("分面分页查询失败：没有数据源记录");
            return ResultUtil.error(ResultEnum.FACET_SEARCH_ERROR_1.getCode(), ResultEnum.FACET_SEARCH_ERROR_1.getMsg());
        } else if (facetPage.getNumber() >= facetPage.getTotalPages()) {
            logger.error("分面分页查询失败：查询的页数超过最大页数");
            return ResultUtil.error(ResultEnum.FACET_SEARCH_ERROR_2.getCode(), ResultEnum.FACET_SEARCH_ERROR_2.getMsg());
        }
        // 返回查询的内容
        logger.info("数据源分页查询成功" + facetPage);
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), facetPage);
    }

}
