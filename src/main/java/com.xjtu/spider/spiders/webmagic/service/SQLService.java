package com.xjtu.spider.spiders.webmagic.service;

import com.xjtu.assemble.domain.Assemble;
import com.xjtu.assemble.repository.AssembleRepository;
import com.xjtu.domain.domain.Domain;
import com.xjtu.domain.repository.DomainRepository;
import com.xjtu.facet.domain.Facet;
import com.xjtu.facet.repository.FacetRepository;
import com.xjtu.source.repository.SourceRepository;
import com.xjtu.topic.domain.Topic;
import com.xjtu.topic.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库查询操作
 * @author yangkuan
 * @date 2018/05/18
 */
@Service
public class SQLService {

    @Autowired
    SourceRepository sourceRepository;

    @Autowired
    DomainRepository domainRepository;

    @Autowired
    TopicRepository topicRepository;

    @Autowired
    FacetRepository facetRepository;

    @Autowired
    AssembleRepository assembleRepository;

    public List<Map<String,Object>> getFacets(String domainName){
        Domain domain = domainRepository.findByDomainName(domainName);
        List<Topic> topics = topicRepository.findByDomainId(domain.getDomainId());
        List<Map<String,Object>> facetContainTopicAndDomains = new ArrayList<>();
        for(Topic topic:topics){
            List<Facet> facets = facetRepository.findByTopicId(topic.getTopicId());
            for(Facet facet:facets){
                Map<String,Object> facetContainTopicAndDomain = new HashMap<>(3);
                facetContainTopicAndDomain.put("facetName",facet.getFacetName());
                facetContainTopicAndDomain.put("topicName",topic.getTopicName());
                facetContainTopicAndDomain.put("domainName",domain.getDomainName());
                facetContainTopicAndDomains.add(facetContainTopicAndDomain);
            }
        }
        return facetContainTopicAndDomains;
    }

    /**
     * 查询对应分面的分面Id和数据源Id
     * @param facetMap
     * @return
     */
    public Map<String,Object> getFacet(Map<String,Object> facetMap){
        Long sourceId = sourceRepository.findBySourceName((String) facetMap.get("sourceName")).getSourceId();
        String domainName = (String) facetMap.get("domainName");
        String topicName = (String) facetMap.get("topicName");
        String facetName = (String) facetMap.get("facetName");
        Domain domain = domainRepository.findByDomainName(domainName);
        Topic topic = topicRepository.findByDomainIdAndTopicName(domain.getDomainId(),topicName);
        Facet facet = facetRepository.findByTopicIdAndFacetName(topic.getTopicId(),facetName);
        facetMap.put("sourceId",sourceId);
        facetMap.put("facetId",facet.getFacetId());
        return facetMap;
    }

    public void saveAssembles(List<Assemble> assembles){
        assembleRepository.save(assembles);
    }

}
