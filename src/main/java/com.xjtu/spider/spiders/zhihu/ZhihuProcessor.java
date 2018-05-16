package com.xjtu.spider.spiders.zhihu;


import com.xjtu.common.Config;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ZhihuProcessor implements PageProcessor {

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
        List<String> fragments = page.getHtml().xpath("div[@class='RichContent-inner']/span[@class='RichText CopyrightRichText-richText']").all();
        List<String> fragmentsPureText = page.getHtml().xpath("div[@class='RichContent-inner']/span[@class='RichText CopyrightRichText-richText']/tidyText()").all();
        FragmentContent fragmentContent = new FragmentContent(fragments, fragmentsPureText);
        page.putField("fragmentContent", fragmentContent);

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
    public void zhihuAnswerCrawl(String courseName){
        //1.获取分面名
        ProcessorSQL processorSQL = new ProcessorSQL();
        List<Map<String, Object>> allFacetsInformation = processorSQL.getAllFacets(Config.FACET_TABLE,courseName);
        //2.添加连接请求
        List<Request> requests = new ArrayList<>();
        for(Map<String, Object> facetInformation : allFacetsInformation){
            Request request = new Request();
            String url = "https://www.zhihu.com/search?type=content&q="
                    +facetInformation.get("ClassName")+" "
                    +facetInformation.get("TermName")+" "
                    +facetInformation.get("FacetName");
            //添加链接;设置额外信息
            facetInformation.put("SourceName", "知乎");
            requests.add(request.setUrl(url).setExtras(facetInformation));
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
