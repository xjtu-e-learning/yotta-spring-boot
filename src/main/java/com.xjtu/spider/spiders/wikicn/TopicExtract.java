package com.xjtu.spider.spiders.wikicn;

import com.spreada.utils.chinese.ZHConverter;
import com.xjtu.topic.domain.Topic;
import com.xjtu.utils.JsonUtil;
import com.xjtu.utils.SpiderUtils;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionOperations;


import java.util.ArrayList;
import java.util.List;

/**  
 * 解析中文维基
 * 1. 获得每一分类的子分类
 * 2. 获得每一分类的子页面
 *  
 * @author 郑元浩 
 * @date 2016年11月26日
 */
public class TopicExtract {

	private static final Logger logger = LoggerFactory.getLogger(TopicExtract.class);
	private static ZHConverter converter = ZHConverter.getInstance(ZHConverter.SIMPLIFIED);// 转化为简体中文

	public static void main(String[] args) throws Exception {
		String url = "https://zh.wikipedia.org/wiki/Category:%E6%95%B0%E6%8D%AE%E7%BB%93%E6%9E%84";
		String html = SpiderUtils.seleniumWikiCN(url);
		Document doc = JsonUtil.parseHtmlText(html);
		getTopic(doc);
//		getLayer(doc);
	}
	
	/**
	 * 解析得到Category中的页面主题
	 * @param doc
	 * @return 
	 */
	public static List<Topic> getTopic(Document doc){
		List<Topic> topics = new ArrayList<>();
		Elements mwPages = doc.select("#mw-pages").select("li");
		int len = mwPages.size();
		logger.info(""+len);
		for (int i = 0; i < mwPages.size(); i++) {
			String topicUrl = "https://zh.wikipedia.org" + mwPages.get(i).select("a").attr("href");
			String topicName = mwPages.get(i).text();
			topicName = converter.convert(topicName);
			Topic topic = new Topic();
			topic.setTopicName(topicName);
			topic.setTopicUrl(topicUrl);
			topics.add(topic);
		}
		return topics;
	}
	
	/**
	 * 解析得到Category中的二级子分类页面
	 * @param doc
	 * @return 
	 */
	public static List<Topic> getLayer(Document doc){
		List<Topic> topics = new ArrayList<Topic>();
		if(doc.select("#mw-subcategories").size()==0){
			logger.error("没有下一层子分类...");
		} else {
			Elements mwPages = doc.select("#mw-subcategories").select("li");
			int len = mwPages.size();
			logger.info(""+len);
			for (int i = 0; i < mwPages.size(); i++) {
				String topicUrl = "https://zh.wikipedia.org" + mwPages.get(i).select("a").attr("href");
				String topicName = mwPages.get(i).select("a").text();
				topicName = converter.convert(topicName);
				Topic topic = new Topic();
				topic.setTopicName(topicName);
				topic.setTopicUrl(topicUrl);
				topics.add(topic);
			}
		}
		return topics;
	}

}
