package com.xjtu.dependency.service;


import com.xjtu.assemble.domain.Assemble;
import com.xjtu.assemble.repository.AssembleRepository;
import com.xjtu.common.Config;
import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.dependency.RankDependency.GetAsymmetry;
import com.xjtu.dependency.RankDependency.RankDependency;
import com.xjtu.dependency.domain.Dependency;
import com.xjtu.dependency.domain.DependencyContainName;
import com.xjtu.dependency.repository.DependencyRepository;
import com.xjtu.domain.domain.Domain;
import com.xjtu.domain.repository.DomainRepository;
import com.xjtu.topic.domain.Topic;
import com.xjtu.topic.domain.TopicContainAssembleText;
import com.xjtu.topic.repository.TopicRepository;
import com.xjtu.utils.ResultUtil;
import com.xjtu.utils.CsvUtil;
import org.apache.commons.io.FileUtils;
import org.gephi.appearance.api.*;
import org.gephi.appearance.plugin.PartitionElementColorTransformer;
import org.gephi.appearance.plugin.RankingElementColorTransformer;
import org.gephi.appearance.plugin.RankingLabelSizeTransformer;
import org.gephi.appearance.plugin.RankingNodeSizeTransformer;
import org.gephi.appearance.plugin.palette.Palette;
import org.gephi.appearance.plugin.palette.PaletteManager;
import org.gephi.filters.api.FilterController;
import org.gephi.filters.api.Query;
import org.gephi.filters.api.Range;
import org.gephi.filters.plugin.graph.DegreeRangeBuilder;
import org.gephi.graph.api.*;
import org.gephi.io.database.drivers.MySQLDriver;
import org.gephi.io.exporter.api.ExportController;
import org.gephi.io.exporter.spi.CharacterExporter;
import org.gephi.io.exporter.spi.Exporter;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.EdgeDirectionDefault;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.importer.plugin.database.EdgeListDatabaseImpl;
import org.gephi.io.importer.plugin.database.ImporterEdgeList;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.layout.plugin.AutoLayout;
import org.gephi.layout.plugin.force.StepDisplacement;
import org.gephi.layout.plugin.force.yifanHu.YifanHuLayout;
import org.gephi.layout.plugin.forceAtlas.ForceAtlasLayout;
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewModel;
import org.gephi.preview.api.PreviewProperty;
import org.gephi.preview.types.EdgeColor;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.statistics.plugin.GraphDistance;
import org.gephi.statistics.plugin.Modularity;
import org.openide.util.Lookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 处理主题依赖关系数据
 *
 * @author yangkuan
 * @date 2018/03/21 12:46
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DependencyService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DependencyRepository dependencyRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private DomainRepository domainRepository;

    @Autowired
    private AssembleRepository assembleRepository;

    @Value("${gexfpath}")
    private String gexfPath;

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    /**
     * 通过主课程名，在课程下的插入、添加主题依赖关系
     *
     * @param domainName     课程名
     * @param startTopicName 起始主题名
     * @param endTopicName   终止主题名
     * @return
     */
    public Result insertDependency(String domainName, String startTopicName, String endTopicName) {
        Domain domain = domainRepository.findByDomainName(domainName);
        //查询课程错误
        if (domain == null) {
            logger.error("主题依赖关系插入失败：没有课程信息记录");
            return ResultUtil.error(ResultEnum.DEPENDENCY_INSERT_ERROR_1.getCode(), ResultEnum.DEPENDENCY_INSERT_ERROR_1.getMsg());
        }
        if (startTopicName.equals(endTopicName)) {
            logger.error("主题依赖关系插入失败:起始和终止主题重名");
            return ResultUtil.error(ResultEnum.DEPENDENCY_INSERT_ERROR_4.getCode(), ResultEnum.DEPENDENCY_INSERT_ERROR_4.getMsg());
        }
        //查找主题id
        Topic startTopic = topicRepository.findByDomainIdAndTopicName(domain.getDomainId(), startTopicName);
        Topic endTopic = topicRepository.findByDomainIdAndTopicName(domain.getDomainId(), endTopicName);
        if (startTopic == null || endTopic == null) {
            logger.error("主题依赖关系插入失败:起始或终止主题不存在");
            return ResultUtil.error(ResultEnum.DEPENDENCY_INSERT_ERROR_2.getCode(), ResultEnum.DEPENDENCY_INSERT_ERROR_2.getMsg());
        }
        //查找是否已有依赖关系
        Dependency existedDependency = dependencyRepository.findByStartTopicIdAndEndTopicId(startTopic.getTopicId(), endTopic.getTopicId());
        if (existedDependency != null) {
            logger.error("主题依赖关系插入失败:主题依赖关系已经存在");
            return ResultUtil.error(ResultEnum.DEPENDENCY_INSERT_ERROR_3.getCode(), ResultEnum.DEPENDENCY_INSERT_ERROR_3.getMsg());
        }
        //插入依赖关系
        Dependency dependency = new Dependency(startTopic.getTopicId(), endTopic.getTopicId(), 0, domain.getDomainId());
        try {
            dependencyRepository.save(dependency);
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "主题依赖关系插入成功");
        } catch (Exception exception) {
            logger.error("主题依赖关系插入失败:插入语句执行失败");
            return ResultUtil.error(ResultEnum.DEPENDENCY_INSERT_ERROR.getCode(), ResultEnum.DEPENDENCY_INSERT_ERROR.getMsg());
        }
    }

    /**
     * 通过主课程名，起始、终止主题id删除依赖关系
     *
     * @param domainName   课程名
     * @param startTopicId 起始主题id
     * @param endTopicId   终止主题id
     * @return
     */
    public Result deleteDependency(String domainName, Long startTopicId, Long endTopicId) {
        Domain domain = domainRepository.findByDomainName(domainName);
        //查询课程错误
        if (domain == null) {
            logger.error("主题依赖关系删除失败：没有课程信息记录");
            return ResultUtil.error(ResultEnum.DEPENDENCY_DELETE_ERROR.getCode(), ResultEnum.DEPENDENCY_DELETE_ERROR.getMsg());
        }
        try {
            dependencyRepository.deleteByDomainIdAndStartTopicIdAndEndTopicId(domain.getDomainId(), startTopicId, endTopicId);
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "主题依赖关系删除成功");
        } catch (Exception exception) {
            logger.error("主题依赖关系删除失败：删除语句执行失败", exception);
            return ResultUtil.error(ResultEnum.DEPENDENCY_DELETE_ERROR_1.getCode(), ResultEnum.DEPENDENCY_DELETE_ERROR_1.getMsg());
        }

    }


    /**
     * 通过课程名删除课程下所有主题依赖关系
     * @param   domainName    课程名
     * @return
     * @author  Qi Jingchao
     */
    public Result deleteDependenciesByDomainName(String domainName) {
        Domain domain = domainRepository.findByDomainName(domainName);
        if (domain == null) {
            logger.error("主题依赖关系删除失败：没有课程信息记录");
            return ResultUtil.error(ResultEnum.DEPENDENCY_DELETE_ERROR.getCode(), ResultEnum.DEPENDENCY_DELETE_ERROR.getMsg());
        }
        try {
            dependencyRepository.deleteDependenciesByDomainId(domain.getDomainId());
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "根据课程名删除依赖关系成功");
        } catch (Exception exception) {
            logger.error("主题依赖关系删除失败：删除课程下所有依赖语句执行失败", exception);
            return ResultUtil.error(ResultEnum.DEPENDENCY_DELETE_ERROR_1.getCode(), ResultEnum.DEPENDENCY_DELETE_ERROR_1.getMsg());
        }

    }



    /**
     * 通过主课程名，起始、终止主题id删除依赖关系
     *
     * @param domainName     课程名
     * @param startTopicName 开始主题名
     * @param endTopicName   终止主题名
     * @return
     */
    public Result deleteDependencyByTopicName(String domainName, String startTopicName, String endTopicName) {
        Domain domain = domainRepository.findByDomainName(domainName);
        //查询课程错误
        if (domain == null) {
            logger.error("主题依赖关系删除失败：没有课程信息记录");
            return ResultUtil.error(ResultEnum.DEPENDENCY_DELETE_ERROR.getCode(), ResultEnum.DEPENDENCY_DELETE_ERROR.getMsg());
        }
        if (startTopicName == null || startTopicName.length() == 0 || endTopicName == null || endTopicName.length() == 0) {
            logger.error("主题依赖关系删除失败：主题为空");
            return ResultUtil.error(ResultEnum.DEPENDENCY_DELETE_ERROR_2.getCode(), ResultEnum.DEPENDENCY_DELETE_ERROR_2.getMsg());
        }
        Topic startTopic = topicRepository.findByDomainIdAndTopicName(domain.getDomainId(), startTopicName);
        Topic endTopic = topicRepository.findByDomainIdAndTopicName(domain.getDomainId(), endTopicName);
        if (startTopic == null || endTopic == null) {
            logger.error("主题依赖关系删除失败：主题不存在");
            return ResultUtil.error(ResultEnum.DEPENDENCY_DELETE_ERROR_2.getCode(), ResultEnum.DEPENDENCY_DELETE_ERROR_2.getMsg());
        }
        try {
            Long startTopicId = startTopic.getTopicId();
            Long endTopicId = endTopic.getTopicId();
            dependencyRepository.deleteByDomainIdAndStartTopicIdAndEndTopicId(domain.getDomainId(), startTopicId, endTopicId);
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "主题依赖关系删除成功");
        } catch (Exception exception) {
            logger.error("主题依赖关系删除失败：删除语句执行失败", exception);
            return ResultUtil.error(ResultEnum.DEPENDENCY_DELETE_ERROR_1.getCode(), ResultEnum.DEPENDENCY_DELETE_ERROR_1.getMsg());
        }

    }

    /**
     * 通过课程名和关键词，获取该课程下的主题依赖关系
     *
     * @param domainName
     * @param keyword
     * @return
     */
    public Result findDependenciesByKeyword(String domainName, String keyword) {
        Domain domain = domainRepository.findByDomainName(domainName);
        //查询课程错误
        if (domain == null) {
            logger.error("主题依赖关系查询失败：没有课程信息记录");
            return ResultUtil.error(ResultEnum.DEPENDENCY_SEARCH_ERROR.getCode(), ResultEnum.DEPENDENCY_SEARCH_ERROR.getMsg());
        }
        try {
            List<Dependency> dependencies = dependencyRepository.findDependenciesByDomainIdAndKeyword(domain.getDomainId(), keyword);
            List<DependencyContainName> dependencyContainNames = new ArrayList<>();
            for (Dependency dependency : dependencies) {
                DependencyContainName dependencyContainName = new DependencyContainName(dependency);
                //获取主题名
                String startTopicName = topicRepository.findOne(dependency.getStartTopicId()).getTopicName();
                String endTopicName = topicRepository.findOne(dependency.getEndTopicId()).getTopicName();
                //设置主题名
                dependencyContainName.setStartTopicName(startTopicName);
                dependencyContainName.setEndTopicName(endTopicName);
                dependencyContainNames.add(dependencyContainName);
            }
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), dependencyContainNames);
        } catch (Exception exception) {
            logger.error("主题依赖关系查询失败：查询语句执行失败", exception);
            return ResultUtil.error(ResultEnum.DEPENDENCY_SEARCH_ERROR_4.getCode(), ResultEnum.DEPENDENCY_SEARCH_ERROR_4.getMsg());
        }
    }

    /**
     * 通过主课程名，获取该课程下的主题依赖关系
     *
     * @param domainName
     * @return
     */
    public Result findDependenciesByDomainName(String domainName) {
        if (domainName == null) {
            logger.error("主题依赖关系查询失败：没有指定课程");
            return ResultUtil.error(ResultEnum.DEPENDENCY_SEARCH_ERROR_5.getCode(), ResultEnum.DEPENDENCY_SEARCH_ERROR_5.getMsg(), "主题依赖关系查询失败：没有指定课程");
        }
        Domain domain = domainRepository.findByDomainName(domainName);
        //查询错误
        if (domain == null) {
            logger.error("主题依赖关系查询失败：没有课程信息记录");
            return ResultUtil.error(ResultEnum.DEPENDENCY_SEARCH_ERROR.getCode(), ResultEnum.DEPENDENCY_SEARCH_ERROR.getMsg(), "主题依赖关系查询失败：没有课程信息记录");
        }
        Long domainId = domain.getDomainId();
        List<Dependency> dependencies = dependencyRepository.findByDomainId(domainId);
        List<DependencyContainName> dependencyContainNames = new ArrayList<>();

        for (Dependency dependency : dependencies) {
            DependencyContainName dependencyContainName = new DependencyContainName(dependency);
            //获取主题名
            Topic startTopic = topicRepository.findOne(dependency.getStartTopicId());
            String startTopicName;
            if (startTopic != null)
                startTopicName = startTopic.getTopicName();
            else
                continue;
            Topic endTopic = topicRepository.findOne(dependency.getEndTopicId());
            String endTopicName;
            if (endTopic != null)
                endTopicName = endTopic.getTopicName();
            else
                continue;
            // String startTopicName = topicRepository.findOne(dependency.getStartTopicId()).getTopicName();
            // String endTopicName = topicRepository.findOne(dependency.getEndTopicId()).getTopicName();
            //设置主题名
            dependencyContainName.setStartTopicName(startTopicName);
            dependencyContainName.setEndTopicName(endTopicName);
            dependencyContainNames.add(dependencyContainName);
        }
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), dependencyContainNames);
    }

    public Result findDependenciesByDomainNameSaveAsGexf(String domainName) {

        Domain domain = domainRepository.findByDomainName(domainName);
        //查询错误
        if (domain == null) {
            logger.error("主题依赖关系查询失败：没有课程信息记录");
            return ResultUtil.error(ResultEnum.DEPENDENCY_SEARCH_ERROR.getCode(), ResultEnum.DEPENDENCY_SEARCH_ERROR.getMsg(), "主题依赖关系查询失败：没有课程信息记录");
        }
        Long domainId = domain.getDomainId();

        new File(gexfPath).mkdir();
        File gexfFile = new File(gexfPath + "\\" + domainName + ".gexf");
        if (gexfFile.exists()) {
            // 如果存在，就直接调用本地gexf文件的内容，返回给前台
            // 第二次之后直接调用本地gexf文件的内容，返回给前台
            try {
                String gexfContent = FileUtils.readFileToString(gexfFile, "UTF-8");
                return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), gexfContent);
            } catch (IOException error) {
                logger.error("主题依赖关系生成失败：gexf文件生成失败 " + error);
                return ResultUtil.error(ResultEnum.DEPENDENCY_SEARCH_ERROR_2.getCode(), ResultEnum.DEPENDENCY_SEARCH_ERROR_2.getMsg(), error);
            }
        }

        //如果不存在，运行gephi java接口生成认知关系图数据。
        // 将其存储到本地，方便第二次以后调用直接读取文件内容。同时返回关系图数据。
        // Init a project - and therefore a workspace
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        Workspace workspace = pc.getCurrentWorkspace();

        //Get models and controllers for this new workspace - will be useful later
        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel();
        PreviewModel model = Lookup.getDefault().lookup(PreviewController.class).getModel();
        ImportController importController = Lookup.getDefault().lookup(ImportController.class);
        FilterController filterController = Lookup.getDefault().lookup(FilterController.class);
        AppearanceController appearanceController = Lookup.getDefault().lookup(AppearanceController.class);
        AppearanceModel appearanceModel = appearanceController.getModel();
        //获取数据库
        Map<String, Object> dbInformation = getDBInformation();

        //Import database
        EdgeListDatabaseImpl db = new EdgeListDatabaseImpl();
        db.setDBName((String) dbInformation.get("dbName"));
        db.setHost((String) dbInformation.get("host"));
        db.setUsername((String) dbInformation.get("username"));
        db.setPasswd((String) dbInformation.get("password"));
        db.setSQLDriver(new MySQLDriver());
        db.setPort((int) dbInformation.get("port"));

        db.setNodeQuery("SELECT DISTINCT " +
                "dt.topic_id AS id, " +
                "dt.topic_name AS label " +
                "FROM " +
                "dependency AS dp , " +
                "topic AS dt " +
                "WHERE " +
                "dt.domain_id = '" + domainId + "' AND " +
                "(dt.topic_id = dp.start_topic_id OR " +
                "dt.topic_id = dp.end_topic_id)");
        db.setEdgeQuery("SELECT dependency.start_topic_id AS source, dependency.end_topic_id AS target "
                + "FROM dependency where domain_id='" + domainId + "'");

        ImporterEdgeList edgeListImporter = new ImporterEdgeList();
        Container container = importController.importDatabase(db, edgeListImporter);
        //Don't create missing nodes
        container.getLoader().setAllowAutoNode(false);
        //Force UNDIRECTED
        container.getLoader().setEdgeDefault(EdgeDirectionDefault.DIRECTED);

        //Append imported data to GraphAPI
        importController.process(container, new DefaultProcessor(), workspace);

        //See if graph is well imported
        DirectedGraph graph = graphModel.getDirectedGraph();
        if (graph.getNodeCount() == 0 || graph.getEdgeCount() == 0) {
            String error = "节点数量为：" + graph.getNodeCount() + "，边数量为：" + graph.getEdgeCount();
            logger.error("主题依赖关系生成失败：gexf文件生成失败 " + error);
            return ResultUtil.error(ResultEnum.DEPENDENCY_SEARCH_ERROR_2.getCode(), ResultEnum.DEPENDENCY_SEARCH_ERROR_2.getMsg(), error);
        }

        //Filter：对节点进行过滤操作
        DegreeRangeBuilder.DegreeRangeFilter degreeFilter = new DegreeRangeBuilder.DegreeRangeFilter();
        degreeFilter.init(graph);
        //Remove nodes with degree < 30
        degreeFilter.setRange(new Range(2, Integer.MAX_VALUE));
        Query query = filterController.createQuery(degreeFilter);
        GraphView view = filterController.filter(query);
        //Set the filter result as the visible view
        graphModel.setVisibleView(view);

        //See visible graph stats
        UndirectedGraph graphVisible = graphModel.getUndirectedGraphVisible();

        //Layout for 1 minute
        AutoLayout autoLayout = new AutoLayout(1, TimeUnit.MINUTES);
        autoLayout.setGraphModel(graphModel);
        YifanHuLayout firstLayout = new YifanHuLayout(null, new StepDisplacement(1f));
        ForceAtlasLayout secondLayout = new ForceAtlasLayout(null);
        //True after 10% of layout time
        AutoLayout.DynamicProperty adjustBySizeProperty = AutoLayout.createDynamicProperty("forceAtlas.adjustSizes.name", Boolean.TRUE, 0.1f);
        //500 for the complete period
        AutoLayout.DynamicProperty repulsionProperty = AutoLayout.createDynamicProperty("forceAtlas.repulsionStrength.name", 1000., 0f);
        autoLayout.addLayout(firstLayout, 0.5f);
        autoLayout.addLayout(secondLayout, 0.5f, new AutoLayout.DynamicProperty[]{adjustBySizeProperty, repulsionProperty});
        autoLayout.execute();

        //Append as a Directed Graph
        DirectedGraph directedGraph = graphModel.getDirectedGraph();
        ArrayList<Node> nodeList = new ArrayList<Node>();
        //Iterate over nodes
        for (Node n : directedGraph.getNodes()) {
            Node[] neighbors = directedGraph.getNeighbors(n).toArray();
            if (neighbors.length != 0) {
            } else {
                nodeList.add(n);
            }
        }
        directedGraph.removeAllNodes(nodeList);

        //Get Centrality
        GraphDistance distance = new GraphDistance();
        distance.setDirected(true);
        distance.execute(graphModel);

        //Rank color by Degree
        Function degreeRanking = appearanceModel.getNodeFunction(graph, AppearanceModel.GraphFunction.NODE_DEGREE, RankingElementColorTransformer.class);
        RankingElementColorTransformer degreeTransformer = degreeRanking.getTransformer();
        degreeTransformer.setColors(new Color[]{new Color(0xFEF0D9), new Color(0xB30000)});
        degreeTransformer.setColorPositions(new float[]{0f, 1f});
        appearanceController.transform(degreeRanking);

        //Rank size by centrality
        Column centralityColumn = graphModel.getNodeTable().getColumn(GraphDistance.BETWEENNESS);
        Function centralityRanking = appearanceModel.getNodeFunction(graph, centralityColumn, RankingNodeSizeTransformer.class);
        RankingNodeSizeTransformer centralityTransformer = centralityRanking.getTransformer();
        centralityTransformer.setMinSize(10);
        centralityTransformer.setMaxSize(30);
        appearanceController.transform(centralityRanking);

        //Rank label size - set a multiplier size
        Function centralityRanking2 = appearanceModel.getNodeFunction(graph, centralityColumn, RankingLabelSizeTransformer.class);
        RankingLabelSizeTransformer labelSizeTransformer = centralityRanking2.getTransformer();
        labelSizeTransformer.setMinSize(0.5f);
        labelSizeTransformer.setMaxSize(1.5f);
        appearanceController.transform(centralityRanking2);

        //Set 'show labels' option in Preview - and disable node size influence on text size
        PreviewModel previewModel = Lookup.getDefault().lookup(PreviewController.class).getModel();
        //previewModel.getProperties().putValue(PreviewProperty.SHOW_NODE_LABELS, Boolean.TRUE);
        previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_PROPORTIONAL_SIZE, Boolean.FALSE);

        //Preview
        model.getProperties().putValue(PreviewProperty.SHOW_NODE_LABELS, Boolean.TRUE);
        model.getProperties().putValue(PreviewProperty.EDGE_COLOR, new EdgeColor(Color.GRAY));
        model.getProperties().putValue(PreviewProperty.EDGE_THICKNESS, new Float(0.1f));
        model.getProperties().putValue(PreviewProperty.NODE_LABEL_FONT, model.getProperties().getFontValue(PreviewProperty.NODE_LABEL_FONT).deriveFont(15));

        // Run modularity algorithm - community detection
        Modularity modularity = new Modularity();
        modularity.execute(graphModel);

        //Partition with 'modularity_class', just created by Modularity algorithm
        Column modColumn = graphModel.getNodeTable().getColumn(Modularity.MODULARITY_CLASS);
        Function func2 = appearanceModel.getNodeFunction(graph, modColumn, PartitionElementColorTransformer.class);
        Partition partition2 = ((PartitionFunction) func2).getPartition();
        Palette palette2 = PaletteManager.getInstance().randomPalette(partition2.size());
        partition2.setColors(palette2.getColors());
        appearanceController.transform(func2);

        //Export
        ExportController ec = Lookup.getDefault().lookup(ExportController.class);
        // 导出成文件
        try {
            ec.exportFile(new File(gexfPath + "\\" + domainName + ".gexf"));
            ec.exportFile(new File(gexfPath + "\\" + domainName + ".pdf"));
        } catch (IOException error) {
            logger.error("主题依赖关系生成失败：gexf文件生成失败 " + error);
            return ResultUtil.error(ResultEnum.DEPENDENCY_SEARCH_ERROR_2.getCode(), ResultEnum.DEPENDENCY_SEARCH_ERROR_2.getMsg(), error);
        }

        // 导出成字符串
        Exporter exporter = ec.getExporter("gexf");
        CharacterExporter characterExporter = (CharacterExporter) exporter;
        StringWriter stringWriter = new StringWriter();
        ec.exportWriter(stringWriter, characterExporter);
        String result = stringWriter.toString();

        if (result == null || result.equals("")) {
            logger.error("主题依赖关系生成失败：gexf文件生成失败 ");
            return ResultUtil.error(ResultEnum.DEPENDENCY_SEARCH_ERROR_2.getCode(), ResultEnum.DEPENDENCY_SEARCH_ERROR_2.getMsg());
        }

        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), result);
    }

    private Map<String, Object> getDBInformation() {
        logger.debug(url);
        logger.debug(password);
        logger.debug(username);
        int firstSlashIndex = url.indexOf("//");
        int lastSlashIndex = url.lastIndexOf("/");
        String hostAndPort = url.substring(firstSlashIndex + 2, lastSlashIndex);
        String[] hostAndPorts = hostAndPort.split(":");
        //获取主机名
        String host = hostAndPorts[0];
        //获取端口号
        Integer port = Integer.valueOf(hostAndPorts[1]);
        int questionMarkIndex = url.indexOf("?");
        //获取数据库名
        String dbName = url.substring(lastSlashIndex + 1, questionMarkIndex);
        //构造返回数据
        Map<String, Object> dbInformation = new HashMap<>(5);
        dbInformation.put("host", host);
        dbInformation.put("port", port);
        dbInformation.put("dbName", dbName);
        dbInformation.put("username", username);
        dbInformation.put("password", password);
        return dbInformation;
    }

    /**
     * 自动构建认知关系。从数据库中读取主题以及碎片
     *
     * @param domainName 课程名
     * @param isEnglish  是否为英文课程
     * @return
     */
    public Result generateDependencyByDomainName(String domainName, Boolean isEnglish) {

        if (domainName == null) {
            logger.error("主题依赖关系查询失败：没有指定课程");
            return ResultUtil.error(ResultEnum.DEPENDENCY_SEARCH_ERROR_5.getCode(), ResultEnum.DEPENDENCY_SEARCH_ERROR_5.getMsg());
        }

        Domain domain = domainRepository.findByDomainName(domainName);
        //查询错误
        if (domain == null) {
            logger.error("主题依赖关系生成失败：没有课程信息记录");
            return ResultUtil.error(ResultEnum.DEPENDENCY_GENERATE_ERROR.getCode(), ResultEnum.DEPENDENCY_GENERATE_ERROR.getMsg());
        }

        Long domainId = domain.getDomainId();

        //查看数据库中是否已有该课程的主题依赖关系
        List<Dependency> dependencies = dependencyRepository.findByDomainId(domainId);
        if (dependencies.size() > 0)   //数据库已有该课程主题依赖关系
        {
            List<DependencyContainName> dependencyContainNames = new ArrayList<>();
            for (Dependency dependency : dependencies) {
                DependencyContainName dependencyContainName = new DependencyContainName(dependency);
                //获取主题名
                String startTopicName = topicRepository.findOne(dependency.getStartTopicId()).getTopicName();
                String endTopicName = topicRepository.findOne(dependency.getEndTopicId()).getTopicName();

                //设置主题名
                dependencyContainName.setStartTopicName(startTopicName);
                dependencyContainName.setEndTopicName(endTopicName);

                dependencyContainNames.add(dependencyContainName);

            }
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), dependencyContainNames);
        }

        //数据库中没有该课程的主题依赖关系，需自动构建
        //获得课程下所有主题
        List<Topic> topicList = topicRepository.findByDomainId(domainId);
        if (topicList.size() < 1) {
            logger.error("主题依赖关系生成失败：主题不存在");
            return ResultUtil.error(ResultEnum.DEPENDENCY_GENERATE_ERROR_1.getCode(), ResultEnum.DEPENDENCY_GENERATE_ERROR_1.getMsg());
        }

        //获得topicContainAssembleText List，即每个主题有对应碎片文本，获得主题内容信息
        List<TopicContainAssembleText> topicContainAssembleTexts = new ArrayList<>();

        for (int i = 0; i < topicList.size(); i++) {
            Topic temp_topic = topicList.get(i);
            TopicContainAssembleText temp_topicContentAssembleText = new TopicContainAssembleText(temp_topic);
            temp_topicContentAssembleText.setTopicId(temp_topic.getTopicId());


            //查询碎片信息
            List<Assemble> assembleList = assembleRepository.findAllAssemblesByTopicId(temp_topic.getTopicId());
            if (assembleList.size() < 1) {
                System.out.print("该主题没有依赖碎片！" + temp_topic.getTopicId());
                continue;
                /**
                 System.out.print(temp_topic.getTopicId());
                 logger.error("主体依赖关系生成失败：碎片内容为空");
                 return ResultUtil.error(ResultEnum.DEPENDENCY_GENERATE_ERROR_2.getCode(), ResultEnum.DEPENDENCY_GENERATE_ERROR_2.getMsg());
                 */
            }
            String text = "";
            for (int j = 0; j <Math.min(assembleList.size(),100); j++) {
                text = text + assembleList.get(j).getAssembleText() + " ";
            }
            temp_topicContentAssembleText.setText(text);

            topicContainAssembleTexts.add(temp_topicContentAssembleText);
        }

        /**
         * 根据主题内容，调用算法得到主题认知关系
         */
//        RankDependency rankDependency = new RankDependency();
//        List<Dependency> generated_dependencies = rankDependency.rankText(topicContainAssembleTexts, topicContainAssembleTexts.size(), isEnglish);
        GetAsymmetry getAsymmetry = new GetAsymmetry();
        List<Dependency> generated_dependencies = getAsymmetry.AsyDependency(topicList, topicContainAssembleTexts);

        //保存自动构建的依赖关系，存到数据库
        dependencyRepository.save(generated_dependencies);
//        for(Dependency temp_dependency : generated_dependencies)
//            dependencyRepository.saveAndFlush(temp_dependency);

        List<DependencyContainName> dependencyContainNames = new ArrayList<>();
        for (Dependency dependency : generated_dependencies) {
            DependencyContainName dependencyContainName = new DependencyContainName(dependency);
            //获取主题名
            String startTopicName = topicRepository.findOne(dependency.getStartTopicId()).getTopicName();
            String endTopicName = topicRepository.findOne(dependency.getEndTopicId()).getTopicName();
            //设置主题名
            dependencyContainName.setStartTopicName(startTopicName);
            dependencyContainName.setEndTopicName(endTopicName);
            dependencyContainNames.add(dependencyContainName);
        }
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), dependencyContainNames);

    }

    /**
     * 自动构建认知关系。从数据库中读取主题以及碎片
     *
     *
     * @return
     */
    public Result generateAllDomainDependency(Integer startIndex) {

        String[] notFilterDomain={"计算机组成原理","C语言","操作系统","计算机系统结构","数据结构",
                "数据库应用","低年级(1-2)语文","低年级(1-2)科学","中年级(3-4)英语","高年级(5-6)数学",
                "五年级科学","七年级语文","八年级语文","九年级语文","七年级数学","八年级数学","九年级数学",
                "七年级英语","八年级英语","九年级英语","八年级物理","九年级物理","九年级化学","七年级生物",
                "八年级生物","七年级历史","八年级历史","九年级历史","七年级地理","八年级地理","七年级政治","八年级政治",
                "九年级政治","初中信息技术","高一语文","高二语文","高中数学","高一英语","高三英语","高一历史","高二历史",
                "高一政治","高二政治","高三政治","高一地理","高三地理","高一生物","高二生物","高一化学","高二化学","高三化学",
                "合同法","数据结构(人工)","示范课程高等数学","概率论"};
//        Integer[] EnglishDomainId={
//                283,284,285,568,569,571,572,573,574,575,576,577,578,579,580,581,604,610,611,618,628,630,
//                650,652,653,654,656,658,659,680,681,682,683,693,694,695,696,698,699,704,705,707,710,715,721,
//                723,725,726,734,748,749,756,763,764,765,768,773,774,780,782,783,786,789,791,793,796,797,799,800,
//                801,802, 804,805,806,807,808,809,810,811,817,818,820,824,825,826,829,831,832,840,856,857, 858,
//                859,860,861 ,862 ,870 ,871,872,873 ,874 ,875 ,876 ,877 ,878
//        };
        List<String> notFilterDomainList= Arrays.asList(notFilterDomain);
//        List<Integer> EnglishDomainIdList= Arrays.asList(EnglishDomainId);

        List<Domain> domains = domainRepository.findAll();
        //查询错误
        if (domains == null) {
            logger.error("主题依赖关系生成失败：没有课程信息记录");
            return ResultUtil.error(ResultEnum.DEPENDENCY_GENERATE_ERROR.getCode(), ResultEnum.DEPENDENCY_GENERATE_ERROR.getMsg());
        }

        for (int k = startIndex; k < domains.size(); k++) {
            Domain domain = domains.get(k);
            Long domainId = domain.getDomainId();
            String domainName = domain.getDomainName();
            if (notFilterDomainList.contains(domainName)) {
                continue;
            }

            logger.info("正在抽取第 " +k+" 门课程 "+domainName+ " 的主题依赖关系");
            List<Dependency> dependencies = dependencyRepository.findByDomainId(domainId);
            if (dependencies.size() > 0)   //数据库已有该课程主题依赖关系
            {
                logger.info("数据库已有 " + domainName + " 课程的主题依赖关系，删除数据库中的依赖关系");
                dependencyRepository.deleteDependenciesByDomainId(domainId);
                logger.info("删除数据库中 " + domainName + " 课程的的依赖关系完成");
            }

            List<Topic> topicList = topicRepository.findByDomainId(domainId);
            if (topicList.size() < 1) {
                logger.error("主题依赖关系生成失败：主题不存在");
                continue;
//                return ResultUtil.error(ResultEnum.DEPENDENCY_GENERATE_ERROR_1.getCode(), ResultEnum.DEPENDENCY_GENERATE_ERROR_1.getMsg());
            }

            //获得topicContainAssembleText List，即每个主题有对应碎片文本，获得主题内容信息
            List<TopicContainAssembleText> topicContainAssembleTexts = new ArrayList<>();

            for (int i = 0; i < topicList.size(); i++) {
                logger.info("开始第"+i+"个主题 "+topicList.get(i));
                Topic temp_topic = topicList.get(i);
                TopicContainAssembleText temp_topicContentAssembleText = new TopicContainAssembleText(temp_topic);
                temp_topicContentAssembleText.setTopicId(temp_topic.getTopicId());

                //查询碎片信息
                List<Assemble> assembleList = assembleRepository.findAllAssemblesByTopicId(temp_topic.getTopicId());
                if (assembleList.size() < 1) {
                    logger.error("该主题没有依赖碎片！" + temp_topic.getTopicId());
                    continue;
                }
                String text = "";
                for (int j = 0; j < Math.min(assembleList.size(),100); j++) {
                    text = text + assembleList.get(j).getAssembleText() + " ";
                }
                temp_topicContentAssembleText.setText(text);

                topicContainAssembleTexts.add(temp_topicContentAssembleText);
            }

            /**
             * 根据主题内容，调用算法得到主题认知关系
             */
//        RankDependency rankDependency = new RankDependency();
//        List<Dependency> generated_dependencies = rankDependency.rankText(topicContainAssembleTexts, topicContainAssembleTexts.size(), isEnglish);
            GetAsymmetry getAsymmetry = new GetAsymmetry();
            List<Dependency> generated_dependencies = getAsymmetry.AsyDependency(topicList, topicContainAssembleTexts);
            logger.info("开始构建 " + domainName + " 课程的主题依赖关系");
            //保存自动构建的依赖关系，存到数据库
            dependencyRepository.save(generated_dependencies);
            List<DependencyContainName> dependencyContainNames = new ArrayList<>();
            for (Dependency dependency : generated_dependencies) {
                DependencyContainName dependencyContainName = new DependencyContainName(dependency);
                //获取主题名
                String startTopicName = topicRepository.findOne(dependency.getStartTopicId()).getTopicName();
                String endTopicName = topicRepository.findOne(dependency.getEndTopicId()).getTopicName();
                //设置主题名
                dependencyContainName.setStartTopicName(startTopicName);
                dependencyContainName.setEndTopicName(endTopicName);
                dependencyContainNames.add(dependencyContainName);
            }
            logger.info("第 " +k+" 门课程 "+domainName+ " 的主题依赖关系构建完成，结果为 " + dependencyContainNames);
        }
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(),"success");

    }





    /**
     * 智慧教育系统获得推荐路径，访问教育大数据组提供的war包
     *
     * @param domainId
     * @param userId
     * @return
     */
    public Result getLearningPath(Long domainId, Long userId) {
        String url = "http://localhost:9218/LearningPathWeb/Path/LearningPath/allLearningPath?domainId=" + domainId + "&userId=" + userId;

        Result result = getRequestResponse(url);
        return result;
    }

    /**
     * 智慧教育系统学习路径：更新用户状态
     *
     * @param domainId
     * @param userId
     * @return
     */
    public Result learningPath_updateUserStates(Long domainId, Long userId) {
        String url = "http://localhost:9218/LearningPathWeb/Path/States/updateUserStates?domainId=" + domainId + "&userId=" + userId;
        Result result = getRequestResponse(url);
        return result;
    }

    public Result learningPath_defineLearningPath(Long domainId, Long userId, Long termId) {
        String url = "http://localhost:9218/LearningPathWeb/Path/LearningPath/defineLearningPath?domainId=" + domainId + "&userId=" + userId + "&termId=" + termId;
        Result result = getRequestResponse(url);
        return result;
    }

    /**
     * 向给定url发起请求，获得请求内容并返回, GET方式请求
     *
     * @param url
     * @return
     */
    public Result getRequestResponse(String url) {
        String userAgent = Config.userAgent;
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", userAgent);
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer learningPath = new StringBuffer();
                String inputLine;
                while ((inputLine = reader.readLine()) != null) {
                    learningPath.append(inputLine);
                }
                reader.close();
                connection.disconnect();
                return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), learningPath.toString());
            }
            connection.disconnect();
            return ResultUtil.error(ResultEnum.DEPENDENCY_LEARNING_PATH_ERROR.getCode(), ResultEnum.DEPENDENCY_LEARNING_PATH_ERROR.getMsg());

        } catch (Exception e) {
            System.out.println("发送get请求出现异常" + e);
            e.printStackTrace();
        }
        return ResultUtil.error(ResultEnum.DEPENDENCY_LEARNING_PATH_ERROR.getCode(), ResultEnum.DEPENDENCY_LEARNING_PATH_ERROR.getMsg());
    }


    /**
     * 使用新的认知关系生成算法生成课程下的认知关系，并写入到csv文件中，不保存到数据库中
     * @param domainName  课程名
     * @param isEnglish   是否为英文课程
     * @return
     */
    public Result getGenerateDependencyCSVFileByDomainName(String domainName, Boolean isEnglish)
    {

        if (domainName == null)
        {
            logger.error("主题依赖关系查询失败：没有指定课程");
            return ResultUtil.error(ResultEnum.DEPENDENCY_SEARCH_ERROR_5.getCode(), ResultEnum.DEPENDENCY_SEARCH_ERROR_5.getMsg());
        }

        Domain domain = domainRepository.findByDomainName(domainName);
        //查询错误
        if (domain == null) {
            logger.error("主题依赖关系生成失败：没有课程信息记录");
            return ResultUtil.error(ResultEnum.DEPENDENCY_GENERATE_ERROR.getCode(), ResultEnum.DEPENDENCY_GENERATE_ERROR.getMsg());
        }

        Long domainId = domain.getDomainId();

        //查看数据库中是否已有该课程的主题依赖关系
        List<Dependency> dependencies = dependencyRepository.findByDomainId(domainId);

//        if(dependencies.size() > 0)   //数据库已有该课程主题依赖关系
//        {
//            logger.error("主题依赖关系生成失败：已有认知关系");
//            return ResultUtil.error(ResultEnum.DEPENDENCY_GENERATE_ERROR.getCode(), ResultEnum.DEPENDENCY_GENERATE_ERROR.getMsg());
//        }

        //数据库中没有该课程的主题依赖关系，需自动构建
        //获得课程下所有主题
        List<Topic> topicList = topicRepository.findByDomainId(domainId);
        if(topicList.size() < 1)
        {
            logger.error("主题依赖关系生成失败：主题不存在");
            return ResultUtil.error(ResultEnum.DEPENDENCY_GENERATE_ERROR_1.getCode(), ResultEnum.DEPENDENCY_GENERATE_ERROR_1.getMsg());
        }

        //获得topicContainAssembleText List，即每个主题有对应碎片文本，获得主题内容信息
        List<TopicContainAssembleText> topicContainAssembleTexts = new ArrayList<>();

        for(int i = 0; i<topicList.size(); i++)
        {
            Topic temp_topic = topicList.get(i);
            TopicContainAssembleText temp_topicContentAssembleText = new TopicContainAssembleText(temp_topic);
            temp_topicContentAssembleText.setTopicId(temp_topic.getTopicId());


            //查询碎片信息
            List<Assemble> assembleList = assembleRepository.findAllAssemblesByTopicId(temp_topic.getTopicId());
            if(assembleList.size() < 1)
            {
                System.out.println("缺乏碎片主题：" + temp_topic.getTopicName());
                continue;
//                System.out.print(temp_topic.getTopicId());
//                logger.error("主体依赖关系生成失败：碎片内容为空");
//                return ResultUtil.error(ResultEnum.DEPENDENCY_GENERATE_ERROR_2.getCode(), ResultEnum.DEPENDENCY_GENERATE_ERROR_2.getMsg());
            }
            String text = "";
            for(int j = 0; j<assembleList.size(); j++)
            {
                text = text + assembleList.get(j).getAssembleText() + " ";
            }
            temp_topicContentAssembleText.setText(text);

            topicContainAssembleTexts.add(temp_topicContentAssembleText);
        }

        /**
         * 根据主题内容，调用算法得到主题认知关系
         */
        GetAsymmetry getAsymmetry = new GetAsymmetry();
        List<Dependency> generated_dependencies = getAsymmetry.AsyDependency(topicList, topicContainAssembleTexts);


        List<DependencyContainName> dependencyContainNames = new ArrayList<>();
        for (Dependency dependency : generated_dependencies) {
            DependencyContainName dependencyContainName = new DependencyContainName();
            dependencyContainName.setStartTopicId(dependency.getStartTopicId());
            dependencyContainName.setEndTopicId(dependency.getEndTopicId());
            dependencyContainName.setConfidence(dependency.getConfidence());
            dependencyContainName.setDomainId(dependency.getDomainId());
//            DependencyContainName dependencyContainName = new DependencyContainName(dependency);
            //获取主题名
            String startTopicName = topicRepository.findOne(dependency.getStartTopicId()).getTopicName();
            String endTopicName = topicRepository.findOne(dependency.getEndTopicId()).getTopicName();
            //设置主题名
            dependencyContainName.setStartTopicName(startTopicName);
            dependencyContainName.setEndTopicName(endTopicName);
            dependencyContainNames.add(dependencyContainName);
        }
//        写入到csv文件
        if (dependencyContainNames.size()>0)
        {
            String[] csvHeaders = {"startTopicName", "endTopicName"};
            CsvUtil.writeCSV(dependencyContainNames, "E:\\认知关系算法生成结果_临时文件夹/"+dependencyContainNames.get(0).getDomainId().toString()+"算法生成.csv", csvHeaders);

        }
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), dependencyContainNames);

    }



    public static void main(String[] args) {
        DependencyService dependencyService = new DependencyService();
        dependencyService.findDependenciesByDomainNameSaveAsGexf("数据结构");
    }
}
