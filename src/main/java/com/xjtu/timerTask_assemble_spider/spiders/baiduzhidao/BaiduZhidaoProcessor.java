package com.xjtu.timerTask_assemble_spider.spiders.baiduzhidao;

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

/**
 * 百度知道爬虫，爬碎片
 */

public class BaiduZhidaoProcessor implements PageProcessor {
    public BaiduZhidaoProcessor(TimeTaskSpiderAssembleService spiderService) {
        this.spiderService = spiderService;
    }

    TimeTaskSpiderAssembleService spiderService;

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
        String temptime = page.getHtml().xpath("//*[@id='wgt-replyer-all-1467851090']/span[3]/span").toString();


        if (temptime!= null)
        {
            String time = temptime.substring(temptime.length()-10, temptime.length());
            //设置日期格式
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // 获取当前系统时间，也可使用当前时间戳

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 30);
            Date date = calendar.getTime();
            String localdate = df.format(date);
            if (time.compareTo(localdate) > 0)
            {
                List<String> assembleContents = page.getHtml().xpath("//div[@class='best-text mb-10']").all();
                List<String> assembleTexts = page.getHtml().xpath("//div[@class='best-text mb-10']/tidyText()").all();
                Assembles assembles = new Assembles(assembleContents, assembleTexts);
                page.putField("assembles", assembles);   //保存碎片信息
            }
        }

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

    public void baiduAnswerCrawl(String domainName, AssembleRepository assembleRepository) {
        //1.获取分面信息
        if (spiderService == null) {
            System.out.println("YYYYYYYYYYYYYYYYYYYYYYYYYY");
        }
        List<Map<String, Object>> facets = spiderService.getFacets(domainName);
//        if (facets == null || facets.size() == 0) {
//            return;
//        }
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
        Spider baiduzhidaoSpider = spiderCreate.create(new BaiduZhidaoProcessor(this.spiderService))
                .addRequests(requests)
                .thread(Config.THREAD)
                .addPipeline(new SqlPipeline(this.spiderService, assembleRepository))
                .addPipeline(new ConsolePipeline());

        baiduzhidaoSpider.runAsync();

    }
}
