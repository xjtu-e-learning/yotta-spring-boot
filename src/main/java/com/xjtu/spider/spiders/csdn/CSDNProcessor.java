package com.xjtu.spider.spiders.csdn;


import com.xjtu.common.Config;
import com.xjtu.spider.service.SpiderService;
import com.xjtu.spider.spiders.webmagic.bean.Assembles;
import com.xjtu.spider.spiders.webmagic.pipeline.SqlPipeline;
import com.xjtu.spider.spiders.webmagic.spider.YangKuanSpider;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * CSDN爬虫
 *
 * @author yangkuan
 * @date 2018/05/18
 */
public class CSDNProcessor implements PageProcessor {


    SpiderService spiderService;

    public CSDNProcessor(SpiderService spiderService) {
        this.spiderService = spiderService;
    }

    private Site site = Site.me()
            .setRetryTimes(Config.retryTimes)
            .setRetrySleepTime(Config.retrySleepTime)
            .setSleepTime(Config.sleepTime)
            .setTimeOut(Config.timeOut)
            .addHeader("User-Agent", Config.userAgent)
            .addHeader("Accept", "*/*");

    @Override
    public Site getSite() {
        return site;
    }

    @Override
    public void process(Page page) {
        List<String> assembleContents = page.getHtml().xpath("div[@id='article_content']").all();
        List<String> assembleTexts = page.getHtml().xpath("div[@id='article_content']/tidyText()").all();
        Assembles assembles = new Assembles(assembleContents, assembleTexts);
        page.putField("assembles", assembles);

        //爬取碎片
        List<String> urls;
        urls = page.getHtml().xpath("dl[@class='search-list J_search']/dd[@class='search-link']/a/@href").all();
        //System.out.println("链接数: "+urls.size()+" "+urls.get(0));
        //此处应该添加请求的附加信息，extras
        for (String url : urls) {
            Request request = new Request();
            request.setUrl(url);
            //System.out.println(url);
            request.setExtras(page.getRequest().getExtras());
            page.addTargetRequest(request);
        }
    }

    public void CSDNAnswerCrawl(String domainName) {
        //1.获取分面信息
        List<Map<String, Object>> facets = spiderService.getFacets(domainName);
        if (facets == null || facets.size() == 0) {
            return;
        }
        //2.添加连接请求
        List<Request> requests = new ArrayList<Request>();
        for (Map<String, Object> facet : facets) {
            Request request = new Request();
            String url = "http://so.csdn.net/so/search/s.do?q="
                    + facet.get("domainName") + " "
                    + facet.get("topicName") + " "
                    + facet.get("facetName");
            //添加链接;设置额外信息
            facet.put("sourceName", "csdn");
            requests.add(request.setUrl(url).setExtras(facet));
        }
        YangKuanSpider.create(new CSDNProcessor(this.spiderService))
                .addRequests(requests)
                .thread(Config.THREAD)
                .addPipeline(new SqlPipeline(this.spiderService))
                .addPipeline(new ConsolePipeline())
                .runAsync();
    }
}
