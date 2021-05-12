package com.xjtu.spider_dynamic_output.spiders.csdn;

import com.xjtu.domain.domain.Domain;
import com.xjtu.facet.domain.Facet;
import com.xjtu.spider_dynamic_output.spiders.wikicn.MysqlReadWriteDAO;
import com.xjtu.topic.domain.Topic;
import com.xjtu.utils.JsoupDao;
import com.xjtu.utils.Log;
import com.xjtu.utils.SpiderUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.concurrent.locks.ReentrantLock;

public class CsdnCrawler {

    /**
     *
     * @param domain
     * @param topic
     * @param facet
     * @param incremental  是否增量爬取 true为是
     */
    public static void csdnSpiderAssemble(Domain domain,Topic topic, Facet facet,Boolean incremental){
        String topicName=topic.getTopicName();
        String facetName=facet.getFacetName();
        String csdnSearchUrl = "https://so.csdn.net/so/search/blog?q=" + topicName + facetName + "&t=blog&p=1&s=0&tm=0&lv=-1&ft=0&l=&u=";
        try {
            String csdnSearchHtml = SpiderUtils.seleniumCsdn(csdnSearchUrl);
            Document csdnSerarchDoc = JsoupDao.parseHtmlText(csdnSearchHtml);
            Elements csdnUrlElements = csdnSerarchDoc.select("div[class*='list-item']").select("a[class='block-title']");
            if (csdnUrlElements.isEmpty()) {
                Log.log("没有拿到csdn链接！");
            }
            int i=0,j=5;
            if(incremental==true){
                i=6;j=10;
            }
            for (; i < j; i++) {
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
            Log.log("\ncsdn碎片获取失败\n链接地址：" + csdnSearchUrl);
        }

    }

}
