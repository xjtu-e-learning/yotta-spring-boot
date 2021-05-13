package com.xjtu.subject.service;

import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.domain.repository.DomainRepository;
import com.xjtu.subject.repository.SubjectRepository;
import com.xjtu.topic.repository.TopicRepository;
import com.xjtu.utils.ResultUtil;
import org.apache.commons.io.FileUtils;
import org.gephi.graph.api.*;
import org.gephi.io.exporter.api.ExportController;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.EdgeDirectionDefault;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Random;

@Service
public class SubjectGephiDemoAdjust {


    /**
     * 处理subject学科导航图
     *
     * @author guanhaishan
     * @date 2021/05/11 16:01
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private DomainRepository domainRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Value("${subjectGexfpath}")
    private String gexfPath;

    /**
     * 根据学科名，返回学科图谱
     *
     * @param subjectName
     * @return
     */
    public Result getSubjectGraphByName(String subjectName) {
        new File(gexfPath).mkdir();
        File gexfFile = new File(gexfPath + "\\" + subjectName + ".gexf");
        if (gexfFile.exists()) {
            // 如果存在，就直接调用本地gexf文件的内容，返回给前台
            try {
                String subjectGraph = FileUtils.readFileToString(gexfFile, "UTF-8");
                return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), subjectGraph);
            } catch (IOException e) {
                logger.error(e.getMessage());
                return ResultUtil.error(ResultEnum.SUBJECT_GRAPH_ERROR.getCode(), ResultEnum.SUBJECT_GRAPH_ERROR.getMsg());
            }
        }
        return ResultUtil.error(ResultEnum.SUBJECT_GRAPH_ERROR.getCode(), ResultEnum.SUBJECT_GRAPH_ERROR.getMsg());
    }

    /**
     * 根据学科名，增加课程名，依赖课程ID，修改学科图谱并返回
     *
     * @param subjectName
     * @param addDomainName
     * @return
     */
    public Result addDomainGenerateSubjectGraph(String subjectName, String addDomainName) {

        //A. 此部分是初始部分，必须的，初始Project、Workspace
        //Init a project - and therefore a workspace
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        Workspace workspace;
        workspace = pc.getCurrentWorkspace();
        //B. GraphModel是全局都需要的
        //Get models and controllers for this new workspace - will be useful later
        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel();
        //C. 数据读入部分
        ImportController importController = Lookup.getDefault().lookup(ImportController.class);
        //Import file
        Container container;
        try {
            File file = new File(gexfPath + "\\" + subjectName + ".gexf");  //注意修改路径
            container = importController.importFile(file);
            container.getLoader().setEdgeDefault(EdgeDirectionDefault.DIRECTED);   //Force DIRECTED
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex.getMessage());
            return ResultUtil.error(ResultEnum.SUBJECT_GRAPH_ERROR.getCode(), ResultEnum.SUBJECT_GRAPH_ERROR.getMsg());
        }
        //Append imported data to GraphAPI
        importController.process(container, new DefaultProcessor(), workspace);


        Node n1 = graphModel.factory().newNode();
        n1.setLabel(addDomainName);

        //new出可操作的图对象(附加为有向图)
        DirectedGraph graph = graphModel.getDirectedGraph();
        //遍历节点
        Node n2 =null;
        int max_degree = 0;
        for(Node n : graph.getNodes()) {
            Node[] neighbors = graph.getNeighbors(n).toArray();
            if(graph.getOutDegree(n)>=max_degree){
                n2 = n;
                max_degree = graph.getOutDegree(n);
            }
//            System.out.println(n.getLabel()+" has "+neighbors.length+" neighbors");
        }

        //为n1设置位置坐标(随机设置)
        Random r = new Random();
        n1.setPosition(r.nextInt(200), r.nextInt(200));
        Edge e1 = graphModel.factory().newEdge(n2, n1, 1, true);

        //See if graph is well imported
        graph.addNode(n1);
        graph.addEdge(e1);
//        System.out.println("Nodes: " + graph.getNodeCount());  //信息查看
//        System.out.println("Edges: " + graph.getEdgeCount());
        ExportController ec = Lookup.getDefault().lookup(ExportController.class);
        try {
            ec.exportFile(new File(gexfPath + "\\" + subjectName + ".gexf")); //路径需要修改
            return getSubjectGraphByName(subjectName);
        } catch (IOException ex) {
            logger.error(ex.getMessage());
            return ResultUtil.error(ResultEnum.SUBJECT_GRAPH_ERROR.getCode(), ResultEnum.SUBJECT_GRAPH_ERROR.getMsg());
        }

    }


    /**
     * 根据学科名，删除课程名，修改图谱并返回
     *
     * @param subjectName
     * @param removeDomainName
     * @return
     */
    //传入学科主题字符串 and 删除课程ID
    public Result removeDomainGenerateSubjectGraph(String subjectName, String removeDomainName) {
        //A. 此部分是初始部分，必须的，初始Project、Workspace
        //Init a project - and therefore a workspace
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        Workspace workspace = pc.getCurrentWorkspace();
        //B. GraphModel是全局都需要的
        //Get models and controllers for this new workspace - will be useful later
        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel();
        //C. 数据读入部分
        ImportController importController = Lookup.getDefault().lookup(ImportController.class);
        //Import file
        Container container;
        try {
            File file = new File(gexfPath + "\\" + subjectName + ".gexf");  //注意修改路径
            container = importController.importFile(file);
            container.getLoader().setEdgeDefault(EdgeDirectionDefault.DIRECTED);   //Force DIRECTED
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        //Append imported data to GraphAPI
        importController.process(container, new DefaultProcessor(), workspace);

        //new出可操作的图对象(附加为有向图)
        DirectedGraph graph = graphModel.getDirectedGraph();
        //根据ID获取删除课程
        Node n1 = graph.getNode(removeDomainName);
        if (n1 == null){
            return ResultUtil.error(ResultEnum.SUBJECT_GRAPH_REMOVE_ERROR.getCode(),ResultEnum.SUBJECT_GRAPH_REMOVE_ERROR.getMsg());
        }
        //删除节点
        graph.removeNode(n1);

        //输出文件
        ExportController ec = Lookup.getDefault().lookup(ExportController.class);
        try {
            ec.exportFile(new File(gexfPath + "\\" + subjectName + ".gexf")); //路径需要修改
            return getSubjectGraphByName(subjectName);
        } catch (IOException ex) {
            ex.printStackTrace();
            logger.error(ex.getMessage());
            return ResultUtil.error(ResultEnum.SUBJECT_GRAPH_ERROR.getCode(), ResultEnum.SUBJECT_GRAPH_ERROR.getMsg());
        }


    }

    public void fileBackups() {
        //暂时闲置、备用
    }

    public void test() {
        //初始化一个项目-并因此创建一个工作区
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        Workspace workspace = pc.getCurrentWorkspace();

//获取一个图模型-它存在是因为我们有一个工作区
        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel();

////创建三个节点
        Node n0 = graphModel.factory().newNode("n0");
        n0.setLabel("Node 0");
        Node n1 = graphModel.factory().newNode("n1");
        n1.setLabel("Node 1");
        Node n2 = graphModel.factory().newNode("n2");
        n2.setLabel("Node 2");

//创建三个边缘
        Edge e1 = graphModel.factory().newEdge(n1, n2, 1, true);
        Edge e2 = graphModel.factory().newEdge(n0, n2, 2, true);
        Edge e3 = graphModel.factory().newEdge(n2, n0, 2, true);   //This is e2's mutual edge 这是e2的共同优势

//Append as a Directed Graph 附加为有向图
        DirectedGraph directedGraph = graphModel.getDirectedGraph();
        directedGraph.addNode(n0);
        directedGraph.addNode(n1);
        directedGraph.addNode(n2);
        directedGraph.addEdge(e1);
        directedGraph.addEdge(e2);
        directedGraph.addEdge(e3);

//Count nodes and edges 计算节点和边缘
        System.out.println("Nodes: " + directedGraph.getNodeCount() + " Edges: " + directedGraph.getEdgeCount());

//Get a UndirectedGraph now and count edges 立即获取UndirectedGraph并计算边数
        UndirectedGraph undirectedGraph = graphModel.getUndirectedGraph();
        System.out.println("Edges: " + undirectedGraph.getEdgeCount());   //The mutual edge is automatically merged

//Iterate over nodes
        for (Node n : directedGraph.getNodes()) {
            Node[] neighbors = directedGraph.getNeighbors(n).toArray();
            System.out.println(n.getLabel() + " has " + neighbors.length + " neighbors");
        }

//Iterate over edges
        for (Edge e : directedGraph.getEdges()) {
            System.out.println(e.getSource().getId() + " -> " + e.getTarget().getId());
        }

//Find node by id
        Node node2 = directedGraph.getNode("n2");

//Get degree
        System.out.println("Node2 degree: " + directedGraph.getDegree(node2));

//Modify the graph while reading
//Due to locking, you need to use toArray() on Iterable to be able to modify
//the graph in a read loop
        for (Node n : directedGraph.getNodes().toArray()) {
            directedGraph.removeNode(n);
        }
    }

}
