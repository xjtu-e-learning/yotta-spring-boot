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
import java.util.List;

/**
 * 构建领域知识森林数据
 *
 * @author lynn
 * @date 2019/08
 */

public class FragmentCrawler {

    /**
     * 将领域术语页面的内容按照分面存储到数据库
     *
     * @param domain 课程名
     * @throws Exception
     */
    public static void storeKGByDomainName(Domain domain) throws Exception {

        /**
         * 读取数据库表格topic，得到知识主题
         */
        String domainName = domain.getDomainName();
        Long domainId = domain.getDomainId();

        List<List<FacetSimple>> facetSimples = new ArrayList<>();

        List<Topic> topicList = MysqlReadWriteDAO.getDomainTopic(domainId);
        //对每一个主题，判断分面和分面层级是否存在，如不存在，则进行爬取

        for (int i = 0; i < topicList.size(); i++) {
            Topic topic = topicList.get(i);
            Long topicID = topic.getTopicId();
            String topicName = topic.getTopicName();
            String topicUrl = topic.getTopicUrl();
            /**
             * 判断数据是否已经存在
             */
            Boolean existFacet = MysqlReadWriteDAO.judgeByClassAndTopic(Config.FACET_TABLE, domainName, topicName);
           // Boolean existFacetRelation = MysqlReadWriteDAO.judgeByClassAndTopic(Config.FACET_RELATION_TABLE, domainName, topicName);
            /**
             * 判断该主题的信息是不是在所有表格中已经存在
             * 只要有一个不存在就需要再次爬取（再次模拟加载浏览器）
             */
            if (!existFacet ) {   //|| !existFacetRelation
                /**
                 * selenium解析网页
                 */
                String topicHtml = SpiderUtils.seleniumWikiCN(topicUrl);
                Document doc = JsoupDao.parseHtmlText(topicHtml);
                // 获取并存储所有分面信息Facet
                List<FacetSimple> facetSimpleList = FragmentCrawlerDAO.getFacet(doc);
                facetSimples.add(facetSimpleList);
                // 获取并存储所有分面关系信息Facet
//                List<FacetRelation> facetRelationList = FragmentCrawlerDAO.getFacetRelation(doc);
//                MysqlReadWriteDAO.storeFacetRelation(domainName, topicID, topicName, facetRelationList);
            } else {
                Log.log("==========课程 " + domainName + "，主题 " + topicName + " 下的分面与分面关系已经存在,无需爬取==========");
            }
        }
        for (int i = 0; i < topicList.size(); i++) {
            MysqlReadWriteDAO.storeFacet(domainName,topicList.get(i).getTopicId(), facetSimples.get(i));
            Log.log("==========课程 " + domainName + "，主题 " + topicList.get(i).getTopicName() + " 下的分面与分面关系已经抽取完毕==========");
        }
        Log.log("\n\n\n==========该课程的知识主题分面树已经构建完成==========");
    }

}
