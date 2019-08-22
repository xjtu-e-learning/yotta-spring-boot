package com.xjtu.spider_topic.spiders.wikicn;

import com.xjtu.common.Config;
import com.xjtu.domain.domain.Domain;
import com.xjtu.topic.domain.Term;
import com.xjtu.topic.domain.LayerRelation;
import com.xjtu.utils.Log;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


/**  
 * 移植：爬取中文维基的领域术语
 * @author 张铎
 * @date 2019年7月
 */
public class TopicCrawler {

	/**
	 * 1.根据课程名，判断存不存在该课程，若不存在，则存储课程名
	 * @param domain 课程
	 * @return true 表示已经爬取
	 */
	public static void storeDomain(Domain domain) {
		List<Domain> list = new ArrayList<>();
		list.add(domain);
		if (!MysqlReadWriteDAO.judgeByClass(Config.DOMAIN_TABLE, domain.getDomainName())){
			MysqlReadWriteDAO.storeDomain(list);
		}
	}

	/**
	 * 2.根据课程名，判断该课程知识主题是否已经爬取，显示结果
	 * 否则，获取三层领域术语和知识主题（某门课程）
	 * @param domain 课程
	 * @throws Exception
	 */
	public static void storeTopic(Domain domain) throws Exception{
		/**
		 * 领域术语采集：爬虫爬取
		 * 将所有领域术语存储到damain_layer表格中
		 */
		String domainName = domain.getDomainName();

		/**
		 * 判断该课程领域术语是否已经爬取
		 */
		Boolean existLayer = MysqlReadWriteDAO.judgeByClass1(Config.TOPIC_TABLE, Config.DOMAIN_TABLE, domainName);
		if (!existLayer) {
			layerExtract(domainName);
		} else {
			Log.log(domain + "：该课程领域术语已经爬取");
		}

		/**
		 * 判断该主题是否已经爬取
		 */
		Boolean existTopic = MysqlReadWriteDAO.judgeByClass1(Config.TOPIC_TABLE, Config.DOMAIN_TABLE, domainName);
		if (!existTopic) {
			topicExtract(domainName);
		} else {
			Log.log(domain + "：该课程知识主题已经爬取");
		}
	}

	/**
	 * 爬虫爬取三层领域术语（某门所有课程）
	 * @param domainName 课程名
	 * @throws Exception
	 */
	public static void layerExtract(String domainName) throws Exception{
		
		/**
		 * 领域术语采集：单门课程
		 */
		/**
		 * 第一层领域术语
		 */
		//测试：String domain = "数据结构";
		String domain_url = "https://zh.wikipedia.org/wiki/Category:" + URLEncoder.encode(domainName ,"UTF-8");//课程维基根目录
		int firstLayer = 1;
		List<Term> topicFirst = TopicCrawlerDAO.topic(domain_url); // 得到第一层领域术语（不含子主题的那一部分）

		MysqlReadWriteDAO.storeDomainLayer(topicFirst, domainName, firstLayer); // 存储第一层领域术语（不含子主题）
		//MysqlReadWriteDAO.storeDomainLayerFuzhu(topicFirst, domainName, firstLayer, 0); // 存储第一层领域术语（不含子主题）
		// 构造一个主题作为没有子主题的一级主题的父主题
		//List<Term> terms = new ArrayList<>();
		//Term term = new Term(domainName + "介绍", domain_url);
		//terms.add(term);
		//MysqlReadWriteDAO.storeLayerRelation(domainName, 0, terms, 0, domainName); // 第一层主题与领域名构成上下位关系
		//MysqlReadWriteDAO.storeLayerRelation(term.getTermName(), 0, topicFirst, firstLayer, domainName); // 第一层主题与领域名构成上下位关系


		/**
		 * 第二层领域术语
		 */
		int secondLayer = 2;
		List<Term> layerSecond = TopicCrawlerDAO.layer(domain_url); // 获取第一层领域术语（含子主题的那一部分）
		//MysqlReadWriteDAO.storeDomainLayerFuzhu(layerSecond, domainName, firstLayer, 1); // 存储第一层领域术语（含子主题）
		//MysqlReadWriteDAO.storeLayerRelation(domainName, 0, layerSecond, firstLayer, domainName); // 第一层主题与领域名构成上下位关系

		List<Term> topicSecondAll = new ArrayList<Term>(); // 保存所有第二层的领域术语
		if(layerSecond.size() != 0){
			for(int i = 0; i < layerSecond.size(); i++) {
				Term layer = layerSecond.get(i);
				String url = layer.getTermUrl();
				List<Term> topicSecond = TopicCrawlerDAO.topic(url); // 得到第二层领域术语（不含子主题的那一部分）

				MysqlReadWriteDAO.storeDomainLayer(topicSecond, domainName, secondLayer); // 存储第二层领域术语（不含子主题）
				//MysqlReadWriteDAO.storeDomainLayerFuzhu(topicSecond, domainName, secondLayer, 0); // 存储第二层领域术语（不含子主题）
				//MysqlReadWriteDAO.storeLayerRelation(layer.getTermName(), firstLayer, topicSecond, secondLayer, domainName); // 存储领域术语的上下位关系

				topicSecondAll.addAll(topicSecond); // 合并所有第二层领域术语

				int thirdLayer = 3;
				List<Term> layerThird = TopicCrawlerDAO.layer(url); // 得到第三层领域术语（含子主题的那一部分）

				//MysqlReadWriteDAO.storeDomainLayerFuzhu(layerThird, domainName, secondLayer, 1); // 存储第二层领域术语（含子主题）
				//MysqlReadWriteDAO.storeLayerRelation(layer.getTermName(), firstLayer, layerThird, secondLayer, domainName); // 存储领域术语的上下位关系

				List<Term> topicThirdAll = new ArrayList<Term>(); // 保存所有第三层的领域术语
				if (layerThird.size() != 0) {
					for(int j = 0; j < layerThird.size(); j++){
						Term layer2 = layerThird.get(j);
						String url2 = layer2.getTermUrl();
						List<Term> topicThird = TopicCrawlerDAO.topic(url2); // 得到第三层领域术语（不含子主题）

						MysqlReadWriteDAO.storeDomainLayer(topicThird, domainName, thirdLayer); // 存储第三层领域术语（不含子主题）
						//MysqlReadWriteDAO.storeDomainLayerFuzhu(topicThird, domainName, thirdLayer, 0); // 存储第三层领域术语（不含子主题）
						//MysqlReadWriteDAO.storeLayerRelation(layer2.getTermName(), secondLayer, topicThird, thirdLayer, domainName); // 存储领域术语的上下位关系

						topicThirdAll.addAll(topicThird); // 合并所有第三层领域术语

						//List<Term> layerThird2 = TopicCrawlerDAO.layer(url); // 得到第二层领域术语（含子主题）
						//MysqlReadWriteDAO.storeDomainLayerFuzhu(layerThird, domainName, secondLayer, 1); // 存储第二层领域术语（含子主题）
						//MysqlReadWriteDAO.storeLayerRelation(layer.getTermName(), firstLayer, layerThird, secondLayer, domainName); // 存储领域术语的上下位关系
					}
				} else {
					Log.log("不存在第三层领域术语源链接....");
				}
			}
		}else{
			Log.log("不存在第二层领域术语源链接...");
		}
	}
	
	/**
	 * 利用算法抽取三层知识主题（某门课程所有领域术语中的主题）
	 * @throws Exception
	 */
	public static void topicExtract(String domainName) throws Exception{
		
		List<Term> topicFirst = MysqlReadWriteDAO.getDomainLayer(domainName, 1);
		List<Term> topicSecond = MysqlReadWriteDAO.getDomainLayer(domainName, 2);
		List<Term> topicThird = MysqlReadWriteDAO.getDomainLayer(domainName, 3);

//		List<Term> topicFirstFuzhu = MysqlReadWriteDAO.getDomainLayerFuzhu(domainName, 1, 0);
//		List<Term> topicSecondFuzhu = MysqlReadWriteDAO.getDomainLayerFuzhu(domainName, 2, 0);
//		List<Term> topicThirdFuzhu = MysqlReadWriteDAO.getDomainLayerFuzhu(domainName, 3, 0);
//
//		List<Term> topicFirstFuzhu2 = MysqlReadWriteDAO.getDomainLayerFuzhu(domainName, 1, 1);
//		List<Term> topicSecondFuzhu2 = MysqlReadWriteDAO.getDomainLayerFuzhu(domainName, 2, 1);
//		List<Term> topicThirdFuzhu2 = MysqlReadWriteDAO.getDomainLayerFuzhu(domainName, 3, 1);

		//List<LayerRelation> layerRelationList = MysqlReadWriteDAO.getDomainLayerRelation(domainName);

		/**
		 * 知识主题筛选：抽取算法获取知识主题
		 * 存储到 domain_topic表格中
		 */
		// 从 domain_layer 删除重复主题(含子主题)保存到 domain_topic

		List<Set<Term>> topicList = TopicCrawlerDAO.getTopic(topicFirst, topicSecond, topicThird);
		for(int i = 0; i < topicList.size(); i++){
			Set<Term> topic = topicList.get(i);
			int layer_ID = i + 1;
			MysqlReadWriteDAO.storeDomainTopic(topic, domainName, layer_ID); // 存储三层领域术语
		}


//		// 从 domain_layer_fuzhu 删除重复主题(不含子主题)保存到 domain_layer_fuzhu2
//		List<Set<Term>> topicListFuzhu = TopicCrawlerDAO.getTopic(topicFirstFuzhu, topicSecondFuzhu, topicThirdFuzhu);
//		for(int i = 0; i < topicListFuzhu.size(); i++){
//			Set<Term> topic = topicListFuzhu.get(i);
//			int layer_ID = i + 1;
//			MysqlReadWriteDAO.storeDomainTopicFuzhu(topic, domainName, layer_ID, 0);
//		}
//		// 从 domain_layer_fuzhu 删除重复主题(含子主题)保存到 domain_layer_fuzhu2
//		List<Set<Term>> topicListFuzhu2 = TopicCrawlerDAO.getTopic(topicFirstFuzhu2, topicSecondFuzhu2, topicThirdFuzhu2);
//		for(int i = 0; i < topicListFuzhu2.size(); i++){
//			Set<Term> topic = topicListFuzhu2.get(i);
//			int layer_ID = i + 1;
//			MysqlReadWriteDAO.storeDomainTopicFuzhu(topic, domainName, layer_ID, 1);
//		}
//		// 从 domain_layer_relation 删除重复主题关系保存到 domain_topic_relation
//		Set<LayerRelation> layerRelationSet = new LinkedHashSet<LayerRelation>(layerRelationList);
//		MysqlReadWriteDAO.storeDomainLayerRelation(layerRelationSet); // 存储 domain_layer_relation2
//		MysqlReadWriteDAO.storeDomainTopicRelation(layerRelationSet); // 存储 domain_topic_relation
	}

}
