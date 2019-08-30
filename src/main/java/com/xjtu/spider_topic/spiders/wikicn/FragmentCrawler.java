package com.xjtu.spider_topic.spiders.wikicn;

import com.xjtu.common.Config;
import com.xjtu.domain.domain.Domain;
import com.xjtu.facet.domain.FacetRelation;
import com.xjtu.facet.domain.FacetSimple;
import com.xjtu.topic.domain.Term;
import com.xjtu.topic.domain.Topic;
import com.xjtu.topic.repository.TopicRepository;
import org.jsoup.nodes.Document;
import com.xjtu.utils.JsoupDao;
import com.xjtu.utils.Log;
import com.xjtu.utils.SpiderUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * 构建领域知识森林数据
 *  
 * @author lynn
 * @date 2019/08
 */

public class FragmentCrawler {

	/**
	 * 将领域术语页面的内容按照分面存储到数据库
	 * @param domain 课程名
	 * @throws Exception
	 */

	public static void storeKGByDomainName(Domain domain) throws Exception {
		
		/**
		 * 读取数据库表格topic，得到知识主题
		 */
		String domainName = domain.getDomainName();

		List<Topic> topicList = MysqlReadWriteDAO.getDomainTopic(domainName);
		//对每一个主题，判断分面和分面层级是否存在，如不存在，则进行爬取

		for(int i = 0; i < topicList.size(); i++){
			Topic topic = topicList.get(i);
			Long topicID = topic.getTopicId();
			String topicName = topic.getTopicName();
			String topicUrl = topic.getTopicUrl();

			/**
			 * 判断数据是否已经存在
			 */
			Boolean existFacet = MysqlReadWriteDAO.judgeByClassAndTopic(Config.FACET_TABLE, domainName, topicName);

			/**
			 * 判断该主题的信息是不是在所有表格中已经存在
			 * 只要有一个不存在就需要再次爬取（再次模拟加载浏览器）
			 */
			if(!existFacet){     // 候选补充： || !existAssembleFragment

				/**
				 * selenium解析网页
				 */
				String topicHtml = SpiderUtils.seleniumWikiCN(topicUrl);
				Document doc = JsoupDao.parseHtmlText(topicHtml);
				// 获取并存储所有分面信息Facet
				List<FacetSimple> facetSimpleList = FragmentCrawlerDAO.getFacet(doc);

				if(!existFacet){
					MysqlReadWriteDAO.storeFacet(domainName, topicID, facetSimpleList);
					Log.log(domainName + "，" + topicName + "：分面已经爬取完毕!!!");
				} else {
					Log.log(domainName + "， " + topicName + "：分面已经存在,无需爬取");
				}

				// 获取并存储各级分面之间的关系FacetRelation
//				List<FacetRelation> facetRelationList = FragmentCrawlerDAO.getFacetRelation(doc);
//				if(!existFacetRelation){
//					MysqlReadWriteDAO.storeFacetRelation(domainName, topicID, topicName, facetRelationList);
//					Log.log(domainName + "，" + topicName + "：分面关系爬取完毕");
//				} else {
//					Log.log(domainName + "，" + topicName + "：分面关系已经爬取");
//				}

			} else {
				Log.log(domainName + "，" + topicName + "分面、分面关系已经存在");
			}
		}
	}

}
