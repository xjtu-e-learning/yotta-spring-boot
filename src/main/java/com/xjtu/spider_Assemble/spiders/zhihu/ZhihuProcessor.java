package com.xjtu.spider_Assemble.spiders.zhihu;

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
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.monitor.SpiderMonitor;
import us.codecraft.webmagic.SpiderListener;
import us.codecraft.webmagic.monitor.SpiderMonitor.MonitorSpiderListener;
import us.codecraft.webmagic.monitor.SpiderStatusMXBean;
import us.codecraft.webmagic.monitor.SpiderStatus;

import javax.management.JMException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ZhihuProcessor implements PageProcessor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    SpiderAssembleService spiderService;

    public ZhihuProcessor(SpiderAssembleService spiderService) {
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
        List<String> assembleContentsTemp = page.getHtml().xpath("div[@class='RichContent-inner']/span[@class='RichText ztext CopyrightRichText-richText']").all();
        assembleContentsTemp.addAll(page.getHtml().xpath("div[@class='RichText ztext Post-RichText']").all());
        logger.debug(assembleContentsTemp.size() + " " + assembleContentsTemp.toString());
        List<String> assembleTextsTemp = page.getHtml().xpath("div[@class='RichContent-inner']/span[@class='RichText ztext CopyrightRichText-richText']/tidyText()").all();
        assembleTextsTemp.addAll(page.getHtml().xpath("div[@class='RichText ztext Post-RichText']/tidyText()").all());
        logger.debug(assembleTextsTemp.size() + " " + assembleTextsTemp.toString());
        List<String> assembleContents = new ArrayList<>();
        List<String> assembleTexts = new ArrayList<>();
        int i = 0;
        for (String assembleText : assembleTextsTemp) {
            if (!assembleText.endsWith("...")) {
                assembleContents.add(assembleContentsTemp.get(i));
                assembleTexts.add(assembleText);
            }
            i++;
        }
        Assembles assembles = new Assembles(assembleContents, assembleTexts);
        page.putField("assembles", assembles);

        List<String> urls;
        //这里获取得到的大部分链接都是相对路径
        urls = page.getHtml().xpath("div[@class='List-item']//h2[@class='ContentItem-title']//a/@href").all();
        logger.debug(urls.toString());
        //此处应该添加请求的附加信息，extras
        for (String url : urls) {
            Request request = new Request();
            //如果链接中不包含字符串“zhihu”，可以断定是相对路径
            if (!url.contains("zhihu")) {
                request.setUrl("https://www.zhihu.com" + url);
            } else {
                request.setUrl(url);
            }
            request.setExtras(page.getRequest().getExtras());
            page.addTargetRequest(request);
        }
    }

    public Spider zhihuAnswerCrawl(String domainName) {
        //1.获取分面信息
        List<Map<String, Object>> facets = spiderService.getFacets(domainName);
//        if (facets == null || facets.size() == 0) {
//            return;
//        }
        Spider zhihuSpider  = startCrawl(facets);

        return zhihuSpider;
    }

    /**
     * 只爬取新增的分面下的碎片
     * @param facets：包含课程名、主题名、分面名
     * @return
     */
    public Spider increasedCrawl(List<Map<String, Object>> facets)
    {
        Spider zhihuSpider = startCrawl(facets);
        return zhihuSpider;
    }

    public Spider startCrawl(List<Map<String, Object>> facets)
    {
        //2.添加连接请求
        List<Request> requests = new ArrayList<>();
        for (Map<String, Object> facet : facets) {
            Request request = new Request();
            String url = "https://www.zhihu.com/search?type=content&q="
                    + facet.get("domainName") + " "
                    + facet.get("topicName") + " "
                    + facet.get("facetName");
            //添加链接;设置额外信息
            facet.put("sourceName", "知乎");
            requests.add(request.setUrl(url).setExtras(facet));
        }
        //3.创建ZhihuProcessor
        Spider zhihuSpider = spiderCreate.create(new ZhihuProcessor(this.spiderService))
                .addRequests(requests)
                .thread(Config.THREAD)
                .addPipeline(new SqlPipeline(this.spiderService))
                .addPipeline(new ConsolePipeline());
        zhihuSpider.runAsync();

        return zhihuSpider;
    }
}
