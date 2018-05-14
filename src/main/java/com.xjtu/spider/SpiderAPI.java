package com.xjtu.spider;

import io.swagger.annotations.*;
import org.apache.commons.io.FileUtils;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.*;

/**  
 * 碎片化知识采集
 * @author 郑元浩 
 */
@Path("/SpiderAPI")
@Api(value = "SpiderAPI")
public class SpiderAPI {

    @GET
    @Path("/getAssembleFragmentByID")
    @ApiOperation(value = "根据碎片ID，获得装配碎片的信息", notes = "根据碎片ID，获得装配碎片的信息")
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "MySql数据库  查询失败"),
            @ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class)})
    @Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
    public static Response getAssembleFragmentByID(
            @ApiParam(value = "碎片ID", required = true) @QueryParam("FragmentID") int FragmentID
    ) {
        Response response = null;
        /**
         * 获得碎片信息
         */
        mysqlUtils mysql = new mysqlUtils();
        String sql = "select * from " + Config.ASSEMBLE_FRAGMENT_TABLE + " where FragmentID=?";
        List<Object> params = new ArrayList<Object>();
        params.add(FragmentID);
        try {
            List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
            response = Response.status(200).entity(results).build();
        } catch (Exception e) {
            e.printStackTrace();
            response = Response.status(401).entity(new error(e.toString())).build();
        } finally {
            mysql.closeconnection();
        }
        return response;
    }

    @GET
    @Path("/getFragmentByID")
    @ApiOperation(value = "根据碎片ID，获得未装配碎片的信息", notes = "根据碎片ID，获得未装配碎片的信息")
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "MySql数据库  查询失败"),
            @ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class)})
    @Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
    public static Response getFragmentByID(
            @ApiParam(value = "碎片ID", required = true) @QueryParam("FragmentID") int FragmentID
    ) {
        Response response = null;
        /**
         * 获得碎片信息
         */
        mysqlUtils mysql = new mysqlUtils();
        String sql = "select * from " + Config.FRAGMENT + " where FragmentID=?";
        List<Object> params = new ArrayList<Object>();
        params.add(FragmentID);
        try {
            List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
            response = Response.status(200).entity(results).build();
        } catch (Exception e) {
            e.printStackTrace();
            response = Response.status(401).entity(new error(e.toString())).build();
        } finally {
            mysql.closeconnection();
        }
        return response;
    }

    @POST
    @Path("/getFragmentCountBySource")
    @ApiOperation(value = "根据课程和主题，得到每个数据源下碎片数量分布", notes = "根据课程和主题，得到每个数据源下碎片数量分布")
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "MySql数据库  查询失败", response = String.class),
            @ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class)})
    @Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
    public static Response getFragmentCountBySource(
            @FormParam("className") String className,
            @FormParam("topicNames") String topicNames
    ) {
        Response response = null;
        mysqlUtils mysql = new mysqlUtils();
        String[] topicNameArray = topicNames.split(",");
        Map<String, Integer> fragmentCount = new HashMap<>();
        EchartObj2 echartObj2 = new EchartObj2(); // 返回对象：碎片来源和数据源信息
        List<EchartObj1> echartObj1s = new ArrayList<>(); // 碎片来源
        List<String> sourceNames = new ArrayList<>(); // 数据源
        // 获得所有数据源，存到list中
        String sqlSource = "select SourceName from source order by SourceID";
        List<Object> params1 = new ArrayList<Object>();
        try {
            List<Map<String, Object>> results = mysql.returnMultipleResult(sqlSource, params1);
            for (int i = 0; i < results.size(); i++) {
                String sourceName = results.get(i).get("SourceName").toString();
                sourceNames.add(sourceName);
                fragmentCount.put(sourceName, 0);
            }
        } catch (Exception e) {
            response = Response.status(401).entity(new error(e.toString())).build();
            e.printStackTrace();
        }
        // 循环所有主题，获得指定主题每个数据源下的碎片数量
        String sql = "SELECT Count(af.FragmentID) AS fc " +
                "FROM " + Config.ASSEMBLE_FRAGMENT_TABLE + " AS af RIGHT JOIN " + Config.SOURCE_TABLE + " AS s " +
                "ON s.SourceName = af.SourceName AND af.ClassName = ? AND af.TermName = ? " +
                "GROUP BY s.SourceName ORDER BY s.SourceID ASC";
        for (int i = 0; i < topicNameArray.length; i++) {
            String topicName = topicNameArray[i];
            List<Object> params = new ArrayList<Object>();
            params.add(className);
            params.add(topicName);
            try {
                List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
                for (int j = 0; j < results.size(); j++) {
                    String sourceName = sourceNames.get(j);
                    int count = Integer.parseInt(results.get(j).get("fc").toString());
                    fragmentCount.put(sourceName, fragmentCount.get(sourceName) + count);
                }
            } catch (Exception e) {
                response = Response.status(401).entity(new error(e.toString())).build();
                e.printStackTrace();
            }
        }
        mysql.closeconnection();
        // 得到所有碎片源信息
        for (String word : fragmentCount.keySet()) {
            Log.log(word + "：" + fragmentCount.get(word));
            EchartObj1 echartObj1 = new EchartObj1();
            echartObj1.setName(word);
            echartObj1.setValue(fragmentCount.get(word));
            echartObj1s.add(echartObj1);
        }
        // 返回对象：碎片来源和数据源信息
        echartObj2.setEchartObj1s(echartObj1s);
        echartObj2.setSources(sourceNames);
        response = Response.status(200).entity(echartObj2).build();
        return response;
    }

    @POST
    @Path("/getWordcount")
    @ApiOperation(value = "根据文本内容得到词频", notes = "根据文本内容得到词频")
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "MySql数据库  查询失败", response = String.class),
            @ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class)})
    @Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
    public static Response getWordcount(
            @FormParam("className") String className,
            @FormParam("topicNames") String topicNames,
            @FormParam("sourceName") String sourceName,
            @FormParam("hasSourceName") boolean hasSourceName
    ) {
        Response response = null;
        String[] topicNameArray = topicNames.split(",");
        StringBuffer text = new StringBuffer(); // 存储所有文本
        Map<String, Integer> wordfre = new HashMap<>();
        mysqlUtils mysql = new mysqlUtils();
        String sql = "select FragmentID, Text from " + Config.ASSEMBLE_FRAGMENT_TABLE + " where ClassName=? and TermName=?";
        if (hasSourceName) {
            sql = "select FragmentID, Text from " + Config.ASSEMBLE_FRAGMENT_TABLE + " where ClassName=? and TermName=? and SourceName=?";
        }
        // 循环所有主题
        for (int i = 0; i < topicNameArray.length; i++) {
            String topicName = topicNameArray[i];
            List<Object> params = new ArrayList<Object>();
            params.add(className);
            params.add(topicName);
            if (hasSourceName) {
                params.add(sourceName);
            }
            try {
                List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
                for (int j = 0; j < results.size(); j++) {
                    Map<String, Object> map = results.get(j);
                    text.append(map.get("Text").toString());
                }
            } catch (Exception e) {
                response = Response.status(401).entity(new error(e.toString())).build();
                e.printStackTrace();
            }
        }
//        Log.log(text.toString());
        // Lucene Ik Analyzer 中文分词
        StringReader reader = new StringReader(text.toString());
        IKSegmenter ik = new IKSegmenter(reader, true); // 当为true时，分词器进行最大词长切分
        Lexeme lexeme = null;
        try {
            while ((lexeme = ik.next()) != null) {
                String word = lexeme.getLexemeText();
                if (!wordfre.containsKey(word)) {
                    wordfre.put(word, 1);
                } else {
                    wordfre.put(word, wordfre.get(word) + 1);
                }
            }
        } catch (IOException e) {
            response = Response.status(401).entity(new error(e.toString())).build();
            e.printStackTrace();
        } finally {
            reader.close();
        }
//        for (String word : wordfre.keySet()) {
//            Log.log(word + "：" + wordfre.get(word));
//        }
        mysql.closeconnection();
        response = Response.status(200).entity(wordfre).build();
        return response;
    }

    @POST
    @Path("/uploadFile")
    @ApiOperation(value = "上传爬虫excel", notes = "上传爬虫excel")
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "MySql数据库  查询失败"),
            @ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class)})
    @Consumes(MediaType.MULTIPART_FORM_DATA + ";charset=" + "UTF-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
    public static Response uploadFile(
            @FormDataParam("file") FormDataContentDisposition disposition,
            @FormDataParam("file") InputStream fileInputStream
    ) throws Exception {
        Map<String, String> map = new HashMap<>();
        Response response = null;
//        Log.log(disposition.getFileName() + ", " + disposition.getType() + ", "  + disposition.getSize());
        // 判断文件是否为空
        if (fileInputStream != null) {
            try {
                // 文件保存路径
                File path = new File(SpiderAPI.class.getClassLoader().getResource("").getPath() + "/upload");
                if (!path.exists()) {
                    boolean status = path.mkdirs();
                }
                String filePath = path + "/" + disposition.getFileName();
                Log.log("爬虫excel保存路径为：" + filePath);
                // 转存文件
                FileUtils.copyInputStreamToFile(fileInputStream, new File(filePath));
                map.put("msg", "上传成功");
                response = Response.status(200).entity(map).build();
            } catch (Exception e) {
                Log.log(e.getMessage());
                map.put("msg", "上传失败");
                response = Response.status(401).entity(map).build();
            }
        }
        return response;
    }

    @GET
    @Path("/startSpiders")
    @ApiOperation(value = "启动爬虫", notes = "输入课程信息excel文件，启动爬虫")
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "MySql数据库  查询失败"),
            @ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class)})
    @Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
    public static Response startSpiders(
            @ApiParam(value = "文件名", required = true) @QueryParam("fileName") String fileName
    ) throws Exception {
        Response response = null;
        // 如果数据库中表格不存在，先新建数据库表格
        DatabaseUtils.createTable();
        // 爬取多门课程
        String filePath = SpiderAPI.class.getClassLoader().getResource("").getPath() + "\\upload\\" + fileName;
        Log.log("加载爬虫所需的课程excel文件：" + filePath);
        List<Domain> domainList = SpidersRun.getDomainFromExcel(filePath);
        for (int i = 0; i < domainList.size(); i++) {
            Log.log(domainList.get(i));
            SpidersRun.constructKGByDomainName(domainList.get(i));
            SpidersRun.spiderFragment(domainList.get(i));
        }
        response = Response.status(200).entity("爬取结束").build();
        return response;
    }

    @GET
    @Path("/startSingleSpider")
    @ApiOperation(value = "启动爬虫", notes = "输入学科和课程，启动爬虫")
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "数据已经爬取"),
            @ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class)})
    @Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
    public static Response startSingleSpider(
            @ApiParam(value = "学科名", required = true) @QueryParam("SubjectName") String SubjectName,
            @ApiParam(value = "课程名", required = true) @QueryParam("ClassName") String ClassName
    ) throws Exception {
        Response response = null;
        // 如果数据库中表格不存在，先新建数据库表格
        DatabaseUtils.createTable();

        Domain domain = new Domain();
        domain.setSubjectName(SubjectName);
        domain.setClassName(ClassName);
        if(MysqlReadWriteDAO.judgeByClass(Config.DOMAIN_TABLE, ClassName)) {
            response = Response.status(401).entity("已经爬取").build();
        } else {
            // 爬取该课程数据
            SpidersRun.constructKGByDomainName(domain);
            SpidersRun.spiderFragment(domain);
            response = Response.status(200).entity("爬取结束").build();
        }
        return response;
    }

    @POST
    @Path("/getFragmentByTopicArrayAndSource")
    @ApiOperation(value = "获取主题下的碎片数据", notes = "根据课程名、数据源、主题数组，获取主题下的碎片数据")
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "MySql数据库  查询失败", response = String.class),
            @ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class)})
    @Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
    public static Response getFragmentByTopicArrayAndSource(
            @FormParam("className") String className,
            @FormParam("topicNames") String topicNames,
            @FormParam("sourceName") String sourceName
//            @DefaultValue("数据结构") @ApiParam(value = "领域名", required = true) @QueryParam("className") String className,
//            @DefaultValue("树状数组,图论术语") @ApiParam(value = "主题名字符串", required = true) @QueryParam("topicNames")
//                    String topicNames
    ) {
        Response response = null;
        List<AssembleFragment> assembleFragmentList = new ArrayList<AssembleFragment>();
        String[] topicNameArray = topicNames.split(",");

        /**
         * 循环所有主题
         */
        for (int i = 0; i < topicNameArray.length; i++) {

            /**
             * 读取spider_fragment，获得主题碎片
             */
            String topicName = topicNameArray[i];
            mysqlUtils mysql = new mysqlUtils();
            String sql = "select * from " + Config.ASSEMBLE_FRAGMENT_TABLE + " where ClassName=? and TermName=? and SourceName=?";
            List<Object> params = new ArrayList<Object>();
            params.add(className);
            params.add(topicName);
            params.add(sourceName);
            try {
                List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
                for (int j = 0; j < results.size(); j++) {
                    Map<String, Object> map = results.get(j);
                    AssembleFragment assembleFragment = new AssembleFragment();
                    assembleFragment.setFragmentID(Integer.parseInt(map.get("FragmentID").toString()));
                    assembleFragment.setFragmentContent(map.get("FragmentContent").toString());
                    assembleFragment.setText(map.get("Text").toString());
                    assembleFragment.setFragmentScratchTime(map.get("FragmentScratchTime").toString());
                    assembleFragment.setTermID(Integer.parseInt(map.get("TermID").toString()));
                    assembleFragment.setTermName(map.get("TermName").toString());
                    assembleFragment.setFacetName(map.get("FacetName").toString());
                    assembleFragment.setFacetLayer(Integer.parseInt(map.get("FacetLayer").toString()));
                    assembleFragment.setClassName(map.get("ClassName").toString());
                    assembleFragment.setSourceName(map.get("SourceName").toString());
                    assembleFragmentList.add(assembleFragment);
                }
            } catch (Exception e) {
                response = Response.status(401).entity(new error(e.toString())).build();
                e.printStackTrace();
            } finally {
                mysql.closeconnection();
            }
        }
        response = Response.status(200).entity(assembleFragmentList).build();

        return response;
    }

    @POST
    @Path("/getFragmentByTopicArray")
    @ApiOperation(value = "根据课程名和主题数组，获取主题下的碎片数据", notes = "根据课程名和主题数组，获取主题下的碎片数据")
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "MySql数据库  查询失败", response = String.class),
            @ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class)})
    @Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
    public static Response getFragmentByTopicArray(
            @FormParam("className") String className,
            @FormParam("topicNames") String topicNames
//            @DefaultValue("数据结构") @ApiParam(value = "领域名", required = true) @QueryParam("className") String className,
//            @DefaultValue("树状数组,图论术语") @ApiParam(value = "主题名字符串", required = true) @QueryParam("topicNames")
//                    String topicNames
    ) {
        Response response = null;
        List<AssembleFragment> assembleFragmentList = new ArrayList<AssembleFragment>();
        String[] topicNameArray = topicNames.split(",");

        /**
         * 循环所有主题
         */
        for (int i = 0; i < topicNameArray.length; i++) {

            /**
             * 读取spider_fragment，获得主题碎片
             */
            String topicName = topicNameArray[i];
            mysqlUtils mysql = new mysqlUtils();
            String sql = "select * from " + Config.ASSEMBLE_FRAGMENT_TABLE + " where ClassName=? and TermName=?";
            List<Object> params = new ArrayList<>();
            params.add(className);
            params.add(topicName);
            try {
                List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
                for (int j = 0; j < results.size(); j++) {
                    Map<String, Object> map = results.get(j);
                    AssembleFragment assembleFragment = new AssembleFragment();
                    assembleFragment.setFragmentID(Integer.parseInt(map.get("FragmentID").toString()));
                    assembleFragment.setFragmentContent(map.get("FragmentContent").toString());
                    assembleFragment.setText(map.get("Text").toString());
                    assembleFragment.setFragmentScratchTime(map.get("FragmentScratchTime").toString());
                    assembleFragment.setTermID(Integer.parseInt(map.get("TermID").toString()));
                    assembleFragment.setTermName(map.get("TermName").toString());
                    assembleFragment.setFacetName(map.get("FacetName").toString());
                    assembleFragment.setFacetLayer(Integer.parseInt(map.get("FacetLayer").toString()));
                    assembleFragment.setClassName(map.get("ClassName").toString());
                    assembleFragment.setSourceName(map.get("SourceName").toString());
                    assembleFragmentList.add(assembleFragment);
                }
            } catch (Exception e) {
                response = Response.status(401).entity(new error(e.toString())).build();
                e.printStackTrace();
            } finally {
                mysql.closeconnection();
            }
        }
        response = Response.status(200).entity(assembleFragmentList).build();

        return response;
    }

    @POST
    @Path("/getFragmentQuestionByTopicArrayAndSource")
    @ApiOperation(value = "获取主题下的碎片数据", notes = "根据课程名、数据源、主题数组，获取主题下的碎片数据")
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "MySql数据库  查询失败", response = String.class),
            @ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class)})
    @Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
    public static Response getFragmentQuestionByTopicArrayAndSource(
            @FormParam("className") String className,
            @FormParam("topicNames") String topicNames,
            @FormParam("sourceName") String sourceName
    ) {
        Response response = null;
        List<AssembleFragmentQuestionAndAsker> assembleFragmentList = new ArrayList<>();
        String[] topicNameArray = topicNames.split(",");

        /**
         * 循环所有主题
         */
        for (int i = 0; i < topicNameArray.length; i++) {

            /**
             * 读取spider_fragment，获得主题碎片
             */
            String topicName = topicNameArray[i];
            mysqlUtils mysql = new mysqlUtils();
            String sql = "SELECT\n" +
                    "af.*,\n" +
                    "afq.*\n" +
                    "FROM\n" +
                    Config.ASSEMBLE_FRAGMENT_TABLE + " AS af\n" +
                    "LEFT JOIN " +
                    Config.ASSEMBLE_FRAGMENT_QUESTION_TABLE +
                    " AS afq ON af.FragmentID = afq.fragment_id\n" +
                    "WHERE\n" +
                    "af.ClassName = ? AND\n" +
                    "af.TermName = ? AND\n" +
                    "af.SourceName = ?\n";
            List<Object> params = new ArrayList<Object>();
            params.add(className);
            params.add(topicName);
            params.add(sourceName);
            try {
                List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
                for (int j = 0; j < results.size(); j++) {
                    Map<String, Object> map = results.get(j);
                    AssembleFragmentQuestionAndAsker assembleFragment = new AssembleFragmentQuestionAndAsker();
                    assembleFragment.setFragmentID(Integer.parseInt(map.get("FragmentID").toString()));
                    assembleFragment.setFragmentContent(map.get("FragmentContent").toString());
                    assembleFragment.setText(map.get("Text").toString());
                    assembleFragment.setFragmentScratchTime(map.get("FragmentScratchTime").toString());
                    assembleFragment.setTermID(Integer.parseInt(map.get("TermID").toString()));
                    assembleFragment.setTermName(map.get("TermName").toString());
                    assembleFragment.setFacetName(map.get("FacetName").toString());
                    assembleFragment.setFacetLayer(Integer.parseInt(map.get("FacetLayer").toString()));
                    assembleFragment.setClassName(map.get("ClassName").toString());
                    assembleFragment.setSourceName(map.get("SourceName").toString());
                    if (!map.get("question_id").toString().equalsIgnoreCase("")) {
                        assembleFragment.setQuestion_id(Integer.parseInt(map.get("question_id").toString()));
                    }
                    assembleFragment.setPage_website_logo(map.get("page_website_logo").toString());
                    assembleFragment.setPage_search_url(map.get("page_search_url").toString());
                    assembleFragment.setPage_column_color(map.get("page_column_color").toString());
                    assembleFragment.setQuestion_url(map.get("question_url").toString());
                    assembleFragment.setQuestion_title(map.get("question_title").toString());
                    assembleFragment.setQuestion_title_pure(map.get("question_title_pure").toString());
                    assembleFragment.setQuestion_body(map.get("question_body").toString());
                    assembleFragment.setQuestion_body_pure(map.get("question_body_pure").toString());
                    assembleFragment.setQuestion_best_answer(map.get("question_best_answer").toString());
                    assembleFragment.setQuestion_best_answer_pure(map.get("question_best_answer_pure").toString());
                    assembleFragment.setQuestion_score(map.get("question_score").toString());
                    assembleFragment.setQuestion_answerCount(map.get("question_answerCount").toString());
                    assembleFragment.setQuestion_viewCount(map.get("question_viewCount").toString());
                    assembleFragment.setAsker_url(map.get("asker_url").toString());
                    assembleFragment.setAsker_name(map.get("asker_name").toString());
                    assembleFragment.setAsker_reputation(map.get("asker_reputation").toString());
                    assembleFragment.setAsker_answerCount(map.get("asker_answerCount").toString());
                    assembleFragment.setAsker_questionCount(map.get("asker_questionCount").toString());
                    assembleFragment.setAsker_viewCount(map.get("asker_viewCount").toString());
                    assembleFragment.setAsker_best_answer_rate(map.get("asker_best_answer_rate").toString());
                    assembleFragment.setQuestion_quality_label(map.get("question_quality_label").toString());
                    if (!map.get("fragment_id").toString().equalsIgnoreCase("")) {
                        assembleFragment.setFragment_id(Integer.parseInt(map.get("fragment_id").toString()));
                    }
                    assembleFragmentList.add(assembleFragment);
                }
            } catch (Exception e) {
                response = Response.status(401).entity(new error(e.toString())).build();
                e.printStackTrace();
            } finally {
                mysql.closeconnection();
            }
        }
        response = Response.status(200).entity(assembleFragmentList).build();

        return response;
    }

    @POST
    @Path("/getFragmentQuestionByTopicArray")
    @ApiOperation(value = "根据课程名和主题数组，获取主题下的碎片数据", notes = "根据课程名和主题数组，获取主题下的碎片数据")
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "MySql数据库  查询失败", response = String.class),
            @ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class)})
    @Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
    public static Response getFragmentQuestionByTopicArray(
            @FormParam("className") String className,
            @FormParam("topicNames") String topicNames
    ) {
        Response response = null;
        List<AssembleFragmentQuestionAndAsker> assembleFragmentList = new ArrayList<>();
        String[] topicNameArray = topicNames.split(",");

        /**
         * 循环所有主题
         */
        for (int i = 0; i < topicNameArray.length; i++) {

            /**
             * 读取spider_fragment，获得主题碎片
             */
            String topicName = topicNameArray[i];
            mysqlUtils mysql = new mysqlUtils();
            String sql = "SELECT\n" +
                    "af.*,\n" +
                    "afq.*\n" +
                    "FROM\n" +
                    Config.ASSEMBLE_FRAGMENT_TABLE + " AS af\n" +
                    "LEFT JOIN " +
                    Config.ASSEMBLE_FRAGMENT_QUESTION_TABLE +
                    " AS afq ON af.FragmentID = afq.fragment_id\n" +
                    "WHERE\n" +
                    "af.ClassName = ? AND\n" +
                    "af.TermName = ?\n";
            List<Object> params = new ArrayList<>();
            params.add(className);
            params.add(topicName);
            try {
                List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
                for (int j = 0; j < results.size(); j++) {
                    Map<String, Object> map = results.get(j);
                    AssembleFragmentQuestionAndAsker assembleFragment = new AssembleFragmentQuestionAndAsker();
                    assembleFragment.setFragmentID(Integer.parseInt(map.get("FragmentID").toString()));
                    assembleFragment.setFragmentContent(map.get("FragmentContent").toString());
                    assembleFragment.setText(map.get("Text").toString());
                    assembleFragment.setFragmentScratchTime(map.get("FragmentScratchTime").toString());
                    assembleFragment.setTermID(Integer.parseInt(map.get("TermID").toString()));
                    assembleFragment.setTermName(map.get("TermName").toString());
                    assembleFragment.setFacetName(map.get("FacetName").toString());
                    assembleFragment.setFacetLayer(Integer.parseInt(map.get("FacetLayer").toString()));
                    assembleFragment.setClassName(map.get("ClassName").toString());
                    assembleFragment.setSourceName(map.get("SourceName").toString());
                    if (!map.get("question_id").toString().equalsIgnoreCase("")) {
                        assembleFragment.setQuestion_id(Integer.parseInt(map.get("question_id").toString()));
                    }
                    assembleFragment.setPage_website_logo(map.get("page_website_logo").toString());
                    assembleFragment.setPage_search_url(map.get("page_search_url").toString());
                    assembleFragment.setPage_column_color(map.get("page_column_color").toString());
                    assembleFragment.setQuestion_url(map.get("question_url").toString());
                    assembleFragment.setQuestion_title(map.get("question_title").toString());
                    assembleFragment.setQuestion_title_pure(map.get("question_title_pure").toString());
                    assembleFragment.setQuestion_body(map.get("question_body").toString());
                    assembleFragment.setQuestion_body_pure(map.get("question_body_pure").toString());
                    assembleFragment.setQuestion_best_answer(map.get("question_best_answer").toString());
                    assembleFragment.setQuestion_best_answer_pure(map.get("question_best_answer_pure").toString());
                    assembleFragment.setQuestion_score(map.get("question_score").toString());
                    assembleFragment.setQuestion_answerCount(map.get("question_answerCount").toString());
                    assembleFragment.setQuestion_viewCount(map.get("question_viewCount").toString());
                    assembleFragment.setAsker_url(map.get("asker_url").toString());
                    assembleFragment.setAsker_name(map.get("asker_name").toString());
                    assembleFragment.setAsker_reputation(map.get("asker_reputation").toString());
                    assembleFragment.setAsker_answerCount(map.get("asker_answerCount").toString());
                    assembleFragment.setAsker_questionCount(map.get("asker_questionCount").toString());
                    assembleFragment.setAsker_viewCount(map.get("asker_viewCount").toString());
                    assembleFragment.setAsker_best_answer_rate(map.get("asker_best_answer_rate").toString());
                    assembleFragment.setQuestion_quality_label(map.get("question_quality_label").toString());
                    if (!map.get("fragment_id").toString().equalsIgnoreCase("")) {
                        assembleFragment.setFragment_id(Integer.parseInt(map.get("fragment_id").toString()));
                    }
                    assembleFragmentList.add(assembleFragment);
                }
            } catch (Exception e) {
                response = Response.status(401).entity(new error(e.toString())).build();
                e.printStackTrace();
            } finally {
                mysql.closeconnection();
            }
        }
        response = Response.status(200).entity(assembleFragmentList).build();

        return response;
    }

    @GET
    @Path("/getDomainTerm")
    @ApiOperation(value = "获得指定领域下主题的信息", notes = "获得指定领域下主题的信息")
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "MySql数据库  查询失败"),
            @ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class)})
    @Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
    public static Response getDomainTerm(@ApiParam(value = "课程名字", required = true) @QueryParam("ClassName") String ClassName) {
        Response response = null;
        /**
         * 根据指定领域，获得该领域下的所有主题信息
         */
        mysqlUtils mysql = new mysqlUtils();
        String sql = "select * from " + Config.DOMAIN_TOPIC_TABLE + " where ClassName=?";
        List<Object> params = new ArrayList<Object>();
        params.add(ClassName);
        try {
            List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
            response = Response.status(200).entity(results).build();
        } catch (Exception e) {
            e.printStackTrace();
            response = Response.status(401).entity(new error(e.toString())).build();
        } finally {
            mysql.closeconnection();
        }
        return response;
    }

    @GET
    @Path("/getDomainTermFacet1")
    @ApiOperation(value = "获得指定领域下指定主题的一级分面信息", notes = "获得指定领域下指定主题的一级分面信息")
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "MySql数据库  查询失败"),
            @ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class)})
    @Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
    public static Response getDomainTermFacet1(@ApiParam(value = "课程名字", required = true) @QueryParam("ClassName") String ClassName, @ApiParam(value = "主题名字", required = true) @QueryParam("TermName") String TermName) {
        Response response = null;
        /**
         * 根据指定领域和指定主题，获得该主题下的所有一级分面信息
         */
        mysqlUtils mysql = new mysqlUtils();
        String sql = "select * from " + Config.FACET_TABLE + " where ClassName=? and TermName=? and FacetLayer='1'";
        List<Object> params = new ArrayList<Object>();
        params.add(ClassName);
        params.add(TermName);
        try {
            List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
            response = Response.status(200).entity(results).build();
        } catch (Exception e) {
            e.printStackTrace();
            response = Response.status(401).entity(new error(e.toString())).build();
        } finally {
            mysql.closeconnection();
        }
        return response;
    }

    @GET
    @Path("/getDomainTermFacet2")
    @ApiOperation(value = "获得指定领域下指定主题一级分面下的二级分面信息", notes = "获得指定领域下指定主题一级分面下的二级分面信息")
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "MySql数据库  查询失败"),
            @ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class)})
    @Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
    public static Response getDomainTermFacet2(@ApiParam(value = "课程名字", required = true) @QueryParam("ClassName") String ClassName, @ApiParam(value = "主题名字", required = true) @QueryParam("TermName") String TermName, @ApiParam(value = "一级分面名字", required = true) @QueryParam("Facet1Name") String Facet1Name) {
        Response response = null;
        /**
         * 获得指定领域下指定主题一级分面下的二级分面信息
         */
        mysqlUtils mysql = new mysqlUtils();
        String sql = "select * from " + Config.FACET_RELATION_TABLE + " where ClassName=? and TermName=? and ParentFacet=? and ParentLayer='1'";
        List<Object> params = new ArrayList<Object>();
        params.add(ClassName);
        params.add(TermName);
        params.add(Facet1Name);
        try {
            List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
            response = Response.status(200).entity(results).build();
        } catch (Exception e) {
            e.printStackTrace();
            response = Response.status(401).entity(new error(e.toString())).build();
        } finally {
            mysql.closeconnection();
        }
        return response;
    }


    @GET
    @Path("/getDomainTermFacet3")
    @ApiOperation(value = "获得指定领域下指定主题二级分面下的三级分面信息", notes = "获得指定领域下指定主题二级分面下的三级分面信息")
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "MySql数据库  查询失败"),
            @ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class)})
    @Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
    public static Response getDomainTermFacet3(@ApiParam(value = "课程名字", required = true) @QueryParam("ClassName") String ClassName, @ApiParam(value = "主题名字", required = true) @QueryParam("TermName") String TermName, @ApiParam(value = "二级分面名字", required = true) @QueryParam("Facet2Name") String Facet2Name) {
        Response response = null;
        /**
         * 获得指定领域下指定主题二级分面下的三级分面信息
         */
        mysqlUtils mysql = new mysqlUtils();
        String sql = "select * from " + Config.FACET_RELATION_TABLE + " where ClassName=? and TermName=? and ParentFacet=? and ParentLayer='2'";
        List<Object> params = new ArrayList<Object>();
        params.add(ClassName);
        params.add(TermName);
        params.add(Facet2Name);
        try {
            List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
            response = Response.status(200).entity(results).build();
        } catch (Exception e) {
            e.printStackTrace();
            response = Response.status(401).entity(new error(e.toString())).build();
        } finally {
            mysql.closeconnection();
        }
        return response;
    }


    @GET
    @Path("/getDomainTermFragment")
    @ApiOperation(value = "获得指定领域下指定主题的碎片信息", notes = "获得指定领域下指定主题的碎片信息")
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "MySql数据库  查询失败"),
            @ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class)})
    @Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
    public static Response getDomainTermFragment(@ApiParam(value = "课程名字", required = true) @QueryParam("ClassName") String ClassName, @ApiParam(value = "主题名字", required = true) @QueryParam("TermName") String TermName) {
        Response response = null;
        /**
         * 根据指定领域和指定主题，获得该主题下的所有碎片信息
         */
        mysqlUtils mysql = new mysqlUtils();
        String sql = "select * from " + Config.ASSEMBLE_FRAGMENT_TABLE + " where ClassName=? and TermName=?";
        List<Object> params = new ArrayList<Object>();
        params.add(ClassName);
        params.add(TermName);
        try {
            List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
            response = Response.status(200).entity(results).build();
        } catch (Exception e) {
            e.printStackTrace();
            response = Response.status(401).entity(new error(e.toString())).build();
        } finally {
            mysql.closeconnection();
        }
        return response;
    }


    @GET
    @Path("/getDomainTermFacet1Fragment")
    @ApiOperation(value = "获得指定领域下指定主题一级分面的碎片信息", notes = "获得指定领域下指定主题一级分面的碎片信息")
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "MySql数据库  查询失败"),
            @ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class)})
    @Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
    public static Response getDomainTermFacet1Fragment(@ApiParam(value = "课程名字", required = true) @QueryParam("ClassName") String ClassName, @ApiParam(value = "主题名字", required = true) @QueryParam("TermName") String TermName, @ApiParam(value = "分面名字", required = true) @QueryParam("FacetName") String FacetName) {
        Response response = null;
        /**
         * 根据指定领域和指定主题，获得该主题下一级分面的碎片信息
         */
        mysqlUtils mysql = new mysqlUtils();
        String sql = "select * from " + Config.ASSEMBLE_FRAGMENT_TABLE + " where ClassName=? and TermName=? and FacetName=? and FacetLayer=?";


        String sql_facet2 = "select * from " + Config.FACET_RELATION_TABLE + " where ClassName=? and TermName=? and ParentFacet=? and ParentLayer='1'";
        String sql_facet3 = "select * from " + Config.FACET_RELATION_TABLE + " where ClassName=? and TermName=? and ParentFacet=? and ParentLayer='2'";
        List<Object> params_facet2 = new ArrayList<Object>();
        params_facet2.add(ClassName);
        params_facet2.add(TermName);
        params_facet2.add(FacetName);
        try {
            List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
            List<Map<String, Object>> results_facet2 = mysql.returnMultipleResult(sql_facet2, params_facet2);
            List<Map<String, Object>> results_finalfacet = new ArrayList<Map<String, Object>>();
            Map<String, Object> facet1 = new HashMap<String, Object>();
            facet1.put("ClassName", ClassName);
            facet1.put("TermName", TermName);
            facet1.put("FacetName", FacetName);
            facet1.put("FacetLayer", 1);
            results_finalfacet.add(facet1);
            for (int i = 0; i < results_facet2.size(); i++) {
                Map<String, Object> facet2 = new HashMap<String, Object>();
                facet2.put("ClassName", ClassName);
                facet2.put("TermName", TermName);
                facet2.put("FacetName", results_facet2.get(i).get("ChildFacet"));
                facet2.put("FacetLayer", 2);
                results_finalfacet.add(facet2);

                List<Object> params_facet3 = new ArrayList<Object>();
                params_facet3.add(ClassName);
                params_facet3.add(TermName);
                params_facet3.add(results_facet2.get(i).get("ChildFacet"));
                try {
                    List<Map<String, Object>> results_facet3 = mysql.returnMultipleResult(sql_facet3, params_facet3);
                    for (int j = 0; j < results_facet3.size(); j++) {
                        Map<String, Object> facet3 = new HashMap<String, Object>();
                        facet3.put("ClassName", ClassName);
                        facet3.put("TermName", TermName);
                        facet3.put("FacetName", results_facet3.get(j).get("ChildFacet"));
                        facet3.put("FacetLayer", 3);
                        results_finalfacet.add(facet3);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            for (int k = 0; k < results_finalfacet.size(); k++) {
                List<Object> params_fragment = new ArrayList<Object>();
                params_fragment.add(results_finalfacet.get(k).get("ClassName"));
                params_fragment.add(results_finalfacet.get(k).get("TermName"));
                params_fragment.add(results_finalfacet.get(k).get("FacetName"));
                params_fragment.add(results_finalfacet.get(k).get("FacetLayer"));
                try {
                    results.addAll(mysql.returnMultipleResult(sql, params_fragment));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            response = Response.status(200).entity(results).build();
        } catch (Exception e) {
            e.printStackTrace();
            response = Response.status(401).entity(new error(e.toString())).build();
        } finally {
            mysql.closeconnection();
        }
        return response;
    }


    @GET
    @Path("/getDomainTermFacet2Fragment")
    @ApiOperation(value = "获得指定领域下指定主题二级分面的碎片信息", notes = "获得指定领域下指定主题二级分面的碎片信息")
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "MySql数据库  查询失败"),
            @ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class)})
    @Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
    public static Response getDomainTermFacet2Fragment(@ApiParam(value = "课程名字", required = true) @QueryParam("ClassName") String ClassName, @ApiParam(value = "主题名字", required = true) @QueryParam("TermName") String TermName, @ApiParam(value = "分面名字", required = true) @QueryParam("FacetName") String FacetName) {
        Response response = null;
        /**
         * 根据指定领域和指定主题，获得该主题下二级分面的碎片信息
         */
        mysqlUtils mysql = new mysqlUtils();
        String sql = "select * from " + Config.ASSEMBLE_FRAGMENT_TABLE + " where ClassName=? and TermName=? and FacetName=? and FacetLayer=?";

        String sql_facet3 = "select * from " + Config.FACET_RELATION_TABLE + " where ClassName=? and TermName=? and ParentFacet=? and ParentLayer='2'";
        List<Object> params_facet3 = new ArrayList<Object>();
        params_facet3.add(ClassName);
        params_facet3.add(TermName);
        params_facet3.add(FacetName);
        try {
            List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
            List<Map<String, Object>> results_facet3 = mysql.returnMultipleResult(sql_facet3, params_facet3);
            List<Map<String, Object>> results_finalfacet = new ArrayList<Map<String, Object>>();
            Map<String, Object> facet2 = new HashMap<String, Object>();
            facet2.put("ClassName", ClassName);
            facet2.put("TermName", TermName);
            facet2.put("FacetName", FacetName);
            facet2.put("FacetLayer", 2);
            results_finalfacet.add(facet2);
            for (int i = 0; i < results_facet3.size(); i++) {
                Map<String, Object> facet3 = new HashMap<String, Object>();
                facet3.put("ClassName", ClassName);
                facet3.put("TermName", TermName);
                facet3.put("FacetName", results_facet3.get(i).get("ChildFacet"));
                facet3.put("FacetLayer", 3);
                results_finalfacet.add(facet3);
            }
            for (int k = 0; k < results_finalfacet.size(); k++) {
                List<Object> params_fragment = new ArrayList<Object>();
                params_fragment.add(results_finalfacet.get(k).get("ClassName"));
                params_fragment.add(results_finalfacet.get(k).get("TermName"));
                params_fragment.add(results_finalfacet.get(k).get("FacetName"));
                params_fragment.add(results_finalfacet.get(k).get("FacetLayer"));
                try {
                    results.addAll(mysql.returnMultipleResult(sql, params_fragment));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            response = Response.status(200).entity(results).build();
        } catch (Exception e) {
            e.printStackTrace();
            response = Response.status(401).entity(new error(e.toString())).build();
        } finally {
            mysql.closeconnection();
        }
        return response;
    }


    @GET
    @Path("/getDomainTermFacet3Fragment")
    @ApiOperation(value = "获得指定领域下指定主题三级分面的碎片信息", notes = "获得指定领域下指定主题三级分面的碎片信息")
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "MySql数据库  查询失败"),
            @ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class)})
    @Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
    public static Response getDomainTermFacet3Fragment(@ApiParam(value = "课程名字", required = true) @QueryParam("ClassName") String ClassName, @ApiParam(value = "主题名字", required = true) @QueryParam("TermName") String TermName, @ApiParam(value = "分面名字", required = true) @QueryParam("FacetName") String FacetName) {
        Response response = null;
        /**
         * 根据指定领域和指定主题，获得该主题下三级分面的碎片信息
         */
        mysqlUtils mysql = new mysqlUtils();
        String sql = "select * from " + Config.ASSEMBLE_FRAGMENT_TABLE + " where ClassName=? and TermName=? and FacetName=? and FacetLayer=?";
        List<Object> params_fragment = new ArrayList<Object>();
        params_fragment.add(ClassName);
        params_fragment.add(TermName);
        params_fragment.add(FacetName);
        params_fragment.add(3);
        try {
            List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
            results = mysql.returnMultipleResult(sql, params_fragment);
            response = Response.status(200).entity(results).build();
        } catch (Exception e) {
            e.printStackTrace();
            response = Response.status(401).entity(new error(e.toString())).build();
        } finally {
            mysql.closeconnection();
        }
        return response;
    }

    @POST
    @Path("/updateFragment")
    @ApiOperation(value = "更新未装配碎片", notes = "更新未装配碎片")
    @ApiResponses(value = {
            @ApiResponse(code = 402, message = "数据库错误", response = error.class),
            @ApiResponse(code = 200, message = "正常返回结果", response = success.class)})
    @Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
    public static Response updateFragment(
            @FormParam("FragmentID") String FragmentID,
            @FormParam("UserName") String UserName,
            @FormParam("FragmentContent") String FragmentContent,
            @FormParam("FragmentUrl") String FragmentUrl,
            @FormParam("SourceName") String SourceName
    ) {
        /**
         * 创建碎片
         */
        boolean result = false;
        mysqlUtils mysql = new mysqlUtils();
        String sql = "update " + Config.FRAGMENT +
                " set UserName = ?," +
                " FragmentContent = ?, " +
                " FragmentUrl = ?, " +
                " SourceName = ? " +
                " where FragmentID = ?";
        List<Object> params = new ArrayList<Object>();
        params.add(UserName);
        params.add(FragmentContent);
        params.add(FragmentUrl);
        params.add(SourceName);
        params.add(FragmentID);
        try {
            result = mysql.addDeleteModify(sql, params);
            if (result) {
                return Response.status(200).entity(new success("碎片更新成功~")).build();
            } else {
                return Response.status(401).entity(new error("碎片更新失败~")).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(402).entity(new error(e.getMessage())).build();
        } finally {
            mysql.closeconnection();
        }
    }

    @POST
    @Path("/createFragment")
    @ApiOperation(value = "创建碎片", notes = "创建碎片")
    @ApiResponses(value = {
            @ApiResponse(code = 402, message = "数据库错误", response = error.class),
            @ApiResponse(code = 200, message = "正常返回结果", response = success.class)})
    @Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
    public static Response createFragment(
            @FormParam("FragmentContent") String FragmentContent,
            @FormParam("UserName") String UserName,
            @FormParam("FragmentUrl") String FragmentUrl,
            @FormParam("SourceName") String SourceName
    ) {
//		Response response = null;
        /**
         * 创建碎片
         */
        try {
            boolean result = false;
            mysqlUtils mysql = new mysqlUtils();
            String sql = "insert into " + Config.FRAGMENT +
                    "(FragmentContent,FragmentScratchTime,UserName,FragmentUrl,SourceName) values(?,?,?,?,?);";
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            List<Object> params = new ArrayList<Object>();
            params.add(FragmentContent);
            params.add(sdf.format(d));
            params.add(UserName);
            params.add(FragmentUrl);
            params.add(SourceName);
            try {
                result = mysql.addDeleteModify(sql, params);
            } catch (Exception e) {
                e.printStackTrace();
                return Response.status(401).entity(new error(e.getMessage())).build();
            } finally {
                mysql.closeconnection();
            }
            if (result) {
                return Response.status(200).entity(new success("碎片创建成功~")).build();
            } else {
                return Response.status(401).entity(new error("碎片创建失败~")).build();
            }
        } catch (Exception e) {
            return Response.status(402).entity(new error(e.toString())).build();
        }
    }

    @GET
    @Path("/getFragment")
    @ApiOperation(value = "获得碎片信息", notes = "获得碎片信息")
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "MySql数据库  查询失败"),
            @ApiResponse(code = 200, message = "MySql数据库  查询成功", response = String.class)})
    @Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
    public static Response getFragment(@QueryParam("UserName") String UserName) {
        Response response = null;
        /**
         * 获得碎片信息
         */
        mysqlUtils mysql = new mysqlUtils();
        String sql = "select * from " + Config.FRAGMENT + " where UserName=?";
        List<Object> params = new ArrayList<Object>();
        params.add(UserName);

        try {
            List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
            response = Response.status(200).entity(results).build();
        } catch (Exception e) {
            e.printStackTrace();
            response = Response.status(401).entity(new error(e.toString())).build();
        } finally {
            mysql.closeconnection();
        }
        return response;
    }


    @POST
    @Path("/createImageFragment")
    @ApiOperation(value = "插入图片", notes = "插入图片")
    @ApiResponses(value = {
            @ApiResponse(code = 402, message = "数据库错误", response = error.class),
            @ApiResponse(code = 200, message = "正常返回结果", response = success.class)})
    @Consumes(MediaType.MULTIPART_FORM_DATA + ";charset=" + "UTF-8")
    @Produces(MediaType.TEXT_PLAIN + ";charset=" + "UTF-8")
    public static Response createImageFragment(
            @FormDataParam("imageContent") FormDataContentDisposition disposition,
            @FormDataParam("imageContent") InputStream imageContent) {

        Response response = null;
        mysqlUtils mysql = new mysqlUtils();

        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


//		String sqlFragment="select * from "+Config.UNADD_IMAGE+" where ImageUrl=?";
        String sqlAdd = "insert into " + Config.UNADD_IMAGE + "(ImageUrl,ImageContent,ImageAPI, ImageScratchTime) values (?, ?, ?,?)";
        String sqlImageID = "select * from " + Config.UNADD_IMAGE + " where ImageUrl=?";
        String sqlApi = "update " + Config.UNADD_IMAGE + " set ImageAPI=? where ImageUrl=?";
        List<Object> paramsAdd = new ArrayList<Object>();
        List<Object> paramsImageID = new ArrayList<Object>();
        List<Object> paramsApi = new ArrayList<Object>();
        paramsAdd.add("http://image.baidu.com/" + disposition.getFileName());
        paramsAdd.add(imageContent);
        paramsAdd.add("");
        paramsAdd.add(sdf.format(d));
        paramsImageID.add("http://image.baidu.com/" + disposition.getFileName());
//		paramsApi.add(e);
//		paramsApi.add("http://image.baidu.com/" + disposition.getFileName());
        List<Map<String, Object>> resultFragment = new ArrayList<Map<String, Object>>();

        try {
            resultFragment = mysql.returnMultipleResult(sqlImageID, paramsImageID);
            if (resultFragment.size() == 0) {
                try {
                    mysql.addDeleteModify(sqlAdd, paramsAdd);
                    try {
                        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
                        result = mysql.returnMultipleResult(sqlImageID, paramsImageID);
                        paramsApi.add(Config.IP2 + "/SpiderAPI/getUnaddImage?imageID=" + result.get(0).get("ImageID"));
                        paramsApi.add("http://image.baidu.com/" + disposition.getFileName());
                        try {
                            mysql.addDeleteModify(sqlApi, paramsApi);
                            response = Response.status(200).entity(paramsApi.get(0)).build();
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                } catch (Exception e0) {
                    e0.printStackTrace();
                }
            } else {
                response = Response.status(200).entity(resultFragment.get(0).get("ImageAPI")).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            response = Response.status(402).entity(new error("MySql数据库  更新失败")).build();
        } finally {
            mysql.closeconnection();
        }
        return response;
    }

    @GET
    @Path("/getUnaddImage")
    @ApiOperation(value = "读取图片数据表中数据到成API", notes = "输入图片ID，得到对应API")
    @ApiResponses(value = {@ApiResponse(code = 401, message = "MySql数据库  图片内容查询失败"),
            @ApiResponse(code = 200, message = "MySql数据库  图片数据表检查处理完成", response = String.class)})
    @Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
    @Produces(MediaType.APPLICATION_OCTET_STREAM + ";charset=" + "UTF-8")
    public static Response getImage(@ApiParam(value = "图片ID", required = true) @QueryParam("imageID") int imageID) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        try {
            mysqlUtils mysql = new mysqlUtils();
            String sql = "select * from " + Config.UNADD_IMAGE + " where ImageID=?";
            List<Object> params = new ArrayList<Object>();
            params.add(imageID);
            try {
                result = mysql.returnMultipleResult(sql, params);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mysql.closeconnection();
            }
            String imageUrl = (String) result.get(0).get("ImageUrl");
            String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            Object imageContent = result.get(0).get("ImageContent");
            return Response.status(200).header("Content-disposition", "attachment; " + "filename=" + filename).entity(imageContent).build();
        } catch (Exception e) {
            return Response.status(402).entity(new error(e.toString())).build();
        }
    }


    @GET
    @Path("/addFacetFragment")
    @ApiOperation(value = "向分面添加碎片", notes = "向分面添加碎片")
    @ApiResponses(value = {
            @ApiResponse(code = 402, message = "数据库错误", response = error.class),
            @ApiResponse(code = 200, message = "正常返回结果", response = success.class)})
    @Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
    public static Response addFacetFragment(
            @ApiParam(value = "课程名字", required = true) @QueryParam("ClassName") String ClassName,
            @ApiParam(value = "主题名字", required = true) @QueryParam("TermName") String TermName,
            @ApiParam(value = "分面名字", required = true) @QueryParam("FacetName") String FacetName,
            @ApiParam(value = "分面级数", required = true) @QueryParam("FacetLayer") String FacetLayer,
            @ApiParam(value = "FragmentID", required = true) @QueryParam("FragmentID") String FragmentID
    ) {
//		Response response = null;
        /**
         * 向分面添加碎片
         */
        try {
            boolean result = false;
            mysqlUtils mysql = new mysqlUtils();
            String sql_term = "select * from " + Config.DOMAIN_TOPIC_TABLE + " where ClassName=? and TermName=?";
            String sql_query = "select * from " + Config.FRAGMENT + " where FragmentID=?";
            String sql_delete = "delete from " + Config.FRAGMENT + " where FragmentID=?";
            String sql_add = "insert into " + Config.ASSEMBLE_FRAGMENT_TABLE + "(FragmentContent,Text,FragmentScratchTime,FragmentUrl, UserName,TermID,TermName,FacetName,FacetLayer,ClassName,SourceName) values(?,?,?,?,?,?,?,?,?,?,?);";

            List<Object> params_term = new ArrayList<Object>();
            params_term.add(ClassName);
            params_term.add(TermName);
            List<Object> params_fragment = new ArrayList<Object>();
            params_fragment.add(FragmentID);
            try {
                List<Map<String, Object>> results_term = mysql.returnMultipleResult(sql_term, params_term);
                List<Map<String, Object>> fragmentinfo = mysql.returnMultipleResult(sql_query, params_fragment);
                List<Object> params_add = new ArrayList<Object>();
                params_add.add(fragmentinfo.get(0).get("FragmentContent"));
                params_add.add(JsoupDao.parseHtmlText(fragmentinfo.get(0).get("FragmentContent").toString()).text());
                params_add.add(fragmentinfo.get(0).get("FragmentScratchTime"));
                params_add.add(fragmentinfo.get(0).get("FragmentUrl"));
                params_add.add(fragmentinfo.get(0).get("UserName"));
                params_add.add(results_term.get(0).get("TermID"));
                params_add.add(TermName);
                params_add.add(FacetName);
                params_add.add(FacetLayer);
                params_add.add(ClassName);
                params_add.add(fragmentinfo.get(0).get("SourceName"));
                result = mysql.addDeleteModify(sql_add, params_add);
                if (result) {
                    try {
                        mysql.addDeleteModify(sql_delete, params_fragment);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mysql.closeconnection();
            }
            if (result) {
                return Response.status(200).entity(new success("碎片添加成功~")).build();
            } else {
                return Response.status(401).entity(new error("碎片添加失败~")).build();
            }
        } catch (Exception e) {
            return Response.status(402).entity(new error(e.toString())).build();
        }

    }


    @GET
    @Path("/deleteUnaddFragment")
    @ApiOperation(value = "删除未挂接的碎片", notes = "删除未挂接的碎片")
    @ApiResponses(value = {
            @ApiResponse(code = 402, message = "数据库错误", response = error.class),
            @ApiResponse(code = 200, message = "正常返回结果", response = success.class)})
    @Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
    public static Response deleteUnaddFragment(@ApiParam(value = "FragmentID", required = true) @QueryParam("FragmentID") String FragmentID) {
        /**
         * 删除未挂接的碎片
         */
        try {
            boolean result = false;
            mysqlUtils mysql = new mysqlUtils();
            String sql = "delete from " + Config.FRAGMENT + " where FragmentID=?;";
            List<Object> params = new ArrayList<Object>();
            params.add(FragmentID);
            try {
                result = mysql.addDeleteModify(sql, params);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mysql.closeconnection();
            }
            if (result) {
                return Response.status(200).entity(new success("碎片删除成功~")).build();
            } else {
                return Response.status(401).entity(new error("碎片删除失败~")).build();
            }
        } catch (Exception e) {
            return Response.status(402).entity(new error(e.toString())).build();
        }
    }


    @GET
    @Path("/deleteFragment")
    @ApiOperation(value = "删除碎片", notes = "删除碎片")
    @ApiResponses(value = {
            @ApiResponse(code = 402, message = "数据库错误", response = error.class),
            @ApiResponse(code = 200, message = "正常返回结果", response = success.class)})
    @Consumes("application/x-www-form-urlencoded" + ";charset=" + "UTF-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=" + "UTF-8")
    public static Response deleteFragment(@ApiParam(value = "FragmentID", required = true) @QueryParam("FragmentID") String FragmentID) {
        /**
         * 删除碎片
         */
        try {

            mysqlUtils mysqlQuery = new mysqlUtils();
            String sqlQuery = "select * from " + Config.ASSEMBLE_FRAGMENT_TABLE + " where FragmentID=?;";
            List<Object> paramsQuery = new ArrayList<Object>();
            paramsQuery.add(FragmentID);
            try {
                List<Map<String, Object>> result = mysqlQuery.returnMultipleResult(sqlQuery, paramsQuery);
                String sqlInsert = "insert into " + Config.FRAGMENT + " (FragmentContent, FragmentScratchTime, FragmentUrl, UserName, SourceName) values(?,?,?,?,?);";
                List<Object> paramsInsert = new ArrayList<Object>();
                paramsInsert.add(result.get(0).get("FragmentContent").toString());
                paramsInsert.add(result.get(0).get("FragmentScratchTime").toString());
                paramsInsert.add(result.get(0).get("FragmentUrl").toString());
                paramsInsert.add(result.get(0).get("UserName").toString());
                paramsInsert.add(result.get(0).get("SourceName").toString());
                mysqlQuery.addDeleteModify(sqlInsert, paramsInsert);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mysqlQuery.closeconnection();
            }

            boolean result = false;
            mysqlUtils mysql = new mysqlUtils();
            String sql = "delete from " + Config.ASSEMBLE_FRAGMENT_TABLE + " where FragmentID=?;";
            List<Object> params = new ArrayList<Object>();
            params.add(FragmentID);
            try {
                result = mysql.addDeleteModify(sql, params);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mysql.closeconnection();
            }
            if (result) {
                return Response.status(200).entity(new success("碎片删除成功~")).build();
            } else {
                return Response.status(401).entity(new error("碎片删除失败~")).build();
            }
        } catch (Exception e) {
            return Response.status(402).entity(new error(e.toString())).build();
        }
    }

}
