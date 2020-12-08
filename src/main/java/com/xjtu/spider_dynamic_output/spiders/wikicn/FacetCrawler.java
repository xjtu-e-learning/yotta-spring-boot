package com.xjtu.spider_dynamic_output.spiders.wikicn;

import com.xjtu.common.Config;
import com.xjtu.domain.domain.Domain;
import com.xjtu.facet.domain.FacetRelation;
import com.xjtu.facet.domain.FacetSimple;
import com.xjtu.spider_topic.spiders.wikicn.FragmentCrawlerDAO;
import com.xjtu.spider_topic.spiders.wikicn.MysqlReadWriteDAO;
import com.xjtu.topic.domain.Topic;
import com.xjtu.utils.JsoupDao;
import com.xjtu.utils.Log;
import com.xjtu.utils.SpiderUtils;
import org.jsoup.nodes.Document;
import java.util.ArrayList;
import java.util.List;

/**
 * 分面爬虫
 * @author ljj
 * */

public class FacetCrawler {


    private static boolean isCompleted = false;

    public static void setIsCompleted(boolean bool) {
        isCompleted = bool;
    }

    public static boolean getisCompleted() {
        return isCompleted;
    }

    /**
     * 根据课程名、主题名爬取该课程下的分面
     */
    public static void storeFacetByTopicName(Domain domain, Topic topic) throws Exception {
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
        if (!existFacet) {   // !existFacetRelation
            /**
             * selenium解析网页
             */
            String topicHtml = SpiderUtils.seleniumWiki(topicUrl);
            Document doc = JsoupDao.parseHtmlText(topicHtml);


            // 获取并存储所有分面信息Facet
            facetSimpleList = FragmentCrawlerDAO.getFacet(doc);

            // 获取并存储所有分面关系信息Facet
            facetRelationList = FragmentCrawlerDAO.getFacetRelation(doc);

            if (facetSimpleList.size() != 0) {
                MysqlReadWriteDAO.storeFacet(topicId, facetSimpleList);
            } else {
                Log.log("该主题不存在分面");
            }
            if (facetRelationList.size() != 0) {
                MysqlReadWriteDAO.storeFacetRelation(topicId, facetRelationList);
            } else {
                Log.log("该主题不存在分面关系");
            }


            Log.log("==========课程 " + domainName + "，主题 " + topicName + " 下的分面树已经爬取==========");
        } else {
            Log.log("==========课程 " + domainName + "，主题 " + topicName + " 下的分面与分面关系已经存在,无需爬取==========");
        }

        isCompleted = true;
    }

}
