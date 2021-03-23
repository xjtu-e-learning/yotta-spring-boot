package com.xjtu.topic.extraction;

import com.xjtu.topic.domain.Topic;
import com.xjtu.topic.domain.TopicContainAssembleText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 进行主题抽取
 */
public class TopicExtraction {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // 设定主题统计处理阈值
    public static final int EXEC_THRESHOLD = 150;

    public static final int MIN_THRESHOLD = 10;

    public static final int MAx_THRESHOLD = 200;

    public List<Topic> extractAlgorithm(List<Topic> topicList, List<TopicContainAssembleText> assembleList, Map<Long, Integer> assembleCounts) {
        List<Topic> topicResult = topicList;

        Map<Integer, Long> sortMap = new TreeMap<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o2.compareTo(o1);
            }

        });

        for (Map.Entry<Long, Integer> e : assembleCounts.entrySet()) {
            sortMap.put(e.getValue(), e.getKey());
        }
        logger.info(sortMap.toString());

        List<Long> sortList = new ArrayList<>();
        for (Map.Entry<Integer, Long> e : sortMap.entrySet()) {
            sortList.add(e.getValue());
        }
        logger.info(sortList.toString());

        // List<HashMap<String, Float>> tf_idf_list = calculate_TF_IDF(topicList, assembleList);

        // System.out.println(tf_idf_list.get(0));

        if (topicResult.size() > EXEC_THRESHOLD) {
            topicResult = getTopTopic(topicResult, sortList);
            logger.info("经筛选后的主题列表大小为:" + topicResult.size());
            return topicResult;
        }
        return topicResult;
    }

    /**
     * 计算候选主题的TF-IDF值
     *
     * @param topicList
     * @param assembleList
     * @return
     */
    public List<HashMap<String, Float>> calculate_TF_IDF(List<Topic> topicList, List<TopicContainAssembleText> assembleList) {
        List<HashMap<String, Float>> tfidf = new ArrayList<>();
        List<HashMap<String, Float>> tf = calculate_TF(topicList, assembleList);
        HashMap<String, Float> idf = calculate_IDF(topicList, assembleList);
        for (HashMap<String, Float> tempTF : tf) {
            HashMap<String, Float> tempTFIDF = new HashMap<>();
            Double norm = 0.0;
            for (Topic topic : topicList) {
                String name = topic.getTopicName();
                tempTFIDF.put(name, tempTF.get(name) * idf.get(name));
                norm += Math.pow(tempTF.get(name) * idf.get(name), 2);
            }
//            if (norm != 0f)
//            {
//                for(Topic t: topicList)
//                {
//                    tempTFIDF.put(t.getTopicName(), (float)(tempTFIDF.get(t.getTopicName())/Math.sqrt(norm)));
//                }
//            }
            tfidf.add(tempTFIDF);
        }
        return tfidf;
    }

    /**
     * 判断文本中包含topic的个数
     *
     * @param topic
     * @param text
     * @return
     */
    public int containTopicNums(String topic, String text) {
        int count = (text.length() - text.replace(topic, "").length()) / topic.length();
        return count;
    }

    /**
     * 生成主题在文档下对应的tf值
     *
     * @param topicList
     * @param assembleList
     * @return
     */
    public List<HashMap<String, Float>> calculate_TF(List<Topic> topicList, List<TopicContainAssembleText> assembleList) {
        List<HashMap<String, Float>> tf = new ArrayList<>();
        for (TopicContainAssembleText topicContainAssembleText : assembleList) {
            HashMap<String, Float> everyTF = new HashMap<>();
            for (Topic topic : topicList) {
                Integer topicNum = containTopicNums(topic.getTopicName(), topicContainAssembleText.getText());
                everyTF.put(topic.getTopicName(), topicNum / (assembleList.size() + 0.0f));
            }
            tf.add(everyTF);
        }

        return tf;
    }

    /**
     * 生成候选主题的拟文档频率idf值
     *
     * @param topicList
     * @param assembleList
     * @return
     */
    public HashMap<String, Float> calculate_IDF(List<Topic> topicList, List<TopicContainAssembleText> assembleList) {
        HashMap<String, Float> idf = new HashMap<>();
        for (Topic topic : topicList) {
            int containNum = 0;
            for (TopicContainAssembleText topicContainAssembleText : assembleList) {
                if (topicContainAssembleText.getText().contains(topic.getTopicName())) {
                    containNum += 1;
                }
            }
            idf.put(topic.getTopicName(), (float) Math.log(assembleList.size() / (1.0 + containNum)));
        }
        return idf;
    }


    public List<Topic> getTopTopic(List<Topic> topicList, List<Long> sortList) {
        List<Topic> topicResult = new ArrayList<>();
        for (Long item : sortList) {
            for (Topic topic : topicList) {
                if (item.equals(topic.getTopicId())) {
                    topicResult.add(topic);
                    logger.info(topic.getTopicName() + " 已添加");
                }
            }
            if (topicResult.size() > MAx_THRESHOLD) {
                break;
            }
        }

        // 兜底策略，课程主题质量不佳，使用伪结果
        if (topicResult.size() < MIN_THRESHOLD) {
            int count = 0;
            topicResult = null;
            for (Topic topic : topicList) {
                topicResult.add(topic);
                count++;
                logger.info("redo: " + topic.getTopicName() + " 已添加");
                if (count > MIN_THRESHOLD * 5) {
                    break;
                }
            }
        }
        return topicResult;
    }
}
