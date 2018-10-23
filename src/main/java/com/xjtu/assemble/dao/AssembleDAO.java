package com.xjtu.assemble.dao;

import com.xjtu.assemble.domain.Assemble;
import com.xjtu.education.domain.AssembleEvaluation;
import com.xjtu.education.repository.AssembleEvaluationRepository;
import com.xjtu.facet.domain.Facet;
import com.xjtu.facet.repository.FacetRepository;
import com.xjtu.source.domain.Source;
import com.xjtu.source.repository.SourceRepository;
import com.xjtu.topic.domain.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据访问
 *
 * @author yangkuan
 */
@Component
public class AssembleDAO {
    @Autowired
    private SourceRepository sourceRepository;

    @Autowired
    private FacetRepository facetRepository;

    @Autowired
    private AssembleEvaluationRepository assembleEvaluationRepository;

    /**
     * @param topicId
     * @return
     */
    public Map<Long, Facet> generateFacetMap(Long topicId) {
        List<Facet> facets = facetRepository.findByTopicId(topicId);
        Map<Long, Facet> facetMap = new HashMap<>(facets.size());
        for (Facet facet : facets) {
            facetMap.put(facet.getFacetId(), facet);
        }
        return facetMap;
    }

    /**
     * @param userId
     * @return
     */
    public Map<Long, Integer> generateAssembleEvaluationMap(Long userId) {
        //查询碎片评价
        List<AssembleEvaluation> assembleEvaluations = assembleEvaluationRepository.findByUserId(userId);
        Map<Long, Integer> assembleEvaluationsMap = new HashMap<>(assembleEvaluations.size());
        for (AssembleEvaluation assembleEvaluation : assembleEvaluations) {
            assembleEvaluationsMap.put(assembleEvaluation.getAssembleId(), assembleEvaluation.getValue());
        }
        return assembleEvaluationsMap;
    }

    public Map<Long, String> generateSourceMap() {
        List<Source> sources = sourceRepository.findAll();
        Map<Long, String> sourceMap = new HashMap<>();
        for (Source source : sources) {
            sourceMap.put(source.getSourceId(), source.getSourceName());
        }
        return sourceMap;
    }

    /**
     * 根据用户评价，计算碎片质量
     * 目前方法：m/n  其中，m是赞数，n是总评价数
     *
     * @param assembleId
     */
    public Map<String, Object> computePriority(Long assembleId) {
        Map<String, Object> result = new HashMap<>(3);
        List<AssembleEvaluation> assembleEvaluations = assembleEvaluationRepository.findByAssembleId(assembleId);
        if (assembleEvaluations == null || assembleEvaluations.size() == 0) {
            result.put("priority", new Double(0));
            result.put("positive", 0);
            result.put("negative", 0);
            return result;
        }
        int positiveCnt = 0;
        int negativeCnt = 0;
        for (AssembleEvaluation assembleQuality : assembleEvaluations) {
            if (assembleQuality.getValue().equals(1)) {
                positiveCnt++;
            } else if (assembleQuality.getValue().equals(-1)) {
                negativeCnt++;
            }
        }
        int value = positiveCnt - negativeCnt;
        result.put("priority", ((double) value) / assembleEvaluations.size());
        result.put("positive", positiveCnt);
        result.put("negative", negativeCnt);
        return result;
    }

    public Map<String, Object> generateAssembleMap(String sourceName, Topic topic
            , Facet firstLayerFacet, Facet secondLayerFacet, Assemble assemble
            , Map<String, Object> assembleEvaluation, Integer evaluation) {
        Map<String, Object> assembleMap = new HashMap<>(14);
        assembleMap.put("sourceName", sourceName);

        assembleMap.put("topicId", topic.getTopicId());
        assembleMap.put("topicName", topic.getTopicName());

        assembleMap.put("firstLayerFacetId", firstLayerFacet.getFacetId());
        assembleMap.put("firstLayerFacetName", firstLayerFacet.getFacetName());
        if (secondLayerFacet == null) {
            assembleMap.put("secondLayerFacetId", null);
            assembleMap.put("secondLayerFacetName", null);
        } else {
            assembleMap.put("secondLayerFacetId", secondLayerFacet.getFacetId());
            assembleMap.put("secondLayerFacetName", secondLayerFacet.getFacetName());
        }
        assembleMap.put("assembleId", assemble.getAssembleId());
        assembleMap.put("assembleContent", assemble.getAssembleContent());
        assembleMap.put("assembleText", assemble.getAssembleText());

        assembleMap.put("priority", assembleEvaluation.get("priority"));
        assembleMap.put("positive", assembleEvaluation.get("positive"));
        assembleMap.put("negative", assembleEvaluation.get("negative"));

        assembleMap.put("evaluation", evaluation);
        return assembleMap;
    }


}
