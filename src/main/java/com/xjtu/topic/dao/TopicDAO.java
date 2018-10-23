package com.xjtu.topic.dao;

/**
 * 主题数据访问对象
 *
 * @author yangkuan
 * @date 2018/10/23
 */

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

    public Map<Long, Integer> countAssemblesByDomainIdGroupByTopicId(Long domainId) {
        List<Object[]> results = topicRepository.countAssemblesByDomainIdGroupByTopicId(domainId);
        Map<Long, Integer> output = new HashMap<>(results.size());
        for (Object[] objects : results) {
            Long key = ((BigInteger) objects[0]).longValue();
            Integer value = ((BigInteger) objects[1]).intValue();
            output.put(key, value);
        }
        return output;
    }

}
