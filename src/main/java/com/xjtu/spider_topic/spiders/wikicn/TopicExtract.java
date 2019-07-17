package com.xjtu.spider_topic.spiders.wikicn;

import com.spreada.utils.chinese.ZHConverter;
import com.xjtu.topic.domain.Term;
import com.xjtu.utils.JsoupDao;
import com.xjtu.utils.Log;
import com.xjtu.utils.SpiderUtils;
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
	
	private static ZHConverter converter = ZHConverter.getInstance(ZHConverter.SIMPLIFIED);// 转化为简体中文

	public static void main(String[] args) throws Exception {
		String url = "https://zh.wikipedia.org/wiki/Category:%E6%95%B0%E6%8D%AE%E7%BB%93%E6%9E%84";
		String html = SpiderUtils.seleniumWikiCN(url);
		Document doc = JsoupDao.parseHtmlText(html);
		getTopic(doc);
//		getLayer(doc);
	}
	
	/**
	 * 解析得到Category中的页面术语
	 * @param doc
	 * @return 
	 */
	public static List<Term> getTopic(Document doc){
		List<Term> termList = new ArrayList<>();
		Elements mwPages = doc.select("#mw-pages").select("li");
		int len = mwPages.size();
		Log.log(len);
		for (int i = 0; i < mwPages.size(); i++) {
			String url = "https://zh.wikipedia.org" + mwPages.get(i).select("a").attr("href");
			String topic = mwPages.get(i).text();
			topic = converter.convert(topic);
//			Log.log("topic is : " + topic + "  url is : " + url);
			Term term = new Term(topic, url);
			termList.add(term);
		}
		return termList;
	}
	
	/**
	 * 解析得到Category中的二级子分类页面
	 * @param doc
	 * @return 
	 */
	public static List<Term> getLayer(Document doc){
		List<Term> termList = new ArrayList<Term>();
		if(doc.select("#mw-subcategories").size()==0){
			Log.log("没有下一层子分类...");
		} else {
			Elements mwPages = doc.select("#mw-subcategories").select("li");
			int len = mwPages.size();
			Log.log(len);
			for (int i = 0; i < mwPages.size(); i++) {
				String url = "https://zh.wikipedia.org" + mwPages.get(i).select("a").attr("href");
				String layer = mwPages.get(i).select("a").text();
				layer = converter.convert(layer);
//				Log.log("Layer is : " + layer + "  url is : " + url);
				Term term = new Term(layer, url);
				termList.add(term);
			}
		}
		return termList;
	}

}
