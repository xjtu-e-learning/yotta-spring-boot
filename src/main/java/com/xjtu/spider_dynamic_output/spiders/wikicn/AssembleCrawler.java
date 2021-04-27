package com.xjtu.spider_dynamic_output.spiders.wikicn;

import com.spreada.utils.chinese.ZHConverter;
import com.xjtu.assemble.domain.Assemble;
import com.xjtu.domain.domain.Domain;
import com.xjtu.facet.domain.Facet;
import com.xjtu.spider_dynamic_output.spiders.csdn.CsdnCrawler;
import com.xjtu.spider_dynamic_output.spiders.csdn.CsdnThread;
import com.xjtu.topic.domain.Topic;
import com.xjtu.utils.JsoupDao;
import com.xjtu.utils.Log;
import com.xjtu.utils.SpiderUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Thread.State.TERMINATED;


/**
 * 碎片爬虫
 * @author ljj
 * */
public class AssembleCrawler {

    /**
     * 根据课程名、主题名爬取碎片
     */
    public static void storeAssembleByTopicName(Domain domain, Topic topic,Boolean increment) throws Exception {
        //定义课程、主题内容
        String domainName = domain.getDomainName();
        String topicName = topic.getTopicName();
        String topicUrl = topic.getTopicUrl();

        /**
         * 判断该主题的信息是不是在所有表格中已经存在
         * 只要有一个不存在就需要再次爬取（再次模拟加载浏览器）
         */

        List<Facet> facets = MysqlReadWriteDAO.getTopicFacet(topic.getTopicId());

        /**
         * selenium解析wiki网页
         */
        HashMap<String,Thread> threadMap=new HashMap<>();
        if (facets.isEmpty())//分面为空的情况
        {
            Log.log("==========课程 " + domainName + "，主题 " + topicName + " 下无分面，无法爬碎片==========");
        } else {
            String topicHtml = SpiderUtils.seleniumWiki(topicUrl);
            Document wikiDoc = JsoupDao.parseHtmlText(topicHtml);
            for (Facet facet : facets) {
                long assembleCount=MysqlReadWriteDAO.findAssembleNumByFacet(facet.getFacetId());
                Log.log("====分面："+facet.getFacetName()+ "下的碎片数量为" +assembleCount );
                if(assembleCount>4l && increment==false){
                    Log.log("分面："+facet.getFacetName()+ "下碎片已爬取，无需再爬" );
                    continue;
                }
                if(assembleCount>9l && increment==true){
                    Log.log("分面："+facet.getFacetName()+ ",下碎片已增量爬取，无需再爬" );
                    continue;
                }
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
                    MysqlReadWriteDAO.storeAssemble(assembleContent, assembleText, domain.getDomainId(), facet.getFacetId(), sourceId);
                }
                Thread thread=new CsdnThread(domain,topic,facet,increment);
                thread.start();
                threadMap.put(facetName,thread);

                //CSDN爬虫
//                String csdnSearchUrl = "https://so.csdn.net/so/search/blog?q=" + topicName + facetName + "&t=blog&p=1&s=0&tm=0&lv=-1&ft=0&l=&u=";
//                try {
//                    String csdnSearchHtml = SpiderUtils.seleniumCsdn(csdnSearchUrl);
//                    Document csdnSerarchDoc = JsoupDao.parseHtmlText(csdnSearchHtml);
//                    Elements csdnUrlElements = csdnSerarchDoc.select("div[class*='list-item']").select("a[class='normal-list-link']");
//                    if (csdnUrlElements.isEmpty()) {
//                        Log.log("没有拿到csdn链接！");
//                    }
//                    for (int i = 0; i < 5; i++) {
//                        Element urlElement = csdnUrlElements.get(i);
//                        String csdnUrl = urlElement.attr("href");
//
//                        String csdnHtml = SpiderUtils.seleniumCsdn(csdnUrl);
//                        Document csdnDoc = JsoupDao.parseHtmlText(csdnHtml);
//                        Elements contentElement = csdnDoc.select("div[class='blog-content-box']");
//                        StringBuffer csdnAssembleContent = new StringBuffer();
//                        StringBuffer csdnAssembleText = new StringBuffer();
//                        csdnAssembleContent.append(contentElement.toString());
//                        csdnAssembleText.append(contentElement.text());
//                        if (csdnAssembleContent.length() != 0 && csdnAssembleText.length() != 0) {
//                            Long sourceId = Long.valueOf(4);
//                            MysqlReadWriteDAO.storeAssemble(csdnAssembleContent.toString(), csdnAssembleText.toString(), domain.getDomainId(), facet.getFacetId(), sourceId);
//                        } else {
//                            Log.log("csdn链接无法解析：" + csdnUrl);
//                        }
//                    }
//                } catch (Exception e) {
//                    Log.log("\ncsdn碎片列表地址获取失败\n链接地址：" + csdnSearchUrl);
//                }
            }
            while (threadMap.isEmpty()==false){
                Thread.sleep(200);
                for(String name:threadMap.keySet()){
                    if(threadMap.get(name).getState()==TERMINATED){
                        threadMap.remove(name);
                        Log.log("==========课程 " + domainName + "，主题 " + topicName +"分面"+name+ " 下的碎片已经爬取==========");
                    }
                }
            }

            Log.log("==========课程 " + domainName + "，主题 " + topicName + " 下的碎片已经爬取==========");
        }
    }



    /**
     * 根据课程名、主题名爬取碎片
     */
    public static Map<String,Thread> storeAssembleByTopicNameReturnThreadMap(Domain domain, Topic topic, Boolean increment) throws Exception {
        //定义课程、主题内容
        String domainName = domain.getDomainName();
        String topicName = topic.getTopicName();
        String topicUrl = topic.getTopicUrl();

        /**
         * 判断该主题的信息是不是在所有表格中已经存在
         * 只要有一个不存在就需要再次爬取（再次模拟加载浏览器）
         */

        List<Facet> facets = MysqlReadWriteDAO.getTopicFacet(topic.getTopicId());

        /**
         * selenium解析wiki网页
         */
        HashMap<String,Thread> threadMap=new HashMap<>();
        if (facets.isEmpty())//分面为空的情况
        {
            Log.log("==========课程 " + domainName + "，主题 " + topicName + " 下无分面，无法爬碎片==========");
        } else {
            String topicHtml = SpiderUtils.seleniumWiki(topicUrl);
            Document wikiDoc = JsoupDao.parseHtmlText(topicHtml);
            for (Facet facet : facets) {
                long assembleCount=MysqlReadWriteDAO.findAssembleNumByFacet(facet.getFacetId());
                Log.log("====分面："+facet.getFacetName()+ "下的碎片数量为" +assembleCount );
                if(assembleCount>4l && increment==false){
                    Log.log("分面："+facet.getFacetName()+ "下碎片已爬取，无需再爬" );
                    continue;
                }
                if(assembleCount>9l && increment==true){
                    Log.log("分面："+facet.getFacetName()+ ",下碎片已增量爬取，无需再爬" );
                    continue;
                }
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
                    MysqlReadWriteDAO.storeAssemble(assembleContent, assembleText, domain.getDomainId(), facet.getFacetId(), sourceId);
                }
                try {
                    CsdnCrawler.csdnSpiderAssemble(domain,topic,facet,increment);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //CSDN爬虫
//                String csdnSearchUrl = "https://so.csdn.net/so/search/blog?q=" + topicName + facetName + "&t=blog&p=1&s=0&tm=0&lv=-1&ft=0&l=&u=";
//                try {
//                    String csdnSearchHtml = SpiderUtils.seleniumCsdn(csdnSearchUrl);
//                    Document csdnSerarchDoc = JsoupDao.parseHtmlText(csdnSearchHtml);
//                    Elements csdnUrlElements = csdnSerarchDoc.select("div[class*='list-item']").select("a[class='normal-list-link']");
//                    if (csdnUrlElements.isEmpty()) {
//                        Log.log("没有拿到csdn链接！");
//                    }
//                    for (int i = 0; i < 5; i++) {
//                        Element urlElement = csdnUrlElements.get(i);
//                        String csdnUrl = urlElement.attr("href");
//
//                        String csdnHtml = SpiderUtils.seleniumCsdn(csdnUrl);
//                        Document csdnDoc = JsoupDao.parseHtmlText(csdnHtml);
//                        Elements contentElement = csdnDoc.select("div[class='blog-content-box']");
//                        StringBuffer csdnAssembleContent = new StringBuffer();
//                        StringBuffer csdnAssembleText = new StringBuffer();
//                        csdnAssembleContent.append(contentElement.toString());
//                        csdnAssembleText.append(contentElement.text());
//                        if (csdnAssembleContent.length() != 0 && csdnAssembleText.length() != 0) {
//                            Long sourceId = Long.valueOf(4);
//                            MysqlReadWriteDAO.storeAssemble(csdnAssembleContent.toString(), csdnAssembleText.toString(), domain.getDomainId(), facet.getFacetId(), sourceId);
//                        } else {
//                            Log.log("csdn链接无法解析：" + csdnUrl);
//                        }
//                    }
//                } catch (Exception e) {
//                    Log.log("\ncsdn碎片列表地址获取失败\n链接地址：" + csdnSearchUrl);
//                }
            }
            while (threadMap.isEmpty()==false){
                Thread.sleep(200);
                for(String name:threadMap.keySet()){
                    if(threadMap.get(name).getState()==TERMINATED){
                        threadMap.remove(name);
                        Log.log("==========课程 " + domainName + "，主题 " + topicName +"分面"+name+ " 下的碎片已经爬取==========");
                    }
                }
            }

            Log.log("==========课程 " + domainName + "，主题 " + topicName + " 下的碎片已经爬取==========");
        }
        return threadMap;
    }




    /**
     * 简书网页爬碎片*/
/**
            for (Facet facet : facets) {


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
                            MysqlReadWriteDAO.storeAssemble(assembleContent, assembleText, domain.getDomainId(), facet.getFacetId(), sourceId);
                        } else {
                            Log.log("简书链接无法解析：" + jianshuUrl);
                        }
                    }
                } catch (Exception e) {
                    Log.log("\n简书碎片列表地址获取失败\n链接地址：" + jianshuSearchUrl);
                }
            }

            Log.log("==========课程 " + domainName + "，主题 " + topicName + " 下的碎片已经爬取==========");
 */




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
                //CSDN爬虫
                String csdnSearchUrl = "https://so.csdn.net/so/search/blog?q=" + topicName + facet.getFacetName() + "&t=blog&p=1&s=0&tm=0&lv=-1&ft=0&l=&u=";
                try {
                    String csdnSearchHtml = SpiderUtils.seleniumCsdn(csdnSearchUrl);
                    Document csdnSerarchDoc = JsoupDao.parseHtmlText(csdnSearchHtml);
                    Elements csdnUrlElements = csdnSerarchDoc.select("div[class*='list-item']").select("a[href]");
                    if (csdnUrlElements.isEmpty()) {
                        Log.log("没有拿到csdn链接！");
                    }
                    for (int i = 0; i < 5; i++) {
                        Element urlElement = csdnUrlElements.get(i);
                        String csdnUrl = urlElement.attr("href");

                        String csdnHtml = SpiderUtils.seleniumCsdn(csdnUrl);
                        Document csdnDoc = JsoupDao.parseHtmlText(csdnHtml);
                        Elements contentElement = csdnDoc.select("div[class='blog-content-box']");
                        StringBuffer csdnAssembleContent = new StringBuffer();
                        StringBuffer csdnAssembleText = new StringBuffer();
                        csdnAssembleContent.append(contentElement.toString());
                        csdnAssembleText.append(contentElement.text());
                        if (csdnAssembleContent.length() != 0 && csdnAssembleText.length() != 0) {
                            Long sourceId = Long.valueOf(4);
                            MysqlReadWriteDAO.storeAssemble(csdnAssembleContent.toString(), csdnAssembleText.toString(), domain.getDomainId(), facet.getFacetId(), sourceId);
                        } else {
                            Log.log("csdn链接无法解析：" + csdnUrl);
                        }
                    }
                } catch (Exception e) {
                    Log.log("\ncsdn碎片列表地址获取失败\n链接地址：" + csdnSearchUrl);
                }

            }
            Log.log("==========课程 " + domainName + "，主题 " + topicName + " 下的碎片已经增量爬取==========");
        }
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

}
