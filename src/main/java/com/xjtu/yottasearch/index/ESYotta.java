package com.xjtu.yottasearch.index;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.filter.Filter;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ESYotta {
    private static RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost("127.0.0.1", 9200, "http")));
    private static ComboPooledDataSource cpds = new ComboPooledDataSource();
    private static String indexName = "yotta_index";

    static {
        try {
            cpds.setDriverClass("com.mysql.jdbc.Driver"); //loads the jdbc driver
            cpds.setJdbcUrl("jdbc:mysql://yotta.xjtushilei.com:9220/yotta_spring_boot_complete");
            cpds.setUser("root");
            cpds.setPassword("root");
            cpds.setMinPoolSize(5);
            cpds.setMaxPoolSize(20);
            cpds.setAcquireIncrement(5);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws IOException, SQLException {

//        creatIndexAndMapping();
//        indexOneTopic(1);
//        indexOneDomain(1);
//        getIndexEntityByTopicID(2);
//        System.out.println(getDomainIDListBySubjectID(1));
        System.out.println(search("计算机", null, null, null, null, null, null, null, 0, 10));
        client.close();
    }

    public static void test() {
        try {
            System.out.println(client.info().getNodeName());
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static Map search(String q, String subjectName, String domainName, String topicName, String facetName, String facetLayer,
                             String assembleSource, String assembleType, int page, int size) throws IOException, SQLException {
       /*
       构建query
        */
        BoolQueryBuilder boolq = new BoolQueryBuilder();
        if (subjectName != null && q.equals("")) {
            boolq.must(QueryBuilders.matchAllQuery());
        } else {
            boolq.must(QueryBuilders.multiMatchQuery(q, "subject_name", "domain_name", "topic_name", "facet_name", "assemble_text"));
        }
        if (subjectName != null && !subjectName.trim().equals("")) {
            boolq.must(QueryBuilders.matchPhraseQuery("subject_name", subjectName.trim()));
        }
        if (subjectName != null && !domainName.trim().equals("")) {
            boolq.must(QueryBuilders.matchPhraseQuery("domain_name", domainName.trim()));
        }
        if (subjectName != null && !topicName.trim().equals("")) {
            boolq.must(QueryBuilders.matchPhraseQuery("topic_name", topicName.trim()));
        }
        if (subjectName != null && !facetName.trim().equals("")) {
            boolq.must(QueryBuilders.matchPhraseQuery("facet_name", facetName.trim()));
        }
        if (subjectName != null && !facetLayer.trim().equals("")) {
            boolq.must(QueryBuilders.matchPhraseQuery("facet_layer", Long.valueOf(facetLayer.trim())));
        }
        if (subjectName != null && !assembleSource.trim().equals("")) {
            boolq.must(QueryBuilders.termQuery("assemble_source", assembleSource.trim()));
        }
        if (subjectName != null && !assembleType.trim().equals("")) {
            boolq.must(QueryBuilders.termQuery("assemble_type", assembleType));
        }

        /*
         * 高亮
         */
        HighlightBuilder hiBuilder = new HighlightBuilder();
        hiBuilder.preTags("<span class=\'point_key\'>");
        hiBuilder.postTags("</span>");
        hiBuilder.field("subject_name", 30);
        hiBuilder.field("domain_name", 30);
        hiBuilder.field("topic_name", 30);
        hiBuilder.field("facet_name", 50);
        hiBuilder.field("assemble_text", 300);

        /*
         * 构建查询
         */
        SearchRequest searchRequest = new SearchRequest(indexName);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.highlighter(hiBuilder)
                .aggregation(AggregationBuilders.terms("subject_name_agg").field("subject_name.key"))
                .aggregation(AggregationBuilders.terms("domain_name_agg").field("domain_name.key"))
                .aggregation(AggregationBuilders.terms("topic_name_agg").field("topic_name.key"))
                .aggregation(AggregationBuilders.terms("facet_name_agg").field("facet_name.key"))
                .aggregation(AggregationBuilders.terms("assemble_source_agg").field("assemble_source"))
                .aggregation(AggregationBuilders.terms("assemble_type_agg").field("assemble_type"))
                .aggregation(AggregationBuilders.filter("subject", QueryBuilders.matchQuery("subject_name", q)).subAggregation(
                        AggregationBuilders.terms("subject_id_agg_sub").field("subject_id")))
                .aggregation(AggregationBuilders.filter("domain", QueryBuilders.matchQuery("domain_name", q)).subAggregation(
                        AggregationBuilders.terms("domain_id_agg_sub").field("domain_id")))
                .aggregation(AggregationBuilders.filter("topic", QueryBuilders.matchQuery("topic_name", q)).subAggregation(
                        AggregationBuilders.terms("topic_id_agg_sub").field("topic_id")))
                .query(boolq)
                .from(page)
                .size(size);
        searchRequest.source(searchSourceBuilder);
        /*
        处理查询结果
         */
        HashMap<String, Object> res = new HashMap<>();
        SearchResponse searchResponse = client.search(searchRequest);
        res.put("total", searchResponse.getHits().getTotalHits());
        res.put("usetime", searchResponse.getTook().getStringRep());
        ArrayList<Map> hits = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits()) {
            Map smap = hit.getSourceAsMap();
            HashMap<String, Object> oneHit = new HashMap<>();
            oneHit.put("subject_id", smap.get("subject_id") == null ? null : smap.get("subject_id").toString());
            oneHit.put("subject_name", smap.get("subject_name") == null ? null : smap.get("subject_name").toString());
            oneHit.put("domain_id", smap.get("domain_id") == null ? null : smap.get("domain_id").toString());
            oneHit.put("domain_name", smap.get("domain_name") == null ? null : smap.get("domain_name").toString());
            oneHit.put("topic_id", smap.get("topic_id") == null ? null : smap.get("topic_id").toString());
            oneHit.put("topic_name", smap.get("topic_name") == null ? null : smap.get("topic_name").toString());
            oneHit.put("topic_layer", smap.get("topic_layer") == null ? null : smap.get("topic_layer").toString());
            oneHit.put("topic_url", smap.get("topic_url") == null ? null : smap.get("topic_url").toString());
            oneHit.put("facet_id", smap.get("facet_id") == null ? null : smap.get("facet_id").toString());
            oneHit.put("facet_name", smap.get("facet_name") == null ? null : smap.get("facet_name").toString());
            oneHit.put("facet_layer", smap.get("facet_layer") == null ? null : smap.get("facet_layer").toString());
            oneHit.put("parent_facet_id", smap.get("parent_facet_id") == null ? null : smap.get("parent_facet_id").toString());
            oneHit.put("assemble_id", smap.get("assemble_id") == null ? null : smap.get("assemble_id").toString());
            oneHit.put("assemble_content", smap.get("assemble_content") == null ? null : smap.get("assemble_content").toString());
            oneHit.put("assemble_scratch_time", smap.get("assemble_scratch_time") == null ? null : smap.get("assemble_scratch_time").toString());
            oneHit.put("assemble_text", smap.get("assemble_text") == null ? null : smap.get("assemble_text").toString());
            oneHit.put("assemble_source_id", smap.get("assemble_source_id") == null ? null : smap.get("assemble_source_id").toString());
            oneHit.put("assemble_source", smap.get("assemble_source") == null ? null : smap.get("assemble_source").toString());
            oneHit.put("assemble_type", smap.get("assemble_type") == null ? null : smap.get("assemble_type").toString());
            oneHit.put("index_date", smap.get("index_date") == null ? null : smap.get("index_date").toString());


            if (hit.getHighlightFields().get("subject_name") != null) {
                StringBuilder str = new StringBuilder();
                for (Text fragment : hit.getHighlightFields().get("subject_name").getFragments()) {
                    str.append(fragment.string());
                }
                oneHit.put("subject_name", str);
            }
            if (hit.getHighlightFields().get("domain_name") != null) {
                StringBuilder str = new StringBuilder();
                for (Text fragment : hit.getHighlightFields().get("domain_name").getFragments()) {
                    str.append(fragment.string());
                }
                oneHit.put("domain_name", str);
            }
            if (hit.getHighlightFields().get("topic_name") != null) {
                StringBuilder str = new StringBuilder();
                for (Text fragment : hit.getHighlightFields().get("topic_name").getFragments()) {
                    str.append(fragment.string());
                }
                oneHit.put("topic_name", str);
            }
            if (hit.getHighlightFields().get("facet_name") != null) {
                StringBuilder str = new StringBuilder();
                for (Text fragment : hit.getHighlightFields().get("facet_name").getFragments()) {
                    str.append(fragment.string());
                }
                oneHit.put("facet_name", str);
            }
            if (hit.getHighlightFields().get("assemble_text") != null) {
                StringBuilder str = new StringBuilder();
                for (Text fragment : hit.getHighlightFields().get("assemble_text").getFragments()) {
                    str.append(fragment.string());
                }
                oneHit.put("assemble_text", str);
            }
            hits.add(oneHit);
        }
        HashMap<String, Object> aggs = new HashMap<>();
        Aggregations aggregations = searchResponse.getAggregations();

        praseAgg("subject_name_agg", aggregations, aggs);
        praseAgg("domain_name_agg", aggregations, aggs);
        praseAgg("topic_name_agg", aggregations, aggs);
        praseAgg("facet_name_agg", aggregations, aggs);
        praseAgg("assemble_source_agg", aggregations, aggs);
        praseAgg("assemble_type_agg", aggregations, aggs);

        List<Map<String, Object>> subjectInfo = new ArrayList<>();
        Filter subjectFilter = searchResponse.getAggregations().get("subject");
        for (Terms.Bucket entry : ((Terms) subjectFilter.getAggregations().get("subject_id_agg_sub")).getBuckets()) {
            String id = String.valueOf(entry.getKey());
            HashMap<String, Object> map = new HashMap<>();
            map.put(id, getSubject(id));
            subjectInfo.add(map);
        }

        List<Map<String, Object>> domainInfo = new ArrayList<>();
        Filter domainFilter = searchResponse.getAggregations().get("domain");
        for (Terms.Bucket entry : ((Terms) domainFilter.getAggregations().get("domain_id_agg_sub")).getBuckets()) {
            String id = String.valueOf(entry.getKey());
            HashMap<String, Object> map = new HashMap<>();
            map.put(id, getDomain(id));
            domainInfo.add(map);
        }

        List<Map<String, Object>> topicInfo = new ArrayList<>();
        Filter topicFilter = searchResponse.getAggregations().get("topic");
        for (Terms.Bucket entry : ((Terms) topicFilter.getAggregations().get("topic_id_agg_sub")).getBuckets()) {
            String id = String.valueOf(entry.getKey());
            HashMap<String, Object> map = new HashMap<>();
            map.put(id, getTopic(id));
            topicInfo.add(map);
        }


        res.put("hits", hits);
        res.put("aggs", aggs);
        res.put("topicInfo", topicInfo);
        res.put("subjectInfo", subjectInfo);
        res.put("domainInfo", domainInfo);

        return res;
    }

    private static void praseAgg(String aggName, Aggregations aggregations, HashMap<String, Object> aggs) {
        Map<String, Long> subject_name_agg = new HashMap<>();
        for (Terms.Bucket entry : ((Terms) aggregations.get(aggName)).getBuckets()) {
            String key = (String) entry.getKey(); // Term
            long count = entry.getDocCount(); // Doc count
            subject_name_agg.put(key, count);
        }
        aggs.put(aggName, subject_name_agg);
    }

    public static String creatIndexAndMapping() throws IOException {
        //创建index
        CreateIndexRequest request = new CreateIndexRequest(indexName);
        request.settings(Settings.builder()
                .put("index.number_of_shards", 10)
                .put("index.number_of_replicas", 0)
        );
        request.mapping("_doc",
                "{\n" +
                        "  \"_doc\": {\n" +
                        "    \"properties\": {\n" +
                        "      \"subject_id\": {\n" +
                        "        \"type\": \"long\"\n" +
                        "      },\n" +
                        "      \"subject_name\": {\n" +
                        "        \"type\": \"text\",\n" +
                        "        \"fielddata\": true,\n" +
                        "        \"fields\": {\n" +
                        "          \"key\": {\n" +
                        "            \"type\": \"keyword\"\n" +
                        "          }\n" +
                        "        }\n" +
                        "      },\n" +
                        "      \"domain_id\": {\n" +
                        "        \"type\": \"long\"\n" +
                        "      },\n" +
                        "      \"domain_name\": {\n" +
                        "        \"type\": \"text\",\n" +
                        "        \"fielddata\": true,\n" +
                        "        \"fields\": {\n" +
                        "          \"key\": {\n" +
                        "            \"type\": \"keyword\"\n" +
                        "          }\n" +
                        "        }\n" +
                        "      },\n" +
                        "      \"topic_id\": {\n" +
                        "        \"type\": \"long\"\n" +
                        "      },\n" +
                        "      \"topic_name\": {\n" +
                        "        \"type\": \"text\",\n" +
                        "        \"fielddata\": true,\n" +
                        "        \"fields\": {\n" +
                        "          \"key\": {\n" +
                        "            \"type\": \"keyword\"\n" +
                        "          }\n" +
                        "        }\n" +
                        "      },\n" +
                        "      \"topic_layer\": {\n" +
                        "        \"type\": \"long\"\n" +
                        "      },\n" +
                        "      \"topic_url\": {\n" +
                        "        \"type\": \"keyword\"\n" +
                        "      },\n" +
                        "      \"facet_id\": {\n" +
                        "        \"type\": \"long\"\n" +
                        "      },\n" +
                        "      \"facet_name\": {\n" +
                        "        \"type\": \"text\",\n" +
                        "        \"fielddata\": true,\n" +
                        "        \"fields\": {\n" +
                        "          \"key\": {\n" +
                        "            \"type\": \"keyword\"\n" +
                        "          }\n" +
                        "        }\n" +
                        "      },\n" +
                        "      \"facet_layer\": {\n" +
                        "        \"type\": \"long\"\n" +
                        "      },\n" +
                        "      \"parent_facet_id\": {\n" +
                        "        \"type\": \"long\"\n" +
                        "      },\n" +
                        "      \"assemble_id\": {\n" +
                        "        \"type\": \"long\"\n" +
                        "      },\n" +
                        "      \"assemble_content\": {\n" +
                        "        \"type\": \"text\"\n" +
                        "      },\n" +
                        "      \"assemble_scratch_time\": {\n" +
                        "        \"type\": \"date\",\n" +
                        "        \"format\" : \"yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis\"\n" +
                        "      },\n" +
                        "      \"assemble_text\": {\n" +
                        "        \"type\": \"text\"\n" +
                        "      },\n" +
                        "      \"assemble_source_id\": {\n" +
                        "        \"type\": \"long\"\n" +
                        "      },\n" +
                        "      \"assemble_source\": {\n" +
                        "        \"type\": \"keyword\"\n" +
                        "      },\n" +
                        "      \"assemble_type\": {\n" +
                        "        \"type\": \"keyword\"\n" +
                        "      },\n" +
                        "      \"index_date\": {\n" +
                        "        \"type\": \"date\",\n" +
                        "        \"format\" : \"yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis\"\n" +
                        "      }\n" +
                        "    }\n" +
                        "  }\n" +
                        "}",
                XContentType.JSON);
        CreateIndexResponse createIndexResponse = client.indices().create(request);
        boolean acknowledged = createIndexResponse.isAcknowledged();
        String res;
        if (acknowledged) {
            res = "创建index成功！";
        } else {
            res = "创建index失败！";
        }
        client.close();
        return res;
    }

    public static Set<String> indexOneSubject(long subjectID) throws SQLException {
        Set<String> result = Collections.synchronizedSet(new HashSet<>());
        getDomainIDListBySubjectID(subjectID).forEach(domainID -> {
            try {
                getTopicIDListByDomainID(domainID).parallelStream().forEach(topicID -> {
                    BulkRequest request = new BulkRequest();
                    ArrayList<Map<String, Object>> list = null;
                    try {
                        list = getIndexEntityByTopicID(topicID);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    if (list.size() != 0) {
                        for (Map<String, Object> map : list) {
                            request.add(new IndexRequest(indexName, "_doc", String.valueOf(map.get("assemble_id"))).source(map));
                        }
                        try {
                            BulkResponse bulkResponse = client.bulk(request);
                            if (bulkResponse.hasFailures()) {
                                result.add(domainID + "-" + topicID);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        System.out.println("index " + subjectID + " done!");
        return result;
    }

    public static Set<Long> indexOneDomain(long domainID) throws SQLException {
        Set<Long> result = Collections.synchronizedSet(new HashSet<>());
        getTopicIDListByDomainID(domainID).parallelStream().forEach(topicID -> {
            try {
                ArrayList<Map<String, Object>> list = getIndexEntityByTopicID(topicID);
                BulkRequest request = new BulkRequest();
                for (Map<String, Object> map : list) {
                    request.add(new IndexRequest(indexName, "_doc", String.valueOf(map.get("assemble_id"))).source(map));
                }
                try {
                    BulkResponse bulkResponse = client.bulk(request);
                    if (bulkResponse.hasFailures()) {
                        result.add(topicID);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

        });
        System.out.println("index " + domainID + " done!");
        return result;
    }

    public static List<String> indexOneTopic(long topicID) throws SQLException {
        List<String> result = Collections.synchronizedList(new ArrayList<>());
        BulkRequest request = new BulkRequest();
        ArrayList<Map<String, Object>> list = getIndexEntityByTopicID(topicID);
        for (Map<String, Object> map : list) {
            request.add(new IndexRequest(indexName, "_doc", String.valueOf(map.get("assemble_id"))).source(map));
        }
        try {
            BulkResponse bulkResponse = client.bulk(request);
            if (bulkResponse.hasFailures()) {
                for (BulkItemResponse bulkItemResponse : bulkResponse) {
                    if (bulkItemResponse.isFailed()) {
                        BulkItemResponse.Failure failure = bulkItemResponse.getFailure();
                        result.add(failure.toString());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("index " + topicID + " done!");
        return result;
    }

    private static ArrayList<Long> getTopicIDListByDomainID(long domainID) throws SQLException {
        Connection conn = cpds.getConnection();

        String sql = "SELECT topic_id\n" +
                "FROM\n" +
                "topic\n" +
                "WHERE\n" +
                "topic.domain_id =  " + domainID;
        PreparedStatement pst = conn.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();
        ArrayList<Long> topicIDList = new ArrayList<>();
        while (rs.next()) {
            topicIDList.add(rs.getLong("topic_id"));
        }
        pst.close();
        conn.close();
        System.out.println("topicIDList:" + topicIDList.size() + "    domainID:" + domainID);
        return topicIDList;
    }

    private static ArrayList<Long> getDomainIDListBySubjectID(long subjectID) throws SQLException {
        Connection conn = cpds.getConnection();

        String sql = "SELECT domain_id\n" +
                "FROM\n" +
                "domain as d\n" +
                "WHERE\n" +
                "d.subject_id =" + subjectID;
        PreparedStatement pst = conn.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();
        ArrayList<Long> domainIDList = new ArrayList<>();
        while (rs.next()) {
            domainIDList.add(rs.getLong("domain_id"));
        }
        pst.close();
        conn.close();
        System.out.println("domainIDList:" + domainIDList.size() + "    subjectID:" + subjectID);
        return domainIDList;
    }

    private static ArrayList<Map<String, Object>> getIndexEntityByTopicID(long topicID) throws SQLException {
        Connection conn = cpds.getConnection();

        String sql = "SELECT *\n" +
                "FROM\n" +
                "assemble ,\n" +
                "domain ,\n" +
                "facet ,\n" +
                "`subject` ,\n" +
                "source ,\n" +
                "topic\n" +
                "WHERE\n" +
                "assemble.facet_id = facet.facet_id AND\n" +
                "assemble.domain_id =  domain.domain_id AND\n" +
                "assemble.source_id = source.source_id AND\n" +
                "`subject`.subject_id = domain.subject_id AND\n" +
                "facet.topic_id = topic.topic_id AND\n" +
                "topic.domain_id = domain.domain_id AND \n" +
                "topic.topic_id = " + topicID;
        PreparedStatement pst = conn.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();


        ArrayList<Map<String, Object>> list = new ArrayList<>();
        while (rs.next()) {
            Map<String, Object> jsonMap = new HashMap<>();

            long subject_id = rs.getLong("subject_id");
            String subject_name = rs.getString("subject_name");
            jsonMap.put("subject_name", subject_name);
            jsonMap.put("subject_id", subject_id);


            long domain_id = rs.getLong("domain_id");
            String domain_name = rs.getString("domain_name");
            jsonMap.put("domain_id", domain_id);
            jsonMap.put("domain_name", domain_name);

            long facet_id = rs.getLong("facet_id");
            long facet_layer = rs.getLong("facet_layer");
            long parent_facet_id = rs.getLong("parent_facet_id");
            String facet_name = rs.getString("facet_name");
            jsonMap.put("facet_id", facet_id);
            jsonMap.put("facet_layer", facet_layer);
            jsonMap.put("parent_facet_id", parent_facet_id);
            jsonMap.put("facet_name", facet_name);


            long topic_id = rs.getLong("topic_id");
            long topic_layer = rs.getLong("topic_layer");
            String topic_name = rs.getString("topic_name");
            String topic_url = rs.getString("topic_url");
            jsonMap.put("topic_id", topic_id);
            jsonMap.put("topic_layer", topic_layer);
            jsonMap.put("topic_name", topic_name);
            jsonMap.put("topic_url", topic_url);

            long assemble_id = rs.getLong("assemble_id");
            String assemble_content = rs.getString("assemble_content");
            String assemble_scratch_time = rs.getString("assemble_scratch_time");
            String assemble_text = rs.getString("assemble_text");
            long assemble_source_id = rs.getLong("source_id");
            String assemble_source = rs.getString("source_name");
            String assemble_type = rs.getString("type");
            jsonMap.put("assemble_id", assemble_id);
            jsonMap.put("assemble_content", assemble_content);
            jsonMap.put("assemble_scratch_time", assemble_scratch_time);
            jsonMap.put("assemble_text", assemble_text);
            jsonMap.put("assemble_type", assemble_type);
            jsonMap.put("assemble_source_id", assemble_source_id);
            jsonMap.put("assemble_source", assemble_source);

            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String index_date = sdf.format(d);
            jsonMap.put("index_date", index_date);

            list.add(jsonMap);
        }
        pst.close();
        conn.close();
        System.out.print("IndexEntity:" + list.size() + "  topicID:" + topicID);
        System.out.println();
        return list;
    }

    private static Map getDomain(String domainID) throws SQLException {
        Connection conn = cpds.getConnection();

        String sql = "SELECT * \n" +
                "FROM\n" +
                "domain as d\n" +
                "WHERE\n" +
                "d.domain_id =" + domainID;
        PreparedStatement pst = conn.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();
        Map<String, Object> map = new HashMap<>();
        while (rs.next()) {
            map.put("domain_id", domainID);
            map.put("domain_name", rs.getString("domain_name"));
            map.put("subject_id", rs.getLong("subject_id"));
        }
        pst.close();
        conn.close();
        return map;
    }

    private static Map getSubject(String subjectID) throws SQLException {
        Connection conn = cpds.getConnection();

        String sql = "SELECT * \n" +
                "FROM\n" +
                "subject \n" +
                "WHERE\n" +
                "subject_id =" + subjectID;
        PreparedStatement pst = conn.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();
        Map<String, Object> map = new HashMap<>();
        while (rs.next()) {
            map.put("subject_id", subjectID);
            map.put("subject_name", rs.getString("subject_name"));
            map.put("note", rs.getString("note"));
        }
        pst.close();
        conn.close();
        return map;
    }

    private static Map getTopic(String topicID) throws SQLException {
        Connection conn = cpds.getConnection();

        String sql = "SELECT * \n" +
                "FROM\n" +
                "topic  \n" +
                "WHERE\n" +
                "topic_id =" + topicID;
        PreparedStatement pst = conn.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();
        Map<String, Object> map = new HashMap<>();
        while (rs.next()) {
            map.put("topic_id", topicID);
            map.put("domain_id", rs.getLong("domain_id"));
            map.put("topic_layer", rs.getLong("topic_layer"));
            map.put("topic_name", rs.getString("topic_name"));
            map.put("topic_url", rs.getString("topic_url"));
        }
        pst.close();
        conn.close();
        return map;
    }

}
