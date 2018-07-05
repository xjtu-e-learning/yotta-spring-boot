package com.xjtu.spider.spiders.stackoverflow;


import com.xjtu.common.Config;
import com.xjtu.spider.service.SpiderService;
import com.xjtu.spider.spiders.webmagic.bean.FragmentContentQuestion;
import com.xjtu.spider.spiders.webmagic.pipeline.SqlQuestionPipeline;
import com.xjtu.spider.spiders.webmagic.spider.YangKuanSpider;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StackoverflowQuestionProcessor implements PageProcessor {

    SpiderService spiderService;

    public StackoverflowQuestionProcessor(SpiderService spiderService) {
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

    // 判断是不是目录
    private String content_regex = ".+?search\\?q=.+";

    @Override
    public Site getSite() {
        return site;
    }

    @Override
    public void process(Page page) {
        Html html = page.getHtml();
        String domain = "https://stackoverflow.com";

        if (page.getUrl().regex(content_regex).match()) {
            /**
             * 主题页面
             */
            // 每一页的链接
            List<String> questions = html.xpath("//div[@class='result-link']//a/@href").all();
            for (int i = 0; i < questions.size(); i++) {
                System.out.println("问题链接：" + domain + questions.get(i));
            }
            // 下一页
            String next = html.xpath("//a[@rel='next']/@href").get();
            // 加入队列
            for (String str : questions) {
                Request request = new Request();
                request.setUrl(domain + str);
                request.setExtras(page.getRequest().getExtras());
                page.addTargetRequest(request);
            }
            Map<String, Object> extras = page.getRequest().getExtras();
            int currentPage = (int) extras.get("page");
            // 页面多于5个不再爬取
            if (currentPage < 1) {
                extras.put("page", currentPage + 1);
                Request request = new Request();
                request.setUrl(domain + next);
                request.setExtras(extras);
                page.addTargetRequest(request);
            }
        } else {
            /**
             * 问题页面
             */
            // 问题链接
            String question_url = page.getUrl().toString();
            System.out.println("问题链接：" + question_url);
            // 提问者链接
            String asker_url = domain + html.xpath("//div[@class='post-signature owner grid--cell fl0']//div[@class='user-details']//a/@href").all().get(0);
            // 获取问题分数、问题回答数、问题浏览数及其预处理
            String question_score = html.xpath("//div[@class='vote']/span[@class='vote-count-post ']/text()").get();
            String question_answerCount = html.xpath("//div[@id='answers-header']//h2/@data-answercount").get();
            String question_viewCount = html.xpath("//table[@id='qinfo']//p[@class='label-key']/allText()").all().get(3);
            question_score = ProcessFeature.processWebsiteNumbers(question_score);
            question_answerCount = ProcessFeature.processQuestionAnswerCount(question_answerCount);
            question_viewCount = ProcessFeature.processQuestionViewCount(question_viewCount);
            System.out.println("score: " + question_score + ", answer: " + question_answerCount + ", view: " + question_viewCount);
            // 获取问题标题、问题正文、问题最佳回答
            String title = html.xpath("//div[@id='question-header']/h1/a").get();
            String title_p = html.xpath("//div[@id='question-header']/h1/a/text()").get();
            // 第一个是问题描述 其余的是答案
            List<String> qas = html.xpath("//div[@class='post-text']").all();
            List<String> qas_p = html.xpath("//div[@class='post-text']/allText()").all();
            String question_body = "";
            String question_body_p = "";
            String question_best_answer = "";
            String question_best_answer_p = "";
            if (qas.size() > 0) {  // 存在正文
                question_body = qas.get(0);
                question_body_p = qas_p.get(0);
                if (qas.size() > 1) {  // 存在最佳回答
                    question_best_answer = qas.get(1);
                    question_best_answer_p = qas_p.get(1);
                }
            }
            List<String> assembleContents = new ArrayList<>();
            List<String> assembleTexts = new ArrayList<>();
            assembleContents.add(title + "\n" + question_body + "\n" + question_best_answer);
            assembleTexts.add(title_p + "\n" + question_body_p + "\n" + question_best_answer_p);

            // 获取extras中的FragmentContentQuestion对象信息
            FragmentContentQuestion fragmentContentQuestion = new FragmentContentQuestion();

            // 保存问题文本信息
            fragmentContentQuestion.setAssembleContents(assembleContents);
            fragmentContentQuestion.setAssembleTexts(assembleTexts);
            fragmentContentQuestion.setPage_search_url("https://stackoverflow.com/search?q=");
            fragmentContentQuestion.setPage_website_logo("fa fa-stack-overflow");
            fragmentContentQuestion.setQuestion_url(question_url);
            fragmentContentQuestion.setQuestion_title(title);
            fragmentContentQuestion.setQuestion_title_pure(title_p);
            fragmentContentQuestion.setQuestion_body(question_body);
            fragmentContentQuestion.setQuestion_body_pure(question_body_p);
            fragmentContentQuestion.setQuestion_best_answer(question_best_answer);
            fragmentContentQuestion.setQuestion_best_answer_pure(question_best_answer_p);
            fragmentContentQuestion.setQuestion_score(question_score);
            fragmentContentQuestion.setQuestion_answerCount(question_answerCount);
            fragmentContentQuestion.setQuestion_viewCount(question_viewCount);
            fragmentContentQuestion.setAsker_url(asker_url);
            page.putField("fragmentContentQuestion", fragmentContentQuestion);
        }
    }

    public void StackoverflowCrawl(String domainName) {
        //1.获取分面名
        List<Map<String, Object>> facets = spiderService.getFacets(domainName);
        if (facets == null || facets.size() == 0) {
            return;
        }
        //2.添加连接请求
        List<Request> requests = new ArrayList<>();
        for (Map<String, Object> facet : facets) {
            Request request = new Request();
            String url = "https://stackoverflow.com/search?q="
//                    + facetInformation.get("ClassName") + " "
                    + facet.get("topicName") + " "
                    + facet.get("facetName");
            //添加链接，设置额外信息
            facet.put("page", 1);
            facet.put("sourceName", "Stackoverflow");
            requests.add(request.setUrl(url).setExtras(facet));
        }

        YangKuanSpider.create(new StackoverflowQuestionProcessor(this.spiderService))
                .addRequests(requests)
                .thread(Config.threadSO)
                .addPipeline(new SqlQuestionPipeline(this.spiderService))
//                .addPipeline(new ConsolePipeline())
                .runAsync();

    }

    public static void main(String[] args) {
        String domainName = "test";
//        new YahooProcessor().YahooCrawl(domainName);
//        new YahooAskerProcessor().YahooCrawl(domainName);
//        new StackoverflowQuestionProcessor().StackoverflowCrawl(domainName);
    }

}
