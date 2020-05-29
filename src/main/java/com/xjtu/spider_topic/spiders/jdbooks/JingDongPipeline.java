package com.xjtu.spider_topic.spiders.jdbooks;

import com.xjtu.spider_topic.service.TFSpiderService;
import com.xjtu.topic.domain.Topic;
import jdk.nashorn.internal.runtime.UnwarrantedOptimismException;
import org.apache.poi.ss.formula.functions.T;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.*;

/**
 * Created on 2020/5/25.
 *
 * @author Duo Zhang
 */
public class JingDongPipeline implements Pipeline {
    TFSpiderService tfSpiderService;

    public JingDongPipeline(){}

    public JingDongPipeline(TFSpiderService tfSpiderService){
        this.tfSpiderService = tfSpiderService;
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        List<Topic> topics = new ArrayList<>();
        for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
            if(entry.getKey() == "书籍id集合") {
                List<String>  bookid = (List<String>) entry.getValue();
                for(String id:bookid){
                    int n = Integer.parseInt(id);
                }
            }else{
                String bookContent = entry.getValue().toString();
                List<Topic> topicsTemp = generateTopics(bookContent);
                for(Topic topic : topicsTemp){
                    if(topic != null && "".equals(topic.toString())){
                        if(!topics.contains(topic)){
                            topics.add(topic);
                        }
                    }
                }
            }
        }
        //tfSpiderService.saveTopics(topics);
    }

    public static List<Topic> generateTopics(String bookContentRaw){
        List<Topic> topics = new ArrayList<>();
        String bookContent = bookContentRaw.replaceAll("\n|出版者的话|译者序|前言|致谢|作者简介|第\\d+版"," ");
        bookContent = bookContent.replaceAll("．",".");
        System.out.println(bookContent);
        String[] topic_set = null;
        
        Topic topic = new Topic();
        return topics;
    }
}
