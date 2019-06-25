package com.xjtu.spider_topic.spiders.wikicn;

import app.Config;
import assemble.bean.AssembleFragmentFuzhu;
import dependency.bean.Dependency;
import dependency.ranktext.RankText;
import dependency.ranktext.Term;
import facet.bean.FacetRelation;
import facet.bean.FacetSimple;
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
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openide.util.Lookup;
import utils.JsoupDao;
import utils.Log;
import utils.SpiderUtils;
import utils.mysqlUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 实现中文维基百科知识森林数据集的构建
 * 将文本和图片存储到一个表格中
 * @author 郑元浩
 *
 */
public class FragmentCrawlerDAO {

	public static void main(String[] args) throws Exception {
		// 设置解析参数
		String topicName = "数据库"; // 链表  跳跃列表  数据结构与算法列表
		String topicUrl = "https://zh.wikipedia.org/wiki/" + URLEncoder.encode(topicName);
		String topicHtml = SpiderUtils.seleniumWikiCN(topicUrl);
		Document doc = JsoupDao.parseHtmlText(topicHtml);

		// 测试解析小程序
//		List<FacetRelation> facetRelationList = getFacetRelation(doc);
//		Log.logFacetRelation(facetRelationList);

		// 解析所有内容
		getFragment(doc); // summary内容 + 一级/二级/三级标题内容
	}

	/**
	 * 获取各级分面父子对应关系
	 * @param doc
	 * @return 
	 */
	public static List<FacetRelation> getFacetRelation(Document doc){
		LinkedList<String> indexs = new LinkedList<String>();// 标题前面的下标
		LinkedList<String> facets = new LinkedList<String>();// 各级标题的名字
		List<FacetRelation> facetRelationList = new ArrayList<FacetRelation>();
		
		try {
			
			/**
			 * 获取标题
			 */
			Elements titles = doc.select("div#toc").select("li");
			Log.log(titles.size());
			if(titles.size()!=0){
				for(int i = 0; i < titles.size(); i++){
					String index = titles.get(i).child(0).child(0).text();
					String text = titles.get(i).child(0).child(1).text();
					text = Config.converter.convert(text);
					Log.log(index + " " + text);
					indexs.add(index);
					facets.add(text);
				}

				/**
				 * 将二级/三级标题全部匹配到对应的父标题
				 */
				Log.log("--------------------------------------------");
				for(int i = 0; i < indexs.size(); i++){
					String index = indexs.get(i);
					if(index.lastIndexOf(".") == 1){ // 二级分面
//						Log.log("二级标题");
						String facetSecond = facets.get(i);
						for(int j = i - 1; j >= 0; j--){
							String index2 = indexs.get(j);
							if(index2.lastIndexOf(".") == -1){
								String facetOne = facets.get(j);
								FacetRelation facetRelation = new FacetRelation(facetSecond, 2, facetOne, 1);
								facetRelationList.add(facetRelation);
								break;
							}
						}
					} 
					else if (index.lastIndexOf(".") == 3) { // 三级分面
//						Log.log("三级标题");
						String facetThird = facets.get(i);
						for(int j = i - 1; j >= 0; j--){
							String index2 = indexs.get(j);
							if(index2.lastIndexOf(".") == 1){
								String facetSecond = facets.get(j);
								FacetRelation facetRelation = new FacetRelation(facetThird, 3, facetSecond, 2);
								facetRelationList.add(facetRelation);
								break;
							}
						}
					}
				}

			} else {
				Log.log("该主题没有目录，不是目录结构，直接爬取 -->摘要<-- 信息");
			}
		} catch (Exception e) {
			Log.log("this is not a normal page...");
		}
		return facetRelationList;
	}

	/**
	 * 得到一个主题的所有分面及其分面级数
	 * 1. 数据结构为: FacetSimple
	 * @param doc
	 * @return 
	 */
	public static List<FacetSimple> getFacet(Document doc){
		List<FacetSimple> facetList = new ArrayList<FacetSimple>();
		List<String> firstTitle = FragmentExtract.getFirstTitle(doc);
		List<String> secondTitle = FragmentExtract.getSecondTitle(doc);
		List<String> thirdTitle = FragmentExtract.getThirdTitle(doc);

		// 判断条件和内容函数保持一致
		// facet中的分面与spider和assemble表格保持一致
		Elements mainContents = doc.select("div#mw-content-text").select("span.mw-headline");
		if(mainContents.size() == 0){ // 存在没有分面的情况
			String facetName = "摘要";
			int facetLayer = 1;
			FacetSimple facetSimple = new FacetSimple(facetName, facetLayer);
			facetList.add(facetSimple);
		} else {
			String facetNameZhai = "摘要";
			int facetLayerZhai = 1;
			FacetSimple facetSimpleZhai = new FacetSimple(facetNameZhai, facetLayerZhai);
			facetList.add(facetSimpleZhai);
			// 保存一级分面名及其分面级数
			for(int i = 0; i < firstTitle.size(); i++){
				String facetName = firstTitle.get(i);
				int facetLayer = 1;
				FacetSimple facetSimple = new FacetSimple(facetName, facetLayer);
				facetList.add(facetSimple);
			}
			// 保存二级分面名及其分面级数
			for(int i = 0; i < secondTitle.size(); i++){
				String facetName = secondTitle.get(i);
				int facetLayer = 2;
				FacetSimple facetSimple = new FacetSimple(facetName, facetLayer);
				facetList.add(facetSimple);
			}
			// 保存三级分面名及其分面级数
			for(int i = 0; i < thirdTitle.size(); i++){
				String facetName = thirdTitle.get(i);
				int facetLayer = 3;
				FacetSimple facetSimple = new FacetSimple(facetName, facetLayer);
				facetList.add(facetSimple);
			}
		}

		return facetList;

	}

	/**
	 * 将从"摘要"到各级标题的所有分面内容全部存到一起
	 * @param doc 解析网页文档
	 * @return
	 */
	public static List<AssembleFragmentFuzhu> getFragment(Document doc){
		List<AssembleFragmentFuzhu> assembleList = new ArrayList<AssembleFragmentFuzhu>();

		Elements mainContents = doc.select("div#mw-content-text").select("span.mw-headline");
		if(mainContents.size() == 0){
			// 网页全部内容
			List<AssembleFragmentFuzhu> specialContent = FragmentExtract.getSpecialContent(doc); // 没有目录栏的词条信息
			assembleList.addAll(specialContent);
		} else {
			// 摘要信息
			List<AssembleFragmentFuzhu> summaryContent = FragmentExtract.getSummary(doc); // 摘要内容
			assembleList.addAll(summaryContent);
			// flagFirst 为 true，保留一级分面数据
			LinkedList<String> firstTitle = FragmentExtract.getFirstTitle(doc);
			if(firstTitle.size() != 0){
				List<AssembleFragmentFuzhu> firstContent = FragmentExtract.getFirstContent(doc); // 一级分面内容
				if (firstContent != null) assembleList.addAll(firstContent);
			}
			// flagSecond 为 true，保留二级分面数据
			LinkedList<String> secondTitle = FragmentExtract.getSecondTitle(doc);
			if(secondTitle.size() != 0){
				List<AssembleFragmentFuzhu> secondContent = FragmentExtract.getSecondContent(doc); // 二级分面内容
				if (secondContent != null) assembleList.addAll(secondContent);
			}
			// flagThird 为 true，保留三级分面数据
			LinkedList<String> thirdTitle = FragmentExtract.getThirdTitle(doc);
			if(thirdTitle.size() != 0){
				List<AssembleFragmentFuzhu> thirdContent = FragmentExtract.getThirdContent(doc); // 三级分面内容
				if (thirdContent != null) assembleList.addAll(thirdContent);
			}
		}
		return assembleList;
	}

	/**
	 * 保存所有信息，如果某个分面含有子分面，那么这个分面下面应该没有碎片
	 * 1. 判断该文本碎片对应的分面是否包含子分面
	 * 2. 判断该文本碎片为那个不需要的文本
	 * 3. 去除长度很短且无意义的文本碎片
	 * @param domain 领域名
	 * @param topic 主题名
	 * @param doc 解析网页文档
	 * @return
	 */
	public static List<AssembleFragmentFuzhu> getFragmentUseful(String domain, String topic, Document doc){
		List<AssembleFragmentFuzhu> assembleResultList = new ArrayList<AssembleFragmentFuzhu>();
		List<AssembleFragmentFuzhu> assembleList = getFragment(doc);
		for(int i = 0; i < assembleList.size(); i++){
			AssembleFragmentFuzhu assemble = assembleList.get(i);
			Boolean exist = MysqlReadWriteDAO.judgeFacetRelation(assemble, domain, topic); // 判断该文本碎片对应的分面是否包含子分面
			Boolean badText = judgeBadText(assemble); // 判断该文本碎片为那个不需要的文本
			Boolean badLength = FragmentExtract.getContentLen(assemble.getFacetContentPureText()) > Config.TEXTLENGTH; // 去除长度很短且无意义的文本碎片
			if (!exist && !badText && badLength) {
				assembleResultList.add(assemble);
			}
		}
		return assembleResultList;
	}
	
	/**
	 * 根据领域名生成认知关系
	 * @param ClassName 领域名
	 * @return 是否产生成功
	 */
	public static boolean getDependenceByClassName(String ClassName, boolean isEnglish) {

		List<Term> termList = new ArrayList<Term>();
		/**
		 * 根据指定领域，查询主题表，获得领域下所有主题
		 */
		mysqlUtils mysql = new mysqlUtils();
		String sql = "select * from " + Config.DOMAIN_TOPIC_TABLE +" where ClassName=?";
		List<Object> params = new ArrayList<Object>();
		params.add(ClassName);
		try {
			List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
			for (int i = 0; i < results.size(); i++) {
				Term term = new Term();
				term.setTermID(Integer.parseInt(results.get(i).get("TermID").toString()));
				term.setTermName(results.get(i).get("TermName").toString());
				termList.add(term);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mysql.closeconnection();
		}
		/**
		 * 根据指定领域及主题，查询碎片表，获得主题的内容信息
		 */
		mysqlUtils mysqlAssemble = new mysqlUtils();
		String sqlAssemble = "select * from " + Config.ASSEMBLE_FRAGMENT_TABLE +" where TermID=? and TermName=? and ClassName=?";
		try {
			for (int i = 0; i < termList.size(); i++) {
				Term term = termList.get(i);
				List<Object> paramsAssemble = new ArrayList<Object>();
				paramsAssemble.add(term.getTermID());
				paramsAssemble.add(term.getTermName());
				paramsAssemble.add(ClassName);
				List<Map<String, Object>> results = mysqlAssemble.returnMultipleResult(sqlAssemble, paramsAssemble);
				StringBuffer termText = new StringBuffer();
				for (int j = 0; j < results.size(); j++) {
					termText.append(results.get(j).get("Text").toString());
				}
				term.setTermText(termText.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mysqlAssemble.closeconnection();
		}

		/**
		 * 根据主题内容，调用算法得到主题认知关系
		 */
		RankText rankText = new RankText();
//		List<Dependency> dependencies = rankText.rankText(termList, ClassName, Config.DEPENDENCEMAX); // 设置认知关系的数量为固定值
		List<Dependency> dependencies = rankText.rankText(termList, ClassName, termList.size(), isEnglish); // 设置认知关系的数量为主题的数量
		/**
		 * 指定领域，存储主题间的认知关系
		 */
		boolean success = false;
		mysqlUtils mysqlDependency = new mysqlUtils();
		String sqlDependency = "insert into " + Config.DEPENDENCY + "(ClassName,Start,StartID,End,EndID,Confidence) values(?,?,?,?,?,?);";
		try {
			for (int i = 0; i < dependencies.size(); i++) {
				Dependency dependency = dependencies.get(i);
				List<Object> paramsDependency = new ArrayList<Object>();
				paramsDependency.add(ClassName);
				paramsDependency.add(dependency.getStart());
				paramsDependency.add(dependency.getStartID());
				paramsDependency.add(dependency.getEnd());
				paramsDependency.add(dependency.getEndID());
				paramsDependency.add(dependency.getConfidence());
				success = mysqlDependency.addDeleteModify(sqlDependency, paramsDependency);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mysqlDependency.closeconnection();
		}
		return success;
	}

	/**
	 * 根据课程名，读取dependency表格，生成对应认知关系的gephi文件供认知关系页面调用
	 * @param ClassName 课程名
	 */
	public static void getGexfByClassName(String ClassName) {
		Log.log("正在处理课程：" + ClassName);
		new File(Config.GEXFPATH).mkdir();
		File gexfFile = new File(Config.GEXFPATH + "\\" + ClassName + ".gexf");
		if (gexfFile.exists()) {
			Log.log("gephi文件已经生成");
		} else {
			// 第一次调用api，运行gephi java接口生成认知关系图数据。将其存储到本地，方便第二次以后调用直接读取文件内容。同时返回关系图数据。
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

			//Import database
			EdgeListDatabaseImpl db = new EdgeListDatabaseImpl();
			db.setDBName(Config.DBNAME);
			db.setHost(Config.HOST);
			db.setUsername(Config.USERNAME);
			db.setPasswd(Config.PASSWD);
			db.setSQLDriver(new MySQLDriver());
			db.setPort(Config.PORT);
			db.setNodeQuery("SELECT DISTINCT " +
					"dt.TermID AS id, " +
					"dt.TermName AS label " +
					"FROM " +
					"dependency AS dp , " +
					"domain_topic AS dt " +
					"WHERE " +
					"dt.ClassName = '" + ClassName + "' AND " +
					"(dt.TermName = dp.`Start` OR " +
					"dt.TermName = dp.`End`)");
			db.setEdgeQuery("SELECT dependency.StartID AS source, dependency.EndID AS target "
					+ "FROM dependency where ClassName='" + ClassName + "'");
			ImporterEdgeList edgeListImporter = new ImporterEdgeList();
			Container container = importController.importDatabase(db, edgeListImporter);
			container.getLoader().setAllowAutoNode(false);  //Don't create missing nodes
			container.getLoader().setEdgeDefault(EdgeDirectionDefault.DIRECTED);   //Force UNDIRECTED

			//Append imported data to GraphAPI
			importController.process(container, new DefaultProcessor(), workspace);

			//See if graph is well imported
			DirectedGraph graph = graphModel.getDirectedGraph();
			if (graph.getNodeCount() == 0 || graph.getEdgeCount() == 0) {
				Log.log("节点数量为：" + graph.getNodeCount() + "，边数量为：" + graph.getEdgeCount());
				return;
			}
			Log.log("Nodes: " + graph.getNodeCount());
			Log.log("Edges: " + graph.getEdgeCount());

			//Filter：对节点进行过滤操作
			DegreeRangeBuilder.DegreeRangeFilter degreeFilter = new DegreeRangeBuilder.DegreeRangeFilter();
			degreeFilter.init(graph);
			degreeFilter.setRange(new Range(Config.FILTERDEGREE, Integer.MAX_VALUE));     //Remove nodes with degree < 30
			Query query = filterController.createQuery(degreeFilter);
			GraphView view = filterController.filter(query);
			graphModel.setVisibleView(view);    //Set the filter result as the visible view

			//See visible graph stats
			UndirectedGraph graphVisible = graphModel.getUndirectedGraphVisible();
			Log.log("Nodes: " + graphVisible.getNodeCount());
			Log.log("Edges: " + graphVisible.getEdgeCount());

//	        //Run YifanHuLayout for 100 passes - The layout always takes the current visible view ： 运行布局算法
//	        YifanHuLayout layout = new YifanHuLayout(null, new StepDisplacement(1f));
//	        layout.setGraphModel(graphModel);
//	        layout.resetPropertiesValues();
//	        layout.setOptimalDistance(200f);
			//
//	        layout.initAlgo();
//	        for (int i = 0; i < 100 && layout.canAlgo(); i++) {
//	            layout.goAlgo();
//	        }
//	        layout.endAlgo();

			//Layout for 1 minute
			AutoLayout autoLayout = new AutoLayout(1, TimeUnit.MINUTES);
			autoLayout.setGraphModel(graphModel);
			YifanHuLayout firstLayout = new YifanHuLayout(null, new StepDisplacement(1f));
			ForceAtlasLayout secondLayout = new ForceAtlasLayout(null);
			AutoLayout.DynamicProperty adjustBySizeProperty = AutoLayout.createDynamicProperty("forceAtlas.adjustSizes.name", Boolean.TRUE, 0.1f);//True after 10% of layout time
			AutoLayout.DynamicProperty repulsionProperty = AutoLayout.createDynamicProperty("forceAtlas.repulsionStrength.name", 1000., 0f);//500 for the complete period
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
//	                System.out.println(n.getLabel() + " has " + neighbors.length + " neighbors");
				} else {
					nodeList.add(n);
//	                System.out.println(n.getLabel() + " has " + neighbors.length + " neighbors, need to delete");
				}
			}
			directedGraph.removeAllNodes(nodeList);
//	        System.out.println("------删除没有邻居节点的节点后的节点数量和边的数量------");
//	        System.out.println("Nodes: " + graphVisible.getNodeCount());
//	        System.out.println("Edges: " + graphVisible.getEdgeCount());
			//Iterate over edges
//	        for (Edge e : directedGraph.getEdges()) {
//	        	Log.log(e.getSource().getId() + " -> " + e.getTarget().getId());
//	        }

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

			// eclipse中使用jar时需要加上
//	        try {
//				Class.forName("org.netbeans.JarClassLoader$JarURLStreamHandler");
//			} catch (ClassNotFoundException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}

			//Partition with 'modularity_class', just created by Modularity algorithm
			Column modColumn = graphModel.getNodeTable().getColumn(Modularity.MODULARITY_CLASS);
			Function func2 = appearanceModel.getNodeFunction(graph, modColumn, PartitionElementColorTransformer.class);
			Partition partition2 = ((PartitionFunction) func2).getPartition();
			Log.log(partition2.size() + " partitions found");
			Palette palette2 = PaletteManager.getInstance().randomPalette(partition2.size());
			partition2.setColors(palette2.getColors());
			appearanceController.transform(func2);

			//Export
			ExportController ec = Lookup.getDefault().lookup(ExportController.class);
			// 导出成文件
			try {
				ec.exportFile(new File(Config.GEXFPATH + "\\" + ClassName + ".gexf"));
				ec.exportFile(new File(Config.GEXFPATH + "\\" + ClassName + ".pdf"));
			} catch (IOException ex) {
				Log.log("生成关系图数据到文件失败~" + ex);
			}
			// 导出成字符串
			Exporter exporter = ec.getExporter("gexf");
			CharacterExporter characterExporter = (CharacterExporter)exporter;
			StringWriter stringWriter = new StringWriter();
			ec.exportWriter(stringWriter, characterExporter);
			String result = stringWriter.toString();
			if (result == null || result.equals("")) {
				Log.log("认知关系gephi文件生成失败");
			} else {
				Log.log("认知关系gephi文件生成成功" + result);
			}
		}
	}

	/**
	 * 判断分面内容是否包含最后一个多余的链接
	 * @return
	 */
	public static Boolean judgeBadText(AssembleFragmentFuzhu assemble){
		Boolean exist = false;
		String facetContent = assemble.getFacetContentPureText();
		String badTxt1 = "本条目";
		String badTxt2 = "主条目";
		String badTxt3 = "目标页面不存在";
		String badTxt4 = "此章节未";
		String badTxt5 = "外部链接";
		String badTxt6 = "[隐藏]";
		String badTxt7 = "参考文献";
		String badTxt8 = "延伸阅读";
		String badTxt9 = "参见";
		String badTxt10 = "[显示]";
		String badTxt11 = "[编辑]";
		if (facetContent.contains(badTxt1) || facetContent.contains(badTxt2)
			|| facetContent.contains(badTxt3) || facetContent.contains(badTxt4)
			|| facetContent.contains(badTxt5) || facetContent.contains(badTxt6)
			|| facetContent.contains(badTxt7) || facetContent.contains(badTxt8)
			|| facetContent.contains(badTxt9) || facetContent.contains(badTxt10)
			|| facetContent.contains(badTxt11)) {
			exist = true;
		}
		return exist;
	}

}

