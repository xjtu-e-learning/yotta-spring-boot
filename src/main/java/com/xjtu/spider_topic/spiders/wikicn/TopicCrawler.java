package com.xjtu.spider_topic.spiders.wikicn;

import com.xjtu.common.Config;
import com.xjtu.domain.domain.Domain;
import com.xjtu.spider_topic.service.TFSpiderService;
import com.xjtu.topic.domain.Term;
import com.xjtu.utils.Log;

import java.net.URLEncoder;
import java.util.ArrayList;
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
    private static String domain_url0;
    private static String domain_url1 = "https://zh.wikipedia.org/wiki/Category:";
    private static String domain_url2 = "https://en.wikipedia.org/wiki/Category:";

    /**
     * 返回count值，来获取已爬取的主题数；
     *
     * @return
     */
    public static int getCountTopicCrawled() {
        return countTopicCrawled;
    }


    /**
     * 根据课程的中英文状态确定爬虫爬取哪个网站
     *
     * @param
     * @return
     */
    public static void setDomainLanguage() {
        if (TFSpiderService.getDomainFlag()) {domain_url0 = domain_url1;}
        else domain_url0 = domain_url2;
    }

    /**
     * 根据课程名，判断该课程知识主题是否已经爬取，显示结果
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
        setDomainLanguage();
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
    private static List<Term> topicFirstAll = new ArrayList<>();
    private static List<Term> topicSecondAll = new ArrayList<>();
    private static List<Term> topicThirdAll = new ArrayList<>();
    public static void layerExtract(String domainName) throws Exception {

        /**
         * 领域术语采集：单门课程
         */

        /**
         * 第一层领域术语
         */
        String domain_url = domain_url0 + URLEncoder.encode(domainName, "UTF-8");//课程维基根目录

        topicFirstAll = TopicCrawlerDAO.topic(domain_url); // 得到第一层领域术语（不含子主题的那一部分）
        countTopicCrawled += topicFirstAll.size();

        /**
         * 第二层领域术语
         */
        List<Term> Subcategory = TopicCrawlerDAO.layer(domain_url); // 获取第一层子分类（含子主题的那一部分）
        if (Subcategory.size() != 0) {
            for (int i = 0; i < Subcategory.size(); i++) {
                Term layer = Subcategory.get(i);
                String url = layer.getTermUrl();
                List<Term> topicSecond = TopicCrawlerDAO.topic(url); // 得到第二层领域术语（不含子主题的那一部分）
                countTopicCrawled += topicSecond.size();
                topicSecondAll.addAll(topicSecond); // 合并所有第二层领域术语
                /**
                 * 第三层领域术语
                 */
                List<Term> SubcategorySecond = TopicCrawlerDAO.layer(url); // 得到第三层领域术语（含子主题的那一部分）
                if (SubcategorySecond.size() != 0) {
                    for (int j = 0; j < SubcategorySecond.size(); j++) {
                        Term layer2 = SubcategorySecond.get(j);
                        String url2 = layer2.getTermUrl();
                        List<Term> topicThird = TopicCrawlerDAO.topic(url2); // 得到第三层领域术语（不含子主题）
                        countTopicCrawled += topicThird.size();
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
        /**
         * 知识主题筛选：抽取算法获取知识主题
         * 存储到 domain_topic表格中
         */
        List<Set<Term>> topicList = TopicCrawlerDAO.getTopic(topicFirstAll, topicSecondAll, topicThirdAll);
        for (int i = 0; i < topicList.size(); i++) {
            Set<Term> topic = topicList.get(i);
            int layer_ID = i + 1;
            Log.log("开始存储知识主题");
            MysqlReadWriteDAO.storeDomainTopic(topic, domainId, layer_ID); // 存储三层领域术语
            Log.log("存储知识主题完毕");
        }
        countTopicCrawled = 0;
    }

}
