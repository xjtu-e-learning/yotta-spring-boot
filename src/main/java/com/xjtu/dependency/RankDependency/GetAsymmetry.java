package com.xjtu.dependency.RankDependency;

/**
 * 输入：主题列表
 * 输出：可信度高的主题对之间的依赖关系
 */

import com.xjtu.dependency.domain.Dependency;
import com.xjtu.topic.domain.Topic;
import com.xjtu.topic.domain.TopicContainAssembleText;
import com.xjtu.utils.CsvUtil;
import io.swagger.models.auth.In;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
/**
 * @Author: makexin
 * @Date: 2021/3/2021:03
 */
public class GetAsymmetry {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private Double THD = 0.5;
    private Double THD0 = 0.0;

    //计算所有知识主题对文本间的不对称性值
    //计算公式：文本A中出现的知识主题对于所有知识主题来说与A的重要性（包括文本的重要性以及知识主题的相似性两方面）作为A的值，同理B也有相应的计算值。A值-B值>阈值，则A先学习
    public List<Dependency> AsyDependency(List<Topic> topicList, List<TopicContainAssembleText> termList) {
        List<Dependency> dependencies = new ArrayList<>();
        List<Dependency> dependenciesAll = new ArrayList<>();

        logger.info("Finish Hash...");
        logger.info("Start computing the hammingDistance...");
        HashMap<TwoTuple<TopicContainAssembleText, TopicContainAssembleText>, Double> disMap = new HashMap<>();
        List<HashMap<String, Float>> tfidf = getTFIDF(topicList, termList);  //计算每个知识主题对每个文本的tfidf值。list的顺序是所有文本资源的顺序，每一个hashmap的key是知识主题
        //任意两个知识主题对间
        for (int i = 0; i < termList.size() - 1; i++) {
            for (int j = i + 1; j < termList.size(); j++) {
                TopicContainAssembleText term1 = termList.get(i);
                TopicContainAssembleText term2 = termList.get(j);
                if (term1.getText().length() == 0 || term2.getText().length() == 0 || term1.getText().equals("") || term2.getText().equals("")) {
//                    Log.log("内容为空");
                } else {

                    List<Double> asyResult = computeAsy(topicList, term1, term2, tfidf.get(i), tfidf.get(j));  //调用计算知识主题对
                    double Afirst = asyResult.get(0);
                    double Bfirst = asyResult.get(1);
//                    System.out.println("Afirst: " + Afirst + "     Bfirst: " + Bfirst);
                    if(Afirst > THD)
                    {
                        Dependency dependency = new Dependency(term1.getTopicId(), term2.getTopicId(), (float)Afirst, term1.getDomainId());
                        dependencies.add(dependency);
                    }
                    if(Bfirst > THD)
                    {
                        Dependency dependency = new Dependency(term2.getTopicId(), term1.getTopicId(), (float)Bfirst, term1.getDomainId());
                        dependencies.add(dependency);
                    }
                    if(Afirst > THD0)
                    {
                        Dependency dependency = new Dependency(term1.getTopicId(), term2.getTopicId(), (float)Afirst, term1.getDomainId());
                        dependenciesAll.add(dependency);
                    }
                    if(Bfirst > THD0)
                    {
                        Dependency dependency = new Dependency(term2.getTopicId(), term1.getTopicId(), (float)Bfirst, term1.getDomainId());
                        dependenciesAll.add(dependency);
                    }
                }
            }
        }
        List<Dependency> all_dependencies = new ArrayList<>();
        if(dependencies.size()> topicList.size()/2)
        {
            all_dependencies = dependencies;
        }
        else
        {
            all_dependencies = dependenciesAll;
        }
        System.out.println("生成的认知关系对数量： " + dependencies.size());
        //去除前向边
        DFSvisit dfSvisit = new DFSvisit();
        HashMap<Long, List<Dependency>> relations = dfSvisit.changeRelation(all_dependencies);
        HashMap<Long, List<Dependency>> resultRelations = dfSvisit.relationProcess(relations);

        List<Dependency> returnDependency = new ArrayList<>();
        for (Long key : resultRelations.keySet())
        {
            returnDependency.addAll(resultRelations.get(key));
        }
        System.out.println("去除前向边生成的认知关系对数量： " + returnDependency.size());
//        logger.info("Finish computing the hammingDistance...");
//        logger.info("Start ranking...");
//
//        List<Map.Entry<TwoTuple<TopicContainAssembleText, TopicContainAssembleText>, Double>> infoIds = new ArrayList<>(disMap.entrySet());
//        Collections.sort(infoIds, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
//        logger.info("Finish ranking!");
//        logger.info("Start printing...");
//
//        int end = MAX;
//        if (infoIds.size() < end) end = infoIds.size();
//        logger.info("end:" + end);
//        for (int k = 0; k < end; k++) {
//            TwoTuple<TopicContainAssembleText, TopicContainAssembleText> twoTuple = infoIds.get(k).getKey();
//            String term1_term2 = twoTuple.first.getTopicId() + "_" + twoTuple.second.getTopicId();
//            float dis = Float.parseFloat(infoIds.get(k).getValue().toString());
//            System.out.println(term1_term2 + ": " + dis);
//            if (new Float(dis).isNaN()) {
//                dis = 0.5f;
//            }
//            System.out.println(dis);
//            Dependency dependency = new Dependency(twoTuple.first.getTopicId(), twoTuple.second.getTopicId(), dis, twoTuple.first.getDomainId());
//            //Dependency dependency = new Dependency(twoTuple.first.getTopicId(), twoTuple.second.getTopicId(), dis, twoTuple.first.getDomainId());
//            dependencies.add(dependency);
//        }
//        logger.info("Finish printing...");

        //写入到csv文件
//        String[] csvHeaders = {"dependency", "startTopicId", "endTopicId", "confidence", "domainId"};
//        CsvUtil.writeCSV(returnDependency, "D:/实验室/算法集成/"+termList.get(0).getDomainId().toString()+"confidence.csv", csvHeaders);
//        CsvUtil.writeCSV(dependencies, "D:/实验室/算法集成/"+termList.get(0).getDomainId().toString()+"confidence_orig.csv", csvHeaders);
        return returnDependency;

    }

    public double singleAsyDependency(List<Topic> topicList, TopicContainAssembleText term1, TopicContainAssembleText term2) {
        List<Dependency> dependencies = new ArrayList<>();
        List<Dependency> dependenciesAll = new ArrayList<>();

        logger.info("Start computing...");
        List<TopicContainAssembleText> termList = new ArrayList<>();
        termList.add(term1);
        termList.add(term2);
        List<HashMap<String, Float>> tfidf = getTFIDF(topicList, termList);
        List<Double> asyResult = computeAsy(topicList, term1, term2, tfidf.get(0), tfidf.get(1));
        double Afirst = asyResult.get(0);
        return Afirst;
    }

    public List<Double> computeAsy(List<Topic> topicList, TopicContainAssembleText term1, TopicContainAssembleText term2,
                                   HashMap<String, Float> tfidfA, HashMap<String, Float> tfidfB)
    {
        String topic1 = term1.getTopicName();
        String text1 = term1.getText();
        String topic2 = term2.getTopicName();
        String text2 = term2.getText();


        //计算的不对称结果，A为先序，B为先序的值
        Double Afirst = 0.0;
        Double Bfirst = 0.0;
        Double sumA = 0.0;
        Double sumB = 0.0;

        //对于所有知识主题
        for(Topic topic: topicList)
        {
            String topicName = topic.getTopicName();

            double sim1 = 0.0;
            double sim2 = 0.0;
            //当该知识主题在text2中出现时，
            if(text2.contains(topicName))
            {
                //则计算该知识主题对text1的重要性
                float tfc = tfidfA.get(topicName);
                sim1 = new SimilarityUtil().getSimilarity(topic1, topicName);  //以及该知识主题与topic1的相似性
//                double dis1 = CosineSimilar.getSimilarity(topic1, topicName);

                Bfirst += sim1 * tfc * tfidfB.get(topic2);
            }
            if(text1.contains(topicName))
            {
                float tfc = tfidfB.get(topicName);
                sim2 = new SimilarityUtil().getSimilarity(topic2, topicName);
//                double dis2 = CosineSimilar.getSimilarity(topic2, topicName);
                Afirst += sim2 * tfc * tfidfA.get(topic1);
            }

            sumB += tfidfA.get(topicName)*sim1;
            sumA += tfidfB.get(topicName)*sim2;
//            System.out.println(Afirst + " " + Bfirst + " " + sumA + " " + sumB + " " + tfidfA.get(topicName) + " " + tfidfB.get(topicName));
//            System.out.println("sim: " + sim1 + " " + sim2);
        }

        if(!sumA.equals(0))
        {
            if (Afirst.equals(0))
            {
                Afirst = 0.0;
            }
            else
                Afirst = Afirst/sumA;   //归一化操作
        }
        else
        {
            Afirst = 0.0;
        }
        if (!sumB.equals(0))
        {
            if (Bfirst.equals(0))
                Bfirst = 0.0;
            else
                Bfirst = Bfirst/sumB;
        }
        else
            Bfirst = 0.0;

        List<Double> result = new ArrayList<>();
        //两边的值相减
        result.add(Afirst-Bfirst);
        result.add(Bfirst-Afirst);
        return result;
    }

    //判断文本中包含topic的个数
    public int containStrainNums(String topic, String text)
    {
        int count = (text.length() - text.replace(topic, "").length())/topic.length();
        return count;
    }

    //每个文档的tf值
    public List<HashMap<String, Float>> getTF(List<Topic> topicList, List<TopicContainAssembleText> termList)
    {
        List<HashMap<String, Float>> tf = new ArrayList<>();
        for(TopicContainAssembleText topicContainAssembleText : termList)
        {
            HashMap<String, Float> everyTF = new HashMap<>();
            for(Topic topic : topicList)
            {
                Integer topicNum = containStrainNums(topic.getTopicName(), topicContainAssembleText.getText());
                everyTF.put(topic.getTopicName(), topicNum/(termList.size()+0.0f));
            }
            tf.add(everyTF);
        }

        return tf;
    }

    public HashMap<String, Float> getIDF(List<Topic> topicList, List<TopicContainAssembleText> termList)
    {
        HashMap<String, Float> idf = new HashMap<>();
        for(Topic topic : topicList)
        {
            int containNum = 0;
            for(TopicContainAssembleText topicContainAssembleText : termList)
            {
                if(topicContainAssembleText.getText().contains(topic.getTopicName()))
                {
                    containNum += 1;
                }
            }
            idf.put(topic.getTopicName(), (float)Math.log(termList.size()/(1.0+containNum)));
        }
        return idf;
    }

    //计算TFC
    public List<HashMap<String, Float>> getTFIDF(List<Topic> topicList, List<TopicContainAssembleText> termList)
    {

        List<HashMap<String, Float>> tfidf = new ArrayList<>();
        List<HashMap<String, Float>> tf = getTF(topicList, termList);
        HashMap<String, Float> idf = getIDF(topicList, termList);
        for(HashMap<String, Float> tempTF : tf)
        {
            HashMap<String, Float> tempTFIDF = new HashMap<>();
            Double norm = 0.0;
            for(Topic topic : topicList)
            {
                String name = topic.getTopicName();
                tempTFIDF.put(name, tempTF.get(name)*idf.get(name));
                norm += Math.pow(tempTF.get(name)*idf.get(name), 2);
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

    //计算单个知识主题与其它知识主题的不对称性值
    public List<Dependency> addDependencyWithNewTopic(List<Topic> topicList, List<TopicContainAssembleText> termList,
                                                      Topic newTopic, TopicContainAssembleText newTopicText)
    {
        List<Dependency> dependencies = new ArrayList<>();
        List<Dependency> dependenciesAll = new ArrayList<>();

        List<HashMap<String, Float>> tfidf = getTFIDF(topicList, termList);

        int newtopicIndex = 0;

        for(int i = 0; i<termList.size(); i++)
        {
            Topic tempT = topicList.get(i);
            if (tempT.getTopicName().equals(newTopic.getTopicName()))
            {
                newtopicIndex = i;
                break;
            }
        }

        //新增的知识主题与其它知识主题的不对称性值计算
        for (int i = 0; i<termList.size(); i++)
        {

            if (i == newtopicIndex)
                continue;

            TopicContainAssembleText term = termList.get(i);
            if (term.getText().length() == 0 || term.getText().equals(""))
                continue;
            List<Double> asyResult = computeAsy(topicList, term, newTopicText, tfidf.get(i),tfidf.get(newtopicIndex));
            double Afirst = asyResult.get(0);
            double Bfirst = asyResult.get(1);
            if(Afirst >= THD)
            {
                Dependency dependency = new Dependency(term.getTopicId(), newTopicText.getTopicId(), (float)Afirst, term.getDomainId());
                dependencies.add(dependency);
            }
            if(Bfirst >= THD)
            {
                Dependency dependency = new Dependency(newTopicText.getTopicId(), term.getTopicId(), (float)Bfirst, term.getDomainId());
                dependencies.add(dependency);
            }
            if(Afirst > THD0)
            {
                Dependency dependency = new Dependency(term.getTopicId(), newTopicText.getTopicId(), (float)Afirst, term.getDomainId());
                dependenciesAll.add(dependency);
            }
            if(Bfirst > THD0)
            {
                Dependency dependency = new Dependency(newTopicText.getTopicId(), term.getTopicId(), (float)Bfirst, term.getDomainId());
                dependenciesAll.add(dependency);
            }
        }

        List<Dependency> all_dependencies = new ArrayList<>();
        if(dependencies.size()> topicList.size()/2)
        {
            all_dependencies = dependencies;
        }
        else
        {
            all_dependencies = dependenciesAll;
        }

        System.out.println("生成的认知关系对数量： " + all_dependencies.size());
        //去除前向边
//        DFSvisit dfSvisit = new DFSvisit();
//        HashMap<Long, List<Dependency>> relations = dfSvisit.changeRelation(all_dependencies);
//        HashMap<Long, List<Dependency>> resultRelations = dfSvisit.relationProcess(relations);

//        List<Dependency> returnDependency = new ArrayList<>();
//        for (Long key : resultRelations.keySet())
//        {
//            returnDependency.addAll(resultRelations.get(key));
//        }

        return all_dependencies;
    }
}
