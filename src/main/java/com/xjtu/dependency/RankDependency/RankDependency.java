package com.xjtu.dependency.RankDependency;

/**
 * 输入：主题列表
 * 输出：可信度高的主题对之间的依赖关系
 */

import com.xjtu.dependency.domain.Dependency;
import com.xjtu.topic.domain.TopicContainAssembleText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


public class RankDependency {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 计算主题间的认知关系
     * @param termList 输入的主题列表（每个主题包含内容信息用于计算主题间的认知关系）
     * @param MAX 认知关系数目设置上限
     * @return 主题间的认知关系
     */
    public List<Dependency> rankText(List<TopicContainAssembleText> termList, int MAX, boolean isEnglish) {
        List<Dependency> dependencies = new ArrayList<>();

        logger.info("Finish Hash...");
        logger.info("Start computing the hammingDistance...");
        HashMap<TwoTuple<TopicContainAssembleText, TopicContainAssembleText>, Double> disMap = new HashMap<>();
        for (int i = 0; i < termList.size() - 1; i++) {
            for (int j = i + 1; j < termList.size(); j++) {
                TopicContainAssembleText term1 = termList.get(i);
                TopicContainAssembleText term2 = termList.get(j);
                if (term1.getText().length() == 0 || term2.getText().length() == 0 || term1.getText().equals("") || term2.getText().equals("")) {
//                    Log.log("内容为空");
                } else {
                    double dis = 0.0;
                    if (isEnglish) {
                        dis = CosineSimilar.getSimilarityEn(term1.getText(), term2.getText());
                    } else {
                        dis = CosineSimilar.getSimilarity(term1.getText(), term2.getText());
                    }
//				    logger.info(dis+"");
                    TwoTuple<TopicContainAssembleText, TopicContainAssembleText> twoTuple = new TwoTuple<>(term1, term2);
                    disMap.put(twoTuple, dis);
                }
            }
        }
        logger.info("Finish computing the hammingDistance...");
        logger.info("Start ranking...");

        List<Map.Entry<TwoTuple<TopicContainAssembleText, TopicContainAssembleText>, Double>> infoIds = new ArrayList<>(disMap.entrySet());
        Collections.sort(infoIds, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        logger.info("Finish ranking!");
        logger.info("Start printing...");
        
        int end = MAX;
        if (infoIds.size() < end) end = infoIds.size();
        logger.info("end:" + end);
        for (int k = 0; k < end; k++) {
            TwoTuple<TopicContainAssembleText, TopicContainAssembleText> twoTuple = infoIds.get(k).getKey();
            String term1_term2 = twoTuple.first.getTopicId() + "_" + twoTuple.second.getTopicId();
            float dis = Float.parseFloat(infoIds.get(k).getValue().toString());
            System.out.println(term1_term2 + ": " + dis);
            if (new Float(dis).isNaN()) {
                dis = 0.5f;
            }
            System.out.println(dis);
            Dependency dependency = new Dependency(twoTuple.first.getTopicId(), twoTuple.second.getTopicId(), dis, twoTuple.first.getDomainId());
            //Dependency dependency = new Dependency(twoTuple.first.getTopicId(), twoTuple.second.getTopicId(), dis, twoTuple.first.getDomainId());
            dependencies.add(dependency);
        }
        logger.info("Finish printing...");
        return dependencies;

    }
}
