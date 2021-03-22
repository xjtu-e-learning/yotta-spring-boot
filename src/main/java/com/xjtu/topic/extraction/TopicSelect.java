package com.xjtu.topic.extraction;

import com.xjtu.assemble.domain.Assemble;
import com.xjtu.assemble.repository.AssembleRepository;
import com.xjtu.topic.domain.Topic;
import com.xjtu.topic.domain.TopicContainAssembleText;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Description
 * 输入：该领域课程的原始主题列表
 * 输出：是筛选后的主题列表
 */
public class TopicSelect {
    @Autowired
    private AssembleRepository assembleRepository;

    public TopicSelect() {
        System.out.println("Start Topic Extraction Mission.");
    }

    public List<Topic> filterAlgorithm(List<Topic> topicList) {
        List<Topic> results = new ArrayList<>();
        FilterUtils filterUtils = new FilterUtils();
        filterUtils.setHashSet();
        HashSet<String> hashSet = filterUtils.getHashSet();
        for (Topic topic : topicList) {
            String name = topic.getTopicName();
            if (name.length() <= 10 && name.length() >= 2) {
                boolean filter = false;
                for (String element : hashSet) {
                    filter = !name.contains(element);
                    if (!filter) break;
                }
                // filter = !name.contains("Template:") && !name.contains("/") && !name.contains(" ")
                //         && !name.contains("（") && !name.contains(".") && !name.contains(":")
                //         && !name.contains("：") && !name.contains("·") && !name.contains("_")
                //         && !name.contains("V") && !name.contains("!") && !name.contains("~")
                //         && !name.contains(",") && !name.contains("，") && !name.contains("#")
                //         && !name.contains("^") && !name.contains("$") && !name.contains("@")
                //         && !name.contains("&") && !name.contains("%") && !name.contains(";")
                //         && !name.contains("?") && !name.contains("、") && !name.contains("[")
                //         && !name.contains("*") && !name.contains("+") && !name.contains("(")
                //         && !name.contains("！") && !name.contains("I") && !name.contains("！");
                // boolean computerNameFilter;
                // computerNameFilter = filter && !name.contains("Microsoft") && !name.contains("AX") && !name.contains(".NET")
                //         && !name.contains("SIG") && !name.contains("ASCII") && !name.contains("User") && !name.contains("IBM")
                //         && !name.contains("Intel") && !name.contains("CAD") && !name.contains("IBM") && !name.contains("IBM")
                //         && !name.contains("Amazon") && !name.contains("Berkeley") && !name.contains("Adobe") && !name.contains("Alpha")
                //         && !name.contains("AMD") && !name.contains("Apache") && !name.contains("Mark") && !name.contains("XML")
                //         && !name.contains("Swift") && !name.contains("AJAX\n") && !name.contains("Wiki") && !name.contains("Google")
                //         && !name.contains("Atom") && !name.contains("Eclipse") && !name.contains("Portal") && !name.contains("AWT")
                //         && !name.contains("升阳") && !name.contains("章") && !name.contains("GUI") && !name.contains("网络")
                //         && !name.contains("Qt") && !name.contains("国家") && !name.contains("游戏") && !name.contains("引擎")
                //         && !name.contains("3D") && !name.contains("异次元") && !name.contains("国际") && !name.contains("互联网")
                //         && !name.contains("Oracle") && !name.contains("商业") && !name.contains("基金") && !name.contains("雅虎")
                //         && !name.contains("艺术") && !name.contains("资料库") && !name.contains("成人") && !name.contains("世界")
                //         && !name.contains("生命") && !name.contains("Power") && !name.contains("SPSS") && !name.contains("化学")
                //         && !name.contains("以利亚") && !name.contains("Web") && !name.contains("SHA") && !name.contains("与")
                //         && !name.contains("zip") && !name.contains("MD") && !name.contains("叛变") && !name.contains("智慧")
                //         && !name.contains("OS") && !name.contains("性爱") && !name.contains("实验室") && !name.contains("电脑")
                //         && !name.contains("驾驶") && !name.contains("的") && !name.contains("机器人") && !name.contains("棋")
                //         && !name.contains("妖精") && !name.contains("刺客") && !name.contains("ABBYY") && !name.contains("怎样")
                //         && !name.contains("政治") && !name.contains("华硕") && !name.contains("英雄") && !name.contains("中华")
                //         && !name.contains("电影") && !name.contains("Online") && !name.contains("王国") && !name.contains("台北")
                //         && !name.contains("浏览器") && !name.contains("公安") && !name.contains("历史") && !name.contains("性幻想")
                //         && !name.contains("黑客") && !name.contains("审查") && !name.contains("魔幻") && !name.contains("富翁")
                //         && !name.contains("大赛") && !name.contains("计划") && !name.contains("系列") && !name.contains("联合国")
                //         && !name.contains("愤怒") && !name.contains("年会") && !name.contains("微软") && !name.contains("杀")
                //         && !name.contains("卡内基") && !name.contains("虚拟") && !name.contains("魅族") && !name.contains("Windows")
                //         && !name.contains("CSS") && !name.contains("飞行") && !name.contains("Wii") && !name.contains("魅蓝")
                //         && !name.contains("引擎") && !name.contains("基金会") && !name.contains("直播") && !name.contains("广告")
                //         && !name.contains("输入法") && !name.contains("微博") && !name.contains("联盟") && !name.contains("山寨")
                //         && !name.contains("争霸") && !name.contains("电视") && !name.contains("性交") && !name.contains("数码");
                if (filter) {
                    results.add(topic);
                }
            }
        }

        //获得topicContainAssembleText List，即每个主题对应的碎片文本，获得主题内容信息
        // List<TopicContainAssembleText> topicContainAssembleTexts = new ArrayList<>();
        // for (int i = 0; i < results.size(); i++) {
        //     Topic tmpTopic = results.get(i);
        //     TopicContainAssembleText temp_topicContentAssembleText = new TopicContainAssembleText(tmpTopic);
        //     temp_topicContentAssembleText.setTopicId(tmpTopic.getTopicId());
        //
        //     //查询主题碎片信息
        //     List<Assemble> assembleList = assembleRepository.findAllAssemblesByTopicId(tmpTopic.getTopicId());
        //     if (assembleList.size() < 1) {
        //         System.out.print("该主题没有碎片知识！" + tmpTopic.getTopicId());
        //         continue;
        //     }
        //     String text = "";
        //     for (int j = 0; j < assembleList.size(); j++) {
        //         text = text + assembleList.get(j).getAssembleText() + " ";
        //     }
        //     temp_topicContentAssembleText.setText(text);
        //
        //     topicContainAssembleTexts.add(temp_topicContentAssembleText);
        // }

        return results;
    }
}
