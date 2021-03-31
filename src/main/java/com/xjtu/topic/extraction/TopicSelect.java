package com.xjtu.topic.extraction;

import com.xjtu.topic.domain.Topic;

import java.util.*;

/**
 * Description
 * 输入：该领域课程的原始主题列表
 * 输出：筛选后的主题列表
 */
public class TopicSelect {

    public TopicSelect() {
        System.out.println("Start Topic Extraction Mission.");
    }

    /**
     * filterAlgorithm 主题过滤算法
     *
     * @param domainId
     * @param topicList
     * @return
     */
    public List<Topic> filterAlgorithm(Long domainId, List<Topic> topicList) {
        List<Topic> topicResult = new ArrayList<>();
        FilterUtils filterUtils = new FilterUtils();
        filterUtils.setHashSet();
        HashSet<String> commonSymbolSet = filterUtils.getSymbolSet();
        HashSet<String> objectSet = filterUtils.getObjectSet();
        for (Topic topic : topicList) {
            String name = topic.getTopicName();
            if (name.length() <= 10 && name.length() >= 2) {
                boolean filter = false;
                for (String element : commonSymbolSet) {
                    filter = !name.contains(element);
                    if (!filter) {
                        break;
                    }
                }

                if (!filter) {
                    continue;
                }

                for (String element : objectSet) {
                    filter = !name.contains(element);
                    if (!filter) {
                        break;
                    }
                }

                if (filter) {
                    topicResult.add(topic);
                }
            }
        }

        return topicResult;
    }
}
