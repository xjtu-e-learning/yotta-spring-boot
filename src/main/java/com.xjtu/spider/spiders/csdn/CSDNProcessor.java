package com.xjtu.spider.spiders.csdn;


import com.xjtu.common.Config;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CSDNProcessor implements PageProcessor {

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
        List<String> fragments =  page.getHtml().xpath("div[@id='article_content']").all();
        List<String> fragmentsPureText =  page.getHtml().xpath("div[@id='article_content']/tidyText()").all();
        FragmentContent fragmentContent = new FragmentContent(fragments, fragmentsPureText);
        page.putField("fragmentContent", fragmentContent);

        //爬取碎片
        List<String> urls;
        urls = page.getHtml().xpath("dl[@class='search-list J_search']/dd[@class='search-link']/a/@href").all();
        //System.out.println("链接数: "+urls.size()+" "+urls.get(0));
        //此处应该添加请求的附加信息，extras
        for(String url:urls){
            Request request = new Request();
            request.setUrl(url);
            //System.out.println(url);
            request.setExtras(page.getRequest().getExtras());
            page.addTargetRequest(request);
        }
    }
    public void CSDNAnswerCrawl(String courseName){
        //1.获取分面名
        ProcessorSQL processorSQL = new ProcessorSQL();
        List<Map<String, Object>> allFacetsInformation = processorSQL.getAllFacets(Config.FACET_TABLE,courseName);
        //2.添加连接请求
        List<Request> requests = new ArrayList<Request>();
        for(Map<String, Object> facetInformation : allFacetsInformation){
            Request request = new Request();
            String url = "http://so.csdn.net/so/search/s.do?q="
                    + facetInformation.get("ClassName") + " "
                    + facetInformation.get("TermName") + " "
                    + facetInformation.get("FacetName");
            //添加链接;设置额外信息
            facetInformation.put("SourceName", "csdn");
            requests.add(request.setUrl(url).setExtras(facetInformation));
        }
        YangKuanSpider.create(new CSDNProcessor())
                .addRequests(requests)
                .thread(Config.THREAD)
                .addPipeline(new SqlPipeline())
                .addPipeline(new ConsolePipeline())
                .runAsync();
    }
}
