package com.xjtu.spider_Assemble.spiders.toutiao;


import com.xjtu.common.Config;

import com.xjtu.spider_Assemble.service.SpiderAssembleService;
import com.xjtu.spider_Assemble.spiders.webmagic.bean.Assembles;
import com.xjtu.spider_Assemble.spiders.webmagic.pipeline.SqlPipeline;
import com.xjtu.spider_Assemble.spiders.webmagic.spider.spiderCreate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.selenium.SeleniumDownloader;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.JsonPathSelector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 今日头条爬虫，爬碎片
 */

public class ToutiaoProcessor implements PageProcessor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    SpiderAssembleService spiderService;

    public ToutiaoProcessor(SpiderAssembleService spiderService) {
        this.spiderService = spiderService;
    }

    private Site site = Site.me()
            .setRetryTimes(Config.retryTimes)
            .setRetrySleepTime(Config.retrySleepTime)
            .setSleepTime(Config.sleepTime)
            .setTimeOut(Config.timeOut)
            .setCharset("UTF-8")
            .addHeader("User-Agent", Config.userAgent);
//            .addHeader("Accept", "application/json, text/javascript")
//            .addHeader("Host","https://www.toutiao.com")
//            .addHeader("Accept","application/json, text/plain, */*")
//            .addHeader("Accept-Language","zh-CN,zh;q=0.9")
//            .addHeader("Accept-Encoding","gzip, deflate, br")
//            .addHeader("X-Requested-With","XMLHttpRequest")
//            .addHeader("Content-Type","application/x-www-form-urlencoded")
//            .addHeader("Referer","https://www.toutiao.com");


    @Override
    public Site getSite() {
        return site;
    }

    @Override
    public void process(Page page) {

        //爬取碎片
        List<String> assembleContents = page.getHtml().xpath("div[@class='article-content']").all();
        List<String> assembleTexts = page.getHtml().xpath("div[@class='article-content']/tidyText()").all();
        Assembles assembles = new Assembles(assembleContents, assembleTexts);
        page.putField("assembles", assembles);

        if (page.getUrl().toString().contains("search")) {
            List<String> urls = new ArrayList<>();
            urls = page.getHtml().xpath("//a[@class='link title']/@href").all();//*[@id="J_section_0"]/div/div/div[1]/div/div[1]/a
            //这里获取得到的大部分链接都是相对路径
//            List<String> datas = new JsonPathSelector("$.data").selectList(page.getHtml().xpath("body/pre/text()").all().get(0));
//            for (String data : datas) {
//                if (data.contains("item_source_url")) {
//                    String url = new JsonPathSelector("$.item_source_url").select(data);
//                    urls.add(url);
//                }
//            }
            logger.debug("urls: " + urls.toString());
            //此处应该添加请求的附加信息，extras
            for (String url : urls) {
                Request request = new Request();
                // /group/a6526867799166419469/ 进行字符创拼接
                request.setUrl("https://www.toutiao.com" + url);
                request.setExtras(page.getRequest().getExtras());
                page.addTargetRequest(request);
            }
        }
    }

    public Spider toutiaoAnswerCrawl(String domainName) {
        //1.获取分面信息
        List<Map<String, Object>> facets = spiderService.getFacets(domainName);
//        if (facets == null || facets.size() == 0) {
//            return;
//        }
        Spider toutiaoSpider  = startCrawl(facets);
        return toutiaoSpider;
    }

    /**
     * 只爬取新增的分面下的碎片
     * @param facets：包含课程名、主题名、分面名
     * @return
     */
    public Spider increasedCrawl(List<Map<String, Object>> facets)
    {
        Spider toutiaoSpider = startCrawl(facets);
        return toutiaoSpider;
    }

    public Spider startCrawl(List<Map<String, Object>> facets)
    {
        //2.添加连接请求
        List<Request> requests = new ArrayList<>();
        for (Map<String, Object> facet : facets) {
            Request request = new Request();

            String url = "https://www.toutiao.com/search/?keyword="
                    + facet.get("domainName") + "%20"
                    + facet.get("topicName") + "%20"
                    + facet.get("facetName");

            //添加链接;设置额外信息
            facet.put("sourceName", "今日头条");
            requests.add(request.setUrl(url).setExtras(facet));
        }
        //3.创建ToutiaoProcessor
        System.setProperty("selenuim_config", "D:\\spiderProject\\webMagicProject\\chromedriver/config.ini");
        Spider toutiaoSpider = spiderCreate.create(new com.xjtu.spider_Assemble.spiders.toutiao.ToutiaoProcessor(this.spiderService))
                .addRequests(requests)
                .setDownloader(new SeleniumDownloader("D:\\spiderProject\\webMagicProject\\chromedriver/chromedriver.exe"))
                .thread(Config.THREAD)
                .addPipeline(new SqlPipeline(this.spiderService));
        //.addPipeline(new ConsolePipeline())

        toutiaoSpider.runAsync();
        return toutiaoSpider;
    }
}
