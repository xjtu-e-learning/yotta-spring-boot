package com.xjtu.spider.spiders.zhihu;


import com.xjtu.common.Config;
import com.xjtu.spider.spiders.webmagic.bean.Assembles;
import com.xjtu.spider.spiders.webmagic.pipeline.SqlPipeline;
import com.xjtu.spider.spiders.webmagic.service.SQLService;
import com.xjtu.spider.spiders.webmagic.spider.YangKuanSpider;
import org.springframework.beans.factory.annotation.Autowired;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ZhihuProcessor implements PageProcessor {

    @Autowired
    SQLService sqlService;

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
    public void process(Page page){
        //爬取碎片
        List<String> assembleContents = page.getHtml().xpath("div[@class='RichContent-inner']/span[@class='RichText CopyrightRichText-richText']").all();
        List<String> assembleTexts = page.getHtml().xpath("div[@class='RichContent-inner']/span[@class='RichText CopyrightRichText-richText']/tidyText()").all();
        Assembles assembles = new Assembles(assembleContents, assembleTexts);
        page.putField("assembles", assembles);

        List<String> urls;
        //这里获取得到的大部分链接都是相对路径
        urls = page.getHtml().xpath("div[@class='title']/a[@class='js-title-link']/@href").all();
        //此处应该添加请求的附加信息，extras
        for (String url : urls) {
            Request request = new Request();
            //如果链接中不包含字符串“zhihu”，可以断定是相对路径
            if(!url.contains("zhihu")){
                request.setUrl("https://www.zhihu.com"+url);
            }
            else{
                request.setUrl(url);
            }
            request.setExtras(page.getRequest().getExtras());
            page.addTargetRequest(request);
        }
    }
    public void zhihuAnswerCrawl(String domainName){
        //1.获取分面信息
        List<Map<String, Object>> facets = sqlService.getFacets(domainName);
        //2.添加连接请求
        List<Request> requests = new ArrayList<>();
        for(Map<String, Object> facet : facets){
            Request request = new Request();
            String url = "https://www.zhihu.com/search?type=content&q="
                    +facet.get("domainName")+" "
                    +facet.get("topicName")+" "
                    +facet.get("facetName");
            //添加链接;设置额外信息
            facet.put("sourceName", "知乎");
            requests.add(request.setUrl(url).setExtras(facet));
        }
        //3.创建ZhihuProcessor
        YangKuanSpider.create(new ZhihuProcessor())
                .addRequests(requests)
                .thread(Config.THREAD)
                .addPipeline(new SqlPipeline())
                .addPipeline(new ConsolePipeline())
                .runAsync();
    }
}
