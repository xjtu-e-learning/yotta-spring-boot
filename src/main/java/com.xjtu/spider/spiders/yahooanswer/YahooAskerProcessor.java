package com.xjtu.spider.spiders.yahooanswer;


import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class YahooAskerProcessor implements PageProcessor {

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
        String asker_name = html.xpath("//h1[@id='name-text-long']/text()").get();
        String asker_reputation = html.xpath("//div[@id='div-pt']/span/text()").get();
        String asker_answerCount = html.xpath("//div[@id='div-answ']/span/text()").get();
        String asker_questionCount = html.xpath("//div[@id='div-ques']/span/text()").get();
        String asker_best_answer_rate = html.xpath("//div[@id='div-ba']//span/text()").get();
        asker_reputation = ProcessFeature.processWebsiteNumbers(asker_reputation);
        asker_answerCount = ProcessFeature.processWebsiteNumbers(asker_answerCount);
        asker_questionCount = ProcessFeature.processWebsiteNumbers(asker_questionCount);
        System.out.println("提问者姓名: " + asker_name + ", 提问者声望值: " + asker_reputation + ", 提问者回答总数: " + asker_answerCount + ", 提问者提问总数: " + asker_questionCount + ", 提问者最佳答案比例为: " + asker_best_answer_rate);

        // 获取extras中的FragmentContentQuestion对象信息
        FragmentContentAsker fragmentContentAsker = new FragmentContentAsker();
        fragmentContentAsker.setAsker_name(asker_name);
        fragmentContentAsker.setAsker_reputation(asker_reputation);
        fragmentContentAsker.setAsker_answerCount(asker_answerCount);
        fragmentContentAsker.setAsker_questionCount(asker_questionCount);
        fragmentContentAsker.setAsker_best_answer_rate(asker_best_answer_rate);
        page.putField("fragmentContentAsker", fragmentContentAsker);

    }

    public void YahooCrawl(String courseName) {
        // 获取问题信息
        ProcessorSQL processorSQL = new ProcessorSQL();
        List<Map<String, Object>> questions = processorSQL.getQuestions(
                Config.ASSEMBLE_FRAGMENT_TABLE, Config.ASSEMBLE_FRAGMENT_QUESTION_TABLE, courseName, "Yahoo");
        // 添加连接请求
        List<Request> requests = new ArrayList<>();
        for (Map<String, Object> question : questions) {
            Request request = new Request();
            String url = question.get("asker_url").toString();
            // 已经有提问者信息的问题不用爬取
            if (question.get("asker_reputation").toString().equals("")) {
                // 设置额外信息
                requests.add(request.setUrl(url).setExtras(question));
            }
        }

        YangKuanSpider.create(new YahooAskerProcessor())
                .addRequests(requests)
                .thread(Config.THREAD)
                .addPipeline(new SqlAskerPipeline())
//                .addPipeline(new ConsolePipeline())
                .runAsync();

    }

    public static void main(String[] args) {
        new YahooAskerProcessor().YahooCrawl("R-tree");
    }

}
