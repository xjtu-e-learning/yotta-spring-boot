package com.xjtu.dependency.service;

import com.xjtu.common.Config;
import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.dependency.domain.Dependency;
import com.xjtu.dependency.domain.DependencyContainName;
import com.xjtu.dependency.repository.DependencyRepository;
import com.xjtu.domain.domain.Domain;
import com.xjtu.domain.repository.DomainRepository;
import com.xjtu.topic.repository.TopicRepository;
import com.xjtu.utils.ResultUtil;
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
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 处理主题依赖关系数据
 * @author yangkuan
 * @date 2018/03/21 12:46
 * */
@Service
public class DependencyService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DependencyRepository dependencyRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private DomainRepository domainRepository;



    /**
     * 通过主课程名，获取该课程下的主题依赖关系
     * @param domainName
     * @return
     * */
    public Result findDependenciesByDomainName(String domainName){
        Domain domain = domainRepository.findByDomainName(domainName);
        //查询错误
        if(domain==null){
            logger.error("主题依赖关系查询失败：没有课程信息记录");
            return ResultUtil.error(ResultEnum.DEPENDENCY_SEARCH_ERROR.getCode(),ResultEnum.DEPENDENCY_SEARCH_ERROR.getMsg());
        }
        Long domainId = domain.getDomainId();
        List<Dependency> dependencies = dependencyRepository.findByDomainId(domainId);
        //查询错误
        if(dependencies.size()==0){
            logger.error("主题依赖关系查询失败：该课程下没有主题依赖关系记录");
            return ResultUtil.error(ResultEnum.DEPENDENCY_SEARCH_ERROR_1.getCode(),ResultEnum.DEPENDENCY_SEARCH_ERROR_1.getMsg());
        }
        List<DependencyContainName> dependencyContainNames = new ArrayList<>();
        for(Dependency dependency:dependencies){
            DependencyContainName dependencyContainName = new DependencyContainName(dependency);
            //获取主题名
            String startTopicName = topicRepository.findByTopicIdAndDomainId(dependency.getStartTopicId(),domainId).getTopicName();
            String endTopicName = topicRepository.findByTopicIdAndDomainId(dependency.getEndTopicId(),domainId).getTopicName();
            //设置主题名
            dependencyContainName.setStartTopicName(startTopicName);
            dependencyContainName.setEndTopicName(endTopicName);
            dependencyContainNames.add(dependencyContainName);
        }
        logger.info("主题依赖关系查询成功");
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), dependencyContainNames);
    }

    public Result findDependenciesByDomainNameSaveAsGexf(String domainName){

        Domain domain = domainRepository.findByDomainName(domainName);
        //查询错误
        if(domain==null){
            logger.error("主题依赖关系查询失败：没有课程信息记录");
            return ResultUtil.error(ResultEnum.DEPENDENCY_SEARCH_ERROR.getCode(),ResultEnum.DEPENDENCY_SEARCH_ERROR.getMsg());
        }
        Long domainId = domain.getDomainId();

        new File(Config.GEXFPATH).mkdir();
        File gexfFile = new File(Config.GEXFPATH + "\\" + domainName + ".gexf");
        if(gexfFile.exists()){
            // 如果存在，就直接调用本地gexf文件的内容，返回给前台
            // 第二次之后直接调用本地gexf文件的内容，返回给前台
            try {
                String gexfContent = FileUtils.readFileToString(gexfFile, "UTF-8");
                return ResultUtil.success(ResultEnum.SUCCESS.getCode(),ResultEnum.SUCCESS.getMsg(),gexfContent);
            } catch (IOException error) {
                logger.error("主题依赖关系生成失败：gexf文件生成失败 "+error);
                return ResultUtil.error(ResultEnum.DEPENDENCY_SEARCH_ERROR_2.getCode(),ResultEnum.DEPENDENCY_SEARCH_ERROR_2.getMsg(),error);
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

        //获取属性文件
        Properties properties = getProperties();
        //获取数据库
        Map<String, Object> dbInformation = getDBInformation(properties);

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
            logger.error("主题依赖关系生成失败：gexf文件生成失败 "+error);
            return ResultUtil.error(ResultEnum.DEPENDENCY_SEARCH_ERROR_2.getCode(),ResultEnum.DEPENDENCY_SEARCH_ERROR_2.getMsg(),error);
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
        logger.info("Nodes: " + graphVisible.getNodeCount());
        logger.info("Edges: " + graphVisible.getEdgeCount());

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
        logger.info(partition2.size() + " partitions found");
        Palette palette2 = PaletteManager.getInstance().randomPalette(partition2.size());
        partition2.setColors(palette2.getColors());
        appearanceController.transform(func2);

        //Export
        ExportController ec = Lookup.getDefault().lookup(ExportController.class);
        // 导出成文件
        try {
            ec.exportFile(new File(Config.GEXFPATH + "\\" + domainName + ".gexf"));
            ec.exportFile(new File(Config.GEXFPATH + "\\" + domainName + ".pdf"));
        } catch (IOException error) {
            logger.error("主题依赖关系生成失败：gexf文件生成失败 "+error);
            return ResultUtil.error(ResultEnum.DEPENDENCY_SEARCH_ERROR_2.getCode(),ResultEnum.DEPENDENCY_SEARCH_ERROR_2.getMsg(),error);
        }

        // 导出成字符串
        Exporter exporter = ec.getExporter("gexf");
        CharacterExporter characterExporter = (CharacterExporter)exporter;
        StringWriter stringWriter = new StringWriter();
        ec.exportWriter(stringWriter, characterExporter);
        String result = stringWriter.toString();

        if (result == null || result.equals("")) {
            logger.error("主题依赖关系生成失败：gexf文件生成失败 ");
            return ResultUtil.error(ResultEnum.DEPENDENCY_SEARCH_ERROR_2.getCode(),ResultEnum.DEPENDENCY_SEARCH_ERROR_2.getMsg());
        }

        return ResultUtil.success(ResultEnum.SUCCESS.getCode(),ResultEnum.SUCCESS.getMsg(),result);
    }
    private Properties getProperties(){
        Resource resource = new ClassPathResource("/application.properties");
        Properties properties = null;
        try {
            properties = PropertiesLoaderUtils.loadProperties(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }
    private Map<String,Object> getDBInformation(Properties properties){
        String url = properties.getProperty("spring.datasource.url");
        int firstSlashIndex = url.indexOf("//");
        int lastSlashIndex = url.lastIndexOf("/");
        String hostAndPort = url.substring(firstSlashIndex + 2,lastSlashIndex);
        String[] hostAndPorts = hostAndPort.split(":");
        //获取主机名
        String host = hostAndPorts[0];
        //获取端口号
        Integer port = Integer.valueOf(hostAndPorts[1]);
        int  questionMarkIndex = url.indexOf("?");
        //获取数据库名
        String dbName = url.substring(lastSlashIndex + 1,questionMarkIndex);
        //获取用户名
        String username = properties.getProperty("spring.datasource.username");
        //获取密码
        String password = properties.getProperty("spring.datasource.password");
        //构造返回数据
        Map<String, Object> dbInformation = new HashMap<>(5);
        dbInformation.put("host", host);
        dbInformation.put("port", port);
        dbInformation.put("dbName", dbName);
        dbInformation.put("username", username);
        dbInformation.put("password", password);
        return dbInformation;
    }
    public static void main(String[] args) {
        DependencyService dependencyService = new DependencyService();
        dependencyService.findDependenciesByDomainNameSaveAsGexf("数据结构");
    }
}
