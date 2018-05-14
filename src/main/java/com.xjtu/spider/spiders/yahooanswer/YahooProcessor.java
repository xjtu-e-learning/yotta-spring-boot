package com.xjtu.spider.spiders.yahooanswer;


import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class YahooProcessor implements PageProcessor {

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
            String question_url = page.getUrl().toString();
            System.out.println("问题链接：" + question_url);
            // 提问者链接
            String asker_url = domain + html.xpath("//div[@id='yq-question-detail-profile-img']/a/@href").all().get(0);
            // 获取问题分数、问题回答数、问题浏览数
            String question_score = html.xpath("//div[@class='qfollow Mend-10 Fz-13 Fw-n D-ib Cur-p']/span[@class='follow-text']/@data-ya-fc").get();
            String question_answerCount = html.xpath("//div[@class='Mend-10 Fz-13 Fw-n D-ib']/span/allText()").all().get(1);
            String question_viewCount = "";
            question_score = ProcessFeature.processWebsiteNumbers(question_score);
            question_answerCount = ProcessFeature.processQuestionAnswerCount(question_answerCount);
            System.out.println("score: " + question_score + ", answer: " + question_answerCount + ", view: " + question_viewCount);
            // 雅虎问答的标题
            String question_title_pure = html.xpath("//h1[@itemprop='name']/text()").get();
            String question_title = html.xpath("//h1[@itemprop='name']").get();
            // 问题描述
            String question_body_pure = html.xpath("//span[@class='D-n ya-q-full-text Ol-n']/text()").get();
            String question_body = html.xpath("//span[@class='D-n ya-q-full-text Ol-n']").get();
            if (question_body_pure == null || question_body == null) {
                question_body_pure = html.xpath("//span[@class='ya-q-text']/text()").get();
                question_body = html.xpath("//span[@class='ya-q-text']").get();
            }
            // 答案
            String question_best_answer = "";
            String question_best_answer_pure = "";
            List<String> answers_p = html.xpath("//span[@class='ya-q-full-text'][@itemprop]/text()").all();
            List<String> answers = html.xpath("//span[@class='ya-q-full-text'][@itemprop]").all();
            // 知识森林碎片
            List<String> fragments = new ArrayList<>();
            List<String> fragmentsPureText = new ArrayList<>();
            if (answers.size() > 0) {
                question_best_answer = answers.get(0);
                question_best_answer_pure = answers_p.get(0);
                fragments.add(question_title + "\n" + question_body + "\n" + question_best_answer);
                fragmentsPureText.add(question_title_pure + "\n" + question_body_pure + "\n" + question_best_answer_pure);
            } else {
                fragments.add(question_title + "\n" + question_body);
                fragmentsPureText.add(question_title_pure + "\n" + question_body_pure);
            }

            // 获取extras中的FragmentContentQuestion对象信息
            FragmentContentQuestion fragmentContentQuestion = new FragmentContentQuestion();

            // 保存问题文本信息
            fragmentContentQuestion.setFragments(fragments);
            fragmentContentQuestion.setFragmentsPureText(fragmentsPureText);
            fragmentContentQuestion.setPage_website_logo("fa fa-yahoo");
            fragmentContentQuestion.setPage_search_url("https://answers.search.yahoo.com/search?p=");
            fragmentContentQuestion.setQuestion_url(question_url);
            fragmentContentQuestion.setQuestion_title(question_title);
            fragmentContentQuestion.setQuestion_title_pure(question_title_pure);
            fragmentContentQuestion.setQuestion_body(question_body);
            fragmentContentQuestion.setQuestion_body_pure(question_body_pure);
            fragmentContentQuestion.setQuestion_best_answer(question_best_answer);
            fragmentContentQuestion.setQuestion_best_answer_pure(question_best_answer_pure);
            fragmentContentQuestion.setQuestion_score(question_score);
            fragmentContentQuestion.setQuestion_answerCount(question_answerCount);
            fragmentContentQuestion.setQuestion_viewCount(question_viewCount);
            fragmentContentQuestion.setAsker_url(asker_url);
            page.putField("fragmentContentQuestion", fragmentContentQuestion);

        }

    }

    public void YahooCrawl(String courseName) {
        //1.获取分面名
        ProcessorSQL processorSQL = new ProcessorSQL();
        List<Map<String, Object>> allFacetsInformation = processorSQL.getAllFacets(Config.FACET_TABLE, courseName);
        //2.添加连接请求
        List<Request> requests = new ArrayList<>();
        for (Map<String, Object> facetInformation : allFacetsInformation) {
            Request request = new Request();
            String url = "https://answers.search.yahoo.com/search?p="
//                    + facetInformation.get("ClassName") + " "
                    + facetInformation.get("TermName") + " "
                    + facetInformation.get("FacetName");
            //添加链接;设置额外信息
            facetInformation.put("SourceName", "Yahoo");
            facetInformation.put("page", 1);
            requests.add(request.setUrl(url).setExtras(facetInformation));
        }

        YangKuanSpider.create(new YahooProcessor())
                .addRequests(requests)
                .thread(Config.THREAD)
                .addPipeline(new SqlQuestionPipeline())
//                .addPipeline(new ConsolePipeline())
                .runAsync();

    }

    public static void main(String[] args) {
        new YahooProcessor().YahooCrawl("test");
    }

}
