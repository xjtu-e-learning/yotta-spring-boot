package com.xjtu.spider_new.spiders.wikicn;

import com.spreada.utils.chinese.ZHConverter;
import com.xjtu.domain.domain.Domain;
import com.xjtu.facet.domain.Facet;
import com.xjtu.topic.domain.Topic;
import com.xjtu.utils.JsoupDao;
import com.xjtu.utils.Log;
import com.xjtu.utils.SpiderUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;


/**
 * 碎片爬虫
 * @author ljj
 * */
public class AssembleCrawler {

    public static long assembleCount = 0;

    public static boolean Completed = false;

    private static List<Topic> topicList;

    public static List<Facet> certainTopicfacets;

    public static List<Facet> allFactes;

    public static int visitedFacets;

    /**
     * 根据课程名、主题名爬取碎片
     * 其中根据每个分面，会爬 1 次wikipeida， 爬 5 次简书
     */
    public static void storeAssembleByTopicName(Domain domain, Topic topic) throws Exception {
        //定义课程、主题内容
        String domainName = domain.getDomainName();
        String topicName = topic.getTopicName();
        String topicUrl = topic.getTopicUrl();

        /**
         * 判断该主题的信息是不是在所有表格中已经存在
         * 只要有一个不存在就需要再次爬取（再次模拟加载浏览器）
         */

        certainTopicfacets = MysqlReadWriteDAO.getTopicFacet(topic.getTopicId());
//        certainTopicfacets = facetService.getFacetsByDomainIdAndTopicName(domain.getDomainId(), topicName);

        /**
         * selenium解析wiki网页
         */
        if (certainTopicfacets.isEmpty())//分面为空的情况
        {
            Log.log("==========课程 " + domainName + "，主题 " + topicName + " 下无分面，无法爬碎片==========");
        } else {
            String topicHtml = SpiderUtils.seleniumWiki(topicUrl);
            Document wikiDoc = JsoupDao.parseHtmlText(topicHtml);

            // 以下两个遍历facets列表其实可以合并
            for (Facet facet : certainTopicfacets) {
                String facetName = facet.getFacetName();
                String assembleContent = "";
                String assembleText = "";
                try {
                    assembleContent = getAssembleContent(wikiDoc, facetName);
                    assembleText = getAssembleText(wikiDoc, facetName);
                } catch (Exception e) {
                    Log.log("\n维基网页查找碎片异常\n链接地址：" + topicUrl);
                }

                if (assembleContent.length() != 0 && assembleText.length() != 0) {
                    Long sourceId = Long.valueOf(1);
                    assembleCount++;
                    MysqlReadWriteDAO.storeAssemble(assembleContent, assembleText, domain.getDomainId(), facet.getFacetId(), sourceId);
                }
            }

            /**
             * 简书网页爬碎片*/

            for (Facet facet : certainTopicfacets) {
                /**
                 * selenium解析简书网页
                 */

                String jianshuSearchUrl = "https://www.jianshu.com/search?q=" + topicName + facet.getFacetName() + "&page=1&type=note";
                try {
                    String jianshuSearchHtml = SpiderUtils.seleniumJianshu(jianshuSearchUrl);
                    Document jianshuSerarchDoc = JsoupDao.parseHtmlText(jianshuSearchHtml);
                    Elements jianshuUrlElements = jianshuSerarchDoc.select("ul[class='note-list']").select("a[class='title']");
                    if (jianshuUrlElements.isEmpty()) {
                        Log.log("没有拿到简书链接！");
                    }
                    for (int i = 0; i < 5; i++) {
                        Element urlElement = jianshuUrlElements.get(i);
                        String jianshuUrl = "https://www.jianshu.com" + urlElement.attr("href");

                        String jianshuHtml = SpiderUtils.seleniumJianshu(jianshuUrl);
                        Document jianshuDoc = JsoupDao.parseHtmlText(jianshuHtml);
                        Elements titleElement = jianshuDoc.select("h1[class='_1RuRku']");
                        Elements contentElement = jianshuDoc.select("article[class='_2rhmJa']");
                        String assembleContent = "";
                        String assembleText = "";
                        assembleContent = titleElement.toString() + contentElement.toString();
                        assembleText = titleElement.text() + contentElement.text();
                        if (assembleContent.length() != 0 && assembleText.length() != 0) {
                            Long sourceId = Long.valueOf(17);
                            assembleCount++;
                            MysqlReadWriteDAO.storeAssemble(assembleContent, assembleText, domain.getDomainId(), facet.getFacetId(), sourceId);
                        } else {
                            Log.log("简书链接无法解析：" + jianshuUrl);
                        }
                    }
                } catch (Exception e) {
                    Log.log("\n简书碎片列表地址获取失败\n链接地址：" + jianshuSearchUrl);
                }
                visitedFacets++;
            }
            Log.log("==========课程 " + domainName + "，主题 " + topicName + " 下的碎片已经爬取==========");
        }

    }

    /**
     * 根据课程名、主题名  增量爬取碎片
     */
    public static void storeAssembleIncrementalByTopicName(Domain domain, Topic topic) throws Exception {
        //定义课程、主题内容
        String domainName = domain.getDomainName();
        String topicName = topic.getTopicName();

        /**
         * 判断该主题的信息是不是在所有表格中已经存在
         * 只要有一个不存在就需要再次爬取（再次模拟加载浏览器）
         */

        List<Facet> facets = MysqlReadWriteDAO.getTopicFacet(topic.getTopicId());

        /**
         * selenium解析wiki网页
         */
        if (facets.isEmpty())//分面为空的情况
        {
            Log.log("==========课程 " + domainName + "，主题 " + topicName + " 下无分面，无法爬取碎片==========");
        } else {
            /**
             * 简书网页爬碎片*/
            for (Facet facet : facets) {
                /**
                 * selenium解析简书网页
                 */
                String jianshuSearchUrl = "https://www.jianshu.com/search?q=" + topicName + facet.getFacetName() + "&page=1&type=note";
                try {
                    String jianshuSearchHtml = SpiderUtils.seleniumJianshu(jianshuSearchUrl);
                    Document jianshuSerarchDoc = JsoupDao.parseHtmlText(jianshuSearchHtml);
                    Elements jianshuUrlElements = jianshuSerarchDoc.select("ul[class='note-list']").select("a[class='title']");
                    if (jianshuUrlElements.isEmpty()) {
                        Log.log("没有拿到简书链接！");
                    } else {
                        for (int i = 5; i < 10; i++) {
                            Element urlElement = jianshuUrlElements.get(i);
                            String jianshuUrl = "https://www.jianshu.com" + urlElement.attr("href");

                            String jianshuHtml = SpiderUtils.seleniumJianshu(jianshuUrl);
                            Document jianshuDoc = JsoupDao.parseHtmlText(jianshuHtml);
                            Elements titleElement = jianshuDoc.select("h1[class='_1RuRku']");
                            Elements contentElement = jianshuDoc.select("article[class='_2rhmJa']");
                            String assembleContent = "";
                            String assembleText = "";
                            assembleContent = titleElement.toString() + contentElement.toString();
                            assembleText = titleElement.text() + contentElement.text();
                            if (assembleContent.length() != 0 && assembleText.length() != 0) {
                                Long sourceId = Long.valueOf(17);
                                assembleCount++;
                                MysqlReadWriteDAO.storeAssemble(assembleContent, assembleText, domain.getDomainId(), facet.getFacetId(), sourceId);
                            } else {
                                Log.log("简书链接无法解析：" + jianshuUrl);
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.log("\n简书碎片列表地址获取失败\n链接地址：" + jianshuSearchUrl);
                }
            }
            Log.log("==========课程 " + domainName + "，主题 " + topicName + " 下的碎片已经爬取==========");
        }
    }

    /**
     * 根据单个分面爬取碎片
     * @param domain
     * @param topic
     * @param facet
     * @throws Exception
     */
    public static boolean crawlAssembleByFacet(Domain domain, Topic topic, Facet facet) throws Exception {
        String topicHtml = SpiderUtils.seleniumWiki(topic.getTopicUrl());
        Document wikiDoc = JsoupDao.parseHtmlText(topicHtml);
        String facetName = facet.getFacetName();
        String assembleContent = "";
        String assembleText = "";
        boolean res = false; // 记录是否爬到了碎片

        try {
            // 在wikipedia上爬碎片
            assembleContent = getAssembleContent(wikiDoc, facetName);
            assembleText = getAssembleText(wikiDoc, facetName);
        } catch (Exception e) {
            Log.log("\n维基网页查找碎片异常\n链接地址：" + topic.getTopicUrl());
        }

        if (assembleContent.length() != 0 && assembleText.length() != 0) {
            Long sourceId = Long.valueOf(1);
            assembleCount++;
            MysqlReadWriteDAO.storeAssemble(assembleContent, assembleText, domain.getDomainId(), facet.getFacetId(), sourceId);
            res = true;
        }

        // 在简书源上爬碎片
        String jianshuSearchUrl = "https://www.jianshu.com/search?q=" + topic.getTopicName() + facet.getFacetName() + "&page=1&type=note";
        try {
            String jianshuSearchHtml = SpiderUtils.seleniumJianshu(jianshuSearchUrl);
            Document jianshuSerarchDoc = JsoupDao.parseHtmlText(jianshuSearchHtml);
            Elements jianshuUrlElements = jianshuSerarchDoc.select("ul[class='note-list']").select("a[class='title']");
            if (jianshuUrlElements.isEmpty()) {
                Log.log("没有拿到简书链接！");
            }
            for (int i = 0; i < 5; i++) {
                Element urlElement = jianshuUrlElements.get(i);
                String jianshuUrl = "https://www.jianshu.com" + urlElement.attr("href");

                String jianshuHtml = SpiderUtils.seleniumJianshu(jianshuUrl);
                Document jianshuDoc = JsoupDao.parseHtmlText(jianshuHtml);
                Elements titleElement = jianshuDoc.select("h1[class='_1RuRku']");
                Elements contentElement = jianshuDoc.select("article[class='_2rhmJa']");
                assembleContent = "";
                assembleText = "";
                assembleContent = titleElement.toString() + contentElement.toString();
                assembleText = titleElement.text() + contentElement.text();
                if (assembleContent.length() != 0 && assembleText.length() != 0) {
                    Long sourceId = Long.valueOf(17);
                    assembleCount++;
                    MysqlReadWriteDAO.storeAssemble(assembleContent, assembleText, domain.getDomainId(), facet.getFacetId(), sourceId);
                    res = true;
                } else {
                    Log.log("简书链接无法解析：" + jianshuUrl);
                }
            }
        } catch (Exception e) {
            Log.log("\n简书碎片列表地址获取失败\n链接地址：" + jianshuSearchUrl);
        }

        return res;
    }

    /**
     * 获得维基碎片的内容
     */
    public static String getAssembleContent(Document doc, String facetName) {
        // 避免wikipedia中id是简繁体的问题
        Element element = doc.getElementById(facetName) == null ?
                doc.getElementById(ZHConverter.convert(facetName, ZHConverter.TRADITIONAL)) :
                doc.getElementById(facetName);
        Element parentElement = element.parent();
        if (parentElement != null) {
            Element nextElement = parentElement.nextElementSibling();
            StringBuffer stringBuffer = new StringBuffer();
            while (nextElement != null && nextElement.tagName() != "h1" && nextElement.tagName() != "h2" && nextElement.tagName() != "h3") {
                Element currentElement = nextElement;
                String currentContent = currentElement.toString();
                stringBuffer.append(currentContent);
                nextElement = currentElement.nextElementSibling();
            }
            return stringBuffer.toString();
        }

        return null;
    }

    /**
     * 获得维基碎片的纯文本
     */
    public static String getAssembleText(Document doc, String facetName) {
        // 避免wikipedia中id是简繁体的问题
        Element element = doc.getElementById(facetName) == null ?
                doc.getElementById(ZHConverter.convert(facetName, ZHConverter.TRADITIONAL)) :
                doc.getElementById(facetName);
        Element parentElement = element.parent();
        if (parentElement != null) {
            Element nextElement = parentElement.nextElementSibling();
            StringBuffer stringBuffer = new StringBuffer();
            while (nextElement != null && nextElement.tagName() != "h1" && nextElement.tagName() != "h2" && nextElement.tagName() != "h3") {
                Element currentElement = nextElement;
                String currentContent = currentElement.text();
                stringBuffer.append(currentContent);
                nextElement = currentElement.nextElementSibling();
            }
            return stringBuffer.toString();
        }

        return null;
    }

    /**
     * 根据课程，获得它下面的主题列表，再一一爬取每个主题列表下的碎片
     * @param domain
     */
    public static void storeAssembleByDomainName(Domain domain) throws Exception {
        /**
         * 读取数据库表格topic，得到知识主题
         */
        String domainName = domain.getDomainName();
        Long domainId = domain.getDomainId();
        /**
         * 获取该课程的主题列表
         */
        topicList = MysqlReadWriteDAO.getDomainTopic(domainId);
//        topicList = topicService.getTopicList(domainId);
//        allFactes = facetService.getFacetsByDomainId(domainId);

        for (Topic topic : topicList) {
            storeAssembleByTopicName(domain, topic);
        }

        setCompleted(true);
    }


    /**
     * 进度计算方法：访问过的分面 / 分面总数
     * @param domainId
     * @return
     * @throws Exception
     */
    public static double getProgress(Long domainId) throws Exception {
        int allFacetCount = 0;

        topicList = MysqlReadWriteDAO.getDomainTopic(domainId);
//        topicList = topicService.getTopicList(domainId);
//        allFactes = facetService.getFacetsByDomainId(domainId);

        for (Topic topic : topicList) {
            allFacetCount += MysqlReadWriteDAO.getTopicFacet(topic.getTopicId()).size();
        }

        return (double)visitedFacets / (double)allFacetCount ;
    }

    public static long getAssembleCount() {
        return assembleCount;
    }

    public static boolean isCompleted() {
        return Completed;
    }

    public static void setCompleted(boolean completed) {
        Completed = completed;
    }
}
