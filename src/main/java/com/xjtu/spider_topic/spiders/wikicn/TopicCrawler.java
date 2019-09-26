package com.xjtu.spider_topic.spiders.wikicn;

import com.xjtu.common.Config;
import com.xjtu.domain.domain.Domain;
import com.xjtu.topic.domain.Term;
import com.xjtu.topic.domain.LayerRelation;
import com.xjtu.utils.Log;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


/**
 * 移植：爬取中文维基的领域术语
 *
 * @author 张铎
 * @date 2019年7月
 */
public class TopicCrawler {

    private static int countTopicCrawled = 0;

    /**
     * 返回count值，来获取已爬取的主题数；
     *
     * @return
     */
    public static int getCountTopicCrawled() {
        return countTopicCrawled;
    }

    /**
     * 1.根据课程名，判断存不存在该课程，若不存在，则存储课程名
     *
     * @param domain 课程
     * @return true 表示已经爬取
     */
    public static void storeDomain(Domain domain) {
        List<Domain> list = new ArrayList<>();
        list.add(domain);
        if (!MysqlReadWriteDAO.judgeByClass(Config.DOMAIN_TABLE, domain.getDomainName())) {
            MysqlReadWriteDAO.storeDomain(list);
        }
    }

    /**
     * 2.根据课程名，判断该课程知识主题是否已经爬取，显示结果
     * 否则，获取三层领域术语和知识主题（某门课程）
     *
     * @param domain 课程
     * @throws Exception
     */
    public static void storeTopic(Domain domain) throws Exception {
        /**
         * 领域术语采集：爬虫爬取
         * 将所有领域术语存储到damain_layer表格中
         */
        String domainName = domain.getDomainName();
        Long domainId = domain.getDomainId();
        /**
         * 判断该课程领域术语是否已经爬取
         */
        Boolean existLayer = MysqlReadWriteDAO.judgeByClass1(Config.TOPIC_TABLE, Config.DOMAIN_TABLE, domainName);
        if (!existLayer) {
            layerExtract(domainName);
            Log.log("==========该课程领域术语爬取完毕===>>>准备开始抽取知识主题==========");
        } else {
            Log.log(domain + "：该课程领域术语已经爬取");
        }

        /**
         * 判断该主题是否已经爬取
         */
        Boolean existTopic = MysqlReadWriteDAO.judgeByClass1(Config.TOPIC_TABLE, Config.DOMAIN_TABLE, domainName);
        if (!existTopic) {
            topicExtract(domainName, domainId);
            Log.log("==========该课程知识主题抽取完毕===>>准备抽取分面==========");
        } else {
            Log.log(domain + "：该课程知识主题已经存在");
        }
    }

    /**
     * 爬虫爬取三层领域术语（某门所有课程）
     *
     * @param domainName 课程名
     * @throws Exception
     */
    public static void layerExtract(String domainName) throws Exception {

        /**
         * 领域术语采集：单门课程
         */

        /**
         * 第一层领域术语
         */
        //测试：String domain = "数据结构";
        String domain_url = "https://en.wikipedia.org/wiki/Category:" + URLEncoder.encode(domainName, "UTF-8");//课程维基根目录

        int firstLayer = 1;
        List<Term> topicFirst = TopicCrawlerDAO.topic(domain_url); // 得到第一层领域术语（不含子主题的那一部分）
        countTopicCrawled += topicFirst.size();
        MysqlReadWriteDAO.storeDomainLayer(topicFirst, domainName, firstLayer); // 存储第一层领域术语（不含子主题）

        /**
         * 第二层领域术语
         */
        int secondLayer = 2;
        List<Term> Subcategory = TopicCrawlerDAO.layer(domain_url); // 获取第一层子分类（含子主题的那一部分）
        List<Term> topicSecondAll = new ArrayList<Term>(); // 保存所有第二层的领域术语
        if (Subcategory.size() != 0) {
            for (int i = 0; i < Subcategory.size(); i++) {
                Term layer = Subcategory.get(i);
                String url = layer.getTermUrl();
                List<Term> topicSecond = TopicCrawlerDAO.topic(url); // 得到第二层领域术语（不含子主题的那一部分）
                countTopicCrawled += topicSecond.size();
                //存储第二层领域术语
                MysqlReadWriteDAO.storeDomainLayer(topicSecond, domainName, secondLayer); // 存储第二层领域术语（不含子主题）
                topicSecondAll.addAll(topicSecond); // 合并所有第二层领域术语

                /**
                 * 第三层领域术语
                 */
                int thirdLayer = 3;
                List<Term> SubcategorySecond = TopicCrawlerDAO.layer(url); // 得到第三层领域术语（含子主题的那一部分）
                List<Term> topicThirdAll = new ArrayList<Term>(); // 保存所有第三层的领域术语
                if (SubcategorySecond.size() != 0) {
                    for (int j = 0; j < SubcategorySecond.size(); j++) {
                        Term layer2 = SubcategorySecond.get(j);
                        String url2 = layer2.getTermUrl();
                        List<Term> topicThird = TopicCrawlerDAO.topic(url2); // 得到第三层领域术语（不含子主题）
                        countTopicCrawled += topicThird.size();
                        // 存储第三层领域术语
                        MysqlReadWriteDAO.storeDomainLayer(topicThird, domainName, thirdLayer); // 存储第三层领域术语（不含子主题）
                        topicThirdAll.addAll(topicThird); // 合并所有第三层领域术语
                    }
                } else {
                    Log.log("不存在第三层候选主题....");
                }
            }
        } else {
            Log.log("不存在第二层候选主题...");
        }
    }

    /**
     * 利用算法抽取三层知识主题（某门课程所有领域术语中的主题）
     *
     * @throws Exception
     */
    public static void topicExtract(String domainName, Long domainId) throws Exception {

        List<Term> topicFirst = MysqlReadWriteDAO.getDomainLayer(domainName, 1);
        List<Term> topicSecond = MysqlReadWriteDAO.getDomainLayer(domainName, 2);
        List<Term> topicThird = MysqlReadWriteDAO.getDomainLayer(domainName, 3);

        /**
         * 知识主题筛选：抽取算法获取知识主题
         * 存储到 domain_topic表格中
         */
        // 从 domain_layer 删除重复主题(含子主题)保存到 domain_topic

        List<Set<Term>> topicList = TopicCrawlerDAO.getTopic(topicFirst, topicSecond, topicThird);
        for (int i = 0; i < topicList.size(); i++) {
            Set<Term> topic = topicList.get(i);
            int layer_ID = i + 1;
            MysqlReadWriteDAO.storeDomainTopic(topic, domainId, layer_ID); // 存储三层领域术语
            countTopicCrawled = 0;
        }

    }

}
