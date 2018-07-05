package com.xjtu.spider.spiders.webmagic.pipeline;


import com.xjtu.assemble.domain.Assemble;
import com.xjtu.assemble.service.AssembleService;
import com.xjtu.question.domain.Question;
import com.xjtu.spider.service.SpiderService;
import com.xjtu.spider.spiders.webmagic.bean.FragmentContentQuestion;
import org.springframework.beans.factory.annotation.Autowired;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * @author yuanhao
 * @date 2018/4/10 16:30
 */
public class SqlQuestionPipeline implements Pipeline {

    SpiderService spiderService;

    public SqlQuestionPipeline(SpiderService spiderService) {
        this.spiderService = spiderService;
    }

    @Autowired
    AssembleService assembleService;

    @Override
    public void process(ResultItems resultItems, Task task) {
        for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {

            // 问题信息
            FragmentContentQuestion fragmentContentQuestion = (FragmentContentQuestion) entry.getValue();
            // 分面信息
            Map<String, Object> facetMap = resultItems.getRequest().getExtras();
            facetMap = spiderService.getFacet(facetMap);
            // 获取在assemble自增的主键
            Long assembleId = spiderService.findMaxAssembleId();

            // 碎片爬取时间
            //设置日期格式
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // new Date()为获取当前系统时间，也可使用当前时间戳
            String date = df.format(new Date());

            Assemble assemble = new Assemble();
            assemble.setAssembleId(assembleId);
            assemble.setAssembleContent(fragmentContentQuestion.getAssembleContents().get(0));
            assemble.setAssembleText(fragmentContentQuestion.getAssembleTexts().get(0));
            assemble.setAssembleScratchTime(date);
            assemble.setFacetId((Long) facetMap.get("facetId"));
            assemble.setSourceId((Long) facetMap.get("sourceId"));
            /**
             * 插入assemble表格
             */
            assembleService.insertAssemble(assemble);

            /**
             * 插入question表格
             */
            Question question = new Question();
            question.setPageWebsiteLogo(fragmentContentQuestion.getPage_website_logo());
            question.setPageSearchUrl(fragmentContentQuestion.getPage_search_url());
            question.setPageColumnColor(fragmentContentQuestion.getPage_column_color());
            question.setQuestionUrl(fragmentContentQuestion.getQuestion_url());
            question.setQuestionTitle(fragmentContentQuestion.getQuestion_title());
            question.setQuestionTitlePure(fragmentContentQuestion.getQuestion_title_pure());
            question.setQuestionBody(fragmentContentQuestion.getQuestion_body());
            question.setQuestionBodyPure(fragmentContentQuestion.getQuestion_body_pure());
            question.setQuestionBestAnswer(fragmentContentQuestion.getQuestion_best_answer());
            question.setQuestionBestAnswerPure(fragmentContentQuestion.getQuestion_best_answer_pure());
            question.setQuestionScore(fragmentContentQuestion.getQuestion_score());
            question.setQuestionAnswerCount(fragmentContentQuestion.getQuestion_answerCount());
            question.setQuestionViewCount(fragmentContentQuestion.getQuestion_viewCount());
            question.setAskerUrl(fragmentContentQuestion.getAsker_url());
            question.setAssembleId(assembleId);
            spiderService.insertQuestion(question);
        }

    }
}
