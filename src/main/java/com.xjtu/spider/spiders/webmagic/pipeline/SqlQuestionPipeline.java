package com.xjtu.spider.spiders.webmagic.pipeline;


import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author yuanhao
 * @date 2018/4/10 16:30
 */
public class SqlQuestionPipeline implements Pipeline {

    @Override
    public void process(ResultItems resultItems, Task task) {
//        System.out.println(resultItems.getAll().size());
        for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {

            // 问题信息
            FragmentContentQuestion fragmentContentQuestion = (FragmentContentQuestion) entry.getValue();
            // 分面信息
            Map<String,Object> facetTableMap = resultItems.getRequest().getExtras();
            // 获取在assemble_fragment自增的主键
            int fragmentId = 0;

            /**
             * 插入assemble_fragment表格
             */
            mysqlUtils mysqlFra = new mysqlUtils();
            List<Object> paramsFra = new ArrayList<Object>();
            // 定义插入语句参数
            String addSqlFra = "insert into " + Config.ASSEMBLE_FRAGMENT_TABLE
                    + "(FragmentContent, Text, FragmentScratchTime, " +
                    "TermID, TermName, FacetName, " +
                    "FacetLayer, ClassName, SourceName) " +
                    "values (?,?,?,?,?,?,?,?,?)";
            // FragmentContent 碎片内容（带html标签）
            paramsFra.add(fragmentContentQuestion.getFragments().get(0));
            // Text 碎片内容（纯文本）
            paramsFra.add(fragmentContentQuestion.getFragmentsPureText().get(0));
            // FragmentScratchTime 碎片爬取时间
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            String date = df.format(new Date());// new Date()为获取当前系统时间，也可使用当前时间戳
            paramsFra.add(date);
            // TermID 主题ID
            paramsFra.add(facetTableMap.get("TermID"));
            // TermName 主题名
            paramsFra.add(facetTableMap.get("TermName"));
            // FacetName 分面名
            paramsFra.add(facetTableMap.get("FacetName"));
            // FacetLayer 分面层
            paramsFra.add(facetTableMap.get("FacetLayer"));
            // ClassName 课程名
            paramsFra.add(facetTableMap.get("ClassName"));
            // Source 数据源
            paramsFra.add(facetTableMap.get("SourceName"));

            try {
                fragmentId = mysqlFra.addGeneratedKey(addSqlFra, paramsFra);
                System.out.println("assemble_fragment：insert question information success, fragmentID is: " + fragmentId);
            }
            catch (SQLException exception){
                System.out.println("assemble_fragment：insert question information fail：" + exception.getMessage());
            } finally {
                mysqlFra.closeconnection();
            }

            /**
             * 插入assemble_fragment_question表格
             */
            mysqlUtils mysql = new mysqlUtils();
            List<Object> params = new ArrayList<Object>();
            // 定义插入语句参数
            String addSql = "insert into " + Config.ASSEMBLE_FRAGMENT_QUESTION_TABLE
                    + "(page_website_logo, page_search_url, page_column_color, " +
                    "question_url, question_title, question_title_pure, " +
                    "question_body, question_body_pure, " +
                    "question_best_answer, question_best_answer_pure, " +
                    "question_score, question_answerCount, question_viewCount, " +
                    "asker_url, fragment_id) " +
                    "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            // page_website_logo 问题网站logo
            // SO为 fa fa-stack-overflow, Yahoo为 fa fa-yahoo
            params.add(fragmentContentQuestion.getPage_website_logo());
            // page_search_url 问题网站搜索链接
            // SO为 https://stackoverflow.com/search?q=, Yahoo为 https://answers.search.yahoo.com/search?p=
            params.add(fragmentContentQuestion.getPage_search_url());
            // page_column_color 问题网站高质量显示颜色
            params.add(fragmentContentQuestion.getPage_column_color());

            // question_url 问题链接
            params.add(fragmentContentQuestion.getQuestion_url());
            // question_title 问题标题（带html标签）
            params.add(fragmentContentQuestion.getQuestion_title());
            // question_title_pure 问题标题（纯文本）
            params.add(fragmentContentQuestion.getQuestion_title_pure());
            // question_body 问题正文（带html标签）
            params.add(fragmentContentQuestion.getQuestion_body());
            // question_body_pure 问题正文（纯文本）
            params.add(fragmentContentQuestion.getQuestion_body_pure());
            // question_best_answer 问题最佳答案（带html标签）
            params.add(fragmentContentQuestion.getQuestion_best_answer());
            // question_best_answer_pure 问题最佳答案（纯文本）
            params.add(fragmentContentQuestion.getQuestion_best_answer_pure());

            // question_score 问题分数
            params.add(fragmentContentQuestion.getQuestion_score());
            // question_answerCount 问题回答数
            params.add(fragmentContentQuestion.getQuestion_answerCount());
            // question_viewCount 问题浏览数
            params.add(fragmentContentQuestion.getQuestion_viewCount());
            // asker_url 提问者个人主页链接
            params.add(fragmentContentQuestion.getAsker_url());

            // fragmentId 外键，对应在assemble_fragment中的主键
            params.add(fragmentId);

            try {
                mysql.addDeleteModify(addSql, params);
                System.out.println("assemble_fragment_question：insert question information success");
            }
            catch (SQLException exception){
                System.out.println("assemble_fragment_question：insert question information fail：" + exception.getMessage());
            } finally {
                mysql.closeconnection();
            }



        }

    }
}
