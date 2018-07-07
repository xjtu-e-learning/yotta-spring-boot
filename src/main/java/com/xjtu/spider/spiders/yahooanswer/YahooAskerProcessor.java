package com.xjtu.spider.spiders.yahooanswer;


import com.xjtu.common.Config;
import com.xjtu.question.domain.Question;
import com.xjtu.spider.service.SpiderService;
import com.xjtu.spider.spiders.stackoverflow.ProcessFeature;
import com.xjtu.spider.spiders.webmagic.bean.Asker;
import com.xjtu.spider.spiders.webmagic.pipeline.SqlAskerPipeline;
import com.xjtu.spider.spiders.webmagic.spider.YangKuanSpider;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 雅虎问答
 *
 * @author yangkuan
 */
public class YahooAskerProcessor implements PageProcessor {


    SpiderService spiderService;

    public YahooAskerProcessor(SpiderService spiderService) {
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
        Html html = page.getHtml();

        System.out.println("提问者主页：" + page.getUrl());
        // 获取提问者姓名、声望值、回答总数、提问总数、浏览总数、最佳答案率（雅虎问答有，SO没有）
        String askerName = html.xpath("//h1[@id='name-text-long']/text()").get();
        String askerReputation = html.xpath("//div[@id='div-pt']/span/text()").get();
        String askerAnswerCount = html.xpath("//div[@id='div-answ']/span/text()").get();
        String askerQuestionCount = html.xpath("//div[@id='div-ques']/span/text()").get();
        String askerBestAnswerRate = html.xpath("//div[@id='div-ba']//span/text()").get();
        askerReputation = ProcessFeature.processWebsiteNumbers(askerReputation);
        askerAnswerCount = ProcessFeature.processWebsiteNumbers(askerAnswerCount);
        askerQuestionCount = ProcessFeature.processWebsiteNumbers(askerQuestionCount);
        System.out.println("提问者姓名: " + askerName +
                ", 提问者声望值: " + askerReputation +
                ", 提问者回答总数: " + askerAnswerCount +
                ", 提问者提问总数: " + askerQuestionCount +
                ", 提问者最佳答案比例为: " + askerBestAnswerRate);

        Asker asker = new Asker();
        asker.setAskerName(askerName);
        asker.setAskerReputation(askerReputation);
        asker.setAskerAnswerCount(askerAnswerCount);
        asker.setAskerQuestionCount(askerQuestionCount);
        asker.setAskerBestAnswerRate(askerBestAnswerRate);

        page.putField("asker", asker);

    }

    public void YahooCrawl(String domainName) {
        // 获取问题信息
        List<Question> questions = spiderService.getQuestions("Stackoverflow", domainName);
        // 添加连接请求
        List<Request> requests = new ArrayList<>();
        for (Question question : questions) {
            Request request = new Request();
            String url = question.getAskerUrl();
            // 已经有提问者信息的问题不用爬取
            if (question.getAskerReputation().equals("")) {
                // 设置额外信息
                Map<String, Object> questionMap = question.convertToMap();
                requests.add(request.setUrl(url).setExtras(questionMap));
            }
        }

        YangKuanSpider.create(new YahooAskerProcessor(this.spiderService))
                .addRequests(requests)
                .thread(Config.THREAD)
                .addPipeline(new SqlAskerPipeline(this.spiderService))
//                .addPipeline(new ConsolePipeline())
                .runAsync();

    }

    public static void main(String[] args) {


    }

}
