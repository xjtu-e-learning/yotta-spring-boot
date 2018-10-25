package com.xjtu.topic.dao;

/**
 * 主题数据访问对象
 *
 * @author yangkuan
 * @date 2018/10/23
 */

import com.xjtu.dependency.repository.DependencyRepository;
import com.xjtu.topic.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TopicDAO {

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private DependencyRepository dependencyRepository;

    public Map<Long, Integer> countAssemblesByDomainIdGroupByTopicId(Long domainId) {
        List<Object[]> results = topicRepository.countAssemblesByDomainIdGroupByTopicId(domainId);
        return parse(results);
    }

    /**
     * 计算主题入度
     *
     * @param domainId
     * @return
     */
    public Map<Long, Integer> countInDegreeByTopicId(Long domainId) {
        List<Object[]> counts = dependencyRepository.countInDegreeByDomainId(domainId);
        return parse(counts);
    }

    /**
     * 计算主题出度
     *
     * @param domainId
     * @return
     */
    public Map<Long, Integer> countOutDegreeByTopicId(Long domainId) {
        List<Object[]> counts = dependencyRepository.countOutDegreeByDomainId(domainId);
        return parse(counts);
    }

    /**
     * 数据转换
     *
     * @param datas
     * @return
     */
    private Map<Long, Integer> parse(List<Object[]> datas) {
        Map<Long, Integer> output = new HashMap<>(datas.size());
        for (Object[] objects : datas) {
            Long key = ((BigInteger) objects[0]).longValue();
            Integer value = ((BigInteger) objects[1]).intValue();
            output.put(key, value);
        }
        return output;
    }

}
