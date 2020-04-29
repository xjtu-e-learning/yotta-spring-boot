package com.xjtu.spider_Assemble.spiders.baiduzhidao;

import com.xjtu.common.Config;

import com.xjtu.spider_Assemble.service.SpiderAssembleService;
import com.xjtu.spider_Assemble.spiders.webmagic.bean.Assembles;
import com.xjtu.spider_Assemble.spiders.webmagic.pipeline.SqlPipeline;
import com.xjtu.spider_Assemble.spiders.webmagic.spider.spiderCreate;
import sun.security.provider.ConfigFile;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 百度知道爬虫，爬碎片
 */

public class BaiduZhidaoProcessor implements PageProcessor {
    public
    BaiduZhidaoProcessor(SpiderAssembleService spiderService) {
        this.spiderService = spiderService;
    }

    SpiderAssembleService spiderService;

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
        List<String> assembleContents = page.getHtml().xpath("//div[@class='best-text mb-10']").all();
        List<String> assembleTexts = page.getHtml().xpath("//div[@class='best-text mb-10']/tidyText()").all();
        Assembles assembles = new Assembles(assembleContents, assembleTexts);
        page.putField("assembles", assembles);   //保存碎片信息
        //爬取碎片
        List<String> urls;
        urls = page.getHtml().xpath("dl[@class='dl']//a[@class='ti']/@href").all();
        //此处应该添加请求的附加信息，extras
        for (String url : urls) {
            Request request = new Request();
            request.setUrl(url);
            request.setExtras(page.getRequest().getExtras());   //Extras中包含信息: domainName, topicName, facetName, sourceName
            page.addTargetRequest(request);
        }
    }

    public Spider baiduAnswerCrawl(String domainName) {
        //1.获取分面信息
        if (spiderService == null) {
            System.out.println("YYYYYYYYYYYYYYYYYYYYYYYYYY");
        }
        List<Map<String, Object>> facets = spiderService.getFacets(domainName);
//        if (facets == null || facets.size() == 0) {
//            return;
//        }
        Spider baiduzhidaoSpider = startCrawl(facets);
        return baiduzhidaoSpider;
    }

    /**
     * 只爬取新增的分面下的碎片
     * @param facets：包含课程名、主题名、分面名
     * @return
     */
    public Spider increasedBaiduAnswerCrawl(List<Map<String, Object>> facets) {
        //只爬取新增加的分面的碎片
        Spider spider = startCrawl(facets);
        return spider;
    }

    public Spider startCrawl(List<Map<String, Object>> facets)
    {
        //2.添加连接请求，根据课程名，主题名，分面名爬取内容
        List<Request> requests = new ArrayList<>();
        for (Map<String, Object> facet : facets) {
            Request request = new Request();
            String url = "https://zhidao.baidu.com/search?lm=0&rn=10&pn=0&fr=search&ie=gbk&word="
                    + facet.get("domainName") + " "
                    + facet.get("topicName") + " "
                    + facet.get("facetName");
            //添加链接;设置额外信息
            facet.put("sourceName", "百度知道");
            requests.add(request.setUrl(url).setExtras(facet));
        }
        Spider baiduzhidaoSpider = spiderCreate.create(new com.xjtu.spider_Assemble.spiders.baiduzhidao.BaiduZhidaoProcessor(this.spiderService))
                .addRequests(requests)
                .thread(Config.THREAD)
                .addPipeline(new SqlPipeline(this.spiderService))
                .addPipeline(new ConsolePipeline());

        baiduzhidaoSpider.runAsync();
        return baiduzhidaoSpider;
    }
}
