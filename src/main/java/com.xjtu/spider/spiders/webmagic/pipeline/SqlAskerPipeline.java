package com.xjtu.spider.spiders.webmagic.pipeline;


import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author yuanhao
 * @date 2018/4/10 16:30
 */
public class SqlAskerPipeline implements Pipeline {

    @Override
    public void process(ResultItems resultItems, Task task) {
//        System.out.println(resultItems.getAll().size());
        for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
            mysqlUtils mysql = new mysqlUtils();
            List<Object> params = new ArrayList<Object>();
            // 定义插入语句参数
            String addSql = "update " + Config.ASSEMBLE_FRAGMENT_QUESTION_TABLE +
                    " set asker_name = ?," +
                    " asker_reputation = ?," +
                    " asker_answerCount = ?," +
                    " asker_questionCount = ?," +
                    " asker_viewCount = ? " +
                    " where question_id = ?";

            // 问题信息
            FragmentContentAsker fragmentContentAsker = (FragmentContentAsker) entry.getValue();
            // 分面信息
            Map<String,Object> question = resultItems.getRequest().getExtras();

            // asker_name 提问者姓名
            params.add(fragmentContentAsker.getAsker_name());
            // asker_reputation 提问者声望值
            params.add(fragmentContentAsker.getAsker_reputation());
            // asker_answerCount 提问者回答总数
            params.add(fragmentContentAsker.getAsker_answerCount());
            // asker_questionCount 提问者问题总数
            params.add(fragmentContentAsker.getAsker_questionCount());
            // asker_viewCount 提问者浏览总数
            params.add(fragmentContentAsker.getAsker_viewCount());
            // 需要更新的问题id
            params.add(Integer.parseInt(question.get("question_id").toString()));

            try {
                mysql.addDeleteModify(addSql, params);
                System.out.println("assemble_fragment_question：update asker information success");
            }
            catch (SQLException exception){
                System.out.println("assemble_fragment_question：update asker information fail：" + exception.getMessage());
            } finally {
                mysql.closeconnection();
            }
        }

    }
}
