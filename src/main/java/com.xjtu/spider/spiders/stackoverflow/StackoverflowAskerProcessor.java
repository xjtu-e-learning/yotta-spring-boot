package com.xjtu.spider.spiders.stackoverflow;

import com.xjtu.common.Config;
import com.xjtu.spider.spiders.webmagic.bean.FragmentContentAsker;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StackoverflowAskerProcessor implements PageProcessor {

    private Site site = Site.me()
            .setRetryTimes(Config.retryTimesSO)
            .setRetrySleepTime(Config.retrySleepTimeSO)
            .setSleepTime(Config.sleepTimeSO)
            .setTimeOut(Config.timeOutSO)
            .addHeader("User-Agent", Config.userAgent)
            .addHeader("Origin", Config.originSO)
            .addHeader("Hosts", Config.hostsSO)
            .addHeader("Accept", "*/*");

    @Override
    public Site getSite() {
        return site;
    }

    @Override
    public void process(Page page) {
        Html html = page.getHtml();

        // 获取提问者姓名、声望值、回答总数、提问总数、浏览总数、最佳答案率（雅虎问答有，SO没有）
        String asker_name = html.xpath("//h2[@class='user-card-name']/text()").get();
        String asker_reputation = html.xpath("//div[@class='reputation']/text()").get();
        String asker_answerCount = html.xpath("//div[@class='stat answers col-3']/span/text()").get();
        String asker_questionCount = html.xpath("//div[@class='stat questions col-3']/span/text()").get();
        List<String> asker_viewCounts = html.xpath("//div[@class='user-links']//li/allText()").all();
        String asker_viewCount = asker_viewCounts.get(asker_viewCounts.size() - 2);
        asker_reputation = ProcessFeature.processWebsiteNumbers(asker_reputation);
        asker_answerCount = ProcessFeature.processWebsiteNumbers(asker_answerCount);
        asker_questionCount = ProcessFeature.processWebsiteNumbers(asker_questionCount);
        asker_viewCount = ProcessFeature.processAskerView(asker_viewCount);
        System.out.println("提问者姓名: " + asker_name + ", 提问者声望值: " + asker_reputation + ", 提问者回答总数: " + asker_answerCount + ", 提问者提问总数: " + asker_questionCount + ", 提问者浏览总数: " + asker_viewCount);

        // 获取extras中的FragmentContentQuestion对象信息
        FragmentContentAsker fragmentContentAsker = new FragmentContentAsker();
        fragmentContentAsker.setAsker_name(asker_name);
        fragmentContentAsker.setAsker_reputation(asker_reputation);
        fragmentContentAsker.setAsker_answerCount(asker_answerCount);
        fragmentContentAsker.setAsker_questionCount(asker_questionCount);
        fragmentContentAsker.setAsker_viewCount(asker_viewCount);
        page.putField("fragmentContentAsker", fragmentContentAsker);

    }

    public void StackoverflowCrawl(String courseName) {
        // 获取问题信息
        ProcessorSQL processorSQL = new ProcessorSQL();
        List<Map<String, Object>> questions = processorSQL.getQuestions(
                Config.ASSEMBLE_FRAGMENT_TABLE, Config.ASSEMBLE_FRAGMENT_QUESTION_TABLE, courseName, "Stackoverflow");
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

        YangKuanSpider.create(new StackoverflowAskerProcessor())
                .addRequests(requests)
                .thread(Config.threadSO)
                .addPipeline(new SqlAskerPipeline())
//                .addPipeline(new ConsolePipeline())
                .runAsync();

    }

    public static void main(String[] args) {
        new StackoverflowAskerProcessor().StackoverflowCrawl("R-tree");
    }

}
