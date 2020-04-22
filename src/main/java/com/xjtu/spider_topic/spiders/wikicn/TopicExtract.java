package com.xjtu.spider_topic.spiders.wikicn;

import com.spreada.utils.chinese.ZHConverter;
import com.xjtu.spider_topic.service.TFSpiderService;
import com.xjtu.topic.domain.Term;
import com.xjtu.utils.Log;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**  
 * 解析中文维基
 * 1. 获得每一分类的子分类
 * 2. 获得每一分类的子页面
 *  
 * @author 张铎
 * @date 2019年7月
 */
public class TopicExtract {
	private static String domain_url0;
	private static String domain_url1 = "https://zh.wikipedia.org";
	private static String domain_url2 = "https://en.wikipedia.org";

	/**
	 * 根据课程的中英文状态确定爬虫爬取哪个网站
	 *
	 * @param
	 * @return
	 */
	public static void setLanguage() {
		if (TFSpiderService.getDomainFlag()) {domain_url0 = domain_url1;}
		else domain_url0 = domain_url2;
	}

	private static ZHConverter converter = ZHConverter.getInstance(ZHConverter.SIMPLIFIED);// 转化为简体中文

	/**
	 * 1.解析得到Category中的页面领域术语
	 * @param doc
	 * @return 
	 */
	public static List<Term> getTopic(Document doc){
		setLanguage();
		List<Term> termList = new ArrayList<>();
		Elements mwPages = doc.select("#mw-pages").select("li");
		int len = mwPages.size();       //页面领域术语的数目，可视为一级知识主题的数目（粗，未去重）；
		if(len != 0) {
			Log.log("该术语第一层候选主题共有" + len + "个");
			for (int i = 0; i < mwPages.size(); i++) {
				String url = domain_url0 + mwPages.get(i).select("a").attr("href");
				String topic = mwPages.get(i).text();
				topic = converter.convert(topic);
				Log.log("The " + (i + 1) + " topic is : " + topic + ", url is : " + url);
				Term term = new Term(topic, url);
				termList.add(term);
			}
		}else {
			Log.log("该术语内容在维基百科中不存在,无法自动构建,请尝试人工构建");
		}
		return termList;
	}
	
	/**
	 * 解析得到Category中的二级子分类页面
	 * @param doc
	 * @return 
	 */
	public static List<Term> getLayer(Document doc){
		setLanguage();
		List<Term> termList = new ArrayList<Term>();
		if(doc.select("#mw-subcategories").size()==0){
			Log.log("该术语没有子分类，停止爬取子分类");
		} else {
			Elements mwPages = doc.select("#mw-subcategories").select("li");
			int len = mwPages.size();
			Log.log("该术语下共有"+len+"个子分类");
			for (int i = 0; i < mwPages.size(); i++) {
				String url = domain_url0 + mwPages.get(i).select("a").attr("href");
				String layer = mwPages.get(i).select("a").text();
				layer = converter.convert(layer);
				Log.log("The "+(i+1)+" Layer is : " + layer + "  url is : " + url);
				Term term = new Term(layer, url);
				termList.add(term);
			}
		}
		return termList;
	}

}
