package com.xjtu.spider_new.spiders;

import com.xjtu.facet.domain.Facet;
import com.xjtu.spider_dynamic_output.spiders.csdn.CsdnCrawler;
import com.xjtu.spider_new.spiders.multisource.GeneralCrawler;
import com.xjtu.spider_new.spiders.wikicn.MysqlReadWriteDAO;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.omg.PortableServer.THREAD_POLICY_ID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BasicCrawlerController {
    private static final Logger logger =
            LoggerFactory.getLogger(BasicCrawlerController.class);

    // 爬虫线程池
    private ExecutorService crawlerThreadPool = Executors.newCachedThreadPool();

    private Object l = new Object();

    public void startCrawler(String url, boolean isChinese, Long domainId, Long topicId) throws Exception {
        CrawlConfig config = new CrawlConfig();

        // Set the folder where intermediate crawl data is stored (e.g. list of urls that are extracted from previously
        // fetched pages and need to be crawled later).
        config.setCrawlStorageFolder("tmp/crawler4j/");

        // Be polite: Make sure that we don't send more than 1 request per second (1000 milliseconds between requests).
        // Otherwise it may overload the target servers.
        config.setPolitenessDelay(1000);

        // 设置线程任务执行完后回收资源的延时，这里由于任务不多，所以延时时间可以小一点
        config.setCleanupDelaySeconds(3);
        config.setThreadShutdownDelaySeconds(3);

        // You can set the maximum crawl depth here. The default value is -1 for unlimited depth.
        config.setMaxDepthOfCrawling(0);

        // You can set the maximum number of pages to crawl. The default value is -1 for unlimited number of pages.
        config.setMaxPagesToFetch(1000);

        // Should binary data should also be crawled? example: the contents of pdf, or the metadata of images etc
        config.setIncludeBinaryContentInCrawling(true);

        // Do you need to set a proxy? If so, you can use:
        // config.setProxyHost("proxyserver.example.com");
        // config.setProxyPort(8080);

        // If your proxy also needs authentication:
        // config.setProxyUsername(username); config.getProxyPassword(password);

        // This config parameter can be used to set your crawl to be resumable
        // (meaning that you can resume the crawl from a previously
        // interrupted/crashed crawl). Note: if you enable resuming feature and
        // want to start a fresh crawl, you need to delete the contents of
        // rootFolder manually.
        config.setResumableCrawling(false);

        // Set this to true if you want crawling to stop whenever an unexpected error
        // occurs. You'll probably want this set to true when you first start testing
        // your crawler, and then set to false once you're ready to let the crawler run
        // for a long time.
//        config.setHaltOnError(true);

        // Instantiate the controller for this crawl.
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        // For each crawl, you need to add some seed urls. These are the first
        // URLs that are fetched and then the crawler starts following links
        // which are found in these pages
//        controller.addSeed("https://zh.wikipedia.org/wiki/%E9%A2%B1%E9%A2%A8%E5%A5%A7%E9%A6%AC");
//        controller.addSeed("https://zh.wikipedia.org/wiki/%E5%A4%AA%E5%B9%B3%E6%B4%8B");
//        controller.addSeed("https://zh.wikipedia.org/wiki/%E6%96%90%E8%BF%AA%E5%8D%97%C2%B7%E9%BA%A5%E5%93%B2%E5%80%AB");
        controller.addSeed(url);


        // Number of threads to use during crawling. Increasing this typically makes crawling faster. But crawling
        // speed depends on many other factors as well. You can experiment with this to figure out what number of
        // threads works best for you.
        int numberOfCrawlers = 2;

        // The factory which creates instances of crawlers.
        CrawlController.WebCrawlerFactory<BasicCrawler> factory =
                () -> new BasicCrawler("https://en.wikipedia.org/wiki/", isChinese, domainId, topicId);

        // Start the crawl. This is a blocking operation, meaning that your code
        // will reach the line after this only when crawling is finished.
        controller.start(factory, numberOfCrawlers);
//        controller.waitUntilFinish();
        logger.info("Crawler is finished");

        List<Object> crawlersLocalData = controller.getCrawlersLocalData();
        Set<String> facetNameSet = new HashSet<>();
        long totalLinks = 0;
        long totalTextSize = 0;
        int totalProcessedPages = 0;
        for (Object localData : crawlersLocalData) {
            CrawlerStat stat = (CrawlerStat) localData;
            totalLinks += stat.getTotalLinks();
            totalTextSize += stat.getTotalTextSize();
            totalProcessedPages += stat.getTotalProcessedPages();
            if (stat.getFacetNameList() == null) continue;
            for (String facetName : stat.getFacetNameList()) {
                if (facetNameSet.contains(facetName)) continue;
                facetNameSet.add(facetName);
            }
        }

        logger.info("Aggregated Statistics:");
        logger.info("\tProcessed Pages: {}", totalProcessedPages);
        logger.info("\tTotal Links found: {}", totalLinks);
        logger.info("\tTotal Text Size: {}", totalTextSize);

//        controller.startNonBlocking(factory, numberOfCrawlers);
//
//        Thread.sleep(30 * 1000);
//
//        controller.shutdown();
//        controller.waitUntilFinish();


        // 再去CSDN上获取新的碎片
        String topicName = MysqlReadWriteDAO.findTopicNameByTopicId(topicId);
        config.setMaxDepthOfCrawling(1);
        config.setMaxPagesToFetch(50);

        assert facetNameSet != null;
        for (String facetName : facetNameSet) {

            pageFetcher = new PageFetcher(config);
            robotstxtConfig = new RobotstxtConfig();
            robotstxtConfig.setEnabled(false);
            robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
            controller = new CrawlController(config, pageFetcher, robotstxtServer);
//            controller.addSeed("https://so.csdn.net/so/search?q=" + topicName + facetName + "&t=&u=&utm_medium=distribute.pc_search_hot_word.none-task-hot_word-alirecmd-7.nonecase&dist_request_id=&depth_1-utm_source=distribute.pc_search_hot_word.none-task-hot_word-alirecmd-7.nonecase");
            String targetURL = "https://cn.bing.com/search?q=" + topicName + facetName;
            controller.addSeed(targetURL);

            // 启动CSDNCrawler
            CrawlController.WebCrawlerFactory<GeneralCrawler> csdnFactory =
                    () -> new GeneralCrawler(targetURL, isChinese, domainId, topicId, facetName);

            controller.start(csdnFactory, numberOfCrawlers);
            logger.info("Crawler is finished");

        }

    }

    /**
     * 根据空主题，仅爬取 维基百科 上的分面以及 碎片
     */
    public void startCrawlerForFacetOnly(String url, boolean isChinese, Long domainId, Long topicId) throws Exception {
        CrawlConfig config = new CrawlConfig();

        config.setCrawlStorageFolder("tmp/crawler4j/");
        config.setPolitenessDelay(1000);
        // 设置线程任务执行完后回收资源的延时，这里由于任务不多，所以延时时间可以小一点
        config.setCleanupDelaySeconds(3);
        config.setThreadShutdownDelaySeconds(3);
        config.setMaxDepthOfCrawling(0);
        config.setMaxPagesToFetch(1000);
        config.setIncludeBinaryContentInCrawling(true);
        config.setResumableCrawling(false);


        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        controller.addSeed(url);


        int numberOfCrawlers = 2;

        CrawlController.WebCrawlerFactory<BasicCrawler> factory =
                () -> new BasicCrawler("https://zh.wikipedia.org/wiki/", isChinese, domainId, topicId);

        controller.start(factory, numberOfCrawlers);
        logger.info("Crawler is finished");

    }

    /**
     * 根据给定课程、主题、分面列表，爬取分面列表下每个分面的碎片
     * @param domainId
     * @param topicId
     * @param facets
     */
    public void startCrawlerForAssembleOnly(Long domainId, Long topicId, List<Facet> facets) throws Exception {
        int numberOfCrawlers = 2;
        String crawlStorageFolder = "/tmp/crawler4j/";
        List<CrawlController> crawlControllers = new ArrayList<>();
        List<CrawlController.WebCrawlerFactory<GeneralCrawler>> crawlerFactories = new ArrayList<>();

        for (int i = 0; i < facets.size(); i++) {
            CrawlConfig config = new CrawlConfig();

            config.setCrawlStorageFolder(crawlStorageFolder + "/facet" + facets.get(i).getFacetName());
            config.setPolitenessDelay(1000);
            // 设置线程任务执行完后回收资源的延时，这里由于任务不多，所以延时时间可以小一点
            config.setCleanupDelaySeconds(3);
            config.setThreadShutdownDelaySeconds(3);
            config.setMaxDepthOfCrawling(2);
            config.setMaxPagesToFetch(100);
            config.setIncludeBinaryContentInCrawling(false);
            config.setResumableCrawling(false);

            // Instantiate the controller for this crawl.
            PageFetcher pageFetcher = new PageFetcher(config);
            RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
            robotstxtConfig.setEnabled(false);
            RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
            CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

            String topicName = MysqlReadWriteDAO.findTopicNameByTopicId(topicId);

            String facetName = facets.get(i).getFacetName();
            String targetURLCSDN = "https://cn.bing.com/search?q=" + topicName + facetName + "+csdn";
            String targetURLCNB = "https://cn.bing.com/search?q=" + topicName + facetName + "+博客园";
            String targetURLJS = "https://cn.bing.com/search?q=" + topicName + facetName + "+简书";
            // 由于 www.bing.com 被墙，临时换成 www2.bing.com
//            String targetURLCSDN = "https://www2.bing.com/search?q=" + topicName + facetName + " csdn";
//            String targetURLCNB = "https://www2.bing.com/search?q=" + topicName + facetName + " 博客园";
//            String targetURLJS = "https://www2.bing.com/search?q=" + topicName + facetName + " 简书";
            // 还是不行，试试 google 的镜像站 gogoo.ml
            // 问题：我们的系统检测到您的计算机网络中存在异常流量。此网页用于确认这些请求是由您而不是自动程序发出的
//            String targetURLCSDN = "https://gogoo.ml/search?q=" + topicName + facetName + "+csdn";
//            String targetURLCNB = "https://gogoo.ml/search?q=" + topicName + facetName + "+博客园";
//            String targetURLJS = "https://gogoo.ml/search?q=" + topicName + facetName + "+简书";
            controller.addSeed(targetURLCSDN);
            controller.addSeed(targetURLCNB);
            controller.addSeed(targetURLJS);
            CrawlController.WebCrawlerFactory<GeneralCrawler> csdnFactory =
                    () -> new GeneralCrawler("https://cn.bing.com/search?q=", true, domainId, topicId, facetName);

            crawlControllers.add(controller);
            crawlerFactories.add(csdnFactory);

        }

        for (int i = 0; i < facets.size(); i++) {
            crawlControllers.get(i).startNonBlocking(crawlerFactories.get(i), numberOfCrawlers);
        }

        for (int i = 0; i < facets.size(); i++) {
            crawlControllers.get(i).waitUntilFinish();
            logger.info("Crawler for " + facets.get(i).getFacetName() +  " is finished.");
        }

    }

    /**
     * 增量式爬取分面列表下每个分面的碎片
     * “增量式”设计：
     * 根据 bing 搜索上的按最近一周内排序获取新的搜索结果并在此基础上爬碎片
     * @param domainId 课程名，存数据库时需要使用
     * @param topicId 主题名，存数据库时需要使用
     * @param facets 分面列表，爬取需要
     */
    public void startIncrementCrawlerForAssembleOnly(Long domainId, Long topicId, List<Facet> facets) throws Exception {
        int numberOfCrawlers = 2;
        String crawlStorageFolder = "/tmp/crawler4j/";
        List<CrawlController> crawlControllers = new ArrayList<>();
        List<CrawlController.WebCrawlerFactory<GeneralCrawler>> crawlerFactories = new ArrayList<>();

        for (int i = 0; i < facets.size(); i++) {
            CrawlConfig config = new CrawlConfig();

            config.setCrawlStorageFolder(crawlStorageFolder + "/facet" + facets.get(i).getFacetName());
            config.setPolitenessDelay(1000);
            // 设置线程任务执行完后回收资源的延时，这里由于任务不多，所以延时时间可以小一点
            config.setCleanupDelaySeconds(3);
            config.setThreadShutdownDelaySeconds(3);
            config.setMaxDepthOfCrawling(0);
            config.setMaxPagesToFetch(50);
            config.setIncludeBinaryContentInCrawling(false);
            config.setResumableCrawling(false);

            // Instantiate the controller for this crawl.
            PageFetcher pageFetcher = new PageFetcher(config);
            RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
            robotstxtConfig.setEnabled(false);
            RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
            CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

            String topicName = MysqlReadWriteDAO.findTopicNameByTopicId(topicId);
            config.setMaxDepthOfCrawling(1);
            config.setMaxPagesToFetch(50);

            String facetName = facets.get(i).getFacetName();
            // URL后拼接 &filters=ex1%3a"ez2" ，是在 bing 上筛选一周内的结果
            String targetURLCSDN1 = "https://cn.bing.com/search?q=" + topicName + " " + facetName + " CSDN" + "&filters=ex1%3a\"ez2\"";
            String targetURLCSDN2 = "https://cn.bing.com/search?q=" + topicName + " " + facetName + " CSDN" + "&filters=ex1%3a\"ez2\"&first=11";
            String targetURLCNB1 = "https://cn.bing.com/search?q=" + topicName + " " + facetName + " 博客园" + "&filters=ex1%3a\"ez2\"";
            String targetURLCNB2 = "https://cn.bing.com/search?q=" + topicName + " " + facetName + " 博客园" + "&filters=ex1%3a\"ez2\"&first=11";
            String targetURLJS1 = "https://cn.bing.com/search?q=" + topicName + " " + facetName + " 简书" + "&filters=ex1%3a\"ez2\"";
            String targetURLJS2 = "https://cn.bing.com/search?q=" + topicName + " " + facetName + " 简书" + "&filters=ex1%3a\"ez2\"&first=11";
            controller.addSeed(targetURLCSDN1);
            controller.addSeed(targetURLCSDN2);
            controller.addSeed(targetURLCNB1);
            controller.addSeed(targetURLCNB2);
            controller.addSeed(targetURLJS1);
            controller.addSeed(targetURLJS2);
            CrawlController.WebCrawlerFactory<GeneralCrawler> csdnFactory =
                    () -> new GeneralCrawler("https://cn.bing.com/search?q=", true, domainId, topicId, facetName);

            crawlControllers.add(controller);
            crawlerFactories.add(csdnFactory);

        }

        for (int i = 0; i < facets.size(); i++) {
            crawlControllers.get(i).startNonBlocking(crawlerFactories.get(i), numberOfCrawlers);
        }

        for (int i = 0; i < facets.size(); i++) {
            crawlControllers.get(i).waitUntilFinish();
            logger.info("Crawler for " + facets.get(i).getFacetName() +  " is finished.");
        }
    }

    /**
     * 使用 CSDN搜索（https://so.csdn.net/so/）来爬取碎片
     * 由于 CSDN搜索 只支持浏览器访问，所以针对每个分面会先使用模拟浏览器的爬虫爬取得到一个链接列表，
     * 再根据链接列表使用 crawler4j 来进行具体碎片的爬取
     *
     * 由于对速度的需求，使每个分面爬取过程并发执行，即要开启多个线程
     */
    public void startCrawlerForAssembleWithCSDNSearch(Long domainId, Long topicId, List<Facet> facets) {

//        List<CrawlController> controllers = new ArrayList<>();

        CrawlController[] controllers = new CrawlController[facets.size()];

        for (int i = 0; i < facets.size(); i++) {
            Facet facet = facets.get(i);

            int finalI = i;
            crawlerThreadPool.execute(() -> {
                System.out.println("start");
                // 模拟浏览器访问 CSDN 搜索获取该分面下所有链接，存在列表里
                List<String> urls = CsdnCrawler.getCsdnUrl(
                        MysqlReadWriteDAO.findTopicNameByTopicId(topicId),
                        facet.getFacetName(),
                        false
                );
                System.out.println("get url");


                for (String url : urls) {
                    System.out.println(facet.getFacetName() + " 下拿到的链接：" + url);
                }

                String facetName = facet.getFacetName();

                // 使用 crawler4j 进行爬取
                String crawlStorageFolder = "/tmp/crawler4j/" + facet.getFacetName() + "/";
                CrawlController csdnCrawlerController = createBasicController(crawlStorageFolder, -1, urls);

                controllers[finalI] = csdnCrawlerController;
            });

        }


        //todo: wait notify

        for (int i = 0; i < controllers.length; i++) {
            int finalI = i;
            CrawlController.WebCrawlerFactory<GeneralCrawler> csdnFactory =
                    () -> new GeneralCrawler("https://cn.bing.com/search?q=", true, domainId, topicId, facets.get(finalI).getFacetName());
            controllers[i].startNonBlocking(csdnFactory, 1);
        }

        String topicName = MysqlReadWriteDAO.findTopicNameByTopicId(topicId);

        for (int i = 0; i < controllers.length; i++) {
            controllers[i].waitUntilFinish();
            logger.info("Crawler for " + topicName + " - " + facets.get(i).getFacetName() +  " is finished.");
        }

    }

    /**
     * ** 串行 非并行，不开线程 **
     * 使用 CSDN搜索（https://so.csdn.net/so/）来爬取碎片
     * 由于 CSDN搜索 只支持浏览器访问，所以针对每个分面会先使用模拟浏览器的爬虫爬取得到一个链接列表，
     * 再根据链接列表使用 crawler4j 来进行具体碎片的爬取
     *
     * 由于对速度的需求，使每个分面爬取过程并发执行，即要开启多个线程
     */
    public void startCrawlerForAssembleWithCSDNSearchNoThread(Long domainId, Long topicId, List<Facet> facets) {

//        List<CrawlController> controllers = new ArrayList<>();

        CrawlController[] controllers = new CrawlController[facets.size()];

        for (int i = 0; i < facets.size(); i++) {
            Facet facet = facets.get(i);

            System.out.println("start");
            // 模拟浏览器访问 CSDN 搜索获取该分面下所有链接，存在列表里
            List<String> urls = CsdnCrawler.getCsdnUrl(
                    MysqlReadWriteDAO.findTopicNameByTopicId(topicId),
                    facet.getFacetName(),
                    false
            );
            System.out.println("get url");


            for (String url : urls) {
                System.out.println(facet.getFacetName() + " 下拿到的链接：" + url);
            }

            String facetName = facet.getFacetName();

            // 使用 crawler4j 进行爬取
            String crawlStorageFolder = "/tmp/crawler4j/" + facet.getFacetName() + "/";
            CrawlController csdnCrawlerController = createBasicController(crawlStorageFolder, -1, urls);

            controllers[i] = csdnCrawlerController;
            int finalI = i;
            CrawlController.WebCrawlerFactory<GeneralCrawler> csdnFactory =
                    () -> new GeneralCrawler("https://cn.bing.com/search?q=", true, domainId, topicId, facets.get(finalI).getFacetName());
            controllers[i].startNonBlocking(csdnFactory, 1);
        }

        String topicName = MysqlReadWriteDAO.findTopicNameByTopicId(topicId);

        for (int i = 0; i < controllers.length; i++) {
            controllers[i].waitUntilFinish();
            logger.info("Crawler for " + topicName + " - " + facets.get(i).getFacetName() +  " is finished.");
        }

    }

    /**
     * 创建一个最基础的 controller ，其中的参数可以自己定义
     * 该方法是为了可读性，避免重复代码
     * @param folderName 爬虫缓存文件目录
     * @param maxDepthOfCrawling 最大爬取深度
     * @param urls seed 的链接列表（一个也是可以的）
     * @return CrawlController
     */
    public CrawlController createBasicController(String folderName, int maxDepthOfCrawling, List<String> urls) {
        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(folderName);
        config.setPolitenessDelay(1000);
        // 设置线程任务执行完后回收资源的延时，这里由于任务不多，所以延时时间可以小一点
        config.setCleanupDelaySeconds(3);
        config.setThreadShutdownDelaySeconds(3);
        config.setMaxDepthOfCrawling(maxDepthOfCrawling);
        config.setMaxPagesToFetch(10);
        config.setIncludeBinaryContentInCrawling(false);
        config.setResumableCrawling(false);

        // Instantiate the controller for this crawl.
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        robotstxtConfig.setEnabled(false);
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = null;
        try {
            controller = new CrawlController(config, pageFetcher, robotstxtServer);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 添加 seed
        for (String url : urls) {
            controller.addSeed(url);
        }

        return controller;
    }

}
