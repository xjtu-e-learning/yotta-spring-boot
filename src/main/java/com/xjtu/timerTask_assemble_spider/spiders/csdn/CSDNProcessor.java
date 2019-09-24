package com.xjtu.timerTask_assemble_spider.spiders.csdn;

import com.xjtu.assemble.repository.AssembleRepository;
import com.xjtu.common.Config;
import com.xjtu.timerTask_assemble_spider.service.TimeTaskSpiderAssembleService;
import com.xjtu.timerTask_assemble_spider.spiders.webmagic.bean.Assembles;
import com.xjtu.timerTask_assemble_spider.spiders.webmagic.pipeline.SqlPipeline;
import com.xjtu.timerTask_assemble_spider.spiders.webmagic.spider.spiderCreate;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import java.text.SimpleDateFormat;
import java.util.*;

public class CSDNProcessor implements PageProcessor {


    TimeTaskSpiderAssembleService spiderService;

    public CSDNProcessor(TimeTaskSpiderAssembleService spiderService) {
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
        String time = page.getHtml().xpath("//*[@id='mainBox']/main/div[1]/div/div/div[2]/div[1]/span[1]").toString();
        if (time!=null)
        {
            //设置日期格式
            SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
            // 获取当前系统时间，也可使用当前时间戳

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 30);
            Date date = calendar.getTime();
            String localdate = df.format(date);
            if (time.compareTo(localdate) > 0)
            {
                List<String> assembleContents = page.getHtml().xpath("div[@id='article_content']").all();
                List<String> assembleTexts = page.getHtml().xpath("div[@id='article_content']/tidyText()").all();
                Assembles assembles = new Assembles(assembleContents, assembleTexts);
                page.putField("assembles", assembles);
            }
        }

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

    public void CSDNAnswerCrawl(String domainName, AssembleRepository assembleRepository) {
        //1.获取分面信息
        List<Map<String, Object>> facets = spiderService.getFacets(domainName);
//        if (facets == null || facets.size() == 0) {
//            return;
//        }
        //2.添加连接请求
        List<Request> requests = new ArrayList<Request>();
        for (Map<String, Object> facet : facets) {
            Request request = new Request();
            String url = "https://so.csdn.net/so/search/s.do?q="
                    + facet.get("domainName") + " "
                    + facet.get("topicName") + " "
                    + facet.get("facetName");
            //添加链接;设置额外信息
            facet.put("sourceName", "csdn");
            requests.add(request.setUrl(url).setExtras(facet));
        }
        Spider csdnSpider = spiderCreate.create(new CSDNProcessor(this.spiderService))
                .addRequests(requests)
                .thread(Config.THREAD)
                .addPipeline(new SqlPipeline(this.spiderService, assembleRepository))
                .addPipeline(new ConsolePipeline());

        csdnSpider.runAsync();

    }
}

