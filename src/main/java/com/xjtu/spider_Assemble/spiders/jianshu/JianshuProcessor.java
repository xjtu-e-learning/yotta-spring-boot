package com.xjtu.spider_Assemble.spiders.jianshu;

import com.xjtu.common.Config;

import com.xjtu.spider_Assemble.service.SpiderAssembleService;
import com.xjtu.spider_Assemble.spiders.webmagic.bean.Assembles;
import com.xjtu.spider_Assemble.spiders.webmagic.pipeline.SqlPipeline;
import com.xjtu.spider_Assemble.spiders.webmagic.spider.spiderCreate;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.selenium.SeleniumDownloader;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import javax.validation.constraints.Null;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JianshuProcessor implements PageProcessor {


    SpiderAssembleService spiderService;

    public JianshuProcessor(SpiderAssembleService spiderService) {
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
        //爬取碎片
        List<String> urls;
        // urls = page.getHtml().xpath("dl[@class='search-list J_search']/dd[@class='search-link']/a/@href").all();
        urls = page.getHtml().xpath("div[@class='result c-container new-pmd']/h3/a/@href").all();
        if(urls.size()==0){
            List<String> assembleContents = page.getHtml().xpath("div[@id=\"__next\"]//div[@class=\"_gp-ck\"]/section[1]").all();
            if(assembleContents.size()==0){
                System.out.println(page.getUrl()+"----不是简书网页！");
                return;
            }
            List<String> assembleTexts = page.getHtml().xpath("div[@id=\"__next\"]//div[@class=\"_gp-ck\"]/section[1]/tidyText()").all();
            Assembles assembles = new Assembles(assembleContents, assembleTexts);

            // System.out.println("网页的url为："+page.getUrl()+"爬取到的内容为："+assembles.toString());
            page.putField("assembles", assembles);
        }
        else {
            //System.out.println("链接数: "+urls.size()+" "+urls.get(0));
            //此处应该添加请求的附加信息，extras
            for (String url : urls) {
                Request request = new Request();
                request.setUrl(url);
                System.out.println(url);
                request.setExtras(page.getRequest().getExtras());
                page.addTargetRequest(request);
            }
        }
    }

    public Spider JianshuAnswerCrawl(String domainName) {
        //1.获取分面信息
        List<Map<String, Object>> facets = spiderService.getFacets(domainName);
//        if (facets == null || facets.size() == 0) {
//            return;
//        }
        Spider jianshuSpider = startCrawl(facets);
        return jianshuSpider;
    }

    /**
     * 只爬取新增的分面下的碎片
     * @param facets：包含课程名、主题名、分面名
     * @return
     */
    public Spider increasedCrawl(List<Map<String, Object>> facets)
    {
        Spider jianshuSpider = startCrawl(facets);
        return jianshuSpider;
    }
    public Spider startCrawl(List<Map<String, Object>> facets)
    {
        //2.添加连接请求
        List<Request> requests = new ArrayList<Request>();
        for (Map<String, Object> facet : facets) {
            Request request = new Request();
            String url = "https://www.baidu.com/s?ie=UTF-8&wd="
                    + facet.get("domainName") + " "
                    + facet.get("topicName") + " "
                    + facet.get("facetName")+" 简书";
            //添加链接;设置额外信息
            facet.put("sourceName", "简书");
            requests.add(request.setUrl(url).setExtras(facet));
        }
        Spider jianshuSpider = spiderCreate.create(new com.xjtu.spider_Assemble.spiders.jianshu.JianshuProcessor(this.spiderService))
                .addRequests(requests)
                .thread(Config.THREAD)
                .addPipeline(new SqlPipeline(this.spiderService))
                .addPipeline(new ConsolePipeline());

        jianshuSpider.runAsync();
        return jianshuSpider;
    }
}

