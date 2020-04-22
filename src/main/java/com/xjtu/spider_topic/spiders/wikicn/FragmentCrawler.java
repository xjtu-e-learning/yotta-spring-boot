package com.xjtu.spider_topic.spiders.wikicn;

import com.xjtu.common.Config;
import com.xjtu.domain.domain.Domain;
import com.xjtu.facet.domain.FacetRelation;
import com.xjtu.facet.domain.FacetSimple;
import com.xjtu.topic.domain.Topic;
import org.jsoup.nodes.Document;
import com.xjtu.utils.JsoupDao;
import com.xjtu.utils.Log;
import com.xjtu.utils.SpiderUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 构建领域知识森林数据
 *
 * @author lynn
 * @date 2019/08
 */

public class FragmentCrawler {

    private static int countFacetCrawled = 0;

    private static int countFacetRelationCrawled = 0;

    private static boolean isCompleted = false;

    /**
     * 获取已爬取分面数目
     *
     * @return
     */
    public static int getCountFacetCrawled() {
        return countFacetCrawled;
    }

    public static int getCountFacetRelationCrawled(){ return countFacetRelationCrawled;}

    public static void setIsCompleted(boolean bool){isCompleted = bool;}
    public static boolean getisCompleted(){return  isCompleted;}
    /**
     * 将领域术语页面的内容按照分面存储到数据库
     *
     * @param domain 课程名
     * @throws Exception
     */
    public static void storeFacetTreeByDomainName(Domain domain) throws Exception {

        /**
         * 读取数据库表格topic，得到知识主题
         */
        String domainName = domain.getDomainName();
        Long domainId = domain.getDomainId();
        /**
         * 设置一个列表保存每个主题的分面集
         */
        List<List<FacetSimple>> facetSimples = new LinkedList<>();
        /**
         * 设置一个列表保存每个主题的分面关系集
         */
        List<List<FacetRelation>> facetRelations = new LinkedList<>();
        /**
         * 获取该课程的主题列表
         */
        List<Topic> topicList = MysqlReadWriteDAO.getDomainTopic(domainId);
        //对每一个主题，判断分面和分面层级是否存在，如不存在，则进行爬取

        //设置为未爬取状态
        setIsCompleted(false);

        for (int i = 0; i < topicList.size(); i++) {
            Topic topic = topicList.get(i);
            String topicName = topic.getTopicName();
            String topicUrl = topic.getTopicUrl();
            /**
             * 判断数据是否已经存在
             * 此处存在可改进点：优化SQL查询
             */
            Boolean existFacet = MysqlReadWriteDAO.judgeByClassAndTopic(Config.FACET_TABLE, domainName, topicName);
            /**
             * 判断该主题的信息是不是在所有表格中已经存在
             * 只要有一个不存在就需要再次爬取（再次模拟加载浏览器）
             */
            if (!existFacet) {   //|| !existFacetRelation
                /**
                 * selenium解析网页
                 */
                String topicHtml = SpiderUtils.seleniumWiki(topicUrl);
                Document doc = JsoupDao.parseHtmlText(topicHtml);

                // 获取并存储所有分面信息Facet
                List<FacetSimple> facetSimpleList = FragmentCrawlerDAO.getFacet(doc);
                countFacetCrawled += facetSimpleList.size();
                facetSimples.add(facetSimpleList);

                // 获取并存储所有分面关系信息Facet
                List<FacetRelation> facetRelationList = FragmentCrawlerDAO.getFacetRelation(doc);
                countFacetRelationCrawled += facetRelationList.size();
                facetRelations.add(facetRelationList);
            } else {
                Log.log("==========课程 " + domainName + "，主题 " + topicName + " 下的分面与分面关系已经存在,无需爬取==========");
            }
        }
        for (int i = 0; i < topicList.size(); i++) {
            if (facetSimples.get(i).size() != 0){
                MysqlReadWriteDAO.storeFacet(topicList.get(i).getTopicId(), facetSimples.get(i));
            } else{
                continue;
            }
            if (facetRelations.get(i).size() != 0){
                MysqlReadWriteDAO.storeFacetRelation(topicList.get(i).getTopicId(), facetRelations.get(i));
            } else {
                continue;
            }

            Log.log("==========课程 " + domainName + "，主题 " + topicList.get(i).getTopicName() + " 下的分面与分面关系已经抽取完毕==========");
        }
        Log.log("\n\n\n==========该课程的分面与分面关系已经构建完成==========");

        //爬取完成时设置为已完成
        setIsCompleted(true);

        //爬取完成时清空分面计数
        countFacetCrawled = 0;
    }

    public static void storeFacetTreeByTopicName(Domain domain,Topic topic) throws Exception {
        //定义课程、主题内容
        String domainName = domain.getDomainName();
        String topicName = topic.getTopicName();
        String topicUrl = topic.getTopicUrl();
        Long topicId = topic.getTopicId();
        /**
         * 判断数据是否已经存在
         * 此处存在可改进点：优化SQL查询
         */
        Boolean existFacet = MysqlReadWriteDAO.judgeByClassAndTopic(Config.FACET_TABLE, domainName, topicName);

        /**
         * 判断该主题的信息是不是在所有表格中已经存在
         * 只要有一个不存在就需要再次爬取（再次模拟加载浏览器）
         */

        List<FacetSimple> facetSimpleList = new ArrayList<>();

        List<FacetRelation> facetRelationList = new ArrayList<>();

        if (!existFacet) {
            /**
             * selenium解析网页
             */
            String topicHtml = SpiderUtils.seleniumWiki(topicUrl);
            Document doc = JsoupDao.parseHtmlText(topicHtml);

            // 获取并存储所有分面信息Facet
            facetSimpleList = FragmentCrawlerDAO.getFacet(doc);

            // 获取并存储所有分面关系信息Facet
            facetRelationList = FragmentCrawlerDAO.getFacetRelation(doc);

            if (facetSimpleList.size() != 0){
                MysqlReadWriteDAO.storeFacet(topicId, facetSimpleList);
            } else{
                Log.log("该主题不存在分面");
            }
            if (facetRelationList.size() != 0){
                MysqlReadWriteDAO.storeFacetRelation(topicId, facetRelationList);
            } else {
                Log.log("该主题不存在分面关系");
            }

            Log.log("==========课程 " + domainName + "，主题 " + topicName + " 下的分面树已经爬取==========");
        } else {
            Log.log("==========主题 " + topicName + " 下的分面与分面关系已经存在,无需爬取==========");
        }
    }

}
