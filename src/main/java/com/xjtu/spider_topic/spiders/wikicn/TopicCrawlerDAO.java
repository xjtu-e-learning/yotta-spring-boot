package com.xjtu.spider_topic.spiders.wikicn;

import com.xjtu.topic.domain.Term;
import com.xjtu.utils.JsoupDao;
import com.xjtu.utils.SpiderUtils;
import org.jsoup.nodes.Document;
import com.xjtu.utils.JsoupDao;
import com.xjtu.utils.Log;
import com.xjtu.utils.SpiderUtils;

import java.util.*;


/**  
 * 移植：爬取中文维基的领域术语
 *  
 * @author 张铎
 * @date 2019年7月
 */
public class TopicCrawlerDAO {
	
	/**
	 * 获取Category中的页面术语
	 * @param url 网页链接
	 * @return 术语集合
	 * @throws Exception
	 */
	public static List<Term> topic(String url) throws Exception{
		String html = SpiderUtils.seleniumWiki(url); // Selenium方式获取
		Document doc = JsoupDao.parseHtmlText(html);
		List<Term> termList = TopicExtract.getTopic(doc); // 解析没有子分类的术语
		return termList;
	}
	
	/**
	 * 获取Category中的子分类术语
	 * @param url 网页链接
	 * @return 术语集合
	 * @throws Exception
	 */
	public static List<Term> layer(String url) throws Exception{
		String html = SpiderUtils.seleniumWiki(url); // Selenium方式获取
		Document doc = JsoupDao.parseHtmlText(html);
		List<Term> termList = TopicExtract.getLayer(doc); // 解析有子分类的术语
		return termList;
	}
	
	/**
	 * 领域术语抽取：抽取知识主题
	 * 判断三层节点中存在的重复元素并进行处理，得到知识主题（测试程序）
	 * @param topicFirst
	 * @param topicSecond
	 * @param topicThird
	 */
	public static List<Set<Term>> getTopic(List<Term> topicFirst, List<Term> topicSecond, List<Term> topicThird){
		List<Set<Term>> topicList = new ArrayList<Set<Term>>();
		Set<Term> topicFirstFinal = new HashSet<Term>();
		Set<Term> topicSecondFinal = new HashSet<Term>();
		Set<Term> topicThirdFinal = new HashSet<Term>();
		/**
		 * 将List存为Set去除重复元素
		 */
		Set<Term> topicFirstSet = listToSet(topicFirst);
		Set<Term> topicSecondSet = listToSet(topicSecond);
		Set<Term> topicThirdSet = listToSet(topicThird);
		Log.log("topicFirst : " + topicFirst.size());
		Log.log("topicSecond : " + topicSecond.size());
		Log.log("topicThird : " + topicThird.size());
		Log.log("topicFirstSet : " + topicFirstSet.size());
		Log.log("topicSecondSet : " + topicSecondSet.size());
		Log.log("topicThirdSet : " + topicThirdSet.size());
		
		/**
		 * 第一层元素不在第二层和第三层中可以保存
		 */
		for(Term term : topicFirstSet){
			String termName = term.getTermName().trim();
			Boolean flag = false; // 标志位判断第一层领域术语是否在第三层中
			for (Term term2 : topicSecondSet) {
				if(termName.equals(term2.getTermName().trim())){
					flag = true;
				}
			}
			for (Term term3 : topicThirdSet) {
				if(termName.equals(term3.getTermName().trim())){
					flag = true;
				}
			}
			if(!flag){
				topicFirstFinal.add(term);
			}
		}
		
		/**
		 * 第二层元素不在第三层中可以保存
		 */
		for(Term term2 : topicSecondSet){
			String termName = term2.getTermName().trim();
			Boolean flag = true; // 标志位判断第二层领域术语是否在第三层中
			for (Term term3 : topicThirdSet) {
				if(termName.equals(term3.getTermName().trim())){
					flag = false;
				}
			}
			if(flag){
				topicSecondFinal.add(term2);
			}
		}
		
		/**
		 * 第三层元素的始终不变
		 */
		topicThirdFinal = topicThirdSet;
		
		/**
		 * 返回结果
		 */
		topicList.add(topicFirstFinal);
		topicList.add(topicSecondFinal);
		topicList.add(topicThirdFinal);
		return topicList;
	}

	/**
	 * 将List转为为无重复Term的Set
	 * @param topicList
	 * @return
	 */
	public static Set<Term> listToSet(List<Term> topicList){
		LinkedHashSet<String> nameSet = new LinkedHashSet<String>();
		LinkedHashSet<String> urlSet = new LinkedHashSet<String>();
		Set<Term> topicSet = new HashSet<Term>();
		for (int i = 0; i < topicList.size(); i++) {
			Term term = topicList.get(i);
			nameSet.add(term.getTermName());
			urlSet.add(term.getTermUrl());
		}
		List<String> nameList = new ArrayList<String>(nameSet);
		List<String> urlList = new ArrayList<String>(urlSet);
		for (int i = 0; i < nameList.size(); i++) {
			topicSet.add(new Term(nameList.get(i), urlList.get(i)));
		}
		return topicSet;
	}

}
