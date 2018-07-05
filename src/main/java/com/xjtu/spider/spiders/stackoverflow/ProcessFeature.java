package com.xjtu.spider.spiders.stackoverflow;

/**
 * 处理问题的特征
 *
 * @author yuanhao
 * @date 2018/4/22 11:53
 */
public class ProcessFeature {

    /**
     * 处理网站的数字：1,21或者空等
     *
     * @param question_score_str
     * @return
     */
    public static String processWebsiteNumbers(String question_score_str) {
        if (question_score_str == null || question_score_str.equalsIgnoreCase("")) {
            return question_score_str;
        }
        if (question_score_str.contains(",")) {
            question_score_str = question_score_str.replaceAll(",", "");
        }
        if (question_score_str != null) {
            question_score_str = question_score_str.trim();
        }
        return question_score_str;
    }

    /**
     * 处理回答数
     *
     * @param answers
     * @return
     */
    public static String processQuestionAnswerCount(String answers) {
        if (answers == null || answers.equalsIgnoreCase("")) {
            return answers;
        }
        if (answers.contains(" answers")) {
            answers = answers.substring(0, answers.indexOf(" answers"));
        } else if (answers.contains(" answer")) {
            answers = answers.substring(0, answers.indexOf(" answer"));
        }
        if (answers.contains(",")) {
            answers = answers.replaceAll(",", "");
        }
        if (answers != null) {
            answers = answers.trim();
        }
        return answers;
    }

    /**
     * 处理浏览数
     *
     * @param views
     * @return
     */
    public static String processQuestionViewCount(String views) {
        if (views == null || views.equalsIgnoreCase("")) {
            return views;
        }
        if (views.contains(" times")) {
            views = views.substring(0, views.indexOf(" times"));
        }
        if (views.contains(",")) {
            views = views.replaceAll(",", "");
        }
        if (views != null) {
            views = views.trim();
        }
        return views;
    }

    /**
     * 处理提问者个人主页浏览数
     *
     * @param views
     * @return
     */
    public static String processAskerView(String views) {
        if (views == null || views.equalsIgnoreCase("")) {
            return views;
        }
        if (views.contains(" profile views")) {
            views = views.substring(0, views.indexOf(" profile views"));
        } else if (views.contains(" answer")) {
            views = views.substring(0, views.indexOf(" profile view"));
        }
        if (views.contains(",")) {
            views = views.replaceAll(",", "");
        }
        if (views != null) {
            views = views.trim();
        }
        return views;
    }

}
