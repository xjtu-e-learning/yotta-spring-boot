package com.xjtu.spider.spiders.webmagic.pipeline;


import com.xjtu.spider.service.SpiderService;
import com.xjtu.spider.spiders.webmagic.bean.Asker;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.Map;

/**
 * @author yuanhao
 * @date 2018/4/10 16:30
 */
public class SqlAskerPipeline implements Pipeline {

    SpiderService spiderService;

    public SqlAskerPipeline(SpiderService spiderService) {
        this.spiderService = spiderService;
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
            // 问题信息
            Asker asker = (Asker) entry.getValue();
            // 分面信息
            Map<String, Object> question = resultItems.getRequest().getExtras();
            try {
                spiderService.updateQuestionByQuestionId(
                        asker.getAskerName(),
                        asker.getAskerReputation(),
                        asker.getAskerAnswerCount(),
                        asker.getAskerQuestionCount(),
                        asker.getAskerViewCount(),
                        Long.parseLong(question.get("questionId").toString())
                );
                System.out.println("update asker information success");
            } catch (Exception exception) {
                System.out.println("update asker information fail：\n" + exception.getMessage());
            }
        }

    }
}
