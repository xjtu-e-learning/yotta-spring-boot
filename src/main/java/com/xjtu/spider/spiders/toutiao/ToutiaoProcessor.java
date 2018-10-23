package com.xjtu.spider.spiders.toutiao;

import com.xjtu.common.Config;
import com.xjtu.spider.service.SpiderService;
import com.xjtu.spider.spiders.webmagic.bean.Assembles;
import com.xjtu.spider.spiders.webmagic.pipeline.SqlPipeline;
import com.xjtu.spider.spiders.webmagic.spider.YangKuanSpider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.downloader.selenium.SeleniumDownloader;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.JsonPathSelector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 今日头条爬虫
 *
 * @author yangkuan
 */
public class ToutiaoProcessor implements PageProcessor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    SpiderService spiderService;

    public ToutiaoProcessor(SpiderService spiderService) {
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

        if (page.getUrl().toString().contains("search_content")) {
            List<String> urls = new ArrayList<>();
            //这里获取得到的大部分链接都是相对路径
            List<String> datas = new JsonPathSelector("$.data").selectList(page.getHtml().xpath("body/pre/text()").all().get(0));
            for (String data : datas) {
                if (data.contains("item_source_url")) {
                    String url = new JsonPathSelector("$.item_source_url").select(data);
                    urls.add(url);
                }
            }
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

    public void toutiaoAnswerCrawl(String domainName) {
        //1.获取分面信息
        List<Map<String, Object>> facets = spiderService.getFacets(domainName);
        if (facets == null || facets.size() == 0) {
            return;
        }
        //2.添加连接请求
        List<Request> requests = new ArrayList<>();
        for (Map<String, Object> facet : facets) {
            Request request = new Request();

            String url = "https://www.toutiao.com/search_content/?offset=0&format=json&keyword="
                    + facet.get("domainName") + "+"
                    + facet.get("topicName") + "+"
                    + facet.get("facetName") + "&autoload=true&count=20&cur_tab=1&from=search_tab";

            //添加链接;设置额外信息
            facet.put("sourceName", "今日头条");
            requests.add(request.setUrl(url).setExtras(facet));
        }
        //3.创建ToutiaoProcessor
        System.setProperty("selenuim_config", "E:/workspace/webmagic-selenium/config.ini");
        YangKuanSpider.create(new ToutiaoProcessor(this.spiderService))
                .addRequests(requests)
                .setDownloader(new SeleniumDownloader("E:\\workspace\\chromedriver.exe"))
                .thread(Config.THREAD)
                .addPipeline(new SqlPipeline(this.spiderService))
                //.addPipeline(new ConsolePipeline())
                .runAsync();
    }
}
