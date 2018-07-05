package com.xjtu.spider.spiders.stackoverflow;

import com.xjtu.common.Config;
import com.xjtu.question.domain.Question;
import com.xjtu.spider.service.SpiderService;
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
 * @author yangkuan
 */
public class StackoverflowAskerProcessor implements PageProcessor {


    SpiderService spiderService;

    public StackoverflowAskerProcessor(SpiderService spiderService) {
        this.spiderService = spiderService;
    }

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
        String askerName = html.xpath("//h2[@class='user-card-name']/text()").get();
        String askerReputation = html.xpath("//div[@class='reputation']/text()").get();
        String askerAnswerCount = html.xpath("//div[@class='stat answers col-3']/span/text()").get();
        String askerQuestionCount = html.xpath("//div[@class='stat questions col-3']/span/text()").get();
        List<String> askerViewCounts = html.xpath("//div[@class='user-links']//li/allText()").all();
        String askerViewCount = askerViewCounts.get(askerViewCounts.size() - 2);
        askerReputation = ProcessFeature.processWebsiteNumbers(askerReputation);
        askerAnswerCount = ProcessFeature.processWebsiteNumbers(askerAnswerCount);
        askerQuestionCount = ProcessFeature.processWebsiteNumbers(askerQuestionCount);
        askerViewCount = ProcessFeature.processAskerView(askerViewCount);
        System.out.println("提问者姓名: " + askerName +
                ", 提问者声望值: " + askerReputation +
                ", 提问者回答总数: " + askerAnswerCount +
                ", 提问者提问总数: " + askerQuestionCount +
                ", 提问者浏览总数: " + askerViewCount);

        // 获取extras中的FragmentContentQuestion对象信息
        Asker asker = new Asker();
        asker.setAskerName(askerName);
        asker.setAskerReputation(askerReputation);
        asker.setAskerAnswerCount(askerAnswerCount);
        asker.setAskerQuestionCount(askerQuestionCount);
        asker.setAskerViewCount(askerViewCount);
        page.putField("asker", asker);

    }

    public void StackoverflowCrawl(String domainName) {
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

        YangKuanSpider.create(new StackoverflowAskerProcessor(this.spiderService))
                .addRequests(requests)
                .thread(Config.threadSO)
                .addPipeline(new SqlAskerPipeline(this.spiderService))
//                .addPipeline(new ConsolePipeline())
                .runAsync();

    }

    public static void main(String[] args) {

    }

}
