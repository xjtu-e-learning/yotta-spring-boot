package com.xjtu.spider_topic.spiders.wikicn;

import com.xjtu.common.Config;
import com.xjtu.domain.domain.Domain;
import com.xjtu.facet.domain.Facet;
import com.xjtu.facet.domain.FacetRelation;
import com.xjtu.facet.domain.FacetSimple;
import com.xjtu.facet.repository.FacetRepository;
import com.xjtu.topic.domain.Term;
import com.xjtu.topic.domain.Topic;
import com.xjtu.utils.Log;
import com.xjtu.utils.mysqlUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;


/**
 * 读取和存储数据库操作
 * 1. 将数据从数据库表格中读取出来
 * 2. 将数据写入数据库表格中
 * 3. 判断数据表中是否存在某个数据
 * <p>
 * 数据库表格如下：
 * 1. domain: 领域ID及其领域名（ClassName）
 * 2. domain_layer: 采集到的领域术语，一共有三层，每一层之间存在重复
 * 3. domain_topic: 知识主题，对领域术语进行抽取，保证每一层术语之间不重复
 * 4. spider_text: 文本知识碎片，爬取知识主题对应的中文维基页面，获取文本知识碎片
 * 5. spider_image: 图片知识碎片，爬取知识主题对应。。。
 * 6. facet: 知识主题对应的分面，一共有三级分面
 * 7. assemble_text: 文本碎片装配，将知识主题下的文本知识碎片挂载到对应的每一级分面上
 * 8. assemble_image: 图片碎片装配，将知识主题下的图片知识碎片挂载到对应的每一级分面上
 *
 * @author 张铎
 * @date 2020年2月29日
 */
public class MysqlReadWriteDAO {

    /**
     * 判断表格，判断某门课程的数据是否已经在这个数据表中存在
     * 适用表格：domain_layer，dependency
     *
     * @param table
     * @param domainName
     * @return true表示该领域已经爬取
     */
    public static Boolean judgeByClass(String table, String domainName) {
        Boolean exist = false;
        mysqlUtils mysql = new mysqlUtils();
        String sql = "select * from " + table + " where domain_name=?";
        List<Object> params = new ArrayList<Object>();
        params.add(domainName);
        try {
            List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
            if (results.size() != 0) {
                exist = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mysql.closeconnection();
        }
        return exist;
    }

    /**
     * 存储课程名
     * 存储domain，保存领域名信息
     *
     * @param domainList
     */
    public static void storeDomain(List<Domain> domainList) {
        mysqlUtils mysql = new mysqlUtils();
        String sql = "insert into " + Config.DOMAIN_TABLE + " (domain_name, subject_id) VALUES(?, ?);";
        for (int i = 0; i < domainList.size(); i++) {
            Domain domain = domainList.get(i);
            List<Object> params = new ArrayList<Object>();
            params.add(domain.getDomainName());
            params.add(domain.getSubjectId());
            try {
                mysql.addDeleteModify(sql, params);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mysql.closeconnection();
    }

    /**
     * 判断表格，判断某门课程的知识主题数据是否已经在这个数据表中存在
     * 适用表格：topic
     *
     * @param table
     * @param domainName
     * @return true表示该领域已经爬取
     */
    public static Boolean judgeByClass1(String table, String table1, String domainName) {
        Boolean exist = false;
        mysqlUtils mysql = new mysqlUtils();
        String sql = "select topic.* " + "from " + table + "," + table1 + " where domain_name=? and domain.domain_id=topic.domain_id";
        List<Object> params = new ArrayList<Object>();
        params.add(domainName);
        try {
            List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
            if (results.size() != 0) {
                exist = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mysql.closeconnection();
        }
        return exist;
    }

    /**
     * 存储domain_layer，存储第n层领域术语到数据库 domain_layer 表格（List）
     *
     * @param termList
     * @param domain
     * @param layer
     */
//    public static void storeDomainLayer(List<Term> termList, String domain, int layer) {
//        mysqlUtils mysql = new mysqlUtils();
//        String sql = "insert into " + Config.DOMAIN_LAYER_TABLE + " (term_name, term_url, term_layer, domain_name)"
//                + " VALUES(?, ?, ?, ?);";
//        for (int i = 0; i < termList.size(); i++) {
//            Term term = termList.get(i);
//            List<Object> params = new ArrayList<Object>();
//            params.add(term.getTermName());
//            params.add(term.getTermUrl());
//            params.add(layer);
//            params.add(domain);
//            try {
//                mysql.addDeleteModify(sql, params);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        mysql.closeconnection();
//    }

    /**
     * 读取domain_layer，得到所有领域术语（按照课程）
     *
     * @return
     */
//    public static List<Term> getDomainLayer(String domain, int layer) throws Exception {
//        List<Term> termList = new ArrayList<Term>();
//        mysqlUtils mysql = new mysqlUtils();
//        String sql = "select * from " + Config.DOMAIN_LAYER_TABLE + " where domain_name=? and term_layer=?";
//        List<Object> params = new ArrayList<Object>();
//        params.add(domain);
//        params.add(layer);
//        try {
//            List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
//            for (int i = 0; i < results.size(); i++) {
//                Map<String, Object> result = results.get(i);
//                String termName = result.get("term_name").toString();
//                String termUrl = result.get("term_url").toString();
//                Term termLayer = new Term(termName, termUrl);
//                termList.add(termLayer);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            mysql.closeconnection();
//        }
//        return termList;
//    }

    /**
     * 存储domain_topic，存储第n层领域术语到数据库 domain_topic 表格（Set）
     *
     * @param termList
     * @param domainId
     * @param layer
     */
    public static void storeDomainTopic(Set<Term> termList, Long domainId, int layer) {
        mysqlUtils mysql = new mysqlUtils();
        String sql = "insert into " + Config.TOPIC_TABLE + " (domain_id, topic_layer, topic_name, topic_url)"
                + " VALUES(?, ?, ?, ?);";
        for (Term term : termList) {
            List<Object> params = new ArrayList<Object>();
            params.add(domainId);
            params.add(layer);
            params.add(term.getTermName());
            params.add(term.getTermUrl());
            try {
                mysql.addDeleteModify(sql, params);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mysql.closeconnection();
    }

    /**
     * 读取domain_topic，得到所有主题（按照课程）
     *
     * @return
     */
    public static List<Topic> getDomainTopic(Long domainId) throws Exception {
        List<Topic> topicList = new ArrayList<Topic>();
        mysqlUtils mysql = new mysqlUtils();
        String sql = "select * from " + Config.TOPIC_TABLE + " where domain_id=?";
        List<Object> params = new ArrayList<Object>();
        params.add(domainId);
        try {
            List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
            for (int i = 0; i < results.size(); i++) {
                Map<String, Object> result = results.get(i);
                Long topicID = Long.parseLong(result.get("topic_id").toString());
                String topicName = result.get("topic_name").toString();
                String topicUrl = result.get("topic_url").toString();
                Topic topic = new Topic(topicID, topicName, topicUrl);
                topicList.add(topic);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mysql.closeconnection();
        }
        return topicList;
    }

    /**
     * 张铎	2019.7
     * 存储facet，按照领域进行存储,【】【】【】【】【暂时没有父子分面信息】
     *
     * @return
     */
    public static void storeFacet(Long topicID, List<FacetSimple> facetSimpleList) throws Exception {
        for (int i = 0; i < facetSimpleList.size(); i++) {
            mysqlUtils mysql = new mysqlUtils();
            String sql = "insert into " + Config.FACET_TABLE + "(topic_id, facet_name, facet_layer) "
                    + "values(?, ?, ?)";
            FacetSimple facetSimple = facetSimpleList.get(i);
            String facetName = facetSimple.getFacetName();
            int facetLayer = facetSimple.getFacetLayer();
            List<Object> params = new ArrayList<Object>();
            params.add(topicID);
            params.add(facetName);
            params.add(facetLayer);
            try {
                mysql.addDeleteModify(sql, params);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mysql.closeconnection();
            }
        }

    }


    /**
     * 存储facet_Relation，按照领域进行存储
     *
     * @return
     */
    public static void storeFacetRelation(Long topicID, List<FacetRelation> facetRelationList) throws Exception {
        for (int i = 0; i < facetRelationList.size(); i++) {
            mysqlUtils mysql = new mysqlUtils();
            String sql = "update " + Config.FACET_TABLE
                    + " set parent_facet_id=? " + "where facet_id=?";
            FacetRelation facetRelation = facetRelationList.get(i);
            Log.log("该主题的分面关系为" + facetRelation.toString());
            //获取子分面ID
            String childFacetName = facetRelation.getChildFacet();
            Long childFacetID = findByTopicIdAndFacetName(topicID, childFacetName);
            //获取父分面的ID
            String parentFacetName = facetRelation.getParentFacet();
            Long parentFacetID = findByTopicIdAndFacetName(topicID, parentFacetName);
            Log.log("取出的子分面ID和父分面ID为：" + childFacetID + " " + parentFacetID);

            List<Object> params = new ArrayList<>();
            params.add(parentFacetID);
            params.add(childFacetID);
            try {
                mysql.addDeleteModify(sql, params);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mysql.closeconnection();
            }
        }

    }

    public static Long findByTopicIdAndFacetName(Long topicID, String parentFcetName) {
        mysqlUtils mysql = new mysqlUtils();
        String sql = "select * from " + Config.FACET_TABLE + " where topic_id=? and facet_name=?";
        List<Object> params = new ArrayList<Object>();
        params.add(topicID);
        params.add(parentFcetName);
        Long facetID = -1l;
        try {
            List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
            if (results.size() != 0) {
                Map<String, Object> map = results.get(0);
                facetID = (Long) map.get("facet_id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mysql.closeconnection();
        }
        return facetID;

    }


    /**
     * 判断表格，判断某门课程下某个主题的数据是否已经在这个数据表中存在
     * 适用表格：facet，spider_text，assemble_text
     *
     * @param table
     * @param domain
     * @param topic
     * @return true表示该领域已经爬取
     */
    public static Boolean judgeByClassAndTopic(String table, String domain, String topic) {
        Boolean exist = false;
        mysqlUtils mysql = new mysqlUtils();
        String sql = "select * from " + table + ",topic,domain where domain.domain_name=? and topic.topic_name=? and domain.domain_id=topic.domain_id and topic.topic_id=" + table + ".topic_id";
        List<Object> params = new ArrayList<Object>();
        params.add(domain);
        params.add(topic);
        try {
            List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
            if (results.size() != 0) {
                exist = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mysql.closeconnection();
        }
        return exist;
    }


    /**
     * 判断表格，判断某一级分面在分面关系表的"父分面"中是否存在
     * 适用表格：facet_relation
     * @param assemble
     * @param domain
     * @param topic
     * @return true表示该领域已经爬取
     */
//	public static Boolean judgeFacetRelation(AssembleFragmentFuzhu assemble, String domain, String topic){
//		Boolean exist = false;
//		mysqlUtils mysql = new mysqlUtils();
//		String facetName = assemble.getFacetName();
//		int facetLayer = assemble.getFacetLayer();
//		String sql = "select * from " + Config.FACET_RELATION_TABLE +
//				" where ClassName=? and TermName=? and ParentFacet=? and ParentLayer=?";
//		List<Object> params = new ArrayList<Object>();
//		params.add(domain);
//		params.add(topic);
//		params.add(facetName);
//		params.add(facetLayer);
//		try {
//			List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
//			if (results.size()!=0) {
//				exist = true;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			mysql.closeconnection();
//		}
//		return exist;
//	}


}
