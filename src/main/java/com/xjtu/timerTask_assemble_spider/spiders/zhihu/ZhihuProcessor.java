package com.xjtu.timerTask_assemble_spider.spiders.zhihu;

import com.xjtu.assemble.repository.AssembleRepository;
import com.xjtu.common.Config;
import com.xjtu.timerTask_assemble_spider.service.TimeTaskSpiderAssembleService;
import com.xjtu.timerTask_assemble_spider.spiders.webmagic.bean.Assembles;
import com.xjtu.timerTask_assemble_spider.spiders.webmagic.pipeline.SqlPipeline;
import com.xjtu.timerTask_assemble_spider.spiders.webmagic.spider.spiderCreate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import java.text.SimpleDateFormat;
import java.util.*;

public class ZhihuProcessor implements PageProcessor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    TimeTaskSpiderAssembleService spiderService;

    public ZhihuProcessor(TimeTaskSpiderAssembleService spiderService) {
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
        //判断时间是否符合更新要求
        String temptime = page.getHtml().xpath("//*[@id='root']/div/main/div/article/div[2]").toString();
        if (temptime!=null)
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
            }
        }

        List<String> urls;
        //这里获取得到的大部分链接都是相对路径
        urls = page.getHtml().xpath("div[@class='List-item']//h2[@class='ContentItem-title']//a/@href").all();
        logger.debug(urls.toString());
        //此处应该添加请求的附加信息，extras
        for (String url : urls) {
            Request request = new Request();
            //如果链接中不包含字符串“zhihu”，可以断定是相对路径
            if (!url.contains("http"))
            {
                if (!url.contains("zhihu"))
                    request.setUrl("https://www.zhihu.com" + url);
                else
                    request.setUrl("https:" + url);
            } else {
                request.setUrl(url);
            }
            request.setExtras(page.getRequest().getExtras());
            page.addTargetRequest(request);
        }
    }

    public void zhihuAnswerCrawl(String domainName, AssembleRepository assembleRepository) {
        //1.获取分面信息
        List<Map<String, Object>> facets = spiderService.getFacets(domainName);
//        if (facets == null || facets.size() == 0) {
//            return;
//        }
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
                .addPipeline(new SqlPipeline(this.spiderService, assembleRepository))
                .addPipeline(new ConsolePipeline());

        zhihuSpider.runAsync();

    }
}
