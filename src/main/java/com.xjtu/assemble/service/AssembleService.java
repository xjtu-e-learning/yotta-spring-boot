package com.xjtu.assemble.service;

import com.xjtu.assemble.domain.Assemble;
import com.xjtu.assemble.domain.TemporaryAssemble;
import com.xjtu.assemble.repository.AssembleRepository;
import com.xjtu.assemble.repository.TemporaryAssembleRepository;
import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.domain.domain.Domain;
import com.xjtu.domain.repository.DomainRepository;
import com.xjtu.facet.domain.Facet;
import com.xjtu.facet.repository.FacetRepository;
import com.xjtu.source.domain.Source;
import com.xjtu.source.repository.SourceRepository;
import com.xjtu.topic.domain.Topic;
import com.xjtu.topic.repository.TopicRepository;
import com.xjtu.utils.JsonUtil;
import com.xjtu.utils.ResultUtil;
import org.apache.regexp.RE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 处理assemble碎片数据
 * @author yangkuan
 * @date 2018/03/15 14:49
 */

@Service
public class AssembleService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SourceRepository sourceRepository;

    @Autowired
    private DomainRepository domainRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private FacetRepository facetRepository;

    @Autowired
    private AssembleRepository assembleRepository;

    @Autowired
    private TemporaryAssembleRepository temporaryAssembleRepository;

    /**
     * 指定课程名、主题名，查询该主题下的碎片
     * @param domainName
     * @param topicName
     * @return
     */
    public Result findAssemblesInTopic(String domainName, String topicName){
        //查询课程
        Domain domain = domainRepository.findByDomainName(domainName);
        if(domain==null){
            logger.error("碎片查询失败：对应课程不存在");
            return ResultUtil.error(ResultEnum.Assemble_SEARCH_ERROR.getCode(), ResultEnum.Assemble_SEARCH_ERROR.getMsg());
        }
        Long domainId = domain.getDomainId();
        //查询主题
        Topic topic = topicRepository.findByDomainIdAndTopicName(domainId, topicName);
        if(topic==null){
            logger.error("碎片查询失败：对应主题不存在");
            return ResultUtil.error(ResultEnum.Assemble_SEARCH_ERROR_1.getCode(), ResultEnum.Assemble_SEARCH_ERROR_1.getMsg());
        }
        Long topicId = topic.getTopicId();
        //查询分面
        List<Facet> facets = facetRepository.findByTopicId(topicId);
        List<Assemble> assembles = new ArrayList<>();
        for(Facet facet:facets){
            //查询碎片
            assembles.addAll(assembleRepository.findByFacetId(facet.getFacetId()));
        }
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(),ResultEnum.SUCCESS.getMsg(),assembles);
    }

    /**
     * 指定课程名、主题名和一级分面名，查询一级分面下的碎片
     * @param domainName
     * @param topicName
     * @param firstLayerFacetName
     * @return
     */
    public Result findAssemblesInFirstLayerFacet(String domainName, String topicName, String firstLayerFacetName){
        //查询课程
        Domain domain = domainRepository.findByDomainName(domainName);
        if(domain==null){
            logger.error("碎片查询失败：对应课程不存在");
            return ResultUtil.error(ResultEnum.Assemble_SEARCH_ERROR.getCode(), ResultEnum.Assemble_SEARCH_ERROR.getMsg());
        }
        Long domainId = domain.getDomainId();
        //查询主题
        Topic topic = topicRepository.findByDomainIdAndTopicName(domainId, topicName);
        if(topic==null){
            logger.error("碎片查询失败：对应主题不存在");
            return ResultUtil.error(ResultEnum.Assemble_SEARCH_ERROR_1.getCode(), ResultEnum.Assemble_SEARCH_ERROR_1.getMsg());
        }
        Long topicId = topic.getTopicId();
        //查询一级分面
        Facet facet = facetRepository.findByTopicIdAndFacetNameAndFacetLayer(topicId,firstLayerFacetName,1);
        if(facet == null){
            logger.error("碎片查询失败：对应分面不存在");
            return ResultUtil.error(ResultEnum.Assemble_SEARCH_ERROR_2.getCode(), ResultEnum.Assemble_SEARCH_ERROR_2.getMsg());
        }
        //查询一级分面下的碎片
        List<Assemble> assembles = assembleRepository.findByFacetId(facet.getFacetId());

        //查询二级分面下的碎片
        List<Facet> secondLayerFacets = facetRepository.findByParentFacetIdAndFacetLayer(facet.getFacetId(),2);
        for(Facet secondLayerFacet:secondLayerFacets){
            //查询二级碎片并添加
            assembles.addAll(assembleRepository.findByFacetId(secondLayerFacet.getFacetId()));
            //此处未考虑三级分面
        }

        return ResultUtil.success(ResultEnum.SUCCESS.getCode(),ResultEnum.SUCCESS.getMsg(),assembles);
    }

    /**
     * 指定课程名、主题名和二级分面名，查询二级分面下的碎片
     * @param domainName
     * @param topicName
     * @param secondLayerFacetName
     * @return
     */
    public Result findAssemblesInSecondLayerFacet(String domainName, String topicName, String secondLayerFacetName){
        //查询课程
        Domain domain = domainRepository.findByDomainName(domainName);
        if(domain==null){
            logger.error("碎片查询失败：对应课程不存在");
            return ResultUtil.error(ResultEnum.Assemble_SEARCH_ERROR.getCode(), ResultEnum.Assemble_SEARCH_ERROR.getMsg());
        }
        Long domainId = domain.getDomainId();
        //查询主题
        Topic topic = topicRepository.findByDomainIdAndTopicName(domainId, topicName);
        if(topic==null){
            logger.error("碎片查询失败：对应主题不存在");
            return ResultUtil.error(ResultEnum.Assemble_SEARCH_ERROR_1.getCode(), ResultEnum.Assemble_SEARCH_ERROR_1.getMsg());
        }
        Long topicId = topic.getTopicId();
        //查询二级分面
        Facet facet = facetRepository.findByTopicIdAndFacetNameAndFacetLayer(topicId,secondLayerFacetName,2);
        if(facet==null){
            logger.error("碎片查询失败：对应分面不存在");
            return ResultUtil.error(ResultEnum.Assemble_SEARCH_ERROR_2.getCode(), ResultEnum.Assemble_SEARCH_ERROR_2.getMsg());
        }
        //此处未考虑三级分面
        //查询碎片
        List<Assemble> assembles = assembleRepository.findByFacetId(facet.getFacetId());
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(),ResultEnum.SUCCESS.getMsg(),assembles);
    }

    /**
     * 指定课程名、主题名和三级分面名，查询三级分面下的碎片
     * @param domainName
     * @param topicName
     * @param thirdLayerFacetName
     * @return
     */
    public Result findAssemblesInThirdLayerFacet(String domainName, String topicName, String thirdLayerFacetName){
        //查询课程
        Domain domain = domainRepository.findByDomainName(domainName);
        if(domain==null){
            logger.error("碎片查询失败：对应课程不存在");
            return ResultUtil.error(ResultEnum.Assemble_SEARCH_ERROR.getCode(), ResultEnum.Assemble_SEARCH_ERROR.getMsg());
        }
        Long domainId = domain.getDomainId();
        //查询主题
        Topic topic = topicRepository.findByDomainIdAndTopicName(domainId, topicName);
        if(topic==null){
            logger.error("碎片查询失败：对应主题不存在");
            return ResultUtil.error(ResultEnum.Assemble_SEARCH_ERROR_1.getCode(), ResultEnum.Assemble_SEARCH_ERROR_1.getMsg());
        }
        Long topicId = topic.getTopicId();
        //查询三级分面
        Facet facet = facetRepository.findByTopicIdAndFacetNameAndFacetLayer(topicId,thirdLayerFacetName,3);
        if(facet==null){
            logger.error("碎片查询失败：对应分面不存在");
            return ResultUtil.error(ResultEnum.Assemble_SEARCH_ERROR_2.getCode(), ResultEnum.Assemble_SEARCH_ERROR_2.getMsg());
        }
        //查询碎片
        List<Assemble> assembles = assembleRepository.findByFacetId(facet.getFacetId());
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(),ResultEnum.SUCCESS.getMsg(),assembles);
    }

    /**
     * 从暂存表中添加碎片到碎片表中，并删除暂存表中的碎片
     * @param domainName 课程名
     * @param topicName 主题名
     * @param facetName 分面名
     * @param facetLayer 分面所在层
     * @param temporaryAssembleId 暂存碎片id
     * @param sourceName 数据源名
     * @return
     */
    public Result insertAssemble(String domainName
            , String topicName
            , String facetName
            , Integer facetLayer
            , Long temporaryAssembleId
            , String sourceName){

        //查询数据源
        Source source = sourceRepository.findBySourceName(sourceName);
        if(source==null){
            logger.error("碎片插入失败：对应数据源不存在");
            return ResultUtil.error(ResultEnum.Assemble_INSERT_ERROR_6.getCode(), ResultEnum.Assemble_INSERT_ERROR_6.getMsg());
        }
        //查询课程
        Domain domain = domainRepository.findByDomainName(domainName);
        if(domain==null){
            logger.error("碎片插入失败：对应课程不存在");
            return ResultUtil.error(ResultEnum.Assemble_INSERT_ERROR_2.getCode(), ResultEnum.Assemble_INSERT_ERROR_2.getMsg());
        }
        Long domainId = domain.getDomainId();
        //查询主题
        Topic topic = topicRepository.findByDomainIdAndTopicName(domainId, topicName);
        if(topic==null){
            logger.error("碎片插入失败：对应主题不存在");
            return ResultUtil.error(ResultEnum.Assemble_INSERT_ERROR_3.getCode(), ResultEnum.Assemble_INSERT_ERROR_3.getMsg());
        }
        Long topicId = topic.getTopicId();
        //查询分面
        Facet facet = facetRepository.findByTopicIdAndFacetNameAndFacetLayer(topicId, facetName,facetLayer);
        if(facet==null){
            logger.error("碎片插入失败：对应分面不存在");
            return ResultUtil.error(ResultEnum.Assemble_INSERT_ERROR_4.getCode(), ResultEnum.Assemble_INSERT_ERROR_4.getMsg());
        }

        //查询碎片暂存表
        TemporaryAssemble temporaryAssemble = temporaryAssembleRepository.findOne(temporaryAssembleId);
        if(temporaryAssemble==null){
            logger.error("碎片插入失败：碎片暂存表不存在该碎片");
            return ResultUtil.error(ResultEnum.Assemble_INSERT_ERROR_5.getCode(), ResultEnum.Assemble_INSERT_ERROR_5.getMsg());
        }
        //从暂存表删除该碎片
        temporaryAssembleRepository.delete(temporaryAssembleId);
        //把该碎片添加进入碎片
        Assemble assemble = new Assemble(temporaryAssemble.getAssembleContent()
                , JsonUtil.parseHtmlText(temporaryAssemble.getAssembleContent()).text()
                , temporaryAssemble.getAssembleScratchTime()
                , facet.getFacetId()
                , source.getSourceId());
        assembleRepository.save(assemble);
        logger.info("碎片添加成功");
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(),ResultEnum.SUCCESS.getMsg(),"碎片添加成功");
    }

    /**
     * 添加碎片到碎片暂存表（temporaryAssemble）中
     * @param assembleContent 碎片内容
     * @param userName 用户名
     * @return
     */
    public Result insertTemporaryAssemble(String assembleContent, String userName){
        if(assembleContent==null||assembleContent.equals("")||assembleContent.length()==0){
            logger.error("暂存碎片添加失败：碎片内容为空");
            ResultUtil.error(ResultEnum.Assemble_INSERT_ERROR_1.getCode(),ResultEnum.Assemble_INSERT_ERROR_1.getMsg());
        }
        //获取系统当前时间
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String assembleScratchTime = simpleDateFormat.format(date);
        try {
            TemporaryAssemble temporaryAssemble = new TemporaryAssemble(assembleContent,assembleScratchTime,userName);
            temporaryAssembleRepository.save(temporaryAssemble);
            logger.info("暂存碎片添加成功");
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(),ResultEnum.SUCCESS.getMsg(),"碎片添加成功");
        }
        catch (Exception error){
            logger.error("暂存碎片添加失败：",error);
            return ResultUtil.error(ResultEnum.Assemble_INSERT_ERROR.getCode(),ResultEnum.Assemble_INSERT_ERROR.getMsg());
        }
    }

    /**
     * 根据用户名从暂存表中查询碎片
     * @param userName 用户名
     * @return
     */
    public Result findTemporaryAssemblesByUserName(String userName){
        try {
            List<TemporaryAssemble> temporaryAssembles = temporaryAssembleRepository.findByUserName(userName);
            logger.info("暂存碎片查询成功");
            return  ResultUtil.success(ResultEnum.SUCCESS.getCode(),ResultEnum.SUCCESS.getMsg(),temporaryAssembles);
        }
        catch (Exception error){
            logger.error("暂存碎片查询失败",error);
            return ResultUtil.error(ResultEnum.Assemble_SEARCH_ERROR_3.getCode(),ResultEnum.Assemble_SEARCH_ERROR_3.getMsg());
        }
    }

    /**
     * 根据碎片id从暂存表中查询碎片
     * @param assembleId 碎片id
     * @return
     */
    public Result findTemporaryAssembleById(Long assembleId){
        try {
            TemporaryAssemble temporaryAssemble = temporaryAssembleRepository.findOne(assembleId);
            logger.info("暂存碎片查询成功");
            return  ResultUtil.success(ResultEnum.SUCCESS.getCode(),ResultEnum.SUCCESS.getMsg(),temporaryAssemble);
        }
        catch (Exception error){
            logger.error("暂存碎片查询失败",error);
            return ResultUtil.error(ResultEnum.Assemble_SEARCH_ERROR_3.getCode(),ResultEnum.Assemble_SEARCH_ERROR_3.getMsg());
        }
    }

    /**
     * 根据碎片Id，更新暂存表中的碎片内容
     * @param assembleId 碎片id
     * @param assembleContent 碎片内容
     * @return
     */
    public Result updateTemporaryAssemble(Long assembleId, String assembleContent){
        if(assembleId==null){
            logger.error("碎片更新失败：碎片id不存在");
            return ResultUtil.error(ResultEnum.Assemble_UPDATE_ERROR.getCode(),ResultEnum.Assemble_UPDATE_ERROR.getMsg());
        }
        try {
            temporaryAssembleRepository.updateTemporaryAssemble(assembleId,assembleContent);
            logger.info("碎片更新成功");
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(),"碎片更新成功");
        }
        catch (Exception error){
            logger.error("碎片更新失败：更新语句执行失败",error);
            return ResultUtil.error(ResultEnum.Assemble_UPDATE_ERROR_1.getCode(),ResultEnum.Assemble_UPDATE_ERROR_1.getMsg());
        }
    }

    /**
     * 根据碎片Id，从碎片暂存表中删除碎片
     * @param assembleId 碎片id
     * @return
     */
    public Result deleteTemporaryAssemble(Long assembleId){
        try {
            temporaryAssembleRepository.delete(assembleId);
            logger.info("碎片删除成功");
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(),"碎片删除成功");
        }
        catch (Exception ex){
            logger.error("碎片删除失败：删除语句执行失败");
            return ResultUtil.error(ResultEnum.Assemble_DELETE_ERROR.getCode(),ResultEnum.Assemble_DELETE_ERROR.getMsg());
        }
    }

    /**
     * 根据碎片Id，从碎片表中删除碎片
     * @param assembleId 碎片id
     * @return
     */
    public Result deleteAssemble(Long assembleId){
        try {
            assembleRepository.delete(assembleId);
            logger.info("碎片删除成功");
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(),"碎片删除成功");
        }
        catch (Exception ex){
            logger.error("碎片删除失败：删除语句执行失败",ex);
            return ResultUtil.error(ResultEnum.Assemble_DELETE_ERROR.getCode(),ResultEnum.Assemble_DELETE_ERROR.getMsg(),ex);
        }
    }

}
