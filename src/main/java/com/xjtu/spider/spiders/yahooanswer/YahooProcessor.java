package com.xjtu.spider.spiders.yahooanswer;


import com.xjtu.common.Config;
import com.xjtu.spider.service.SpiderService;
import com.xjtu.spider.spiders.stackoverflow.ProcessFeature;
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

/**
 * 雅虎
 *
 * @author yangkuan
 */
public class YahooProcessor implements PageProcessor {

    SpiderService spiderService;

    public YahooProcessor(SpiderService spiderService) {
        this.spiderService = spiderService;
    }

    private Site site = Site.me()
            .setRetryTimes(Config.retryTimes)
            .setRetrySleepTime(Config.retrySleepTime)
            .setSleepTime(Config.sleepTime)
            .setTimeOut(Config.timeOut)
            .addHeader("User-Agent", Config.userAgent)
            .addHeader("Accept", "*/*");

    private String content_regex = ".+?search\\?p=.+";

    @Override
    public Site getSite() {
        return site;
    }

    @Override
    public void process(Page page) {
        Html html = page.getHtml();

        String domain = "https://answers.yahoo.com";

        if (page.getUrl().regex(content_regex).match()) {
            /**
             * 主题页面
             */
            List<String> qlinks = html.xpath("//a[@class=' lh-17 fz-m']/@href").all();
            String nextPage = html.xpath("//a[@class='next']/@href").get();

            for (int i = 0; i < qlinks.size(); i++) {
                System.out.println("问题链接：" + qlinks.get(i));
            }

            // 内容链接
            for (String str : qlinks) {
                Request request = new Request();
                request.setUrl(str);
                request.setExtras(page.getRequest().getExtras());
                page.addTargetRequest(request);
            }

            // 下一页链接
            // 页面多于5个不再爬取
            Map<String, Object> extras = page.getRequest().getExtras();
            int currentPage = (int) extras.get("page");
            if (currentPage < 1) {
                extras.put("page", currentPage + 1);
                Request request = new Request();
                request.setUrl(nextPage);
                request.setExtras(extras);
                page.addTargetRequest(request);
            }
        } else {
            /**
             * 问题页面
             */
            // 问题链接
            String questionUrl = page.getUrl().toString();
            System.out.println("问题链接：" + questionUrl);
            // 提问者链接
            String askerUrl = domain + html.xpath("//div[@id='yq-question-detail-profile-img']/a/@href").all().get(0);
            // 获取问题分数、问题回答数、问题浏览数
            String questionScore = html.xpath("//div[@class='qfollow Mend-10 Fz-13 Fw-n D-ib Cur-p']/span[@class='follow-text']/@data-ya-fc").get();
            String questionAnswerCount = html.xpath("//div[@class='Mend-10 Fz-13 Fw-n D-ib']/span/allText()").all().get(1);
            String questionViewCount = "";
            questionScore = ProcessFeature.processWebsiteNumbers(questionScore);
            questionAnswerCount = ProcessFeature.processQuestionAnswerCount(questionAnswerCount);
            System.out.println("score: " + questionScore +
                    ", answer: " + questionAnswerCount +
                    ", view: " + questionViewCount);
            // 雅虎问答的标题
            String questionTitlePure = html.xpath("//h1[@itemprop='name']/text()").get();
            String questionTitle = html.xpath("//h1[@itemprop='name']").get();
            // 问题描述
            String questionBodyPure = html.xpath("//span[@class='D-n ya-q-full-text Ol-n']/text()").get();
            String questionBody = html.xpath("//span[@class='D-n ya-q-full-text Ol-n']").get();
            if (questionBodyPure == null || questionBody == null) {
                questionBodyPure = html.xpath("//span[@class='ya-q-text']/text()").get();
                questionBody = html.xpath("//span[@class='ya-q-text']").get();
            }
            // 答案
            String questionBestAnswer = "";
            String questionBestAnswerPure = "";
            List<String> answersP = html.xpath("//span[@class='ya-q-full-text'][@itemprop]/text()").all();
            List<String> answers = html.xpath("//span[@class='ya-q-full-text'][@itemprop]").all();
            // 知识森林碎片
            List<String> fragments = new ArrayList<>();
            List<String> fragmentsPureText = new ArrayList<>();
            if (answers.size() > 0) {
                questionBestAnswer = answers.get(0);
                questionBestAnswerPure = answersP.get(0);
                fragments.add(questionTitle + "\n" + questionBody + "\n" + questionBestAnswer);
                fragmentsPureText.add(questionTitlePure + "\n" + questionBodyPure + "\n" + questionBestAnswerPure);
            } else {
                fragments.add(questionTitle + "\n" + questionBody);
                fragmentsPureText.add(questionTitlePure + "\n" + questionBodyPure);
            }

            // 获取extras中的FragmentContentQuestion对象信息
            FragmentContentQuestion fragmentContentQuestion = new FragmentContentQuestion();

            // 保存问题文本信息
            fragmentContentQuestion.setAssembleContents(fragments);
            fragmentContentQuestion.setAssembleTexts(fragmentsPureText);
            fragmentContentQuestion.setPage_website_logo("fa fa-yahoo");
            fragmentContentQuestion.setPage_search_url("https://answers.search.yahoo.com/search?p=");
            fragmentContentQuestion.setQuestion_url(questionUrl);
            fragmentContentQuestion.setQuestion_title(questionTitle);
            fragmentContentQuestion.setQuestion_title_pure(questionTitlePure);
            fragmentContentQuestion.setQuestion_body(questionBody);
            fragmentContentQuestion.setQuestion_body_pure(questionBodyPure);
            fragmentContentQuestion.setQuestion_best_answer(questionBestAnswer);
            fragmentContentQuestion.setQuestion_best_answer_pure(questionBestAnswerPure);
            fragmentContentQuestion.setQuestion_score(questionScore);
            fragmentContentQuestion.setQuestion_answerCount(questionAnswerCount);
            fragmentContentQuestion.setQuestion_viewCount(questionViewCount);
            fragmentContentQuestion.setAsker_url(askerUrl);
            page.putField("fragmentContentQuestion", fragmentContentQuestion);

        }

    }

    public void YahooCrawl(String domainName) {
        //1.获取分面名
        List<Map<String, Object>> facets = spiderService.getFacets(domainName);
        if (facets == null || facets.size() == 0) {
            return;
        }
        //2.添加连接请求
        List<Request> requests = new ArrayList<>();
        for (Map<String, Object> facet : facets) {
            Request request = new Request();
            String url = "https://answers.search.yahoo.com/search?p="
//                    + facetInformation.get("ClassName") + " "
                    + facet.get("TermName") + " "
                    + facet.get("FacetName");
            //添加链接;设置额外信息
            facet.put("SourceName", "Yahoo");
            facet.put("page", 1);
            requests.add(request.setUrl(url).setExtras(facet));
        }

        YangKuanSpider.create(new YahooProcessor(this.spiderService))
                .addRequests(requests)
                .thread(Config.THREAD)
                .addPipeline(new SqlQuestionPipeline(this.spiderService))
//                .addPipeline(new ConsolePipeline())
                .runAsync();

    }

    public static void main(String[] args) {


    }

}
