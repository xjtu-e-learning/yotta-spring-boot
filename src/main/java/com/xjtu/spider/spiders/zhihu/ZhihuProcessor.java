package com.xjtu.spider.spiders.zhihu;


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
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 知乎碎片爬虫
 *
 * @author yangkuan
 */
public class ZhihuProcessor implements PageProcessor {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    SpiderService spiderService;

    public ZhihuProcessor(SpiderService spiderService) {
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
        List<String> assembleContents = page.getHtml().xpath("div[@class='RichContent-inner']/span[@class='RichText ztext CopyrightRichText-richText']").all();
        assembleContents.addAll(page.getHtml().xpath("div[@class='RichText ztext Post-RichText']").all());
        logger.debug(assembleContents.size() + " " + assembleContents.toString());
        List<String> assembleTexts = page.getHtml().xpath("div[@class='RichContent-inner']/span[@class='RichText ztext CopyrightRichText-richText']/tidyText()").all();
        assembleTexts.addAll(page.getHtml().xpath("div[@class='RichText ztext Post-RichText']/tidyText()").all());
        logger.debug(assembleTexts.size() + " " + assembleTexts.toString());
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

    public void zhihuAnswerCrawl(String domainName) {
        //1.获取分面信息
        List<Map<String, Object>> facets = spiderService.getFacets(domainName);
        if (facets == null || facets.size() == 0) {
            return;
        }
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
        YangKuanSpider.create(new ZhihuProcessor(this.spiderService))
                .addRequests(requests)
                .thread(Config.THREAD)
                .addPipeline(new SqlPipeline(this.spiderService))
                .addPipeline(new ConsolePipeline())
                .runAsync();
    }
}
